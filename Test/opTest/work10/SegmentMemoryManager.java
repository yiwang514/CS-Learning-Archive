import java.util.*;

public class SegmentMemoryManager {

    static class Partition {//  分区类：它是用来动态切分的
        int id;
        int size;
        int address;
        String status;

        Partition(int id, int size, int address, String status) {
            this.id = id;
            this.size = size;
            this.address = address;
            this.status = status;
        }
    }

    static class Segment {// 2. 段类：记录这个作业的某一段，被放在了哪个起始地址
        int id;
        int length;
        int baseAddr;

        Segment(int id, int length, int baseAddr) {
            this.id = id;
            this.length = length;
            this.baseAddr = baseAddr;
        }
    }

    private static LinkedList<Partition> partitionList = new LinkedList<>();
    private static Map<String, List<Segment>> segmentTables = new HashMap<>();
    private static Scanner sc = new Scanner(System.in);
    private static int partitionIdCounter = 1;

    public static void main(String[] args) {
        System.out.print("请输入内存大小: ");
        int totalSize = sc.nextInt();
        System.out.print("请输入起始地址: ");
        int startAddr = sc.nextInt();

        // 初始分区
        partitionList.add(new Partition(partitionIdCounter++, totalSize, startAddr, "空闲"));

        while (true) {
            System.out.println("*********可变分区管理*********");
            System.out.println("* 1. 内存分配         *");
            System.out.println("* 2. 内存去配         *");
            System.out.println("* 0. 退出             *");
            System.out.print("    请输入选项"); // 修正：只打印提示，等待输入
            String choice = sc.next();

            if (choice.equals("1")) {
                System.out.println("1. 内存分配"); 
                allocate();
            } else if (choice.equals("2")) {
                System.out.println("2. 内存去配");
                deallocate();
            } else if (choice.equals("0")) {
                System.out.println("0. 退出");
                break;
            }
        }
    }

    private static void printMemoryStatus() {
        System.out.println("******************主存分配情况******************");
        System.out.println("已分配:");
        System.out.println("分配号  大小 (KB)      起始 (KB)      状态");
        for (Partition p : partitionList) {
            if (!p.status.equals("空闲")) {
                System.out.printf("%-7d %-14d %-14d %s\n", p.id, p.size, p.address, p.status);
            }
        }
        System.out.println("\n未分配:");
        System.out.println("分配号  大小 (KB)      起始 (KB)      状态");
        for (Partition p : partitionList) {
            if (p.status.equals("空闲")) {
                System.out.printf("%-7d %-14d %-14d %s\n", p.id, p.size, p.address, p.status);
            }
        }
    }

    private static void allocate() {
        printMemoryStatus();
        System.out.print("请输入作业名:");
        String jobName = sc.next();
        System.out.print("请输入" + jobName + "需要分配的主存大小:");
        int totalNeeded = sc.nextInt();

        // 检查总空间
        int totalFree = partitionList.stream().filter(p -> p.status.equals("空闲")).mapToInt(p -> p.size).sum();
        if (totalNeeded > totalFree) {
            System.out.println("错误：容量不足！");
            return;
        }

        System.out.print("请输入要分成几段: ");
        int segCount = sc.nextInt();
        List<Segment> table = new ArrayList<>();
        int remaining = totalNeeded;

        for (int i = 0; i < segCount; i++) {
            System.out.print("剩余" + remaining + "KB的内容，请输入第" + (i + 1) + "段的大小: ");
            int segSize = sc.nextInt();

            boolean assigned = false;
            // 遍历现有的内存分区，寻找能装下当前段 (segSize) 的空闲分区
            for (int j = 0; j < partitionList.size(); j++) {
                Partition p = partitionList.get(j);
                // 首次适应算法：找到第一个空闲且够大的分区
                if (p.status.equals("空闲") && p.size >= segSize) {
                    int originalAddr = p.address;
                    p.status = jobName + i;
                    int leftover = p.size - segSize;    // 算一下切完还剩多少
                    p.size = segSize;   // 把当前分区的大小“精准缩水”到需要的尺寸

                    // 如果有剩余空间，把它变成一个新的独立空闲分区，插在后面
                    if (leftover > 0) {
                        partitionList.add(j + 1, new Partition(partitionIdCounter++, leftover, originalAddr + segSize, "空闲"));
                    }
                    table.add(new Segment(i, segSize, originalAddr));
                    assigned = true;
                    remaining -= segSize;
                    break;
                }
            }

            if (!assigned) {
                System.out.println("分配失败！");
                return;
            }
            printMemoryStatus(); // 每一段分配完都要打印一次
        }

        segmentTables.put(jobName, table);
        System.out.println("分配成功！");
        System.out.println("**********打印 " + jobName + " 段表**********");
        System.out.println("段号    段长    基址");
        for (Segment s : table) {
            System.out.printf("%-7d %-7d %-7d\n", s.id, s.length, s.baseAddr);
        }
    }

    private static void deallocate() {
        System.out.print("请输入您想回收的作业名: ");
        String jobName = sc.next();
        boolean found = false;

        Iterator<Partition> it = partitionList.iterator();
        while (it.hasNext()) {
            Partition p = it.next();
            if (p.status.startsWith(jobName)) {
                System.out.println("回收" + jobName + "的段" + p.status + "成功！");
                p.status = "空闲";
                found = true;
            }
        }

        if (found) {
            mergePartitions();
            printMemoryStatus();
        } else {
            System.out.println("未找到作业！");
        }
    }

    private static void mergePartitions() {
        for (int i = 0; i < partitionList.size() - 1; i++) {
            Partition curr = partitionList.get(i);
            Partition next = partitionList.get(i + 1);
            // 如果当前是空闲，紧挨着的下一个也是空闲
            if (curr.status.equals("空闲") && next.status.equals("空闲")) {
                curr.size += next.size;// 把下一个的大小加到自己身上
                partitionList.remove(i + 1);
                i--;
            }
        }
    }
}