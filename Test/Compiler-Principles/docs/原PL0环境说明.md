# PL/0 编译系统实验环境

> 历史环境说明：原文保留供查阅源码来源和早期配置；其中旧目录树、任务名称和统一构建产物描述不再代表当前结构。当前使用方式见 [课程首页](../README.md) 和 [迁移说明](目录迁移说明.md)。

本目录已经配置好一个可直接编译运行的 PL/0 教学编译系统，并加入了一个独立实现的 PL/0 词法分析器。编译系统采用 C 语言实现，能把 PL/0 源程序编译成类 P-code，再由自带解释器执行。环境适用于 Windows + MinGW GCC，并配有 VS Code 一键构建任务。

## 1. 目录结构

```text
Compiler-Principles/
├── include/             PL/0 编译器头文件
├── pl0-compiler/c/src/pl0.c            PL/0 编译器 C 源码
├── lexical-analysis/c/src/pl0lex.c         独立 PL/0 词法分析器 C 源码
├── textbook/            王生原教材风格的 C 版词法分析器
├── pl0-compiler/c/examples/            可直接运行的示例程序
├── test/                PL/0 测试程序与词法测试程序
├── docs/                PL/0 参考资料与实验二讲解文档
├── bin/                 编译生成的 pl0.exe 与 pl0lex.exe
├── build.cmd            一键构建
├── run.cmd              一键运行
├── lexical-analysis\c\run.cmd            一键运行词法分析器
├── lexical-analysis\c-textbook\run.cmd   一键运行教材风格词法分析器
├── verify.cmd           构建并运行示例做验证
├── Makefile             兼容 mingw32-make 的构建文件
└── .vscode/             VS Code 构建与运行任务
```

## 2. 已配置的软件环境

机器上可用的工具如下：

- Windows 10/11 64 位
- MinGW-W64 GCC，检测版本为 gcc 8.1.0
- GNU Make 4.2.1，命令名为 `mingw32-make`
- Visual Studio Code

GCC 已加入 PATH。可以在终端中用下面命令复核：

```text
where gcc
gcc --version
mingw32-make --version
```

## 3. 快速使用

在 `Compiler-Principles` 文件夹下打开终端，然后执行：

```bat
build.cmd
run.cmd pl0-compiler\c\examples\multiply.pl0
lexical-analysis\c\run.cmd lexical-analysis\c\tests\lex_demo.pl0
```

`build.cmd` 会生成 `pl0-compiler\c\bin\pl0.exe` 和 `lexical-analysis\c\bin\pl0lex.exe`。`run.cmd` 会把示例源码编译、列出 P-code 并解释执行；`lexical-analysis\c\run.cmd` 会输出词法分析后的 Token 表。

也可以直接手动编译和运行：

```bat
gcc -std=c11 -Wall -Wextra -Iinclude -o pl0-compiler\c\bin\pl0.exe pl0-compiler\c\src\pl0.c
gcc -std=c11 -Wall -Wextra -Werror -Iinclude -o lexical-analysis\c\bin\pl0lex.exe lexical-analysis\c\src\pl0lex.c
pl0-compiler\c\bin\pl0.exe pl0-compiler\c\examples\tests.pl0
lexical-analysis\c\bin\pl0lex.exe lexical-analysis\c\tests\lex_demo.pl0
```

使用 Make：

```bat
mingw32-make -f Makefile
mingw32-make -f Makefile run
```

## 4. 在 VS Code 中使用

1. 用 VS Code 打开 `Compiler-Principles` 文件夹。
2. 按 `Ctrl+Shift+B` 执行“PL/0: build all”，同时编译编译器和词法分析器。
3. 在“终端 -> 运行任务”中选择：
   - `PL/0: run multiply example`
   - `PL/0: run current file`
   - `PL/0: run lexer demo`
   - `PL/0: lex current file`

如果以后改动的是 `pl0-compiler/c/src/pl0.c`、`pl0-compiler/c/include/pl0.h` 或 `lexical-analysis/c/src/pl0lex.c`，运行任务会自动先重新编译。

## 5. 当前 PL/0 教学版的特点

保留字：

```text
begin call const do end if odd procedure then var while
```

常用语法要点：

- 常量定义：`const m = 7, n = 85;`
- 变量定义：`var x, y, z;`
- 赋值符号：`:=`
- 注释：`/* ... */`
- 标识符不超过 10 个字符
- 分程序嵌套层数不超过 3

以 `pl0-compiler\c\tests\example.pl0` 为例：

```pascal
const limit = 6;
var i, total;
begin
    total := 0;
    i := 1;
    while i <= limit do
    begin
        total := total + i;
        i := i + 1
    end
end.
```

这个原始教学实现没有 `read/write` 保留字；解释器每次执行 `sto` 存储指令时会打印栈顶值，因此可以观察到程序运行中的赋值结果。老师后续如果要求添加 `read/write`，通常是在词法、语法、语义和 P-code 解释器上做小范围扩充。

注意：独立词法分析器 `pl0lex.exe` 使用 PL/0 课程中更常见的 13 个保留字表，额外包含 `read` 和 `write`，方便老师实验里采用带输入输出的 PL/0 文法。

## 6. 自己编写和运行 PL/0 程序

1. 在 `test` 目录新建 `my.pl0`。
2. 执行 `build.cmd`。
3. 执行 `run.cmd test\my.pl0`。

编译过程中若出现语法错误，程序会打印错误位置编号，并且不再进入解释执行。`pl0-compiler\c\tests\undeclared.pl0` 是一个故意写错、用于观察错误处理的示例：

```bat
pl0-compiler\c\bin\pl0.exe pl0-compiler\c\tests\undeclared.pl0
```

## 7. 源码说明

编译系统的源码结构分为：

- 词法分析：`getsym()`
- 递归下降语法分析：`block()`、`statement()`、`condition()`、`expression()`、`term()`、`factor()`
- 符号表：`enter()`、`position()`
- 代码生成与列表：`gen()`、`listcode()`
- P-code 解释执行：`interpret()`

详细的中文参考资料放在 `references\pl0.pdf`。源码整理自中国科学技术大学编译原理课程的 PL/0 Project Source Tree（Yu Zhang），在本项目中做了 Windows/MinGW 适配、去除非必要输出并补充中文使用说明，仅供课程学习使用。

## 8. 实验二：独立词法分析器

`lexical-analysis/c/src/pl0lex.c` 是独立于 `pl0.c` 词法分析程序，核心函数为 `next_token()`。它不依赖原编译器，可对任意 PL/0 源文件输出 Token 表。

```bat
build.cmd
lexical-analysis\c\bin\pl0lex.exe lexical-analysis\c\tests\lex_demo.pl0
lexical-analysis\c\bin\pl0lex.exe lexical-analysis\c\tests\lex_errors.pl0
```

程序实现的功能与实验要求对应：

- 标识符、整常数、关键字和特殊符号分别以 `IDENTIFIER`、`INTEGER`、`KEYWORD`、`SPECIAL` 分类输出
- 先按完整单词查保留字表，能区分标识符和保留字
- 保留字按大小写不敏感识别（如 `BEGIN` 和 `begin` 都算关键字），输出时保留源程序原文
- 自动跳过空格、制表符、换行和 `/* ... */` 注释
- 能报告非法字符、超长标识符、超长整数、数字后紧跟字母、单独 `:` 以及未闭合注释等词法错误
- 输出中包含每个 Token 的行号、列号，便于定位源程序位置

三个测试文件：

- `lexical-analysis/c/tests/lex_demo.pl0`：无词法错误的完整 PL/0 示例，输出应显示 `0 lexical error(s)`
- `lexical-analysis/c/tests/lex_boundary.pl0`：大小写保留字、10 字符标识符和 14 位整数的边界测试，输出应显示 `0 lexical error(s)`
- `lexical-analysis/c/tests/lex_errors.pl0`：故意包含多种词法错误的示例，输出应显示 `6 lexical error(s)`

词法分析器执行完后如果发现错误，会以非零退出码结束，方便在批处理和 VS Code 中判断结果。

录视频前可以先看 [实验二保姆级讲解](../lexical-analysis/c/docs/实验二保姆级讲解.md)，里面包含源文件说明、三个测试案例的运行方法、照着念的录制稿，并按 A、B、C 三个等级覆盖 `pl0lex.c` 中全部函数的解释。

## 9. 教材风格版本（王生原、董渊 C 语言版）

教材风格版本放在 `textbook` 目录，核心源码是：

- `lexical-analysis/c-textbook/src/pl0_lexer.c`
- `lexical-analysis/c-textbook/include/pl0_t.h`
- `lexical-analysis/c-textbook/tests/basic.pl0`
- `lexical-analysis/c-textbook/tests/read_write.pl0`
- `lexical-analysis/c-textbook/tests/errors.pl0`

这个版本保留了教材 C 版的主要结构，包括 `enum symbol`、13 个保留字、`read/write`、`getch()`、`getsym()`、`error()`、`#` 不等号、源码行回显和 `****...!n` 错误格式。

运行：

```bat
build.cmd
lexical-analysis\c-textbook\run.cmd lexical-analysis\c-textbook\tests\basic.pl0
lexical-analysis\c-textbook\bin\pl0_textbook.exe lexical-analysis\c-textbook\tests\read_write.pl0
lexical-analysis\c-textbook\bin\pl0_textbook.exe lexical-analysis\c-textbook\tests\errors.pl0
```

详细说明见 [lexical-analysis/c-textbook/README.md](../lexical-analysis/c-textbook/README.md)，录制视频时使用 [实验二教材版保姆级讲解](../lexical-analysis/c-textbook/docs/实验二教材版保姆级讲解.md)。

如果只是不熟悉 C 语言文件操作，先读 [C语言文件操作精讲](../lexical-analysis/c-textbook/docs/C语言文件操作精讲.md)，再读 [教材版逐行精讲](../lexical-analysis/c-textbook/docs/教材版逐行精讲.md)。完整的 [C语言零基础预备课](../lexical-analysis/c-textbook/docs/C语言零基础预备课.md) 作为可选补充保留。
