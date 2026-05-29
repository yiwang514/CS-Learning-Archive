import java.util.*;

class Time {
    int hour;
    int min;

    public Time(int hour, int min) {
        this.hour = hour;
        this.min = min;
    }

    public int toMinutes() {
        return hour * 60 + min;
    }

    public static Time fromMinutes(int totalMinutes) {
        int hour = totalMinutes / 60;
        int min = totalMinutes % 60;
        return new Time(hour, min);
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d", hour, min);
    }
}

class Process {
    int id;
    String name;

    Time arrivalTime;     // 到达时间
    int zx;               // 总执行时间

    Time startTime;       // 当前轮开始执行时间
    Time finishTime;      // 最终完成时间

    int finishedTime;     // 已完成时间
    int remainingTime;    // 剩余完成时间

    int zz;               // 周转时间
    double zzxs;          // 带权周转时间

    boolean isCompleted;

    public Process(int id, String name, Time arrivalTime, int zx) {
        this.id = id;
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.zx = zx;
        this.finishedTime = 0;
        this.remainingTime = zx;
        this.isCompleted = false;
        this.startTime = new Time(0, 0);
    }
}

public class RoundScheduling {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print("请输入操作:(1:开始进程;-1:结束进程):");
            int op = sc.nextInt();

            if (op == -1) {
                break;
            }

            if (op == 1) {
                System.out.println("请输入进程数:");
                int n = sc.nextInt();

                List<Process> processes = new ArrayList<>();
                List<Process> readyQueue = new ArrayList<>();
                List<Process> finishedProcesses = new ArrayList<>();

                System.out.println("请输入时间片时间:");
                int timeSlice = sc.nextInt();

                System.out.println("请输入进程的参数:");
                System.out.println("id号  名字  到达时间  执行时间(分钟):");

                for (int i = 0; i < n; i++) {
                    int id = sc.nextInt();
                    String name = sc.next();
                    String timeString = sc.next();
                    String[] timeParts = timeString.split(":");
                    Time arrivalTime = new Time(Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]));
                    int zx = sc.nextInt();

                    processes.add(new Process(id, name, arrivalTime, zx));
                }

                // 按到达时间排序，保证进入就绪队列顺序符合 FCFS
                processes.sort(Comparator.comparingInt(p -> p.arrivalTime.toMinutes()));

                int currentTime = processes.get(0).arrivalTime.toMinutes();
                int completedCount = 0;
                int round = 1;
                int index = 0; // 指向尚未加入就绪队列的进程

                while (completedCount < n) {
                    // 将当前时间之前到达的进程加入就绪队列
                    while (index < n && processes.get(index).arrivalTime.toMinutes() <= currentTime) {
                        readyQueue.add(processes.get(index));
                        index++;
                    }

                    // 若就绪队列为空，则时间推进到下一到达进程
                    if (readyQueue.isEmpty()) {
                        if (index < n) {
                            currentTime = processes.get(index).arrivalTime.toMinutes();
                            continue;
                        }
                    }

                    // 取队首进程执行
                    Process current = readyQueue.remove(0);
                    current.startTime = Time.fromMinutes(currentTime);

                    int runTime = Math.min(timeSlice, current.remainingTime);
                    current.finishedTime += runTime;
                    current.remainingTime -= runTime;
                    currentTime += runTime;

                    // 本轮执行结束后，再把新到达的进程加入就绪队列
                    while (index < n && processes.get(index).arrivalTime.toMinutes() <= currentTime) {
                        readyQueue.add(processes.get(index));
                        index++;
                    }

                    // 若当前进程未完成，插入就绪队列队尾
                    if (current.remainingTime > 0) {
                        readyQueue.add(current);
                    } else {
                        current.isCompleted = true;
                        current.finishTime = Time.fromMinutes(currentTime);
                        current.zz = current.finishTime.toMinutes() - current.arrivalTime.toMinutes();
                        current.zzxs = (double) current.zz / current.zx;
                        finishedProcesses.add(current);
                        completedCount++;
                    }

                    // 输出本轮结果：当前执行进程 + 当前就绪队列
                    System.out.println("第" + round + "轮执行和就绪队列的结果:");
                    System.out.println("ID号 名字 到达时间 总执行时间(分钟) 当前开始时间 已完成时间(分钟) 剩余完成时间(分钟)");

                    // 先输出本轮执行的进程
                    System.out.printf("%-4d %-4s %-8s %-14d %-10s %-14d %-14d\n",
                            current.id,
                            current.name,
                            current.arrivalTime.toString(),
                            current.zx,
                            current.startTime.toString(),
                            current.finishedTime,
                            current.remainingTime);

                    // 再输出当前就绪队列中的进程
                    for (Process p : readyQueue) {
                        System.out.printf("%-4d %-4s %-8s %-14d %-10s %-14d %-14d\n",
                                p.id,
                                p.name,
                                p.arrivalTime.toString(),
                                p.zx,
                                "00:00",
                                p.finishedTime,
                                p.remainingTime);
                    }

                    round++;
                }

                // 最终结果输出
                System.out.println("------------------------------------------------------------------");
                System.out.println("所有进程执行完成后的结果:");
                System.out.println("ID号 名字 到达时间 总执行时间 完成时间 周转时间 带权周转时间");

                double sumZz = 0;
                double sumZzxs = 0;

                for (Process p : finishedProcesses) {
                    System.out.printf("%-4d %-4s %-8s %-10d %-8s %-8d %-10.2f\n",
                            p.id,
                            p.name,
                            p.arrivalTime.toString(),
                            p.zx,
                            p.finishTime.toString(),
                            p.zz,
                            p.zzxs);

                    sumZz += p.zz;
                    sumZzxs += p.zzxs;
                }

                System.out.printf("平均周转时间: %.2f\n", sumZz / n);
                System.out.printf("平均带权周转时间: %.2f\n", sumZzxs / n);
            }
        }

        System.out.println("操作结束！");
        sc.close();
    }
}