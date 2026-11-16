package com.inf.noise.system.noise.overworld.Sine;

import com.inf.farlands.simulation.int1024.SimulationNoiseSystem;
import com.inf.farlands.simulation.int1024.noise.Aquifer;
import com.inf.farlands.simulation.int1024.noise.Density;
import com.inf.farlands.simulation.int1024.noise.FluidPicker;
import com.inf.farlands.simulation.int1024.noise.FluidStatus;
import com.inf.farlands.simulation.int1024.noise.Random;
import com.inf.farlands.simulation.int1024.noise.Router;
import com.inf.farlands.simulation.int1024.noise.Settings;
import com.inf.farlands.terrain.registry.SystemArgs;
import com.inf.farlands.terrain.registry.SystemDefaultParams;
import com.inf.farlands.terrain.registry.SystemParams;
import com.inf.long2.ints.Int1024;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 测试用的模拟系统：地形是一张双向正弦波纹，没有别的。
 *
 * <p>
 * 三处口径与 VOID 系统相反：密度由 {@link SineDensity} 给出，不再恒负；液面查询恒空气，避免密度非正的半边被默认
 * picker 填成水或岩浆；含水层直接接管非正的一侧为空气，省掉流体的网格取样。
 *
 * <p>
 * 不吃参数，声明写 {@link SystemParams#EMPTY}：注册期要求每个实现类都有声明字段，没有声明不是一种状态。
 */
public final class SineSimulationNoiseSystem implements SimulationNoiseSystem {

    @SystemDefaultParams
    public static final SystemParams DEFAULT_PARAMS = SystemParams.EMPTY;

    public SineSimulationNoiseSystem(SystemArgs args) {
    }

    @Override
    public Density createFinalDensity(Router router) {
        return new SineDensity();
    }

    /** 液位取 MIN_VALUE 恒出空气，判据是严格小于。 */
    @Override
    public FluidPicker createFluidPicker(Settings settings) {
        return (x, y, z) -> new FluidStatus(Int1024.MIN_VALUE, Blocks.AIR.defaultBlockState());
    }

    /** 密度为正交给默认方块，非正接管成空气。 */
    @Override
    public Aquifer createAquifer(Settings settings, Int1024 sectionX, Int1024 sectionY, Int1024 sectionZ,
            Router router, Random random, FluidPicker picker) {
        return new Aquifer() {

            @Override
            public BlockState computeSubstance(Int1024 x, Int1024 y, Int1024 z, double density) {
                return density > 0.0 ? null : Blocks.AIR.defaultBlockState();
            }

            @Override
            public boolean shouldScheduleFluidUpdate() {
                return false;
            }
        };
    }
}
