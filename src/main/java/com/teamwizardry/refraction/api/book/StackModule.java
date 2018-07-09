package com.teamwizardry.refraction.api.book;

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LordSaad
 */
public class StackModule implements IParsedModule {

	@Nullable
	public final ItemStack stack;
	public int x;
	public int y;
	@Nonnull
	public ComponentText component;

	public StackModule(TextAdapter textAdapter, @Nullable ItemStack stack, int x, int y) {
		fr.setBidiFlag(true);
		fr.setUnicodeFlag(true);
		this.stack = stack;
		this.x = x;
		this.y = y;

		if (x + fr.getStringWidth(stack == null ? "<NULL>" : stack.getDisplayName()) > TextAdapter.wrapLength) {
			this.x = 0;
			this.y += fr.FONT_HEIGHT;
		}

		component = new ComponentText(this.x, this.y, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
		component.getUnicode().setValue(true);
		if (stack != null) {
			String displayName = " [" + TextFormatting.UNDERLINE + "" + TextFormatting.BLUE + stack.getDisplayName() + TextFormatting.RESET + "]";
			component.getText().setValue(displayName);
			this.x += fr.getStringWidth(displayName);

			List<String> tooltip = stack.getTooltip(Minecraft.getMinecraft().player, ITooltipFlag.TooltipFlags.NORMAL);
			component.BUS.hook(GuiComponentEvents.PostDrawEvent.class, postDrawEvent -> {
				if (component.getMouseOver())
					component.render.setTooltip(tooltip);
			});
		} else {
			component.getText().setValue("<NULL>");
			this.x += fr.getStringWidth("<NULL>");
		}
		component.setSize(new Vec2d(fr.getStringWidth(component.getText().getValue(component)), fr.FONT_HEIGHT));

		fr.setBidiFlag(false);
		fr.setUnicodeFlag(false);
	}

	@Nonnull
	@Override
	public ComponentText getComponent() {
		return component;
	}
}
