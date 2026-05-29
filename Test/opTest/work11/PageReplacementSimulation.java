import java.util.*;

public class PageReplacementSimulation {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("请输入物理块的块数：");
        int n = sc.nextInt();

        while (true) {
            System.out.println("**********请求分页式存储管理**********");
            System.out.println("* 1. FIFO分配          *");
            System.out.println("* 2. LRU(LFU)分配      *");
            System.out.println("* 0. 退出              *");
            System.out.print("          请输入选项：");
            int choice = sc.nextInt();

            if (choice == 0) {
                break;
            }

            System.out.print("请输入作业名：");
            String jobName = sc.next();
            System.out.print("请输入作业页面长度：");
            int m = sc.nextInt();
            System.out.println("请输入作业页面顺序：");
            int[] ss = new int[m];
            for (int i = 0; i < m; i++) {
                ss[i] = sc.nextInt();
            }

            // 初始化记录数组
            int[][] ans = new int[n][m];
            int[] vis = new int[m];
            int[] outPage = new int[m];
            for (int i = 0; i < n; i++) {
                Arrays.fill(ans[i], -1);
            }
            Arrays.fill(outPage, -1);

            if (choice == 1) {
                // FIFO 算法逻辑
                int[] blocks = new int[n];
                Arrays.fill(blocks, -1);
                int replaceIdx = 0;

                for (int j = 0; j < m; j++) {
                    int page = ss[j];
                    boolean hit = false;
                    for (int i = 0; i < n; i++) {
                        if (blocks[i] == page) {
                            hit = true;
                            break;
                        }
                    }

                    if (hit) {
                        vis[j] = 0;
                    } else {
                        vis[j] = 1;
                        int emptyIdx = -1;
                        for (int i = 0; i < n; i++) {
                            if (blocks[i] == -1) {
                                emptyIdx = i;
                                break;
                            }
                        }

                        if (emptyIdx != -1) {
                            blocks[emptyIdx] = page;
                        } else {
                            outPage[j] = blocks[replaceIdx];
                            blocks[replaceIdx] = page;
                            replaceIdx = (replaceIdx + 1) % n;
                        }
                    }
                    for (int i = 0; i < n; i++) {
                        ans[i][j] = blocks[i];
                    }
                }
                printResult("FIFO", jobName, n, m, ans, vis, outPage);

            } else if (choice == 2) {
                // LRU 算法逻辑
                int[] blocks = new int[n];
                Arrays.fill(blocks, -1);
                int[] lastAccess = new int[n];
                Arrays.fill(lastAccess, -1);

                for (int j = 0; j < m; j++) {
                    int page = ss[j];
                    boolean hit = false;
                    int hitIdx = -1;
                    for (int i = 0; i < n; i++) {
                        if (blocks[i] == page) {
                            hit = true;
                            hitIdx = i;
                            break;
                        }
                    }

                    if (hit) {
                        vis[j] = 0;
                        lastAccess[hitIdx] = j;
                    } else {
                        vis[j] = 1;
                        int emptyIdx = -1;
                        for (int i = 0; i < n; i++) {
                            if (blocks[i] == -1) {
                                emptyIdx = i;
                                break;
                            }
                        }

                        if (emptyIdx != -1) {
                            blocks[emptyIdx] = page;
                            lastAccess[emptyIdx] = j;
                        } else {
                            int lruIdx = 0;
                            int minTime = lastAccess[0];
                            for (int i = 1; i < n; i++) {
                                if (lastAccess[i] < minTime) {
                                    minTime = lastAccess[i];
                                    lruIdx = i;
                                }
                            }
                            outPage[j] = blocks[lruIdx];
                            blocks[lruIdx] = page;
                            lastAccess[lruIdx] = j;
                        }
                    }
                    for (int i = 0; i < n; i++) {
                        ans[i][j] = blocks[i];
                    }
                }
                printResult("LRU", jobName, n, m, ans, vis, outPage);
            }
        }
        sc.close();
    }

    // 严格按照3字符间距复刻控制台的对齐效果
    private static void printResult(String algo, String jobName, int n, int m, int[][] ans, int[] vis, int[] outPage) {
        System.out.println("**********打印作业" + algo + "调度进入主存页的过程**********");
        System.out.println("作业名：" + jobName);
        System.out.println("作业调度过程：");

        // 打印列号
        System.out.print("   ");
        for (int j = 0; j < m; j++) {
            System.out.printf("%-3d", j);
        }
        System.out.println();

        // 打印主存物理块各时刻状态
        for (int i = 0; i < n; i++) {
            System.out.printf("%-3d", i);
            for (int j = 0; j < m; j++) {
                if (ans[i][j] == -1) {
                    System.out.print("   ");
                } else {
                    System.out.printf("%-3d", ans[i][j]);
                }
            }
            System.out.println();
        }

        // 打印缺页标志行
        System.out.print("   ");
        for (int j = 0; j < m; j++) {
            if (vis[j] == 1) {
                System.out.print("+  ");
            } else {
                System.out.print("   ");
            }
        }
        System.out.println();

        // 打印置换出的页面号行
        System.out.print("   ");
        for (int j = 0; j < m; j++) {
            if (outPage[j] != -1) {
                System.out.printf("%-3d", outPage[j]);
            } else {
                System.out.print("   ");
            }
        }
        System.out.println();

        // 计算缺页中断率
        int faultCount = 0;
        for (int v : vis) {
            if (v == 1) faultCount++;
        }
        double faultRate = (double) faultCount / m * 100;
        System.out.printf("缺页中断率为：%.2f\n", faultRate);
    }
}