package com.teamwizardry.refraction.client.render.armor;

import com.teamwizardry.refraction.common.item.armor.ItemArmorReflectiveAlloy;
import net.minecraft.inventory.EntityEquipmentSlot;

/**
 * Created by LordSaad.
 */
public class ModelReflectiveAlloy extends ModelReflectiveArmorBase {

    public EntityEquipmentSlot slot;

    public ModelReflectiveAlloy(EntityEquipmentSlot slot) {
        super(slot);
        ModelReflectiveArmorHolder m = ItemArmorReflectiveAlloy.holder;
        this.head = m.helm;
        this.armL = m.armL;
        this.armR = m.armr;
        this.chest = m.body;
        this.legL = m.belt;
        this.legR = m.belt;
        this.bootL = m.bootL;
        this.bootR = m.bootR;
        this.armorScale = 1.2f;
    }
}
