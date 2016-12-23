package com.teamwizardry.refraction.common.item;

import com.teamwizardry.librarianlib.common.base.item.ItemMod;
import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.IAmmoConsumer;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.modes.ModeGun;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * Created by LordSaad.
 */
public class ItemPhotonCannon extends ItemMod implements IAmmoConsumer {

    public ItemPhotonCannon() {
        super("photon_cannon");
        setMaxStackSize(1);
        addPropertyOverride(new ResourceLocation(Constants.MOD_ID, "firing"),
                (stack, worldIn, entityIn) -> entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F);
    }

    @NotNull
    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @NotNull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@NotNull ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        playerIn.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 1000;
    }

    @Override
    @Nullable
    public ItemStack onItemUseFinish(@NotNull ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        return stack;
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase playerIn, int count) {
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("color")) {
            if (IAmmoConsumer.hasDurability(stack)) {
                if (IAmmoConsumer.getDurability(stack) <= 0) {
                    IAmmoConsumer.removeDurability(stack);
                    return;
                }
                IAmmoConsumer.setDurability(stack, IAmmoConsumer.getDurability(stack) - 1);

                Color color = new Color(ItemNBTHelper.getInt(stack, "color", 0xFFFFFF), true);

                boolean handMod = playerIn.getHeldItemMainhand() == stack ^ playerIn.getPrimaryHand() == EnumHandSide.LEFT;
                Vec3d cross = playerIn.getLook(1).crossProduct(new Vec3d(0, playerIn.getEyeHeight(), 0)).normalize().scale(playerIn.width / 2);
                if (!handMod) cross = cross.scale(-1);
                Vec3d playerVec = new Vec3d(playerIn.posX + cross.xCoord, playerIn.posY + playerIn.getEyeHeight() + cross.yCoord - 0.2, playerIn.posZ + cross.zCoord);

                stack.damageItem(1, playerIn);
                Beam beam = new Beam(playerIn.getEntityWorld(), playerVec, playerIn.getLook(1), color)
                        .setMode(new ModeGun())
                        .setUUIDToSkip(playerIn.getUniqueID())
                        .setCaster(playerIn)
                        .setRange(30)
                        .enableParticleBeginning()
                        .enableParticleEnd();
                beam.spawn();

            }
        }
    }
}
