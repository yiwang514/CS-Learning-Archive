# 实验：NFA 到 DFA 的转换

> 目录已整理：文中的课程根目录指 `Compiler-Principles`。完整操作入口以本实验 README 为准；源码与测试规则未改变。

核心源文件是 [src/NfaToDfa.java](../src/NfaToDfa.java)，与原来的 `Pl0Lexer.java` 相互独立。
提交这个实验的源代码时，只需要 `NfaToDfa.java`；其中包含 `Nfa`、`Dfa` 两个内部数据类。
`classes` 中的 `.class` 是自动编译产物，不是额外需要编写的源文件。

## 1. 我们讨论的设计与代码的对应关系

| 讨论中的内容 | 实际代码 |
|---|---|
| 状态总数、初态、终态、字符集合 | `Nfa` 中的 `stateCount`、`startState`、`acceptingStates`、`alphabet` |
| 普通边单独保存 | `Nfa.transitions`：`List<Map<Character, Set<Integer>>>` |
| ε 边单独保存 | `Nfa.epsilonTransitions`：`List<Set<Integer>>` |
| 保存一条边 | `Nfa.addEdge` |
| 栈迭代求闭包 | `epsilonClosure` |
| 经过一条指定字符边 | `move` |
| DFA 编号到 NFA 子集 | `Dfa.states`，下标就是 DFA 编号 |
| DFA 转换表 | `Dfa.transitions`：`List<Map<Character, Integer>>` |
| 新状态查重 | `buildDfa` 内的 `stateIds` |
| 尚待处理的状态队列 | `buildDfa` 内的 `pendingStates` |
| 统一登记新状态 | `addDfaState` |
| 判断子集是否包含 NFA 终态 | `containsAcceptingState` |
| 打印状态集合和转换表 | `printDfa` |

`addDfaState` 把我们讨论的“创建新状态时要做的几件事”集中在一起：分配编号、复制子集、创建空转换表、登记查重表、加入队列、判断终态。D0 也经过同一方法，避免忘记检查初态是否为终态。

保存的子集使用 `Collections.unmodifiableSet(new TreeSet<>(subset))`，先复制再设为只读。排序只是为了显示稳定；集合判重不依赖排列顺序。

## 2. 编译与运行

要求已安装 JDK，且 `java`、`javac` 可以在终端执行。脚本默认按 UTF-8 编译和读取输入。

在 `Compiler-Principles` 项目根目录的 PowerShell 中执行：

```powershell
.\nfa-to-dfa\java\build.cmd
.\nfa-to-dfa\java\run.cmd nfa-to-dfa\java\tests\epsilon_chain.txt
```

`run.cmd` 每次会先编译，避免修改源码后仍运行旧版本。
文件路径相对于你执行命令时所在的目录，而不是相对于脚本目录。带空格的路径请加英文双引号。

如果已进入 `nfa-to-dfa/java` 目录：

```powershell
.\run.cmd tests\epsilon_chain.txt
```

脚本接收文件路径只是提供方便，底层仍通过标准输入重定向把文本交给 Java，不改变老师要求的 stdin 格式。

### 手动粘贴标准输入

在项目根目录运行：

```powershell
.\nfa-to-dfa\java\run.cmd
```

程序没有文件名提示，会等待输入。粘贴以下完整内容，最后一行之后按回车：

```text
3 0 1 2
a
2
0 1 $
1 2 a
```

读够声明的两条边就立即输出，不需要再输入待识别字符串，也不需要手动发送 EOF。
注意：按声明的边数读取，不再检查后续多余行，以免交互输入一直等待 EOF。边数不能为负，提前 EOF 会报告缺少哪一行。

### 不用脚本的标准命令

在项目根目录先编译：

```powershell
javac -encoding UTF-8 -d nfa-to-dfa\java\classes nfa-to-dfa\java\src\NfaToDfa.java
```

在 CMD 中可以直接重定向：

```bat
java -cp nfa-to-dfa\java\classes NfaToDfa < nfa-to-dfa\java\tests\epsilon_chain.txt
```

PowerShell 不直接使用这种 `<` 语法，请调用上面的 `.cmd` 脚本。

## 3. 输入格式

```text
第一行：状态总数 初态ID 终态个数 终态ID列表
第二行：普通终结符，空格分隔，不包含 $
第三行：边数
后续每行：起点 终点 符号
```

- 状态编号由 `0` 到 `stateCount - 1`，即使没有出现在边上也属于 NFA 状态集合。
- 终态个数可以为 0，不能为负，不能超过状态总数；重复终态编号会报错。
- 普通终结符为单个 Java `char`，例如 `a`、`b`；重复符号自动合并。
- `$` 表示 ε，仅出现在边中，不出现在第二行。
- 如果终结符集合为空，第二行仍要保留，写成空行。
- 重复边自动合并；普通边的符号必须在第二行声明。
- 不支持注释行，也不跳过格式中的空行。
- 缺行、非整数、编号越界、符号过长等输入问题会给出 `Input error:` 提示并返回退出码 1。

## 4. 三个测试案例

| 文件 | 主要验证点 | 预期结果 |
|---|---|---|
| [basic.txt](../tests/basic.txt) | 无 ε 边，同字符多个目标，陷阱状态 | 4 个 DFA 状态，终态 D3 |
| [epsilon_chain.txt](../tests/epsilon_chain.txt) | 课堂中手算的例子，初态闭包 | D0={0,1}，D1={2} 为终态，D2={} |
| [epsilon_cycle.txt](../tests/epsilon_cycle.txt) | ε 环、合并目标、不可达 NFA 状态4 | 4 个 DFA 状态，终态 D1、D3，子集中没有4 |

在项目根目录运行：

```powershell
.\nfa-to-dfa\java\run.cmd nfa-to-dfa\java\tests\basic.txt
.\nfa-to-dfa\java\run.cmd nfa-to-dfa\java\tests\epsilon_chain.txt
.\nfa-to-dfa\java\run.cmd nfa-to-dfa\java\tests\epsilon_cycle.txt
```

第二个案例的输出为：

```text
DFA state count: 3
Alphabet: [a]
Start state: D0
Accepting states: {D1}

States (* = start, + = accepting):
*D0 = {0, 1}
+D1 = {2}
D2 = {}

Transition table:
State   a
D0      D1
D1      D2
D2      D2
```

`*` 标记初态，`+` 标记终态，一个状态同时是初态和终态时显示 `*+`。输出最后还会提示空集的含义与未执行最小化。

## 5. 自动验证

```powershell
.\nfa-to-dfa\java\verify.cmd
```

它会编译主程序和独立测试程序 [NfaToDfaTest.java](../tests/NfaToDfaTest.java)，执行算法断言，再检查三个标准输入文件可以转换。

验证包括：

- ε 链、ε 环、闭包包含原集合且不修改原集合。
- move 只走一条普通边，自动合并重复目标。
- 初态闭包、终态判断、子集查重、队列发现顺序。
- 空集陷阱状态、非零 NFA 初态、初态也是终态、没有终态、空字符集合。
- 状态键只读、重复边合并、非法输入拒绝、输出表内容。
- 2 万个状态的 ε 链，确认不使用递归。
- 40 个固定随机种子的 NFA，对长度 0 到 4 的所有 a/b 字符串，独立模拟 NFA 并对照 DFA 接受结果。

字符串模拟只存在于测试代码，不是主程序额外要求输入的功能。

## 6. 建议怎样开始读源码

先看 `Nfa` 和 `Dfa` 的字段，对照本说明第一节；然后依次看：

1. `epsilonClosure`：你已经理解的“集合判重 + 栈”。
2. `move`：遍历输入集合，合并每个状态的直接目标。
3. `addDfaState`：创建一个 DFA 状态时同步更新哪些容器。
4. `buildDfa`：把前面三个方法连起来，先 move 再闭包。
5. `readNfa`：把此前讲过的输入解析整理在一起。
6. `printDfa`：把状态、标记与每个字符的目标编号输出。

其余辅助方法只负责格式转换或输入检查，不参与子集构造算法。

本实现只生成可达 DFA 状态，并在需要时保留空集陷阱状态，输出完整转换表。不做 DFA 最小化；状态数最坏仍可能达到 NFA 状态数的指数级，这是子集构造法本身的特点。
