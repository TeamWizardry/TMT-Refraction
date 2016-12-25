package com.teamwizardry.refraction.client.gui;

import com.teamwizardry.librarianlib.client.core.ClientTickHandler;
import com.teamwizardry.librarianlib.client.gui.GuiBase;
import com.teamwizardry.librarianlib.client.gui.GuiComponent;
import com.teamwizardry.librarianlib.client.gui.components.ComponentVoid;
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
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.io.IOException;
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

        for (ItemStack ammo : ammoList) {
            Color color = new Color(ItemNBTHelper.getInt(ammo, "color", 0xFFFFF));

            ComponentVoid ammoComp = new ComponentVoid(0, 0);
            ammoComp.BUS.hook(GuiComponent.PostDrawEvent.class, (event) -> {
                GlStateManager.pushMatrix();
                GlStateManager.enableRescaleNormal();
                GlStateManager.enableBlend();

                // TODO: Draw colored triangle sections here.
                // TODO: OR draw colored circles.

                GlStateManager.disableBlend();
                GlStateManager.disableRescaleNormal();
                GlStateManager.popMatrix();
            });

            getMainComponents().add(ammoComp);

        }

    }

    // COPIED PSI CODE
    private static float mouseAngle(int x, int y, int mx, int my) {
        Vector2f baseVec = new Vector2f(1F, 0F);
        Vector2f mouseVec = new Vector2f(mx - x, my - y);

        float ang = (float) (Math.acos(Vector2f.dot(baseVec, mouseVec) / (baseVec.length() * mouseVec.length())) * (180F / Math.PI));
        return my < y ? 360F - ang : ang;
    }

    @Override
    public void drawScreen(int mx, int my, float partialTicks) {
        super.drawScreen(mx, my, partialTicks);

        // COPIED PSI CODE

        /*GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();

        int x = width / 2;
        int y = height / 2;
        int maxRadius = 80;

        float angle = mouseAngle(x, y, mx, my);

        int highlight = 5;

        GlStateManager.enableBlend();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        int segments = slots.size();
        float totalDeg = 0;
        float degPer = 360F / segments;

        for (int seg = 0; seg < segments; seg++) {
            boolean mouseInSector = angle > totalDeg && angle < totalDeg + degPer;
            float radius = Math.max(0F, Math.min((timeIn + partialTicks - seg * 6F / segments) * 40F, maxRadius));

            GL11.glBegin(GL11.GL_TRIANGLE_FAN);

            float gs = 0.25F;
            if (seg % 2 == 0) gs += 0.1F;
            float r = gs;
            float g = gs;
            float b = gs;
            float a = 0.4F;

            if (ammoConsumer != null) {
                Color color = new Color(ItemNBTHelper.getInt(ammoConsumer, "color", 0xFFFFF));
                r = color.getRed() / 255F;
                g = color.getGreen() / 255F;
                b = color.getBlue() / 255F;
            }

            GlStateManager.color(r, g, b, a);
            GL11.glVertex2i(x, y);

            totalDeg += degPer;

            GL11.glVertex2i(x, y);
            GL11.glEnd();

            if (mouseInSector) radius -= highlight;
        }

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableTexture2D();

        float stime = 5F;
        float fract = Math.min(stime, timeIn + partialTicks) / stime;
        float s = 3F * fract;
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.enableGUIStandardItemLighting();

        if (ammoConsumer != null && ammoList.size() > 0) {
            int xs = width / 2 - 18 * ammoList.size() / 2;
            int ys = height / 2;

            for (int i = 0; i < ammoList.size(); i++) {
                float yoff = 25F + maxRadius;
                if (i == selectedSlot)
                    yoff += 5F;

                GlStateManager.translate(0, -yoff * fract, 0F);
                mc.getRenderItem().renderItemAndEffectIntoGUI(ammoList.get(i), xs + i * 18, ys);
                GlStateManager.translate(0, yoff * fract, 0F);
            }

        }

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableBlend();
        GlStateManager.disableRescaleNormal();

        GlStateManager.popMatrix();*/
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        // COPIED PSI CODE
        if (ammoConsumer != null && ammoList.size() > 0) {
            if (mouseButton == 0) {
                selectedSlot++;
                if (selectedSlot >= ammoList.size())
                    selectedSlot = 0;
            } else if (mouseButton == 1) {
                selectedSlot--;
                if (selectedSlot < 0)
                    selectedSlot = ammoList.size() - 1;
            }

            ItemNBTHelper.setInt(ammoConsumer, "color", ItemNBTHelper.getInt(ammoList.get(selectedSlot), "color", 0xFFFFF));
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
