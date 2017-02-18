package com.teamwizardry.refraction.client.core;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.inventory.EntityEquipmentSlot;

import javax.annotation.Nonnull;

/**
 * armor_reflective - wiiv
 * Created using Tabula 4.1.1
 */
public class ModelReflectiveArmorHolder extends ModelBiped {

    public ModelRenderer helm;
    public ModelRenderer body;
    public ModelRenderer armR;
    public ModelRenderer armL;
    public ModelRenderer belt;
    public ModelRenderer bootR;
    public ModelRenderer bootL;
    public ModelRenderer armr1;
    public ModelRenderer armr2;
    public ModelRenderer armr3;
    public ModelRenderer armr4;
    public ModelRenderer arml1;
    public ModelRenderer arml2;
    public ModelRenderer arml3;
    public ModelRenderer arml4;
    public ModelRenderer legR;
    public ModelRenderer legL;
    public ModelRenderer legr1;
    public ModelRenderer legr2;
    public ModelRenderer legl1;
    public ModelRenderer legl2;
    private EntityEquipmentSlot slot;

    public ModelReflectiveArmorHolder(EntityEquipmentSlot slot) {
        this.slot = slot;
        this.textureWidth = 64;
        this.textureHeight = 128;
        this.legl2 = new ModelRenderer(this, 18, 56);
        this.legl2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.legl2.addBox(1.5F, 4.0F, -1.5F, 1, 5, 3, 0.0255F);
        this.armR = new ModelRenderer(this, 0, 40);
        this.armR.setRotationPoint(-5.0F, 2.0F, -0.0F);
        this.armR.addBox(-3.0F, -2.0F, -2.5F, 4, 6, 5, 0.0255F);
        this.body = new ModelRenderer(this, 0, 16);
        this.body.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.body.addBox(-4.5F, 0.0F, -3.0F, 9, 6, 6, 0.0255F);
        this.bootR = new ModelRenderer(this, 0, 75);
        this.bootR.setRotationPoint(-1.9F, 12.0F, 0.0F);
        this.bootR.addBox(-2.0F, 6.0F, -2.0F, 4, 6, 4, 0.0255F);
        this.armr2 = new ModelRenderer(this, 18, 48);
        this.armr2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.armr2.addBox(-3.5F, 3.0F, -1.5F, 1, 5, 3, 0.0255F);
        this.arml3 = new ModelRenderer(this, 48, 40);
        this.arml3.mirror = true;
        this.arml3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.arml3.addBox(2.5F, -3.0F, -3.0F, 1, 6, 6, 0.0255F);
        this.setRotateAngle(arml3, 0.0F, 0.08726646259971647F, 0.0F);
        this.armr4 = new ModelRenderer(this, 34, 40);
        this.armr4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.armr4.addBox(-2.0F, -3.0F, -3.0F, 1, 6, 6, 0.0255F);
        this.armL = new ModelRenderer(this, 0, 40);
        this.armL.mirror = true;
        this.armL.setRotationPoint(5.0F, 2.0F, -0.0F);
        this.armL.addBox(-1.0F, -2.0F, -2.5F, 4, 6, 5, 0.0255F);
        this.arml2 = new ModelRenderer(this, 18, 48);
        this.arml2.mirror = true;
        this.arml2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.arml2.addBox(2.5F, 3.0F, -1.5F, 1, 5, 3, 0.0255F);
        this.belt = new ModelRenderer(this, 0, 28);
        this.belt.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.belt.addBox(-4.0F, 6.0F, -2.5F, 8, 7, 5, 0.0255F);
        this.arml4 = new ModelRenderer(this, 34, 40);
        this.arml4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.arml4.addBox(1.0F, -3.0F, -3.0F, 1, 6, 6, 0.0255F);
        this.bootL = new ModelRenderer(this, 0, 75);
        this.bootL.mirror = true;
        this.bootL.setRotationPoint(1.9F, 12.0F, 0.0F);
        this.bootL.addBox(-2.0F, 6.0F, -2.0F, 4, 6, 4, 0.0255F);
        this.legL = new ModelRenderer(this, 0, 51);
        this.legL.mirror = true;
        this.legL.setRotationPoint(1.9F, 12.0F, 0.0F);
        this.legL.addBox(-2.0F, -1.0F, -2.51F, 4, 7, 5, 0.0255F);
        this.legl1 = new ModelRenderer(this, 0, 63);
        this.legl1.mirror = true;
        this.legl1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.legl1.addBox(0.0F, -1.0F, -3.0F, 3, 6, 6, 0.0255F);
        this.armr3 = new ModelRenderer(this, 48, 40);
        this.armr3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.armr3.addBox(-3.5F, -3.0F, -3.0F, 1, 6, 6, 0.0255F);
        this.setRotateAngle(armr3, 0.0F, -0.08726646259971647F, 0.0F);
        this.arml1 = new ModelRenderer(this, 18, 40);
        this.arml1.mirror = true;
        this.arml1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.arml1.addBox(-1.0F, 6.5F, -2.0F, 4, 4, 4, 0.0255F);
        this.helm = new ModelRenderer(this, 0, 0);
        this.helm.setRotationPoint(0.0F, 0.0F, 0.0F);
	    this.helm.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.3F);
	    this.legR = new ModelRenderer(this, 0, 51);
        this.legR.setRotationPoint(-1.9F, 12.0F, 0.0F);
        this.legR.addBox(-2.0F, -1.0F, -2.51F, 4, 7, 5, 0.0255F);
        this.legr1 = new ModelRenderer(this, 0, 63);
        this.legr1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.legr1.addBox(-3.0F, -1.0F, -3.0F, 3, 6, 6, 0.0255F);
        this.legr2 = new ModelRenderer(this, 18, 56);
        this.legr2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.legr2.addBox(-2.5F, 4.0F, -1.5F, 1, 5, 3, 0.0255F);
        this.armr1 = new ModelRenderer(this, 18, 40);
        this.armr1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.armr1.addBox(-3.0F, 6.5F, -2.0F, 4, 4, 4, 0.0255F);
        this.legL.addChild(this.legl2);
        this.armR.addChild(this.armr2);
        this.armL.addChild(this.arml3);
        this.armR.addChild(this.armr4);
        this.armL.addChild(this.arml2);
        this.armL.addChild(this.arml4);
        this.legL.addChild(this.legl1);
        this.armR.addChild(this.armr3);
        this.armL.addChild(this.arml1);
        this.legR.addChild(this.legr1);
        this.legR.addChild(this.legr2);
        this.armR.addChild(this.armr1);
        this.body.addChild(this.belt);

        bipedHead = helm;
        bipedBody = body;
        bipedRightArm = armL;
        bipedLeftArm = armR;
        bipedRightLeg = legL;
        bipedLeftLeg = legR;
    }

    @Override
    public void render(@Nonnull Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

        if (entity instanceof EntityArmorStand) {
            // Hack so helmets look right on armor stand
            netHeadYaw = 0;
        }

        helm.showModel = slot == EntityEquipmentSlot.HEAD;
        body.showModel = slot == EntityEquipmentSlot.CHEST;
        armR.showModel = slot == EntityEquipmentSlot.CHEST;
        armL.showModel = slot == EntityEquipmentSlot.CHEST;
        legR.showModel = slot == EntityEquipmentSlot.LEGS;
        legL.showModel = slot == EntityEquipmentSlot.LEGS;
        bootL.showModel = slot == EntityEquipmentSlot.FEET;
        bootR.showModel = slot == EntityEquipmentSlot.FEET;
        bipedHeadwear.showModel = false;

        bipedHead = helm;
        bipedBody = body;
        bipedRightArm = armR;
        bipedLeftArm = armL;
        if (slot == EntityEquipmentSlot.LEGS) {
            bipedRightLeg = legR;
            bipedLeftLeg = legL;
        } else {
            bipedRightLeg = bootR;
            bipedLeftLeg = bootL;
        }

        super.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
