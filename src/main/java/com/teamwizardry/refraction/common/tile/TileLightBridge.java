package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.common.block.BlockLightBridge;
import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by Saad on 8/18/2016.
 */
@TileRegister("light_bridge")
public class TileLightBridge extends TileMod implements ITickable {

	@Save
	public BlockPos source;
	@Save
	public EnumFacing direction;
	@Save
	public int expire = Constants.SOURCE_TIMER;
	@Save
	public boolean isEnd = false;

	public TileLightBridge() {
	}

	public static void invokeUpdate(BlockPos pos, World world, EnumFacing facing) {
		if (facing == null) return;
		IBlockState frontState = world.getBlockState(pos);
		if (frontState.getBlock() == ModBlocks.LIGHT_BRIDGE) {

			TileLightBridge bridge = (TileLightBridge) world.getTileEntity(pos);
			if (bridge == null) return;
			if (bridge.direction != facing && bridge.direction != facing.getOpposite()) return;

			if (world.getBlockState(bridge.source).getBlock() == ModBlocks.ELECTRON_EXCITER) {
				TileElectronExciter exciter = (TileElectronExciter) world.getTileEntity(bridge.source);
				if (exciter != null && exciter.hasAdjancetExciter()) {
					bridge.expire = Constants.SOURCE_TIMER;
				} else bridge.expire = 0;
			} else bridge.expire = 0;

		} else if (frontState.getBlock() == ModBlocks.ELECTRON_EXCITER) {
			TileElectronExciter exciterTile = (TileElectronExciter) world.getTileEntity(pos);
			if (exciterTile == null) return;
			if (!exciterTile.hasAdjancetExciter()) return;

			world.setBlockState(pos.offset(facing), ModBlocks.LIGHT_BRIDGE.getDefaultState().withProperty(BlockLightBridge.FACING, facing.getAxis()));

			TileLightBridge tileLightBridge = (TileLightBridge) world.getTileEntity(pos.offset(facing));
			if (tileLightBridge != null) {
				tileLightBridge.direction = facing;
				tileLightBridge.source = pos;
				tileLightBridge.isEnd = false;
				tileLightBridge.createNextBlock();
			}
		}
	}

	public void createNextBlock() {
		if (source == null && direction == null) {
			worldObj.setBlockState(pos, Blocks.AIR.getDefaultState());
			return;
		}
		BlockPos front = pos.offset(direction);
		IBlockState state = worldObj.getBlockState(front);
		if (state.getBlock() != Blocks.AIR) {
			isEnd = true;
			TileEntity entity = worldObj.getTileEntity(source);
			if (entity instanceof TileElectronExciter) {
				TileElectronExciter exciter = (TileElectronExciter) entity;
				exciter.bridge = pos;
			}
			return;
		}
		worldObj.setBlockState(front, worldObj.getBlockState(pos));
		TileLightBridge bridge = (TileLightBridge) worldObj.getTileEntity(front);
		if (bridge == null) return;
		bridge.source = source;
		bridge.direction = direction;
		bridge.isEnd = false;
		bridge.createNextBlock();
	}

	@Override
	public void update() {
		if (worldObj.isRemote) return;
		if (isEnd) {
			if (expire > 0) expire--;
			else worldObj.setBlockState(pos, Blocks.AIR.getDefaultState());
		}
	}
}
