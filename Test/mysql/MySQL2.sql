--常用数学函数
1.ABS(X); --返回X的绝对值
SELECT ABS(-10);
2.FLOOR(X); --返回不大于X的最大整数
SELECT FLOOR(3.14);
3.CEIL(X); --返回不小于X的最小整数
SELECT CEIL(1.4);
4.TRUNCATE(X,D); --返回数值X保留到小数点后D位的值，截断时不进行四舍五入
SELECT TRUNCATE(1.233,1);