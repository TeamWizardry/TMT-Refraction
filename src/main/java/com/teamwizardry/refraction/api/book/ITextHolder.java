package com.teamwizardry.refraction.api.book;

import com.google.common.collect.Lists;
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

	@NotNull
	@SideOnly(Side.CLIENT)
	default List<String> getTooltip() {
		return Lists.newArrayList();
	}

	@SideOnly(Side.CLIENT)
	default void onClick() {
		// NO-OP
	}
}
