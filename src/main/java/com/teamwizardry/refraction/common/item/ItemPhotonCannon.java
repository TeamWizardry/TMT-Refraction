package com.teamwizardry.refraction.common.item;

import com.teamwizardry.librarianlib.common.base.item.ItemMod;
import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.modes.ModeGun;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;

/**
 * Created by LordSaad.
 */
public class ItemPhotonCannon extends ItemMod {

    public ItemPhotonCannon() {
        super("photon_cannon");
        setMaxStackSize(1);
        setMaxDamage(500);
        addPropertyOverride(new ResourceLocation(Constants.MOD_ID, "firing"),
                (stack, worldIn, entityIn) -> entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        playerIn.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 1000;
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase playerIn, int count) {
        if (stack.getItemDamage() >= 500) return;
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("color")) {
            Color color = new Color(ItemNBTHelper.getInt(stack, "color", 0xFFFFFF), true);
            boolean handMod = playerIn.getHeldItemMainhand() == stack ^ playerIn.getPrimaryHand() == EnumHandSide.LEFT;
            Vec3d cross = playerIn.getLook(1).crossProduct(new Vec3d(0, playerIn.getEyeHeight(), 0)).normalize().scale(playerIn.width / 2);
            if (!handMod) cross = cross.scale(-1);
            Vec3d playerVec = new Vec3d(playerIn.posX + cross.xCoord, playerIn.posY + playerIn.getEyeHeight() + cross.yCoord - 0.2, playerIn.posZ + cross.zCoord);

            stack.damageItem(1, playerIn);
            Minecraft.getMinecraft().player.sendChatMessage(stack.getItemDamage() + "/" + stack.getMaxDamage());
            Beam beam = new Beam(playerIn.getEntityWorld(), playerVec, playerIn.getLook(1), color)
                    .setMode(new ModeGun())
                    .setUUIDToSkip(playerIn.getUniqueID())
                    .enableParticleBeginning()
                    .enableParticleEnd();
            beam.spawn();
        }
    }
}
