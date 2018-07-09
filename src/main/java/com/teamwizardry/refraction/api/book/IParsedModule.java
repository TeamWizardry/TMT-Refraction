package com.teamwizardry.refraction.api.book;

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import javax.annotation.Nonnull;

/**
 * @author LordSaad
 */
public interface IParsedModule {

	FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

	@SuppressWarnings("rawtypes")
	@Nonnull
	GuiComponent getComponent();
}
