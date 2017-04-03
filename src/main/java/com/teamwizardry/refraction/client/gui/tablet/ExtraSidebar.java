package com.teamwizardry.refraction.client.gui.tablet;

import com.google.gson.JsonObject;
import com.teamwizardry.librarianlib.client.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.client.gui.GuiComponent;
import com.teamwizardry.librarianlib.client.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.client.gui.components.ComponentStack;
import com.teamwizardry.librarianlib.client.gui.components.ComponentText;
import com.teamwizardry.librarianlib.client.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.client.gui.mixin.ButtonMixin;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import com.teamwizardry.librarianlib.common.util.math.Vec2d;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.client.gui.RightSidebar;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by LordSaad.
 */
public class ExtraSidebar {

	@Nonnull
	private final SidebarType sidebarType;
	public int id = 0;
	@Nullable
	public ComponentVoid contentComp, iconComp;
	@Nullable
	public ComponentStack slotcomp;
	@Nullable
	public Sprite image;
	public String title;
	public SubPage subPage;
	public boolean isSelected = false;
	private ComponentSprite component;

	public ExtraSidebar(SubPage subPage, int id, @Nullable JsonObject object, @Nonnull SidebarType sidebarType) {
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
		int x = RightSidebar.rightNormal.getWidth() + GuiBook.BACKGROUND_SPRITE.getWidth() - 14;
		ComponentSprite background = new ComponentSprite(RightSidebar.rightNormal, x, 0, RightSidebar.rightNormal.getWidth(), RightSidebar.rightNormal.getHeight());
		background.addTag(id);

		if (sidebarType == SidebarType.IMAGE && image != null) {
			ComponentSprite imageComp = new ComponentSprite(this.image, RightSidebar.rightNormal.getWidth() - this.image.getWidth(), 1, 16, 16);
			new ButtonMixin<>(imageComp, () -> {
			});
			imageComp.BUS.hook(GuiComponent.ComponentTickEvent.class, (event) -> {
				if (isSelected) {
					imageComp.setSize(new Vec2d(64, 64));
					imageComp.setPos(new Vec2d(32, 20));
				} else {
					imageComp.setSize(new Vec2d(16, 16));
					imageComp.setPos(new Vec2d(RightSidebar.rightNormal.getWidth() - imageComp.getSize().getXi() - 8, 1));
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
				background.setSprite(RightSidebar.rightLarge);
				background.setPos(new Vec2d(x, (20 * id) - (subPage.id * 20) - (subPage.page.id * 20) - 20));
				background.setSize(new Vec2d(RightSidebar.rightLarge.getWidth(), RightSidebar.rightLarge.getHeight()));
			} else {
				background.setSprite(RightSidebar.rightNormal);
				if (subPage.selectedExtraID < id && subPage.extraSidebars.get(subPage.selectedExtraID).isSelected)
					background.setPos(new Vec2d(x, (20 * id) - (subPage.id * 20) - (subPage.page.id * 20) + (RightSidebar.rightLarge.getHeight()) - 38));
				else background.setPos(new Vec2d(x, (20 * id) - (subPage.id * 20) - (subPage.page.id * 20) - 20));
				background.setSize(new Vec2d(RightSidebar.rightNormal.getWidth(), RightSidebar.rightNormal.getHeight()));
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
