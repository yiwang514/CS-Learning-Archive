CREATE Table IF NOT EXISTS student(
   number VARCHAR(30) NOT NULL PRIMARY KEY COMMENT '学号,主键', 
   name VARCHAR(30) NOT NULL COMMENT '姓名',
    sex TINYINT(1) NOT NULL COMMENT '性别,0为男,1为女,2为其他',
    age TINYINT(3) NOT NULL COMMENT '年龄',
    score DOUBLE(5,2) UNSIGNED NOT NULL COMMENT '成绩'
)ENGINE=InnoDB CHARSET=UTF8 COMMENT='学生表';

--需要用到 ALTER的情况（修改表结构）
--任何改变表的“骨架”的操作都必须用 ALTER TABLE

--DDL 分为三个基本命令：
-- CREATE TABLE：创建表
-- ALTER TABLE：修改表结构
-- DROP TABLE：删除表

Alter TABLE student RENAME AS Stu;
--VARCHAR​ 表示“可变长度字符串”（Variable Character），用于存储文本数据。
--括号里的 11​ 表示该字段允许存储的 最大字符数（不是字节数）。
ALTER TABLE stu ADD phone VARCHAR(11) NOT NULL COMMENT '电话号码';
DROP TABLE IF EXISTS student;

-- MODIFY 只能修改字段的修饰属性
ALTER TABLE stu MODIFY sex VARCHAR(2) DEFAULT '男' COMMENT '性别:男,女,其他';
-- CHANGE 可以修改字段的名字以及修饰属性
ALTER TABLE stu CHANGE phone mobile VARCHAR(11) NOT NULL COMMENT '联系电话';
--将stu表中的mobile 字段删除
ALTER TABLE stu DROP mobile;

--DML语句
-- 插入数据
INSERT INTO stu (number, name, sex, age, score) VALUES ('001', '张三', '男', 20, 85.5);
INSERT INTO stu VALUES ('002', '李四', '女', 22, 90.0);
INSERT INTO stu (number,sex,name,age,score) VALUES('003','男','王五',21,88.0);
INSERT INTO stu (number,sex,name,age,score) VALUES('004','男','王六',21,88.0),('005','女','赵六',19,92.5);

-- 修改数据
UPDATE stu SET number = '004',score = 100 WHERE name = '赵六';

-- 删除数据
DELETE FROM stu WHERE name = '王六';
-- 清空表中所有数据
TRUNCATE TABLE stu;

--DQL语句
SELECT * FROM stu;  --查询表中所有数据

SELECT name,score FROM stu; --查询表中所有学生的姓名和成绩

SELECT name FROM stu WHERE score >= 90; --查询成绩大于等于90的学生信息

SELECT score AS '成绩',age AS '年龄' FROM stu WHERE name = '张三';   --查询张三的成绩和年龄

--比较操作符
--查询学号不为NULL的学生信息
SELECT * FROM stu WHERE number IS NOT NULL; 
--查询成绩在80到90之间的学生信息s
SELECT * FROM stu WHERE score BETWEEN 80 AND 90; 
--查询姓名以'张'开头的学生信息
SELECT * FROM stu WHERE name LIKE '张%';
--查询姓名以'三'结尾的学生信息
SELECT * FROM stu WHERE name LIKE '%三';
--查询男生信息
SELECT * FROM stu WHERE sex IN ('男', '其他');

--分组
--分组查询
--分组查询所得的结果只是该组中的第一条数据
SELECT * FROM stu  WHERE score >= 80 GROUP BY sex;
SELECT * FROM stu  WHERE score BETWEEN 80 AND 90 GROUP BY sex,age;
--聚合函数
--COUNT() ：统计满足条件的数据总条数
SELECT COUNT(*) '总人数' FROM stu WHERE score >= 80;
--SUM()：只能用于数值类型的字段或者表达式，计算该满足条件的字段值的总和
SELECT COUNT(*) '总人数',SUM(score) '总成绩' FROM stu WHERE score >= 80;
--AVG()：只能用于数值类型的字段或者表达式，计算该满足条件的字段值的平均值
SELECT sex,AVG(score) '平均成绩' FROM stu GROUP BY sex;
--MAX()：只能用于数值类型的字段或者表达式，计算该满足条件的字段值的最大值
SELECT MAX(score) '最高成绩' FROM stu;
--MIN()：只能用于数值类型的字段或者表达式，计算该满足条件的字段值的最小值
SELECT MIN(score) '最低成绩' FROM stu;
--分组查询结果筛选
SELECT * FROM stu WHERE age BETWEEN 20 AND 30 GROUP BY sex HAVING AVG(score) >= 85;
--排序,DESC 降序，ASC 升序
SELECT * FROM stu ORDER BY score DESC,age ASC;
--分页
--LIMIT的第一个参数表示偏移量，也就是跳过的行数。
--LIMIT的第二个参数表示查询返回的最大行数，可能没有给定的数量那么多行。
SELECT * FROM stu LIMIT 1,3;