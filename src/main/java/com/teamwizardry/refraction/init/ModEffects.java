package com.teamwizardry.refraction.init;

import com.teamwizardry.refraction.common.effect.*;
import com.teamwizardry.refraction.api.beam.EffectTracker;

public class ModEffects {
	public static void init() {
		EffectTracker.registerEffect(new EffectFreeze());
		EffectTracker.registerEffect(new EffectAttract());
		EffectTracker.registerEffect(new EffectBonemeal());
		EffectTracker.registerEffect(new EffectBreak());
		EffectTracker.registerEffect(new EffectBurn());
		EffectTracker.registerEffect(new EffectDisperse());
		EffectTracker.registerEffect(new EffectPlace());
	}
}
