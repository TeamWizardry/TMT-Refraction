package com.teamwizardry.refraction.client.gui;

import com.teamwizardry.librarianlib.client.core.ClientTickHandler;
import com.teamwizardry.librarianlib.client.gui.GuiBase;
import com.teamwizardry.librarianlib.client.gui.GuiComponent;
import com.teamwizardry.librarianlib.client.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.client.gui.mixin.ButtonMixin;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import com.teamwizardry.librarianlib.client.sprite.Texture;
import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.IAmmoConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LordSaad.
 */
public class GuiAmmoSelector extends GuiBase {

    private static final ResourceLocation background = new ResourceLocation(Constants.MOD_ID, "textures/gui/ammo_recharge.png");
    private static final Texture texBackground = new Texture(background);
    private static final Sprite sprBackground = texBackground.getSprite("bg", 128, 128);

    private int timeIn = 0;

    private int selectedSlot;

    private ItemStack ammoConsumer;
    private List<Integer> slots = new ArrayList<>();
    private List<ItemStack> ammoList = new ArrayList<>();

    public GuiAmmoSelector() {
        super(0, 0);
        mc = Minecraft.getMinecraft();
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());

        int posX = res.getScaledWidth();
        int posY = res.getScaledHeight();

        if (mc.player.getActiveItemStack() == null) return;
        if (!(mc.player.getActiveItemStack().getItem() instanceof IAmmoConsumer)) return;

        ammoConsumer = mc.player.getActiveItemStack();
        ammoList = IAmmoConsumer.findAllAmmo(mc.player);
        for (int i = 0; i < ammoList.size(); i++) slots.add(i);

        ComponentVoid background = new ComponentVoid(0, 0);
        background.BUS.hook(GuiComponent.PostDrawEvent.class, (event) -> {
            // TODO: start from a scale of 1/1.5, then scale it up FAST then slow it down as it
            // TODO: reaches it's full size (which should be doubled because the image is small.
            // TODO: What also should happen is that it starts from color(0,0,0) and finish at (1,1,1)
            float maxTime = 8F;
            float fract = Math.min(maxTime, (timeIn + ClientTickHandler.getPartialTicks())) / maxTime;
            float scale = 1.5F * fract;
            GlStateManager.pushMatrix();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();

            GlStateManager.scale(scale, scale, scale);
            GlStateManager.translate(-sprBackground.getWidth() / 2, -sprBackground.getHeight() / 2, 0);
            texBackground.bind();
            sprBackground.draw((int) ClientTickHandler.getPartialTicks(), 0, 0);

            GlStateManager.disableBlend();
            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();
        });
        getMainComponents().add(background);

        ComponentVoid ammoConsumerComp = new ComponentVoid(0, 0);
        ammoConsumerComp.BUS.hook(GuiComponent.PostDrawEvent.class, (event) -> {
            // TODO: start from a scale of 9, then scale it up FAST then slow it down as it
            // TODO: reaches it's full size (which should be doubled because the image is small.
            // TODO: What also should happen is that it starts from color(0,0,0) and finish at (1,1,1)
            float maxTime = 5F;
            float fract = Math.min(maxTime, (timeIn + ClientTickHandler.getPartialTicks())) / maxTime;
            float scale = 3F * fract;
            GlStateManager.pushMatrix();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();
            RenderHelper.enableStandardItemLighting();

            GlStateManager.scale(scale, scale, scale);
            GlStateManager.translate(-8, -8, 0);
            mc.getRenderItem().renderItemAndEffectIntoGUI(ammoConsumer, 0, 0);

            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableBlend();
            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();
        });
        getMainComponents().add(ammoConsumerComp);

        double slice = 2 * Math.PI / ammoList.size();
        for (ItemStack ammo : ammoList) {
            Color color = new Color(ItemNBTHelper.getInt(ammo, "color", 0xFFFFF));

            new ButtonMixin<>(background, () -> {
            });

            double radius = 200, size = 0.3;
            int slot = ammoList.indexOf(ammo);
            double angle = slice * slot;
            float newX = (float) ((-sprBackground.getHeight() / 2) + radius * Math.cos(angle));
            float newY = (float) ((-sprBackground.getHeight() / 2) + radius * Math.sin(angle));

            ComponentVoid ammoComp = new ComponentVoid((int) (newX * size), (int) (newY * size), (int) (sprBackground.getWidth() * size), (int) (sprBackground.getHeight() * size));
            ammoComp.BUS.hook(GuiComponent.PostDrawEvent.class, (event) -> {
                GlStateManager.pushMatrix();
                GlStateManager.enableRescaleNormal();
                GlStateManager.enableBlend();
                GlStateManager.enableAlpha();

                GlStateManager.scale(size, size, size);
                GlStateManager.color((float) (color.getRed() / 255.0), (float) (color.getGreen() / 255.0), (float) (color.getBlue() / 255.0));
                texBackground.bind();
                sprBackground.draw((int) ClientTickHandler.getPartialTicks(), newX, newY);

                GlStateManager.color(1, 1, 1);
                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
                GlStateManager.disableRescaleNormal();
                GlStateManager.popMatrix();
            });
            ammoComp.BUS.hook(ButtonMixin.ButtonClickEvent.class, (event) -> {
                mc.player.sendChatMessage(".");
                ItemNBTHelper.setInt(ammoConsumer, "color", ItemNBTHelper.getInt(ammo, "color", 0xFFFFF));
            });

            getMainComponents().add(ammoComp);

        }

    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        timeIn++;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

}
