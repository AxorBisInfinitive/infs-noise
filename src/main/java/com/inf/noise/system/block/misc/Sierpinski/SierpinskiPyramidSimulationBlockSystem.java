package com.inf.noise.system.block.misc.Sierpinski;

import com.inf.farlands.simulation.int1024.SimulationBlockSystem;
import com.inf.farlands.terrain.registry.SystemArgs;
import com.inf.farlands.terrain.registry.SystemDefaultParams;
import com.inf.farlands.terrain.registry.SystemParamSpec;
import com.inf.farlands.terrain.registry.SystemParams;
import love.crysh.long2.ints.Int1024;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 谢尔宾斯基四棱锥的逐块模拟系统，顶点位于 (0, apexY, 0)，向下展开。
 * 三轴坐标与几何判定均使用 Int1024，实例构造后只读。
 */
public final class SierpinskiPyramidSimulationBlockSystem implements SimulationBlockSystem {

    private static final Int1024 DEFAULT_APEX_Y = Int1024.ONE.shift(1022).subtract(Int1024.ONE);

    @SystemDefaultParams
    public static final SystemParams DEFAULT_PARAMS = SystemParams.of(
            SystemParamSpec.ofInt1024("apexY").define(DEFAULT_APEX_Y).build());

    private final Int1024 apexY;

    public SierpinskiPyramidSimulationBlockSystem(SystemArgs args) {
        this.apexY = args.getInt1024("apexY");
    }

    @Override
    public BlockState fillBlock(Int1024 x, Int1024 y, Int1024 z) {
        if (y.compareTo(this.apexY) > 0) {
            return Blocks.AIR.defaultBlockState();
        }

        // 数学层数可超过有符号正数范围，先比较高度，再以低 1024 位参与判定。
        Int1024 depth = this.apexY.subtract(y);
        boolean hitX = depth.subtract(x).and(depth.add(x)).equals(Int1024.ZERO);
        boolean hitZ = depth.subtract(z).and(depth.add(z)).equals(Int1024.ZERO);
        return hitX && hitZ ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.AIR.defaultBlockState();
    }
}
