package com.teamwizardry.refraction.api.book;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.jetbrains.annotations.NotNull;

/**
 * @author WireSegal
 *         Created at 4:45 PM on 11/21/16.
 */
public final class PlayerTextHolder extends BaseTextHolder {
	@NotNull
	@Override
	public String getUnformattedText() {
		return FMLCommonHandler.instance().getSide().isClient() ? getPlayerNameClient() : "PLAYERNAME";
	}

	private String getPlayerNameClient() {
		Minecraft minecraft = Minecraft.getMinecraft();
		EntityPlayer player = minecraft.thePlayer;
		return player.getName();
	}
}
