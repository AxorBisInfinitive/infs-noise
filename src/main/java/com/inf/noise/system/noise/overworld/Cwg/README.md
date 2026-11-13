# CubicWorldGen 噪声 / CwgNoiseSystem

## 实现

本实现包含三个类：

- `CwgNoise`：调用 `FlowNoise`，并配置坐标折叠。
- `CwgDensity`：负责地形密度计算。
- `CwgNoiseSystem`：负责参数配置，以及与现代 Minecraft 密度函数系统的集成。

与原始实现不同，**Height** 和 **Volatility** 由 Minecraft 的地形样条函数提供。

## 致谢

本实现基于 [CubicWorldGen](https://github.com/OpenCubicChunks/CubicWorldGen) 修改得来。

原始 CubicWorldGen 许可证保留在 [LICENSE.md](LICENSE.md) 中。
