CREATE Table IF NOT EXISTS student(
   number VARCHAR(30) NOT NULL PRIMARY KEY COMMENT '学号,主键', 
   name VARCHAR(30) NOT NULL COMMENT '姓名',
    sex TINYINT(1) NOT NULL COMMENT '性别,0为男,1为女,2为其他',
    age TINYINT(3) NOT NULL COMMENT '年龄',
    score DOUBLE(5,2) UNSIGNED NOT NULL COMMENT '成绩'
)ENGINE=InnoDB CHARSET=UTF8 COMMENT='学生表';

Alter TABLE student RENAME AS Stu;

ALTER TABLE stu ADD phone VARCHAR(11) NOT NULL COMMENT '电话号码';
DROP TABLE IF EXISTS student;

-- MODIFY 只能修改字段的修饰属性
ALTER TABLE stu MODIFY sex VARCHAR(2) DEFAULT '男' COMMENT '性别:男,女,其他';
-- CHANGE 可以修改字段的名字以及修饰属性
ALTER TABLE stu CHANGE phone mobile VARCHAR(11) NOT NULL COMMENT '联系电话';
--将stu表中的mobile 字段删除
ALTER TABLE stu DROP mobile;