package com.teamwizardry.refraction.client.gui;

import com.teamwizardry.librarianlib.client.core.ClientTickHandler;
import com.teamwizardry.librarianlib.client.gui.GuiBase;
import com.teamwizardry.librarianlib.client.gui.GuiComponent;
import com.teamwizardry.librarianlib.client.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.client.gui.mixin.ButtonMixin;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import com.teamwizardry.librarianlib.client.sprite.Texture;
import com.teamwizardry.librarianlib.common.util.math.Vec2d;
import com.teamwizardry.refraction.api.Constants;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

/**
 * Created by LordSaad.
 */
public class GuiBuilder extends GuiBase {

	private static final Texture texScreen = new Texture(new ResourceLocation(Constants.MOD_ID, "textures/gui/builder/screen.png"));
	private static final Texture texBorder = new Texture(new ResourceLocation(Constants.MOD_ID, "textures/gui/builder/border.png"));
	private static final Texture texTilePickedNormal = new Texture(new ResourceLocation(Constants.MOD_ID, "textures/gui/builder/tile.png"));
	private static final Texture texTilePickedSelected = new Texture(new ResourceLocation(Constants.MOD_ID, "textures/gui/builder/tile_selected.png"));
	private static final Texture texTileNormalSelected = new Texture(new ResourceLocation(Constants.MOD_ID, "textures/gui/builder/normal_selected.png"));
	private static final Sprite sprScreen = texScreen.getSprite("bg", 256, 256);
	private static final Sprite sprBorder = texBorder.getSprite("bg", 276, 276);
	private static final Sprite sprTilePickedNormal = texTilePickedNormal.getSprite("bg", 16, 16);
	private static final Sprite sprTilePickedSelected = texTilePickedSelected.getSprite("bg", 16, 16);
	private static final Sprite sprTileNormalSelected = texTileNormalSelected.getSprite("bg", 16, 16);

	public Type[][] grid = new Type[16][16];
	public Type[][] tempGrid = grid;
	public Type[][] prevGrid = new Type[16][16];

	private Vec2d prevPos;
	private boolean drag;

	public GuiBuilder() {
		super(0, 0);

		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid.length; j++) {
				grid[i][j] = Type.NORMAL;
			}

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

		new ButtonMixin<>(compScreen, () -> {
		});

		compScreen.BUS.hook(ButtonMixin.ButtonClickEvent.class, (event) -> {
			if (drag) {
				drag = false;
				event.cancel();
			}
			Vec2d pos = event.getMousePos();
			int x = pos.getXi() / 16;
			int y = pos.getYi() / 16;
			if (x < grid.length && y < grid.length)
				if (grid[x][y] == Type.NORMAL) grid[x][y] = Type.PICKED;
				else grid[x][y] = Type.NORMAL;
		});

		compScreen.BUS.hook(GuiComponent.MouseUpEvent.class, (event) -> {
			if (prevPos != null) {
				prevPos = null;
				event.cancel();
			}
		});

		compScreen.BUS.hook(GuiComponent.MouseDragEvent.class, (event) -> {
			Vec2d newPos = event.getMousePos();
			if (prevPos == null) {
				prevPos = newPos;
				prevGrid = grid;
				drag = true;
				tempGrid = grid;
			} else grid = prevGrid;

           /* if (prevPos.getXi() / 16 < grid.length
					&& prevPos.getYi() / 16 < grid.length
                    && newPos.getXi() / 16 < grid.length
                    && newPos.getYi() / 16 < grid.length) return;*/

			if (prevPos.getXi() / 16 != newPos.getXi() / 16 || prevPos.getYi() / 16 != newPos.getYi() / 16) {

				for (int i = (prevPos.getX() < newPos.getX() ? prevPos.getXi() / 16 : newPos.getXi()) / 16;
					 i < Math.abs((prevPos.getXi() / 16) - (newPos.getXi() / 16)); i++) {
					for (int j = (prevPos.getYi() < newPos.getYi() ? prevPos.getYi() / 16 : newPos.getYi()) / 16;
						 j < Math.abs((prevPos.getYi() / 16) - (newPos.getYi() / 16)); j++) {
						if (tempGrid[i][j] != prevGrid[i][j]) continue;
						if (tempGrid[i][j] == Type.NORMAL) tempGrid[i][j] = Type.NORMAL_SELECTED;
						else if (tempGrid[i][j] == Type.PICKED) tempGrid[i][j] = Type.PICKED_SELECTED;
						else if (tempGrid[i][j] == Type.NORMAL_SELECTED) tempGrid[i][j] = Type.NORMAL;
						else if (tempGrid[i][j] == Type.PICKED_SELECTED) tempGrid[i][j] = Type.PICKED;

						grid[i][j] = tempGrid[i][j];
					}
				}
			} else {
				int x = event.getMousePos().getXi() / 16;
				int y = event.getMousePos().getYi() / 16;
				if (x < grid.length && y < grid.length)
					if (grid[x][y] == Type.NORMAL) grid[x][y] = Type.PICKED;
					else grid[x][y] = Type.NORMAL;
			}
		});

		compScreen.BUS.hook(GuiComponent.PostDrawEvent.class, (event) -> {
			for (int i = 0; i < grid.length; i++)
				for (int j = 0; j < grid.length; j++) {
					Type box = grid[i][j];

					GlStateManager.pushMatrix();
					GlStateManager.enableAlpha();
					GlStateManager.enableBlend();
					GlStateManager.enableTexture2D();
					GlStateManager.disableLighting();

					if (box == Type.PICKED) {
						texTilePickedNormal.bind();
						sprTilePickedNormal.draw((int) ClientTickHandler.getPartialTicks(),
								event.getComponent().getPos().getXi() + i * 16,
								event.getComponent().getPos().getYi() + j * 16);
					} else if (box == Type.PICKED_SELECTED) {
						texTilePickedSelected.bind();
						sprTilePickedSelected.draw((int) ClientTickHandler.getPartialTicks(),
								event.getComponent().getPos().getXi() + i * 16,
								event.getComponent().getPos().getYi() + j * 16);
					} else if (box == Type.NORMAL_SELECTED) {
						texTileNormalSelected.bind();
						sprTileNormalSelected.draw((int) ClientTickHandler.getPartialTicks(),
								event.getComponent().getPos().getXi() + i * 16,
								event.getComponent().getPos().getYi() + j * 16);
					}

					GlStateManager.enableLighting();
					GlStateManager.disableTexture2D();
					GlStateManager.disableBlend();
					GlStateManager.disableAlpha();
					GlStateManager.popMatrix();
				}
		});

		getMainComponents().add(compScreen);
		// SCREEN //
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	private enum Type {
		NORMAL, NORMAL_SELECTED, PICKED, PICKED_SELECTED
	}
}
