// Q3. 最大连续 1 的个数

// 给定一个二进制数组 nums ， 计算其中最大连续 1 的个数。

 

// 示例 1：

// 输入：nums = [1,1,0,1,1,1]
// 输出：3
// 解释：开头的两位和最后的三位都是连续 1 ，所以最大连续 1 的个数是 3.
// 示例 2:

// 输入：nums = [1,0,1,1,0,1]
// 输出：2

import java.util.*;

class Solution_Q3 {
    public int findMaxConsecutiveOnes(int[] nums) {
        int maxcount=0;
        int count=0;
        for(int i=0;i<nums.length;i++){
            if(nums[i]==1){
                count++;
            }else{
                maxcount=Math.max(maxcount, count);
                count=0;
            }
        }
        return Math.max(maxcount, count);
    }
    public static void main(String[] args){
        Solution_Q3 solution = new Solution_Q3();
        Scanner sc =new Scanner(System.in);
        int n = sc.nextInt();
        int[] nums = new int[n];
        for(int i=0;i<n;i++){
            nums[i]=sc.nextInt();
        }       
        int ans = solution.findMaxConsecutiveOnes(nums);
        System.out.println(ans);
        sc.close();
    }
}