# 读教材版 PL/0 代码前的 C 语言零基础课

> 目录已整理：文中的课程根目录指 `Compiler-Principles`。完整操作入口以本实验 README 为准；源码与测试规则未改变。

这份文档是备用补充。如果你已经会变量、数组、函数、`if` 和循环，可以直接跳过，重点阅读 [C语言文件操作精讲](C语言文件操作精讲.md)。

如果你连 C 语法也还不熟，可以先用这份文档补基础，再看 [教材版逐行精讲](教材版逐行精讲.md)。

## 1. C 语言程序是怎么变成程序的

我们写出来的文件叫“源代码文件”，例如：

```text
pl0_lexer.c
pl0_t.h
```

`.c` 文件通常放真正的代码，`.h` 文件通常放声明和公共信息。

C 语言代码不能直接运行，需要先由编译器翻译成计算机能执行的文件：

```text
pl0_lexer.c
    |
    | gcc 编译
    v
pl0_textbook.exe
```

本项目使用的编译命令是：

```bat
gcc -std=c11 -Wall -Wextra -Werror -I./lexical-analysis/c-textbook/include -o lexical-analysis/c-textbook/bin/pl0_textbook.exe lexical-analysis/c-textbook/src/pl0_lexer.c
```

先不要求看懂这行命令，只要知道：

- `gcc`：编译器
- `-std=c11`：使用 C11 标准
- `-Wall -Wextra -Werror`：开启更多警告，并把警告当成错误
- `-I./lexical-analysis/c-textbook/include`：告诉编译器去教材版的 `include` 目录找头文件
- `-o lexical-analysis/c-textbook/bin/pl0_textbook.exe`：指定生成的可执行文件
- 最后的 `pl0_lexer.c`：要编译的源文件

## 2. `#include` 是什么

代码开头有：

```c
#include <stdio.h>
#include <string.h>
#include "pl0_t.h"
```

`#include` 可以理解为“把另一个文件里的内容拿来”。

### `#include <stdio.h>`

`stdio` 是 standard input/output 的缩写，意思是标准输入输出。

它提供了：

- `printf`：向屏幕输出
- `scanf`：从键盘输入
- `fopen`：打开文件
- `fgetc`：从文件读取一个字符
- `fclose`：关闭文件

如果没有写这一行，编译器就不知道 `printf`、`fopen` 是什么。

### `#include <string.h>`

`string.h` 提供字符串相关函数。

本项目用到了：

- `strcpy`：复制字符串
- `strncpy`：最多复制指定数量的字符
- `strcmp`：比较两个字符串

### `#include "pl0_t.h"`

这个双引号形式表示包含项目自己的头文件。

当前源码位置是：

```text
lexical-analysis/c-textbook/src/pl0_lexer.c
```

编译命令中的 `-I` 指定了头文件搜索目录，因此编译器找到的是：

```text
lexical-analysis/c-textbook/include/pl0_t.h
```

这个文件名与完整编译系统的 `pl0.h` 不同，可以避免混用。以后看到路径里的 `..` 时，它表示上一级目录；但本文件的 include 现在不使用这种写法。

## 3. `#define` 是什么

代码里有：

```c
#define norw 13
#define nmax 14
#define al 10
#define symnum 32
```

`#define` 可以理解成“给一个名字绑定一个固定内容”。

编译前，编译器会把代码里的 `norw` 替换成 `13`，把 `nmax` 替换成 `14`。

例如：

```c
char word[norw][al];
```

实际会被理解成：

```c
char word[13][10];
```

它的作用是避免代码里到处写数字。如果以后要改数量，只改 `#define` 一处即可。

本项目中的含义：

- `norw = 13`：有 13 个保留字
- `nmax = 14`：整数最多 14 位
- `al = 10`：标识符最多 10 个字符
- `symnum = 32`：符号种别一共有 32 个

## 4. 变量是什么

变量可以理解成一个贴了名字的盒子，里面可以放数据。

例如：

```c
int num;
```

意思是创建一个叫 `num` 的盒子，里面存放整数。

```c
num = 85;
```

意思是把 85 放进 `num` 这个盒子。

### C 语言里常见的数据类型

| 类型 | 意思 | 项目里的例子 |
| --- | --- | --- |
| `int` | 整数 | `int num;` |
| `char` | 一个字符 | `char ch;` |
| `FILE *` | 文件指针 | `FILE *fin;` |
| 数组 | 一排连续的盒子 | `char line[81];` |
| 结构体 | 自己组合出来的新类型 | `struct token_record` |
| 枚举 | 一组有名字的整数 | `enum symbol` |

## 5. `int` 是什么

`int` 表示整数。

```c
int token_count;
```

表示 `token_count` 里面放整数。

`int` 可以做加减乘除：

```c
token_count++;
```

`++` 表示加 1。

上面的代码等价于：

```c
token_count = token_count + 1;
```

项目中的 `num`、`cc`、`ll`、`cx`、`err`、`token_count` 都是整数变量。

## 6. `char` 是什么

`char` 表示一个字符。

```c
char ch;
```

表示 `ch` 里面可以保存一个字符，例如：

```c
ch = 'a';
ch = '+';
ch = '\n';
```

### 字符要用单引号

C 语言中：

```text
'a'   一个字符
"a"   一个字符串
```

单引号只能放一个字符。双引号组成字符串，字符串末尾会自动有一个 `\0`。

### 常见字符

| 写法 | 含义 |
| --- | --- |
| `' '` | 空格 |
| `'\n'` | 换行 |
| `'\r'` | 回车 |
| `'\t'` | 制表符 |
| `'\0'` | 字符串结束标志 |

`\n`、`\r`、`\t` 叫转义字符，反斜杠表示后面的字母有特殊含义。

## 7. 为什么字符能和数字比较

计算机内部保存字符时，实际保存的是编码。

例如 ASCII 编码中：

```text
'0' 的编码是 48
'1' 的编码是 49
'9' 的编码是 57
'a' 的编码是 97
'z' 的编码是 122
```

因此可以写：

```c
if (ch >= '0' && ch <= '9')
```

意思是：`ch` 是不是数字字符。

### 字符转换成数字

```c
num = 10 * num + ch - '0';
```

可以拆成：

```c
int 当前数字 = ch - '0';
num = num * 10 + 当前数字;
```

例如读到字符 `'8'`：

```text
'8' 的编码是 56
'0' 的编码是 48
56 - 48 = 8
```

## 8. 字符串是什么

字符串可以理解成一排字符，并且最后一定要有一个结束标志 `\0`。

例如：

```c
char id[11];
```

表示 `id` 最多能放 11 个 `char`。

如果内容是：

```text
read
```

在内存中可以理解为：

```text
下标:  0    1    2    3    4
内容: 'r'  'e'  'a'  'd'  '\0'
```

`al` 是 10，所以：

```c
char id[al + 1];
```

就是：

```c
char id[11];
```

多出的第 11 个位置专门放 `\0`。

## 9. 数组是什么

数组是一排相同类型的盒子。

```c
char line[81];
```

表示有 81 个字符格子：

```text
line[0], line[1], line[2], ..., line[80]
```

数组下标从 0 开始，不是从 1 开始。

所以 81 个格子对应：

```text
0 到 80
```

本项目还有：

```c
char word[norw][al];
```

它是二维数组，可以理解成一张表：

```text
word[0]  -> begin
word[1]  -> call
word[2]  -> const
...
word[12] -> write
```

第二个维度最多 10 个字符。

## 10. `if` 是什么

`if` 表示“如果”。

```c
if (cc == ll) {
    ...
}
```

意思是：如果 `cc` 等于 `ll`，就执行大括号里的内容。

### `=` 和 `==` 的区别

这是 C 语言最容易混淆的地方：

```c
a = b;    // 把 b 的值放进 a
```

```c
a == b;   // 判断 a 和 b 是否相等
```

赋值用 `=`，判断相等用 `==`。

### `if ... else`

```c
if (条件) {
    条件成立时执行
} else {
    条件不成立时执行
}
```

例如：

```c
if (token_count >= max_tokens) {
    return;
}
```

表示 Token 已满时直接结束当前函数。

## 11. `while` 是什么

`while` 表示“只要条件成立，就一直重复”。

```c
while (ch != '\n') {
    ...
}
```

意思是：只要 `ch` 不是换行，就反复执行。

它的执行顺序：

```text
1. 判断条件
2. 条件成立，执行一次
3. 再判断条件
4. 条件不成立，结束循环
```

项目中：

```c
while (ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t') {
    getch();
}
```

表示跳过空格、换行、回车和制表符。

## 12. `do ... while` 是什么

`do ... while` 和 `while` 很像，但它会先执行一次，再判断条件。

```c
do {
    循环体
} while (条件);
```

项目中：

```c
do {
    if (k < al) {
        a[k] = ch;
        k++;
    }
    getch();
} while ((ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9'));
```

先读一个字母，再不断读后面的字母和数字。

## 13. `for` 是什么

`for` 适合“一开始就知道循环次数”的情况。

```c
for (i = 0; i < 256; i++) {
    ssym[i] = nul;
}
```

分成三部分：

```text
i = 0       从头开始
i < 256     只要小于 256 就继续
i++         每轮结束后加 1
```

所以这会把 `ssym[0]` 到 `ssym[255]` 全部设为 `nul`。

## 14. `&&`、`||` 和 `!` 是什么

### `&&` 表示“而且”

```c
ch >= '0' && ch <= '9'
```

表示两个条件都成立：

```text
ch 大于等于 '0'
而且
ch 小于等于 '9'
```

### `||` 表示“或者”

```c
ch == ' ' || ch == '\n'
```

表示只要其中一个成立就够了。

### `!` 表示“不是”

```c
ch != '\n'
```

`!=` 表示“不等于”。

### 括号很重要

```c
(ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9')
```

含义是：

```text
（是小写字母）或者（是数字）
```

## 15. 函数是什么

函数是一段有名字、可以重复使用的代码。

```c
int getch(void)
{
    ...
}
```

拆开看：

```text
int       返回值的类型
getch     函数名字
void      不接收参数
{ ... }   函数体
```

调用它：

```c
getch();
```

意思是执行 `getch` 里面的代码。

### 有参数的函数

```c
void error(int n)
```

表示：

- 返回值是 `void`，也就是不返回东西
- 接收一个整数参数，名字叫 `n`

调用：

```c
error(30);
```

此时函数里的 `n` 就是 30。

### `return` 是什么

```c
return 0;
```

表示结束当前函数，并把 0 返回给调用者。

```c
return -1;
```

表示结束并返回 -1。

项目里 `getch()` 和 `getsym()` 约定：

```text
0   正常
-1  发生文件中断，需要停止
```

## 16. `enum` 是什么

`enum` 是“枚举”，用一串名字表示一组整数。

```c
enum symbol {
    nul,
    ident,
    number,
    plus
};
```

C 会这样编号：

```text
nul = 0
ident = 1
number = 2
plus = 3
```

以后写：

```c
sym = number;
```

比写：

```c
sym = 2;
```

更容易看懂。

## 17. `struct` 是什么

`struct` 可以把多种数据组合成一个新的整体。

```c
struct token_record {
    char word[128];
    char category[128];
    char value[128];
};
```

可以理解成：

```text
一条 Token 记录
├── word
├── category
└── value
```

创建一个结构体变量：

```c
struct token_record token;
```

访问里面的成员：

```c
token.word
token.category
token.value
```

## 18. 数组和结构体里为什么能用 `tokens[i]`

```c
static struct token_record tokens[2048];
```

表示创建 2048 条 Token 记录。

```c
tokens[0]
tokens[1]
tokens[2]
```

分别表示第 0 条、第 1 条、第 2 条记录。

```c
tokens[token_count]
```

表示“当前应该写入的那一条记录”。

写入后：

```c
token_count++;
```

下一条就会写进下一个位置。

## 19. 指针是什么

指针可以先简单理解成“记录地址的变量”。

### 文件指针

```c
FILE *fin;
```

这里的 `FILE *` 不是普通整数，而是表示“一个打开的文件”。

`fopen` 返回 `FILE *`：

```c
fin = fopen(fname, "r");
```

如果成功，`fin` 指向打开的文件。

如果失败，`fin` 等于 `NULL`。`NULL` 表示“没有指向任何有效东西”。

```c
if (fin == NULL) {
    ...
}
```

就是判断文件是否打开失败。

### 字符串指针

```c
static const char *symbol_name[] = {
    "NUL",
    "IDENT",
    ...
};
```

`const char *` 可以理解为“指向一段不能修改的文本”。

`symbol_name[0]` 就是 `"NUL"`。

## 20. 全局变量、局部变量、`static` 和 `extern`

### 全局变量

函数外面定义的变量是全局变量。

```c
char ch;
int num;
```

整个源文件里的函数都可以使用它。

### 局部变量

函数内部定义的变量是局部变量。

```c
int getch(void)
{
    int c;
}
```

`c` 只能在这个函数里使用。

### `static`

`static` 放在全局变量前面，表示“只能在本文件里使用”。

```c
static int token_count;
```

其他 `.c` 文件不能直接用 `token_count`。

### `extern`

`extern` 表示“这个变量或函数不是在这里定义的，别的地方有定义”。

头文件里写：

```c
extern int num;
```

源文件里写：

```c
int num;
```

头文件负责告诉其他文件“有这个名字”，源文件负责真正创建它。

## 21. 文件操作是什么

### 打开文件

```c
fin = fopen(fname, "r");
```

含义：

- `fopen`：打开文件
- `fname`：文件名
- `"r"`：只读模式
- 返回值交给 `fin`

### 读取一个字符

```c
int c = fgetc(fin);
```

含义：

- 从 `fin` 指向的文件读一个字符
- 返回读到的字符
- 如果文件已经结束，返回 `EOF`

`EOF` 是 End Of File，表示文件结束。

### 关闭文件

```c
fclose(fin);
```

程序用完文件后要关闭，释放系统资源。

## 22. `printf` 是什么

`printf` 向屏幕输出文字。

```c
printf("program incomplete\n");
```

`\n` 表示换行。

### 输出变量的值

```c
printf("%d ", cx);
```

`%d` 是一个占位符，表示这里要放一个整数。

如果 `cx` 是 0，输出：

```text
0 
```

常见占位符：

| 占位符 | 含义 |
| --- | --- |
| `%d` | 整数 |
| `%c` | 一个字符 |
| `%s` | 字符串 |
| `%20s` | 字符串至少占 20 个字符宽 |
| `%-20s` | 字符串占 20 个字符宽，左对齐 |

Token 表使用：

```c
printf("%-20s%-20s%-20s\n",
       tokens[i].word,
       tokens[i].category,
       tokens[i].value);
```

意思是输出三列，每列宽 20 个字符。

## 23. `scanf` 是什么

```c
scanf("%255s", fname);
```

表示从键盘读一段不含空格的文字。

`255` 表示最多读 255 个字符，防止 `fname` 溢出。

## 24. 字符串函数

### `strcpy`

```c
strcpy(id, a);
```

表示把 `a` 里的字符串复制到 `id`。

### `strncpy`

```c
strncpy(dest, source, size - 1);
```

表示最多复制 `size - 1` 个字符。

### `strcmp`

```c
strcmp(id, word[k])
```

比较两个字符串。

返回值：

```text
小于 0   id 在 word[k] 前面
等于 0   两个字符串相同
大于 0   id 在 word[k] 后面
```

折半查找就是利用这个返回值决定往左找还是往右找。

## 25. `sizeof` 是什么

```c
sizeof(number_text)
```

表示取得 `number_text` 占多少字节。

对于字符数组，可以把它理解成“这个数组最多有多少个字符”。

```c
if (i < (int)sizeof(number_text) - 1)
```

表示还没写满数组时才能继续写，防止越界。

### 为什么要减 1

字符数组最后要留一个位置放 `\0`，所以最多只能保存“长度减 1”个普通字符。

## 26. `NULL` 和 `'\0'` 不要混淆

### `NULL`

表示“没有有效地址”，主要用在指针上：

```c
if (fin == NULL)
```

### `'\0'`

表示字符串结束符：

```c
a[k] = '\0';
```

虽然两者都和 0 有关系，但用途不同：

- `NULL` 用于指针
- `'\0'` 用于字符串结尾

## 27. 后置 `++` 是什么

```c
cc++;
```

表示把 `cc` 加 1。

```c
line[ll++] = ch;
```

可以拆成两步：

```c
line[ll] = ch;
ll = ll + 1;
```

先使用 `ll` 当前的值，再把它加 1。

## 28. 三目运算符是什么

```c
return err == 0 ? 0 : 1;
```

这是 `if ... else` 的简写。

等价于：

```c
if (err == 0) {
    return 0;
} else {
    return 1;
}
```

含义：

```text
问号前面的条件成立 -> 返回冒号左边的 0
条件不成立       -> 返回冒号右边的 1
```

## 29. 把这些知识放回词法分析器

现在再回头看主流程：

```text
main()
  |
  +-- fopen 打开源文件
  |
  +-- init 初始化表格
  |
  +-- while 循环
        |
        +-- getsym 分析一个 Token
              |
              +-- getch 读取字符
              +-- if 判断字符类型
              +-- while 跳过空白和注释
              +-- 查 word/wsym/ssym 表
              +-- record_current_token 保存结果
  |
  +-- print_tokens 输出表格
  |
  +-- fclose 关闭文件
```

如果这一段你都能大概理解，就可以继续读下一份逐行精讲文档。
