package com.teamwizardry.refraction.common.item;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.common.block.BlockMirror;

/**
 * Created by LordSaad44
 */
public class ItemScrewDriver extends Item {

	public static final String MODE_TAG = "mode";
	protected static final float[] multipliers = { // out of order so it defaults to 5
		5, 22.5f, 45, 90,
		1f/8f, 1f/4f, 1f/2f, 1
	};
	
	public ItemScrewDriver() {
		setRegistryName("screw_driver");
		setUnlocalizedName("screw_driver");
		GameRegistry.register(this);
		setMaxStackSize(1);
		setCreativeTab(Refraction.tab);
	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		Block block = worldIn.getBlockState(pos).getBlock();
		
		if(block instanceof BlockMirror) {
			( (BlockMirror) block ).adjust(worldIn, pos, stack, playerIn, facing);
			return EnumActionResult.SUCCESS;
		} else {
			if(stack.getTagCompound() == null)
				stack.setTagCompound(new NBTTagCompound());
			int i = stack.getTagCompound().getInteger(MODE_TAG);
			i = (i + (playerIn.isSneaking() ? -1 : 1));
			while(i < 0)
				i += multipliers.length;
			i = i % multipliers.length;
			stack.getTagCompound().setInteger(MODE_TAG, i);
		}
		
		return EnumActionResult.FAIL;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int i = 0;
		if(stack.getTagCompound() != null)
			i = stack.getTagCompound().getInteger(MODE_TAG);
		return super.getUnlocalizedName(stack) + "." + (i % multipliers.length);
	}
	
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
	
	public float getRotationMultiplier(ItemStack stack) {
		return multipliers[getRotationIndex(stack)];
	}
	
	public int getRotationIndex(ItemStack stack) {
		int i = 0;
		if(stack.getTagCompound() != null)
			i = stack.getTagCompound().getInteger(MODE_TAG);
		while(i < 0)
			i += multipliers.length;
		return i % multipliers.length;
	}
}