# PL/0 独立词法分析器（C）

源码：[src/pl0lex.c](src/pl0lex.c)。它独立于完整编译系统，核心过程为 `next_token()`，输出 Token 的行号、列号、分类与内容。

进入本目录执行：

```powershell
.\build.cmd
.\run.cmd tests\lex_demo.pl0
.\verify.cmd
```

需要 MinGW GCC，生成 `bin/pl0lex.exe`。不传文件名时运行默认示例。

| 测试 | 内容 | 预期 |
|---|---|---|
| [lex_demo.pl0](tests/lex_demo.pl0) | 普通 PL/0 示例 | 0 个词法错误 |
| [lex_boundary.pl0](tests/lex_boundary.pl0) | 大小写关键字、长度边界 | 0 个词法错误 |
| [lex_errors.pl0](tests/lex_errors.pl0) | 非法字符、过长单词等 | 6 个词法错误，非零退出码 |

本版本关键字不区分大小写，使用 `<>` 表示不等号；不要与教材风格版和 Java 版的规则混用。
完整讲解：[实验二保姆级讲解](docs/实验二保姆级讲解.md)。
