package com.teamwizardry.refraction.common.block;

import com.teamwizardry.librarianlib.client.util.TooltipHelper;
import com.teamwizardry.librarianlib.common.base.block.BlockMod;
import com.teamwizardry.refraction.api.IBeamHandler;
import com.teamwizardry.refraction.api.Utils;
import com.teamwizardry.refraction.common.light.Beam;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.UUID;

/**
 * Created by Saad on 10/16/2016.
 */
public class BlockReflectiveAlloyBlock extends BlockMod implements IBeamHandler {

	public BlockReflectiveAlloyBlock() {
		super("reflective_alloy_block", Material.IRON);
		setHardness(1F);
		setSoundType(SoundType.METAL);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		TooltipHelper.addToTooltip(tooltip, "simple_name.refraction:" + getRegistryName().getResourcePath());
	}

	@Override
	public void handleBeams(@NotNull World world, @NotNull BlockPos pos, @NotNull Beam... beams) {
		if (beams.length == 0)
			return;

		for (Beam beam : beams) {
			EnumFacing facing = Utils.getCollisionSide(beam.trace);
			if (facing == null)
				continue;

			Vec3d incomingDir = beam.slope;
			Vec3d outgoingDir;
			Vec3d outgoingLoc = beam.finalLoc;
			switch (facing) {
				case UP:
					outgoingDir = new Vec3d(incomingDir.xCoord, Math.abs(incomingDir.yCoord), incomingDir.zCoord);
					outgoingLoc = outgoingLoc.subtract(0, 0.001, 0);
					break;
				case DOWN:
					outgoingDir = new Vec3d(incomingDir.xCoord, -Math.abs(incomingDir.yCoord), incomingDir.zCoord);
					break;
				case NORTH:
					outgoingDir = new Vec3d(incomingDir.xCoord, incomingDir.yCoord, -Math.abs(incomingDir.zCoord));
					break;
				case SOUTH:
					outgoingDir = new Vec3d(incomingDir.xCoord, incomingDir.yCoord, Math.abs(incomingDir.zCoord));
					outgoingLoc = outgoingLoc.subtract(0, 0, 0.001);
					break;
				case EAST:
					outgoingDir = new Vec3d(Math.abs(incomingDir.xCoord), incomingDir.yCoord, incomingDir.zCoord);
					outgoingLoc = outgoingLoc.subtract(0.001, 0, 0);
					break;
				case WEST:
					outgoingDir = new Vec3d(-Math.abs(incomingDir.xCoord), incomingDir.yCoord, incomingDir.zCoord);
					break;
				default:
					outgoingDir = incomingDir;
			}
			Color c = new Color(beam.color.getRed(), beam.color.getGreen(), beam.color.getBlue(), (int) (beam.color.getAlpha() / 1.05));
			beam.createSimilarBeam(outgoingLoc, outgoingDir, c).setUUID(UUID.randomUUID()).spawn();
		}
	}
}
