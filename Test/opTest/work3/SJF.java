import java.util.*;

class Time{
    int hour;
    int min;
    
    public Time(int hour, int min){
        this.hour = hour;
        this.min = min;
    }

    public int toMinutes(){
        return hour * 60 + min;
    }

    public static Time fromMinutes(int totalMinutes){
        int hour = totalMinutes / 60;
        int min = totalMinutes % 60;
        return new Time(hour, min);
    }

    public String toString(){
        return String.format("%02d:%02d", hour, min);
    }
}

class  Process {
    int id;
    String name;

    Time arrivalTime;
    int zx;

    Time startTime;
    Time finishTime;

    int zz;
    double zzxs;

    boolean isCompleted;

    public Process(int id, String name, Time arrivalTime, int zx){
        this.id = id;
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.zx = zx;
        this.isCompleted = false;
    }
}

public class SJF {
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        while(true){
            System.out.print("请输入操作（1：开始进程调度；-1：结束进程） ");
            int op = sc.nextInt();
            if (op == -1) break;
            if (op == 1) {
                System.out.print("请输入进程数： ");
                int n = sc.nextInt();
                List<Process> processes = new ArrayList<>();
                List<Process> finishedProcesses = new ArrayList<>();
                System.out.println("id号\t名字\t到达时间\t执行时间");
                for(int i = 0;i < n; i++){
                    int id = sc.nextInt();
                    String name = sc.next();
                    String timeString = sc.next();
                    String[] timeParts = timeString.split(":");
                    Time arrivalTime = new Time(Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]));
                    int zx = sc.nextInt();
                    processes.add(new Process(id, name, arrivalTime, zx));
                 }
                int currentTime = Integer.MAX_VALUE;
                int completedProcesses = 0;
                for(Process p : processes){
                    if(p.arrivalTime.toMinutes() < currentTime){
                        currentTime = p.arrivalTime.toMinutes();
                    }
                }
                while(completedProcesses < n){
                    Process nextProcess = null;
                    int minZx = Integer.MAX_VALUE;
                    for(Process p : processes){
                        if(!p.isCompleted && p.arrivalTime.toMinutes() <= currentTime && p.zx < minZx){
                            minZx = p.zx;
                            nextProcess = p;
                        }
                    }
                    if(nextProcess != null){
                        nextProcess.startTime = Time.fromMinutes(currentTime);
                        currentTime += nextProcess.zx;
                        nextProcess.finishTime = Time.fromMinutes(currentTime);
                        nextProcess.zz = nextProcess.finishTime.toMinutes() - nextProcess.arrivalTime.toMinutes();
                        nextProcess.zzxs = (double)nextProcess.zz / nextProcess.zx;
                        nextProcess.isCompleted = true;
                        finishedProcesses.add(nextProcess);
                        completedProcesses++;
                    }else{
                        // 如果当前没进程，跳到下一个最近的到达时间
                        int nextArrivalTime = Integer.MAX_VALUE;
                        for(Process p : processes){
                            if(!p.isCompleted && p.arrivalTime.toMinutes() < nextArrivalTime){
                                nextArrivalTime = p.arrivalTime.toMinutes();
                            }
                        }
                        currentTime = nextArrivalTime;
                    }
                }
                System.out.println("模拟短进程优先调度过程输出结果");
                System.out.println("id号\t名字\t到达时间\t执行时间（分钟）\t开始时间\t完成时间\t周转时间（分钟）\t带权周转系数");
                
                float sumZz = 0;
                float sumZzxs = 0;
                for (Process p : finishedProcesses) {
                    System.out.printf("%d\t%s\t%s\t%d\t%s\t%s\t%d\t%.2f\n",
                            p.id, p.name, p.arrivalTime.toString(), p.zx, p.startTime.toString(), 
                            p.finishTime.toString(), p.zz, p.zzxs);
                    sumZz += p.zz;
                    sumZzxs += p.zzxs;
                }

                System.out.printf("\n系统平均周转时间为：\t\t\t\t\t\t%.2f\n", sumZz / n);
                System.out.printf("系统平均带权周转系数为：\t\t\t\t\t\t%.2f\n", sumZzxs / n);
            }
        }
        sc.close();
    }
}
