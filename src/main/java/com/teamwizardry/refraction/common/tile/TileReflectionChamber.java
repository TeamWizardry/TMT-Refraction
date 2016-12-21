package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.refraction.api.MultipleBeamTile;
import com.teamwizardry.refraction.api.PosUtils;
import com.teamwizardry.refraction.api.RotationHelper;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.modes.BeamMode;
import com.teamwizardry.refraction.api.beam.modes.ModeEffect;
import com.teamwizardry.refraction.common.block.BlockOpticFiber;
import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by LordSaad44
 */
@TileRegister("reflection_chamber")
public class TileReflectionChamber extends MultipleBeamTile implements ITickable {

    @Nullable
    private Beam beam;

    @NotNull
    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public void update() {
        World world = getWorld();

        if (beam != null) beam.spawn();

        if (checkTick()) {
            beam = null;
            HashMap<BeamMode, ColorData> colorData = new HashMap<>();

            for (Beam beam : beams) {

                boolean pass = false;
                BeamMode finalMode = new ModeEffect();
                for (BeamMode mode : colorData.keySet()) {
                    if (beam.mode.getClass().isAssignableFrom(mode.getClass())) {
                        pass = true;
                        finalMode = mode;
                        break;
                    }
                }

                Color color = beam.color;
                if (!pass) {
                    colorData.put(beam.mode, new ColorData(color, beam.slope));
                    continue;
                }

                ColorData data = colorData.get(finalMode);
                data.red += color.getRed() * (color.getAlpha() / 255f);
                data.green += color.getGreen() * (color.getAlpha() / 255f);
                data.blue += color.getBlue() * (color.getAlpha() / 255f);
                data.alpha += color.getAlpha();

                List<Vec3d> angles = new ArrayList<>();
                angles.add(data.angle);
                angles.add(beam.slope);
                data.angle = RotationHelper.averageDirection(angles);

                data.count++;

                colorData.put(finalMode, data);
            }

            for (BeamMode mode : colorData.keySet()) {
                ColorData data = colorData.get(mode);
                int red = Math.min(data.red / data.count, 255),
                        green = Math.min(data.green / data.count, 255),
                        blue = Math.min(data.blue / data.count, 255),
                        alpha = Math.min(data.alpha, 255);

                float[] hsbvals = Color.RGBtoHSB(red, green, blue, null);
                Color color = new Color(Color.HSBtoRGB(hsbvals[0], hsbvals[1], 1));
                color = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);

                Beam beam = new Beam(world, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), data.angle, color).setMode(mode);
                EnumFacing facing = EnumFacing.getFacingFromVector((float) beam.slope.xCoord, (float) beam.slope.yCoord, (float) beam.slope.zCoord);
                IBlockState state = world.getBlockState(pos.offset(facing));
                if (state.getBlock() == ModBlocks.OPTIC_FIBER && state.getValue(BlockOpticFiber.FACING).contains(facing))
                    this.beam = beam.setSlope(PosUtils.getVecFromFacing(facing));
                else this.beam = beam;
            }

            endUpdateTick();
        }
    }

    private class ColorData {

        public int count = 1;
        int red, green, blue, alpha;
        Vec3d angle;

        ColorData(Color color, Vec3d angle) {
            red = color.getRed();
            green = color.getGreen();
            blue = color.getBlue();
            alpha = color.getAlpha();
            this.angle = angle;
        }
    }
}
