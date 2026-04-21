// Q6. 找到所有数组中消失的数字

// 给你一个含 n 个整数的数组 nums ，其中 nums[i] 在区间 [1, n] 内。请你找出所有在 [1, n] 
// 范围内但没有出现在 nums 中的数字，并以数组的形式返回结果。

 

// 示例 1：

// 输入：nums = [4,3,2,7,8,2,3,1]
// 输出：[5,6]
// 示例 2：

// 输入：nums = [1,1]
// 输出：[2]
 

// 提示：

// n == nums.length
// 1 <= n <= 105
// 1 <= nums[i] <= n
// 进阶：你能在不使用额外空间且时间复杂度为 O(n) 的情况下解决这个问题吗? 你可以假定返回的数组不算在额外空间内。

import java.util.*;

class Solution_Q6 {
    public List<Integer> findDisappearedNumbers(int[] nums) {
        List<Integer> result = new ArrayList<>();
        for(int i=0;i<nums.length;i++){
            int index = Math.abs(nums[i])-1;
            if (nums[index]>0) {
                nums[index] = -nums[index];
            }
        }
        for(int i=0;i<nums.length;i++){
            if (nums[i]>0) {
                result.add(i+1);
            }
        }
        return result;
    }
    public static void main(String[] args) {
        Solution_Q6 solution = new Solution_Q6();
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入数组元素个数：");
        int n = sc.nextInt();
        int[] nums = new int[n];
        System.out.println("请输入数组元素：");
        for(int i=0;i<n;i++){
            nums[i] = sc.nextInt();
        }
        List<Integer> result = solution.findDisappearedNumbers(nums);
        System.out.println(result);
       sc.close();
    }
}