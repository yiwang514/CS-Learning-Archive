# MySQL 核心基础速查复习

> 面向已学完基础的快速巩固手册：只留要点、对照表和最小示例，遇到 ⚠️ 重点看。

---

## 一、DDL：库与表的基本操作

### 1. 数据库操作

```sql
CREATE DATABASE db1;                          -- 建库
CREATE DATABASE IF NOT EXISTS db1;            -- 建库（已存在不报错）
DROP DATABASE db1;                            -- 删库
USE db1;                                      -- 切换/进入库
SHOW DATABASES;                               -- 查看所有库
```

### 2. 表操作

```sql
CREATE TABLE student (
    id      INT PRIMARY KEY AUTO_INCREMENT,   -- 主键 + 自增
    name    VARCHAR(20) NOT NULL,             -- 非空
    gender  CHAR(1) DEFAULT '男',             -- 默认值
    score   DECIMAL(5,2),                     -- 小数
    birth   DATE                              -- 日期
);

SHOW TABLES;                                  -- 查看所有表
DESC student;                                 -- 查看表结构
ALTER TABLE student ADD age INT;              -- 加列
ALTER TABLE student MODIFY age VARCHAR(10);   -- 改列类型（MySQL）
ALTER TABLE student CHANGE age age2 INT;      -- 改列名+类型（旧名 新名 类型）
ALTER TABLE student DROP age2;                -- 删列
DROP TABLE student;                           -- 删表
```

> ⚠️ **ADD / MODIFY / CHANGE 区分**：加新列用 `ADD`；改已有列类型用 `MODIFY`；改列名必须用 `CHANGE`（新旧名都要写）。
> ⚠️ **`MODIFY` 要重写完整定义**，漏写 `NOT NULL` 会把它弄丢。

---

## 二、DML：数据的增删改

```sql
-- 增（INSERT）
INSERT INTO student (name, score) VALUES ('张三', 90.5);
INSERT INTO student VALUES (NULL, '李四', '女', 88, '2000-01-01');  -- 全列可省略列名

-- 改（UPDATE）
UPDATE student SET score = 95 WHERE id = 1;
UPDATE student SET score = score + 5;         -- 缺 WHERE 会改全表！

-- 删（DELETE）
DELETE FROM student WHERE id = 1;
```

> ⚠️ **`DELETE` vs `TRUNCATE` vs `DROP`**（面试高频）：
> - `DELETE` 删数据，**可带 WHERE**，可回滚（事务内），主键自增**不会重置**。
> - `TRUNCATE` 清空整表数据，**不可带 WHERE**，速度快，自增**会重置**。
> - `DROP` 删的是**表结构**，数据和表都没了。

---

## 三、DQL：数据查询

### 1. 基础查询

```sql
SELECT name, score FROM student;              -- 查指定列
SELECT * FROM student;                        -- 查所有列
SELECT DISTINCT gender FROM student;          -- 去重
SELECT name AS 姓名, score + 10 AS 加分后 FROM student;  -- 别名 + 运算
SELECT CONCAT(name, '-', gender) FROM student;  -- 拼接
```

> ⚠️ **`DISTINCT` 作用于其后所有列的组合**，不是只对第一列去重。
> ⚠️ **`NULL` 参与运算结果为 `NULL`**，要用 `IFNULL(score, 0)` 兜底。

### 2. 条件查询（WHERE）

```sql
SELECT * FROM student WHERE score >= 90;
SELECT * FROM student WHERE score BETWEEN 60 AND 90;      -- 闭区间
SELECT * FROM student WHERE gender IN ('男', '女');
SELECT * FROM student WHERE name LIKE '张%';               -- % 任意多字符
SELECT * FROM student WHERE name LIKE '张_';               -- _ 单个字符
SELECT * FROM student WHERE score IS NULL;                 -- 判空用 IS NULL
SELECT * FROM student WHERE score > 80 AND gender = '男';   -- AND / OR / NOT
```

> ⚠️ **判空必须用 `IS NULL` / `IS NOT NULL`**，不能用 `= NULL`（永远是假）。
> ⚠️ **`LIKE '%张%'`** 包含张、`'张%'` 开头、`'%张'` 结尾；`_` 只占一个字符。

### 3. 排序（ORDER BY）

```sql
SELECT * FROM student ORDER BY score DESC;                 -- 降序
SELECT * FROM student ORDER BY score ASC, id DESC;         -- 先按分数升序，同分再按 id 降序
```

> ⚠️ 默认 `ASC`（升序）；`ORDER BY` 永远写在 `WHERE` 之后。

### 4. 聚合函数

| 函数 | 作用 | 备注 |
|------|------|------|
| `COUNT(*)` | 统计总行数 | 不忽略 NULL |
| `COUNT(列)` | 统计该列非 NULL 行数 | **忽略 NULL** |
| `SUM(列)` | 求和 | 忽略 NULL |
| `AVG(列)` | 平均值 | 忽略 NULL |
| `MAX(列)` / `MIN(列)` | 最大 / 最小 | 忽略 NULL |

```sql
SELECT COUNT(*) AS 总人数, AVG(score) AS 平均分 FROM student;
```

> ⚠️ **`COUNT(*)` vs `COUNT(列)`（面试高频）**：`COUNT(*)` 数所有行；`COUNT(列)` 只数该列**不为 NULL** 的行。有 NULL 时两者结果不同。
> ⚠️ 聚合函数**不能写在 `WHERE` 里**，因为 `WHERE` 在分组/聚合之前执行。

### 5. 分组（GROUP BY + HAVING）

```sql
SELECT gender, AVG(score) FROM student GROUP BY gender;              -- 按性别分组求平均分
SELECT gender, COUNT(*) FROM student GROUP BY gender HAVING COUNT(*) > 3;  -- 筛选人数>3的组
```

> ⚠️ **`WHERE` vs `HAVING`（面试高频）**：
> - `WHERE` 在**分组前**过滤**行**，不能用聚合函数；
> - `HAVING` 在**分组后**过滤**组**，可以用聚合函数。
> - 能用 `WHERE` 就别用 `HAVING`（先过滤再分组效率更高）。
>
> ⚠️ **SELECT 里出现的非聚合列，必须出现在 `GROUP BY` 里**（`ONLY_FULL_GROUP_BY`），否则报错或结果无意义。

### 6. 分页（LIMIT）

```sql
SELECT * FROM student LIMIT 5;               -- 前 5 行
SELECT * FROM student LIMIT 5, 10;           -- 从第 6 行起取 10 行（跳过 5 条）
SELECT * FROM student LIMIT 10 OFFSET 5;     -- 等价写法（OFFSET 跳过条数）
```

---

## 四、多表查询

### 1. 内连接（INNER JOIN）

只返回**两表都匹配**的行。

```sql
-- 显式内连接（推荐）
SELECT s.name, c.class_name
FROM student s
INNER JOIN class c ON s.class_id = c.id;

-- 隐式内连接（旧写法，等价）
SELECT s.name, c.class_name FROM student s, class c WHERE s.class_id = c.id;
```

### 2. 左 / 右外连接（LEFT / RIGHT JOIN）

```sql
-- 左连接：左表全部保留，右表匹配不上填 NULL
SELECT s.name, c.class_name
FROM student s
LEFT JOIN class c ON s.class_id = c.id;

-- 右连接：右表全部保留（等价于把两表顺序对调后的左连接）
SELECT s.name, c.class_name
FROM student s
RIGHT JOIN class c ON s.class_id = c.id;
```

> ⚠️ **左连接 vs 内连接（面试高频）**：左连接**保留左表所有行**，匹配不上的右边补 `NULL`；内连接只留两边都匹配的行。

### 3. 子查询

```sql
-- 标量子查询（结果单个值）
SELECT * FROM student WHERE score = (SELECT MAX(score) FROM student);

-- 列子查询（结果一列多值，配合 IN）
SELECT * FROM student WHERE class_id IN (SELECT id FROM class WHERE grade = 1);

-- 表子查询（结果当临时表用，必须起别名）
SELECT * FROM (SELECT name, score FROM student ORDER BY score DESC LIMIT 3) AS top3;
```

> ⚠️ 子查询当**表**使用时（放在 `FROM` 后）**必须起别名**。
> ⚠️ 用 `=` 配合子查询时，要确保子查询返回**单个值**（标量），多值用 `IN` / `ANY` / `ALL`。

---

## 五、常用约束

| 约束 | 关键字 | 说明 | 易错点 |
|------|--------|------|--------|
| 主键 | `PRIMARY KEY` | 唯一 + 非空，唯一标识一行 | 一张表**只能有一个**主键 |
| 外键 | `FOREIGN KEY` | 关联另一张表的主键，保证引用完整性 | 类型要和被引用列**一致** |
| 唯一 | `UNIQUE` | 值不能重复 | **允许有多个 NULL**（NULL 不算重复） |
| 非空 | `NOT NULL` | 不能为空 | 主键自带非空 |
| 默认 | `DEFAULT` | 未指定值时的默认值 | 只有插入时不填该列才生效 |

```sql
CREATE TABLE student (
    id      INT PRIMARY KEY AUTO_INCREMENT,
    email   VARCHAR(50) UNIQUE,               -- 唯一约束
    name    VARCHAR(20) NOT NULL,
    class_id INT,
    CONSTRAINT fk_stu_class FOREIGN KEY (class_id) REFERENCES class(id)  -- 外键
);
```

> ⚠️ **主键 vs 唯一（面试高频）**：主键**非空且唯一**，一表一个；唯一**允许 NULL**，可有多个。
> ⚠️ 外键列类型必须与被引用主键**完全一致**，否则建表报错。

---

## 六、常见数据类型

| 类型 | 用途 | 示例 | 说明 |
|------|------|------|------|
| `INT` | 整数 | `age INT` | 约 ±21 亿，够用；更大用 `BIGINT` |
| `VARCHAR(n)` | 变长字符串 | `VARCHAR(20)` | n 是**字符数**，不是字节 |
| `CHAR(n)` | 定长字符串 | `CHAR(1)` | 固定长度，性别/状态码用 |
| `DATE` | 日期 | `'2026-08-30'` | 年月日 |
| `DATETIME` | 日期+时间 | `'2026-08-30 10:30:00'` | 年月日时分秒 |
| `DECIMAL(m,d)` | 精确小数 | `DECIMAL(5,2)` | 共 5 位，2 位小数；**金额必用** |
| `TEXT` | 长文本 | `content TEXT` | 存文章等 |

> ⚠️ **`VARCHAR(n)` 的 n 是字符数不是字节数**，中文也按 1 个字符算。
> ⚠️ **金额/精确计算用 `DECIMAL`，不要用 `FLOAT`/`DOUBLE`**（浮点有精度误差）。
> ⚠️ **`INT(n)` 的 n 只是"显示宽度"，不影响存储范围**（如 `BIGINT(20)` 不表示 20 位）。

---

## 附：SQL 书写与执行顺序速记

**书写顺序**（必须按此写）：

```sql
SELECT ... FROM ... WHERE ... GROUP BY ... HAVING ... ORDER BY ... LIMIT ...;
```

**执行顺序**（数据库内部）：

```
FROM → WHERE → GROUP BY → HAVING → SELECT → ORDER BY → LIMIT
```

> ⚠️ 记住执行顺序，就能理解：为什么 `WHERE` 不能用聚合函数，为什么 `SELECT` 里的别名不能直接在 `WHERE` 里用（但能在 `ORDER BY` 里用）。
