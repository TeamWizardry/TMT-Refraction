package com.teamwizardry.refraction.api.book;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author WireSegal
 *         Created at 4:45 PM on 11/21/16.
 */
public abstract class BaseTextHolder implements ITextHolder {
	@Nullable
	private TextStyle style;

	@NotNull
	@Override
	public ITextHolder setStyle(TextStyle style) {
		this.style = style;
		return this;
	}

	@Nonnull
	@Override
	public TextStyle getStyle() {
		if (style == null) style = new TextStyle();
		return style;
	}

	@NotNull
	public String getFormattedText() {
		return getStyle().getFormattingCode() +
				getUnformattedText() +
				TextFormatting.RESET;
	}

	@Override
	public String toString() {
		return getFormattedText();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onClick(@NotNull Gui gui, @NotNull ScaledResolution res, int x, int y) {
		// NO-OP
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderTooltip(@NotNull Gui gui, @NotNull ScaledResolution res, int x, int y) {
		List<String> tooltip = getTooltip();
		if (tooltip != null)
			GuiUtils.drawHoveringText(tooltip, x, y, res.getScaledWidth(), res.getScaledHeight(), -1, Minecraft.getMinecraft().fontRendererObj);
	}
}
