import java.util.*;

public class PagedStorageManagement {
    static class JobNode {
        String name;
        int[] pageTable; 
        JobNode(String name, int size) {
            this.name = name;
            this.pageTable = new int[size];
        }
    }

    private static int[][] sc = new int[16][64]; 
    private static int availableBlockNum = 0;
    private static List<JobNode> jobList = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        initSystem();
        
        while (true) {
            System.out.println("*********分页式管理*********");
            System.out.println("* 1. 内存分配         *");
            System.out.println("* 2. 内存去配         *");
            System.out.println("* 0. 退出             *");
            System.out.print("请输入选项: "); // 修正：不再打印数字
            int choice = scanner.nextInt();

            if (choice == 1) {
                allocate();
            } else if (choice == 2) {
                deallocate();
            } else if (choice == 0) {
                break;
            }
        }
    }

    private static void initSystem() {
        System.out.print("请输入系统内存空间的大小：");
        int memSize = scanner.nextInt();
        System.out.print("请输入字长 (16/32/64)：");
        int wordLen = scanner.nextInt();
        System.out.print("请输入物理块大小：");
        int blockS = scanner.nextInt();

        Random rand = new Random();
        availableBlockNum = 0;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 64; j++) {
                sc[i][j] = rand.nextInt(2);
                if (sc[i][j] == 0) availableBlockNum++;
            }
        }
        printSiteTable();
    }

    private static void allocate() {
        System.out.print("请输入作业的名字：");
        String name = scanner.next();
        System.out.print("请输入作业需要分配的大小：");
        int size = scanner.nextInt();

        if (size > availableBlockNum) {
            System.out.println("剩余空间不足，分配失败！");
            return;
        }

        JobNode job = new JobNode(name, size);
        int allocatedCount = 0;
        for (int i = 0; i < 16 && allocatedCount < size; i++) {
            for (int j = 0; j < 64 && allocatedCount < size; j++) {
                if (sc[i][j] == 0) {
                    sc[i][j] = 1; 
                    job.pageTable[allocatedCount] = i * 64 + j;
                    allocatedCount++;
                    availableBlockNum--;
                }
            }
        }
        jobList.add(job);
        System.out.println("分配成功！");
        printSiteTable();
        printPageTable(job);
    }

    private static void deallocate() {
        System.out.print("请输入当前要回收的作业名：");
        String name = scanner.next();
        JobNode target = null;
        for (JobNode j : jobList) {
            if (j.name.equals(name)) { target = j; break; }
        }

        if (target != null) {
            for (int blockNum : target.pageTable) {
                sc[blockNum / 64][blockNum % 64] = 0;
                availableBlockNum++;
            }
            jobList.remove(target);
            System.out.println("该作业回收成功！");
            printSiteTable();
        } else {
            System.out.println("无此作业！回收失败！");
        }
    }

    private static void printSiteTable() {
        System.out.println("主存位示图如下所示：");
        System.out.print("   ");
        for (int j = 0; j < 64; j++) System.out.printf("%-2d", j);
        System.out.println();
        for (int i = 0; i < 16; i++) {
            System.out.printf("%-3d", i);
            for (int j = 0; j < 64; j++) System.out.print(sc[i][j] + " ");
            System.out.println();
        }
        System.out.println("剩余物理块数" + availableBlockNum);
    }

    private static void printPageTable(JobNode job) {
        System.out.println("*********打印" + job.name + "作业的页表*********");
        for (int i = 0; i < job.pageTable.length; i++) {
            System.out.println(i + "       " + job.pageTable[i]);
        }
    }
}