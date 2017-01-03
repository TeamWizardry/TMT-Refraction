package com.teamwizardry.refraction.init.recipies;

import com.teamwizardry.refraction.api.recipe.ColorConsumingBehavior;
import com.teamwizardry.refraction.init.ModBlocks;
import com.teamwizardry.refraction.init.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import java.awt.*;
import static com.teamwizardry.refraction.api.recipe.AssemblyBehaviors.register;

/**
 * Created by LordSaad44
 */
public class ModAssemblyRecipes
{
	public static void init()
	{
		// T0
		register("mirror", new ItemStack(ModBlocks.MIRROR), new Color(255, 255, 255, 1), new Color(255, 255, 255, 64), Blocks.GLASS_PANE, Blocks.GLASS_PANE, Blocks.GLASS_PANE, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY);

		// T1
		register("prism", new ItemStack(ModBlocks.PRISM), new Color(255, 255, 255, 17), new Color(255, 255, 255, 64), ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS);

		// T2
		register("reflection_chamber", new ItemStack(ModBlocks.REF_CHAMBER), new Color(255, 96, 0, 32), new Color(255, 160, 0, 64), ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT);
		register("splitter", new ItemStack(ModBlocks.SPLITTER), new Color(0, 255, 0, 16), new Color(0, 255, 0, 32), ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY);
		register("spectrometer", new ItemStack(ModBlocks.SPECTROMETER), new Color(255, 255, 255, 49), new Color(255, 255, 255, 96), new ItemStack(ModBlocks.LENS, 6), new ItemStack(ModItems.REFLECTIVE_ALLOY, 6), new ItemStack(Items.DIAMOND, 2), new ItemStack(Items.DYE, 2, 4), new ItemStack(Items.DYE, 2, 10), new ItemStack(Items.DYE, 2, 1));
		register("sensor", new ItemStack(ModBlocks.SENSOR), new Color(255, 96, 0, 32), new Color(255, 160, 0, 64), new ItemStack(ModBlocks.LENS, 6), new ItemStack(Items.REDSTONE, 3), new ItemStack(Items.DIAMOND, 1));

		// T3
		boolean silverExists = OreDictionary.doesOreNameExist("ingotSilver");
		boolean platinumExists = OreDictionary.doesOreNameExist("ingotPlatinum");
		register("electric_laser", new ItemStack(ModBlocks.ELECTRIC_LASER), new Color(0, 96, 255, 54), new Color(0, 160, 255, 128), new ItemStack(ModBlocks.OPTIC_FIBER, 4), Blocks.IRON_BLOCK, new ItemStack(Items.REDSTONE, 3), silverExists ? "ingotSilver" : new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()), platinumExists ? "ingotPlatinum" : new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()), new ItemStack(ModItems.REFLECTIVE_ALLOY, 6), new ItemStack(ModBlocks.LENS, 3));
		register("laser", new ItemStack(ModBlocks.LASER), new Color(255, 64, 64, 81), new Color(255, 106, 106, 192), new ItemStack(ModBlocks.OPTIC_FIBER, 4), Blocks.IRON_BLOCK, Items.REDSTONE, Items.REDSTONE, Items.REDSTONE, Items.GLOWSTONE_DUST, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS);
		register("disco", new ItemStack(ModBlocks.DISCO_BALL), new Color(255, 0, 255, 54), new Color(255, 0, 255, 128), Items.DIAMOND, Items.DIAMOND, Items.DIAMOND, Items.DIAMOND, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY);
		register("optic_fiber", new ItemStack(ModBlocks.OPTIC_FIBER, 4), new Color(96, 0, 255, 54), new Color(160, 0, 255, 128), new ItemStack(ModBlocks.LENS, 3), new ItemStack(ModItems.REFLECTIVE_ALLOY, 3), new ItemStack(Items.DIAMOND));

		// T4
		register("translocator", new ItemStack(ModBlocks.TRANSLOCATOR, 4), new Color(255, 0, 96, 128), new Color(255, 0, 160, 255), new ItemStack(ModBlocks.OPTIC_FIBER, 4), new ItemStack(Items.ENDER_PEARL), new ItemStack(Items.IRON_INGOT));
		register("electron_exciter", new ItemStack(ModBlocks.ELECTRON_EXCITER), new Color(0, 255, 96, 128), new Color(0, 255, 160, 255), new ItemStack(ModBlocks.LENS, 6), new ItemStack(ModItems.REFLECTIVE_ALLOY, 6), new ItemStack(ModBlocks.SPLITTER), new ItemStack(ModBlocks.OPTIC_FIBER, 4));

		// T5
		register("axyz", new ItemStack(ModBlocks.AXYZ), new Color(96, 1, 255, 225), new Color(160, 64, 255, 255), new ItemStack(ModBlocks.TRANSLOCATOR, 6), new ItemStack(Blocks.CHORUS_FLOWER), new ItemStack(Blocks.PISTON, 2));

		// Other
		register("ammo", new ColorConsumingBehavior(new ItemStack(ModItems.LIGHT_CARTRIDGE), new ItemStack(ModItems.LIGHT_CARTRIDGE)));
		register("grenade", new ColorConsumingBehavior(new ItemStack(ModItems.GRENADE), new ItemStack(ModItems.GRENADE)));
	}
}
