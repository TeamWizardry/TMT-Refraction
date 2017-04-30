package com.teamwizardry.refraction.common.network;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.awt.*;

/**
 * Created by LordSaad.
 */
public class PacketAmmoColorChange extends PacketBase {

	private int slot;
	private Color color;

	public PacketAmmoColorChange() {

	}

	public PacketAmmoColorChange(int slot, Color color) {
		this.slot = slot;
		this.color = color;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(slot);
		buf.writeInt(color.getRGB());
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		slot = buf.readInt();
		color = new Color(buf.readInt(), true);
	}

	@Override
	public void handle(MessageContext messageContext) {
		ItemStack stack = messageContext.getServerHandler().player.inventory.getStackInSlot(slot);
		if (stack.isEmpty()) return;
		ItemNBTHelper.setInt(stack, "color", color.getRGB());
	}
}
