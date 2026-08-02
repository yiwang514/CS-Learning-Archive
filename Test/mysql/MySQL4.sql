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



