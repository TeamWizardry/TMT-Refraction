package com.teamwizardry.refraction.client.gui;

import com.teamwizardry.librarianlib.client.core.ClientTickHandler;
import com.teamwizardry.librarianlib.client.gui.GuiBase;
import com.teamwizardry.librarianlib.client.gui.GuiComponent;
import com.teamwizardry.librarianlib.client.gui.components.ComponentList;
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
    private static final Texture texSpriteSheet = new Texture(new ResourceLocation(Constants.MOD_ID, "textures/gui/builder/builder_sheet.png"));
    private static final Sprite sprScreen = texScreen.getSprite("bg", 256, 256);
    private static final Sprite sprBorder = texBorder.getSprite("bg", 276, 276);
    private static final Sprite sprTileRightSelected = texSpriteSheet.getSprite("tile_right_selected", 16, 16);
    private static final Sprite sprTileLeftSelected = texSpriteSheet.getSprite("tile_left_selected", 16, 16);
    private static final Sprite sprTileNormal = texSpriteSheet.getSprite("tile_normal", 16, 16);
    private static final Sprite sprIconDirect = texSpriteSheet.getSprite("icon_direct", 16, 16);
    private static final Sprite sprIconRegionSelection = texSpriteSheet.getSprite("icon_region_selection", 16, 16);
    private static final Sprite sprTabMode = texSpriteSheet.getSprite("tab_mode", 16, 16);

    public TileType[][] grid = new TileType[16][16];
    public Mode selectedMode;

    public GuiBuilder() {
        super(0, 0);

        for (int i = 0; i < grid.length; i++)
            for (int j = 0; j < grid.length; j++) {
                grid[i][j] = TileType.NORMAL;
            }

        ComponentList leftBar = new ComponentList(-LeftSidebar.sliderExtendedSprite.getWidth(), -sprBorder.getHeight() / 2);

        LeftSidebar modeComp = new LeftSidebar(leftBar, "Selection Modes", sprTabMode, false);
        modeComp.listComp.setMarginLeft(5);
        modeComp.listComp.add(new ModeSelector(modeComp.listComp, this, Mode.DIRECT, "Set Tiles Directly", sprIconDirect, true).component);
        modeComp.listComp.add(new ModeSelector(modeComp.listComp, this, Mode.SELECT, "Select Regions", sprIconRegionSelection, false).component);
        leftBar.add(modeComp.component);

        getMainComponents().add(leftBar);

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
            Vec2d pos = event.getMousePos();
            int x = pos.getXi() / 16;
            int y = pos.getYi() / 16;
            if (x < grid.length && y < grid.length)
                if (grid[x][y] == TileType.NORMAL) {
                    grid[x][y] = TileType.PLACED;
                } else grid[x][y] = TileType.NORMAL;
        });

        compScreen.BUS.hook(GuiComponent.PostDrawEvent.class, (event) -> {
            for (int i = 0; i < grid.length; i++)
                for (int j = 0; j < grid.length; j++) {
                    TileType box = grid[i][j];

                    GlStateManager.pushMatrix();
                    GlStateManager.enableAlpha();
                    GlStateManager.enableBlend();
                    GlStateManager.enableTexture2D();
                    GlStateManager.disableLighting();

                    texSpriteSheet.bind();
                    if (box == TileType.PLACED) {
                        sprTileNormal.draw((int) ClientTickHandler.getPartialTicks(),
                                event.getComponent().getPos().getXi() + i * 16,
                                event.getComponent().getPos().getYi() + j * 16);
                    } else if (box == TileType.LEFT_SELECTED) {
                        sprTileLeftSelected.draw((int) ClientTickHandler.getPartialTicks(),
                                event.getComponent().getPos().getXi() + i * 16,
                                event.getComponent().getPos().getYi() + j * 16);
                    } else if (box == TileType.RIGHT_SELECTED) {
                        sprTileRightSelected.draw((int) ClientTickHandler.getPartialTicks(),
                                event.getComponent().getPos().getXi() + i * 16,
                                event.getComponent().getPos().getYi() + j * 16);
                    }

                    GlStateManager.enableLighting();
                    GlStateManager.disableTexture2D();
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

    private enum TileType {
        NORMAL, LEFT_SELECTED, RIGHT_SELECTED, PLACED
    }

    public enum Mode {
        DIRECT, SELECT
    }
}
