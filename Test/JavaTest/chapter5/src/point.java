class Point_ks {

    public static void main(String[] args){

        Point p1=new Point();

        System.out.println(p1);

        Point p2=new Point(2,3);

        System.out.println(p2);

        Point p3=new Point(p2);

        System.out.println(p3);

        System.out.println(p2.equals(p3));

        p2.change(3,-2);

        System.out.print("p2改变坐标后为:");

        System.out.println(p2);

        System.out.println(p2.equals(p3));

    }

}

class Point{
    private int x,y;
    Point(){
        x=0;
        y=0;
    }
    Point(int x,int y){
        this.x=x;
        this.y=y;
    }
    Point(Point p){
        this.x=p.x;
        this.y=p.y;
    }
    void change(int intervalX,int intervalY){
        x=x+intervalX;
        y=y+intervalY;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public void setX(int x){
        this.x=x;
    }
    public void setY(int y){
        this.y=y;
    }
    public String toString(){
        return "点的坐标为:(" + x + "," + y + ")";
    }
    public boolean equals(Point p){
        if(x==p.x&&y==p.y){
            return true;
        }else {
            return false;
        }
    }

}
