// Q4. 错误的集合

// 集合 s 包含从 1 到 n 的整数。不幸的是，因为数据错误，导致集合里面某一个数字复制成了集合里面的另外一个数字的值，导致集合 丢失了一个数字 并且 有一个数字重复 。

// 给定一个数组 nums 代表了该集合发生错误后的结果。

// 请你找出重复出现的整数，再找到丢失的整数，将它们以数组的形式返回。

 

// 示例 1：

// 输入：nums = [1,2,2,4]
// 输出：[2,3]
// 示例 2：

// 输入：nums = [1,1]
// 输出：[1,2]

import java.util.*;

class Solution_Q4 {
    public int[] findErrorNums(int[] nums) {
        int[] anum = new int[2];
        int n = nums.length;
        for(int i= 0;i<n;i++){
            int index = Math.abs(nums[i])-1;  //获取下标
            if(nums[index] < 0){  //如果下标对应的数已经被访问过了，说明这个数是重复的
                anum[0] = index+1;  //记录重复的数
            } else {
                nums[index] = -nums[index];  //标记该数已被访问
            }
        }
        for(int i=0;i<n;i++){
            if(nums[i] > 0){  //如果下标对应的数没有被访问过，说明这个数是丢失的
                anum[1] = i+1;  //记录丢失的数
                break;
            }
        }
        return anum;
    }

    public static void main(String[] args) {
        Solution_Q4 solution = new Solution_Q4();
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入数组元素个数：");
        int n = sc.nextInt();
        int[] nums = new int[n];
        System.out.println("请输入数组元素：");
        for(int i=0;i<n;i++){
            nums[i] = sc.nextInt();
        }
        int[] result = solution.findErrorNums(nums);
        System.out.println(Arrays.toString(result));
       sc.close();
    }
}


//标记法：原地哈希
// 值 → 下标：每个数字 v 都会去访问 nums[v-1] 这个位置。

// 标记：将 nums[v-1] 变成负数，表示“值 v 已出现”。

// 找缺失：因为缺失的数字 m 从未出现，所以 nums[m-1] 永远不会被访问，也就不会被改成负数，遍历时就能发现它仍为正数。

// 这个逻辑完全不依赖数组的排列顺序，只依赖“值域与下标域的天然一一对应”
// 初始数组：[5, 2, 4, 2, 1]（长度 n=5，值 1~5）

// i	nums[i]	abs	index = abs-1	当前 nums[index]	操作	数组变化
// 0	5	     5	      4	        nums[4]=1（正）	    标记负	[5,2,4,2,-1]
// 1	2	     2	      1	        nums[1]=2（正）	    标记负	[5,-2,4,2,-1]
// 2	4	     4	      3	        nums[3]=2（正）	    标记负	[5,-2,4,-2,-1]
// 3	2	     2	      1	        nums[1]=-2（负）	找到重复2	不变
// 4	1	     1	      0	        nums[0]=5（正）	    标记负	[-5,-2,4,-2,-1]
