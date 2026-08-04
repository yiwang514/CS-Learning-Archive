--**变量**
1.全局变量
--显示所有的全局变量
SHOW GLOBAL VARIABLES;
--设置全局变量得到值的两种方式
SET GLOBAL sql_warnings = ON;
SET @@GLOBAL.sql_warnings = OFF;
--查询全局变量的值的两种方式
SELECT @@GLOBAL.sql_warnings;
SHOW GLOBAL VARIABLES LIKE '%sql_warnings%';

2.会话变量
--显示所有的会话变量
SHOW SESSION VARIABLES;
--设置会话变量的值的三种方式
SET SESSION auto_increment_increment = 1;
SET @@SESSION.auto_increment_increment = 2;
--当省略SESSION关键字时，默认缺省为SESSION，即设置会话变量的值
SET auto_increment_increment = 3;
--查询会话变量的值的三种方式
SELECT @@auto_increment_increment;
SELECT @@SESSION.auto_increment_increment;
--SESSION关键字可以省略，也可以用关键字LOCAL替代
SHOW SESSION VARIABLES LIKE '%auto_increment_increment%';

SET @@LOCAL.auto_increment_increment = 1;
SELECT @@LOCAL.auto_increment_increment;

3.用户变量（不需要提前指定数据类型，直接带 @ 就能用。）
--第一种用法，使用SET时可以用“=”或“:=”来赋值
SET @age = 19;
--第二种用法，使用SELECT时只能用“:=”来赋值
SELECT @age := 22;
SELECT @age := age FROM stu WHERE name = '张三';
--第三种用法,使用SELECT...INTO时语句赋值
SELECT age INTO @age FROM stu WHERE name = '张三';
SELECT @age;

4.局部变量
--局部变量只能在存储过程、函数、触发器中BEGIN/END语句块中使用，不能在普通的SQL语句中使用。必须先定义数据类型，才能使用。
--定义局部变量
DECLARE age INT(3) DEFAULT 0;
--为局部变量赋值
SET age = 10;
SELECT age :=10;
SELECT 10 INTO age;
SELECT age;






**存储过程**
    语法：
    --声明 分隔符
    [DELIMITER $$]
    CREATE PROCEDURE 存储过程名称 ([IN|OUT|INOUT]参数名1 数据类型,[[IN|OUT|INOUT]参数名2 数据类型,...
    [IN|OUT|INOUT]参数名n 数据类型])
    --语句块开始
    --SQL语句集
    --还原 分隔符
    [DELIMITER ;]
    --调用存储过程
    CALL 存储过程名(参数1，参数2...);

    实例：
    使用存储过程完成银行转账业务
    --创建存储过程
    CREATE PROCEDURE transfer (IN treansferFrom BIGINT,IN transferTo BIGINT.IN transferMoney BIGINT(20))
    BEGIN
        UPDATE account SET balance = balance - transferMoney WHERE account =  transferFrom
        UPDATE account SET balance = balance + transferMoney WHERE account = transferTo;
        END
    CALL transfer(123456,123457,2000);
    
    如果转账账户余额不足，上面的SQL代码依然可以正常执行，只是执行完后，转账账户的余额变为了负数。这显然不符合常理。因此需要修正。
    DROP PROCEDURE IF EXISTS `查询员工月度业绩`;
    CREATE PROCEDURE transfer(IN transferFrom BIGINT,IN transferTo BIGINT,IN transferMoney BIGINT(20))
    BEGIN
        --定义变量表示执行结果：0- 失败，1-成功
        DECLARE result TINYINT(1) DEFAULT 0;
        --转账账户必须保证余额大于等于转账金额
        UPDATE  account SET balance = balance - transferMoney WHERE account = transferFrom AND balance >= transferMoney;
        --检测受影响的行数是否为1，为1表示更新成功
        IF ROW_COUNT() =1 THEN
        --目标账号余额增加
        UPDATE account SET balance = balance + transferMoney WHERE account = transferTo;
            IF ROW_COUNT() =1 THEN 
        --更新结果为1
            SET result =1;
            END IF;
        END IF;
        --查询结果
        SELECT result;
    END






**自定义函数**
    语法：
    CREATEFUNCTION 函数名（参数名1 数据类型，参数名2 数据类型，...,参数名n 数据类型）
    RETURNS 数据类型
    --函数特征：
    -- DETERMINISTIC：确定的
    -- NO SQL：没有SQL语句，当然也不会修改数据
    -- READS SQL DATA：只是读取数据，不会修改数据
    -- MODIFIES SQL DATA：要修改数据
    -- CONTAINS SQL：包含了SQL语句
    DETERMINISTIC | NO SQL | READS SQL DATA | MODIFIES SQL DATA | CONTAINS SQL
    语句块开始
    BEGIN
        SQL语句集
        RETURN 结果；
    END

    示例：使用函数实现求score表中的成绩最大差值
    CREATE FUNCTION getMaxDiff()
    RETURNS DOUBLE（10,2）
    DETERMINISTIC
    BEGIN
        RETURN(SELECT MAX(score)-MIN(score) FROM score);
    END
    调用函数
    SELECT getMaxDiff();

    循环结构
    WHILE 循环条件 DO
        SQL语句集
    END WHILE;

    REPEAT 
    -- SQL语句集
    UNTIL 循环条件 END REPEAT;
    标号：LOOP
    -- SQL语句集
        IF 循环终止条件 THEN LEAVE 标号;
        END IF;
    END LOOP;

--示例：使用函数实现求0~给定的任意整数的累加和
CREATE FUNCTION getTotal(maxNum INT(11))
RETURNS INT(11)
NO SQL
BEGIN
    DECLARE i INT(11) DEFAULT 0;
    DECLARE total INT(11) DEFAULT 0;
    WHILE i <= maxNum DO
        SET total = total + i;
        SET i = i + 1;
    END WHILE;
    RETURN total;
END





--  触发器
    定义：
    DROP TRIGGER IF EXISTS 触发器名称;
    --创建触发器-》触发时机为BEFORE或者AFTER——》触发事件，为INSERT，UPDATE或者DELETE
    CREATE TRIGGER 触发器名称 {BEFORE|AFTER} {INSERT|UPDATE|DELETE} ON 表名 FOR EACH ROW
    BEGIN
        SQL语句集
    END

实例一：现有商品表goods和订单表order，每一个订单购买数量的更新都意味着商品数量的变动，请使用触发器完
成这一过程。
    --创建触发器
    CREATE TRIGGER updateOrder AFTER UPDATE ON  `order` FOR EACH ROW
    BEGIN
        DECLARE changeNum INT(11) DEFAULT 0;
        SET changeNum = NEW.sale_count - OLD.sale_count;
        UPDATE goods SET number = number - changeNum WHERE goods_id = OLD.goods_id;
    END

UPDATE `order` SET sale_count = sale_count +2WHERE id=20;






--视图 
    语法
    --创建视图
    CREATE VIEW 视图名称 AS SELECT 字段1,字段2,... FROM 表名 WHERE 条件;
    --更新视图
    CREATE OR REPLACE VIEW 视图名称 AS SELECT 字段1,字段2,... FROM 表名 WHERE 条件;
    --删除视图
    DROP VIEW IF EXISTS 视图名称;
    实例：定制用户数据，聚焦特定的数据。例如：如果频繁获取销售人员编号、姓名和代理商名称，可以创建视图
    CREATE OR REPLACE VIEW salesInfo AS
    SELECT a.id,
           a.`name` saleName,
           b.`name` agentName
    FROM
        sales a,
        agent b
    WHERE
        a.agent_id = b.id;


    
