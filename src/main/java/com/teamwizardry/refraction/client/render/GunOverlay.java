package com.teamwizardry.refraction.client.render;

import com.teamwizardry.librarianlib.client.core.ClientTickHandler;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import com.teamwizardry.librarianlib.client.sprite.Texture;
import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.IAmmoConsumer;
import com.teamwizardry.refraction.api.Utils;
import com.teamwizardry.refraction.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Created by LordSaad.
 */
public class GunOverlay {

    public static final GunOverlay INSTANCE = new GunOverlay();

    private static Texture texBox;
    private static Texture texHandleVignette;
    private static Texture texPallete;
    private static Sprite sprBox;
    private static Sprite sprHandleVignette;
    private static Sprite sprPallete;

    static {
        Utils.HANDLER.runIfClient(() -> {
            texBox = new Texture(new ResourceLocation(Constants.MOD_ID, "textures/gui/ammoselector/gun_box.png"));
            texHandleVignette = new Texture(new ResourceLocation(Constants.MOD_ID, "textures/gui/ammoselector/gun_vignette.png"));
            texPallete = new Texture(new ResourceLocation(Constants.MOD_ID, "textures/gui/ammoselector/pallete.png"));

            sprBox = texBox.getSprite("box", 28, 135);
            sprHandleVignette = texHandleVignette.getSprite("box", 28, 135);
            sprPallete = texPallete.getSprite("box", 16, 16);
        });
    }

    public GunOverlay() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void overlay(RenderGameOverlayEvent.Post event) {
        ItemStack stack = getItemInHand(ModItems.PHOTON_CANNON);
        if (event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR) return;
        if (stack == null) return;

        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());

        int posX = res.getScaledWidth();
        int posY = res.getScaledHeight();


        if (GuiScreen.isAltKeyDown()) {
            List<ItemStack> ammoList = IAmmoConsumer.findAllAmmo(Minecraft.getMinecraft().player);
            Set<Color> colorSet = ammoList.stream().map(ammo -> new Color(ItemNBTHelper.getInt(ammo, "color", 0xFFFFF), true)).collect(Collectors.toSet());
            List<Color> colors = new ArrayList<>(colorSet);

            for (Color color : colors) {
                // TODO: COLOR WHEEL HERE

                double slice = 2 * Math.PI / ammoList.size();
                double radius = 30;
                int slot = colors.indexOf(color);
                double angle = slice * slot;
                float newX = (float) (-4 + radius * Math.cos(angle));
                float newY = (float) (-4 + radius * Math.sin(angle));

                GlStateManager.pushMatrix();
                GlStateManager.enableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.translate(posX / 2, posY / 2, 0);
                GlStateManager.translate(newX, newY, 0);
                GlStateManager.scale(0.5, 0.5, 0.5);
                GlStateManager.color(color.getRed() / 255, color.getGreen() / 255, color.getBlue() / 255);
                texPallete.bind();
                sprPallete.draw((int) ClientTickHandler.getPartialTicks(), 0, 0);

                GlStateManager.popMatrix();
            }
        }

        // RIGHT SIDEBAR //
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
                List<ItemStack> ammoList = IAmmoConsumer.findAllAmmo(Minecraft.getMinecraft().player, color);
                for (ItemStack ammo : ammoList)
                    width = Math.min(28, width + (ammo.getMaxDamage() - ammo.getItemDamage()) * 28 / ammo.getMaxDamage());

                texHandleVignette.bind();
                GlStateManager.translate(-posX, -posY / 2, 0);
                GlStateManager.rotate(180, 0, 0, 1);
                GlStateManager.translate(-28, -sprHandleVignette.getHeight() / 2, 0);
                GlStateManager.translate(-posX, -posY / 2, 0);
                GlStateManager.translate(28, -1, 0);
                texHandleVignette.bind();
                sprHandleVignette.drawClipped(ClientTickHandler.getTicks(), 0, 0, width, 135);
            }
        }

        GlStateManager.translate(-posX, -posY / 2, 0);
        GlStateManager.popMatrix();
        // RIGHT SIDEBAR //
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
