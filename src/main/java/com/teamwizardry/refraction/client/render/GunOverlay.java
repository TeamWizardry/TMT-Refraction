package com.teamwizardry.refraction.client.render;

import com.teamwizardry.librarianlib.client.core.ClientTickHandler;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import com.teamwizardry.librarianlib.client.sprite.Texture;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.IAmmoConsumer;
import com.teamwizardry.refraction.api.Utils;
import com.teamwizardry.refraction.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

/**
 * Created by LordSaad.
 */
public class GunOverlay {

    public static final GunOverlay INSTANCE = new GunOverlay();

    private static Texture texBox;
    private static Texture texVignette;
    private static Sprite sprBox;
    private static Sprite sprVignette;

    static {
        Utils.HANDLER.runIfClient(() -> {
            texBox = new Texture(new ResourceLocation(Constants.MOD_ID, "textures/gui/gunbar/gun_box.png"));
            texVignette = new Texture(new ResourceLocation(Constants.MOD_ID, "textures/gui/gunbar/gun_vignette.png"));
            sprBox = texBox.getSprite("box", 28, 135);
            sprVignette = texVignette.getSprite("box", 28, 135);
        });
    }

    public GunOverlay() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void overlay(RenderGameOverlayEvent.Post event) {
        ItemStack stack = getItemInHand(ModItems.PHOTON_CANNON);
        if (stack == null)
            return;

        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());

            int posX = res.getScaledWidth();
            int posY = res.getScaledHeight();

            GlStateManager.pushMatrix();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.translate(posX, posY / 2, 0);
            GlStateManager.color(1f, 1f, 1f);

            NBTTagCompound compound = stack.getTagCompound();
            if (compound != null) {
                if (compound.hasKey("color")) {
                    texBox.bind();
                    sprBox.draw(ClientTickHandler.getTicks(), -28, -sprBox.getHeight() / 2);

                    Color color = new Color(compound.getInteger("color"));
                    GlStateManager.color(color.getRed(), color.getGreen(), color.getBlue());

                    int width = 0;
                    ItemStack ammo = IAmmoConsumer.findAmmo(Minecraft.getMinecraft().player);
                    if (ammo != null) width = ammo.getItemDamage() * 28 / ammo.getMaxDamage();

                    texVignette.bind();
                    GlStateManager.translate(-posX, -posY / 2, 0);
                    GlStateManager.rotate(180, 0, 0, 1);
                    GlStateManager.translate(-28, -sprVignette.getHeight() / 2, 0);
                    GlStateManager.translate(-posX, -posY / 2, 0);
                    GlStateManager.translate(28, -1, 0);
                    texVignette.bind();
                    sprVignette.drawClipped(ClientTickHandler.getTicks(), 0, 0, width, 135);
                }
            }

            GlStateManager.translate(-posX, -posY / 2, 0);
            GlStateManager.popMatrix();
        }
    }

    private ItemStack getItemInHand(Item item) {
        ItemStack stack = Minecraft.getMinecraft().player.getHeldItemMainhand();
        if (stack == null)
            stack = Minecraft.getMinecraft().player.getHeldItemOffhand();

        if (stack == null)
            return null;
        if (stack.getItem() != item)
            return null;

        return stack;
    }
}
