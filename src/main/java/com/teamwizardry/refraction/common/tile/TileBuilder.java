package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.saving.SaveMethodGetter;
import com.teamwizardry.librarianlib.features.saving.SaveMethodSetter;
import com.teamwizardry.refraction.api.MultipleBeamTile;
import com.teamwizardry.refraction.api.Utils;
import com.teamwizardry.refraction.client.gui.builder.GuiBuilder;
import com.teamwizardry.refraction.common.block.BlockBuilder;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import java.awt.*;

/**
 * Created by Demoniaque.
 */
//@TileRegister("builder") unused
public class TileBuilder extends MultipleBeamTile {

	public GuiBuilder.TileType[][][] grid;

	@SaveMethodGetter(saveName = "grid_saver")
	public NBTTagCompound getter() {
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		if (grid != null) {
			for (int i = 0; i < grid.length; i++)
				for (int j = 0; j < grid.length; j++)
					for (int k = 0; k < grid.length; k++) {
						GuiBuilder.TileType box = grid[i][j][k];
						if (box == GuiBuilder.TileType.EMPTY || box == null) continue;
						NBTTagCompound compound = new NBTTagCompound();
						compound.setString("type", box.toString());
						compound.setInteger("layer", i);
						compound.setInteger("x", j);
						compound.setInteger("y", k);
						list.appendTag(compound);
					}
		}
		nbt.setTag("list", list);
		return nbt;
	}

	@SaveMethodSetter(saveName = "grid_saver")
	public void setter(NBTTagCompound nbt) {
		if (grid == null) grid = new GuiBuilder.TileType[16][16][16];

		NBTTagList list = nbt.getTagList("list", Constants.NBT.TAG_COMPOUND);
		for (int q = 0; q < list.tagCount(); q++) {
			NBTTagCompound compound = list.getCompoundTagAt(q);
			GuiBuilder.TileType type = GuiBuilder.TileType.valueOf(compound.getString("type"));
			int layer = compound.getInteger("layer");
			int x = compound.getInteger("x");
			int y = compound.getInteger("y");
			grid[layer][x][y] = type;
		}
	}

	@Override
	public void update() {
		super.update();

		if (world.isRemote) return;
		if (grid == null) return;

		if (outputBeam != null && Utils.doColorsMatchNoAlpha(outputBeam.getColor(), Color.GREEN)) {
			for (int i = 0; i < grid.length; i++)
				for (int j = 0; j < grid.length; j++)
					for (int k = 0; k < grid.length; k++) {
						GuiBuilder.TileType box = grid[j][i][k];
						if (box != GuiBuilder.TileType.EMPTY && box != null) {
							BlockPos loc = pos.offset(world.getBlockState(pos).getValue(BlockBuilder.FACING).getOpposite(), grid.length);
							loc = loc.add(i, j, k);

							//if (world.isAirBlock(loc)) {
							world.setBlockState(loc, Blocks.QUARTZ_BLOCK.getDefaultState());
							break;
							//}
						}
					}

		}
	}
}
