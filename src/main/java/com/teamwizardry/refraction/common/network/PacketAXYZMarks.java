package com.teamwizardry.refraction.common.network;

import com.teamwizardry.librarianlib.common.network.PacketBase;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketAXYZMarks extends PacketBase {

	@Save
	private BlockPos[] positions, originPositions;

	public PacketAXYZMarks() {}

	public PacketAXYZMarks(BlockPos[] positions, BlockPos[] origins) {
		this.positions = positions;
		this.originPositions = origins;
	}

	public boolean canPush(World world, BlockPos pos) {
		IBlockState srcState = world.getBlockState(pos);
		TileEntity tile = world.getTileEntity(pos);
		Material mat = srcState.getMaterial();
		return tile == null &&
				mat.getMobilityFlag() == EnumPushReaction.NORMAL &&
				srcState.getBlockHardness(world, pos) != -1 &&
				!srcState.getBlock().isAir(srcState, world, pos);
	}

	public boolean isAir(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		return state.getBlock().isAir(state, world, pos);
	}
	
	@Override
	public void handle(MessageContext ctx) {
		if (ctx.side.isServer()) return;

		World world = Minecraft.getMinecraft().theWorld;

		for (int i = 0; i < positions.length; i++) {
			BlockPos pos = positions[i], origin = originPositions[i];
			if (isAir(world, pos)) {
				// black particles in the center plus a smaller set of black particles streaming towards original block
			} else if (!canPush(world, pos)) {
				// red steaming particles around the block plus a smaller set of red particles streaming towards original block
			} else {
				// black steaming particles around the block plus a smaller set of black particles streaming towards original block
			}
		}
	}
}
