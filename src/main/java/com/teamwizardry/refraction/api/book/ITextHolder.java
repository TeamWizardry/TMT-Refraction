package com.teamwizardry.refraction.api.book;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author WireSegal
 *         Created at 3:51 PM on 11/21/16.
 */
public interface ITextHolder {

	@NotNull ITextHolder setStyle(@Nullable TextStyle style);

	@Nullable TextStyle getStyle();

	@NotNull String getUnformattedText();

	@NotNull String getFormattedText();

	@Nullable
	@SideOnly(Side.CLIENT)
	default List<String> getTooltip() {
		return null;
	}

	@SideOnly(Side.CLIENT)
	void onClick(@NotNull Gui gui, @NotNull ScaledResolution res, int x, int y);

	@SideOnly(Side.CLIENT)
	void renderTooltip(@NotNull Gui gui, @NotNull ScaledResolution res, int x, int y);
}
