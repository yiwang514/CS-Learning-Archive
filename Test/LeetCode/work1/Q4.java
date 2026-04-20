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