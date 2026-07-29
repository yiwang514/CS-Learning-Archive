--**主外键关联关系的定义**

CREATE TABLE IF NOT EXISTS cls(
    number INT(11) AUTO_INCREMENT NOT NULL PRIMARY KEY COMMENT '班级编号,主键',
    name VARCHAR(20) NOT NULL COMMENT '班级名称',
    grade VARCHAR(20) NOT NULL COMMENT '年级'
)ENGINE=InnoDB CHARSET=UTF8 COMMENT='班级表';

CREATE TABLE IF NOT EXISTS student(
    number BIGINT(20) AUTO_INCREMENT NOT NULL COMMENT '学号,主键',
    name VARCHAR(20) NOT NULL COMMENT '姓名',
    sex VARCHAR(2) DEFAULT '男' COMMENT '性别:男,女,其他',
    age TINYINT(3) DEFAULT 0 COMMENT '年龄',
    cls_number INT(11) NOT NULL COMMENT '班级编号,外键',
    PRIMARY KEY(number),
    FOREIGN KEY(cls_number) REFERENCES cls(number)
)ENGINE=InnoDB CHARSET=UTF8 COMMENT='学生表';

 --约束（Constraint）是对数据表中数据的限制条件，约束可以保证数据的完整性和有效性。
 --约束分为列级约束和表级约束两种。列级约束是定义在列上的约束，表级约束是定义在表上的约束。

 1. 主键约束
 --添加主键约束：保证数据的唯一性和完整性，主键约束可以在创建表时定义，也可以在表创建后添加。
 ALTER TABLE 表名 ADD PRIMARY KEY(字段名1，字段名2,...);
 --删除主键约束：删除主键约束后，数据表中可以存在重复的记录，主键约束可以在创建表时定义，也可以在表创建后删除。
 ALTER TABLE 表名 DROP PRIMARY KEY;
 2. 外键约束
 --添加外键约束：保证数据的完整性和有效性，外键约束可以在创建表时定义，也可以在表创建后添加。
 ALTER TABLE 表名1 ADD CONSTRAINT 外键名称 FOREIGN KEY(表名1的字段名) REFERENCES 表名2(表名2的字段名);
 --删除外键约束：删除外键约束后，数据表中可以存在不完整的记录，外键约束可以在创建表时定义，也可以在表创建后删除。
 ALTER TABLE 表名1 DROP FOREIGN KEY 外键名称;
 3. 唯一约束
 --添加唯一约束：保证数据的唯一性和完整性，唯一约束可以在创建表时定义，也可以在表创建后添加。
 ALTER TABLE 表名 ADD CONSTRAINT 唯一约束名称 UNIQUE(字段名1，字段名2,...);
 --删除唯一约束：删除唯一约束后，数据表中可以存在重复的记录，唯一约束可以在创建表时定义，也可以在表创建后删除。
 ALTER TABLE 表名 DROP KEY 唯一约束名称;
 4.非空约束
 --添加非空约束：保证数据的完整性和有效性，非空约束可以在创建表时定义，也可以在表创建后添加。
 ALTER TABLE 表名 MODIFY 字段名 数据类型 NOT NULL;
 --删除非空约束：删除非空约束后，数据表中可以存在空值，非空约束可以在创建表时定义，也可以在表创建后删除。
 ALTER TABLE 表名 MODIFY 字段名 数据类型 NULL;
 5. 检查约束
 --添加检查约束：保证数据的完整性和有效性，检查约束可以在创建表时定义，也可以在表创建后添加。
 ALTER TABLE 表名 ADD CONSTRAINT 检查约束名称 CHECK(条件表达式);
 --删除检查约束：删除检查约束后，数据表中可以存在不符合条件的记录，检查约束可以在创建表时定义，也可以在表创建后删除。
 ALTER TABLE 表名 DROP CHECK 检查约束名称;
 6.默认约束
 --添加默认约束：保证数据的完整性和有效性，默认约束可以在创建表时定义，也可以在表创建后添加。
 ALTER TABLE 表名 ALTER (COLUMN) 字段名 SET DEFAULT 默认值;
 --删除默认约束：删除默认约束后，数据表中可以存在空值，默认约束可以在创建表时定义，也可以在表创建后删除。
 ALTER TABLE 表名 ALTER (COLUMN) 字段名 DROP DEFAULT;
 7.自增约束
 --添加自增约束：保证数据的唯一性和完整性，自增约束可以在创建表时定义，也可以在表创建后添加。
 ALTER TABLE 表名 MODIFY 字段名 数据类型 AUTO_INCREMENT;
 --删除自增约束：删除自增约束后，数据表中可以存在重复的记录，自增约束可以在创建表时定义，也可以在表创建后删除。
 ALTER TABLE 表名 MODIFY 字段名 数据类型;




--**索引**
--创建索引
ALTER TABLE 表名 ADD INDEX 索引名称(字段名1，字段名2,...);
--创建全局索引
ALTER TABLE 表名 ADD FULLTEXT INDEX 索引名称(字段名1，字段名2,...) ;
--查看索引
SHOW INDEX FROM 表名;
--删除索引
ALTER TABLE 表名 DROP INDEX 索引名称;


--**多表查询**
1.  内连接查询（Inner Join）：返回两个表中匹配的记录。
SELECT 字段名1, 字段名2, ... FROM 表名1 [INNER] JOIN 表名2 ON 表名1.字段名 = 表名2.字段名;
SELECT 字段名1, 字段名2, ... FROM 表名1, 表名2 [WHERE 关联条件 AND 查询条件];

2.左外连接查询（Left Outer Join）：返回左表中的所有记录，以及右表中匹配的记录，如果右表中没有匹配的记录，则返回NULL。
SELECT 字段名1, 字段名2, ... FROM 主表 LEFT JOIN 从表 [ON 连接条件];
3.右外连接查询（Right Outer Join）：返回右表中的所有记录，以及左表中匹配的记录，如果左表中没有匹配的记录，则返回NULL。
SELECT 字段名1, 字段名2, ... FROM 主表 RIGHT JOIN 从表 [ON 连接条件];



