package com.teamwizardry.refraction.common.block;

import com.google.common.collect.Lists;
import com.teamwizardry.librarianlib.client.util.TooltipHelper;
import com.teamwizardry.librarianlib.common.base.block.BlockMod;
import com.teamwizardry.librarianlib.common.base.block.ItemModBlock;
import com.teamwizardry.librarianlib.common.network.PacketHandler;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.api.IBeamHandler;
import com.teamwizardry.refraction.api.IOpticConnectable;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.network.PacketAXYZMarks;
import com.teamwizardry.refraction.init.ModBlocks;
import gnu.trove.map.hash.TObjectIntHashMap;
import kotlin.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonMoving;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author WireSegal
 *         Created at 9:34 PM on 11/12/16.
 */
@SuppressWarnings("StatementWithEmptyBody")
public class BlockAXYZ extends BlockMod implements IBeamHandler, IOpticConnectable {

    public static final PropertyBool[] PROPS = new PropertyBool[] {
            PropertyBool.create("down"),
            PropertyBool.create("up"),
            PropertyBool.create("north"),
            PropertyBool.create("south"),
            PropertyBool.create("west"),
            PropertyBool.create("east")
    };

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        for (int i = 0; i < PROPS.length; i++) {
            EnumFacing facing = EnumFacing.VALUES[i];
            IBlockState fiber = worldIn.getBlockState(pos.offset(facing));
            state = state.withProperty(PROPS[i], fiber.getBlock() instanceof BlockOpticFiber &&
                    fiber.getValue(BlockOpticFiber.FACING).contains(facing.getOpposite()));
        }
        return state;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, PROPS);
    }

    // Bindings
    public final Map<DimWithPos, DimWithPos> mappedPositions = new HashMap<>();

    private final Set<DimWithPos> removeQueue = new HashSet<>();
    private final Set<DimWithPos> checkedCoords = new HashSet<>();
    private final TObjectIntHashMap<DimWithPos> coordsToCheck = new TObjectIntHashMap<>(10, 0.5F, -1);

    private final AxisAlignedBB AABB = new AxisAlignedBB(3 / 16.0, 3 / 16.0, 3 / 16.0, 13 / 16.0, 13 / 16.0, 13 / 16.0);

    public BlockAXYZ() {
        super("axyz", Material.GOURD);
        setHardness(2F);
        setResistance(10F);
        setSoundType(SoundType.METAL);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        TooltipHelper.addToTooltip(tooltip, "simple_name.refraction:" + getRegistryName().getResourcePath());
    }

    private boolean harvesting = false;

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (player.isCreative()) return;
        harvesting = true;
        dropBlockAsItem(worldIn, pos, state, 0);
        harvesting = false;
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, @Nonnull Random random) {
        return harvesting ? 1 : 0;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullyOpaque(IBlockState state) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB;
    }

    @Override
    public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        if(!world.isRemote)
            mapCoords(world.provider.getDimension(), pos, 2);
    }

    private void mapCoords(int world, BlockPos pos, int time) {
        coordsToCheck.put(new DimWithPos(world, pos), time);
    }

    private void decrCoords(DimWithPos key) {
        int time = getTimeInCoords(key);

        if(time <= 0)
            removeQueue.add(key);
        else coordsToCheck.adjustValue(key, -1);
    }

    private int getTimeInCoords(DimWithPos key) {
        return coordsToCheck.get(key);
    }

    private Block getBlockAt(DimWithPos key) {
        return getStateAt(key).getBlock();
    }

    private IBlockState getStateAt(DimWithPos key) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if(server == null)
            return Blocks.AIR.getDefaultState();
        return server.worldServerForDimension(key.dim).getBlockState(key.blockPos);
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if(!event.getWorld().isRemote)
            WorldData.get(event.getWorld());
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if(!event.getWorld().isRemote)
            WorldData.get(event.getWorld()).markDirty();
    }

    public static class WorldData extends WorldSavedData {

        private static final String ID = Refraction.MOD_ID + "-LaserRelayPairs";

        public WorldData(String id) {
            super(id);
        }

        @Override
        public void readFromNBT(@Nonnull NBTTagCompound nbttagcompound) {
            ModBlocks.AXYZ.mappedPositions.clear();

            Collection<String> tags = nbttagcompound.getKeySet();
            for(String key : tags) {
                NBTBase tag = nbttagcompound.getTag(key);
                if(tag instanceof NBTTagString) {
                    String value = ((NBTTagString) tag).getString();

                    ModBlocks.AXYZ.mappedPositions.put(DimWithPos.fromString(key), DimWithPos.fromString(value));
                }
            }
        }

        @Nonnull
        @Override
        public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbttagcompound) {
            for(DimWithPos s : ModBlocks.AXYZ.mappedPositions.keySet())
                nbttagcompound.setString(s.toString(), ModBlocks.AXYZ.mappedPositions.get(s).toString());
            return nbttagcompound;
        }

        public static WorldData get(World world) {
            if(world.getMapStorage() == null)
                return null;

            WorldData data = (WorldData) world.getMapStorage().getOrLoadData(WorldData.class, ID);

            if (data == null) {
                data = new WorldData(ID);
                data.markDirty();
                world.getMapStorage().setData(ID, data);
            }
            return data;
        }
    }

    @SubscribeEvent
    public void tickEnd(TickEvent event) {
        if(event.type == TickEvent.Type.SERVER && event.phase == TickEvent.Phase.END) {
            for(DimWithPos s : coordsToCheck.keySet()) {
                decrCoords(s);
                if(checkedCoords.contains(s))
                    continue;

                Block block = getBlockAt(s);
                if(block == Blocks.PISTON_EXTENSION) {
                    IBlockState state = getStateAt(s);
                    EnumFacing dir = state.getValue(BlockPistonMoving.FACING);

                    MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

                    if(server != null && getTimeInCoords(s) == 0) {
                        DimWithPos newPos;

                        int worldId = s.dim, x = s.blockPos.getX(), y = s.blockPos.getY(), z = s.blockPos.getZ();
                        BlockPos pos = s.blockPos;
                        World world = server.worldServerForDimension(worldId);
                        if(world.isAirBlock(pos.offset(dir)))
                            world.setBlockState(pos.offset(dir), ModBlocks.AXYZ.getDefaultState());
                        else if(!world.isRemote) {
                            ItemStack stack = new ItemStack(ModBlocks.AXYZ);
                            world.spawnEntityInWorld(new EntityItem(world, x + dir.getFrontOffsetX(), y + dir.getFrontOffsetY(), z + dir.getFrontOffsetZ(), stack));
                        }
                        checkedCoords.add(s);
                        newPos = new DimWithPos(world.provider.getDimension(), pos.offset(dir));

                        if(!mappedPositions.containsKey(s))
                            mappedPositions.put(s, new DimWithPos(s.dim, s.blockPos.offset(dir, 3)));

                        DimWithPos dPos = mappedPositions.get(s);
                        worldId = dPos.dim;
                        BlockPos pos2 = dPos.blockPos;
                        world = server.worldServerForDimension(worldId);


                        mappedPositions.remove(s);
                        mappedPositions.put(newPos, new DimWithPos(world.provider.getDimension(), pos2.offset(dir)));
                        save(world);
                    }
                } else removeQueue.add(s);
            }

            for(DimWithPos s : removeQueue) {
                coordsToCheck.remove(s);
                checkedCoords.remove(s);
                mappedPositions.remove(s);
            }
            removeQueue.clear();

            HashMap<Integer, List<Pair<BlockPos, BlockPos>>> map = new HashMap<>();
            for (Map.Entry<DimWithPos, DimWithPos> s : mappedPositions.entrySet()) {
                int dim = s.getKey().dim;
                if (!map.containsKey(dim)) map.put(dim, Lists.newArrayList());

                map.get(dim).add(new Pair<>(s.getValue().blockPos, s.getKey().blockPos));
            }

            for (Integer dim : map.keySet()) {
                List<Pair<BlockPos, BlockPos>> l = map.get(dim);
                BlockPos[] arr1 = new BlockPos[l.size()];
                BlockPos[] arr2 = new BlockPos[l.size()];
                for (int i = 0; i < arr1.length; i++) {
                    arr1[i] = l.get(i).getFirst();
                    arr2[i] = l.get(i).getSecond();
                }

                PacketHandler.NETWORK.sendToAll(new PacketAXYZMarks(arr1, arr2, dim));
            }
        }
    }

    public void save(World world) {
        WorldData data = WorldData.get(world);
        if(data != null)
            data.markDirty();
    }

    public static class DimWithPos {
        public final int dim;
        public final BlockPos blockPos;

        public DimWithPos(int dim, BlockPos pos) {
            this.dim = dim;
            blockPos = pos;
        }

        @Override
        public int hashCode() {
            return 31 * dim ^ blockPos.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof DimWithPos
                    && dim == ((DimWithPos) o).dim
                    && blockPos.equals(((DimWithPos) o).blockPos);
        }

        @Override
        public String toString() {
            return dim + ":" + blockPos.getX() + ":" + blockPos.getY() + ":" + blockPos.getZ();
        }

        public static DimWithPos fromString(String s) {
            String[] split = s.split(":");
            return new DimWithPos(Integer.parseInt(split[0]), new BlockPos(Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3])));
        }

    }

    @Override
    public void handleBeams(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Beam... beams) {
        for (Beam beam : beams)
            handleFiberBeam(world, pos, beam);
    }

    @Nonnull
    @Override
    public List<EnumFacing> getAvailableFacings(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos, @Nonnull EnumFacing facing) {
        return Lists.newArrayList(EnumFacing.VALUES);
    }

    @Override
    public void handleFiberBeam(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Beam beam) {
        int dim = world.provider.getDimension();
        Color c = new Color(beam.color.getRed(), beam.color.getGreen(), beam.color.getBlue(), (int) (beam.color.getAlpha() / 1.05));
        DimWithPos key = new DimWithPos(dim, pos);
        if (mappedPositions.containsKey(key)) {
            BlockPos mapPos = mappedPositions.get(key).blockPos;
            beam.createSimilarBeam(new Vec3d(mapPos).addVector(0.5, 0.5, 0.5), beam.slope).setColor(c).spawn();
        }
    }

    @Nullable
    @Override
    public ItemBlock createItemForm() {

        return new ItemModBlock(this) {

            private int RAND_NAMES = 0;

            @NotNull
            @Override
            public String getUnlocalizedName(ItemStack par1ItemStack) {
                if (par1ItemStack.getItemDamage() == 0 && FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
                    if (RAND_NAMES == 0)
                        for (RAND_NAMES = 0; I18n.hasKey(super.getUnlocalizedName(par1ItemStack) + "." + RAND_NAMES + 1 + ".name"); RAND_NAMES++);

                    StackTraceElement stackTrace[] = (new Throwable()).getStackTrace();
                    if ("net.minecraft.item.Item".equals(stackTrace[1].getClassName())) {
                        long curTime = System.currentTimeMillis();
                        if (curTime - prevTime > 1000L || curRand == -1)
                            curRand = rand.nextInt(RAND_NAMES);
                        prevTime = curTime;
                        return super.getUnlocalizedName(par1ItemStack) + "." + curRand;
                    }
                }
                return super.getUnlocalizedName(par1ItemStack);
            }

            private Random rand = new Random();
            private long prevTime = 0x80000000L;
            private int curRand = -1;
        };
    }
}
