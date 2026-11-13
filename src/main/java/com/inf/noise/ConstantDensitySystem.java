package com.inf.noise;

import com.inf.farlands.terrain.NoiseSystem;
import com.inf.farlands.terrain.registry.SystemArgs;
import com.inf.farlands.terrain.registry.SystemDefaultParams;
import com.inf.farlands.terrain.registry.SystemParams;

import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseRouter;

public final class ConstantDensitySystem implements NoiseSystem {
    @SystemDefaultParams
    public static final SystemParams DEFAULT_PARAMS = SystemParams.EMPTY;

    public ConstantDensitySystem(SystemArgs args) {
    }

    @Override
    public DensityFunction createFinalDensity(NoiseRouter router) {
        return DensityFunctions.constant(-1.0);
    }
}
