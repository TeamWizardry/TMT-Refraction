package com.teamwizardry.refraction.common.item;

import com.teamwizardry.librarianlib.features.base.item.IItemColorProvider;
import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.utilities.client.ColorUtils;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.IAmmo;
import com.teamwizardry.refraction.api.IAmmoConsumer;
import com.teamwizardry.refraction.common.entity.EntityPlasma;
import kotlin.jvm.functions.Function2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

/**
 * Created by LordSaad.
 */
public class ItemPhotonCannon extends ItemMod implements IAmmoConsumer, IItemColorProvider {

	public ItemPhotonCannon() {
		super("photon_cannon");
		setMaxStackSize(1);
		addPropertyOverride(new ResourceLocation(Constants.MOD_ID, "firing"),
				(stack, worldIn, entityIn) -> entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F);
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand hand) {
		ItemStack stack = playerIn.getHeldItem(hand);
		if (stack.getTagCompound() == null) return new ActionResult<>(EnumActionResult.FAIL, stack);

		if (!stack.getTagCompound().hasKey("color")) return new ActionResult<>(EnumActionResult.FAIL, stack);

		Color color = new Color(ItemNBTHelper.getInt(stack, "color", 0xFFFFFF), true);
		ItemStack ammo = IAmmoConsumer.findAmmo(playerIn, color);
		if (ammo == null) return new ActionResult<>(EnumActionResult.FAIL, stack);
		IAmmo ammoItem = (IAmmo) ammo.getItem();

		if (!ammoItem.drain(ammo, 1, true)) return new ActionResult<>(EnumActionResult.FAIL, stack);

		int ammoColor = ammoItem.getInternalColor(ammo);

		if (ammoColor != color.getRGB())
			ItemNBTHelper.setInt(stack, "color", ammoColor);

		if (!playerIn.capabilities.isCreativeMode)
			ammoItem.drain(ammo, 1, false);

		boolean handMod = playerIn.getHeldItemMainhand() == stack ^ playerIn.getPrimaryHand() == EnumHandSide.LEFT;
		Vec3d cross = playerIn.getLook(1).crossProduct(new Vec3d(0, playerIn.getEyeHeight(), 0)).normalize().scale(playerIn.width / 2);
		if (!handMod) cross = cross.scale(-1);
		Vec3d playerVec = new Vec3d(playerIn.posX + cross.x, playerIn.posY + playerIn.getEyeHeight() + cross.y - 0.2, playerIn.posZ + cross.z);

		if (worldIn.isRemote) return new ActionResult<>(EnumActionResult.FAIL, stack);

		EntityPlasma plasma = new EntityPlasma(worldIn, playerIn.getLook(0), color, playerIn.getEntityId());
		plasma.setPosition(playerVec.x, playerVec.y, playerVec.z);
		worldIn.spawnEntity(plasma);
		playerIn.getCooldownTracker().setCooldown(this, 5);
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	@Nullable
	@Override
	public Function2<ItemStack, Integer, Integer> getItemColorFunction() {
		return (stack, tintIndex) -> (tintIndex == 1 ? getColor(stack).getRGB() : 0xFFFFFF);
	}

	private Color getColor(ItemStack stack) {
		Color c = new Color(ItemNBTHelper.getInt(stack, "color", 0xFFFFFF));
		float[] comps = c.getRGBComponents(null);
		c = new Color(
				(float) Math.max(comps[0] - 0.12, 0),
				(float) Math.max(comps[1] - 0.12, 0),
				(float) Math.max(comps[2] - 0.12, 0));
		return ColorUtils.pulseColor(c);
	}
}
