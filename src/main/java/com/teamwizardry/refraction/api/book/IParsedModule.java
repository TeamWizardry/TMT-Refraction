package com.teamwizardry.refraction.api.book;

import com.teamwizardry.librarianlib.client.gui.GuiComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import javax.annotation.Nonnull;

/**
 * @author LordSaad
 */
public interface IParsedModule {

	FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;

	@SuppressWarnings("rawtypes")
	@Nonnull
	GuiComponent getComponent();
}
