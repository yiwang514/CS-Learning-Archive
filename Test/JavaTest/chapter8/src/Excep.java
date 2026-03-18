import java.util.Scanner;
import java.util.Random;

class WarshallAlgorithm1{
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        System.out.print("请输入矩阵的大小：");
        int n = sc.nextInt();
        boolean[][] matrix = generateRandomMatrix(n);
        Random random = new Random();
        System.out.println("\n随机生成的关系矩阵：");
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                System.out.print(matrix[i][j]?"1":"0");
            }
            System.out.println();
        }

        boolean[][] result = warshall(matrix);
        System.out.println("\n传递闭包：");
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                System.out.print(result[i][j]?"1":"0");
            }
            System.out.println();
        }
        System.out.println("\n程序执行完毕，按回车键退出...");
        sc.nextLine(); // 消耗之前的换行符
        sc.nextLine(); // 等待用户输入回车
    }
    public static boolean[][] generateRandomMatrix(int n) {
        Random random = new Random();
        boolean[][] matrix = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = random.nextBoolean();
            }
        }
        return matrix;
    }
    public static boolean[][] warshall(boolean[][] matrix){
        int n = matrix.length;
        boolean[][] result = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = matrix[i][j];
            }
        }
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (result[i][k] == result[k][j]) {
                        result[i][j] = true;
                    }
                }
            }
        }
        return result;
    }

}