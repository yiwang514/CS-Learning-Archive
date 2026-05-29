import java.util.*;

public class BankersAlgorithm {
  
    int zy; // 资源种类
    int N, M; // N为资源数, M为进程数
    int[] Available; // 系统的可用资源量
    int[][] Max; // 进程对资源的最大需求量
    int[][] Need; // 进程剩余所需资源量
    int[][] Allocation; // 进程已占有的资源量
    int[] Finish; // 表示系统是否有能力使进程执行完成 (0/1)
    int[][] Request; // 进程某次申请的资源数量
    int[] State; // 进程所处状态 (0: Working, 1: Finished)
    int[] Work; // 工作向量
    String[] Name; // 记录进程的名字
    int[] safe_arr; // 安全序列

    Scanner sc = new Scanner(System.in);

    public void init() {
        System.out.println("请输入进程数:");
        M = sc.nextInt();// 对应数据结构中的M（进程数）

        System.out.println("请输入资源种类的数量:");
        zy = sc.nextInt();
        N = zy; // 对应数据结构中的N（资源数）

        // 初始化数组
        Available = new int[N];
        Max = new int[M][N];
        Allocation = new int[M][N];
        Need = new int[M][N];
        Name = new String[M];
        State = new int[M];
        Finish = new int[M];
        safe_arr = new int[M];
        Work = new int[N];
        Request = new int[M][N]; 

        System.out.println("请输入" + zy + "类资源初始化的资源数:");
        for (int i = 0; i < N; i++) {
            Available[i] = sc.nextInt();
        }

        System.out.println("请输入进程的相关信息:");
        System.out.println("进程名  最大需求量");
        for (int i = 0; i < M; i++) {
            Name[i] = sc.next();
            for (int j = 0; j < N; j++) {
                Max[i][j] = sc.nextInt();
                Need[i][j] = Max[i][j]; // 初始状态 Need = Max
            }
        }

        System.out.println("进程开始执行！");
        System.out.println("请为进程分配相关资源！");
        
        // 第一次直接进入分配
        handleRequest();

        // 循环申请
        while (true) {
            System.out.println("\n是否需要申请资源?(Y/N):"); 
            String choice = sc.next();
            if (choice.equalsIgnoreCase("Y")) {     //比较字符串是否相等的方法，忽略大小写。
                handleRequest();
            } else {
                displayTable();
                break;
            }
        }
    }

    private void handleRequest() {
        while (true) {
            System.out.println("请输入进程名:");
            String targetName = sc.next();
            int pIdx = -1;  //程序在数组中对应的下标
            for (int i = 0; i < M; i++) {
                if (Name[i].equals(targetName)) {
                    pIdx = i;
                    break;
                }
            }

            if (pIdx == -1 || State[pIdx] == 1) {
                System.out.println("进程名无效或已完成，请重新输入！");
                continue;
            }

            System.out.println("请输入该进程当前申请的各个资源数量");
            // 使用 Request[pIdx] 存储本次申请
            for (int j = 0; j < N; j++) {
                Request[pIdx][j] = sc.nextInt();
            }

            // 判断申请异常 (Request > Available 或 Request > Need)
            boolean isAbnormal = false;
            for (int j = 0; j < N; j++) {
                if (Request[pIdx][j] > Available[j] || Request[pIdx][j] > Need[pIdx][j]) {
                    isAbnormal = true;
                    break;
                }
            }

            if (isAbnormal) {
                System.out.println("申请异常!");
                System.out.println("系统可用资源如下:");
                for (int j = 0; j < N; j++) System.out.print(Available[j] + " ");
                System.out.println("\n当前进程剩余所需资源如下:");
                for (int j = 0; j < N; j++) System.out.print(Need[pIdx][j] + " ");
                System.out.println("\n请重新输入!");
                continue; // 申请异常直接回到输入进程名
            }

            // 预分配 (先假设分配成功，后续检查安全性，如果不安全则回滚)
            for (int j = 0; j < N; j++) {
                Available[j] -= Request[pIdx][j];    // 系统可用的减少
                Allocation[pIdx][j] += Request[pIdx][j];    // 进程已分配的增加
                Need[pIdx][j] -= Request[pIdx][j];   // 进程还需要的减少
            }

            if (checkSafe()) {
                // 如果 Need 归零，释放资源
                boolean finished = true;
                for (int j = 0; j < N; j++) if (Need[pIdx][j] != 0) finished = false;
                
                if (finished) {
                    State[pIdx] = 1;
                    for (int j = 0; j < N; j++) {
                        Available[j] += Allocation[pIdx][j];
                        Allocation[pIdx][j] = 0;
                    }
                }

                // 判断是否全部完成
                boolean allDone = true;
                for (int i = 0; i < M; i++) if (State[i] == 0) allDone = false;

                if (allDone) {
                    System.out.println("申请成功！安全序列为:进程已全部完成，无需安全序列");
                } else {
                    System.out.print("申请成功！安全序列为:");
                    printSafeSeq();
                }
                displayTable();
                return; // 成功后跳出，去询问 Y/N
            } else {
                // 回滚
                for (int j = 0; j < N; j++) {
                    Available[j] += Request[pIdx][j];
                    Allocation[pIdx][j] -= Request[pIdx][j];
                    Need[pIdx][j] += Request[pIdx][j];
                }
                System.out.println("无安全序列，请重新输入！");
                return; // 不安全则去询问 Y/N
            }
        }
    }

    private boolean checkSafe() {
        // 使用 Work = Available 的副本
    // 为什么要复制？因为 Available 是系统的真实数据，不能被检查过程污染
        for (int i = 0; i < N; i++) Work[i] = Available[i];
        // 使用 Finish 变量名 (0: false, 1: true)
        for (int i = 0; i < M; i++) Finish[i] = State[i]; // 已完成的设为1
        // 寻找满足 Need <= Work 且 Finish == 0 的进程
      // 统计有多少个进程还没完成，作为循环终止条件
        int target = 0;
        for (int i = 0; i < M; i++) if (State[i] == 0) target++;
         // 核心循环：最多需要找到 target 个进程
        int foundIdx = 0;
        while (foundIdx < target) {
            boolean found = false;  // 标记本轮是否找到了可执行的进程
            for (int i = 0; i < M; i++) {
                if (Finish[i] == 0) {// 还没被标记为完成
                    //检查 Need[i] 的每个分量是否都 <= Work 的对应分量
                    boolean canAlloc = true;
                    for (int j = 0; j < N; j++) {
                        if (Need[i][j] > Work[j]) {
                            canAlloc = false;
                            break;
                        }
                    }
                    if (canAlloc) {
                        for (int j = 0; j < N; j++) Work[j] += Allocation[i][j];// 假装它执行完并归还了资源
                        Finish[i] = 1;
                        safe_arr[foundIdx++] = i;
                        found = true;
                    }
                }
            }
             // 本轮扫描完，没有找到任何可执行的进程 → 不安全
            if (!found) return false;
        }
         // 所有未完成的进程都找到了 → 安全
        return true;
    }

    private void printSafeSeq() {
        int unfinished = 0;
        for (int i = 0; i < M; i++) if (State[i] == 0) unfinished++;
        
      
        for (int i = 0; i < unfinished; i++) {
            System.out.print(Name[safe_arr[i]]);
            if (i < unfinished - 1) System.out.print("->");
        }
        System.out.println();
    }

    private void displayTable() {
        System.out.println("进程名\t最大需求量\t尚需求量\t已分配量\t执行状态:");
        for (int i = 0; i < M; i++) {
            System.out.print(Name[i] + "\t");
            for (int j = 0; j < N; j++) System.out.print(Max[i][j] + " ");
            System.out.print("\t\t");
            for (int j = 0; j < N; j++) System.out.print(Need[i][j] + " ");
            System.out.print("\t\t");
            for (int j = 0; j < N; j++) System.out.print(Allocation[i][j] + " ");
            System.out.print("\t\t");
            System.out.println(State[i] == 0 ? "Working" : "Finished");
        }
        System.out.println("系统剩余资源:");
        for (int i = 0; i < N; i++) System.out.print(Available[i] + " ");
        System.out.println();
    }

    public static void main(String[] args) {
        new BankersAlgorithm().init();
    }
}