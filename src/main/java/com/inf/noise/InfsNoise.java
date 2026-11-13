package com.inf.noise;

import com.inf.farlands.simulation.registry.SimulationRegistries;
import com.inf.farlands.terrain.registry.FarlandsSystems;
import com.inf.farlands.terrain.registry.SystemId;
import com.inf.noise.system.noise.overworld.Cwg.CwgNoiseSystem;
import com.inf.noise.system.noise.overworld.Cwg.simulation.CwgSimulationNoiseSystem;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

public final class InfsNoise implements ModInitializer {
    public static final String MOD_ID = "infs-noise";

    @Override
    public void onInitialize() {
        FarlandsSystems.registerTerrain(
                new SystemId(
                        id("overworld_cwg_noise_system")),
                CwgNoiseSystem.class);
        SimulationRegistries.register(
                new SystemId(
                        id("simulation_cwg_noise_system")),
                CwgSimulationNoiseSystem.class);
    }

    public static Identifier id(String value) {
        return Identifier.fromNamespaceAndPath(MOD_ID, value);
    }
}
