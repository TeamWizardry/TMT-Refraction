package com.teamwizardry.refraction.init.recipies;

import com.teamwizardry.refraction.init.ModBlocks;
import com.teamwizardry.refraction.init.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by LordSaad44
 */
public class AssemblyRecipies {

	public static ArrayList<AssemblyRecipe> recipes;

	public static void init() {
		recipes = new ArrayList<>();

		recipes.add(new AssemblyRecipe(new ItemStack(ModItems.REFLECTIVE_ALLOY, 3), new Color(0x80FFFFFF, true), Color.WHITE, Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT, Items.GOLD_INGOT, Items.GOLD_INGOT, Items.GOLD_INGOT));
		recipes.add(new AssemblyRecipe(new ItemStack(Item.getItemFromBlock(ModBlocks.DISCO_BALL)), new Color(0x999B9B9B, true), new Color(0x696969), Items.DIAMOND, Items.DIAMOND, Items.DIAMOND, Items.DIAMOND, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY));
		recipes.add(new AssemblyRecipe(new ItemStack(ModBlocks.MIRROR), new Color(0x59003CFF, true), new Color(0xCC0026A2, true), Blocks.GLASS_PANE, Blocks.GLASS_PANE, Blocks.GLASS_PANE, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY));
		recipes.add(new AssemblyRecipe(new ItemStack(ModBlocks.PRISM), new Color(0x8CFFFFFF, true), new Color(0xCCFFFFFF, true), ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS));
		recipes.add(new AssemblyRecipe(new ItemStack(ModBlocks.SPLITTER), 0, 0, 0, 80, 255, 255, 255, 200, ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY));
		recipes.add(new AssemblyRecipe(new ItemStack(ModBlocks.LASER), 0, 0, 0, 180, 255, 255, 255, 200, new ItemStack(ModBlocks.OPTIC_FIBER, 4), Blocks.IRON_BLOCK, Items.REDSTONE, Items.REDSTONE, Items.REDSTONE, Items.GLOWSTONE_DUST, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS));
		recipes.add(new AssemblyRecipe(new ItemStack(ModBlocks.REF_CHAMBER), 0, 0, 0, 100, 255, 255, 255, 200, ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT));
		recipes.add(new AssemblyRecipe(new ItemStack(ModBlocks.ELECTRON_EXCITER), 0, 0, 0, 200, 255, 255, 255, 250, new ItemStack(ModBlocks.LENS, 6), new ItemStack(ModItems.REFLECTIVE_ALLOY, 6), new ItemStack(ModBlocks.SPLITTER), new ItemStack(ModBlocks.OPTIC_FIBER, 4)));
		recipes.add(new AssemblyRecipe(new ItemStack(ModBlocks.OPTIC_FIBER, 4), 0, 0, 0, 100, 255, 255, 255, 200, new ItemStack(ModBlocks.LENS, 3), new ItemStack(ModItems.REFLECTIVE_ALLOY, 3), new ItemStack(Items.DIAMOND)));
		recipes.add(new AssemblyRecipe(new ItemStack(ModBlocks.SPECTROMETER), 0, 0, 0, 50, 255, 255, 255, 100, new ItemStack(ModBlocks.LENS, 6), new ItemStack(ModItems.REFLECTIVE_ALLOY, 6), new ItemStack(Items.DIAMOND, 2), new ItemStack(Items.DYE, 2, 4), new ItemStack(Items.DYE, 2, 10), new ItemStack(Items.DYE, 2, 1)));
		recipes.add(new AssemblyRecipe(new ItemStack(ModBlocks.SENSOR), 0, 0, 0, 100, 255, 255, 255, 200, new ItemStack(ModBlocks.LENS, 6), new ItemStack(Items.REDSTONE, 3), new ItemStack(Items.DIAMOND, 1)));
	}
}
