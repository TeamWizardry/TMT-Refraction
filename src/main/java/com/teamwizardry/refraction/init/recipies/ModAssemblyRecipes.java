package com.teamwizardry.refraction.init.recipies;

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
public class ModAssemblyRecipes {

	public static void init() {
		register("disco", new ItemStack(ModBlocks.DISCO_BALL), new Color(0xA6FF00BE, true), new Color(0xCCDC00FF, true), Items.DIAMOND, Items.DIAMOND, Items.DIAMOND, Items.DIAMOND, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY);
		register("mirror", new ItemStack(ModBlocks.MIRROR), new Color(0x01FFFFFF, true), new Color(0xB3FFFFFF, true), Blocks.GLASS_PANE, Blocks.GLASS_PANE, Blocks.GLASS_PANE, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY);
		register("prism", new ItemStack(ModBlocks.PRISM), new Color(0x1AFFFFFF, true), new Color(0xB3FFFFFF, true), ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS);
		register("splitter", new ItemStack(ModBlocks.SPLITTER), new Color(0x4D00FF00, true), new Color(0x8800FF00, true), ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY);
		register("laser", new ItemStack(ModBlocks.LASER), new Color(0xB3FF3200, true), new Color(0xD9FF0032, true), new ItemStack(ModBlocks.OPTIC_FIBER, 4), Blocks.IRON_BLOCK, Items.REDSTONE, Items.REDSTONE, Items.REDSTONE, Items.GLOWSTONE_DUST, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS);
		register("reflection_chamber", new ItemStack(ModBlocks.REF_CHAMBER), new Color(0x59FF6400, true), new Color(0xB3FF0096, true), ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT);
		register("electron_exciter", new ItemStack(ModBlocks.ELECTRON_EXCITER), new Color(0xE600FFFF, true), new Color(0xFF00FF64, true), new ItemStack(ModBlocks.LENS, 6), new ItemStack(ModItems.REFLECTIVE_ALLOY, 6), new ItemStack(ModBlocks.SPLITTER), new ItemStack(ModBlocks.OPTIC_FIBER, 4));
		register("optic_fiber", new ItemStack(ModBlocks.OPTIC_FIBER, 4), new Color(0xA60000FF, true), new Color(0xCCFF0080, true), new ItemStack(ModBlocks.LENS, 3), new ItemStack(ModItems.REFLECTIVE_ALLOY, 3), new ItemStack(Items.DIAMOND));
		register("spectrometer", new ItemStack(ModBlocks.SPECTROMETER), new Color(0x59FFFFFF, true), new Color(0xB3FFFFFF, true), new ItemStack(ModBlocks.LENS, 6), new ItemStack(ModItems.REFLECTIVE_ALLOY, 6), new ItemStack(Items.DIAMOND, 2), new ItemStack(Items.DYE, 2, 4), new ItemStack(Items.DYE, 2, 10), new ItemStack(Items.DYE, 2, 1));
		register("sensor", new ItemStack(ModBlocks.SENSOR), new Color(0x40FF3200, true), new Color(0xB3FF0032, true), new ItemStack(ModBlocks.LENS, 6), new ItemStack(Items.REDSTONE, 3), new ItemStack(Items.DIAMOND, 1));
		register("translocator", new ItemStack(ModBlocks.TRANSLOCATOR, 4), new Color(0xA60000FF, true), new Color(0xCCFF0080, true), new ItemStack(ModBlocks.OPTIC_FIBER, 4), new ItemStack(Items.ENDER_PEARL), new ItemStack(Items.IRON_INGOT));
		register("axyz", new ItemStack(ModBlocks.AXYZ), new Color(0xC87000DF, true), new Color(0xFF9020FF, true), new ItemStack(ModBlocks.TRANSLOCATOR, 6), new ItemStack(Blocks.CHORUS_FLOWER), new ItemStack(Blocks.PISTON, 2));

		boolean silverExists = OreDictionary.doesOreNameExist("ingotSilver");
		boolean platinumExists = OreDictionary.doesOreNameExist("ingotPlatinum");
		register("electric_laser", new ItemStack(ModBlocks.ELECTRIC_LASER), new Color(0xB30000FF, true), new Color(0xD900FFFF, true), new ItemStack(ModBlocks.OPTIC_FIBER, 4), Blocks.IRON_BLOCK, new ItemStack(Items.REDSTONE, 3), silverExists ? "ingotSilver" : new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()), platinumExists ? "ingotPlatinum" : new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()), new ItemStack(ModItems.REFLECTIVE_ALLOY, 6), new ItemStack(ModBlocks.LENS, 3));
	}
}
