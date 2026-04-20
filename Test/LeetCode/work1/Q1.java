

//Q1. 数组串联

// 给你一个长度为 n 的整数数组 nums 。请你构建一个长度为 2n 的答案数组 ans ，数组下标 从 0 开始计数 ，对于所有 0 <= i < n 的 i ，满足下述所有要求：

// ans[i] == nums[i]
// ans[i + n] == nums[i]
// 具体而言，ans 由两个 nums 数组 串联 形成。

// 返回数组 ans 。

 

// 示例 1：

// 输入：nums = [1,2,1]
// 输出：[1,2,1,1,2,1]
// 解释：数组 ans 按下述方式形成：
// - ans = [nums[0],nums[1],nums[2],nums[0],nums[1],nums[2]]
// - ans = [1,2,1,1,2,1]

import java.util.*;

class Solution_Q1 {
    public int[] getConcatenation(int[] nums) {
        int n= nums.length;             //用nums.length来获取数组的长度，避免了手动输入长度可能出现的错误
        int[] ans =new int[2*n];
        for(int i=0;i<n;i++){           //数组赋值可靠循环来实现
            ans[i]=nums[i];
            ans[i+n]=nums[i];
        }
        return ans;
    }

    public static void main(String[] args) {
        Solution_Q1 solution = new Solution_Q1();
        Scanner sc =new Scanner(System.in);
        int n = sc.nextInt();
        int[] nums = new int[n];
        for(int i=0;i<n;i++){
            nums[i]=sc.nextInt();
        }       
        int[] ans = solution.getConcatenation(nums);
        System.out.println(Arrays.toString(ans));
        sc.close();
    }
}