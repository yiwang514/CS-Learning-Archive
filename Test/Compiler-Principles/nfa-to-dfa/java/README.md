# NFA 转 DFA（Java）

使用子集构造法，把带 ε 边的 NFA 转为 DFA。提交源码：[src/NfaToDfa.java](src/NfaToDfa.java)，其中包含 Nfa、Dfa 两个内部数据类。

进入本目录执行：

```powershell
.\build.cmd
.\run.cmd tests\basic.txt
.\run.cmd tests\epsilon_chain.txt
.\run.cmd tests\epsilon_cycle.txt
.\verify.cmd
```

需要 JDK，使用 UTF-8。运行脚本先编译，再把文件重定向给标准输入；不传文件名时直接读取键盘输入。
编译结果在 `classes/`，不作为源文件提交。

## 输入格式

```text
状态总数 初态ID 终态个数 终态ID列表
终结符列表（空格隔开）
边数
起点 终点 符号
...
```

状态编号为从 0 开始的连续整数；终结符必须是单个字符；`$` 只表示 ε 边，不能出现在终结符列表中。
程序按照声明的边数读入，不需要等到输入结束。

## 实现重点

- NFA 的普通边和 ε 边分开保存。
- ε-闭包使用栈迭代，通过结果集合去重，防止循环和重复入栈。
- move 只沿指定字符边前进一步；再求 ε-闭包。
- 使用集合到 DFA 编号的映射查重，队列依次处理新子集。
- 子集包含任一 NFA 终态时，该 DFA 状态就是终态，初态也检查。
- 输出状态对应子集、初态、终态和转换表；可达的空集保留为陷阱状态，使 DFA 转换完整。

[完整输入、输出与设计说明](docs/NFA转DFA使用说明.md)。
测试包含三个输入案例，以及 [NfaToDfaTest.java](tests/NfaToDfaTest.java) 的固定案例、长 ε 链与随机语言等价性检查。
