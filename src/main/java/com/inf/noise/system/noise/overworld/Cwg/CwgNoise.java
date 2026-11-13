package com.inf.noise.system.noise.overworld.Cwg;

import java.util.Random;

import com.inf.noise.util.math.flow.noise.FlowNoise;

/** CWG 特定的梯度表、种子折叠与输出归一化。 */
public final class CwgNoise {
    /** 256 行梯度表，每行 4 个分量，只用前三个。构造后只读。 */
    private static final double[] GRADIENTS = new double[256 * 4];

    static {
        int[] gradX = { 1, -1, 1, -1, 1, -1, 1, -1, 0, 0, 0, 0, 1, 0, -1, 0 };
        int[] gradY = { 1, 1, -1, -1, 0, 0, 0, 0, 1, -1, 1, -1, 1, -1, 1, -1 };
        int[] gradZ = { 0, 0, 0, 0, 1, 1, -1, -1, 1, 1, -1, -1, 0, 1, 0, -1 };
        Random random = new Random(123456789);
        for (int i = 0; i < 256; i++) {
            int j = random.nextInt(gradX.length);
            GRADIENTS[i * 4] = gradX[j] / 2.0;
            GRADIENTS[i * 4 + 1] = gradY[j] / 2.0;
            GRADIENTS[i * 4 + 2] = gradZ[j] / 2.0;
        }
    }

    private final FlowNoise noise;
    private final double normalization;

    public CwgNoise(double frequencyX, double frequencyY, double frequencyZ, int octaves, int seed,
            boolean foldCoordinates) {
        this.noise = new FlowNoise(GRADIENTS,
                new FlowNoise.Parameters(frequencyX, frequencyY, frequencyZ,
                        octaves, seed, foldCoordinates, 0.5, 2.0, 1.0, 0.5));
        this.normalization = 2.0 / FlowNoise.maxValue(octaves, 0.5);
    }

    public static int foldSeed(long seed) {
        return (int) ((seed & 0xFFFFFFFFL) ^ (seed >>> 32));
    }

    public double normalized(double x, double y, double z) {
        return this.noise.sample(x, y, z) * this.normalization - 1.0;
    }
}
