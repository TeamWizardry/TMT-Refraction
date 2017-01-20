package com.teamwizardry.refraction.client;

import com.teamwizardry.librarianlib.common.network.PacketHandler;
import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.IAmmo;
import com.teamwizardry.refraction.api.IAmmoConsumer;
import com.teamwizardry.refraction.api.Utils;
import com.teamwizardry.refraction.common.network.PacketAmmoColorChange;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by TheCodeWarrior
 */
public class EventHandlerClient {
    public static final EventHandlerClient INSTANCE = new EventHandlerClient();
    public static int index = 0;

    private EventHandlerClient() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void stitch(TextureStitchEvent.Pre event) {
        event.getMap().registerSprite(new ResourceLocation(Constants.MOD_ID, "particles/glow"));
        event.getMap().registerSprite(new ResourceLocation(Constants.MOD_ID, "particles/star"));
        event.getMap().registerSprite(new ResourceLocation(Constants.MOD_ID, "particles/sparkle_blurred"));
        event.getMap().registerSprite(new ResourceLocation(Constants.MOD_ID, "particles/lens_flare_1"));
    }

    @SubscribeEvent
    public void onMouseEvent(MouseEvent event) {
        if (event.getDwheel() != 0 && Minecraft.getMinecraft().player.isSneaking()) {
            ItemStack stack = Minecraft.getMinecraft().player.getHeldItemMainhand();
            if (stack != null && stack.getItem() instanceof IAmmoConsumer) {

                List<ItemStack> ammoList = IAmmoConsumer.findAllAmmo(Minecraft.getMinecraft().player);
                if (ammoList.size() <= 0) {
                    event.setCanceled(true);
                    return;
                }
                Set<Color> colorSet = ammoList.stream().map(ammo -> new Color(((IAmmo) ammo.getItem()).getColor(ammo), true)).collect(Collectors.toSet());
                List<Color> colors = new ArrayList<>(colorSet);

                Color gunColor = new Color(ItemNBTHelper.getInt(stack, "color", 0xFFFFFF), true);
                int slot = -1;
                for (Color color : colors)
                    if (Utils.doColorsMatchNoAlpha(color, gunColor)) {
                        slot = colors.indexOf(color);
                        break;
                    }
                if (slot == -1) slot = 0;

                if (event.getDwheel() > 0) {
                    if (colors.size() - 1 >= slot + 1)
                        PacketHandler.NETWORK.sendToServer(new PacketAmmoColorChange(Minecraft.getMinecraft().player.inventory.getSlotFor(stack), colors.get(slot + 1)));
                    else
                        PacketHandler.NETWORK.sendToServer(new PacketAmmoColorChange(Minecraft.getMinecraft().player.inventory.getSlotFor(stack), colors.get(0)));
                } else if (event.getDwheel() < 0) {
                    if (slot - 1 >= 0)
                        PacketHandler.NETWORK.sendToServer(new PacketAmmoColorChange(Minecraft.getMinecraft().player.inventory.getSlotFor(stack), colors.get(slot - 1)));
                    else
                        PacketHandler.NETWORK.sendToServer(new PacketAmmoColorChange(Minecraft.getMinecraft().player.inventory.getSlotFor(stack), colors.get(colors.size() - 1)));
                }
                event.setCanceled(true);
            }
        }
    }
}
