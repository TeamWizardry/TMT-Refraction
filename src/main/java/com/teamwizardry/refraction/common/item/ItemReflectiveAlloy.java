package com.teamwizardry.refraction.common.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.teamwizardry.refraction.Refraction;

public class ItemReflectiveAlloy extends Item
{
	public ItemReflectiveAlloy()
	{
		setRegistryName("reflective_alloy");
		setUnlocalizedName("reflective_alloy");
		GameRegistry.register(this);
		setCreativeTab(Refraction.tab);
	}
	
	@SideOnly(Side.CLIENT)
	public void initModel()
	{
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
}
