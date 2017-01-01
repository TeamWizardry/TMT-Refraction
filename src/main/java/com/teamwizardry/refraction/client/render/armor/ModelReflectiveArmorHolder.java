package com.teamwizardry.refraction.client.render.armor;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * armor_reflective - wiiv
 * Created using Tabula 4.1.1
 */
public class ModelReflectiveArmorHolder extends ModelBiped {

    public ModelRenderer body;
    public ModelRenderer armr;
    public ModelRenderer armL;
    public ModelRenderer belt;
    public ModelRenderer bootR;
    public ModelRenderer bootL;
    public ModelRenderer helm;
    public ModelRenderer armr_1;
    public ModelRenderer armr_2;
    public ModelRenderer armr_3;
    public ModelRenderer armr_4;
    public ModelRenderer armr_5;
    public ModelRenderer armr_6;
    public ModelRenderer armr_7;
    public ModelRenderer armr_8;
    public ModelRenderer bootL2;
    public ModelRenderer bootR1;
    public ModelRenderer armr_9;
    public ModelRenderer armr_10;
    public ModelRenderer armr_11;
    public ModelRenderer armr_12;

    public ModelReflectiveArmorHolder() {
        this.textureWidth = 64;
        this.textureHeight = 128;
        this.bootL2 = new ModelRenderer(this, 0, 51);
        this.bootL2.mirror = true;
        this.bootL2.setRotationPoint(1.9F, 12.0F, 0.0F);
        this.bootL2.addBox(-1.5F, -1.0F, -2.51F, 4, 7, 5, 0.0F);
        this.bootR1 = new ModelRenderer(this, 0, 51);
        this.bootR1.setRotationPoint(-1.9F, 12.0F, 0.0F);
        this.bootR1.addBox(-2.5F, -1.0F, -2.51F, 4, 7, 5, 0.0F);
        this.body = new ModelRenderer(this, 0, 16);
        this.body.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.body.addBox(-4.5F, 0.0F, -3.0F, 9, 6, 6, 0.0F);
        this.armr_5 = new ModelRenderer(this, 18, 40);
        this.armr_5.mirror = true;
        this.armr_5.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.armr_5.addBox(-1.0F, 6.0F, -2.0F, 4, 4, 4, 0.0F);
        this.armr_10 = new ModelRenderer(this, 18, 56);
        this.armr_10.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.armr_10.addBox(2.0F, 4.0F, -1.5F, 1, 5, 3, 0.0F);
        this.helm = new ModelRenderer(this, 0, 0);
        this.helm.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.helm.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
        this.bootL = new ModelRenderer(this, 0, 75);
        this.bootL.mirror = true;
        this.bootL.setRotationPoint(1.9F, 12.0F, 0.0F);
        this.bootL.addBox(-1.5F, 6.0F, -2.0F, 4, 6, 4, 0.0F);
        this.armr_3 = new ModelRenderer(this, 48, 40);
        this.armr_3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.armr_3.addBox(-3.5F, -3.0F, -3.0F, 1, 6, 6, 0.0F);
        this.setRotateAngle(armr_3, 0.0F, -0.08726646259971647F, 0.0F);
        this.armr_2 = new ModelRenderer(this, 18, 48);
        this.armr_2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.armr_2.addBox(-3.5F, 3.0F, -1.5F, 1, 5, 3, 0.0F);
        this.armr_8 = new ModelRenderer(this, 18, 48);
        this.armr_8.mirror = true;
        this.armr_8.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.armr_8.addBox(2.5F, 3.0F, -1.5F, 1, 5, 3, 0.0F);
        this.armr_9 = new ModelRenderer(this, 0, 63);
        this.armr_9.mirror = true;
        this.armr_9.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.armr_9.addBox(0.5F, -1.0F, -3.0F, 3, 6, 6, 0.0F);
        this.armr = new ModelRenderer(this, 0, 40);
        this.armr.setRotationPoint(-5.0F, 2.0F, -0.0F);
        this.armr.addBox(-3.0F, -2.0F, -2.5F, 4, 6, 5, 0.0F);
        this.armr_6 = new ModelRenderer(this, 48, 40);
        this.armr_6.mirror = true;
        this.armr_6.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.armr_6.addBox(2.5F, -3.0F, -3.0F, 1, 6, 6, 0.0F);
        this.setRotateAngle(armr_6, 0.0F, 0.08726646259971647F, 0.0F);
        this.armr_7 = new ModelRenderer(this, 34, 40);
        this.armr_7.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.armr_7.addBox(1.0F, -3.0F, -3.0F, 1, 6, 6, 0.0F);
        this.belt = new ModelRenderer(this, 0, 28);
        this.belt.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.belt.addBox(-4.0F, 6.0F, -2.5F, 8, 7, 5, 0.0F);
        this.bootR = new ModelRenderer(this, 0, 75);
        this.bootR.setRotationPoint(-1.9F, 12.0F, 0.0F);
        this.bootR.addBox(-2.5F, 6.0F, -2.0F, 4, 6, 4, 0.0F);
        this.armr_11 = new ModelRenderer(this, 0, 63);
        this.armr_11.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.armr_11.addBox(-3.5F, -1.0F, -3.0F, 3, 6, 6, 0.0F);
        this.armr_12 = new ModelRenderer(this, 18, 56);
        this.armr_12.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.armr_12.addBox(-3.0F, 4.0F, -1.5F, 1, 5, 3, 0.0F);
        this.armr_1 = new ModelRenderer(this, 18, 40);
        this.armr_1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.armr_1.addBox(-3.0F, 6.0F, -2.0F, 4, 4, 4, 0.0F);
        this.armL = new ModelRenderer(this, 0, 40);
        this.armL.mirror = true;
        this.armL.setRotationPoint(5.0F, 2.0F, -0.0F);
        this.armL.addBox(-1.0F, -2.0F, -2.5F, 4, 6, 5, 0.0F);
        this.armr_4 = new ModelRenderer(this, 34, 40);
        this.armr_4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.armr_4.addBox(-2.0F, -3.0F, -3.0F, 1, 6, 6, 0.0F);
        this.belt.addChild(this.bootL2);
        this.belt.addChild(this.bootR1);
        this.armL.addChild(this.armr_5);
        this.bootL2.addChild(this.armr_10);
        this.armr.addChild(this.armr_3);
        this.armr.addChild(this.armr_2);
        this.armL.addChild(this.armr_8);
        this.bootL2.addChild(this.armr_9);
        this.armL.addChild(this.armr_6);
        this.armL.addChild(this.armr_7);
        this.bootR1.addChild(this.armr_11);
        this.bootR1.addChild(this.armr_12);
        this.armr.addChild(this.armr_1);
        this.armr.addChild(this.armr_4);

        bipedHead.addChild(helm);
        bipedBody.addChild(body);
        bipedBody.addChild(belt);
        bipedRightArm.addChild(armr);
        bipedLeftArm.addChild(armL);
        bipedLeftLeg.addChild(bootL2);
        bipedRightLeg.addChild(bootR1);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.body.render(f5);
        this.helm.render(f5);
        this.bootL.render(f5);
        this.armr.render(f5);
        this.belt.render(f5);
        this.bootR.render(f5);
        this.armL.render(f5);
    }

    /**
     * This is a helper function from Tabula to set the rotation of armor parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
