package com.teamwizardry.refraction.common.item;

import com.teamwizardry.librarianlib.client.util.ColorUtils;
import com.teamwizardry.librarianlib.common.base.item.IItemColorProvider;
import com.teamwizardry.librarianlib.common.base.item.ItemMod;
import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.IAmmoConsumer;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.modes.BeamModeRegistry;
import kotlin.jvm.functions.Function2;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    @NotNull
    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        if (ItemNBTHelper.getInt(stack, "color", -1) == -1 || GuiScreen.isAltKeyDown()) return EnumAction.NONE;
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
        World world = playerIn.getEntityWorld();

        if (!(playerIn instanceof EntityPlayer)) return;

        if (!world.isRemote && stack.getTagCompound() == null) return;

        if (!world.isRemote && !stack.getTagCompound().hasKey("color")) return;

        Color color = new Color(ItemNBTHelper.getInt(stack, "color", 0xFFFFFF), true);
        ItemStack ammo = IAmmoConsumer.findAmmo((EntityPlayer) playerIn, color);

        if (!world.isRemote && ammo == null) return;

        if (!world.isRemote && ammo.getItemDamage() >= ammo.getMaxDamage() - 1) return;

        if (!world.isRemote) ammo.setItemDamage(ammo.getItemDamage() + 1);

        boolean handMod = playerIn.getHeldItemMainhand() == stack ^ playerIn.getPrimaryHand() == EnumHandSide.LEFT;
        Vec3d cross = playerIn.getLook(1).crossProduct(new Vec3d(0, playerIn.getEyeHeight(), 0)).normalize().scale(playerIn.width / 2);
        if (!handMod) cross = cross.scale(-1);
        Vec3d playerVec = new Vec3d(playerIn.posX + cross.xCoord, playerIn.posY + playerIn.getEyeHeight() + cross.yCoord - 0.2, playerIn.posZ + cross.zCoord);

        Beam beam = new Beam(playerIn.getEntityWorld(), playerVec, playerIn.getLook(1), color)
                .setMode(BeamModeRegistry.GUN)
                .setUUIDToSkip(playerIn.getUniqueID())
                .setCaster(playerIn)
                .setRange(30)
                .enableParticleEnd();
        beam.spawn();
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
