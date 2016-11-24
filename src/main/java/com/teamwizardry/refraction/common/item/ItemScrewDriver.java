package com.teamwizardry.refraction.common.item;

import com.teamwizardry.librarianlib.common.base.item.ItemMod;
import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.librarianlib.common.util.MethodHandleHelper;
import com.teamwizardry.refraction.api.IPrecision;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.teamwizardry.refraction.api.IPrecision.Helper.*;

/**
 * Created by LordSaad44
 */
public class ItemScrewDriver extends ItemMod {


	@SideOnly(Side.CLIENT)
	private static Function2<GuiIngame, Object, Unit> remainingHighlightTicksSetter;

	private static boolean setUpSetter = false;

	public ItemScrewDriver() {
		super("screw_driver");
		setMaxStackSize(1);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		Block block = worldIn.getBlockState(pos).getBlock();

		if (block instanceof IPrecision) {
			((IPrecision) block).adjust(worldIn, pos, stack, playerIn.isSneaking(), facing);
			return EnumActionResult.SUCCESS;
		} else {
			int ori = getRotationIndex(stack);
			int i = MathHelper.clamp_int(playerIn.isSneaking() ? ori - 1 : ori + 1, 0, multipliers.length - 1);

			if (ori == i) return EnumActionResult.FAIL;

			ItemNBTHelper.setInt(stack, MODE_TAG, i);
			if (playerIn.worldObj.isRemote)
				displayItemName(30);
			return EnumActionResult.SUCCESS;
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		int ori = getRotationIndex(stack);
		int i = MathHelper.clamp_int(playerIn.isSneaking() ? ori - 1 : ori + 1, 0, multipliers.length - 1);
		if (ori == i)
			return ActionResult.newResult(EnumActionResult.FAIL, stack);

		ItemNBTHelper.setInt(stack, MODE_TAG, MathHelper.clamp_int(i, 0, multipliers.length - 1));
		if (playerIn.worldObj.isRemote)
			displayItemName(30);
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@SideOnly(Side.CLIENT)
	private void displayItemName(int ticks) {
		GuiIngame gui = Minecraft.getMinecraft().ingameGUI;
		if (!setUpSetter) {
			remainingHighlightTicksSetter = MethodHandleHelper.wrapperForSetter(GuiIngame.class, "q", "field_92017_k", "remainingHighlightTicks");
			setUpSetter = true;
		}
		remainingHighlightTicksSetter.invoke(gui, ticks);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int i = getRotationIndex(stack);
		return super.getUnlocalizedName(stack) + "." + i;
	}
}
