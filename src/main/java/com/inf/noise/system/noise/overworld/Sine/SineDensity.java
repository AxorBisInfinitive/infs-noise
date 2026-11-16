package com.inf.noise.system.noise.overworld.Sine;

import com.inf.farlands.simulation.int1024.noise.Density;
import com.inf.long2.ints.Int1024;

/**
 * 测试用的正弦密度：地面高度由两条正弦叠加给出，密度取「地面高度减方块 Y」。
 *
 * <p>
 * 密度为正即实方块、为负即空气，所以这个密度的意思是「地面以下全是石头，地面以上全是空气」。两条正弦的波长不同，
 * 水平方向因此是一张可辨认的双向波纹，镜像或旋转一眼能看出来。
 *
 * <p>
 * 坐标先一次性转 double：这一层不求精度，只求形状可辨。量级极大时 double 的波长会被 ulp 吞掉，波纹变成平顶，
 * 那是预期表现，不是缺陷。
 */
public final class SineDensity implements Density {

    /** 平均地面高度。 */
    private static final double BASE_Y = 64.0;

    /** 两个方向的振幅。 */
    private static final double X_AMPLITUDE = 16.0;

    private static final double Z_AMPLITUDE = 16.0;

    /** 两个方向的波长，故意取不同值，便于分辨 X 与 Z 是否被交换或被镜像。 */
    private static final double X_WAVELENGTH = 64.0;

    private static final double Z_WAVELENGTH = 96.0;

    private static final double X_FREQUENCY = Math.PI * 2.0 / X_WAVELENGTH;

    private static final double Z_FREQUENCY = Math.PI * 2.0 / Z_WAVELENGTH;

    @Override
    public double compute(Int1024 x, Int1024 y, Int1024 z) {
        double surface = BASE_Y + X_AMPLITUDE * Math.sin(x.toDouble() * X_FREQUENCY)
                + Z_AMPLITUDE * Math.sin(z.toDouble() * Z_FREQUENCY);
        return surface - y.toDouble();
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
