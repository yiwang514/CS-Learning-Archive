# Pl0Lexer.java 保姆级代码讲解

> 目录已整理：文中的课程根目录指 `Compiler-Principles`。完整操作入口以本实验 README 为准；源码与测试规则未改变。

这份文档不是只告诉你“这个函数是干什么的”，而是按照下面四个层次讲：

1. **这个函数为什么存在**：没有它，程序会出现什么问题。
2. **它在整个流程中的位置**：谁调用它，它又调用谁。
3. **代码逐句解释**：每一行到底改变了哪个变量。
4. **条件和循环为什么这样写**：所有 `if`、`while`、`do-while`、`switch` 都单独解释。

本文只讲 Java 版 [Pl0Lexer.java](../src/Pl0Lexer.java)。C 版词法分析器不使用本文中的类、`Map` 和异常机制，所以不要把两套代码混在一起阅读。

---

## 0. 先记住一句话：词法分析器在做什么

词法分析器的工作可以概括成一句话：

> 从源文件中不断读取字符，把连续的一段字符组成一个有意义的单词，再给这个单词分类。

例如：

```text
const m = 7;
```

词法分析器会得到：

```text
const  -> KEYWORD   CONSTSYM
m      -> IDENTIFIER m
=      -> SPECIAL   EQL
7      -> INTEGER   7
;      -> SPECIAL   SEMICOLON
```

它不是一次性把整个文件读完，而是反复执行：

```text
读取若干字符 -> 识别一个 Token -> 输出表增加一行 -> 继续识别下一个 Token
```

程序中的 `Token` 不只表示符号本身，还保存三类信息：

| 字段 | 含义 | 例子 |
|---|---|---|
| `word` | 源程序中实际出现的文字 | `const`、`m`、`7` |
| `category` | 它属于哪一类 | `KEYWORD`、`IDENTIFIER` |
| `value` | 程序内部使用的符号值 | `CONSTSYM`、`IDENT`、`EQL` |

---

## 1. 先看程序总流程

不要把全部代码从上到下当成一条直线看。这个程序有四个主层次：

```text
main()
  负责准备文件名

run()
  负责打开文件、反复调用 getsym()

getsym()
  负责识别一个 Token
  例如关键字、标识符、整数或特殊符号

getch()
  负责向文件要下一个字符
  它不理解 Token，只负责读取字符
```

合作方式可以画成：

```text
main
  |
  v
run ---------> getsym ---------> getch ---------> reader.readLine()
   |              |                 |
   |              |                 +-- 读取当前行中的一个字符
   |              |
   |              +-- 跳过空白和注释
   |              +-- 识别标识符、整数、特殊符号
   |              +-- 记录 Token
   |
   +-- 最终调用 printTokens() 打印结果
```

最重要的理解点是：

```text
main 负责“从哪里读”
run  负责“什么时候结束”
getsym 负责“这段字是什么”
getch 负责“下一个字符是什么”
```

---

## 2. 常量和枚举：先把名字和数字固定下来

### 2.1 三个长度常量

```java
private static final int MAX_IDENTIFIER_LENGTH = 10;
private static final int MAX_NUMBER_LENGTH = 14;
private static final int ERROR_LINE_WIDTH = 80;
```

`private` 表示只能在 `Pl0Lexer` 类内部使用。

`static` 表示它们属于类，不属于某一个 `Pl0Lexer` 对象。

`final` 表示赋值以后不能再修改。

这三个名字比直接到处写 `10`、`14`、`80` 更容易理解：

| 名字 | 值 | 为什么使用它 |
|---|---:|---|
| `MAX_IDENTIFIER_LENGTH` | 10 | PL/0 标识符最多保存 10 个字符 |
| `MAX_NUMBER_LENGTH` | 14 | PL/0 整数最多允许 14 位 |
| `ERROR_LINE_WIDTH` | 80 | 教材式错误标记最多铺到 80 个字符的位置 |

### 2.2 `Symbol` 枚举

```java
private enum Symbol {
    NUL, IDENT, NUMBER, PLUS, MINUS,
    TIMES, SLASH, ODDSYM, EQL, NEQ,
    LSS, LEQ, GTR, GEQ, LPAREN,
    RPAREN, COMMA, SEMICOLON, PERIOD, BECOMES,
    BEGINSYM, ENDSYM, IFSYM, THENSYM, WHILESYM,
    WRITESYM, READSYM, DOSYM, CALLSYM, CONSTSYM,
    VARSYM, PROCSYM
}
```

枚举的作用是规定“程序内部允许出现的符号种类”。

例如程序中出现 `const` 时，最终令：

```java
currentSymbol = Symbol.CONSTSYM;
```

它不保存字符串 `"CONSTSYM"`，而是保存一个固定的枚举对象。这样做的好处是：

1. 拼错 `Symbol.CONSTSYM` 时编译器会报错。
2. 不需要自己记每一种符号对应的整数编号。
3. 输出时使用 `currentSymbol.name()` 可以取得名字 `"CONSTSYM"`。

常见符号对应关系如下：

| 源程序形式 | 枚举 | Token 表中的 Value |
|---|---|---|
| `+` | `PLUS` | `PLUS` |
| `:=` | `BECOMES` | `BECOMES` |
| `#` | `NEQ` | `NEQ` |
| `10` | `NUMBER` | `NUMBER` |
| `sum` | `IDENT` | `IDENT` |
| `const` | `CONSTSYM` | `CONSTSYM` |
| `write` | `WRITESYM` | `WRITESYM` |

`NUL` 表示空符号或非法符号。例如单独出现的 `:` 会被设为 `NUL`。

### 2.3 `TokenCategory` 枚举

```java
private enum TokenCategory {
    IDENTIFIER, INTEGER, KEYWORD, SPECIAL, ERROR
}
```

它规定 Token 表第二列只能是五种分类之一：

| 分类 | 对应内容 | 例子 |
|---|---|---|
| `IDENTIFIER` | 普通标识符 | `x`、`sum` |
| `INTEGER` | 整常数 | `7`、`123` |
| `KEYWORD` | 关键字 | `begin`、`read` |
| `SPECIAL` | 特殊符号 | `+`、`:=`、`#` |
| `ERROR` | 词法错误记录 | 非法字符、整数过长 |

如果这里使用普通字符串：

```java
String category = "IDENTIFER"; // 拼错也不会马上报错
```

程序可能直到运行时才出现错误。枚举可以避免这种拼写错误。

---

## 3. `Token` 类：Token 表中的一行

### 3.1 它解决什么问题

每识别出一个单词，都需要保存三项信息：

```text
word + category + value
```

如果使用三个平行列表，就必须保证三个列表的下标始终同步，很容易出错。所以程序用一个小类把三项信息放在一起。

### 3.2 字段和构造方法

```java
private static final class Token {
    private final String word;
    private final TokenCategory category;
    private final String value;

    private Token(String word, TokenCategory category, String value) {
        this.word = word;
        this.category = category;
        this.value = value;
    }
}
```

这三个字段都写了 `final`：

```text
Token 创建完成以后，这三个值不再修改。
```

构造方法：

```java
private Token(String word, TokenCategory category, String value)
```

是一个函数。它的作用是接收三个参数，并把它们保存到当前对象中。

这里出现三次 `this`：

```java
this.word = word;
this.category = category;
this.value = value;
```

`this.word` 表示“当前对象的 `word` 字段”，右边的 `word` 表示“传入方法的参数”。如果两边名字相同，就必须写 `this` 才能明确区分。

例如：

```java
new Token("x", TokenCategory.IDENTIFIER, "x")
```

会创建：

```text
word     = x
category = IDENTIFIER
value    = x
```

---

## 4. 两组符号集合和三类容器

### 4.1 `KEYWORD_SYMBOLS`

```java
private static final Set<Symbol> KEYWORD_SYMBOLS = EnumSet.of(
        Symbol.ODDSYM,
        Symbol.BEGINSYM,
        ...
);
```

它保存所有关键字枚举。以后只需要写：

```java
KEYWORD_SYMBOLS.contains(currentSymbol)
```

就能判断“当前符号是不是关键字”。

为什么不用下标范围判断？因为那种写法和枚举排列顺序绑在一起。只要有人调整枚举顺序，原来的代码就可能悄悄出错。`Set.contains` 看的是一组明确列出的成员，意图更直接。

### 4.2 `SPECIAL_SYMBOLS`

```java
private static final Set<Symbol> SPECIAL_SYMBOLS = EnumSet.of(
        Symbol.PLUS,
        Symbol.MINUS,
        ...
);
```

它保存所有特殊符号。`recordCurrentToken()` 使用它判断当前 Token 是否应该写成 `SPECIAL`。

注意：`ODDSYM` 属于关键字集合，不属于这个特殊符号集合。

### 4.3 关键字表和特殊字符表

```java
private final Map<String, Symbol> keywords = new LinkedHashMap<>();
private final Map<Character, Symbol> specialCharacters = new LinkedHashMap<>();
```

`Map` 是“键到值的映射”，可以理解成一本字典。

关键字表的键是字符串，值是枚举：

```text
"begin" -> BEGINSYM
"read"  -> READSYM
"write" -> WRITESYM
```

特殊字符表的键是单个字符，值是枚举：

```text
'+' -> PLUS
'#' -> NEQ
';' -> SEMICOLON
```

使用 `LinkedHashMap` 是为了保持插入顺序，方便调试时观察内容。这个程序的识别正确性不依赖遍历顺序。

### 4.4 Token 列表

```java
private final List<Token> tokens = new ArrayList<>();
```

每识别出一个 Token，程序不会立刻打印，而是先加入列表：

```java
tokens.add(new Token(...));
```

程序全部分析结束后，`printTokens()` 再统一输出。这样可以让源码回显和 Token 表分开显示。

---

## 5. 程序运行状态：这些字段就是“当前进度”

```java
private BufferedReader reader;
private String currentLine = "";
private int currentColumn;
private char currentChar = ' ';
private Symbol currentSymbol = Symbol.NUL;
private String identifier = "";
private String numberText = "";
private int codeAddress;
private int errorCount;
private boolean inputFinished;
```

逐个理解：

| 字段 | 保存什么 | 为什么要保存 |
|---|---|---|
| `reader` | 已打开的源文件读取器 | `getch()` 要不断向它读取下一行 |
| `currentLine` | 当前源码行，末尾额外带一个空格 | `getch()` 从这一行取字符 |
| `currentColumn` | 当前读到第几个字符 | 同时用于计算错误标记的位置 |
| `currentChar` | 当前字符 | `getsym()` 根据它判断单词种类 |
| `currentSymbol` | 当前识别出的符号种类 | 识别完成后记录 Token |
| `identifier` | 刚识别出的标识符文字 | 关键字判断、标识符 Token 都要使用 |
| `numberText` | 刚识别出的整数字符串 | 保留原始数字，例如 `007` |
| `codeAddress` | 教材格式中的代码地址 | 本程序只做词法分析，所以始终是 0 |
| `errorCount` | 共发现多少个词法错误 | 决定程序最终返回 0 还是 1 |
| `inputFinished` | 文件是否已经读完 | 防止程序继续读取不存在的字符 |

`currentLine` 末尾补空格的原因是：让每一行最后一个 Token 后面也有一个结束字符，`getch()` 可以统一处理，不需要为“行尾”单独写很多特殊情况。

---

## 6. `main(String[] args)`：程序入口

### 6.1 作用

`main()` 负责三件事：

1. 判断是“命令行给了文件名”还是“需要从键盘输入文件名”。
2. 创建 `Pl0Lexer` 对象。
3. 调用 `run()`，并根据返回值决定是否设置退出码。

它不负责读取源码字符，也不负责识别 Token。

### 6.2 代码

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

### 6.3 逐句解释

`String[] args` 是 Java 命令行参数数组。

如果运行：

```bat
java -cp lexical-analysis\java\classes Pl0Lexer lexical-analysis\java\tests\basic.pl0
```

则：

```text
args.length = 1
args[0] = lexical-analysis\java\tests\basic.pl0
```

如果直接运行：

```bat
java -cp lexical-analysis\java\classes Pl0Lexer
```

则：

```text
args.length = 0
```

第一段：

```java
if (args.length > 1) {
    System.out.println("Usage: java Pl0Lexer [source.pl0]");
    return;
}
```

最多只接受一个文件名。给出两个或更多参数时，输出用法并结束。

```java
String fileName = args.length == 1
        ? args[0]
        : readFileNameFromKeyboard();
```

这是三元运算符：

```text
如果 args.length == 1 成立，fileName = args[0]
否则，fileName = readFileNameFromKeyboard() 的结果
```

然后：

```java
Pl0Lexer lexer = new Pl0Lexer();
```

创建词法分析器对象。创建后，它拥有了 `reader`、`tokens`、`keywords` 等运行状态。

```java
int exitCode = lexer.run(fileName);
```

正式开始分析。`run()` 返回：

```text
0：没有词法错误
1：有词法错误，或者文件无法打开或读取
```

### 6.4 条件卡片

| 条件 | 条件含义 | 为什么这样判断 |
|---|---|---|
| `args.length > 1` | 命令行参数多于一个 | 本程序只支持一个 PL/0 源文件，多余参数无法定义清楚 |
| `args.length == 1` | 命令行正好给了一个文件路径 | 有路径时直接使用，不需要再让用户在键盘输入 |
| `fileName == null` | 用户没有输入有效文件名 | 没有文件名就无法打开文件，只能结束程序 |
| `args.length == 1` | 当前文件名来自命令行 | 为了让回显和教材提示格式一致，把文件名补打印出来 |
| `exitCode != 0` | 程序运行结果不是“正常” | 只有非零退出码才需要主动调用 `System.exit()` |

### 6.5 为什么正常时不写 `System.exit(0)` 

Java 的 `main()` 正常走到最后时，进程默认以 0 结束，所以不需要额外调用 `System.exit(0)`。只有错误退出码需要主动设置。

---

## 7. `readFileNameFromKeyboard()`：从键盘读取文件名

### 7.1 作用

当命令行没有文件名时，提示用户从键盘输入一个路径，并处理空输入和读取异常。

### 7.2 代码

```java
private static String readFileNameFromKeyboard() {
    try {
        BufferedReader keyboard = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
        String fileName = keyboard.readLine();
        if (fileName == null) {
            return null;
        }
        fileName = fileName.trim();
        return fileName.isEmpty() ? null : fileName;
    } catch (IOException exception) {
        System.out.println("Can't read the file name!");
        return null;
    }
}
```

### 7.3 逐句解释

```java
BufferedReader keyboard = ...
```

把标准输入 `System.in` 包装起来，使代码可以使用 `readLine()`。

`readLine()` 会读取一行，并自动去掉行尾换行符。

```java
fileName = fileName.trim();
```

去掉用户输入前后的空格。例如：

```text
"  lexical-analysis\java\tests\basic.pl0  "
```

会变成：

```text
"lexical-analysis\java\tests\basic.pl0"
```

### 7.4 条件卡片

| 条件 | 条件含义 | 为什么这样判断 |
|---|---|---|
| `fileName == null` | 输入流已经结束，没有读到任何一行 | `null` 不能继续调用 `trim()`，必须提前返回 |
| `fileName.isEmpty()` | 去空格后得到空字符串 | 空路径无法打开文件，应当按“没有输入”处理 |
| `catch (IOException)` | 读取键盘时发生输入异常 | 不能让异常直接让程序崩溃，需要给出可理解的提示 |

### 7.5 为什么不用 `Scanner`

`Scanner` 也可以读取文件名，但本程序只读一行，`BufferedReader.readLine()` 更直接，也不需要处理 `Scanner` 的分词规则。例如文件名中如果有空格，`Scanner.next()` 只会读到第一个空格前的内容，而 `readLine()` 会读取整行。

---

## 8. `run(String fileName)`：整个分析过程的总指挥

### 8.1 作用

`run()` 负责：

1. 打开文件。
2. 初始化运行状态。
3. 反复调用 `getsym()`。
4. 输出 Token 表。
5. 返回退出码。

### 8.2 代码

```java
private int run(String fileName) {
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
    } catch (FileNotFoundException exception) {
        System.out.println("Can't open file!");
        return 1;
    } catch (IOException exception) {
        System.out.println("Read file failed: " + exception.getMessage());
        return 1;
    }
}
```

### 8.3 逐句解释

```java
try (BufferedReader fileReader = ...) {
```

这是 Java 的 `try-with-resources` 写法。文件读取器放在小括号中，代码块结束后 Java 会自动调用 `close()` 关闭文件。

因此这里不需要手动写：

```java
finally {
    fileReader.close();
}
```

创建读取器的顺序是：

```text
FileInputStream       负责按字节打开文件
InputStreamReader     把字节按 UTF-8 解释成字符
BufferedReader        提供 readLine()，一次读取一行
```

```java
reader = fileReader;
```

把局部读取器保存到对象的 `reader` 字段中。这样 `getch()` 和 `readNextLine()` 才能使用同一个读取器。

```java
init();
```

把所有表和状态重置。即使以后同一个对象被重复运行，旧 Token 也不会残留。

核心循环：

```java
while (currentSymbol != Symbol.PERIOD && getsym()) {
}
```

这句有两个条件，并且使用短路计算：

1. 先判断 `currentSymbol != Symbol.PERIOD`。
2. 只有第一部分成立时，才调用 `getsym()`。

为什么先判断句号？

PL/0 程序以 `.` 结束。如果上一个 Token 已经是 `PERIOD`，就没有必要再读取下一个字符。此时直接结束循环。

为什么还要调用 `getsym()`？

`getsym()` 返回 `true` 表示成功识别了一个 Token；返回 `false` 表示文件结束或发生无法继续读取的情况。只有它返回 `true`，循环才继续。

等价写法是：

```java
while (true) {
    if (currentSymbol == Symbol.PERIOD) {
        break;
    }
    if (!getsym()) {
        break;
    }
}
```

原来的写法把两个终止条件直接写在 `while` 条件中，代码更短，但必须理解 `&&` 的短路规则。

最后：

```java
return errorCount == 0 ? 0 : 1;
```

意思是：

```text
没有错误：返回 0
有错误：返回 1
```

### 8.4 条件卡片

| 条件 | 条件含义 | 为什么这样判断 |
|---|---|---|
| `currentSymbol != Symbol.PERIOD` | 当前 Token 还不是句号 | 句号代表程序结束，出现后不必继续读取 |
| `getsym()` | 成功识别出一个 Token | 返回 `false` 时表示文件结束或无法继续 |
| `errorCount == 0` | 没有记录任何词法错误 | 无错误返回正常退出码 0，有错误返回 1 |
| `catch (FileNotFoundException)` | 文件不存在或无法打开 | 给出明确提示，不让 Java 默认异常堆栈吓到用户 |
| `catch (IOException)` | 打开后读取过程中发生异常 | 区分“打不开”和“打开后读失败”，方便定位问题 |

---

## 9. `init()`：清空旧状态，建立查找表

### 9.1 作用

`init()` 建立两张查找表：

```text
关键字字符串 -> Symbol
特殊字符     -> Symbol
```

同时把 `currentLine`、`currentChar`、错误计数等全部重置。

### 9.2 代码前半部分：关键字表

```java
keywords.clear();
keywords.put("begin", Symbol.BEGINSYM);
keywords.put("call", Symbol.CALLSYM);
keywords.put("const", Symbol.CONSTSYM);
...
keywords.put("write", Symbol.WRITESYM);
```

`clear()` 先清空旧数据，防止重复初始化时保留旧内容。

`put(键, 值)` 负责加入映射：

```text
"begin" -> BEGINSYM
"read"  -> READSYM
"write" -> WRITESYM
```

这就是程序能够区分“标识符”和“关键字”的基础。识别出 `read` 后，只要查表得到 `READSYM`，它就不是普通标识符。

### 9.3 代码后半部分：特殊字符表

```java
specialCharacters.clear();
specialCharacters.put('+', Symbol.PLUS);
specialCharacters.put('-', Symbol.MINUS);
...
```

单字符特殊符号格式统一，所以使用表格比写一长串 `if-else` 更清楚。

### 9.4 重置运行状态

```java
currentLine = "";
currentColumn = 0;
currentChar = ' ';
currentSymbol = Symbol.NUL;
identifier = "";
numberText = "";
codeAddress = 0;
errorCount = 0;
inputFinished = false;
tokens.clear();
```

`codeAddress = 0` 是因为本程序只做词法分析，没有代码生成阶段。教材回显格式里仍然保留这个位置。

### 9.5 条件和循环卡片

`init()` 本身没有 `if` 或 `while`。它只做确定性初始化。

不过有一个重要设计原因：把表放在 `init()` 而不是在字段声明后立即写死，可以让程序运行前明确执行一次“完整重置”。这对以后增加“重复分析同一个对象”的功能更安全。

---

## 10. `getch()`：取得下一个字符

### 10.1 作用

这是词法分析器的“字符入口”。它不关心字符是不是关键字，只负责回答一个问题：

> 当前源文件中，下一个字符是什么？

读取成功返回 `0`，文件结束返回 `-1`。

### 10.2 代码

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

### 10.3 为什么返回 `int` 而不是 `char`

读取字符时有两种结果：

```text
成功：得到一个字符
失败：文件已经结束
```

如果返回 `char`，就必须另外想办法表示“没有字符”。返回 `int` 时：

```text
0 表示读取成功
-1 表示文件结束
```

这个约定和 C 语言中的 `fgetc()` 很接近，也让调用者可以写：

```java
if (getch() == -1) {
    return false;
}
```

### 10.4 逐句解释

```java
if (currentColumn >= currentLine.length())
```

`currentColumn` 是当前要读取的位置，`currentLine.length()` 是当前行长度。

如果位置已经到达或超过长度，表示当前行读完了，需要读取下一行。

这里使用 `>=` 而不是 `==` 更安全。即使由于逻辑错误导致 `currentColumn` 意外超过长度，也会进入“读取下一行”的分支，而不是继续访问不存在的字符。

```java
if (!readNextLine()) {
    return -1;
}
```

`readNextLine()` 返回 `true` 表示成功读到一行，返回 `false` 表示文件结束或读取失败。

前面的 `!` 表示取反：

```text
readNextLine() == true   -> !true == false -> 不进入 if
readNextLine() == false  -> !false == true -> 进入 if，返回 -1
```

成功读到一行后：

```java
currentChar = currentLine.charAt(currentColumn);
currentColumn++;
```

先取出当前位置的字符，再把位置向后移动一格。

例如：

```text
当前行：const
currentColumn 原来为 0
取出 'c'
currentColumn 变成 1
```

下次调用 `getch()` 就会取出 `'o'`。

### 10.5 条件卡片

| 条件 | 条件含义 | 为什么这样判断 |
|---|---|---|
| `currentColumn >= currentLine.length()` | 当前行已经读完 | 需要读取下一行，不能访问超出字符串长度的位置 |
| `!readNextLine()` | 读取下一行没有成功 | 文件已经结束或读取异常，必须返回 `-1` |
| `getch() == -1` | 调用者看到文件结束 | 这是 `getch()` 向所有上层函数传递“无法继续读取”的统一信号 |

### 10.6 一个完整例子

源程序：

```text
const m = 7;
```

第一次读取时：

```text
readNextLine() 读入 "const m = 7;"
currentLine = "const m = 7; "
currentColumn = 0
getch() 取出 'c'，currentColumn = 1
```

连续调用后，`currentChar` 依次变成：

```text
c -> o -> n -> s -> t -> 空格 -> m -> 空格 -> = -> 空格 -> 7 -> ; -> 行末空格
```

`getch()` 不知道这些字符能组成什么 Token。它只负责一个字符一个字符地往前移动。

---

## 11. `readNextLine()`：读取并回显一行

### 11.1 作用

`readNextLine()` 是 `getch()` 的辅助函数。它使用 `reader.readLine()` 读取一整行，并把源码回显到屏幕，同时处理 EOF 和读取异常。

### 11.2 代码

```java
private boolean readNextLine() {
    currentColumn = 0;
    currentLine = "";

    if (inputFinished) {
        System.out.print("program incomplete\n");
        return false;
    }

    String line;
    try {
        line = reader.readLine();
    } catch (IOException exception) {
        System.out.print("Read file failed: " + exception.getMessage() + "\n");
        inputFinished = true;
        return false;
    }

    if (line == null) {
        inputFinished = true;
        System.out.print(codeAddress + " \nprogram incomplete\n");
        return false;
    }

    currentLine = line + ' ';
    System.out.print(codeAddress + " " + line + "\n\n");
    return true;
}
```

### 11.3 逐句解释

```java
currentColumn = 0;
currentLine = "";
```

准备读取新行，所以把列指针归零，并清空旧行。

```java
reader.readLine()
```

读取一行内容，自动去掉行尾换行符。比如文件中是：

```text
const m = 7;\n
```

返回的是：

```text
"const m = 7;"
```

如果文件已经结束，`readLine()` 返回 `null`。

```java
currentLine = line + ' ';
```

末尾补一个空格。它相当于一个“行尾哨兵”，保证当前行的最后一个 Token 后面还有一个可以读取的字符。

例如：

```text
原始行：const
存入 currentLine 后：const 
```

```java
System.out.print(codeAddress + " " + line + "\n\n");
```

回显格式：

```text
0 const m = 7;

```

`codeAddress` 在本程序里始终是 0，所以每一行前都显示 `0 `。两个 `\n` 会让源码回显和 Token 表之间保持教材式的空行。

### 11.4 条件卡片

| 条件 | 条件含义 | 为什么这样判断 |
|---|---|---|
| `inputFinished` | 之前已经检测到文件结束 | 不能重复调用 `readLine()`；直接返回失败 |
| `line == null` | `readLine()` 没有读到新行 | Java 用 `null` 表示 EOF，需要设置结束标志并返回失败 |
| `catch (IOException)` | 读取文件时发生异常 | 把异常转换成用户能看懂的提示，并阻止上层继续读取 |

### 11.5 为什么不是 `reader.read()` 一个字符一个字符读

逐字符读也可以实现，但会把行号、行尾、换行符和回显混在一起，代码更难读。

现在的分工是：

```text
readNextLine() 负责“读一行”
getch()         负责“从这一行拿一个字符”
```

两层分开后，每一层都只关心一个尺寸单位：一个行、一个字符。

---

## 12. `getsym()`：识别一个 Token 的总调度函数

### 12.1 作用

这是词法分析器最核心的函数。它根据当前字符决定下一步：

```text
空白       -> 跳过
注释       -> 跳过
小写字母   -> 标识符或关键字
数字       -> 整数
冒号       -> :=
小于号     -> < 或 <=
大于号     -> > 或 >=
其他单字符 -> 查特殊符号表
```

它不直接读取每一行，也不直接操作 `FileInputStream`。它使用 `getch()` 得到字符，再把具体识别工作交给更小的方法。

### 12.2 第一层循环：跳过空白和注释

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

这里的 `while (true)` 表示“只要还没有找到可以识别的非空白字符，就继续处理”。

| 条件 | 条件含义 | 为什么这样判断 |
|---|---|---|
| `inputFinished` | 跳过空白后已经到达文件末尾 | 没有字符可以识别，`getsym()` 返回 `false` |
| `currentChar != '/'` | 当前字符不是斜杠 | 不是注释开头，也不是除号，可以跳出预处理循环 |
| `getch() == -1` | 斜杠后面没有字符 | 文件不完整，当前函数返回 `false` |
| `currentChar != '*'` | 斜杠后面的字符不是星号 | 说明它不是注释，只是普通除号 `/` |
| `!skipComment()` | 注释没有成功结束 | 注释未闭合时无法继续词法分析 |

为什么读取到 `/` 后要再看下一个字符？

因为 `/` 有两种可能：

```text
/x  -> 普通除号
/*  -> 注释开始
```

`getsym()` 已经看到 `/`，所以必须再读取一个字符才能区分两者。

### 12.3 第二层：根据首字符分类

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

`tokenReady` 表示“是否已经准备好一个可以记录的 Token”。

| 条件 | 为什么先判断它 |
|---|---|
| `isLowercaseLetter(currentChar)` | 字母开头通常是标识符或关键字 |
| `isDigit(currentChar)` | 数字开头按整数读取 |
| `currentChar == ':'` | 冒号后面可能是 `=`，要单独判断 `:=` |
| `currentChar == '<'` | `<` 和 `<=` 是两种不同 Token |
| `currentChar == '>'` | `>` 和 `>=` 是两种不同 Token |
| 最后的 `else` | 前面都不成立时，尝试按单字符特殊符号查表 |

注意 `<=` 和 `>=` 的判断必须放在 `<`、`>` 的首字符分支中，不能交给单字符表处理。

### 12.4 记录结果和返回值

```java
if (tokenReady) {
    recordCurrentToken();
}
return tokenReady || !inputFinished;
```

为什么不能无条件记录？

因为有些函数只负责“报错并让调用者继续”，不一定生成了一个正常 Token。例如单独 `:` 会先报告错误，然后不记录普通 Token。

返回值的含义：

```text
tokenReady == true         -> 已经处理出一个 Token，继续
tokenReady == false 且 EOF -> 不能继续
tokenReady == false 但未 EOF -> 记录错误后还要继续处理后续字符
```

所以最后使用：

```java
return tokenReady || !inputFinished;
```

如果 `tokenReady` 是假、但文件还没结束，就返回 `true`，让 `run()` 的循环继续处理下一个字符。

---

## 13. `skipWhitespace()`：跳过没有意义的空白

### 13.1 作用

空格、制表符、回车和换行不会形成 Token，因此需要全部跳过，直到遇到真正有意义的字符。

### 13.2 代码

```java
private void skipWhitespace() {
    while (currentChar == ' ' || currentChar == '\n'
            || currentChar == '\r' || currentChar == '\t') {
        if (getch() == -1) {
            return;
        }
    }
}
```

### 13.3 条件卡片

`while` 条件是：

```java
currentChar == ' ' || currentChar == '\n'
|| currentChar == '\r' || currentChar == '\t'
```

只要当前字符是四种空白字符之一，就继续读取下一个字符：

| 字符 | 含义 |
|---|---|
| `' '` | 空格 |
| `'\n'` | 换行 |
| `'\r'` | 回车，Windows 换行的一部分 |
| `'\t'` | 制表符 |

`if (getch() == -1)` 的条件是“下一字符无法读取”。出现这种情况说明已经到文件末尾，函数直接返回，让 `getsym()` 的下一层检查 `inputFinished`。

### 13.4 为什么空白不能作为 Token

PL/0 源程序里的空格主要用来分隔单词：

```text
const m
```

空格让 `const` 和 `m` 在视觉上分开，但词法规则不需要把空格本身放进 Token 表。因此词法分析器必须忽略它们。

---

## 14. `skipComment()`：跳过多行注释

### 14.1 作用

当 `getsym()` 确认当前是 `/*` 后，`skipComment()` 负责一直读取到 `*/`。

### 14.2 代码

```java
private boolean skipComment() {
    if (getch() == -1) {
        error(38);
        return false;
    }

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
}
```

### 14.3 逐段解释

调用 `skipComment()` 之前，`getsym()` 已经读到了：

```text
/
*
```

因此函数开头的：

```java
if (getch() == -1) {
    error(38);
    return false;
}
```

作用是把读取位置移到 `/*` 后面的第一个字符。

如果这一步就遇到文件结束，说明 `/*` 后面根本没有注释内容，也没有结束符，因此报告错误码 38。

主循环：

```java
while (true) {
```

含义是“一直找结束符，除非遇到 EOF 或错误”。

```java
if (currentChar == '*') {
```

只有遇到星号时，才有可能是结束标记 `*/` 的开头。

然后读取下一个字符：

```java
if (getch() == -1) {
    error(38);
    return false;
}
```

如果星号后面已经是文件末尾，说明注释不可能再以 `*/` 正常结束。

```java
if (currentChar == '/') {
    ...
    return true;
}
```

如果一个 `*` 后面紧跟着 `/`，就找到了结束标记。

找到结束标记后还会调用一次 `getch()`，把读取位置移动到 `*/` 后面的字符，返回 `true` 告诉 `getsym()` 注释结束。

如果当前不是结束标记：

```java
} else if (getch() == -1) {
    error(38);
    return false;
}
```

就继续读取下一个字符。如果读到 EOF，仍然说明注释没有闭合。

### 14.4 条件卡片

| 条件 | 条件含义 | 为什么这样判断 |
|---|---|---|
| 第一个 `getch() == -1` | `/*` 后没有字符可读 | 注释不可能完整结束，错误码 38 |
| `while (true)` | 没有找到结束符就继续 | 注释长度不确定，必须一直扫描 |
| `currentChar == '*'` | 当前字符可能是结束符开始 | 只有 `*` 后面的 `/` 才能组成 `*/` |
| 第一个内层 `getch() == -1` | 星号后面是 EOF | 注释没有闭合 |
| `currentChar == '/'` | 当前字符是斜杠 | 与前面的星号组成 `*/`，注释结束 |
| 结束后的 `getch() == -1` | `*/` 已经是文件最后两个字符 | 不需要报错，但返回 `false`，告诉上层没有后续字符 |
| 末尾 `else if (getch() == -1)` | 普通注释字符后面没有更多内容 | 没有遇到 `*/` 就结束，错误码 38 |

### 14.5 这个实现支持嵌套注释吗

不支持。它遇到第一个 `*/` 就认为注释结束。这符合当前教材风格版本的行为。

---

## 15. `readIdentifier()`：识别标识符和关键字

### 15.1 作用

当 `getsym()` 看到一个小写字母时，调用这个函数。它会一直读取字母和数字，得到完整的单词，然后判断：

```text
它在关键字表中 -> 关键字
它不在关键字表中 -> 普通标识符
```

### 15.2 代码

```java
private void readIdentifier() {
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
}
```

### 15.3 为什么要用 `do-while`

进入这个函数时，第一个字符已经由 `getsym()` 读到了，并且已经确认它是小写字母。因此至少要保存第一个字符。

`do-while` 的特点是：

```text
先执行一次循环体，再判断是否继续
```

如果用普通 `while`，就需要在循环外先保存第一个字符，代码会变长。

### 15.4 逐句解释

```java
StringBuilder fullIdentifier = new StringBuilder();
```

`String` 不能修改，`StringBuilder` 可以不断追加字符。因此用它保存正在读取的标识符。

```java
fullIdentifier.append(currentChar);
```

把当前字符追加到末尾。

例如依次读取：

```text
a -> ab -> abc
```

```java
if (getch() == -1) {
    break;
}
```

读取下一个字符。如果文件结束，就停止追加，避免继续访问不存在的字符。

`do-while` 的条件：

```java
isLowercaseLetter(currentChar) || isDigit(currentChar)
```

表示“下一个字符还是标识符的一部分”时继续。

为什么数字可以出现在标识符中，但不能作为首字符？

PL/0 规则如下：

```text
a1   合法标识符
abc9 合法标识符
1a   不是合法标识符形式
```

首字符是否合法已经由 `getsym()` 判断，进入这里后可以允许数字继续。

### 15.5 标识符长度判断

```java
boolean identifierTooLong = fullIdentifier.length() > MAX_IDENTIFIER_LENGTH;
```

如果长度超过 10，`identifierTooLong` 变成 `true`。

```java
if (identifierTooLong) {
    fullIdentifier.setLength(MAX_IDENTIFIER_LENGTH);
}
```

只保留前 10 个字符。这样后面的输出和教材版保持一致。

例如：

```text
abcdefghijk -> abcdefghij
```

然后：

```java
if (identifierTooLong) {
    error(36);
    recordErrorToken(identifier, "identifier is longer than 10 characters");
}
```

同时做两件事：

1. 输出错误码 36。
2. 在 Token 表中加入一条 `ERROR`。

注意：程序不会因为错误立刻停止，仍会继续分析后面的内容。

### 15.6 判断关键字

```java
currentSymbol = keywords.getOrDefault(identifier, Symbol.IDENT);
```

这是 `Map` 的“有就用，没有就给默认值”方法：

```text
identifier 在关键字表中 -> 返回对应 Symbol
identifier 不在表中     -> 返回 Symbol.IDENT
```

例如：

```text
id = "while" -> WHILESYM
id = "sum"   -> IDENT
```

### 15.7 条件卡片

| 条件 | 条件含义 | 为什么这样判断 |
|---|---|---|
| `getch() == -1` | 读取标识符过程中到文件末尾 | 无法继续读取，必须停止 |
| `isLowercaseLetter(currentChar) || isDigit(currentChar)` | 下一个字符是字母或数字 | 两者都是标识符后续字符 |
| `fullIdentifier.length() > MAX_IDENTIFIER_LENGTH` | 标识符超过 10 个字符 | 触发错误码 36，并截断到 10 个字符 |
| `identifierTooLong` | 当前标识符确实超长 | 只有超长时才报错和添加错误 Token |
| `keywords.getOrDefault(...)` | 查关键字表 | 存在则是关键字，不存在才是普通标识符 |

---

## 16. `readNumber()`：识别整常数

### 16.1 作用

当 `getsym()` 看到数字时，这个函数负责读取连续的十进制数字，并检查：

1. 位数是否超过 14。
2. 数字后面是否直接跟着字母。

### 16.2 代码

```java
private void readNumber() {
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
}
```

### 16.3 逐句解释

```java
StringBuilder digits = new StringBuilder();
```

创建用于保存数字文本的缓冲区。

```java
do {
    digits.append(currentChar);
    ...
} while (isDigit(currentChar));
```

第一个数字一定属于整数，因此先保存，再读取下一个字符。只要后面还是数字，就继续追加。

例如：

```text
123
```

最终得到：

```java
numberText = "123";
currentSymbol = Symbol.NUMBER;
```

注意 Token 的 `word` 和 `value` 都使用 `numberText`，所以 `007` 会保留成 `007`，不会因为整数变成 7 而丢失前导零。

### 16.4 条件卡片

| 条件 | 条件含义 | 为什么这样判断 |
|---|---|---|
| `getch() == -1` | 数字读取过程中到文件末尾 | 已经没有后续字符，必须停止 |
| `isDigit(currentChar)` | 下一个字符还是数字 | 连续数字属于同一个整数 Token |
| `numberText.length() > MAX_NUMBER_LENGTH` | 数字超过 14 位 | 触发错误码 30 |
| `isLowercaseLetter(currentChar)` | 数字后面紧跟着字母 | 例如 `12abc`，整数字和标识符之间缺少分隔，触发错误码 39 |

### 16.5 为什么数字后跟字母要报错

PL/0 的合法整数是：

```text
12
0
123456789
```

而下面的形式不是合法整数字：

```text
12abc
```

程序不会把它直接当成一个错误整体丢掉，而是：

```text
先产生 12 的 INTEGER Token
再产生错误码 39
随后 abc 作为 IDENTIFIER 继续识别
```

这样做可以让词法分析继续运行，也能在 Token 表中留下完整线索。

---

## 17. `readBecomes()`：识别 `:=`

### 17.1 作用

PL/0 的赋值符号是两个字符：

```text
:=
```

这个函数负责判断冒号后面是否紧跟着等号。

### 17.2 代码

```java
private boolean readBecomes() {
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
}
```

### 17.3 逐句解释

进入函数时，`currentChar` 是 `:`。

```java
if (getch() == -1) {
    return false;
}
```

读取冒号后面的字符。如果文件结束，无法判断完整符号，直接返回失败。

```java
if (currentChar != '=') {
```

如果下一个字符不是等号，说明只有单个冒号，例如：

```text
:
```

这不是合法的 PL/0 赋值符号。程序：

1. 把 `currentSymbol` 设为 `NUL`。
2. 报告错误码 19。
3. 添加一个 `ERROR` Token。
4. 返回 `false`，不产生正常的 `BECOMES` Token。

如果下一个字符是 `=`：

```java
currentSymbol = Symbol.BECOMES;
getch();
return true;
```

识别为 `BECOMES`，然后读取下一个字符，为后面的识别做准备。

### 17.4 条件卡片

| 条件 | 条件含义 | 为什么这样判断 |
|---|---|---|
| `getch() == -1` | 冒号后面没有字符 | 无法继续组成 `:=`，返回失败 |
| `currentChar != '='` | 冒号后面不是等号 | 单独 `:` 不合法，错误码 19 |
| 成功分支 | 冒号后面正好是等号 | 组成合法赋值符号 `:=` |

---

## 18. `readLessOperator()`：识别 `<` 和 `<=`

### 18.1 作用

判断当前 `<` 是单独的“小于”，还是和 `=` 组成“小于等于”。

### 18.2 代码

```java
private boolean readLessOperator() {
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
}
```

### 18.3 条件卡片

| 条件 | 条件含义 | 为什么这样判断 |
|---|---|---|
| `getch() == -1` | `<` 后面没有字符 | 无法判断是 `<` 还是 `<=`，返回失败 |
| `currentChar == '='` | `<` 后面紧跟等号 | 识别为小于等于 `LEQ` |
| `else` | `<` 后面不是等号 | 识别为普通小于 `LSS` |

读取到 `<=` 后还要调用一次 `getch()`，因为 `=` 已经被消耗掉，下一步应从 `=` 后面的字符开始。

---

## 19. `readGreaterOperator()`：识别 `>` 和 `>=`

### 19.1 作用

判断当前 `>` 是普通的“大于”，还是和 `=` 组成“大于等于”。

### 19.2 代码

```java
private boolean readGreaterOperator() {
    if (getch() == -1) {
        return false;
    }
    if (currentChar == '=') {
        currentSymbol = Symbol.GEQ;
        getch();
    } else {
        currentSymbol = Symbol.GTR;
    }
    return true;
}
```

### 19.3 条件卡片

| 条件 | 条件含义 | 为什么这样判断 |
|---|---|---|
| `getch() == -1` | `>` 后面没有字符 | 无法判断是 `>` 还是 `>=` |
| `currentChar == '='` | `>` 后面紧跟等号 | 识别为大于等于 `GEQ` |
| `else` | `>` 后面不是等号 | 识别为普通大于 `GTR` |

这个函数和 `readLessOperator()` 的结构完全相同，只是枚举值不同。

---

## 20. `readSingleSpecialCharacter()`：识别单字符特殊符号

### 20.1 作用

处理 `+`、`-`、`*`、`/`、`(`、`)`、`=`、`,`、`.`、`#`、`;` 等单字符特殊符号。

### 20.2 代码

```java
private boolean readSingleSpecialCharacter() {
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
}
```

### 20.3 逐句解释

```java
Symbol symbol = specialCharacters.get(currentChar);
```

在特殊字符表中查找当前字符。

如果找到，`symbol` 是例如 `PLUS`、`EQL`；如果没找到，返回 `null`。

### 20.4 条件卡片

| 条件 | 条件含义 | 为什么这样判断 |
|---|---|---|
| `symbol == null` | 当前字符不在特殊符号表中 | 它不是合法 PL/0 特殊符号，例如 `@`，要报错误码 37 |
| `currentSymbol != Symbol.PERIOD` | 当前符号不是句号 | 普通符号后要继续读取下一个字符 |

为什么句号特殊处理？

`.` 是 PL/0 程序的结束标记。识别到 PERIOD 后，主循环已经可以停止，不需要再尝试读取下一个字符。这样可以避免在文件末尾多读一次并产生不必要的提示。

非法字符分支会调用 `getch()`，目的是把非法字符消耗掉，避免下一次循环又看到同一个字符。然后返回 `false`。这不是“文件结束”的意思，而是“这里没有产生正常 Token”。

---

## 21. `error(int code)`：输出教材格式的错误位置

### 21.1 作用

当词法规则被破坏时，这个函数负责输出类似：

```text
****           !39
```

其中：

* `****` 是固定前缀。
* 中间的空白决定错误指向哪一列。
* `!39` 是错误码。

### 21.2 代码

```java
private void error(int code) {
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
}
```

### 21.3 为什么是 `currentColumn - 1`

`currentChar` 被取出后，`currentColumn` 会向后移动一格。

假设错误发生在第 5 个字符：

```text
读取前 currentColumn = 4
读取后 currentColumn = 5
```

错误标记前面需要 4 个空格，因此写：

```java
currentColumn - 1
```

### 21.4 条件卡片

| 条件 | 条件含义 | 为什么这样判断 |
|---|---|---|
| `currentColumn > 0` | 当前列不是 0 | 0 无法减一得到有效的前导空格数 |
| `currentColumn <= ERROR_LINE_WIDTH` | 当前列不超过 80 | 教材错误标记最多只铺到第 80 列 |
| 两个条件同时成立 | 位置在 1 到 80 之间 | 使用 `currentColumn - 1` 精确对齐 |
| 否则 | 位置是 0、超过 80，或刚进入新行 | 使用固定宽度 80，避免打印过多空格 |

### 21.5 `for` 循环在做什么

```java
for (int i = 0; i < spaceCount; i++) {
    message.append(' ');
}
```

循环变量 `i` 从 0 开始，每轮加 1，直到 `i < spaceCount` 不成立为止。

如果 `spaceCount` 是 4，会加入 4 个空格：

```text
****    !39
```

最后：

```java
errorCount++;
```

把错误总数加一。`run()` 最终根据它决定返回 0 还是 1。

---

## 22. `recordCurrentToken()`：把当前符号加入 Token 表

### 22.1 作用

`currentSymbol` 只是一个枚举值，例如 `EQL`。这个函数负责把它转换成 Token 表中的一行，包括：

```text
word、category、value
```

### 22.2 代码

```java
private void recordCurrentToken() {
    if (currentSymbol == Symbol.IDENT) {
        addToken(identifier, TokenCategory.IDENTIFIER, identifier);
    } else if (currentSymbol == Symbol.NUMBER) {
        addToken(numberText, TokenCategory.INTEGER, numberText);
    } else if (KEYWORD_SYMBOLS.contains(currentSymbol)) {
        addToken(identifier, TokenCategory.KEYWORD, currentSymbol.name());
    } else if (SPECIAL_SYMBOLS.contains(currentSymbol)) {
        addToken(symbolText(currentSymbol), TokenCategory.SPECIAL, currentSymbol.name());
    }
}
```

### 22.3 四类分支

| 条件 | 例子 | 添加的 Token |
|---|---|---|
| `currentSymbol == Symbol.IDENT` | `sum` | `sum / IDENTIFIER / sum` |
| `currentSymbol == Symbol.NUMBER` | `123` | `123 / INTEGER / 123` |
| `KEYWORD_SYMBOLS.contains(currentSymbol)` | `read` | `read / KEYWORD / READSYM` |
| `SPECIAL_SYMBOLS.contains(currentSymbol)` | `:=` | `:= / SPECIAL / BECOMES` |

为什么标识符的 `word` 和 `value` 都是 `identifier`？

因为普通标识符没有固定 Symbol 值，它本身的名字就是它的值。

为什么关键字的 `value` 是 `currentSymbol.name()`？

因为关键字在程序内部对应固定枚举，例如：

```text
read -> READSYM
write -> WRITESYM
```

为什么特殊符号要调用 `symbolText()`？

因为枚举名不一定是源程序字符。例如：

```text
Symbol.BECOMES.name() -> "BECOMES"
```

但 Token 表第一列应该显示源程序中的 `:=`，所以需要 `symbolText()`。

### 22.4 条件卡片

这里的 `if-else if` 是从上到下依次判断。只要前一个条件成立，后面的条件就不会再判断。

判断顺序很重要：

1. 先判断 `IDENT`，因为它是普通标识符。
2. 再判断 `NUMBER`，因为整数需要保留原文。
3. 再判断关键字集合。
4. 最后判断特殊符号集合。

如果 `currentSymbol` 是 `NUL`，四个条件都不成立，函数不加入普通 Token。错误 Token 已经由具体错误分支单独记录。

---

## 23. `recordErrorToken(String word, String message)`

### 23.1 作用

把一条词法错误加入 Token 表，分类固定为 `ERROR`。

### 23.2 代码

```java
private void recordErrorToken(String word, String message) {
    addToken(word, TokenCategory.ERROR, message);
}
```

### 23.3 参数解释

`word` 表示错误附近的文字，可能是：

```text
""
":"
"@"
"abcdefghij"
```

`message` 是错误原因，例如：

```text
number cannot be followed by a letter
identifier is longer than 10 characters
```

### 23.4 条件卡片

这个函数没有 `if` 或 `while`。它只做一次固定的 Token 添加操作。

单独封装它的原因是：错误 Token 的类别永远是 `ERROR`，不需要每个调用点都重复写一次 `TokenCategory.ERROR`。

---

## 24. `addToken(...)`：真正向列表加入一行

### 24.1 作用

把三个参数打包成一个 `Token` 对象，并加入 `tokens` 列表。

### 24.2 代码

```java
private void addToken(String word, TokenCategory category, String value) {
    tokens.add(new Token(word, category, value));
}
```

### 24.3 条件卡片

没有条件判断。所有 Token 最终的入口都经过这里。

这样做的好处是：

```text
以后如果要在每个 Token 上增加行号或列号，
只需要修改 Token 类和这个函数，
不需要修改十几个识别函数。
```

---

## 25. `symbolText(Symbol symbol)`：枚举名转回源程序文字

### 25.1 作用

把程序内部的符号枚举转换成源程序中实际出现的文字。

### 25.2 代码

```java
private String symbolText(Symbol symbol) {
    switch (symbol) {
        case PLUS: return "+";
        case MINUS: return "-";
        case TIMES: return "*";
        case SLASH: return "/";
        case EQL: return "=";
        case NEQ: return "#";
        case LSS: return "<";
        case LEQ: return "<=";
        case GTR: return ">";
        case GEQ: return ">=";
        case LPAREN: return "(";
        case RPAREN: return ")";
        case COMMA: return ",";
        case SEMICOLON: return ";";
        case PERIOD: return ".";
        case BECOMES: return ":=";
        default: return "";
    }
}
```

### 25.3 `switch` 条件卡片

`switch (symbol)` 不是 `if`，但作用同样是分支：

```text
symbol 是 PLUS       -> 返回 "+"
symbol 是 BECOMES    -> 返回 ":="
symbol 是其他值      -> 返回空字符串
```

为什么需要 `default`？

因为 `Symbol` 中还有很多值，例如 `IDENT`、`NUMBER`、`CONSTSYM`。它们不会调用这个函数，但 Java 仍然要求 `switch` 有完整出口。`default: return ""` 可以避免缺少返回值导致编译错误。

---

## 26. `isLowercaseLetter(char value)`

### 26.1 作用

判断一个字符是不是小写英文字母。

### 26.2 代码

```java
private boolean isLowercaseLetter(char value) {
    return value >= 'a' && value <= 'z';
}
```

### 26.3 条件卡片

条件是：

```java
value >= 'a' && value <= 'z'
```

含义是“字符不小于 `a`，同时不大于 `z`”。两个条件必须同时成立。

为什么不用 `Character.isLetter()`？

因为 PL/0 这个实现只接受小写英文字母作为标识符首字符。`Character.isLetter()` 还会接受中文、希腊字母等其他字符，范围比本实验要求更宽。

---

## 27. `isDigit(char value)`

### 27.1 作用

判断字符是不是十进制数字 `0` 到 `9`。

### 27.2 代码

```java
private boolean isDigit(char value) {
    return value >= '0' && value <= '9';
}
```

### 27.3 条件卡片

`value >= '0' && value <= '9'` 表示字符必须位于数字字符的连续范围内。

`+1`、`-1` 不在这里判断，因为它们属于特殊符号，不属于整数的一部分。

---

## 28. `printTokens()`：打印最终 Token 表

### 28.1 作用

所有 Token 分析完成后，统一打印表头和每一行。

### 28.2 代码

```java
private void printTokens() {
    System.out.print("\nWord                Category            Value\n");
    for (Token token : tokens) {
        System.out.printf("%-20s%-20s%-20s\n",
                token.word, token.category, token.value);
    }
}
```

### 28.3 逐句解释

```java
System.out.print("\nWord ...\n");
```

先打印一个换行，再打印表头。

```java
for (Token token : tokens)
```

这是增强 `for` 循环，表示“依次取出列表中的每个 `Token`”，每一轮把当前对象命名为 `token`。

```java
System.out.printf("%-20s%-20s%-20s\n", ...)
```

`%-20s` 的含义是：

```text
%-  左对齐
20  最小宽度为 20
s   按字符串格式输出
```

因此下面的输出会对齐：

```text
const               KEYWORD             CONSTSYM
m                   IDENTIFIER          m
```

### 28.4 条件卡片

`for` 循环的条件是：

```text
列表中还有下一个 Token
```

当所有 Token 都打印完后，循环自动结束。这个函数没有额外的 `if`。

---

## 29. 一个完整的识别例子

以这一行为例：

```pl0
const m = 7;
```

### 第一步：读取字符

`readNextLine()` 读入整行，`getch()` 每次返回一个字符。

```text
c -> o -> n -> s -> t -> 空格 -> m -> 空格 -> = -> 空格 -> 7 -> ; -> 行末空格
```

### 第二步：识别 `const`

`getsym()` 看到 `c`，调用 `readIdentifier()`：

```text
读取 c、o、n、s、t
得到字符串 const
查关键字表，得到 CONSTSYM
```

记录为：

```text
const / KEYWORD / CONSTSYM
```

### 第三步：跳过空格

`skipWhitespace()` 把 `const` 后面的空格跳过。

### 第四步：识别 `m`

`m` 满足小写字母条件，但不在关键字表，于是得到：

```text
m / IDENTIFIER / m
```

### 第五步：识别 `=`

`getsym()` 看到 `=`，在 `specialCharacters` 表中查到 `EQL`：

```text
= / SPECIAL / EQL
```

### 第六步：识别 `7`

`getsym()` 看到数字，调用 `readNumber()`：

```text
7 / INTEGER / 7
```

### 第七步：识别分号

`;` 在特殊字符表中查到 `SEMICOLON`：

```text
; / SPECIAL / SEMICOLON
```

### 第八步：重复下一行

当前行读完后，`getch()` 自动调用 `readNextLine()`，处理下一行，直到识别到 `.` 或文件结束。

---

## 30. 错误码速查

| 错误码 | 触发位置 | 条件 | 含义 |
|---:|---|---|---|
| 19 | `readBecomes()` | `:` 后面不是 `=` | 单独冒号，缺少等号 |
| 30 | `readNumber()` | 数字超过 14 位 | 整数过长 |
| 36 | `readIdentifier()` | 标识符超过 10 个字符 | 标识符过长 |
| 37 | `readSingleSpecialCharacter()` | 特殊字符表查不到 | 非法字符 |
| 38 | `skipComment()` | 读到 EOF 前没有找到 `*/` | 注释未闭合 |
| 39 | `readNumber()` | 数字后面紧跟小写字母 | 数字和标识符没有分开 |

---

## 31. 全部条件速查表

### 入口和文件

| 位置 | 条件 | 为什么 |
|---|---|---|
| `main` | `args.length > 1` | 参数过多 |
| `main` | `args.length == 1` | 命令行提供了文件名 |
| `main` | `fileName == null` | 没有有效文件名 |
| `main` | `exitCode != 0` | 需要设置错误退出码 |
| `readFileNameFromKeyboard` | `fileName == null` | 键盘输入已经结束 |
| `readFileNameFromKeyboard` | `fileName.isEmpty()` | 输入内容为空 |
| `run` | `currentSymbol != PERIOD` | 还没遇到程序结束句号 |
| `run` | `getsym()` 返回 `true` | 成功取得 Token |

### 读取字符

| 位置 | 条件 | 为什么 |
|---|---|---|
| `getch` | `currentColumn >= currentLine.length()` | 当前行读完了 |
| `getch` | `!readNextLine()` | 下一行无法读取 |
| `readNextLine` | `inputFinished` | 文件已经结束 |
| `readNextLine` | `line == null` | `readLine()` 返回 EOF |
| `readNextLine` | `catch IOException` | 读取过程中发生异常 |

### 词法调度

| 位置 | 条件 | 为什么 |
|---|---|---|
| `getsym` | `inputFinished` | 空白后仍没有字符 |
| `getsym` | `currentChar != '/'` | 不需要处理注释或除号 |
| `getsym` | `getch() == -1` | 斜杠后没有字符 |
| `getsym` | `currentChar != '*'` | 是除号而不是注释 |
| `getsym` | `!skipComment()` | 注释没有正常结束 |
| `getsym` | `tokenReady` | 确实产生了可记录的 Token |

### 各 Token 规则

| 位置 | 条件 | 为什么 |
|---|---|---|
| `skipWhitespace` | 当前字符是空格、换行、回车或制表符 | 这些字符不形成 Token |
| `skipComment` | `currentChar == '*'` | 检查是否出现结束符开头 |
| `skipComment` | `currentChar == '/'` | 与星号组成 `*/` |
| `readIdentifier` | 下一个字符是字母或数字 | 继续组成标识符 |
| `readIdentifier` | 长度超过 10 | 标识符过长 |
| `readNumber` | 下一个字符是数字 | 继续组成整数 |
| `readNumber` | 长度超过 14 | 整数过长 |
| `readNumber` | 后面是小写字母 | 数字后紧接字母 |
| `readBecomes` | 冒号后不是 `=` | 单独冒号非法 |
| `readLessOperator` | 后面是 `=` | 识别 `<=` |
| `readGreaterOperator` | 后面是 `=` | 识别 `>=` |
| `readSingleSpecialCharacter` | 查表结果为 `null` | 非法字符 |
| `readSingleSpecialCharacter` | 当前符号不是句号 | 普通特殊符号后面继续读取 |

---

## 32. 建议的阅读顺序

第一次阅读不要从字段声明一路硬读到文件末尾。按照下面顺序更容易建立整体印象：

1. 阅读 `main()`，知道文件名从哪里来。
2. 阅读 `run()`，知道主循环在哪里。
3. 阅读 `getsym()`，理解它如何选择识别函数。
4. 阅读 `getch()` 和 `readNextLine()`，理解字符从哪里来。
5. 阅读 `readIdentifier()`、`readNumber()`，理解普通 Token 如何组成。
6. 阅读 `readBecomes()`、`readLessOperator()`、`readGreaterOperator()`，理解双字符符号。
7. 阅读 `recordCurrentToken()` 和 `printTokens()`，理解输出表。
8. 最后阅读 `skipComment()` 和 `error()`，理解错误处理。

---

## 33. 最后用一句话总结每个函数

| 函数 | 一句话作用 |
|---|---|
| `main` | 取得文件名并启动分析 |
| `readFileNameFromKeyboard` | 从键盘读取文件名 |
| `run` | 打开文件、循环分析、打印结果 |
| `init` | 建立查找表并重置状态 |
| `getch` | 取得下一个字符 |
| `readNextLine` | 读取、回显下一行并处理 EOF |
| `getsym` | 调度整个 Token 识别过程 |
| `skipWhitespace` | 跳过空白字符 |
| `skipComment` | 跳过 `/* ... */` 注释 |
| `readIdentifier` | 识别标识符或关键字 |
| `readNumber` | 识别整数并检查长度 |
| `readBecomes` | 识别 `:=` |
| `readLessOperator` | 识别 `<` 或 `<=` |
| `readGreaterOperator` | 识别 `>` 或 `>=` |
| `readSingleSpecialCharacter` | 识别单字符特殊符号 |
| `error` | 输出教材式错误位置 |
| `recordCurrentToken` | 将当前符号加入 Token 表 |
| `recordErrorToken` | 加入一条 ERROR Token |
| `addToken` | 创建 Token 对象并加入列表 |
| `symbolText` | 把符号枚举转回源代码文字 |
| `isLowercaseLetter` | 判断小写字母 |
| `isDigit` | 判断数字 |
| `printTokens` | 打印最终 Token 表 |

只要能把“读取字符”和“识别 Token”分成两条线来理解，这个程序就不难读：

```text
getch / readNextLine：负责字符流
getsym 和多个 readXxx：负责 Token 规则
Token / recordXxx / printTokens：负责保存和输出结果
```
