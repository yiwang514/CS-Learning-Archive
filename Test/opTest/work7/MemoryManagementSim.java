import java.util.Scanner;

public class MemoryManagementSim {
    // 对应 struct Y
    static class Partition {
        int id;
        int sz;
        int address;
        String station; // 状态："0" 表示空闲，"JOBx" 表示被哪个作业占用了

        Partition(int id, int sz, int address) {
            this.id = id;
            this.sz = sz;
            this.address = address;
            this.station = "0";
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // 1. 分区输入
        System.out.print("请输入系统的分区块数：");
        int n = sc.nextInt();
        Partition[] a = new Partition[n];
        System.out.println("请依次输入：");
        System.out.println("分区号 大小 起始");
        for (int i = 0; i < n; i++) {
            int id = sc.nextInt();
            int sz = sc.nextInt();
            int addr = sc.nextInt();
            a[i] = new Partition(id, sz, addr);
        }

        // 2. 打印初始分区信息
        printPartitionInfo(a);

        // 3. 作业输入
        System.out.print("请输入作业的个数：");
        int m = sc.nextInt();
        int[] v = new int[m];// 重点：建了一个叫 v 的数组，专门用来存“每个作业需要多大空间”
        System.out.println("请输入这" + m + "个作业的信息：");
        for (int i = 0; i < m; i++) {
            System.out.print("请输入作业" + (i + 1) + "的大小：");
            v[i] = sc.nextInt();
        }

        // 4. 打印作业信息
        System.out.println("打印各作业的信息：");
        System.out.println("作业名 作业大小");
        for (int i = 0; i < m; i++) {
            System.out.println("JOB" + (i + 1) + " " + v[i] + "KB");
        }

        // 5. 分配内存 (First Fit)
        for (int i = 0; i < m; i++) {       // 外层循环：遍历每一个作业（客人）
            for (int j = 0; j < n; j++) {       // 内层循环：对于每一个作业，从头到尾遍历所有的内存分区
                // 【核心判断逻辑】：当前这个包厢必须是空的，当前包厢的容量，必须大于等于作业需要的大小
                if (a[j].station.equals("0") && a[j].sz >= v[i]) {
                    a[j].station = "JOB" + (i + 1);
                    break;
                }
            }
        }

        // 6. 打印分配后的分区信息
        printPartitionInfo(a);

        // 7. 回收流程
        while (true) {
            System.out.print("是否需要回收(y/n)？");
            String choice = sc.next();
            if (choice.equalsIgnoreCase("y")) {
                printPartitionInfo(a);
                System.out.print("请输入回收的作业名：");
                String jobName = sc.next();
                
                boolean found = false;// 搞个标志位，记录找没找到
                for (int i = 0; i < n; i++) {
                    if (a[i].station.equals(jobName)) {
                        a[i].station = "0";
                        found = true;
                        break;
                    }
                }
                
                if (found) {
                    System.out.println("回收成功");
                }
                printPartitionInfo(a);
            } else {
                break;
            }
        }

        sc.close();
    }

    // 严格复刻截图中的表格打印
    private static void printPartitionInfo(Partition[] a) {
        System.out.println("**********打印分区信息**********");
        System.out.println("分区号 大小(KB) 起始(KB) 状态");
        for (Partition p : a) {
            System.out.println(p.id + "      " + p.sz + "      " + p.address + "      " + p.station);
        }
    }
}