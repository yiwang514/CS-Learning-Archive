# PL/0 编译系统（C）

本实验包括词法分析、递归下降语法分析、符号表、P-code 生成与解释执行，不是仅输出 Token 的独立词法分析器。

- 源码：[src/pl0.c](src/pl0.c)、[include/pl0.h](include/pl0.h)。
- 可运行示例：[examples/](examples/)。
- 测试输入：[tests/example.pl0](tests/example.pl0)、[tests/undeclared.pl0](tests/undeclared.pl0)，后者故意使用未声明变量。
- 参考资料：[pl0.pdf](../../references/pl0.pdf)。

进入本目录执行：

```powershell
.\build.cmd
.\run.cmd
.\run.cmd examples\factorial.pl0
.\verify.cmd
```

需要 MinGW GCC。默认示例为 `examples/multiply.pl0`，运行后列出 P-code 并解释执行；验证脚本检查该示例执行完成。
编译结果为 `bin/pl0.exe`。

核心源码整理自中国科学技术大学编译原理课程的 PL/0 Project Source Tree（Yu Zhang），此前做过 Windows/MinGW 适配，仅供课程学习使用。原环境说明保留在 [历史说明](../../docs/原PL0环境说明.md)。
