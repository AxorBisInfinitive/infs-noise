package com.inf.noise.util.math.flow.noise;

import java.util.Objects;

/**
 * 基于 Flow Noise / libnoise 算法结构的三维梯度噪声数学工具。
 *
 * <p>
 * 不依赖 Minecraft、区块、群系或密度函数。梯度表由调用方提供；本类只实现
 * 格点哈希、梯度点积、三次 S 曲线插值、坐标折叠和多八度求和。
 *
 * <p>
 * 与原始 Flow Noise 并不保证数值等价：梯度表、梯度缩放及偏移均可配置。
 * 格点定位保留 Java double 到 int 的饱和转换和 int 运算的回绕语义。
 *
 * <p>
 * 算法来源及许可证参见本目录 README.md 和 LICENSE.md。
 */
public final class FlowNoise {
    private static final double FOLD_LIMIT = 1073741824.0;
    private static final int X_NOISE_GEN = 1619;
    private static final int Y_NOISE_GEN = 31337;
    private static final int Z_NOISE_GEN = 6971;
    private static final int SEED_NOISE_GEN = 1013;
    private static final int SHIFT_NOISE_GEN = 8;

    /**
     * 统一管理噪声的数学参数。
     *
     * @param frequencyX      X 轴频率（允许为 0）
     * @param frequencyY      Y 轴频率（允许为 0，适合二维噪声）
     * @param frequencyZ      Z 轴频率（允许为 0）
     * @param octaves         八度数
     * @param seed            32 位噪声种子
     * @param foldCoordinates 是否按 Flow Noise 的规则折叠坐标
     * @param persistence     每阶振幅乘数（CWG 为 0.5）
     * @param lacunarity      每阶频率乘数（CWG 为 2.0）
     * @param gradientScale   梯度点积的乘数（CWG 为 1.0）
     * @param gradientOffset  梯度点积的偏移（CWG 为 0.5）
     */
    public record Parameters(double frequencyX, double frequencyY, double frequencyZ,
            int octaves, int seed, boolean foldCoordinates,
            double persistence, double lacunarity,
            double gradientScale, double gradientOffset) {
    }

    /** 256 行 × 4 个 double，每行前三项为梯度向量；最后一项为填充位。 */
    private final double[] gradients;
    private final Parameters parameters;

    /** 创建独立的噪声实例；只在构造时复制一次梯度表，之后可安全共享给不同线程。 */
    public FlowNoise(double[] gradients, Parameters parameters) {
        Objects.requireNonNull(gradients, "gradients");
        if (gradients.length != 256 * 4) {
            throw new IllegalArgumentException("gradients must contain 256 groups of 4 doubles");
        }
        this.gradients = gradients.clone();
        this.parameters = Objects.requireNonNull(parameters, "parameters");
        if (parameters.octaves() < 1) {
            throw new IllegalArgumentException("octaves must be positive");
        }
    }

    /** 未归一化的多八度和，保持原算法的运算顺序。 */
    public double sample(double x, double y, double z) {
        double x1 = x * this.parameters.frequencyX();
        double y1 = y * this.parameters.frequencyY();
        double z1 = z * this.parameters.frequencyZ();
        double value = 0.0;
        double persistence = 1.0;
        for (int octave = 0; octave < this.parameters.octaves(); octave++) {
            double nx = this.parameters.foldCoordinates() ? fold(x1) : x1;
            double ny = this.parameters.foldCoordinates() ? fold(y1) : y1;
            double nz = this.parameters.foldCoordinates() ? fold(z1) : z1;
            value += coherent(nx, ny, nz, this.parameters.seed() + octave) * persistence;
            x1 *= this.parameters.lacunarity();
            y1 *= this.parameters.lacunarity();
            z1 *= this.parameters.lacunarity();
            persistence *= this.parameters.persistence();
        }
        return value;
    }

    /** 八度振幅几何级数之和；保持 CWG 使用的闭式求值顺序。 */
    public static double maxValue(int octaves, double persistence) {
        if (persistence == 1.0) {
            return octaves;
        }
        return (Math.pow(persistence, octaves) - 1.0) / (persistence - 1.0);
    }

    /** 对外暴露的坐标折叠规则，保留原始公式。 */
    public static double fold(double n) {
        if (n >= FOLD_LIMIT) {
            return (2.0 * n % FOLD_LIMIT) - FOLD_LIMIT;
        }
        if (n <= -FOLD_LIMIT) {
            return (2.0 * n % FOLD_LIMIT) + FOLD_LIMIT;
        }
        return n;
    }

    /** 八角点梯度噪声，以三次 S 曲线控制三线性插值。 */
    private double coherent(double x, double y, double z, int seed) {
        int x0 = x > 0.0 ? (int) x : (int) x - 1;
        int y0 = y > 0.0 ? (int) y : (int) y - 1;
        int z0 = z > 0.0 ? (int) z : (int) z - 1;
        int x1 = x0 + 1;
        int y1 = y0 + 1;
        int z1 = z0 + 1;

        double xs = sCurve3(x - x0);
        double ys = sCurve3(y - y0);
        double zs = sCurve3(z - z0);

        double n0 = gradient(x, y, z, x0, y0, z0, seed);
        double n1 = gradient(x, y, z, x1, y0, z0, seed);
        double ix0 = lerp(n0, n1, xs);
        n0 = gradient(x, y, z, x0, y1, z0, seed);
        n1 = gradient(x, y, z, x1, y1, z0, seed);
        double ix1 = lerp(n0, n1, xs);
        double iy0 = lerp(ix0, ix1, ys);
        n0 = gradient(x, y, z, x0, y0, z1, seed);
        n1 = gradient(x, y, z, x1, y0, z1, seed);
        ix0 = lerp(n0, n1, xs);
        n0 = gradient(x, y, z, x0, y1, z1, seed);
        n1 = gradient(x, y, z, x1, y1, z1, seed);
        ix1 = lerp(n0, n1, xs);
        double iy1 = lerp(ix0, ix1, ys);
        return lerp(iy0, iy1, zs);
    }

    /** 单角点梯度打分：哈希溢出按 int32 回绕，梯度由调用方确定。 */
    private double gradient(double fx, double fy, double fz, int ix, int iy, int iz, int seed) {
        int index = X_NOISE_GEN * ix + Y_NOISE_GEN * iy + Z_NOISE_GEN * iz + SEED_NOISE_GEN * seed;
        index ^= index >> SHIFT_NOISE_GEN;
        index &= 0xFF;
        double value = this.gradients[index * 4] * (fx - ix)
                + this.gradients[index * 4 + 1] * (fy - iy)
                + this.gradients[index * 4 + 2] * (fz - iz);
        // scale=1 时不增加运算，避免改变 CWG 原有舍入路径。
        if (this.parameters.gradientScale() != 1.0) {
            value *= this.parameters.gradientScale();
        }
        return value + this.parameters.gradientOffset();
    }

    private static double sCurve3(double a) {
        return a * a * (3.0 - 2.0 * a);
    }

    private static double lerp(double a, double b, double t) {
        return (1.0 - t) * a + t * b;
    }
}
