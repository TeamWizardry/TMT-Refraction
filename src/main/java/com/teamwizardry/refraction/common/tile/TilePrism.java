package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.network.PacketHandler;
import com.teamwizardry.refraction.common.block.BlockPrism;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;
import com.teamwizardry.refraction.common.network.PacketLaserFX;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

/**
 * Created by LordSaad44
 */
public class TilePrism extends TileEntity implements IBeamHandler {

	private IBlockState state;

	public TilePrism() {
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
	
	public static double airIOR = 1, glassIOR = 1.75;
	
	@Override
	public void handle(Beam... beams)
	{
		glassIOR = 1.2;
		double redIOR = 0.6, greenIOR = 0.4, blueIOR = 0.2;
		
		IBlockState state = this.worldObj.getBlockState(pos);
		if(!( state.getBlock() instanceof  BlockPrism))
			return;
		BlockPrism b = (BlockPrism)state.getBlock();
		
		for (Beam beam : beams)
		{
			float red = beam.color.getRed();
			float green = beam.color.getGreen();
			float blue = beam.color.getBlue();
			float alphaPer = beam.color.getAlpha()/(red+green+blue);
			
			Vec3d dir = beam.finalLoc.subtract(beam.initLoc).normalize();
			
			Vec3d ref = dir;
			Vec3d hitPos = beam.finalLoc;
			
			if(beam.color.getRed() != 0) fireColor(b, state, hitPos, ref, redIOR,   new Color(beam.color.getRed(), 0, 0, alphaPer*beam.color.getRed()), true);
			if(beam.color.getGreen() != 0) fireColor(b, state, hitPos, ref, greenIOR, new Color(0, beam.color.getGreen(), 0, alphaPer*beam.color.getGreen()), true);
			if(beam.color.getBlue() != 0) fireColor(b, state, hitPos, ref, blueIOR,  new Color(0, 0, beam.color.getBlue(), alphaPer*beam.color.getBlue()), true);
		}
	}
	
	private void fireColor(BlockPrism block, IBlockState state, Vec3d hitPos, Vec3d ref, double IORMod, Color color, boolean hasEffect) {
		BlockPrism.RayTraceResultData<Vec3d> r = block.collisionRayTraceLaser(state, worldObj, pos, hitPos.subtract(ref), hitPos.add(ref));
		if(r == null)
			return;
		Vec3d normal = r.data;
		ref = refracted(airIOR+IORMod, glassIOR+IORMod, ref, normal).normalize();
		hitPos = r.hitVec;
		
		for(int i = 0; i < 5; i++) {
			
			r = block.collisionRayTraceLaser(state, worldObj, pos, hitPos.add(ref), hitPos);
			// trace backward so we don't hit hitPos first
			
			if(r == null)
				break;
			else {
				
				normal = r.data.scale(-1);
				Vec3d oldRef = ref;
				ref = refracted(glassIOR+IORMod, airIOR+IORMod, ref, normal).normalize();
				if(Double.isNaN(ref.xCoord) || Double.isNaN(ref.yCoord) || Double.isNaN(ref.zCoord)) {
					ref = oldRef; // it'll bounce back on itself and cause a NaN vector, that means we should stop
					break;
				}
				showBeam(hitPos, r.hitVec, color);
				hitPos = r.hitVec;
			}
			
		}
		
		new Beam(worldObj, hitPos, ref, color);
	}
	
	private Vec3d refracted(double from, double to, Vec3d vec, Vec3d normal) {
		double r = from/to, c = -normal.dotProduct(vec);
		return vec.scale(r).add(   normal.scale((r*c) - Math.sqrt( 1- (r*r)*( 1-(c*c) ) ))   );
	}
	
	private void showBeam(Vec3d start, Vec3d end, Color color) {
		PacketHandler.INSTANCE.getNetwork().sendToAllAround(new PacketLaserFX(start, end, color),
			new NetworkRegistry.TargetPoint(worldObj.provider.getDimension(), start.xCoord, start.yCoord, start.zCoord, 256));
	}
}
