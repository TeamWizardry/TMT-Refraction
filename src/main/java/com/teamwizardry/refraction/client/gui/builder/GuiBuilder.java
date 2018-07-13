package com.teamwizardry.refraction.client.gui.builder;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.features.gui.GuiBase;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents;
import com.teamwizardry.librarianlib.features.gui.components.ComponentList;
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.features.gui.mixin.ButtonMixin;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.librarianlib.features.sprite.Texture;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.client.gui.LeftSidebar;
import com.teamwizardry.refraction.client.gui.RightSidebar;
import com.teamwizardry.refraction.client.gui.builder.regionoptions.OptionFill;
import com.teamwizardry.refraction.common.network.PacketBuilderGridSaver;
import com.teamwizardry.refraction.common.tile.TileBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

/**
 * Created by Demoniaque.
 */
public class GuiBuilder extends GuiBase {

	private static final Texture texScreen = new Texture(new ResourceLocation(Constants.MOD_ID, "textures/gui/builder/screen.png"));
	private static final Texture texBorder = new Texture(new ResourceLocation(Constants.MOD_ID, "textures/gui/builder/border.png"));
	private static final Texture texSpriteSheet = new Texture(new ResourceLocation(Constants.MOD_ID, "textures/gui/builder/builder_sheet.png"));
	public static final Sprite sprArrowUp = texSpriteSheet.getSprite("arrow_up", 16, 16);
	public static final Sprite sprArrowDown = texSpriteSheet.getSprite("arrow_down", 16, 16);
	public static final Sprite sprLayers = texSpriteSheet.getSprite("layers", 16, 16);
	private static final Sprite sprScreen = texScreen.getSprite("bg", 256, 256);
	private static final Sprite sprBorder = texBorder.getSprite("bg", 276, 276);
	private static final Sprite sprTileRightSelected = texSpriteSheet.getSprite("tile_right_selected", 16, 16);
	private static final Sprite sprTileLeftSelected = texSpriteSheet.getSprite("tile_left_selected", 16, 16);
	private static final Sprite sprTileNormal = texSpriteSheet.getSprite("tile_normal", 16, 16);
	private static final Sprite sprIconDirect = texSpriteSheet.getSprite("icon_direct", 16, 16);
	private static final Sprite sprIconRegionSelection = texSpriteSheet.getSprite("icon_region_selection", 16, 16);
	private static final Sprite sprTabMode = texSpriteSheet.getSprite("tab_mode", 16, 16);
	public TileType[][][] grid = new TileType[16][16][16];
	public int selectedLayer = 0;
	public Mode selectedMode = Mode.DIRECT;
	public BlockPos location;

	public GuiBuilder(BlockPos location) {
		super(sprBorder.getWidth() + LeftSidebar.leftExtended.getWidth() * 2, sprBorder.getHeight());

		TileBuilder builder = (TileBuilder) Minecraft.getMinecraft().world.getTileEntity(location);
		if (builder != null && builder.grid != null) {
			grid = builder.grid;

			for (int x = 0; x < grid.length; x++)
				for (int y = 0; y < grid.length; y++)
					for (int z = 0; z < grid.length; z++) {
						if (grid[y][x][z] == null)
							grid[y][x][z] = GuiBuilder.TileType.EMPTY;
					}
		} else {

			for (int i = 0; i < grid.length; i++)
				for (int j = 0; j < grid.length; j++)
					for (int k = 0; k < grid.length; k++)
						grid[i][j][k] = TileType.EMPTY;
		}
		// LEFT //
		this.location = location;
		//TODO CHECK IF THIS WORKS
		ComponentList leftSidebar = new ComponentList(LeftSidebar.leftExtended.getWidth(), 0, LeftSidebar.leftExtended.getHeight());

		LeftSidebar modeComp = new LeftSidebar(leftSidebar, "Selection Modes", sprTabMode, true, true);
		modeComp.listComp.getPos().add(5,0);//.setMarginLeft(5);
		modeComp.listComp.add(new ModeSelector(modeComp.listComp, this, Mode.DIRECT, "Set Tiles Directly", sprIconDirect, true).component);

		//TODO CHECK IF THIS WORKS
		ComponentList selectRegionOptions = new ComponentList(0, 0, this.height);
		selectRegionOptions.add(new OptionFill(this, selectRegionOptions, "Fill", sprIconDirect, TileType.PLACED).component);
		selectRegionOptions.add(new OptionFill(this, selectRegionOptions, "Clear", sprIconDirect, TileType.EMPTY).component);

		ModeSelector selectRegionComp = new ModeSelector(modeComp.listComp, this, Mode.SELECT, "Select Regions", sprIconRegionSelection, false);
		selectRegionComp.listComp.add(selectRegionOptions);
		modeComp.listComp.add(selectRegionComp.component);
		leftSidebar.add(modeComp.component);

		getMainComponents().add(leftSidebar);
		// LEFT //

		// RIGHT //
		//TODO CHECK IF THIS WORKS
		ComponentList rightSidebar = new ComponentList(sprBorder.getWidth() * 2 - 11, 0, sprBorder.getHeight());

		RightSidebar layers = new RightSidebar(rightSidebar, "Layers", sprLayers, false, true);
		layers.listComp.getPos().add(-5,0);
		//layers.listComp.setMarginLeft(-5);

		layers.component.BUS.hook(GuiComponentEvents.ComponentTickEvent.class, componentTickEvent -> {
			layers.title = "Layers - Current: " + selectedLayer;
		});

		LayerSelector layerUp = new LayerSelector(this, rightSidebar, "Layer Up", sprArrowUp, true);
		LayerSelector layerDown = new LayerSelector(this, rightSidebar, "Layer Down", sprArrowDown, false);

		rightSidebar.add(layers.component, layerUp.component, layerDown.component);
		getMainComponents().add(rightSidebar);
		// RIGHT //

		// BORDER //
		ComponentSprite compBorder = new ComponentSprite(sprBorder,
				(getGuiWidth() / 2) - (sprBorder.getWidth() / 2),
				(getGuiHeight() / 2) - (sprBorder.getHeight() / 2));
		getMainComponents().add(compBorder);
		// BORDER //

		// SCREEN //
		ComponentSprite compScreen = new ComponentSprite(sprScreen,
				(getGuiWidth() / 2) - (sprScreen.getWidth() / 2),
				(getGuiHeight() / 2) - (sprScreen.getHeight() / 2));

		new ButtonMixin(compScreen, () -> {
		});

		compScreen.BUS.hook(GuiComponentEvents.MouseDragEvent.class, (event) -> {
			Vec2d pos = event.getMousePos();
			int x = pos.getXi() / 16;
			int y = pos.getYi() / 16;
			if (x < grid.length && y < grid.length && x > 0 && y > 0)
				if (selectedMode == Mode.DIRECT) {
					if (event.getButton() == EnumMouseButton.LEFT)
						grid[selectedLayer][x][y] = TileType.PLACED;
					else if (event.getButton() == EnumMouseButton.RIGHT)
						grid[selectedLayer][x][y] = TileType.EMPTY;
					PacketHandler.NETWORK.sendToServer(new PacketBuilderGridSaver(location, grid));
				}
		});

		compScreen.BUS.hook(GuiComponentEvents.MouseDownEvent.class, (event) -> {
			Vec2d pos = event.getMousePos();
			int x = pos.getXi() / 16;
			int y = pos.getYi() / 16;
			if (x < grid.length && y < grid.length && x > 0 && y > 0)
				if (selectedMode == Mode.DIRECT) {
					if (grid[selectedLayer][x][y] == TileType.EMPTY)
						grid[selectedLayer][x][y] = TileType.PLACED;
					else grid[selectedLayer][x][y] = TileType.EMPTY;
					PacketHandler.NETWORK.sendToServer(new PacketBuilderGridSaver(location, grid));
				} else if (selectedMode == Mode.SELECT) {
					if (grid[selectedLayer][x][y] == TileType.EMPTY)
						if (event.getButton() == EnumMouseButton.LEFT) {
							Vec2d left = getTile(TileType.LEFT_SELECTED);
							if (left != null) grid[selectedLayer][left.getXi()][left.getYi()] = TileType.EMPTY;
							grid[selectedLayer][x][y] = TileType.LEFT_SELECTED;
							PacketHandler.NETWORK.sendToServer(new PacketBuilderGridSaver(location, grid));
						} else {
							Vec2d left = getTile(TileType.RIGHT_SELECTED);
							if (left != null) grid[selectedLayer][left.getXi()][left.getYi()] = TileType.EMPTY;
							grid[selectedLayer][x][y] = TileType.RIGHT_SELECTED;
							PacketHandler.NETWORK.sendToServer(new PacketBuilderGridSaver(location, grid));
						}
					else {
						grid[selectedLayer][x][y] = TileType.EMPTY;
						PacketHandler.NETWORK.sendToServer(new PacketBuilderGridSaver(location, grid));
					}
				}
		});

		compScreen.BUS.hook(GuiComponentEvents.PostDrawEvent.class, (event) -> {
			for (int i = 0; i < grid.length; i++)
				for (int j = 0; j < grid.length; j++) {
					TileType box = grid[selectedLayer][i][j];

					GlStateManager.pushMatrix();
					GlStateManager.enableAlpha();
					GlStateManager.enableBlend();
					GlStateManager.enableTexture2D();
					GlStateManager.disableLighting();

					texSpriteSheet.bind();
					if (box == TileType.PLACED) {
						sprTileNormal.draw((int) ClientTickHandler.getPartialTicks(),
								event.component.getPos().getXi() + i * 16,
								event.component.getPos().getYi() + j * 16);
					} else if (box == TileType.LEFT_SELECTED) {
						sprTileLeftSelected.draw((int) ClientTickHandler.getPartialTicks(),
								event.component.getPos().getXi() + i * 16,
								event.component.getPos().getYi() + j * 16);
					} else if (box == TileType.RIGHT_SELECTED) {
						sprTileRightSelected.draw((int) ClientTickHandler.getPartialTicks(),
								event.component.getPos().getXi() + i * 16,
								event.component.getPos().getYi() + j * 16);
					}

					GlStateManager.enableLighting();
					GlStateManager.disableTexture2D();
					GlStateManager.popMatrix();
				}

			for (int i = 0; i < grid.length; i++)
				for (int j = 0; j < grid.length; j++) {
					if (selectedLayer - 1 >= 0) {
						TileType box = grid[selectedLayer - 1][i][j];

						GlStateManager.pushMatrix();
						GlStateManager.enableAlpha();
						GlStateManager.enableBlend();
						GlStateManager.enableTexture2D();
						GlStateManager.disableLighting();

						GlStateManager.color(1, 1, 1, 0.3f);

						texSpriteSheet.bind();
						if (box == TileType.PLACED) {
							sprTileNormal.draw((int) ClientTickHandler.getPartialTicks(),
									event.component.getPos().getXi() + i * 16,
									event.component.getPos().getYi() + j * 16);
						} else if (box == TileType.LEFT_SELECTED) {
							sprTileLeftSelected.draw((int) ClientTickHandler.getPartialTicks(),
									event.component.getPos().getXi() + i * 16,
									event.component.getPos().getYi() + j * 16);
						} else if (box == TileType.RIGHT_SELECTED) {
							sprTileRightSelected.draw((int) ClientTickHandler.getPartialTicks(),
									event.component.getPos().getXi() + i * 16,
									event.component.getPos().getYi() + j * 16);
						}

						GlStateManager.enableLighting();
						GlStateManager.disableTexture2D();
						GlStateManager.popMatrix();
					}
				}
		});
		getMainComponents().add(compScreen);
		// SCREEN //
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	public boolean hasTile(TileType type) {
		for (TileType[] x : grid[selectedLayer])
			for (TileType tileType : x)
				if (tileType == type) return true;
		return false;
	}

	@Nullable
	public Vec2d getTile(TileType type) {
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid.length; j++)
				if (grid[selectedLayer][i][j] == type) return new Vec2d(i, j);
		return null;
	}

	public enum TileType {
		EMPTY, LEFT_SELECTED, RIGHT_SELECTED, PLACED
	}

	public enum Mode {
		DIRECT, SELECT
	}
}
