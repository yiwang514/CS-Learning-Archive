import java.util.*;

class Time{
    int hour;
    int min;

    public Time(int h,int m){
        this.hour = h;
        this.min = m;
    }

    public int toMinues(){
        return hour*60 + min;
    }

    public static Time formMinutes(int totalMinutes){
        return new Time(totalMinutes/60,totalMinutes%60);
    }

    public String toString(){
        return String.format("%02d:%02d",hour,min);  //%02d就是补0来凑够2位整数
    }
}

class Process{
    int id;
    String name;
    Time arrive;
    int zx;
    Time start;
    Time finish;
    int zz;
    double zzxs;

    public Process(int id,String name,Time arrive,int zx){
        this.id = id;
        this.name = name;
        this.arrive = arrive;
        this.zx = zx;
    }
}

public class FCFS {

    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        while(true){
            System.out.print("请输入操作 (1:开始进程调度; 0:结束进程): ");
            int op = sc.nextInt();
            if(op == 0) break;

            System.out.print("请输入进程数量: ");
            int n = sc.nextInt();

            List<Process> processes = new ArrayList<>();
            System.out.println("请输入进程的参数：id号 名字 到达时间(时:分) 执行时间(分钟):");
            for(int i=0;i<n;i++){
                int id = sc.nextInt();
                String name = sc.next();
                String timeStr = sc.next();
                String[] parts = timeStr.split(":");
                Time arriveTime = new Time(Integer.parseInt(parts[0]),Integer.parseInt(parts[1]));
                int zx = sc.nextInt();
                processes.add(new Process(id, name,arriveTime,zx));
            }

            processes.sort((p1,p2)->p1.arrive.toMinues() - p2.arrive.toMinues());

            int currentTime = 0;
            double totalZZ = 0;
            double totalZZXS = 0;

            for(int i = 0;i<processes.size();i++){
                Process p = processes.get(i);
                int arriveMins = p.arrive.toMinues();

                if(currentTime<arriveMins){
                    currentTime = arriveMins;
                }

                p.start = Time.formMinutes(currentTime);
                currentTime += p.zx;
                p.finish = Time.formMinutes(currentTime);

                p.zz = currentTime - arriveMins;
                p.zzxs = (double)p.zz/p.zx;

                totalZZ += p.zz;
                totalZZXS += p.zzxs;
            }
            System.out.println("\n模拟进程FCFS调度过程输出结果:");
            System.out.println("id号\t名字\t到达时间\t执行时间(分钟)\t开始时间\t完成时间\t周转时间(分钟)\t带权周转系数");
            for (Process p : processes) {
                System.out.printf("%d\t%s\t%s\t\t%d(分钟)\t%s\t\t%s\t\t%d(分钟)\t%.2f\n",
                        p.id, p.name, p.arrive, p.zx, p.start, p.finish, p.zz, p.zzxs);
            }

            System.out.printf("\n系统平均周转时间为:\t\t\t\t\t\t\t%.2f\n", totalZZ / n);
            System.out.printf("系统平均带权周转系数为:\t\t\t\t\t\t%.2f\n\n", totalZZXS / n);
        }
        sc.close();
    }
}