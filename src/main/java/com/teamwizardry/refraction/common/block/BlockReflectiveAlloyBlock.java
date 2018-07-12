package com.teamwizardry.refraction.common.block;

import com.teamwizardry.librarianlib.features.base.block.BlockMod;
import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer;
import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.Utils;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.ILightSink;
import com.teamwizardry.refraction.common.item.ItemScrewDriver;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Objects;

/**
 * Created by Saad on 10/16/2016.
 */
public class BlockReflectiveAlloyBlock extends BlockMod implements ILightSink {

	public BlockReflectiveAlloyBlock() {
		super("reflective_alloy_block", Material.IRON);
		setHardness(1F);
		setSoundType(SoundType.METAL);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		TooltipHelper.addToTooltip(tooltip, "simple_name." + Constants.MOD_ID + ":" + getRegistryName().getResourcePath());
	}

	@Override
	public boolean handleBeam(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Beam beam) {
		EnumFacing facing = Utils.getCollisionSide(beam.trace);
		if (facing == null) return true;

		Vec3d incomingDir = beam.slope;
		Vec3d outgoingDir;
		Vec3d outgoingLoc = beam.finalLoc;
		switch (facing) {
			case UP:
				outgoingDir = new Vec3d(incomingDir.x, Math.abs(incomingDir.y), incomingDir.z);
				outgoingLoc = outgoingLoc.subtract(0, 0.001, 0);
				break;
			case DOWN:
				outgoingDir = new Vec3d(incomingDir.x, -Math.abs(incomingDir.y), incomingDir.z);
				break;
			case NORTH:
				outgoingDir = new Vec3d(incomingDir.x, incomingDir.y, -Math.abs(incomingDir.z));
				break;
			case SOUTH:
				outgoingDir = new Vec3d(incomingDir.x, incomingDir.y, Math.abs(incomingDir.z));
				outgoingLoc = outgoingLoc.subtract(0, 0, 0.001);
				break;
			case EAST:
				outgoingDir = new Vec3d(Math.abs(incomingDir.x), incomingDir.y, incomingDir.z);
				outgoingLoc = outgoingLoc.subtract(0.001, 0, 0);
				break;
			case WEST:
				outgoingDir = new Vec3d(-Math.abs(incomingDir.x), incomingDir.y, incomingDir.z);
				break;
			default:
				outgoingDir = incomingDir;
		}
		beam.createSimilarBeam(outgoingLoc, outgoingDir, beam.effect.copy().setPotency((int) (beam.getAlpha() / 1.05))).spawn();
		return true;
	}

	@Override
	public boolean isToolEffective(String type, IBlockState state) {
		return super.isToolEffective(type, state) || Objects.equals(type, ItemScrewDriver.SCREWDRIVER_TOOL_CLASS);
	}

	@Override
	public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beacon) {
		return true;
	}
}
