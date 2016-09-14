package com.teamwizardry.refraction.init;

import com.teamwizardry.refraction.common.effect.EffectAccelerate;
import com.teamwizardry.refraction.common.effect.EffectAttract;
import com.teamwizardry.refraction.common.effect.EffectBonemeal;
import com.teamwizardry.refraction.common.effect.EffectBreak;
import com.teamwizardry.refraction.common.effect.EffectBurn;
import com.teamwizardry.refraction.common.effect.EffectDisperse;
import com.teamwizardry.refraction.common.light.EffectTracker;

public class ModEffects
{
	public static void init()
	{
		EffectTracker.registerEffect(new EffectAccelerate());
		EffectTracker.registerEffect(new EffectAttract());
		EffectTracker.registerEffect(new EffectBonemeal());
		EffectTracker.registerEffect(new EffectBreak());
		EffectTracker.registerEffect(new EffectBurn());
		EffectTracker.registerEffect(new EffectDisperse());
	}
}
