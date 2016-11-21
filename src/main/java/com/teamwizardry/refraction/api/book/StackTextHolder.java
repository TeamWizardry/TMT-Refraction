package com.teamwizardry.refraction.api.book;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author WireSegal
 *         Created at 4:45 PM on 11/21/16.
 */
public final class StackTextHolder extends BaseTextHolder {
	@Nullable
	private final ItemStack stack;

	@NotNull
	private final ITextHolder core;

	public StackTextHolder(@Nullable ItemStack stack) {
		this.stack = stack;
		if (stack == null)
			core = new StringTextHolder("NULL");
		else {
			StringTextHolder name = new StringTextHolder(stack.getDisplayName());
			if (stack.hasDisplayName())
				name.getStyle().setItalic(true);

			core = new CombinedTextHolder(new StringTextHolder("["), name, new StringTextHolder("]"));
		}
	}

	@NotNull
	@Override
	public String getUnformattedText() {
		return core.getUnformattedText();
	}

	@NotNull
	@Override
	public String getFormattedText() {
		return core.getFormattedText();
	}

	@NotNull
	@Override
	@SideOnly(Side.CLIENT)
	public List<String> getTooltip() {
		if (stack == null) return super.getTooltip();
		return stack.getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
	}
}
