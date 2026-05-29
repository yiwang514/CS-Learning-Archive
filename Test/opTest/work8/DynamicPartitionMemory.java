import java.util.*;

public class DynamicPartitionMemory {

    // 分区节点类
    static class Node {
        int id;
        int size;
        int address;
        String status; // JOB名称 或 "空闲"

        Node(int id, int size, int address, String status) {
            this.id = id;
            this.size = size;
            this.address = address;
            this.status = status;
        }
    }

    private static List<Node> freeList = new LinkedList<>();      // 未分配分区表
    private static List<Node> allocatedList = new LinkedList<>(); // 已分配分区表
    private static int totalMemory, startAddr;
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.print("请输入内存大小为: ");
        totalMemory = sc.nextInt();
        System.out.print("请输入起始地址大小为: ");
        startAddr = sc.nextInt();

        // 初始化：整个内存为一个大的空闲分区
        freeList.add(new Node(1, totalMemory, startAddr, "空闲"));

        while (true) {
            System.out.println("********************可变分区管理********************");
            System.out.println("*\t\t1. 内存管理\t\t*");
            System.out.println("*\t\t2. 内存去配\t\t*");
            System.out.println("*\t\t0. 退出\t\t\t*");
            System.out.print("请输入选项: ");
            int choice = sc.nextInt();

            if (choice == 1) {
                allocationMenu();
            } else if (choice == 2) {
                deallocation();
            } else if (choice == 0) {
                break;
            }
        }
    }

    // 分配算法菜单
    private static void allocationMenu() {
        System.out.println("********************分配算法********************");
        System.out.println("*\t\t1. 最先适应算法\t\t*");
        System.out.println("*\t\t2. 最优适应算法\t\t*");
        System.out.println("*\t\t3. 最坏适应算法\t\t*");
        System.out.print("请输入选项: ");
        int algo = sc.nextInt();

        System.out.print("请输入作业名: ");
        String jobName = sc.next();
        System.out.print(jobName + "需要分配的主存大小(单位: KB): ");
        int size = sc.nextInt();

        allocate(jobName, size, algo);
    }

    // 执行分配逻辑
    private static void allocate(String jobName, int size, int algo) {
        Node target = null;

        // 根据算法排序
        if (algo == 1) { // First Fit: 按地址排序
            freeList.sort(Comparator.comparingInt(n -> n.address));
        } else if (algo == 2) { // Best Fit: 按大小升序
            freeList.sort(Comparator.comparingInt(n -> n.size));
        } else if (algo == 3) { // Worst Fit: 按大小降序
            freeList.sort((n1, n2) -> n2.size - n1.size);
        }

        // 查找满足的分区
        for (Node n : freeList) {
            if (n.size >= size) {
                target = n;
                break;
            }
        }

        if (target != null) {
            // 切割分区：前半部分给作业，后半部分留空闲
            Node newNode = new Node(0, size, target.address, jobName);
            allocatedList.add(newNode);

            if (target.size == size) {
                freeList.remove(target);
            } else {
                target.address += size;
                target.size -= size;
            }
            System.out.println("分配成功!");
            printTables();
        } else {
            System.out.println("分配失败，内存不足!");
        }
    }

    // 回收逻辑
    private static void deallocation() {
        System.out.print("请输入要回收的作业名: ");
        String jobName = sc.next();
        Node target = null;

        for (Node n : allocatedList) {
            if (n.status.equals(jobName)) {
                target = n;
                break;
            }
        }

        if (target != null) {
            allocatedList.remove(target);
            target.status = "空闲";
            freeList.add(target);
            mergeFreeBlocks(); // 合并相邻空闲区
            System.out.println("回收成功!");
            printTables();
        } else {
            System.out.println("未找到该作业!");
        }
    }

    // 合并相邻的空闲分区（核心逻辑：无上邻下邻、有上邻无下邻等四种情况的抽象）
    private static void mergeFreeBlocks() {
        if (freeList.isEmpty()) return;
        
        // 按照起始地址排序，以便发现相邻分区
        freeList.sort(Comparator.comparingInt(n -> n.address));

        for (int i = 0; i < freeList.size() - 1; i++) {
            Node current = freeList.get(i);
            Node next = freeList.get(i + 1);

            // 如果当前分区末尾地址 == 下一个分区起始地址，则合并
            if (current.address + current.size == next.address) {
                current.size += next.size;
                freeList.remove(i + 1);
                i--; // 继续检查合并后的块是否还能与后续合并
            }
        }
    }

    // 严格复刻截图中的表格输出格式
    private static void printTables() {
        System.out.println("********************主存分配情况********************");
        System.out.println("已分配:");
        System.out.println("分区号\t大小(KB)\t起始(KB)\t状态");
        allocatedList.sort(Comparator.comparingInt(n -> n.address));
        int idCounter = 1;
        for (Node n : allocatedList) {
            System.out.println((idCounter++) + "\t" + n.size + "\t\t" + n.address + "\t\t" + n.status);
        }

        System.out.println("未分配:");
        System.out.println("分区号\t大小(KB)\t起始(KB)\t状态");
        freeList.sort(Comparator.comparingInt(n -> n.address));
        for (Node n : freeList) {
            System.out.println((idCounter++) + "\t" + n.size + "\t\t" + n.address + "\t\t" + n.status);
        }
    }
}