package com.teamwizardry.refraction.api.internal;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author WireSegal
 *         Created at 10:17 PM on 12/20/16.
 */
@FunctionalInterface
public interface ClientRunnable {
	@SideOnly(Side.CLIENT)
	void run();
}
