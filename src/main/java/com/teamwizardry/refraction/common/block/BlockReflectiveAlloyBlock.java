package com.teamwizardry.refraction.common.block;

import com.teamwizardry.librarianlib.client.util.TooltipHelper;
import com.teamwizardry.librarianlib.common.base.block.BlockMod;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.Utils;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.IBeamHandler;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.Objects;

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
        TooltipHelper.addToTooltip(tooltip, "simple_name." + Constants.MOD_ID + ":" + getRegistryName().getResourcePath());
    }

    @Override
    public boolean handleBeam(@NotNull World world, @NotNull BlockPos pos, @NotNull Beam beam) {
        EnumFacing facing = Utils.getCollisionSide(beam.trace);
        if (facing == null) return true;

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
        beam.createSimilarBeam(outgoingLoc, outgoingDir, c).enableParticleBeginning().spawn();
        return true;
    }

    @Override
    public boolean isToolEffective(String type, IBlockState state) {
        return super.isToolEffective(type, state) || Objects.equals(type, "screwdriver");
    }
}
