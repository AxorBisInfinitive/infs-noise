package com.inf.noise;

import com.inf.farlands.terrain.registry.FarlandsSystems;
import com.inf.farlands.terrain.registry.SystemId;
import com.inf.noise.system.noise.overworld.Cwg.CwgNoiseSystem;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

public final class InfsNoise implements ModInitializer {
    public static final String MOD_ID = "infs-noise";

    @Override
    public void onInitialize() {
        SystemId id = new SystemId(
                Identifier.fromNamespaceAndPath(MOD_ID, "overworld_cwg_noise_system"));
        FarlandsSystems.registerTerrain(id, CwgNoiseSystem.class);
    }
}
