# 实验一 Java 版 PL/0 词法分析器代码讲解

对应源码：[Pl0Lexer.java](../src/Pl0Lexer.java)

重新编写日期：2026-10-19

这份文档从“运行一个测试文件”开始，跟踪字符怎样进入程序、怎样组成 Token、出错后怎样继续。你已经会 Java 基础语法，因此不会反复解释变量和普通赋值；文件输入、脚本参数、读取位置和循环条件会展开说明。

本文只解释当前 Java 源码，不修改程序。不要把它和 C 独立版、C 教材版或 NFA 转 DFA 的输入方式混在一起。

## 阅读路线

| 你现在想弄懂什么 | 从哪一节开始 |
|---|---|
| 文件是怎么交给程序的，args 是什么 | 第 2 节 |
| Java 怎样打开和关闭文件 | 第 3 节 |
| currentChar、currentColumn 到底指向哪里 | 第 4、5 节 |
| getsym 为什么有两层循环，返回值怎么理解 | 第 6 节 |
| 标识符、数字、注释、运算符怎么识别 | 第 7～10 节 |
| 错误信息和最终 Token 表怎么产生 | 第 11 节 |
| 想把一个例子从头跟到尾 | 第 12 节 |
| 查某个方法或某个判断条件 | 第 14、15 节 |

第一次建议按 1 → 2 → 3 → 4 → 5 → 6 → 12 阅读，先建立完整流程。第二次再细看各个识别方法。做完第 16 节的自测，比连续读完所有文字更能检查是否真的理解。

## 1 先分清楚程序的输入和输出

### 1.1 输入的是 PL/0 源程序，不是 Java 程序

例如文件内容是：

```pl0
var x;
begin
    x := 12 + 3;
end.
```

这个程序要做的不是算出 x 的值，也不是运行这段 PL/0，而是把文字拆成有意义的单词。

这里的“单词”不只指字母组成的词。`var`、`x`、`:=`、`12`、`+`、`;` 都可以各自成为一个 Token。

### 1.2 Token 的三列分别是什么

当前程序的输出格式是：

```text
Word                Category            Value
var                 KEYWORD             VARSYM
x                   IDENTIFIER          x
;                   SPECIAL             SEMICOLON
```

| 列 | 保存什么 | 例子 |
|---|---|---|
| Word | 单词文本 | `var`、`x`、`12`、`:=` |
| Category | 输出用的大分类 | KEYWORD、IDENTIFIER、INTEGER、SPECIAL、ERROR |
| Value | 本程序记录的值或种别名 | VARSYM、x、12、BECOMES |

注意实际代码的区别：标识符的 Value 是标识符文本，例如 `x`，不是字符串 `IDENT`；整数的 Value 是数字文本，不是 Java 已计算出的整数值。

`Symbol.IDENT`、`Symbol.NUMBER` 是识别过程中用的内部种别。它们不一定原样出现在 Value 列。

### 1.3 当前版本遵守的规则

- 13 个小写关键字：`begin call const do end if odd procedure read then var while write`。
- 标识符以 ASCII 小写字母开头，后面可以跟 ASCII 小写字母或数字，最长 10 个字符。
- 整数由 `0`～`9` 组成，最多 14 位；负号单独识别为 MINUS。
- 不等号是 `#`。本版本不把 `<>` 合并为一个不等号。
- 注释使用 `/* ... */`，不支持嵌套注释，也不把 `//` 当作行注释。
- 遇到第一个句号 Token 就停止分析；后面的文字不再被逐个检查。

所以 `begin1` 是标识符，`begin` 是关键字；`BEGIN` 不会自动转为小写，而会触发非法字符处理。

## 2 从运行命令到 main 的参数

### 2.1 先进入正确的目录

在课程根目录打开 PowerShell 后执行：

```powershell
cd .\lexical-analysis\java
.\run.cmd tests\basic.pl0
```

后文短命令都以终端位于 `lexical-analysis\java` 为前提。

第一行是改变终端当前目录。第二行中的 `run.cmd` 是 Windows 批处理脚本，不是 Java 源码。

`tests\basic.pl0` 是传给脚本的第一个参数。它是文件路径，不是文件里的整段源码。

### 2.2 run.cmd 怎样把参数继续交给 Java

脚本里真正启动 Java 的命令是：

```bat
java -cp "%~dp0classes" Pl0Lexer %*
```

| 写法 | 含义 |
|---|---|
| `java` | 启动已经编译好的 Java 程序 |
| `-cp` | 指定查找 `.class` 文件的目录，完整写法是 `-classpath` |
| `%~dp0` | 当前脚本所在目录，结尾自带反斜杠 |
| `%~dp0classes` | 脚本旁边的 classes 目录 |
| 双引号 | 把整个路径作为一个整体，避免路径中的空格把它拆开 |
| `Pl0Lexer` | 要启动的类名，不加 `.java` 或 `.class` |
| `%*` | 脚本收到的全部参数，继续交给 Java 程序 |

因此，在这个实验目录里运行脚本，其效果相当于：

```bat
java -cp classes Pl0Lexer tests\basic.pl0
```

Java 启动器处理 `-cp classes`，找到 Pl0Lexer 后调用 main。交给 main 的参数只有类名后面的文件路径：

```java
args.length == 1
args[0].equals("tests\\basic.pl0")
```

`args.length` 是参数数量，不是文件行数。`args[0]` 是第一个参数，下标从 0 开始。

如果文件路径有空格，运行时加双引号：

```powershell
.\run.cmd "tests\my example.pl0"
```

Java 的 `args[0]` 会收到完整路径，不包含命令行外围用于分组的双引号。

### 2.3 这和 NFA 实验不一样

NFA 脚本用的是：

```bat
java -cp "%~dp0classes" NfaToDfa < "%~1"
```

那里 `<` 把文件内容接到 `System.in`，文件名没有进入 Java 的 args。

本实验没有这个重定向，而是通过 `%*` 传递文件路径，之后由 Java 自己打开文件。

```text
PL/0 实验：文件路径 → args[0] → FileInputStream → 读取源码
NFA 实验：文件内容 → 输入重定向 → System.in → 读取 NFA
```

### 2.4 main 中每一步在做什么

```java
public static void main(String[] args) {
    if (args.length > 1) {
        System.out.println("Usage: java Pl0Lexer [source.pl0]");
        return;
    }

    System.out.print("Input pl/0 file?   ");
    String fileName = args.length == 1
            ? args[0]
            : readFileNameFromKeyboard();

    if (fileName == null) {
        return;
    }
    if (args.length == 1) {
        System.out.println(fileName);
    }

    Pl0Lexer lexer = new Pl0Lexer();
    int exitCode = lexer.run(fileName);
    if (exitCode != 0) {
        System.exit(exitCode);
    }
}
```

文件名的两种来源：

| 启动方式 | args.length | fileName 从哪里来 |
|---|---:|---|
| `.\run.cmd tests\basic.pl0` | 1 | `args[0]` |
| `.\run.cmd` | 0 | `readFileNameFromKeyboard()` |

三目运算的意思可以展开理解成下面这样，但源码没有必要改：

```java
if (args.length == 1) {
    fileName = args[0];
} else {
    fileName = readFileNameFromKeyboard();
}
```

`fileName == null` 表示没有取得可用文件名，就直接结束，不去打开文件。

带参数时额外打印文件名，是为了让屏幕提示完整，并不是再次读取文件。

main 是 static 方法，而 run 和大多数识别方法操作的是某个分析器对象的状态，所以先创建 `new Pl0Lexer()`，再调用 `lexer.run(fileName)`。

run 返回 0 通常表示没有计入词法错误，非 0 表示失败，main 用 `System.exit(exitCode)` 把这个结果交给操作系统。正常情况让 main 自然结束即可。

源码中“参数超过一个”的分支只是 return，没有显式以非零退出码结束。脚本本身还会提前拒绝第二个非空参数。不要把使用提示分支描述成统一完善的错误退出机制。

### 2.5 readFileNameFromKeyboard 只读取文件名

核心代码：

```java
BufferedReader keyboard = new BufferedReader(
        new InputStreamReader(System.in, StandardCharsets.UTF_8));
String fileName = keyboard.readLine();
if (fileName == null) {
    return null;
}
fileName = fileName.trim();
return fileName.isEmpty() ? null : fileName;
```

这里的 `System.in` 是标准输入，通常来自键盘。输入：

```text
tests\basic.pl0
```

它只取得这一行文件路径，不会把后面粘贴的 PL/0 源码当成待分析程序。

`readLine()` 读到输入结束时返回 null；按一个空回车得到的则是空字符串，不是 null。trim 去掉路径两端空白后，空字符串也转换成 null。

如果读取过程抛出 IOException，catch 打印提示并返回 null。这个方法不使用 try-with-resources 关闭 keyboard，因为关闭包装它的 reader 也会关闭共享的 System.in。

## 3 文件究竟怎样打开 读取和关闭

### 3.1 run 中的三层包装

```java
try (BufferedReader fileReader = new BufferedReader(
        new InputStreamReader(
                new FileInputStream(fileName), StandardCharsets.UTF_8))) {
    reader = fileReader;
    init();

    while (currentSymbol != Symbol.PERIOD && getsym()) {
        // getsym() 已经记录了一个 Token，这里继续读取下一个 Token。
    }

    printTokens();
    System.out.print('\n');
    return errorCount == 0 ? 0 : 1;
}
```

从最里面往外读：

| 对象 | 接收什么 | 提供什么 |
|---|---|---|
| `FileInputStream(fileName)` | 文件路径 | 从文件读取字节的能力 |
| `InputStreamReader(..., UTF_8)` | 字节流与编码 | 把字节按 UTF-8 解码成字符 |
| `BufferedReader(...)` | 字符读取器 | 缓冲读取及 `readLine()` 按行读取 |

文件里保存的是字节。程序想判断当前是不是字符 `'a'`、`':'`，必须先按约定编码解码。

`new FileInputStream(fileName)` 会尝试打开文件。只是写了字符串 `"tests\\basic.pl0"`，并不代表文件已经打开。

### 3.2 相对路径是相对谁

`tests\basic.pl0` 相对的是**启动 Java 时终端的当前目录**，不是源码所在目录，也不是自动相对 run.cmd 所在目录。

脚本中的 `%~dp0classes` 只解决“到哪里找 class”，不会替你改写输入文件路径。

build.cmd 使用 pushd/popd 临时进入自己的目录编译，结束后会恢复调用者目录。因此从课程根目录运行，应写完整的相对位置：

```powershell
.\lexical-analysis\java\run.cmd lexical-analysis\java\tests\basic.pl0
```

### 3.3 reader = fileReader 不是复制文件

`fileReader` 是 run 里的局部引用；`reader` 是分析器对象的成员字段。

这句让两者引用同一个 BufferedReader 对象，以便 `readNextLine()` 等其他实例方法通过成员 reader 继续读取同一份文件。

没有复制文件内容，也没有再打开一次文件。

### 3.4 文件什么时候关闭

`try (资源声明)` 是 try-with-resources。

离开 try 块时，无论正常 return，还是异常跳出，Java 都会尝试关闭 fileReader；关闭外层 reader 也会关闭它包装的底层文件流。

因此不需要在 return 前另外写一遍 close，也不应把“方法 return 了”理解成“资源就不关闭了”。

### 3.5 两个 catch 与循环空体

- `FileNotFoundException`：文件无法打开，例如路径不存在；打印 `Can't open file!`，返回 1。它属于 IOException 的子类，所以写在更宽泛的 IOException 前面。
- `IOException`：其他文件 I/O 异常；外层捕获到后打印详情并返回 1。不过读下一行时也有内部 catch，具体差别见第 13 节。

while 的循环体看起来只有一条注释，是因为工作发生在条件中的 `getsym()` 内：识别、更新状态、记录 Token 都已经在那里完成。

每次回到 while 条件，又会判断是否继续识别。这里不是空转。

## 4 读源码前先认清这些状态

### 4.1 两种枚举与 Token 对象

`Symbol` 表示细分的内部种别，例如 IDENT、NUMBER、PLUS、BECOMES、VARSYM。NUL 是初始化或特定错误时使用的占位种别。

`TokenCategory` 表示输出用的大分类：IDENTIFIER、INTEGER、KEYWORD、SPECIAL、ERROR。

一个 Token 对象保存一行输出数据：

```java
private final String word;
private final TokenCategory category;
private final String value;
```

Token 构造方法把传入的三个参数保存到对应字段。final 表示字段赋值后不能重新指向别的值，不表示程序只允许创建一个 Token。

### 4.2 两张 Map 和两个 EnumSet

| 名称 | 例子 | 用途 |
|---|---|---|
| keywords | `"var" → Symbol.VARSYM` | 从完整单词查询是否为关键字 |
| specialCharacters | `';' → Symbol.SEMICOLON` | 从一个字符查询单字符特殊符号 |
| KEYWORD_SYMBOLS | 包含 VARSYM、READSYM 等 | 输出时判断当前种别属于关键字大类 |
| SPECIAL_SYMBOLS | 包含 PLUS、LEQ、BECOMES 等 | 输出时判断当前种别属于特殊符号大类 |

前两张表回答“查到什么具体种别”，后两个集合回答“这个种别属于哪一大类”。

`specialCharacters` 没有直接存 `:=`、`<=`、`>=`，因为它的键是 Character，只能查一个字符。这些需要看第二个字符的符号由专门方法处理。

EnumSet 是适合枚举元素的集合；这里主要用它的 contains 判断，不需要背底层实现。

### 4.3 最值得追踪的成员字段

| 字段 | 它保存什么 | 容易误解的地方 |
|---|---|---|
| reader | 当前打开的文件读取器 | 不是文件名字符串 |
| currentLine | 当前整行字符缓存 | 结尾人为补了一个空格 |
| currentColumn | 下一次 getch 在缓存中要取的下标 | 通常已经比 currentChar 的下标大 1 |
| currentChar | 当前正在判断的一个字符 | 不是完整 Token |
| currentSymbol | 最近识别出的具体种别 | 不是待打印表中的所有历史结果 |
| identifier | 最近的标识符或关键字文本 | 处理超长标识符时会截断 |
| numberText | 最近的整数文本 | 是 String，不是 int |
| tokens | 已记录的 Token 对象列表 | 最后统一打印，里面也可能有 ERROR 行 |
| inputFinished | 读取是否已结束或被内部 I/O 异常终止 | 不表示已经遇到句号 |
| errorCount | error 方法被调用的次数 | 不等于 ERROR 行数，也不一定包含全部 I/O 问题 |
| codeAddress | 教材式回显前缀 | 本实验没有代码生成，所以保持 0，不是行号 |

三个常量：标识符最长 10 字符、整数最长 14 位、错误指示缩进上限 80。最后一个不是限制源文件每行只能有 80 字符。

### 4.4 init 做了哪些准备

init 先清空并重新填充关键字表、特殊字符表，再重置读取状态与结果列表。

需要特别注意的初始值：

```java
currentLine = "";
currentColumn = 0;
currentChar = ' ';
currentSymbol = Symbol.NUL;
inputFinished = false;
tokens.clear();
```

为什么 currentChar 先设成空格？第一次 getsym 会先跳过空白，因此会调用 getch，把第一个真实字符读进来。

为什么 currentSymbol 不是 PERIOD？run 的 while 先判断“当前种别不是句号”，初始设为 NUL 才能进入第一次识别。

clear 还避免同一个分析器对象被再次使用时，旧 Token 或旧错误计数混进新结果。init 不负责打开或关闭文件。

## 5 先把字符读取层弄明白

识别器并不是每要一个字符，就直接向磁盘读取一次。它先通过 readLine 取得一行，再用 currentColumn 在这行缓存里逐个取字符。

### 5.1 getch 只向前推进一个字符

```java
private int getch() {
    if (currentColumn >= currentLine.length()) {
        if (!readNextLine()) {
            return -1;
        }
    }

    currentChar = currentLine.charAt(currentColumn);
    currentColumn++;
    return 0;
}
```

逐步执行：

1. 如果 currentColumn 已经到达或越过当前行长度，说明缓存用完，先换行。
2. 换行失败就返回 -1，不再调用 charAt，避免越界。
3. 否则取下标 currentColumn 对应的字符，放进 currentChar。
4. 再把 currentColumn 加 1，为下一次读取做准备。
5. 返回 0，表示成功取得一个字符。

这里的 0 和 -1 是 getch 自己约定的状态码，不是读取到的字符。真实字符保存在 currentChar。

### 5.2 用 var x; 看下标的变化

readNextLine 取得源文件里的 `var x;` 后，缓存其实是：

```text
下标             0   1   2   3   4   5   6
currentLine      v   a   r  空格 x   ;  补的空格
```

| 调用后 | currentChar | currentColumn | 下一次取哪个下标 |
|---|---|---:|---:|
| 第一次 getch | v | 1 | 1 |
| 第二次 getch | a | 2 | 2 |
| 第三次 getch | r | 3 | 3 |
| 第四次 getch | 空格 | 4 | 4 |

因此读到 `r` 时 currentColumn 是 3，不是 2。要表示当前字符在缓存里的零起点下标，通常用 currentColumn−1。

不要把 currentColumn 误当成“还没读取的当前字符位置”，否则后面解释错误标记位置时会差一位。

### 5.3 readNextLine 为什么给结尾补空格

核心代码：

```java
String line = reader.readLine();
if (line == null) {
    inputFinished = true;
    System.out.print(codeAddress + " \nprogram incomplete\n");
    return false;
}

currentLine = line + ' ';
System.out.print(codeAddress + " " + line + "\n\n");
return true;
```

BufferedReader.readLine 返回一行的文本，不包含行末的 `\n` 或 `\r\n`。

假设文件这样写：

```pl0
var
x
```

如果直接把两行的字母连续交给识别方法，又没有任何行分隔，可能误当成 `varx`。每行补一个空格，让行与行之间保持单词边界，也让最后一行的标识符或数字能遇到一个分隔字符。

空行不是文件结束：readLine 返回 `""` 时，补空格后缓存是 `" "`，后续跳过即可。只有返回 null 才表示已经没有下一行。

### 5.4 readNextLine 的其他分支

| 条件或处理 | 为什么这样做 |
|---|---|
| 先把 currentColumn 设为 0 | 新行要从下标 0 开始取 |
| 先清空 currentLine | 不把上一行剩余内容当作新行 |
| inputFinished 已为 true | 已经不能继续读取，打印不完整提示并返回 false |
| reader.readLine 抛出 IOException | 打印读取失败提示，设置结束标记并返回 false |
| line == null | 设置结束标记，打印不完整提示并返回 false |
| 正常取得一行 | 补空格、回显该行，然后返回 true |

回显前面一直出现的 `0` 来自 codeAddress。它不是“程序总在读取第 0 行”，而是保留教材代码地址输出的形式；词法分析实验没有生成指令，也没有更新这个地址。

### 5.5 为什么读到句号却还没到文件结尾

句号是 PL/0 的结束符，EOF 是文件流已经读不到新数据，两者不同。

当前实现遇到句号后主动停止，不要求先读到 EOF。句号所在行可能已经被整行缓存并回显，但它后面的字符不会继续产生 Token。

## 6 getsym 怎样安排一次识别

这是理解词法分析器的核心方法。它不是“一次只读取一个字符”，而是使用若干次 getch，尝试完成一次单词识别或错误恢复。

### 6.1 第一阶段反复跳过空白和注释

```java
while (true) {
    skipWhitespace();
    if (inputFinished) {
        return false;
    }

    if (currentChar != '/') {
        break;
    }

    if (getch() == -1) {
        return false;
    }
    if (currentChar != '*') {
        currentSymbol = Symbol.SLASH;
        recordCurrentToken();
        return true;
    }

    if (!skipComment()) {
        return false;
    }
}
```

这里需要 while，而不是只写一个 if，因为注释后面还可能是空白或另一条注释：

```pl0
/* first */   /* second */ var x;
```

执行过程：跳空白 → 跳第一条注释 → 再跳空白 → 跳第二条注释 → 再跳空白 → 遇到 v，离开准备阶段。

`while (true)` 不是无条件永远运行。它在“遇到普通 Token 起点”时 break，在读完或失败时 return。

遇到 `/` 为什么还要 getch？因为只看一个斜杠无法知道它是除号还是 `/*` 的开头，需要看后一个字符。

- 后一个字符不是 `*`：刚才的斜杠是除号，记录 SLASH；已经读到的后一个字符留给下一轮。
- 后一个字符是 `*`：进入 skipComment，注释不产生普通 Token。

### 6.2 第二阶段按起始字符分流

```java
boolean tokenReady;
if (isLowercaseLetter(currentChar)) {
    readIdentifier();
    tokenReady = true;
} else if (isDigit(currentChar)) {
    readNumber();
    tokenReady = true;
} else if (currentChar == ':') {
    tokenReady = readBecomes();
} else if (currentChar == '<') {
    tokenReady = readLessOperator();
} else if (currentChar == '>') {
    tokenReady = readGreaterOperator();
} else {
    tokenReady = readSingleSpecialCharacter();
}
```

| 当前字符 | 进入哪个方法 | 可能得到什么 |
|---|---|---|
| a～z | readIdentifier | 标识符或关键字 |
| 0～9 | readNumber | 整数，可能同时报告格式问题 |
| `:` | readBecomes | `:=`，或者单独冒号错误 |
| `<` | readLessOperator | `<` 或 `<=` |
| `>` | readGreaterOperator | `>` 或 `>=` |
| 其他字符 | readSingleSpecialCharacter | 单字符特殊符号或非法字符 |

这里不用先写“如果是关键字”。关键字也是从字母开头，必须先完整读出单词，再查表区分。

### 6.3 tokenReady 和 getsym 的返回值不是一回事

方法结尾是：

```java
if (tokenReady) {
    recordCurrentToken();
}
return tokenReady || !inputFinished;
```

tokenReady 表示：这条识别分支是否准备好了一个应由统一出口记录的普通 Token。

getsym 返回 true 的实际含义更宽：可能成功识别了普通 Token，也可能是遇到可恢复错误，仍然允许主循环继续。

| 情形 | tokenReady | inputFinished | getsym 返回值 |
|---|---|---|---|
| 正常识别一个符号 | true | 通常 false | true |
| 遇到 @，报告错误后向前读了一个字符 | false | false | true |
| 错误恢复时已经读到文件结束 | false | true | false |
| 在开头跳空白时读完文件 | 尚未进入此阶段 | true | 前面直接返回 false |

因此不能把注释里的“true 表示获得一个 Token”当成对所有分支的严格保证。解释当前代码时，应以这些实际分支为准。

识别标识符和数字的方法内部也可能额外插入 ERROR 行，因此“一次 getsym 恰好新增一行输出”同样不成立。

### 6.4 run 外层 while 怎样停

```java
while (currentSymbol != Symbol.PERIOD && getsym()) {
}
```

判断从左到右，`&&` 会短路：

1. 当前种别不是 PERIOD，才调用 getsym。
2. getsym 返回 true，这轮循环完成，随后再次判断。
3. 如果本次识别到了句号，它已经被记录。下一次左边为 false，不再调用 getsym。
4. 如果 getsym 返回 false，也会结束循环。

所以句号会出现在 Token 表里，但句号后面的文字不会继续识别。

## 7 标识符和关键字怎样区分

### 7.1 readIdentifier 的完整步骤

```java
StringBuilder fullIdentifier = new StringBuilder();

do {
    fullIdentifier.append(currentChar);
    if (getch() == -1) {
        break;
    }
} while (isLowercaseLetter(currentChar) || isDigit(currentChar));

boolean identifierTooLong = fullIdentifier.length() > MAX_IDENTIFIER_LENGTH;
if (identifierTooLong) {
    fullIdentifier.setLength(MAX_IDENTIFIER_LENGTH);
}

identifier = fullIdentifier.toString();
if (identifierTooLong) {
    error(36);
    recordErrorToken(identifier, "identifier is longer than 10 characters");
}

currentSymbol = keywords.getOrDefault(identifier, Symbol.IDENT);
```

为什么用 do-while？调用这个方法前，getsym 已经确认当前字符是小写字母。这个第一个字符肯定属于当前单词，所以先保存它，再取下一个字符。

循环顺序是“保存当前字符 → getch 推进 → 判断新字符还能不能接在单词后面”。

例如输入 `abc2;`：

| 已保存文本 | getch 后 currentChar | 是否继续 |
|---|---|---|
| a | b | 是，小写字母 |
| ab | c | 是，小写字母 |
| abc | 2 | 是，数字 |
| abc2 | `;` | 否 |

循环退出时，分号已经进入 currentChar，但没有加入 fullIdentifier。下一次 getsym 就从这个分号开始处理。

这就是为什么本程序通常不需要“把多读的字符退回文件”：分隔字符仍保存在 currentChar，没有丢失，也没有额外再读一次。

### 7.2 先读完整单词，再查询 keywords

`getOrDefault(identifier, Symbol.IDENT)` 的意思：

- keywords 里有这个完整文本，取对应关键字种别。
- 查不到，就使用默认值 IDENT。

```text
var     → 查到 VARSYM → 关键字
variable→ 查不到       → 标识符
var1    → 查不到       → 标识符
```

不能在读到 `var` 三个字母时马上结束，因为后面还可能跟着 `iable` 或 `1`。

### 7.3 超长时发生什么

程序先把整个连续单词读完，确定长度超过 10，再把 StringBuilder 截成前 10 个字符。

先读完整再截断，可以避免把第 11 个字符留在输入里，被误当成下一个新单词。

当前实现会：

1. 调用 error(36)，增加错误计数并打印位置。
2. 记录一行 ERROR，word 使用截断文本。
3. 设置 currentSymbol 为查表结果，随后 getsym 仍会记录普通 Token。

所以超长标识符可能对应“一条 ERROR 行加一条截断后的 IDENTIFIER 行”。这不是把非法单词完全丢弃的实现方式。

## 8 整数怎样读取 为什么不转成 int

readNumber 的核心代码：

```java
StringBuilder digits = new StringBuilder();

do {
    digits.append(currentChar);
    if (getch() == -1) {
        break;
    }
} while (isDigit(currentChar));

numberText = digits.toString();
currentSymbol = Symbol.NUMBER;

if (numberText.length() > MAX_NUMBER_LENGTH) {
    error(30);
}
if (isLowercaseLetter(currentChar)) {
    error(39);
    recordErrorToken("", "number cannot be followed by a letter");
}
```

调用前已经确认第一个字符是数字，因此同样先保存，再向前取字符。遇到非数字就停止，那个非数字保留在 currentChar。

numberText 使用 String。这个实验只负责识别整数文本，不做算术求值；14 位整数也可能超过 Java int 的范围，直接 parseInt 反而会引入当前实验不需要的溢出问题。

### 8.1 两个 if 检查不同的问题

| 条件 | 检查什么 | 当前行为 |
|---|---|---|
| numberText.length() > 14 | 数字是否超长 | error(30)，但不额外新增 ERROR 行 |
| isLowercaseLetter(currentChar) | 数字后是否立即跟小写字母 | error(39)，并记录一条 ERROR 行 |

两个条件独立存在，因此一个数字既超长又紧跟字母时，可以同时报告两种问题。

### 8.2 输入 12abc; 不会整段当成一个 Token

实际过程：

```text
读入 1、2
下一字符是 a，数字循环结束
报告 !39，插入一条 ERROR 行
记录整数 12
下一次 getsym 从 a 开始，识别标识符 abc
再处理分号
```

因此本实现是“报告不合法相邻关系，并保留数字和后续标识符继续扫描”，不是把 `12abc` 合成单个错误 Token 后整体吞掉。

`12 abc` 中间有空格，则不触发这个数字紧跟字母的检查。

## 9 空白 注释和除号怎样处理

### 9.1 skipWhitespace

```java
while (currentChar == ' ' || currentChar == '\n'
        || currentChar == '\r' || currentChar == '\t') {
    if (getch() == -1) {
        return;
    }
}
```

四个条件用 `||` 连接，表示只要属于其中一种空白，就继续推进。

当前文件读取器通过 readLine 去掉原始换行，再给行尾补空格，因此多数换行边界实际通过补的空格处理。这不妨碍条件中保留 `\n` 和 `\r` 判断。

getch 返回 -1 时必须返回，否则 currentChar 可能仍是旧空白，循环会反复尝试读取已经结束的输入。

### 9.2 / 后面的字符为什么不能再跳一次

考虑 `x/2;`。识别完 x 时，currentChar 已经是 `/`。

getsym 看到 `/` 后调用 getch，读到 `2`。因为不是 `*`，记录除号并返回。

此时 currentChar 留在 `2`。下一次 getsym 直接进入 readNumber，不能为了“开始下一个 Token”先额外调用一次 getch，否则会跳过这个数字。

### 9.3 skipComment 的进入位置

进入 skipComment 时，`/` 已经判断过，currentChar 正停在注释开头的 `*` 上。

所以方法开头的 getch 是为了从这个星号推进到注释正文：

```java
if (getch() == -1) {
    error(38);
    return false;
}
```

然后反复检查是否遇到相邻的 `*/`：

```java
while (true) {
    if (currentChar == '*') {
        if (getch() == -1) {
            error(38);
            return false;
        }
        if (currentChar == '/') {
            if (getch() == -1) {
                return false;
            }
            return true;
        }
    } else if (getch() == -1) {
        error(38);
        return false;
    }
}
```

| 当前情况 | 处理方式 |
|---|---|
| 不是星号 | 向前读一个字符，继续找 |
| 是星号 | 向前读，检查后面是不是斜杠 |
| 星号后是斜杠 | 注释结束，再读一个字符，交还给 getsym |
| 星号后不是斜杠 | 当前新字符仍在 currentChar，继续循环判断 |
| 还没找到结束符就读完 | error(38)，返回 false |

注意星号后不是斜杠时，没有无条件再读第二次。否则连续星号附近的结束符可能被漏掉。

注释正常结束后的 getch 如果已经失败，会直接返回 false，不会再报告“未闭合注释”，因为 `*/` 已经找到了。随后显示的不完整提示属于文件结束处理。

### 9.4 这个方法不支持嵌套

它没有维护“进入注释几层”的计数，只寻找首先出现的结束符。因此不要把它说成支持嵌套注释的程序。

注释的换行通过 getch 自动触发 readNextLine，不需要在 skipComment 里另写整行读取逻辑。注释内容不会添加到 Token 表。

## 10 特殊符号怎样决定读取几个字符

### 10.1 readBecomes 识别赋值符号 :=

```java
if (getch() == -1) {
    return false;
}
if (currentChar != '=') {
    currentSymbol = Symbol.NUL;
    error(19);
    recordErrorToken(":", "':' must be followed by '='");
    return false;
}

currentSymbol = Symbol.BECOMES;
getch();
return true;
```

调用时 currentChar 是冒号。第一次 getch 检查它后面是不是等号。

- 是等号：确认 `:=`，再推进到符号后面的字符，交给下一次识别。
- 不是等号：报告单独冒号错误，把当前种别设为 NUL；后一个字符保留供恢复。

例如 `x : 1;`，冒号后读到空格而不是等号，readBecomes 返回 false。但文件还没结束，getsym 的最终返回值仍可能为 true，主循环可以继续识别后面的整数。

### 10.2 readLessOperator 识别 < 或 <=

```java
if (getch() == -1) {
    return false;
}
if (currentChar == '=') {
    currentSymbol = Symbol.LEQ;
    getch();
} else {
    currentSymbol = Symbol.LSS;
}
return true;
```

| 原输入 | 检查第二字符后 | 下一轮从哪里继续 |
|---|---|---|
| `<5` | 不是等号，识别 LSS | currentChar 已是 5，不再额外推进 |
| `<=5` | 是等号，识别 LEQ | 再 getch 一次，让 currentChar 到 5 |

两条分支都把“不属于当前运算符的第一个字符”留给下一次识别。

### 10.3 readGreaterOperator 识别 > 或 >=

它和小于号方法结构相同，只把种别换成 GTR 或 GEQ。

必须看后一个字符，才能区分长度为 1 和长度为 2 的符号。不是每个 Token 都调用固定次数的 getch。

### 10.4 readSingleSpecialCharacter 识别单字符符号

```java
Symbol symbol = specialCharacters.get(currentChar);
if (symbol == null) {
    error(37);
    recordErrorToken(String.valueOf(currentChar), "illegal character");
    getch();
    return false;
}

currentSymbol = symbol;
if (currentSymbol != Symbol.PERIOD) {
    getch();
}
return true;
```

查表得到 null，表示当前字符不是已登记的单字符符号。例如 `@`：先报告并记录错误，再推进一个字符，避免下一轮仍卡在同一个 `@`。

查到普通符号就设置 currentSymbol，并推进到后面一个字符。

唯一特例是句号：设置为 PERIOD 后不再 getch，因为外层准备结束扫描。这样不会为了处理已经结束的程序再额外请求下一行。

这一层不使用 `Character.isLetter` 或 Unicode 数字判断。大写字母、汉字等没有通过前面的 ASCII 分支，也没有出现在特殊字符表里，会进入非法字符处理。

## 11 识别结果怎样变成输出

### 11.1 error 只做位置提示和计数

```java
int spaceCount = (currentColumn > 0 && currentColumn <= ERROR_LINE_WIDTH)
        ? currentColumn - 1
        : ERROR_LINE_WIDTH;

StringBuilder message = new StringBuilder("****");
for (int i = 0; i < spaceCount; i++) {
    message.append(' ');
}
message.append('!').append(code);

System.out.print(message);
System.out.print('\n');
errorCount++;
```

currentColumn 在 1～80 时，使用 currentColumn−1 个空格；不在这个范围时使用 80 个空格。for 循环只负责追加这些空格，末尾再添加感叹号和错误码。

`error(37)` 的参数是整数 37，不是第 37 行。当前定位依据是读取游标；有时游标已经指向被多看一眼的字符，因此不要把提示描述成对整个错误单词的精确下划线。

error 不直接插入 ERROR Token。是否增加 ERROR 行，由对应识别方法另外决定。

| 错误码 | 含义 | 是否另加 ERROR 行 |
|---|---|---|
| 19 | 单独冒号后没有紧接等号 | 是 |
| 30 | 整数字符串超过 14 位 | 否，仍记录 INTEGER |
| 36 | 标识符超过 10 字符 | 是，并继续记录截断后的普通 Token |
| 37 | 非法字符 | 是 |
| 38 | 注释未闭合 | 否，结束扫描 |
| 39 | 数字后立即跟小写字母 | 是，并继续记录数字及后面的标识符 |

所以 errors.pl0 的六种错误，不代表最终表里恰好会有六行 ERROR。

### 11.2 recordCurrentToken 负责分类

```java
if (currentSymbol == Symbol.IDENT) {
    addToken(identifier, TokenCategory.IDENTIFIER, identifier);
} else if (currentSymbol == Symbol.NUMBER) {
    addToken(numberText, TokenCategory.INTEGER, numberText);
} else if (KEYWORD_SYMBOLS.contains(currentSymbol)) {
    addToken(identifier, TokenCategory.KEYWORD, currentSymbol.name());
} else if (SPECIAL_SYMBOLS.contains(currentSymbol)) {
    addToken(symbolText(currentSymbol), TokenCategory.SPECIAL, currentSymbol.name());
}
```

这几条分支把“具体种别”转换为“大分类和显示内容”。

`currentSymbol.name()` 返回枚举常量的名字，例如 `VARSYM`；不是返回关键字原文 `var`。

特殊符号文本用 symbolText 取回，例如 BECOMES 对应 `:=`。标识符和数字直接使用前面读取时保存的文本。

NUL 没有匹配分支，所以不会生成一行普通 Token。

### 11.3 recordErrorToken 和 addToken

```java
private void recordErrorToken(String word, String message) {
    addToken(word, TokenCategory.ERROR, message);
}

private void addToken(String word, TokenCategory category, String value) {
    tokens.add(new Token(word, category, value));
}
```

前者只把大分类固定成 ERROR，后者创建 Token 对象并追加到列表末尾。它们不读取字符，也不改变文件游标。

ERROR 行的 Value 放错误说明，而不是符号枚举名。

### 11.4 symbolText 只是反向映射

switch 根据种别返回符号字符串，例如：

```java
case PLUS: return "+";
case LEQ: return "<=";
case PERIOD: return ".";
case BECOMES: return ":=";
default: return "";
```

每个 case 直接 return，所以不需要再写 break，也不会接着执行后面的 case。

default 是没有匹配时的备用结果。正常调用前已经通过 SPECIAL_SYMBOLS 检查，不应该把任意种别都当成特殊符号传进来。

### 11.5 printTokens 最后统一打印

```java
for (Token token : tokens) {
    System.out.printf("%-20s%-20s%-20s\n",
            token.word, token.category, token.value);
}
```

循环按列表顺序访问已保存的记录。

`%s` 表示字符串形式，`20` 是最小字段宽度，`-` 表示左对齐，`\n` 换行。宽度不是截断上限：内容超过 20 字符仍会完整打印，可能让后面一列看起来不齐。

源代码回显和 `****...!n` 是扫描过程中打印的，最终 Token 表是扫描结束后统一打印的。两种输出混在终端里，不代表程序在反复打开文件。

## 12 把 x:=12; 从头跟一遍

先暂时只跟踪源文件中的这一行，整份程序后面还有 `end.`。readNextLine 放进缓存的是 `x:=12; `，最后一个空格为人工补充。

```text
下标         0  1  2  3  4  5  6
字符         x  :  =  1  2  ;  空格
```

| 处理阶段 | 关键操作 | 结束后的 currentChar | currentColumn | 记录结果 |
|---|---|---|---:|---|
| 首字符已读入 | getch 取得 x | x | 1 | 暂无 |
| 识别标识符 | 保存 x；getch 取得冒号；不是字母或数字，停 | `:` | 2 | IDENTIFIER x |
| 识别赋值 | getch 取得等号；确认 :=；再 getch 到 1 | 1 | 4 | SPECIAL BECOMES |
| 识别整数 | 保存 1、2；getch 到分号，停 | `;` | 6 | INTEGER 12 |
| 识别分号 | 查表得 SEMICOLON；getch 到补的空格 | 空格 | 7 | SPECIAL SEMICOLON |
| 下一轮准备 | 跳过空格；行已用完，读取下一行 | 下一行第一个字符 | 1 | 暂无 |

最重要的是每个方法交接时的 currentChar：

```text
readIdentifier 交出冒号
readBecomes 交出第一个数字
readNumber 交出分号
readSingleSpecialCharacter 交出后面的空格
```

它们没有各自从文件重新开始读，也不会在每次方法调用时把当前行清空。只有缓存用完，getch 才会请求下一行。

### 12.1 再跟一下文件句号

读取 `end.` 时，readIdentifier 保存 end 后，多看到了句号；关键字查表得到 ENDSYM。

下一次 getsym 把句号记录为 PERIOD，不再推进字符。返回 run 后，下一轮 while 左边发现 currentSymbol 已是 PERIOD，于是停止。

最后调用 printTokens，退出 try 块，文件自动关闭。

### 12.2 自己跟踪时写哪几列

用草稿纸写“当前方法、currentChar、currentColumn、currentSymbol、本次新增 Token”五列即可。

遇到每一次 getch 就更新前两列；遇到赋值 currentSymbol 就更新种别；遇到 addToken 才增加结果行。不要把“读取字符”和“记录 Token”混成同一个动作。

## 13 测试方法与当前实现的边界

### 13.1 三个已有案例

在实验 Java 目录运行：

```powershell
.\run.cmd tests\basic.pl0
.\run.cmd tests\read_write.pl0
.\run.cmd tests\errors.pl0
.\verify.cmd
```

| 案例 | 应重点观察什么 |
|---|---|
| basic.pl0 | const、var、begin、end，标识符、整数和算术符号能分类输出 |
| read_write.pl0 | read 为 READSYM，write 为 WRITESYM，# 为 NEQ |
| errors.pl0 | 依次报告 !39、!30、!37、!19、!36、!38；故意错误案例返回非零 |

verify.cmd 检查这些案例的返回值和代表性输出，正常最后显示：

```text
[OK] lexical-analysis/java tests passed.
```

它是针对现有固定案例的验证，不代表穷尽了所有词法规则和异常情况。

### 13.2 不要把当前程序讲成完整编译器

它不会检查变量是否声明，也不会检查表达式、语句结构是否满足完整语法；不会生成代码，也不会执行 PL/0 的 read/write。

即使文件包含 `write(x)`，它只是识别 WRITESYM、左括号、标识符、右括号等 Token，不是向屏幕输出 x 的运行值。

### 13.3 结束与报错有几个实际限制

| 情况 | 当前源码的实际行为 |
|---|---|
| 第一个句号后还有文字 | 后续内容不会继续识别；整行回显可能已经展示了这些文字 |
| 文件缺少句号 | 读到 EOF 时打印 program incomplete，但该提示本身不增加 errorCount，可能仍返回 0 |
| readNextLine 内部捕获 IOException | 设 inputFinished 并返回 false，没有在该处增加 errorCount；不能保证所有读失败最终都返回 1 |
| 文件带 UTF-8 BOM | 本版本没有像 NFA 解析器那样去掉首字符 BOM；可能将它当作非法字符 |
| 大写关键字 BEGIN | 不自动忽略大小写，会按当前规则报告非法字符 |
| 超长整数 | 记录 !30，但仍输出完整 INTEGER 文本 |
| 超长标识符 | 先读完整，截断到 10 字符，同时可能输出 ERROR 和普通标识符 |
| 嵌套注释、// 注释 | 不支持，不能作为已实现功能介绍 |

这些内容用于准确理解现有代码，不是已经修复的问题清单。本次只重写文档，没有借机改变算法行为。

## 14 全部方法按职责速查

| 方法或构造方法 | 调用方 | 做什么 |
|---|---|---|
| Token(...) | addToken | 保存一条结果的 word、category、value |
| main | Java 启动器 | 取得文件名，创建分析器，处理 run 返回值 |
| readFileNameFromKeyboard | main | 没有路径参数时从标准输入读一行文件名 |
| run | main | 打开文件、初始化、循环扫描、打印并关闭文件 |
| init | run | 建立查找表，重置状态、结果与错误计数 |
| getch | 各扫描辅助方法 | 从行缓存取下一个字符，用完缓存时换行 |
| readNextLine | getch | 按行读取文件，补空格并回显 |
| getsym | run | 跳空白注释，选择识别分支，记录或恢复 |
| skipWhitespace | getsym | 跳过空格、制表符等分隔字符 |
| skipComment | getsym | 读取注释直到 */，未闭合时报告 !38 |
| readIdentifier | getsym | 读完整字母数字串，查关键字表并检查长度 |
| readNumber | getsym | 读数字文本，检查位数及紧邻字母 |
| readBecomes | getsym | 识别 :=，拒绝单独冒号 |
| readLessOperator | getsym | 区分 < 和 <= |
| readGreaterOperator | getsym | 区分 > 和 >= |
| readSingleSpecialCharacter | getsym | 查单字符表，处理非法字符及句号特例 |
| error | 各错误检查分支 | 打印教材式错误提示，增加错误计数 |
| recordCurrentToken | getsym | 把内部种别转换成一行普通 Token |
| recordErrorToken | 部分错误分支 | 添加 ERROR 分类的结果行 |
| addToken | 两种记录方法 | 创建 Token 对象并追加到列表 |
| symbolText | recordCurrentToken | 把特殊符号枚举转换成符号文本 |
| isLowercaseLetter | 标识符、数字与分流逻辑 | 判断字符是否在 a～z 之间 |
| isDigit | 数字、标识符与分流逻辑 | 判断字符是否在 0～9 之间 |
| printTokens | run | 依次打印已经保存的 Token |

### 14.1 为什么编译后出现多个 class

主源码只有 Pl0Lexer.java，但里面定义了 Symbol、TokenCategory、Token 等内部类型，因此 javac 会生成多个 class 文件，有时还会生成用于实现语言特性的辅助 class。

这些是编译产物，不是另外几份需要手工写的源代码。提交本实验 Java 源文件时，核心文件仍然是 src/Pl0Lexer.java。

## 15 判断条件与循环小抄

本节供回头查阅。完整解释在对应章节，下面只标出“为什么要判断”和“什么时候停”。

### 15.1 启动 初始化与读取

| 位置与条件 | 成立时意味着什么 |
|---|---|
| main：args.length > 1 | 超过允许的一个文件参数，提示用法后 return |
| main：args.length == 1，三目表达式 | 用参数给出的路径；否则从键盘读路径 |
| main：fileName == null | 没有可用文件名，不再打开文件 |
| main：args.length == 1，输出分支 | 文件名来自参数，需要回显到提示后面 |
| main：exitCode != 0 | 用非零进程退出码表示 run 的失败结果 |
| 键盘读取：fileName == null | 标准输入已结束，而不是读到普通空行 |
| 键盘读取：fileName.isEmpty()，三目表达式 | 去空白后没有路径，返回 null |
| run：currentSymbol != PERIOD && getsym() | 尚未结束，并且扫描器允许继续；左边为 false 时不再调用右边 |
| run：errorCount == 0，三目表达式 | 未计入词法错误时返回 0，否则返回 1 |
| getch：currentColumn >= currentLine.length() | 缓存已用完，需要读取下一行 |
| getch：!readNextLine() | 换行失败，不继续 charAt，返回 -1 |
| readNextLine：inputFinished | 之前已经结束或读取失败，不能继续正常读取 |
| readNextLine：line == null | 文件中没有下一行，设置结束标记 |

init 本身没有 if 或循环：它按固定代码填表并重置字段。常量表只是静态数据，不是读入输入后动态猜测出的关键字。

### 15.2 getsym 的控制条件

| 位置与条件 | 成立时的含义与动作 |
|---|---|
| 准备阶段 while(true) | 反复跳空白和注释，靠 break/return 结束 |
| inputFinished | 已无法继续扫描，直接返回 false |
| currentChar != '/' | 不需要区分注释与除号，跳出准备循环 |
| getch() == -1 | 检查斜杠后一个字符时读取失败，返回 false |
| currentChar != '*' | 刚才的斜杠是除号，记录 SLASH 并保留当前字符 |
| !skipComment() | 注释处理没有成功交回可继续扫描的字符，返回 false |
| isLowercaseLetter(currentChar) | 进入单词识别，再查关键字 |
| isDigit(currentChar) | 进入整数识别 |
| currentChar == ':' / '<' / '>' | 分别进入可能需要两个字符的符号识别 |
| 上述分支都不匹配 | 进入单字符表或非法字符处理 |
| tokenReady | 统一登记刚准备好的普通 Token |
| tokenReady || !inputFinished | 有普通 Token，或尚可继续恢复扫描时返回 true |

### 15.3 空白 注释 标识符与整数

| 位置与条件 | 为什么存在 |
|---|---|
| skipWhitespace：四种空白条件用 || 连接 | 任一种空白都应跳过；遇到非空白停止 |
| skipWhitespace：getch() == -1 | 已读完则 return，避免在旧空白上反复读取 |
| skipComment：开头 getch() == -1 | 无法进入注释正文，报告 !38 |
| skipComment：while(true) | 注释长度未知，直到找到结束符或读完 |
| skipComment：currentChar == '*' | 星号可能是结束符开头，需要看下一个字符 |
| 星号分支中的 getch() == -1 | 还没确认 */ 就读完，报告 !38 |
| 星号分支中的 currentChar == '/' | 找到 */，向后推进后返回 |
| 找到 */ 后 getch() == -1 | 注释已闭合，但没有可交回的新字符，返回 false，不报 !38 |
| 非星号分支 getch() == -1 | 尚未找到结束符就读完，报告 !38 |
| readIdentifier：do-while(小写字母 || 数字) | 至少收集已确认的首字母，随后收集可接在后面的字符 |
| readIdentifier：getch() == -1 | 无法再推进，退出收集循环 |
| readIdentifier：identifierTooLong，第一次 if | 先把显示和查表用的文本截到长度上限 |
| readIdentifier：identifierTooLong，第二次 if | 报告 !36 并添加 ERROR 行 |
| readNumber：do-while(isDigit(currentChar)) | 至少收集已确认的首数字，只继续收集数字 |
| readNumber：getch() == -1 | 无法再推进，退出收集循环 |
| numberText.length() > 14 | 报告整数位数超限 !30 |
| isLowercaseLetter(currentChar)，数字检查中 | 报告数字紧跟字母 !39 |

### 15.4 特殊符号 输出及辅助方法

| 位置与条件 | 含义 |
|---|---|
| readBecomes：getch() == -1 | 不能读到冒号后字符，返回 false |
| readBecomes：currentChar != '=' | 单独冒号不合法，记录 !19，不生成 BECOMES |
| readLessOperator：getch() == -1 | 无法取得用于区分符号的后一个字符 |
| readLessOperator：currentChar == '=' | 合并成 LEQ，否则是 LSS |
| readGreaterOperator：getch() == -1 | 无法取得用于区分符号的后一个字符 |
| readGreaterOperator：currentChar == '=' | 合并成 GEQ，否则是 GTR |
| readSingleSpecialCharacter：symbol == null | 查不到合法单字符符号，报告 !37，并推进恢复 |
| readSingleSpecialCharacter：currentSymbol != PERIOD | 不是结束句号才继续 getch |
| error：currentColumn > 0 && currentColumn <= 80 | 当前游标在常规范围内，缩进用 currentColumn−1，否则取上限 |
| error：for(i < spaceCount) | 每次加一个空格，达到所需数量后停止 |
| recordCurrentToken：IDENT / NUMBER / 两个集合 contains | 按具体种别选中对应输出分类，只执行匹配的一支 |
| symbolText：switch 各 case | 根据枚举返回符号字符串，不读取输入 |
| isLowercaseLetter：value >= 'a' && value <= 'z' | 两端都满足才是 ASCII 小写字母 |
| isDigit：value >= '0' && value <= '9' | 两端都满足才是 ASCII 数字 |
| printTokens：for(Token token : tokens) | 遍历已有结果，全部打印后结束 |

## 16 用六个问题检查自己有没有读懂

先遮住下面的答案，再用自己的话回答。只记住函数名字，不足以解释这些例子。

### 题目

1. 输入 `var1;`，为什么不能识别成关键字 var 再加整数 1？
2. readIdentifier 读 `abc;` 后，currentChar 已是分号。下一次 getsym 为什么不能先无条件 getch？
3. 输入 `/2;`，识别除号后，数字 2 还在不在？
4. 输入 `x : 1;`，readBecomes 返回 false，为什么主循环未必结束？
5. 一次 error(30) 后，Token 表为什么可能没有新增 ERROR 行？
6. 文件末尾没有句号，看到 program incomplete 后，能否断定进程退出码一定是 1？

### 核对答案

1. 标识符允许首字母后跟数字，必须先读完整的 var1，再查关键字表；完整文本不匹配 var，所以是标识符。
2. 分号只是被提前看到了，还没有处理；再 getch 会跳过它。
3. 在。判断斜杠后一个字符时已把 2 放入 currentChar，下一次从这个 2 开始。
4. getsym 最后返回 tokenReady 或“输入尚未结束”，可恢复错误时仍允许继续。
5. error 只打印和计数；整数超长分支没有额外调用 recordErrorToken。
6. 不能。该提示本身不增加 errorCount；应区分实际计数、输出提示和最终返回值。

读到这里，再回源码找 main、run、getsym、getch，试着不看本页讲出它们的分工。能解释清楚“字符从哪里来、当前停在哪里、何时记录结果、何时结束”，就已经掌握了这份程序最重要的部分。
