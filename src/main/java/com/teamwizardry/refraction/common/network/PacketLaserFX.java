package com.teamwizardry.refraction.common.network;

import com.teamwizardry.librarianlib.network.PacketBase;
import com.teamwizardry.librarianlib.util.Color;
import com.teamwizardry.refraction.client.LaserRenderer;
import com.teamwizardry.refraction.client.fx.ParticleLaser;
import com.teamwizardry.refraction.client.fx.SparkleFX;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by TheCodeWarrior
 */
public class PacketLaserFX extends PacketBase {
	
	protected Vec3d start, end;
	protected Color color;
	
	public PacketLaserFX() {}
	
	public PacketLaserFX(Vec3d start, Vec3d end, Color color) {
		this.start = start;
		this.end = end;
		this.color = color;
	}
	
	@Override
	public void handle(MessageContext ctx) {
		LaserRenderer.add(start, end, color);
//		float density = 2;
		
//		Vec3d o = end.subtract(start).normalize().scale(1.0/density);
//		int amount = (int)( end.subtract(start).lengthVector()*density );
//
//		double x = start.xCoord;
//		double y = start.yCoord;
//		double z = start.zCoord;
//
//		for(int i = 0; i < amount; i++) {
//			SparkleFX p = new SparkleFX(Minecraft.getMinecraft().theWorld, x, y, z);
//			p.setAge(0);
//			Minecraft.getMinecraft().effectRenderer.addEffect(p);
//			x += o.xCoord;
//			y += o.yCoord;
//			z += o.zCoord;
//		}
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		start = new Vec3d(buf.readFloat(), buf.readFloat(), buf.readFloat());
		end = new Vec3d(buf.readFloat(), buf.readFloat(), buf.readFloat());
		color = new Color(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeFloat((float)start.xCoord);
		buf.writeFloat((float)start.yCoord);
		buf.writeFloat((float)start.zCoord);
		
		buf.writeFloat((float)end.xCoord);
		buf.writeFloat((float)end.yCoord);
		buf.writeFloat((float)end.zCoord);
		
		buf.writeFloat((float)color.r);
		buf.writeFloat((float)color.g);
		buf.writeFloat((float)color.b);
		buf.writeFloat((float)color.a);
	}
}
