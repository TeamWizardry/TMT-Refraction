package com.teamwizardry.refraction.init.recipies;

import com.teamwizardry.refraction.api.recipe.ColorConsumingBehavior;
import com.teamwizardry.refraction.init.ModBlocks;
import com.teamwizardry.refraction.init.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.awt.*;

import static com.teamwizardry.refraction.api.lib.LibOreDict.*;
import static com.teamwizardry.refraction.api.recipe.AssemblyBehaviors.register;

/**
 * Created by LordSaad44
 */
public class ModAssemblyRecipes {

	public static void init() {

		// T0
		register("mirror", new ItemStack(ModBlocks.MIRROR),
				new Color(255, 255, 255, 1), new Color(255, 255, 255, 64),
				Blocks.GLASS_PANE, Blocks.GLASS_PANE, Blocks.GLASS_PANE,
				REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY);

		// T1
		register("prism", new ItemStack(ModBlocks.PRISM),
				new Color(255, 255, 255, 17), new Color(255, 255, 255, 64),
				LENS, LENS, LENS, LENS, LENS, LENS);

		// T2
		register("reflection_chamber", new ItemStack(ModBlocks.REF_CHAMBER),
				new Color(255, 96, 0, 32), new Color(255, 160, 0, 64),
				LENS, LENS, LENS, LENS, LENS, LENS,
				REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY,
				"ingotIron", "ingotIron", "ingotIron");
		register("splitter", new ItemStack(ModBlocks.SPLITTER),
				new Color(0, 255, 0, 16), new Color(0, 255, 0, 32),
				LENS, LENS, LENS,
				REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY);
		register("sensor", new ItemStack(ModBlocks.SENSOR),
				new Color(255, 96, 0, 32), new Color(255, 160, 0, 64),
				LENS, LENS, LENS, LENS, LENS, LENS,
				"dustRedstone", "dustRedstone", "dustRedstone",
				"gemDiamond", "gemDiamond");

		// T3
		register("electric_laser", new ItemStack(ModBlocks.ELECTRIC_LASER),
				new Color(0, 96, 255, 54), new Color(0, 160, 255, 128),
				OPTIC_FIBER, OPTIC_FIBER, OPTIC_FIBER, OPTIC_FIBER,
				"blockIron",
				"dustRedstone", "dustRedstone", "dustRedstone",
				OreDictionary.doesOreNameExist("ingotSilver") ? "ingotSilver" : "gemLapis",
				OreDictionary.doesOreNameExist("ingotPlatinum") ? "ingotPlatinum" : "gemLapis",
				REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY,
				LENS, LENS, LENS);
		register("laser", new ItemStack(ModBlocks.LASER),
				new Color(255, 64, 64, 81), new Color(255, 106, 106, 192),
				OPTIC_FIBER, OPTIC_FIBER, OPTIC_FIBER, OPTIC_FIBER,
				"blockIron",
				"dustRedstone", "dustRedstone", "dustRedstone",
				"dustGlowstone",
				REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY,
				LENS, LENS, LENS);
		register("disco", new ItemStack(ModBlocks.DISCO_BALL),
				new Color(255, 0, 255, 54), new Color(255, 0, 255, 128),
				"gemDiamond", "gemDiamond", "gemDiamond", "gemDiamond",
				REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY);
		register("optic_fiber", new ItemStack(ModBlocks.OPTIC_FIBER, 4),
				new Color(96, 0, 255, 54), new Color(160, 0, 255, 128),
				LENS, LENS, LENS,
				REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY,
				"gemDiamond", "gemDiamond");

		// T4
		register("translocator", new ItemStack(ModBlocks.TRANSLOCATOR, 4),
				new Color(255, 0, 96, 128), new Color(255, 0, 160, 255),
				OPTIC_FIBER, OPTIC_FIBER, OPTIC_FIBER, OPTIC_FIBER,
				"enderpearl",
				"ingotIron");
		register("electron_exciter", new ItemStack(ModBlocks.ELECTRON_EXCITER),
				new Color(0, 255, 96, 128), new Color(0, 255, 160, 255),
				LENS, LENS, LENS, LENS, LENS, LENS,
				REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY,
				new ItemStack(ModBlocks.SPLITTER),
				OPTIC_FIBER, OPTIC_FIBER, OPTIC_FIBER, OPTIC_FIBER);


		// T5
		register("axyz", new ItemStack(ModBlocks.AXYZ),
				new Color(96, 1, 255, 225), new Color(160, 64, 255, 255),
				TRANSLOCATOR, TRANSLOCATOR, TRANSLOCATOR, TRANSLOCATOR, TRANSLOCATOR, TRANSLOCATOR,
				Blocks.CHORUS_FLOWER,
				Blocks.PISTON, Blocks.PISTON);

		// Other
		register("ammo", new ColorConsumingBehavior(new ItemStack(ModItems.LIGHT_CARTRIDGE), new ItemStack(ModItems.LIGHT_CARTRIDGE)));
		register("grenade", new ColorConsumingBehavior(new ItemStack(ModItems.GRENADE), new ItemStack(ModItems.GRENADE)));
	}
}
