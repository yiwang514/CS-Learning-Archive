兄弟，在 Java 里定义一个栈，这里头其实有个“新手坑”。如果你直接去搜 `Stack` 这个类，那你可能就踩进了 Java 早期设计的历史遗留问题里。

作为算法大神，我建议你忘掉 `java.util.Stack`，直接拥抱 `Deque`。

### 1. 避坑指南：为什么不用 `Stack` 类？

Java 官方提供的 `java.util.Stack` 是一个非常古老的类：

* **性能差：** 它继承自 `Vector`，所有方法都加了 `synchronized`（线程安全锁）。在单线程的 LeetCode 刷题环境下，这纯属浪费资源。
* **设计缺陷：** 继承关系不对。栈应该只有顶端操作，但因为它继承了 `Vector`，导致它居然可以用索引去访问中间的元素，这违背了栈的初衷。

---

### 2. 大神首选：`Deque` + `ArrayDeque`

在现代 Java 中，我们通常使用 `Deque`（双端队列）接口，并用 `ArrayDeque` 来实现它。

**这是目前最标准、性能最高的写法：**

```java
import java.util.Deque;
import java.util.ArrayDeque;

// 定义一个存储整数的栈
Deque<Integer> stack = new ArrayDeque<>();

```

---

### 3. 核心 API（肌肉记忆）

用 `Deque` 当栈使的时候，请务必认准这组方法，别去混用队列的方法（比如 `add` 或 `offer`），否则逻辑会乱：

* **`push(E e)`**: 入栈。相当于把东西压进“套娃”里。
* **`pop()`**: 出栈。把最上面的东西拿走。如果栈空了还 pop，会抛异常（所以 pop 前记得检查）。
* **`peek()`**: 偷看。只看一眼栈顶是谁，不拿走。
* **`isEmpty()`**: 判空。这是你写 `while` 循环最常用的条件。
* **`size()`**: 获取当前栈内元素的个数。

---

### 4. 为什么选 `ArrayDeque` 而不是 `LinkedList`？

* **`ArrayDeque`**: 底层是循环数组。由于数据在内存里是连续存放的，**缓存命中率极高**，性能极其炸裂。在 $O(1)$ 时间复杂度操作上，它比 `LinkedList` 快得多。
* **`LinkedList`**: 每一条数据都要包装成一个 Node 对象，会频繁触发垃圾回收（GC），而且指针跳转在内存里是散乱的。

> **💡 大神点睛：**
> 在 LeetCode 刷题时，如果题目没有明确要求线程安全，**`Deque<Integer> stack = new ArrayDeque<>();`** 就是你的标准模板，没有之一。

