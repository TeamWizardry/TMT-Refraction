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
		register("disco", new ItemStack(ModBlocks.DISCO_BALL),
				new Color(0xA6FF00BE, true), new Color(0xCCDC00FF, true),
				"gemDiamond", "gemDiamond", "gemDiamond", "gemDiamond",
				REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY);

		register("mirror", new ItemStack(ModBlocks.MIRROR),
				new Color(0x01FFFFFF, true), new Color(0xB3FFFFFF, true),
				Blocks.GLASS_PANE, Blocks.GLASS_PANE, Blocks.GLASS_PANE,
				REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY);

		register("prism", new ItemStack(ModBlocks.PRISM),
				new Color(0x1AFFFFFF, true), new Color(0xB3FFFFFF, true),
				LENS, LENS, LENS, LENS, LENS, LENS);

		register("splitter", new ItemStack(ModBlocks.SPLITTER),
				new Color(0x4D00FF00, true), new Color(0x8800FF00, true),
				LENS, LENS, LENS,
				REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY);

		register("laser", new ItemStack(ModBlocks.LASER),
				new Color(0xB3FF3200, true), new Color(0xD9FF0032, true),
				OPTIC_FIBER, OPTIC_FIBER, OPTIC_FIBER, OPTIC_FIBER,
				"blockIron",
				"dustRedstone", "dustRedstone", "dustRedstone",
				"dustGlowstone",
				REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY,
				LENS, LENS, LENS);

		register("reflection_chamber", new ItemStack(ModBlocks.REF_CHAMBER),
				new Color(0x59FF6400, true), new Color(0xB3FF0096, true),
				LENS, LENS, LENS, LENS, LENS, LENS,
				REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY,
				"ingotIron", "ingotIron", "ingotIron");

		register("electron_exciter", new ItemStack(ModBlocks.ELECTRON_EXCITER),
				new Color(0xE600FFFF, true), new Color(0xFF00FF64, true),
				LENS, LENS, LENS, LENS, LENS, LENS,
				REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY,
				new ItemStack(ModBlocks.SPLITTER),
				OPTIC_FIBER, OPTIC_FIBER, OPTIC_FIBER, OPTIC_FIBER);

		register("optic_fiber", new ItemStack(ModBlocks.OPTIC_FIBER, 4),
				new Color(0xA60000FF, true), new Color(0xCCFF0080, true),
				LENS, LENS, LENS,
				REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY,
				"gemDiamond", "gemDiamond");

		register("spectrometer", new ItemStack(ModBlocks.SPECTROMETER),
				new Color(0x59FFFFFF, true), new Color(0xB3FFFFFF, true),
				LENS, LENS, LENS, LENS, LENS, LENS,
				REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY,
				"gemDiamond", "gemDiamond",
				"dyeBlue", "dyeBlue",
				"dyeLime", "dyeLime",
				"dyeRed", "dyeRed");

		register("sensor", new ItemStack(ModBlocks.SENSOR),
				new Color(0x40FF3200, true), new Color(0xB3FF0032, true),
				LENS, LENS, LENS, LENS, LENS, LENS,
				"dustRedstone", "dustRedstone", "dustRedstone",
				"gemDiamond", "gemDiamond");

		register("translocator", new ItemStack(ModBlocks.TRANSLOCATOR, 4),
				new Color(0xA60000FF, true), new Color(0xCCFF0080, true),
				OPTIC_FIBER, OPTIC_FIBER, OPTIC_FIBER, OPTIC_FIBER,
				"enderpearl",
				"ingotIron");

		register("axyz", new ItemStack(ModBlocks.AXYZ),
				new Color(0xC87000DF, true), new Color(0xFF9020FF, true),
				TRANSLOCATOR, TRANSLOCATOR, TRANSLOCATOR, TRANSLOCATOR, TRANSLOCATOR, TRANSLOCATOR,
				Blocks.CHORUS_FLOWER,
				Blocks.PISTON, Blocks.PISTON);

		register("electric_laser", new ItemStack(ModBlocks.ELECTRIC_LASER),
				new Color(0xB30000FF, true), new Color(0xD900FFFF, true),
				OPTIC_FIBER, OPTIC_FIBER, OPTIC_FIBER, OPTIC_FIBER,
				"blockIron",
				"dustRedstone", "dustRedstone", "dustRedstone",
				OreDictionary.doesOreNameExist("ingotSilver") ? "ingotSilver" : "gemLapis",
				OreDictionary.doesOreNameExist("ingotPlatinum") ? "ingotPlatinum" : "gemLapis",
				REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY,
				LENS, LENS, LENS);


		register("ammo", new ColorConsumingBehavior(new ItemStack(ModItems.LIGHT_CARTRIDGE), new ItemStack(ModItems.LIGHT_CARTRIDGE)));
		register("grenade", new ColorConsumingBehavior(new ItemStack(ModItems.GRENADE), new ItemStack(ModItems.GRENADE)));
	}
}
