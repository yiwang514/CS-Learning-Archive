import java.util.*;

public class SegmentPagedStorageManager {

    // 页表结构体，对应 ye
    static class Ye {
        int num;   // 页号
        int block; // 块号
    }

    // 段表结构体，对应 duan
    static class Duan {
        int duan_num; // 段号
        String state; // 状态
        int y_len;    // 页长
        Ye[] B = new Ye[1000]; // 页表数组
    }

    // 作业结构体，对应 work
    static class Work {
        int size;     // 作业大小
        String name;  // 作业名字
        int d_len;    // 段数
        Duan[] A = new Duan[1000]; // 段表数组
    }

    private static int[][] table = new int[16][64]; // 主存位示图 (16行 * 64列 = 1024块)
    private static int availableBlockNum = 0;       // 剩余空闲块数
    private static List<Work> jobList = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // 1. 系统初始化输入
        System.out.print("请输入内存大小为：");
        int memorySize = scanner.nextInt();
        System.out.print("请输入系统字长大小(32/64)为：");
        int wordLength = scanner.nextInt();
        System.out.print("请输入块长：");
        int blockLength = scanner.nextInt();

        // 随机初始化位示图 (0/1)
        Random rand = new Random();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 64; j++) {
                table[i][j] = rand.nextInt(2);
                if (table[i][j] == 0) {
                    availableBlockNum++;
                }
            }
        }

        printSiteTable();

        // 2. 菜单主循环
        while (true) {
            System.out.println("********************可变分区管理********************");
            System.out.println("* 1. 内存分配          *");
            System.out.println("* 2. 内存去配          *");
            System.out.println("* 0. 退出              *");
            System.out.print("请输入选项：");
            int choice = scanner.nextInt();

            if (choice == 1) {
                System.out.println("1. 内存分配");
                allocate();
            } else if (choice == 2) {
                System.out.println("2. 内存去配");
                deallocate();
            } else if (choice == 0) {
                break;
            }
        }
        scanner.close();
    }

    // 内存分配
    private static void allocate() {
        printSiteTable();
        System.out.print("请输入作业名：");
        String name = scanner.next();
        System.out.print("请输入" + name + "所需主存大小：");
        int size = scanner.nextInt();

        if (size > availableBlockNum) {
            System.out.println("剩余空间不足，分配失败！");
            return;
        }

        Work work = new Work();
        work.name = name;
        work.size = size;

        System.out.print("请输入要分成几段：");
        work.d_len = scanner.nextInt();

        int remaining = size;
        for (int i = 0; i < work.d_len; i++) {
            System.out.print("剩余" + remaining + "的内存未分配，请输入第" + i + "段的大小：");
            int segSize = scanner.nextInt();

            Duan duan = new Duan();
            duan.duan_num = i;
            duan.y_len = segSize;
            duan.state = "已分配";

            // 为当前段的每一页分配物理块
            int allocatedPages = 0;
            for (int row = 0; row < 16 && allocatedPages < segSize; row++) {
                for (int col = 0; col < 64 && allocatedPages < segSize; col++) {
                    if (table[row][col] == 0) {
                        table[row][col] = 1; // 标记占用
                        
                        Ye ye = new Ye();
                        ye.num = allocatedPages;
                        ye.block = row * 64 + col; // 计算物理块号
                        duan.B[allocatedPages] = ye;

                        allocatedPages++;
                        availableBlockNum--;
                    }
                }
            }

            work.A[i] = duan;
            remaining -= segSize;
            System.out.println("内存分配成功！");
        }

        jobList.add(work);

        // 打印分配后的主存状态与段页表
        printSiteTable();
        System.out.println("作业" + name + "的分配情况如下：");
        for (int i = 0; i < work.d_len; i++) {
            System.out.println("    第" + i + "段：");
            System.out.println("        页号    块号");
            Duan d = work.A[i];
            for (int j = 0; j < d.y_len; j++) {
                System.out.printf("        %-7d %-7d\n", d.B[j].num, d.B[j].block);
            }
        }
    }

    // 内存去配
    private static void deallocate() {
        System.out.print("请输入作业名：");
        String name = scanner.next();
        Work target = null;

        for (Work w : jobList) {
            if (w.name.equals(name)) {
                target = w;
                break;
            }
        }

        if (target != null) {
            // 遍历该作业的所有段和所有页，将对应的物理块在位示图中清零
            for (int i = 0; i < target.d_len; i++) {
                Duan d = target.A[i];
                for (int j = 0; j < d.y_len; j++) {
                    int blockNum = d.B[j].block;
                    int row = blockNum / 64;
                    int col = blockNum % 64;
                    table[row][col] = 0;
                    availableBlockNum++;
                }
            }
            jobList.remove(target);
            System.out.println("去配成功！");
            printSiteTable();
        } else {
            System.out.println("未找到该作业，去配失败！");
        }
    }

    // 严格复刻截图中的 16*64 位示图对齐格式
    private static void printSiteTable() {
        System.out.println("主存位示图如下所示：");
        System.out.print("   ");
        for (int j = 0; j < 64; j++) {
            System.out.printf("%3d", j);
        }
        System.out.println();

        for (int i = 0; i < 16; i++) {
            System.out.printf("%-3d", i);
            for (int j = 0; j < 64; j++) {
                System.out.printf("%3d", table[i][j]);
            }
            System.out.println();
        }
        System.out.println("剩余空闲块数：" + availableBlockNum);
    }
}