package com.inf.noise;

import com.inf.farlands.terrain.registry.FarlandsSystems;
import com.inf.farlands.terrain.registry.SystemId;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

public final class InfsNoise implements ModInitializer {
    public static final String MOD_ID = "infs-noise";

    @Override
    public void onInitialize() {
        SystemId id = new SystemId(Identifier.fromNamespaceAndPath(MOD_ID, "constant_density"));
        FarlandsSystems.registerTerrain(id, ConstantDensitySystem.class);
    }
}
