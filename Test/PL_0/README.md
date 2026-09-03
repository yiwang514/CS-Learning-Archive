# PL/0 编译系统实验环境

本目录已经配置好一个可直接编译运行的 PL/0 教学编译系统。编译系统采用 C 语言实现，能把 PL/0 源程序编译成类 P-code，再由自带解释器执行。环境适用于 Windows + MinGW GCC，并配有 VS Code 一键构建任务。

## 1. 目录结构

```text
PL_0/
├── include/             PL/0 编译器头文件
├── src/                 PL/0 编译器 C 源码
├── examples/            可直接运行的示例程序
├── test/                自己实验时放测试程序的位置
├── docs/                PL/0 项目参考资料
├── bin/                 编译生成的 pl0.exe
├── build.cmd            一键构建
├── run.cmd              一键运行
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

在 `PL_0` 文件夹下打开终端，然后执行：

```bat
build.cmd
run.cmd examples\multiply.pl0
```

`build.cmd` 会生成 `bin\pl0.exe`，`run.cmd` 会把示例源码编译、列出 P-code 并解释执行。

也可以直接手动编译和运行：

```bat
gcc -std=c11 -Wall -Wextra -Iinclude -o bin\pl0.exe src\pl0.c
bin\pl0.exe examples\tests.pl0
```

使用 Make：

```bat
mingw32-make -f Makefile
mingw32-make -f Makefile run
```

## 4. 在 VS Code 中使用

1. 用 VS Code 打开 `PL_0` 文件夹。
2. 按 `Ctrl+Shift+B` 执行“PL/0: build compiler”，完成编译。
3. 在“终端 -> 运行任务”中选择：
   - `PL/0: run multiply example`
   - `PL/0: run current file`

如果以后改动的是 `src/pl0.c` 或 `include/pl0.h`，运行任务会自动先重新编译。

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

以 `test\example.pl0` 为例：

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

## 6. 自己编写和运行 PL/0 程序

1. 在 `test` 目录新建 `my.pl0`。
2. 执行 `build.cmd`。
3. 执行 `run.cmd test\my.pl0`。

编译过程中若出现语法错误，程序会打印错误位置编号，并且不再进入解释执行。`test\undeclared.pl0` 是一个故意写错、用于观察错误处理的示例：

```bat
bin\pl0.exe test\undeclared.pl0
```

## 7. 源码说明

编译系统的源码结构分为：

- 词法分析：`getsym()`
- 递归下降语法分析：`block()`、`statement()`、`condition()`、`expression()`、`term()`、`factor()`
- 符号表：`enter()`、`position()`
- 代码生成与列表：`gen()`、`listcode()`
- P-code 解释执行：`interpret()`

详细的中文参考资料放在 `docs\pl0.pdf`。源码整理自中国科学技术大学编译原理课程的 PL/0 Project Source Tree（Yu Zhang），在本项目中做了 Windows/MinGW 适配、去除非必要输出并补充中文使用说明，仅供课程学习使用。

