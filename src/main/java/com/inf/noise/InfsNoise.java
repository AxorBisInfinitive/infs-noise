package com.inf.noise;

import com.inf.farlands.simulation.registry.SimulationRegistries;
import com.inf.farlands.terrain.registry.FarlandsSystems;
import com.inf.farlands.terrain.registry.SystemId;
import com.inf.noise.system.noise.overworld.Cwg.CwgNoiseSystem;
import com.inf.noise.system.noise.overworld.Cwg.simulation.CwgSimulationNoiseSystem;
import com.inf.noise.system.noise.overworld.Sine.SineSimulationNoiseSystem;

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
        // 测试用：双向正弦波纹地形，用来肉眼核对方向、镜像与 2^63 之外的表现。
        SimulationRegistries.register(
                new SystemId(
                        id("simulation_sine_noise_system")),
                SineSimulationNoiseSystem.class);
    }

    public static Identifier id(String value) {
        return Identifier.fromNamespaceAndPath(MOD_ID, value);
    }
}
