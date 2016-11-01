package com.teamwizardry.refraction.api;

import com.teamwizardry.refraction.common.light.Beam;

/**
 * 
 * @author Escapee
 * 
 * Attach this to TileEntities whos blocks should attach to adjacent Fiber Optic Cables. Implement {@link IOpticConnectable} on the corresponding block.
 *
 */
public interface ICableHandler
{
	void handle(Beam beam);	
}
