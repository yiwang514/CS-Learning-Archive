class date {
    int year;
    int month;
    int day;
    int[] dim={31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    public date(int y,int m,int d){
        year=y;
        month=m;
        day=d;
    }
    public date(){
        year=1971;
        month=9;
        day=1;
    }

    void reset(int y,int m,int d){
        year=y;
        month=m;
        day=d;
    }

    public String toString(){
        return "日期为："+year+"年"+month+"月"+day+"日";
    }

    public String display() {
        return "日期为："+year + "年" + month + "月" + day + "日";
    }

    boolean leapyear(){
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    int daysinmonth(){
        if(month==2&&leapyear())
            return dim[1]=29;
        return dim[month-1];
    }

    public date plusDays(int days){
        int newyear=year;
        int newmonth=month;
        int newday=day+days;


        if(newday>daysinmonth()){
            newmonth+=newday/30;
            newday=newday-daysinmonth();
        }
        if(newmonth>12){
            newyear+=newmonth/12;
            newmonth=newmonth%12;
        }
        return new date(newyear,newmonth,newday);
    }
}

class App2{
    public static void main(String[] args){
        date d1=new date();
        date d2=new date(2025,8,17);
        System.out.println(d1.toString());
        d1.reset(2000,9,1);
        System.out.println("重新设置后的"+d1.display());
        System.out.println(d2.toString());
        date dateafter13=d2.plusDays(13);
        System.out.println("13天后的"+dateafter13.display());
        date dateafter15=d2.plusDays(15);
        System.out.println("15天后的"+dateafter15.display());
    }
}
