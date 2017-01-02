package com.teamwizardry.refraction.client.gui;

import com.google.gson.JsonObject;
import com.teamwizardry.librarianlib.client.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.client.gui.GuiComponent;
import com.teamwizardry.librarianlib.client.gui.components.ComponentSlot;
import com.teamwizardry.librarianlib.client.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.client.gui.components.ComponentText;
import com.teamwizardry.librarianlib.client.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.client.gui.mixin.ButtonMixin;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import com.teamwizardry.librarianlib.client.sprite.Texture;
import com.teamwizardry.librarianlib.common.util.math.Vec2d;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.client.jei.JEIRefractionPlugin;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by LordSaad.
 */
public class ExtraSidebar {

    private static final ResourceLocation extraSlider = new ResourceLocation(Constants.MOD_ID, "textures/gui/extra_slider.png");
    public static final Sprite extraSliderSprite = new Texture(extraSlider).getSprite("slider", 130, 18);
    private static final ResourceLocation extraSliderImage = new ResourceLocation(Constants.MOD_ID, "textures/gui/extra_slider_image.png");
    public static final Sprite extraSliderImageSprite = new Texture(extraSliderImage).getSprite("slider", 135, 100);
    @NotNull
    private final SidebarType sidebarType;
    public int id = 0;
    @Nullable
    public ComponentVoid contentComp, iconComp;
    @Nullable
    public ComponentSlot slotcomp;
    @Nullable
    public Sprite image;
    public String title;
    public SubPage subPage;
    public boolean isSelected = false;
    private ComponentSprite component;

    public ExtraSidebar(SubPage subPage, int id, @Nullable JsonObject object, @NotNull SidebarType sidebarType) {
        this.subPage = subPage;
        this.id = id;
        this.sidebarType = sidebarType;

        if (object != null) {
            if (sidebarType == SidebarType.IMAGE)
                if (object.has("image") && object.get("image").isJsonPrimitive()) {
                    ResourceLocation icon = new ResourceLocation(object.get("image").getAsString());
                    ResourceLocation iconQualified = new ResourceLocation(icon.getResourceDomain(), icon.getResourcePath());
                    image = new Sprite(iconQualified);
                }

            if (object.has("title") && object.get("title").isJsonPrimitive()) {
                title = object.get("title").getAsString();
            }
        }
    }

    public ExtraSidebar init() {
        int x = SubPage.sliderExtendedSprite.getWidth() + GuiBook.BACKGROUND_SPRITE.getWidth() - 14;
        ComponentSprite background = new ComponentSprite(extraSliderSprite, x, 0, extraSliderSprite.getWidth(), extraSliderSprite.getHeight());
        background.addTag(id);

        if (sidebarType == SidebarType.IMAGE && image != null) {
            ComponentSprite imageComp = new ComponentSprite(this.image, extraSliderSprite.getWidth() - this.image.getWidth(), 1, 16, 16);
            new ButtonMixin<>(imageComp, () -> {
            });
            imageComp.BUS.hook(GuiComponent.ComponentTickEvent.class, (event) -> {
                if (isSelected) {
                    imageComp.setSize(new Vec2d(64, 64));
                    imageComp.setPos(new Vec2d(32, 20));
                } else {
                    imageComp.setSize(new Vec2d(16, 16));
                    imageComp.setPos(new Vec2d(extraSliderSprite.getWidth() - imageComp.getSize().getXi() - 8, 1));
                }
            });
            background.add(imageComp);

        } else if (sidebarType == SidebarType.RECIPE && slotcomp != null) background.add(slotcomp);

        ComponentText titleComp = new ComponentText(5, 9, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
        titleComp.getText().setValue(title);
        background.add(titleComp);

        new ButtonMixin<>(background, () -> {
        });

        background.BUS.hook(GuiComponent.ComponentTickEvent.class, (event) -> {
            if (subPage.isSelected) {
                background.setVisible(true);
                background.setEnabled(true);
            } else {
                background.setVisible(false);
                background.setEnabled(false);
            }

            if (isSelected) {
                background.setSprite(extraSliderImageSprite);
                background.setPos(new Vec2d(x, (20 * id) - (subPage.id * 20) - (subPage.page.id * 20) - 20));
                background.setSize(new Vec2d(extraSliderImageSprite.getWidth(), extraSliderImageSprite.getHeight()));
            } else {
                background.setSprite(extraSliderSprite);
                if (subPage.selectedExtraID < id && subPage.extraSidebars.get(subPage.selectedExtraID).isSelected)
                    background.setPos(new Vec2d(x, (20 * id) - (subPage.id * 20) - (subPage.page.id * 20) + (extraSliderImageSprite.getHeight()) - 38));
                else background.setPos(new Vec2d(x, (20 * id) - (subPage.id * 20) - (subPage.page.id * 20) - 20));
                background.setSize(new Vec2d(extraSliderSprite.getWidth(), extraSliderSprite.getHeight()));
            }
        });

        if (sidebarType == SidebarType.RECIPE && Loader.isModLoaded("JEI")) {
            ItemStack stack = slotcomp.getStack().getValue(slotcomp);
            IRecipeLayoutDrawable drawable = JEIRefractionPlugin.getDrawableFromItem(stack);

            background.BUS.hook(GuiComponent.PostDrawEvent.class, (event) -> {
                if (subPage.isSelected && isSelected) {
                    GlStateManager.pushMatrix();
                    RenderHelper.enableGUIStandardItemLighting();
                    GlStateManager.translate(background.getPos().getXi() + 8, background.getPos().getYi() + 30, 0);

                    Vec2d pos = event.getMousePos();
                    pos = new Vec2d(pos.getXi() - 8, pos.getYi() - 30);

                    // check if recipe is refraction's
                    boolean isAssembly = false;
                    IRecipeRegistry registry = JEIRefractionPlugin.jeiRuntime.getRecipeRegistry();
                    IFocus<ItemStack> focus = registry.createFocus(IFocus.Mode.OUTPUT, stack);
                    for (IRecipeCategory<?> category : registry.getRecipeCategories(focus))
                        if (category.getUid().equals(Constants.MOD_ID + ".assembly_table"))
                            isAssembly = true;

                    if (isAssembly) {
                        GlStateManager.translate(10, -10, 0);
                        GlStateManager.scale(0.5, 0.5, 0.5);
                        pos = new Vec2d(pos.getXi() - 10, pos.getYi() + 10).mul(1 / 0.5);
                    }

                    drawable.draw(Minecraft.getMinecraft(), pos.getXi(), pos.getYi());

                    if (isAssembly) {
                        GlStateManager.translate(-10, 10, 0);
                        GlStateManager.scale(1 / 0.5, 1 / 0.5, 1 / 0.5);
                        GlStateManager.translate(-background.getPos().getXi() - 8, -background.getPos().getYi() - 30, 0);
                    }
                    GlStateManager.enableBlend();
                    RenderHelper.disableStandardItemLighting();
                    GlStateManager.popMatrix();
                }
            });
        }

        background.BUS.hook(ButtonMixin.ButtonClickEvent.class, (event -> {
            if (event.getButton() == EnumMouseButton.LEFT) {
                if (!isSelected) {
                    for (ExtraSidebar sidebar : subPage.extraSidebars) sidebar.isSelected = false;
                    isSelected = true;
                    subPage.selectedExtraID = id;
                } else isSelected = false;
            }
        }));

        component = background;
        return this;
    }

    public ComponentSprite getComponent() {
        return component;
    }

    public enum SidebarType {
        IMAGE, RECIPE
    }
}
