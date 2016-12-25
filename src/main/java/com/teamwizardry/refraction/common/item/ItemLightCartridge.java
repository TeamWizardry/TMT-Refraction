package com.teamwizardry.refraction.common.item;

import com.teamwizardry.librarianlib.common.base.item.ItemMod;
import com.teamwizardry.refraction.api.IAmmo;

/**
 * Created by LordSaad.
 */
public class ItemLightCartridge extends ItemMod implements IAmmo {

    public ItemLightCartridge() {
        super("light_cartridge");
        setMaxStackSize(1);
        setMaxDamage(1000);
    }
}
