package com.teamwizardry.refraction.common.network;

import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.refraction.client.render.LaserRenderer;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.awt.*;

/**
 * Created by TheCodeWarrior
 */
public class PacketLaserFX extends PacketBase {

	protected Vec3d start, end;
	protected Color color;

	public PacketLaserFX() {
	}

	public PacketLaserFX(Vec3d start, Vec3d end, Color color) {
		this.start = start;
		this.end = end;
		this.color = color;
	}

	@Override
	public void handle(MessageContext ctx) {
		LaserRenderer.add(start, end, color);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		start = new Vec3d(buf.readFloat(), buf.readFloat(), buf.readFloat());
		end = new Vec3d(buf.readFloat(), buf.readFloat(), buf.readFloat());
		color = new Color(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeFloat((float) start.x);
		buf.writeFloat((float) start.y);
		buf.writeFloat((float) start.z);

		buf.writeFloat((float) end.x);
		buf.writeFloat((float) end.y);
		buf.writeFloat((float) end.z);

		buf.writeInt(color.getRed());
		buf.writeInt(color.getGreen());
		buf.writeInt(color.getBlue());
		buf.writeInt(color.getAlpha());
	}
}
