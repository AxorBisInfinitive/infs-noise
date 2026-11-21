package com.inf.noise.system.noise.overworld.Cwg.simulation;

import com.inf.farlands.simulation.int1024.noise.Density;
import com.inf.farlands.simulation.int1024.noise.InterpolatedDensity;
import love.crysh.long2.ints.Int1024;
import com.inf.noise.system.noise.overworld.Cwg.CwgDensity;
import com.inf.noise.system.noise.overworld.Cwg.CwgNoise;

/**
 * 模拟 CWG 密度链：复用 CwgNoise 与 CwgDensity.Ops，
 * 使用 1024 位方块坐标、常量高度与波动系数输入。
 * 算子顺序与原模拟实现保持一致。
 */
public final class CwgSimulationDensity implements InterpolatedDensity {
    private final CwgNoise selector;
    private final CwgNoise low;
    private final CwgNoise high;
    private final CwgNoise depth;
    private final CwgDensity.Ops ops;
    private final double heightSource;
    private final double factorSource;

    public CwgSimulationDensity(CwgNoise selector, CwgNoise low, CwgNoise high,
            CwgNoise depth, double heightSource, double factorSource, CwgDensity.Ops ops) {
        this.selector = selector;
        this.low = low;
        this.high = high;
        this.depth = depth;
        this.ops = ops;
        this.heightSource = heightSource;
        this.factorSource = factorSource;
    }

    @Override
    public double compute(Int1024 x, Int1024 y, Int1024 z) {
        double dx = x.toDouble();
        double dy = y.toDouble();
        double dz = z.toDouble();

        double selectorValue = this.selector.normalized(dx, dy, dz) * this.ops.selectorFactor()
                + this.ops.selectorOffset();
        selectorValue = selectorValue < 0.0 ? 0.0 : (selectorValue > 1.0 ? 1.0 : selectorValue);

        double lowValue = this.low.normalized(dx, dy, dz) * this.ops.lowFactor()
                + this.ops.lowOffset();
        double highValue = this.high.normalized(dx, dy, dz) * this.ops.highFactor()
                + this.ops.highOffset();

        double depthValue = this.depth.normalized(dx, dy, dz) * this.ops.depthFactor()
                + this.ops.depthOffset();
        if (depthValue < 0.0) {
            depthValue *= -0.3;
        }
        depthValue = depthValue * 3.0 - 2.0;
        depthValue = depthValue < -2.0 ? -2.0 : (depthValue > 1.0 ? 1.0 : depthValue);
        if (depthValue < 0.0) {
            depthValue /= 2.0 * 2.0 * 1.4;
        } else if (depthValue > 0.0) {
            depthValue /= 8.0;
        }
        depthValue *= 0.2 * 17.0 / 64.0;

        double height = this.heightSource * this.ops.heightFactor() + this.ops.heightOffset();
        double variation = Math.abs(this.factorSource);
        if (height > dy) {
            variation *= this.ops.specialHeightVariationFactor();
        }
        double volatility = variation * this.ops.heightVariationFactor()
                + this.ops.heightVariationOffset();

        double blended = lowValue + selectorValue * (highValue - lowValue);
        return (blended + depthValue) * volatility + height - Math.signum(volatility) * dy;
    }

    @Override
    public double minValue() {
        return -1.0E300;
    }

    @Override
    public double maxValue() {
        return 1.0E300;
    }

    @Override
    public Density mapAll(Visitor visitor) {
        return visitor.apply(this);
    }
}
