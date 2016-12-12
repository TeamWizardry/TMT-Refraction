package com.teamwizardry.refraction.common.block;

import com.teamwizardry.librarianlib.client.util.TooltipHelper;
import com.teamwizardry.librarianlib.common.base.block.BlockMod;
import com.teamwizardry.librarianlib.common.network.PacketHandler;
import com.teamwizardry.librarianlib.common.util.math.Matrix4;
import com.teamwizardry.refraction.api.ConfigValues;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.IBeamHandler;
import com.teamwizardry.refraction.api.raytrace.ILaserTrace;
import com.teamwizardry.refraction.api.raytrace.Tri;
import com.teamwizardry.refraction.common.network.PacketLaserFX;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.UUID;

/**
 * Created by LordSaad44
 */
public class BlockLens extends BlockMod implements ILaserTrace, IBeamHandler {

    public BlockLens() {
        super("lens", Material.GLASS);
        setHardness(1F);
        setSoundType(SoundType.GLASS);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return new AxisAlignedBB(0, 0, 0, 1, 0.5, 1);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        TooltipHelper.addToTooltip(tooltip, "simple_name." + Constants.MOD_ID + ":" + getRegistryName().getResourcePath());
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
        Block block = iblockstate.getBlock();
        return blockState != iblockstate || block != this && block != this && super.shouldSideBeRendered(blockState, blockAccess, pos, side);

    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState blockState) {
        return false;
    }

    @Override
    public void handleBeam(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Beam beam) {
        IBlockState state = world.getBlockState(pos);
        fireColor(world, pos, state, beam.finalLoc, beam.finalLoc.subtract(beam.initLoc).normalize(), ConfigValues.GLASS_IOR, beam.color, beam.enableEffect, beam.ignoreEntities, UUID.randomUUID());
    }

    private void fireColor(World worldObj, BlockPos pos, IBlockState state, Vec3d hitPos, Vec3d ref, double IORMod, Color color, boolean disableEffect, boolean ignoreEntities, UUID uuid) {
        BlockPrism.RayTraceResultData<Vec3d> r = collisionRayTraceLaser(state, worldObj, pos, hitPos.subtract(ref), hitPos.add(ref));
        assert r != null;
        Vec3d normal = r.data;
        ref = refracted(ConfigValues.AIR_IOR + IORMod, ConfigValues.GLASS_IOR + IORMod, ref, normal).normalize();
        hitPos = r.hitVec;

        for (int i = 0; i < 5; i++) {

            r = collisionRayTraceLaser(state, worldObj, pos, hitPos.add(ref), hitPos);
            // trace backward so we don't hit hitPos first

            assert r != null;
            normal = r.data.scale(-1);
            Vec3d oldRef = ref;
            ref = refracted(ConfigValues.GLASS_IOR + IORMod, ConfigValues.AIR_IOR + IORMod, ref, normal).normalize();
            if (Double.isNaN(ref.xCoord) || Double.isNaN(ref.yCoord) || Double.isNaN(ref.zCoord)) {
                ref = oldRef; // it'll bounce back on itself and cause a NaN vector, that means we should stop
                break;
            }
            showBeam(worldObj, hitPos, r.hitVec, color);
            hitPos = r.hitVec;
        }

        new Beam(worldObj, hitPos, ref, color).setEnableEffect(disableEffect).setIgnoreEntities(ignoreEntities).setUUID(uuid).enableParticleBeginning().spawn();
    }

    private Vec3d refracted(double from, double to, Vec3d vec, Vec3d normal) {
        double r = from / to, c = -normal.dotProduct(vec);
        return vec.scale(r).add(normal.scale((r * c) - Math.sqrt(1 - (r * r) * (1 - (c * c)))));
    }

    private void showBeam(World worldObj, Vec3d start, Vec3d end, Color color) {
        PacketHandler.NETWORK.sendToAllAround(new PacketLaserFX(start, end, color),
                new NetworkRegistry.TargetPoint(worldObj.provider.getDimension(), start.xCoord, start.yCoord, start.zCoord, 256));
    }

    @Override
    public BlockPrism.RayTraceResultData<Vec3d> collisionRayTraceLaser(@NotNull IBlockState blockState, @NotNull World worldIn, @NotNull BlockPos pos, @NotNull Vec3d startRaw, @NotNull Vec3d endRaw) {

        EnumFacing facing = EnumFacing.UP;

        Matrix4 matrixA = new Matrix4();
        Matrix4 matrixB = new Matrix4();
        switch (facing) {
            case UP:
            case DOWN:
            case EAST:
                break;
            case NORTH:
                matrixA.rotate(Math.toRadians(270), new Vec3d(0, -1, 0));
                matrixB.rotate(Math.toRadians(270), new Vec3d(0, 1, 0));
                break;
            case SOUTH:
                matrixA.rotate(Math.toRadians(90), new Vec3d(0, -1, 0));
                matrixB.rotate(Math.toRadians(90), new Vec3d(0, 1, 0));
                break;
            case WEST:
                matrixA.rotate(Math.toRadians(180), new Vec3d(0, -1, 0));
                matrixB.rotate(Math.toRadians(180), new Vec3d(0, 1, 0));
                break;
        }

        Vec3d
                a = new Vec3d(0.001, 0.001, 0), // This needs to be offset
                b = new Vec3d(1, 0.001, 0.5),
                c = new Vec3d(0.001, 0.001, 1), // and this too. Just so that blue refracts in ALL cases
                A = a.addVector(0, 0.998, 0),
                B = b.addVector(0, 0.998, 0), // these y offsets are to fix translocation issues
                C = c.addVector(0, 0.998, 0);

        Tri[] tris = new Tri[]{
                new Tri(a, b, c),
                new Tri(A, C, B),

                new Tri(a, c, C),
                new Tri(a, C, A),

                new Tri(a, A, B),
                new Tri(a, B, b),

                new Tri(b, B, C),
                new Tri(b, C, c),
        };

        Vec3d start = matrixA.apply(startRaw.subtract(new Vec3d(pos)).subtract(0.5, 0.5, 0.5)).addVector(0.5, 0.5, 0.5);
        Vec3d end = matrixA.apply(endRaw.subtract(new Vec3d(pos)).subtract(0.5, 0.5, 0.5)).addVector(0.5, 0.5, 0.5);

        Tri hitTri = null;
        Vec3d hit = null;
        double shortestSq = Double.POSITIVE_INFINITY;

        for (Tri tri : tris) {
            Vec3d v = tri.trace(start, end);
            if (v != null) {
                double distSq = start.subtract(v).lengthSquared();
                if (distSq < shortestSq) {
                    hit = v;
                    shortestSq = distSq;
                    hitTri = tri;
                }
            }
        }

        if (hit == null)
            return null;

        return new BlockPrism.RayTraceResultData<Vec3d>(matrixB.apply(hit.subtract(0.5, 0.5, 0.5)).addVector(0.5, 0.5, 0.5).add(new Vec3d(pos)), EnumFacing.UP, pos).data(matrixB.apply(hitTri.normal()));
    }
}
