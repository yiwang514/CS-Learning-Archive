# 编译原理实验

这里集中保存编译原理课程的实验。目录按实验主题划分，而不按实验编号划分，避免不同课程安排中的编号冲突。

## 从哪里开始

| 实验 | 版本与入口 | 提交的核心源码 |
|---|---|---|
| PL/0 编译系统 | [C 版](pl0-compiler/c/README.md) | [pl0.c](pl0-compiler/c/src/pl0.c) 与 [pl0.h](pl0-compiler/c/include/pl0.h) |
| PL/0 词法分析 | [Java 版（主用）](lexical-analysis/java/README.md) | [Pl0Lexer.java](lexical-analysis/java/src/Pl0Lexer.java) |
| PL/0 词法分析 | [C 独立版](lexical-analysis/c/README.md) | [pl0lex.c](lexical-analysis/c/src/pl0lex.c) |
| PL/0 词法分析 | [C 教材风格版](lexical-analysis/c-textbook/README.md) | [pl0_lexer.c](lexical-analysis/c-textbook/src/pl0_lexer.c) 与 [pl0_t.h](lexical-analysis/c-textbook/include/pl0_t.h) |
| NFA → DFA（子集构造法） | [Java 版](nfa-to-dfa/java/README.md) | [NfaToDfa.java](nfa-to-dfa/java/src/NfaToDfa.java) |

## 目录约定

```text
Compiler-Principles/
  pl0-compiler/c/             PL/0 编译、代码生成与解释执行
  lexical-analysis/
    c/                       独立 C 词法分析器
    c-textbook/              教材风格 C 词法分析器
    java/                    Java 词法分析器
  nfa-to-dfa/java/            Java 子集构造法
  references/pl0.pdf         PL/0 参考资料
  docs/                      目录迁移记录、历史环境说明
  .vscode/                   全课程构建与运行任务
  build.cmd                  编译所有实验
  verify.cmd                 验证所有实验
  Makefile                   Windows Make 入口
```

每个版本独立保存 `src/`（源码）、`tests/`（测试输入/测试程序）、`docs/`（讲解文档，有则保留），以及 `build.cmd`、`run.cmd`、`verify.cmd`。
C 版本的头文件放在 `include/`，可执行文件生成到自己的 `bin/`；Java 编译结果生成到自己的 `classes/`。编译产物不上传 GitHub。

## 运行 Java 实验

在本目录打开 PowerShell：

```powershell
# PL/0 词法分析
.\lexical-analysis\java\run.cmd lexical-analysis\java\tests\read_write.pl0
.\lexical-analysis\java\verify.cmd

# NFA 转 DFA
.\nfa-to-dfa\java\run.cmd nfa-to-dfa\java\tests\epsilon_chain.txt
.\nfa-to-dfa\java\verify.cmd
```

运行脚本会先编译源码。传入的文件路径相对于终端当前目录；路径有空格时加英文双引号。
NFA 的运行脚本不传文件名时读取标准输入；Java 词法分析器不传文件名时提示输入源文件名。

## 编译与验证全部实验

需要 JDK（验证时使用 21），以及 MinGW GCC；使用 Make 入口时另需 `mingw32-make`。工具应已加入 PATH。

```powershell
.\build.cmd
.\verify.cmd
# 可选的 Make 入口
mingw32-make
mingw32-make verify
```

只有 JDK 时，直接使用各 Java 目录内的脚本，不必安装 GCC。所有脚本均可在课程根目录或对应实验目录调用，不会改变调用者的工作目录。

VS Code 请打开整个 `Compiler-Principles` 文件夹。默认构建任务编译全部实验，其他任务可按实验单独选择，也保留了对当前 PL/0 文件的分析任务。

## 学习文档

- [Java 词法分析逐函数讲解](lexical-analysis/java/docs/Pl0Lexer代码讲解.md)
- [Java 词法分析视频口播稿](lexical-analysis/java/docs/讲解视频口播稿.md)
- [NFA 转 DFA 使用与设计说明](nfa-to-dfa/java/docs/NFA转DFA使用说明.md)
- [C 文件操作精讲](lexical-analysis/c-textbook/docs/C语言文件操作精讲.md)
- [目录迁移对照表](docs/目录迁移说明.md)

后续新增实验时，在课程根目录增加一个主题目录，再按实现语言划分即可。新增实验如果需要参与全量编译与验证，再登记到根目录脚本和 VS Code 任务中。
