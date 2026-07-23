--常用数学函数
1.ABS(X); --返回X的绝对值
SELECT ABS(-10);
2.FLOOR(X); --返回不大于X的最大整数
SELECT FLOOR(3.14);
3.CEIL(X); --返回不小于X的最小整数
SELECT CEIL(1.4);
4.TRUNCATE(X,D); --返回数值X保留到小数点后D位的值，截断时不进行四舍五入
SELECT TRUNCATE(1.233,1);
5.ROUND(X,D); --返回数值X保留到小数点后D位的值，截断时要四舍五入
SELECT ROUND(1.263,1);
6.ROUND(X); --返回离X最近的整数，要四舍五入
SELECT ROUND(1.533);
7.RAND(); --返回0到1之间的随机数
SELECT RAND();
8.MOD(X,Y); --返回X除以Y的余数
SELECT MOD(10,3);


--常用字符串函数
1.CHAR_LENGTH(X); --返回字符串X的字符数
SELECT CHAR_LENGTH("鸣潮牛逼");
2.LENGTH(X); --返回字符串X的长度，单位为字节
SELECT LENGTH("鸣潮牛逼");
--常用汉字在 UTF-8 里占 3 字节,GBK 编码下汉字只占 2 字节
3.CONCAT(X,Y,...); --返回字符串X、Y...连接后的结果,其中任意值为NULL，则返回结果为NULL
SELECT CONCAT("鸣潮","牛逼");
4.LOWER(X); LCASE(X) --返回字符串X转换为小写后的结果
SELECT LOWER("MING CHAO");
5.UPPER(X); UCASE(X) --返回字符串X转换为大写后的结果
SELECT UPPER("ming chao");
6.LEFT(X,N); --返回字符串X从左边开始的N个字符
SELECT LEFT("鸣潮牛逼",2);
7.RIGHT(X,N); --返回字符串X从右边开始的N个字符
SELECT RIGHT("鸣潮牛逼",2);
8.LTRIM(X); --返回字符串X去掉左边空格后的结果
SELECT LTRIM("  鸣潮牛逼  ");
RTRIM(X); --返回字符串X去掉右边空格后的结果
SELECT RTRIM("  鸣潮牛逼  ");
TRIM(X); --返回字符串X去掉两边空格后的结果
SELECT TRIM("  鸣潮牛逼  ");
9.REPLACE(X,Y,Z); --返回字符串X中所有的Y替换为Z后的结果
SELECT REPLACE("鸣潮牛逼","牛逼","nb");
10.SUBSTRING(X,N,len); --返回字符串X从第N个字符开始，长度为len的字符串
SELECT SUBSTRING("鸣潮牛逼",2,3);


--日期和时间函数
1.CURDATE();  CURRENT_DATE(); --返回当前日期
SELECT CURDATE();
2.CURTIME();  CURRENT_TIME(); --返回当前时间
SELECT CURTIME();
3.NOW();  CURRENT_TIMESTAMP();  SYSDATE(); --返回当前日期和时间
SELECT NOW();  SELECT SYSDATE(); SELECT CURRENT_TIMESTAMP();
4.YEAR(X); --返回日期X的年份
SELECT YEAR(NOW());
5.MONTH(X); --返回日期X的月份
SELECT MONTH(NOW());
6.DAYOFMONTH(X); --返回日期X是当月的第几天
SELECT DAYOFMONTH(NOW());
7.HOUR(X); --返回时间X的小时
SELECT HOUR(NOW());
8.MINUTE(X); --返回时间X的分钟
SELECT MINUTE(NOW());
9.SECOND(X); --返回时间X的秒
SELECT SECOND(NOW());
10.ADDDATE(X,N); --返回日期X加上N个时间单位后的日期(默认是天)
SELECT ADDDATE(NOW(),10);
11.TIMESTAMPDIFF(unit,X,Y); --返回日期X和Y之间的时间差，单位为unit(计量单位)
SELECT TIMESTAMPDIFF(DAY,NOW(),'2026-08-31');
12.DATE_FORMAT(X,format); --返回日期X按照format格式化后的结果
SELECT DATE_FORMAT(NOW(),'%Y-%m-%d %H:%i:%s');