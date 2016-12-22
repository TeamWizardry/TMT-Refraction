package com.teamwizardry.refraction.api.book;

import com.teamwizardry.librarianlib.client.gui.GuiComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.jetbrains.annotations.NotNull;

/**
 * @author LordSaad
 */
public interface IParsedModule {

    FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;

    @SuppressWarnings("rawtypes")
	@NotNull
    GuiComponent getComponent();
}
