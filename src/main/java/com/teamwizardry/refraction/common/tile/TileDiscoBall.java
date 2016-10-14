package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.util.math.Matrix4;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad44
 */
public class TileDiscoBall extends TileEntity implements IBeamHandler, ITickable {

	private IBlockState state;
	private Set<BeamHandler> handlers = new HashSet<>();
	private double rotX = 0, rotY = 0;

	public TileDiscoBall() {
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);

		// TODO
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);

		// TODO

		return compound;
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new SPacketUpdateTileEntity(pos, 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		super.onDataPacket(net, packet);
		readFromNBT(packet.getNbtCompound());

		state = worldObj.getBlockState(pos);
		worldObj.notifyBlockUpdate(pos, state, state, 3);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public net.minecraft.util.math.AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public void handle(Beam... inputs) {
		if (!worldObj.isBlockPowered(getPos()) && worldObj.isBlockIndirectlyGettingPowered(getPos()) == 0) return;
		for (Beam beam : inputs) {
			beam.color = new Color(beam.color.getRed(), beam.color.getGreen(), beam.color.getBlue(), beam.color.getAlpha() / ThreadLocalRandom.current().nextInt(1, 8));
			for (int i = 0; i < 4; i++) {
				double radius = 5;
				double t = 2 * Math.PI * ThreadLocalRandom.current().nextDouble(-radius, radius);
				double u = ThreadLocalRandom.current().nextDouble(-radius, radius) + ThreadLocalRandom.current().nextDouble(-radius, radius);
				double r = (u > 1) ? 2 - u : u;
				double x = r * Math.cos(t), z = r * Math.sin(t);
				Vec3d dest = new Vec3d(x, getPos().getY() - 1, z).scale(-1);
				handlers.add(new BeamHandler(dest, new Vec3d(getPos()).addVector(0.5, 0.3, 0.5).addVector(ThreadLocalRandom.current().nextDouble(-0.3, 0.3), ThreadLocalRandom.current().nextDouble(-0.3, 0.3), ThreadLocalRandom.current().nextDouble(-0.3, 0.3)), beam.color, beam.enableEffect));
			}
		}
	}

	@Override
	public void update() {
		if (!worldObj.isBlockPowered(getPos()) && worldObj.isBlockIndirectlyGettingPowered(getPos()) == 0) return;
		if (handlers.size() == 0) return;
		if (rotX < 360) rotX += 5;
		else rotX = 0;

		for (Iterator<BeamHandler> iterator = handlers.iterator(); iterator.hasNext(); ) {
			BeamHandler handler = iterator.next();
			handler.life--;
			Matrix4 matrix = new Matrix4();
			matrix.rotate(Math.toRadians(rotX), new Vec3d(0, handler.invert ? -1 : 1, 0));
			Color c = new Color(handler.color.getRed(), handler.color.getGreen(), handler.color.getBlue(), handler.color.getAlpha() * handler.life / handler.maxLife);
			new Beam(worldObj, handler.origin, matrix.apply(handler.dest), c, handler.enableEffect, false);

			if (handler.life <= 0) iterator.remove();
		}
	}

	private class BeamHandler {
		public Vec3d dest, origin;
		public Color color;
		public boolean enableEffect;
		public boolean invert = false;
		public int life = 20, maxLife = 20;

		public BeamHandler(Vec3d dest, Vec3d origin, Color color, boolean enableEffect) {
			this.dest = dest;
			this.origin = origin;
			this.color = color;
			this.enableEffect = enableEffect;
			this.invert = ThreadLocalRandom.current().nextBoolean();
			this.life = this.maxLife = ThreadLocalRandom.current().nextInt(5, 10);
		}
	}
}
