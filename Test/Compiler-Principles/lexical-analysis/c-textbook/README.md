# 王生原教材 C 版 PL/0 词法分析器

> 目录已整理：文中的课程根目录指 `Compiler-Principles`。完整操作入口以本实验 README 为准；源码与测试规则未改变。

这个目录是单独新建的教材风格版本，原来的 `lexical-analysis/c/src/pl0lex.c` 保持不变。

如果准备录制讲解视频，先看 [实验二教材版保姆级讲解](docs/实验二教材版保姆级讲解.md)。

如果 C 语言基础比较薄弱，按下面顺序阅读：

1. [C语言文件操作精讲](docs/C语言文件操作精讲.md)
2. [教材版逐行精讲](docs/教材版逐行精讲.md)
3. [实验二教材版保姆级讲解](docs/实验二教材版保姆级讲解.md)

[C语言零基础预备课](docs/C语言零基础预备课.md) 仍然保留，但如果你已经会变量、循环、数组和函数，只需要看文件操作精讲。

如果老师要求上传实验二的 C 语言源文件，这个版本上传：

```text
lexical-analysis/c-textbook/src/pl0_lexer.c
```

配套头文件是：

```text
lexical-analysis/c-textbook/include/pl0_t.h
```

源码使用 `#include "pl0_t.h"`，编译脚本通过 `-Iinclude` 指定本版本的头文件目录，避免误用完整编译系统中的 `pl0.h`。

## 1. 复刻依据

本版本按王生原、董渊等编著的《编译原理（第 3 版）》C 语言案例的结构重新实现，主要保留了下面这些教材特征：

- `norw = 13`，保留字包含 `read` 和 `write`
- `nmax = 14`，整数最多 14 位
- `al = 10`，标识符最长 10 个字符
- 使用 `enum symbol` 定义符号种别
- 使用 `word`、`wsym`、`ssym` 三张表
- 使用 `getch()` 按行读取并回显源程序
- 使用独立过程 `getsym()` 完成词法分析
- 使用 `error()` 输出 `****...!n` 格式的错误位置
- 不等号使用教材 C 版本中的 `#`
- 关键字采用小写形式

本实现只保留词法分析，不包含语法分析、代码生成和虚拟机部分。

## 2. 教材式输出

程序先按教材方式回显源代码。每行前面的数字是教材 `getch()` 中的代码地址输出，本版本只做词法分析，所以始终为 0：

```text
Input pl/0 file?   lexical-analysis/c-textbook/tests/basic.pl0
0 const m = 7, n = 85;

0 var x, y, z;

0 begin

...
```

词法分析完成后输出 Token 表：

```text
Word                Category            Value
const               KEYWORD             CONSTSYM
m                   IDENTIFIER          m
=                   SPECIAL             EQL
7                   INTEGER             7
;                   SPECIAL             SEMICOLON
```

三类信息和教材符号名的对应关系：

- `KEYWORD` 对应 `BEGINSYM`、`CONSTSYM`、`READSYM`、`WRITESYM` 等保留字符号
- `IDENTIFIER` 对应 `IDENT`
- `INTEGER` 对应 `NUMBER`
- `SPECIAL` 对应 `PLUS`、`BECOMES`、`LEQ`、`NEQ` 等特殊符号

## 3. 构建与运行

在 `Compiler-Principles` 目录执行：

```bat
lexical-analysis\c-textbook\build.cmd
lexical-analysis\c-textbook\run.cmd lexical-analysis\c-textbook\tests\basic.pl0
```

也可以直接运行：

```bat
lexical-analysis\c-textbook\bin\pl0_textbook.exe lexical-analysis\c-textbook\tests\basic.pl0
lexical-analysis\c-textbook\bin\pl0_textbook.exe lexical-analysis\c-textbook\tests\read_write.pl0
lexical-analysis\c-textbook\bin\pl0_textbook.exe lexical-analysis\c-textbook\tests\errors.pl0
```

如果直接运行 `lexical-analysis\c-textbook\bin\pl0_textbook.exe`，程序会像教材一样提示：

```text
Input pl/0 file?
```

然后输入 PL/0 源程序文件名。

## 4. 三个测试案例

### `lexical-analysis/c-textbook/tests/basic.pl0`

普通声明和表达式测试，包含：

```text
const var begin := + end .
```

预期结果：正常输出 Token 表，没有词法错误。

### `lexical-analysis/c-textbook/tests/read_write.pl0`

输入输出与教材不等号测试，包含：

```text
read write while # do
```

预期结果：`read` 输出 `READSYM`，`write` 输出 `WRITESYM`，`#` 输出 `NEQ`。

### `lexical-analysis/c-textbook/tests/errors.pl0`

错误检测测试，包含：

- 数字后跟字母
- 超过 14 位的整数
- 非法字符 `@`
- 没有写 `=` 的单独冒号
- 超过 10 个字符的标识符
- 没有闭合的 `/*` 注释

这些测试会输出教材式错误标记和 Token 表中的 `ERROR` 行。

## 5. 与原始教材代码的区别

为了满足本次词法分析实验的全部要求，本版本做了下面三项补充：

1. 保留教材源码不能单独输出 Token 的问题，在分析结束后统一打印 `Word / Category / Value` 表。
2. 增加 `/* ... */` 注释跳过功能。
3. 增加词法错误记录。教材原有错误码 `30` 和 `19` 保持不变，另外使用：
   - `36`：标识符超过 10 个字符
   - `37`：非法字符
   - `38`：注释没有闭合
   - `39`：数字后紧跟字母

除这些补充外，主流程仍然是 `init()`、`getch()`、`getsym()`、`error()`，没有加入语法分析代码。
