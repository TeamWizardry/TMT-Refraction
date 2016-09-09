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
		EffectTracker.registerEffect(new EffectAccelerate(0));
		EffectTracker.registerEffect(new EffectAttract(0));
		EffectTracker.registerEffect(new EffectBonemeal(0));
		EffectTracker.registerEffect(new EffectBreak(0));
		EffectTracker.registerEffect(new EffectBurn(0));
		EffectTracker.registerEffect(new EffectDisperse(0));
	}
}
