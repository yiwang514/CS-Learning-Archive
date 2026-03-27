# SJF 代码讲解笔记

## 1. 这份程序是做什么的

这段 `SJF.java` 实现的是 **SJF（Shortest Job First，短作业优先）调度算法**。

它的作用是：

- 读入若干个进程的信息；
- 按照 **已经到达内存的进程里，执行时间最短者优先运行** 的规则进行调度；
- 计算每个进程的开始时间、完成时间、周转时间、带权周转系数；
- 最后输出每个进程的调度结果，以及系统平均周转时间和平均带权周转系数。

注意：这份代码实现的是 **非抢占式 SJF**。

也就是说：

- 一个进程一旦开始执行，
- 就会一直运行到结束，
- 中途不会因为来了更短的进程而被打断。

---

## 2. SJF 的核心思想

SJF 的调度原则可以概括成一句话：

> 在当前已经到达的所有进程中，选择执行时间最短的进程先运行。

例如：

- 当前时刻已经到达了 A、B、C 三个进程；
- 它们执行时间分别是 5、2、7；
- 那么先运行 B，因为它最短。

### SJF 的优点

- 平均周转时间通常较小；
- 对短作业比较友好；
- 调度结果比较直观。

### SJF 的缺点

- 长作业可能长期等待；
- 必须事先知道或估计进程执行时间；
- 如果新短作业不断到来，长作业容易“饥饿”。

---

## 3. 程序整体结构

本程序一共定义了 3 个类：

1. `Time`：表示时间；
2. `Process`：表示进程；
3. `SJF`：主类，完成输入、调度、输出。

可以这样理解：

- `Time` 负责“时间格式转换”；
- `Process` 负责“保存一个进程的全部信息”；
- `SJF` 负责“真正执行短作业优先调度”。

---

## 4. `Time` 类详解

代码位置：`work3/SJF.java:3`

```java
class Time{
    int hour;
    int min;
```

### 成员变量

- `hour`：小时；
- `min`：分钟。

### 构造方法

```java
public Time(int hour, int min){
    this.hour = hour;
    this.min = min;
}
```

作用：创建一个时间对象，例如 `new Time(8, 30)` 表示 `08:30`。

### `toMinutes()`

```java
public int toMinutes(){
    return hour * 60 + min;
}
```

作用：把“几点几分”转换成“总分钟数”。

例如：

- `08:30 -> 8 * 60 + 30 = 510`

这样做的好处是：

- 比较时间大小更方便；
- 做加减运算更简单；
- 调度时可以直接用整数表示当前时刻。

### `fromMinutes()`

```java
public static Time fromMinutes(int totalMinutes){
    int hour = totalMinutes / 60;
    int min = totalMinutes % 60;
    return new Time(hour, min);
}
```

作用：把总分钟数再转回时:分格式。

例如：

- `550 -> 09:10`

这个方法主要用于：

- 计算出开始时间、完成时间后，
- 再转成正常时间格式输出给用户看。

### `toString()`

```java
public String toString(){
    return String.format("%02d:%02d", hour, min);
}
```

作用：把时间格式化成两位数显示。

例如：

- `8:5` 会显示为 `08:05`

这样输出更规范。

---

## 5. `Process` 类详解

代码位置：`work3/SJF.java:27`

```java
class Process {
    int id;
    String name;
    Time arrivalTime;
    int zx;
    Time startTime;
    Time finishTime;
    int zz;
    double zzxs;
    boolean isCompleted;
}
```

这个类用于保存每个进程从输入到调度结束的所有信息。

### 字段含义

- `id`：进程编号；
- `name`：进程名；
- `arrivalTime`：到达时间；
- `zx`：执行时间；
- `startTime`：开始执行时间；
- `finishTime`：完成时间；
- `zz`：周转时间；
- `zzxs`：带权周转系数；
- `isCompleted`：该进程是否已经执行完成。

### 构造方法

```java
public Process(int id, String name, Time arrivalTime, int zx){
    this.id = id;
    this.name = name;
    this.arrivalTime = arrivalTime;
    this.zx = zx;
    this.isCompleted = false;
}
```

这里初始化了进程的基础信息，并把 `isCompleted` 设为 `false`，表示刚输入时还没有执行。

---

## 6. 主程序 `SJF` 的执行流程

代码位置：`work3/SJF.java:51`

主程序可以分成 5 步：

1. 读入操作命令；
2. 输入所有进程；
3. 找出最早到达时间作为初始当前时间；
4. 循环选择“已到达且执行时间最短”的进程；
5. 输出全部结果和平均值。

---

## 7. 输入部分讲解

### 7.1 外层循环

```java
while(true){
    System.out.print("请输入操作（1：开始进程调度；-1：结束进程） ");
    int op = sc.nextInt();
    if (op == -1) break;
    if (op == 1) {
```

这里表示：

- 输入 `1`：开始一次调度；
- 输入 `-1`：退出程序。

### 7.2 输入进程数量

```java
System.out.print("请输入进程数： ");
int n = sc.nextInt();
```

`n` 表示本次要调度的进程个数。

### 7.3 创建两个表

```java
List<Process> processes = new ArrayList<>();
List<Process> finishedProcesses = new ArrayList<>();
```

- `processes`：保存所有进程；
- `finishedProcesses`：按实际调度顺序保存已经完成的进程，便于最后输出。

### 7.4 输入每个进程的信息

```java
for(int i = 0;i < n; i++){
    int id = sc.nextInt();
    String name = sc.next();
    String timeString = sc.next();
    String[] timeParts = timeString.split(":");
    Time arrivalTime = new Time(Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]));
    int zx = sc.nextInt();
    processes.add(new Process(id, name, arrivalTime, zx));
}
```

每个进程输入四项内容：

- 编号 `id`
- 名字 `name`
- 到达时间 `arrivalTime`
- 执行时间 `zx`

比如输入：

```text
1 P1 08:00 4
```

表示：

- 进程编号 1
- 进程名 P1
- 08:00 到达
- 执行 4 分钟

其中时间先按字符串读入，再用 `split(":")` 切开成小时和分钟。

---

## 8. 初始化当前时间

```java
int currentTime = Integer.MAX_VALUE;
for(Process p : processes){
    if(p.arrivalTime.toMinutes() < currentTime){
        currentTime = p.arrivalTime.toMinutes();
    }
}
```

`currentTime` 表示 CPU 当前时刻。

这里先把它设成很大值，再遍历所有进程，找到 **最早到达时间** 作为系统开始调度的时间。

为什么这样做？

因为在最早进程到来之前，CPU 不可能执行任何进程。

---

## 9. SJF 调度核心循环

代码片段：`work3/SJF.java:80`

```java
while(completedProcesses < n){
```

意思是：只要还有进程没有完成，就继续调度。

### 9.1 选出下一个要执行的进程

```java
Process nextProcess = null;
int minZx = Integer.MAX_VALUE;
for(Process p : processes){
    if(!p.isCompleted && p.arrivalTime.toMinutes() <= currentTime && p.zx < minZx){
        minZx = p.zx;
        nextProcess = p;
    }
}
```

这一段是全程序最核心的地方。

它筛选条件有 3 个：

1. `!p.isCompleted`：该进程还没完成；
2. `p.arrivalTime.toMinutes() <= currentTime`：该进程已经到达；
3. `p.zx < minZx`：在已到达进程中执行时间最短。

满足这 3 个条件的进程会被选为 `nextProcess`。

这正是 SJF 的实现。

### 9.2 如果找到了可执行进程

```java
if(nextProcess != null){
    nextProcess.startTime = Time.fromMinutes(currentTime);
    currentTime += nextProcess.zx;
    nextProcess.finishTime = Time.fromMinutes(currentTime);
    nextProcess.zz = nextProcess.finishTime.toMinutes() - nextProcess.arrivalTime.toMinutes();
    nextProcess.zzxs = (double)nextProcess.zz / nextProcess.zx;
    nextProcess.isCompleted = true;
    finishedProcesses.add(nextProcess);
    completedProcesses++;
}
```

这段代码完成了一个进程从“开始执行”到“执行结束”的全部处理。

#### `startTime`

```java
nextProcess.startTime = Time.fromMinutes(currentTime);
```

开始时间就是当前 CPU 时间。

#### 更新 `currentTime`

```java
currentTime += nextProcess.zx;
```

当前进程执行了多少分钟，系统时间就往后推进多少分钟。

#### `finishTime`

```java
nextProcess.finishTime = Time.fromMinutes(currentTime);
```

执行完后，新的 `currentTime` 就是它的完成时间。

#### 周转时间 `zz`

```java
nextProcess.zz = nextProcess.finishTime.toMinutes() - nextProcess.arrivalTime.toMinutes();
```

公式：

> 周转时间 = 完成时间 - 到达时间

它表示一个进程从提交到执行结束，总共在系统中停留了多久。

#### 带权周转系数 `zzxs`

```java
nextProcess.zzxs = (double)nextProcess.zz / nextProcess.zx;
```

公式：

> 带权周转系数 = 周转时间 / 执行时间

它反映：

- 进程实际经历的总时间，
- 是其真正运行时间的多少倍。

数值越接近 1，说明等待越少。

#### 标记完成

```java
nextProcess.isCompleted = true;
finishedProcesses.add(nextProcess);
completedProcesses++;
```

表示：

- 该进程以后不再参与调度；
- 把它加入完成队列；
- 完成数量加 1。

### 9.3 如果当前没有任何进程可以执行

```java
else{
    int nextArrivalTime = Integer.MAX_VALUE;
    for(Process p : processes){
        if(!p.isCompleted && p.arrivalTime.toMinutes() < nextArrivalTime){
            nextArrivalTime = p.arrivalTime.toMinutes();
        }
    }
    currentTime = nextArrivalTime;
}
```

这部分非常重要，老师很可能会问。

它处理的是：

- 当前时刻没有任何已到达且未完成的进程；
- 说明 CPU 需要空闲等待。

程序的处理办法是：

- 找到“剩余进程中最近的到达时间”；
- 直接把 `currentTime` 跳到那个时刻。

这相当于模拟：

> CPU 空转，直到下一个进程到来。

---

## 10. 输出部分讲解

### 10.1 输出表头

```java
System.out.println("模拟短进程优先调度过程输出结果");
System.out.println("id号\t名字\t到达时间\t执行时间（分钟）\t开始时间\t完成时间\t周转时间（分钟）\t带权周转系数");
```

这里会输出每个进程的详细调度结果。

### 10.2 统计平均值

```java
float sumZz = 0;
float sumZzxs = 0;
for (Process p : finishedProcesses) {
    System.out.printf("%d\t%s\t%s\t%d\t%s\t%s\t%d\t%.2f\n",
            p.id, p.name, p.arrivalTime.toString(), p.zx, p.startTime.toString(), 
            p.finishTime.toString(), p.zz, p.zzxs);
    sumZz += p.zz;
    sumZzxs += p.zzxs;
}
```

这里一边输出每个进程的信息，一边累加：

- 总周转时间 `sumZz`
- 总带权周转系数 `sumZzxs`

### 10.3 计算系统平均指标

```java
System.out.printf("\n系统平均周转时间为：\t\t\t\t\t\t%.2f\n", sumZz / n);
System.out.printf("系统平均带权周转系数为：\t\t\t\t\t\t%.2f\n", sumZzxs / n);
```

公式分别是：

- 平均周转时间 = 所有进程周转时间之和 / 进程数
- 平均带权周转系数 = 所有进程带权周转系数之和 / 进程数

---

## 11. 用一个例子走完整个过程

假设输入：

```text
1
4
1 P1 08:00 4
2 P2 08:01 3
3 P3 08:02 1
4 P4 08:03 2
-1
```

### 第一步：确定初始时间

最早到达的是 `P1`，时间是 `08:00`，所以：

- `currentTime = 08:00`

### 第二步：在已到达进程中选最短作业

#### 时刻 `08:00`

已到达的只有：

- `P1(4)`

所以先执行 `P1`。

- 开始：`08:00`
- 完成：`08:04`

#### 时刻 `08:04`

此时已到达：

- `P2(3)`
- `P3(1)`
- `P4(2)`

最短的是 `P3(1)`，所以执行 `P3`。

- 开始：`08:04`
- 完成：`08:05`

#### 时刻 `08:05`

剩余已到达：

- `P2(3)`
- `P4(2)`

最短的是 `P4(2)`。

- 开始：`08:05`
- 完成：`08:07`

#### 时刻 `08:07`

剩余：

- `P2(3)`

执行 `P2`。

- 开始：`08:07`
- 完成：`08:10`

### 最终调度顺序

```text
P1 -> P3 -> P4 -> P2
```

### 各项指标计算

#### `P1`

- 到达：`08:00`
- 完成：`08:04`
- 周转时间：`4`
- 带权周转系数：`4 / 4 = 1.00`

#### `P3`

- 到达：`08:02`
- 完成：`08:05`
- 周转时间：`3`
- 带权周转系数：`3 / 1 = 3.00`

#### `P4`

- 到达：`08:03`
- 完成：`08:07`
- 周转时间：`4`
- 带权周转系数：`4 / 2 = 2.00`

#### `P2`

- 到达：`08:01`
- 完成：`08:10`
- 周转时间：`9`
- 带权周转系数：`9 / 3 = 3.00`

---

## 12. 这份代码体现了哪些操作系统知识点

你讲的时候可以从下面几个点展开：

### 12.1 进程调度

这段代码模拟的是操作系统中的 **处理机调度**，也就是决定 CPU 下一步给哪个进程使用。

### 12.2 非抢占式调度

程序中一旦选中了 `nextProcess`，就直接：

```java
currentTime += nextProcess.zx;
```

说明它会一直执行到结束，不会中途切换，所以这是 **非抢占式**。

### 12.3 周转时间

周转时间衡量的是进程从提交到结束的总耗时，是评价调度算法的重要指标。

### 12.4 带权周转系数

这个指标更公平，因为它考虑了进程本身执行时间的长短。

短进程等待很久时，这个系数会明显增大。

---

## 13. 老师可能会问的问题

### 1）为什么说它是非抢占式 SJF？

答：因为代码一旦选择了某个进程执行，就直接把 `currentTime` 加上该进程的全部执行时间，说明它会运行到结束，中间不会被打断。

### 2）`currentTime` 的作用是什么？

答：它表示当前 CPU 的时间推进位置。调度时要根据它判断哪些进程已经到达，以及下一个进程从什么时候开始执行。

### 3）为什么要把时间转换成分钟？

答：因为比较大小和加减运算更方便。如果直接用“小时:分钟”处理，逻辑会更复杂。

### 4）如果当前没有进程到达怎么办？

答：程序会找到下一个最近到达的进程，把 `currentTime` 直接跳到那个到达时刻，表示 CPU 空闲等待。

### 5）周转时间和带权周转系数怎么计算？

答：

- 周转时间 = 完成时间 - 到达时间
- 带权周转系数 = 周转时间 / 执行时间

### 6）这段代码有没有处理“执行时间相同”的情况？

答：有一定处理，但不是专门设计的。因为代码只在 `p.zx < minZx` 时更新，所以若执行时间相同，会保留先遍历到的那个进程，相当于按输入顺序优先。

### 7）这段代码是不是最短剩余时间优先（SRTF）？

答：不是。SRTF 是抢占式算法，运行过程中如果来了更短的作业会抢占当前进程；而本代码不会抢占，所以只是普通的非抢占式 SJF。

---

## 14. 这份代码的优点

- 结构清晰，分成时间类、进程类、主调度类；
- 时间转换单独封装，便于理解；
- 核心算法逻辑完整，能正确模拟 SJF；
- 能输出完整的调度结果和统计指标；
- 考虑了 CPU 空闲时跳到下一个到达时间的情况。

---

## 15. 这份代码还能改进的地方

如果老师问“还能怎么优化”，你可以这样回答：

### 15.1 类名和变量名可以更规范

例如：

- `zx` 可以改成 `serviceTime` 或 `burstTime`
- `zz` 可以改成 `turnaroundTime`
- `zzxs` 可以改成 `weightedTurnaroundTime`

这样可读性更高。

### 15.2 可以增加输入合法性判断

比如：

- 时间格式输错；
- 执行时间输入负数；
- 进程数小于等于 0。

当前代码默认用户输入一定正确。

### 15.3 可以支持更多调度算法

可以在此基础上继续扩展：

- FCFS 先来先服务；
- RR 时间片轮转；
- HRRN 高响应比优先；
- 抢占式 SJF。

### 15.4 可以按优先队列优化查找

当前每次都要遍历所有进程找最短作业，数据量大时效率一般。可以考虑使用优先队列保存已到达进程。

---

## 16. 上台讲解时可以直接照着说的话

下面这段可以作为你的讲解稿：

> 这段程序实现的是短作业优先 SJF 调度算法。核心思想是在当前已经到达的进程中，选执行时间最短的先运行。程序首先定义了 `Time` 类来完成时间和分钟数之间的转换，定义了 `Process` 类来保存进程的编号、名字、到达时间、执行时间、开始时间、完成时间、周转时间和带权周转系数。然后在主类 `SJF` 中，先输入所有进程信息，再把当前时间初始化为最早到达的时间。之后通过循环不断从“已到达且未完成”的进程中选出执行时间最短的一个，计算它的开始时间、完成时间、周转时间和带权周转系数，并标记为完成。如果当前时刻没有进程可执行，就把系统时间跳到下一个进程到达的时刻。最后程序输出每个进程的调度结果，以及系统平均周转时间和平均带权周转系数。由于进程一旦开始执行就会运行到结束，因此这是一个非抢占式的 SJF 算法实现。

---

## 17. 你最后可以重点强调的 3 句话

如果时间紧，你就抓住这 3 句：

1. 这段代码实现的是 **非抢占式 SJF 短作业优先调度算法**。
2. 核心判断条件是：**在当前已到达且未完成的进程中，选择执行时间最短的进程运行**。
3. 程序最终计算了 **开始时间、完成时间、周转时间、带权周转系数，以及系统平均值**。

---

## 18. 一句话总结

这份 `SJF.java` 本质上就是：**用分钟数模拟 CPU 时间推进，再按照短作业优先规则依次执行进程，并统计每个进程及整个系统的调度性能指标。**
