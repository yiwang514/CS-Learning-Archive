# PL/0 词法分析器（Java）

提交源码：[src/Pl0Lexer.java](src/Pl0Lexer.java)。其他 `.class` 文件由内部类自动编译产生，不是额外需要编写的源文件。

本版本识别 13 个小写关键字（含 `read/write`），使用 `#` 表示不等号，跳过空白和注释，输出 `Word / Category / Value` Token 表并检查词法错误。

进入本目录执行：

```powershell
.\build.cmd
.\run.cmd tests\basic.pl0
.\run.cmd tests\read_write.pl0
.\run.cmd tests\errors.pl0
.\verify.cmd
```

需要 JDK，源码与输入使用 UTF-8。不传参数时提示输入源文件名。脚本每次运行前重新编译，编译结果放在 `classes/`。
文件参数相对于终端当前目录，带空格的路径加英文双引号。

| 测试 | 重点 |
|---|---|
| [basic.pl0](tests/basic.pl0) | 声明、表达式、普通 Token |
| [read_write.pl0](tests/read_write.pl0) | READSYM、WRITESYM、NEQ |
| [errors.pl0](tests/errors.pl0) | 故意触发 !19、!30、!36、!37、!38、!39，退出码非零 |

## 阅读与录制

- [逐函数代码讲解](docs/Pl0Lexer代码讲解.md)：每个函数的作用、调用关系、if/while 条件及原因。
- [视频口播稿](docs/讲解视频口播稿.md)：录制准备、演示顺序、讲解和结束语。

推荐阅读顺序：`main → run → getch → getsym → 各类 read 方法 → recordCurrentToken → printTokens`。
NFA 转 DFA 已移到独立的 [nfa-to-dfa/java](../../nfa-to-dfa/java/README.md)，不再与词法分析器混放。
