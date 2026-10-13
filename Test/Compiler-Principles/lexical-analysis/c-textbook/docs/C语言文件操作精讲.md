# C 语言文件操作精讲：结合教材版 PL/0 代码

> 目录已整理：文中的课程根目录指 `Compiler-Principles`。完整操作入口以本实验 README 为准；源码与测试规则未改变。

你已经会 C 语言的基本语法，这份文档只专注解决文件操作。

本文对应源码：

```text
lexical-analysis/c-textbook/src/pl0_lexer.c
```

重点看这些内容：

```text
FILE *fin
fopen()
NULL
fgetc()
EOF
input_finished
fclose()
line[]、cc、ll
```

## 1. 为什么词法分析器需要文件操作

PL/0 源程序通常保存成文件，例如：

```text
lexical-analysis/c-textbook/tests/basic.pl0
```

程序运行时，源程序不在变量里，而在硬盘文件中。

所以必须先：

1. 找到文件
2. 打开文件
3. 一个字符一个字符读取
4. 判断文件什么时候结束
5. 用完以后关闭文件

这就是代码中文件操作的全部任务。

## 2. 屏幕输入和文件输入的区别

你已经熟悉的可能是：

```c
printf("hello\n");
scanf("%d", &x);
```

它们处理的是：

```text
printf -> 标准输出，一般是屏幕
scanf  -> 标准输入，一般是键盘
```

现在要读取的是硬盘上的 `.pl0` 文件，所以不能直接用 `scanf` 一个字符一个字符读。

C 语言为此提供了 `FILE` 文件操作接口：

```c
FILE *fin;
fin = fopen("test.pl0", "r");
```

## 3. `FILE *` 到底是什么

代码里有：

```c
FILE *fin;
```

这里的 `FILE` 不是你写的普通结构体类型，而是 C 标准库提供的文件类型。

`FILE *fin` 可以理解成一张“文件操作凭证”：

```text
fin
 |
 +--> 操作系统知道它对应哪一个文件
```

`fin` 本身不保存整个文件内容，它只是一个句柄。你通过它告诉标准库：

```text
从这个文件读一个字符
关闭这个文件
```

可以把它类比成电视遥控器：

- 遥控器不是电视本身
- 但可以通过遥控器控制电视

`fin` 不是文件内容本身，但可以通过它操作文件。

## 4. 打开文件的完整调用

项目里的代码：

```c
fin = fopen(fname, "r");
if (fin == NULL) {
    printf("Can't open file!\n");
    return 1;
}
```

拆开看：

```c
fopen(fname, "r")
```

- `fopen`：打开文件
- `fname`：文件名
- `"r"`：只读模式
- 返回值：`FILE *`

打开成功后：

```text
fin 指向文件
```

打开失败后：

```text
fin 等于 NULL
```

## 5. 为什么一定要检查 `NULL`

文件可能不存在，例如：

```text
lexical-analysis/c-textbook/tests/no_such_file.pl0
```

也可能路径写错：

```text
lexical-analysis/c-textbook/tests/basic.pl0.txt
```

还可能没有读取权限。

如果打开失败，`fopen` 返回 `NULL`：

```c
if (fin == NULL) {
    printf("Can't open file!\n");
    return 1;
}
```

如果省略检查，后面执行：

```c
fgetc(fin);
```

就会出现未定义行为，程序可能直接崩溃。

所以文件操作的第一条规则是：

```text
fopen 之后必须判断返回值是不是 NULL
```

## 6. `"r"`、`"w"`、`"a"` 模式

```c
fopen(fname, "r")
```

第二个参数是打开模式。

| 模式 | 含义 |
| --- | --- |
| `"r"` | 只读，文件必须存在 |
| `"w"` | 只写，文件不存在就创建，存在就清空 |
| `"a"` | 追加写入，从文件末尾继续写 |
| `"rb"` | 二进制只读 |
| `"wb"` | 二进制只写 |

本程序只读取 PL/0 源文件，所以使用 `"r"`。

`"r"` 不会修改源程序。

## 7. `fgetc()` 一次读一个字符

项目里的代码：

```c
int c = fgetc(fin);
```

含义：

```text
从 fin 指向的文件读取一个字符
把读到的内容返回
```

假设文件内容是：

```text
const
```

连续调用 `fgetc` 会依次返回：

```text
'c'
'o'
'n'
's'
't'
'\n'
```

## 8. 为什么 `fgetc` 的返回值是 `int`，不是 `char`

这是文件操作里很重要的细节。

函数声明可以简化理解成：

```c
int fgetc(FILE *stream);
```

它返回 `int` 的原因是：正常字符和文件结束需要区分。

普通字符使用 0 到 255。

文件结束时返回：

```c
EOF
```

`EOF` 通常是：

```c
-1
```

如果返回值使用 `char`，某些平台上 `char` 也可能表示 `-1`，就无法可靠区分“文件结束”和“某个特殊字符”。

所以项目里写：

```c
int c = fgetc(fin);
```

而不是：

```c
char c = fgetc(fin);
```

## 9. 为什么读完以后又要转成 `char`

```c
ch = (char)c;
```

因为 `c` 是 `int`，而后续词法分析要按字符处理。

流程是：

```text
fgetc 返回 int
        |
        v
判断是不是 EOF
        |
        v
转成 char，放入 ch
```

这一步只在已经确认不是 `EOF` 后执行。

## 10. 项目里完整的读取循环

```c
while (1) {
    int c = fgetc(fin);
    if (c == EOF) {
        input_finished = 1;
        break;
    }
    ch = (char)c;
    printf("%c", ch);
    if (ll < 80) {
        line[ll++] = ch;
    }
    if (ch == '\n') {
        break;
    }
}
```

逐行解释：

### `while (1)`

条件永远为真，表示要一直读。

### `int c = fgetc(fin);`

读一个字符。

### `if (c == EOF)`

判断文件是否结束。

### `input_finished = 1;`

记录“文件已经结束”。

### `break;`

跳出当前读取循环。

### `ch = (char)c;`

把读到的整数转成字符。

### `printf("%c", ch);`

把字符显示到屏幕，形成教材式源码回显。

### `line[ll++] = ch;`

把字符保存到当前行数组。

### `if (ch == '\n')`

如果读到换行，表示当前行读取完成，跳出循环。

## 11. `EOF` 是什么

`EOF` 是 End Of File 的缩写。

它的意思是：

```text
文件已经读完，没有下一个字符了
```

它不是文件里的一个真实字符。

例如文件内容是：

```text
abc
```

调用顺序是：

```text
第 1 次 fgetc -> 'a'
第 2 次 fgetc -> 'b'
第 3 次 fgetc -> 'c'
第 4 次 fgetc -> EOF
```

## 12. 为什么不要随便写 `while (!feof(fin))`

很多初学者会写：

```c
while (!feof(fin)) {
    c = fgetc(fin);
}
```

这种写法容易多处理一次数据，因为 `feof()` 只有在读取尝试失败之后才会变成真。

更稳妥的写法是：

```c
while ((c = fgetc(fin)) != EOF) {
    处理 c
}
```

本项目为了同时按行回显源代码，在 `getch()` 中使用的是：

```c
if (c == EOF) {
    input_finished = 1;
    break;
}
```

这样逻辑更直观。

## 13. `line`、`cc`、`ll` 不是标准库的文件缓冲区

项目里还有：

```c
char line[81];
int cc;
int ll;
```

它们是程序自己写的“行缓冲”。

标准库内部也会有文件缓冲，但那部分由 `FILE *` 自己管理。

本项目的逻辑另外做了一层：

```text
硬盘文件
  |
  | fgetc
  v
line 行数组
  |
  | 通过 cc 取字符
  v
getsym 词法分析
```

### `ll`

当前行一共有多少个字符。

### `cc`

当前已经读到当前行的哪个位置。

示意：

```text
line: 'c' 'o' 'n' 's' 't' ' ' 'm' '\n' ' '
下标:  0   1   2   3   4   5   6   7   8
cc:    当前下标
ll:    9
```

每次：

```c
ch = line[cc];
cc++;
```

就把 `cc` 往后移动一格。

当：

```c
cc == ll
```

表示这一行已经取完，需要再次调用 `fgetc` 读下一行。

## 14. 为什么读到换行还要在行末加空格

代码：

```c
if (ll < 80) {
    line[ll++] = ' ';
}
```

这是为了在行末留一个空白字符。

词法分析器在行尾处理字符时，多出的空格可以作为一个简单的结束保护，避免行数组刚好用完时没有可读字符。

## 15. `input_finished` 是什么

```c
static int input_finished;
```

它表示文件是否已经读完。

```text
0  还没读完
1  已经读完
```

当 `fgetc` 返回 `EOF`：

```c
input_finished = 1;
```

以后如果还尝试读取，就会输出：

```text
program incomplete
```

并返回 `-1`。

## 16. 为什么打开文件后要关闭文件

项目最后：

```c
fclose(fin);
```

关闭文件有两个作用：

1. 告诉操作系统这个文件不再使用
2. 释放文件相关资源

文件操作的基本流程固定为：

```text
fopen
  |
  +-- 成功
  |     |
  |     +-- fgetc / fprintf / fscanf
  |     |
  |     +-- fclose
  |
  +-- 失败
        |
        +-- 处理错误
```

## 17. 把项目里的文件操作串起来

### 第一步：得到文件名

有命令行参数时：

```c
strncpy(fname, argv[1], sizeof(fname) - 1);
fname[sizeof(fname) - 1] = '\0';
```

没有命令行参数时：

```c
scanf("%255s", fname);
```

### 第二步：打开文件

```c
fin = fopen(fname, "r");
```

### 第三步：检查是否打开成功

```c
if (fin == NULL) {
    printf("Can't open file!\n");
    return 1;
}
```

### 第四步：循环读取字符

```c
int c = fgetc(fin);
if (c == EOF) {
    input_finished = 1;
    break;
}
```

### 第五步：关闭文件

```c
fclose(fin);
```

## 18. 用 `basic.pl0` 模拟一次文件读取

文件内容开头是：

```text
const m = 7, n = 85;
```

读取过程：

```text
fgetc -> 'c'  -> 放入 line[0]，cc/ll 更新
fgetc -> 'o'  -> 放入 line[1]
fgetc -> 'n'  -> 放入 line[2]
fgetc -> 's'  -> 放入 line[3]
fgetc -> 't'  -> 放入 line[4]
fgetc -> ' '  -> 放入 line[5]
fgetc -> 'm'  -> 放入 line[6]
fgetc -> ' '  -> 放入 line[7]
fgetc -> '='  -> 放入 line[8]
...
```

以后 `getch()` 不是每次都直接调用 `fgetc`。

它先从 `line[cc]` 取字符：

```c
ch = line[cc];
cc++;
```

只有当前行取空后，才再次用 `fgetc` 读下一行。

这样做的原因是：

```text
标准库负责从硬盘读一块数据
项目自己再按行交给词法分析器
```

## 19. 相对路径是怎么找到文件的

运行：

```bat
lexical-analysis\c-textbook\bin\pl0_textbook.exe lexical-analysis\c-textbook\tests\basic.pl0
```

这里：

```text
lexical-analysis\c-textbook\tests\basic.pl0
```

是相对于当前工作目录的路径。

如果当前目录是：

```text
D:\Cs-Learning-Archive\CS-Learning-Archive\Test\Compiler-Principles
```

那么实际打开的是：

```text
D:\Cs-Learning-Archive\CS-Learning-Archive\Test\Compiler-Principles\lexical-analysis\c-textbook\tests\basic.pl0
```

如果工作目录不对，就会提示：

```text
Can't open file!
```

这不是文件内容错误，而是路径没有找到。

## 20. `stdin`、`stdout` 和普通文件的区别

标准库已经帮你准备好了三个流：

| 名称 | 含义 | 常用函数 |
| --- | --- | --- |
| `stdin` | 标准输入，一般是键盘 | `scanf`、`fgetc(stdin)` |
| `stdout` | 标准输出，一般是屏幕 | `printf`、`fputc` |
| `stderr` | 标准错误，一般是屏幕 | `fprintf(stderr, ...)` |

普通文件需要先打开：

```c
fin = fopen(fname, "r");
```

而键盘和屏幕不需要手动 `fopen`，程序启动时已经可以使用。

## 21. 文件操作和本项目函数的关系

| 代码位置 | 文件操作作用 |
| --- | --- |
| `main()` | 收文件名、调用 `fopen`、最后 `fclose` |
| `getch()` | 调用 `fgetc` 读取字符 |
| `input_finished` | 记录是否读到 `EOF` |
| `line` | 临时保存当前行 |
| `cc`、`ll` | 决定何时从文件读下一行 |
| `getsym()` | 不直接读硬盘，只处理已经读入的字符 |

最重要的一句话：

```text
main 负责打开和关闭文件
getch 负责从文件取字符
getsym 只做词法分析
```

这样职责清楚，代码更容易理解。

## 22. 常见错误

### 错误 1：没有检查 `fopen` 返回值

```c
fin = fopen(fname, "r");
fgetc(fin);
```

如果打开失败，`fin` 是 `NULL`，后面的操作不安全。

正确写法：

```c
if (fin == NULL) {
    ...
}
```

### 错误 2：用 `char` 接收 `fgetc`

```c
char c = fgetc(fin);
```

不推荐，因为无法可靠区分所有字符和 `EOF`。

推荐：

```c
int c = fgetc(fin);
```

### 错误 3：忘记关闭文件

```c
fin = fopen(...);
...
```

正常路径最后要写：

```c
fclose(fin);
```

### 错误 4：把文件名和文件内容混淆

```c
fname = "lexical-analysis/c-textbook/tests/basic.pl0";
```

这里保存的是“文件路径字符串”，不是文件里面的内容。

文件内容要通过 `fgetc` 读出来。

### 错误 5：文件路径写错

文件名大小写、目录层级、扩展名必须匹配实际文件。

Windows 通常不区分大小写，但 Linux 通常区分。

## 23. 练习

### 练习 1

下面代码能不能打开文件？

```c
FILE *fp;
fp = fopen("lexical-analysis/c-textbook/tests/basic.pl0", "r");
```

答：可以，前提是当前工作目录是 `Compiler-Principles`，而且文件存在。

### 练习 2

下面代码哪里有问题？

```c
char c = fgetc(fin);
while (c != EOF) {
    c = fgetc(fin);
}
```

答：`fgetc` 的返回值最好用 `int`，因为要可靠表示 `EOF`。

### 练习 3

打开文件失败后，`fin` 是什么？

答：`NULL`。

### 练习 4

本项目中，哪个函数负责把文件字符放进 `line`？

答：`getch()`。

### 练习 5

本项目中，`EOF` 被检测到后，哪个变量会变成 1？

答：`input_finished`。

## 24. 一句话总结

这份代码的文件操作可以记成五步：

```text
1. 得到文件名
2. fopen 打开文件
3. 检查结果是不是 NULL
4. fgetc 循环读取，遇到 EOF 停止
5. fclose 关闭文件
```

识别、分类、跳过空白、判断关键字这些属于词法分析，不属于文件操作。把这两部分分开理解，代码就不会乱。

