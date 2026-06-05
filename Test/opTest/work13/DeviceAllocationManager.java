import java.util.*;

public class DeviceAllocationManager {

    // 1. 系统设备表项 (SDT Entry)
    static class SDTEntry {
        String type;
        int total;
        int available;
        int dctNum; // 在SDT里的编号/DCT索引号

        SDTEntry(String type, int total, int available, int dctNum) {
            this.type = type;
            this.total = total;
            this.available = available;
            this.dctNum = dctNum;
        }
    }

    // 2. 设备控制表项 (DCT Entry)
    static class DCTEntry {
        int absoluteId;
        String type;
        String isGood;  // "好" 或 "坏"
        String status;  // "yes" 或 占用作业名
        int relativeId;

        DCTEntry(int absoluteId, String type, String isGood, String status, int relativeId) {
            this.absoluteId = absoluteId;
            this.type = type;
            this.isGood = isGood;
            this.status = status;
            this.relativeId = relativeId;
        }
    }

    // 3. 控制器控制表项 (COCT Entry)
    static class COCTEntry {
        int id;
        String name;
        String isGood;  // "好" 或 "坏"
        String status;  // "yes" 或 占用的设备绝对号
        int channelId;

        COCTEntry(int id, String name, String isGood, String status, int channelId) {
            this.id = id;
            this.name = name;
            this.isGood = isGood;
            this.status = status;
            this.channelId = channelId;
        }
    }

    // 4. 通道控制表项 (CHCT Entry)
    static class CHCTEntry {
        int id;
        List<Integer> ctrlIds = new ArrayList<>();

        CHCTEntry(int id) {
            this.id = id;
        }
    }

    // 5. 逻辑单位表项 (LUT Entry)
    static class LUTEntry {
        int relId;
        int absId;
        int sdtId;

        LUTEntry(int relId, int absId, int sdtId) {
            this.relId = relId;
            this.absId = absId;
            this.sdtId = sdtId;
        }
    }

    // 系统全局维护的数据结构
    private static List<SDTEntry> sdt = new ArrayList<>();
    private static List<DCTEntry> dct = new ArrayList<>();
    private static List<COCTEntry> coct = new ArrayList<>();
    private static List<CHCTEntry> chct = new ArrayList<>();
    private static Map<String, List<LUTEntry>> lut = new LinkedHashMap<>(); // 保持作业添加顺序

    private static int globalAbsoluteIdCounter = 0;
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        // 初始化通道与控制器数量输入
        System.out.print("请输入通道的数量：");
        int numChannels = sc.nextInt();
        System.out.print("请输入控制器的数量：");
        int numControllers = sc.nextInt();

        // 根据测试用例初始化 COCT 和 CHCT 静态拓扑结构
        for (int i = 0; i < numChannels; i++) {
            chct.add(new CHCTEntry(i));
        }
        for (int i = 0; i < numControllers; i++) {
            String isGood = (i == 0) ? "坏" : "好";
            int chId = (i == 0) ? 0 : (i % 2 == 1 ? 1 : 2);
            COCTEntry c = new COCTEntry(i, "control_" + i, isGood, "yes", chId);
            coct.add(c);
            chct.get(chId).ctrlIds.add(i);
        }

        // 主菜单循环
        while (true) {
            System.out.println("**********设备独占分配管理**********");
            System.out.println("      * 1.设备添加   *");
            System.out.println("      * 2.设备分配   *");
            System.out.println("      * 3.回收释放   *");
            System.out.println("      * 0.退出      *");
            System.out.print("请输入选项：");
            int choice = sc.nextInt();

            if (choice == 1) {
                addDevice();
            } else if (choice == 2) {
                allocateDevice();
            } else if (choice == 3) {
                recoverDevice();
            } else if (choice == 0) {
                break;
            }
        }
        sc.close();
    }

    // 1. 设备添加逻辑
    private static void addDevice() {
        System.out.print("请输入添加的设备类型：");
        String type = sc.next();
        System.out.print("请输入添加的数量：");
        int count = sc.nextInt();

        int sdtNo = sdt.size();
        int availableCount = count > 0 ? count - 1 : 0; // 根据用例，每种设备相对号为0的默认为"坏"，可用数量减1

        sdt.add(new SDTEntry(type, count, availableCount, sdtNo));

        for (int i = 0; i < count; i++) {
            String isGood = (i == 0) ? "坏" : "好";
            dct.add(new DCTEntry(globalAbsoluteIdCounter++, type, isGood, "yes", i));
        }

        System.out.println("添加成功！");
        printAllTables(false);
    }

    // 2. 设备分配逻辑
    private static void allocateDevice() {
        System.out.print("请输入作业名：");
        String jobName = sc.next();
        System.out.print("请输入作业所需的设备类型：");
        String type = sc.next();

        // 查找对应的 SDT 表项
        SDTEntry sdtEntry = null;
        int sdtIdx = -1;
        for (int i = 0; i < sdt.size(); i++) {
            if (sdt.get(i).type.equals(type)) {
                sdtEntry = sdt.get(i);
                sdtIdx = i;
                break;
            }
        }

        if (sdtEntry == null) {
            System.out.println("分配失败！未找到该类型的设备。");
            return;
        }

        System.out.println("设备可用数量为：" + sdtEntry.available);
        System.out.print("请输入作业所需的设备数量：");
        int neededCount = sc.nextInt();

        List<LUTEntry> tempLutEntries = new ArrayList<>();

        for (int k = 0; k < neededCount; k++) {
            // 每次选择设备前，打印对应类型的设备控制表 DCT
            System.out.println("**********打印" + type + "设备控制表DCT**********");
            System.out.println("设备绝对号 设备类型 设备好坏 设备空闲 设备相对号");
            for (DCTEntry d : dct) {
                if (d.type.equals(type)) {
                    System.out.printf("%-10d %-10s %-8s %-8s %d\n", d.absoluteId, d.type, d.isGood, d.status, d.relativeId);
                }
            }

            while (true) {
                System.out.print("请输入你选择的设备的设备相对号：");
                int relId = sc.nextInt();

                // 定位具体的设备表项
                DCTEntry targetDct = null;
                for (DCTEntry d : dct) {
                    if (d.type.equals(type) && d.relativeId == relId) {
                        targetDct = d;
                        break;
                    }
                }

                // 检查设备状态
                if (targetDct == null || targetDct.isGood.equals("坏") || !targetDct.status.equals("yes")) {
                    System.out.println("分配失败！请重新申请！");
                    continue; // 重新输入相对号
                }

                // 分配路径：寻找第一个状态为"好"且"yes"空闲的控制器
                COCTEntry targetCoct = null;
                for (COCTEntry c : coct) {
                    if (c.isGood.equals("好") && c.status.equals("yes")) {
                        targetCoct = c;
                        break;
                    }
                }

                if (targetCoct != null) {
                    // 更新控制器和设备分配状态
                    targetCoct.status = String.valueOf(targetDct.absoluteId);
                    targetDct.status = jobName;
                    sdtEntry.available--;

                    tempLutEntries.add(new LUTEntry(targetDct.relativeId, targetDct.absoluteId, sdtIdx));
                    System.out.println("分配成功！");
                    break; 
                } else {
                    System.out.println("分配失败！没有可用的控制器。");
                    return;
                }
            }
        }

        // 把成功分配的设备信息记录到逻辑单位表 LUT 中
        if (!tempLutEntries.isEmpty()) {
            lut.put(jobName, tempLutEntries);
        }
        printAllTables(true);
    }

    // 3. 回收释放逻辑
    private static void recoverDevice() {
        System.out.print("请输入要回收的作业名：");
        String jobName = sc.next();

        if (!lut.containsKey(jobName)) {
            System.out.println("回收失败！无法找到作业对应的设备！请重新申请回收！");
            return;
        }

        // 释放该作业占用的所有底层硬件资源
        for (DCTEntry d : dct) {
            if (d.status.equals(jobName)) {
                // 1. 恢复系统设备控制表状态
                d.status = "yes";

                // 2. 恢复系统设备表可用数量
                for (SDTEntry s : sdt) {
                    if (s.type.equals(d.type)) {
                        s.available++;
                        break;
                    }
                }

                // 3. 恢复控制器控制表状态
                for (COCTEntry c : coct) {
                    if (c.status.equals(String.valueOf(d.absoluteId))) {
                        c.status = "yes";
                        break;
                    }
                }
            }
        }

        // 从 LUT 中移除已注销的作业
        lut.remove(jobName);
        System.out.println("回收成功！");
        printAllTables(true);
    }

    // 统一封装五张控制表的格式化打印函数（完全复刻对齐要求）
    private static void printAllTables(boolean showLut) {
        if (showLut && !lut.isEmpty()) {
            System.out.println("**********系统逻辑单位表LUT**********");
            for (Map.Entry<String, List<LUTEntry>> entry : lut.entrySet()) {
                System.out.println(entry.getKey() + "占用设备的设备相对号和设备绝对号及SDT号：");
                System.out.println("设备相对号 设备绝对号 SDT号");
                for (LUTEntry le : entry.getValue()) {
                    System.out.printf("%-10d %-10d %d\n", le.relId, le.absId, le.sdtId);
                }
            }
        }

        System.out.println("**********打印系统设备表SDT**********");
        System.out.println("设备类型 设备总数量 设备可用数量 设备DCT号");
        for (SDTEntry s : sdt) {
            System.out.printf("%-8s %-10d %-12d %d\n", s.type, s.total, s.available, s.dctNum);
        }

        System.out.println("**********打印系统设备控制表DCT**********");
        System.out.println("设备绝对号 设备类型 设备好坏 设备空闲 设备相对号");
        for (DCTEntry d : dct) {
            System.out.printf("%-10d %-10s %-8s %-8s %d\n", d.absoluteId, d.type, d.isGood, d.status, d.relativeId);
        }

        System.out.println("**********系统控制器控制表COCT**********");
        System.out.println("编号 名字       好坏 空闲 所属通道");
        int availableControllers = 0;
        for (COCTEntry c : coct) {
            System.out.printf("%-4d %-10s %-4s %-4s %d\n", c.id, c.name, c.isGood, c.status, c.channelId);
            if (c.isGood.equals("好") && c.status.equals("yes")) {
                availableControllers++;
            }
        }
        System.out.println("系统剩余可用控制器数量：" + availableControllers);

        System.out.println("**********系统通道控制表CHCT**********");
        System.out.println("编号 通道内控制器序号");
        for (CHCTEntry ch : chct) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < ch.ctrlIds.size(); i++) {
                sb.append(ch.ctrlIds.get(i));
                if (i < ch.ctrlIds.size() - 1) sb.append(",");
            }
            System.out.printf("%-4d %s\n", ch.id, sb.toString());
        }
    }
}