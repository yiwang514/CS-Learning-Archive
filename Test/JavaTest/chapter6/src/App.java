public class App{

    public static void main(String args[] ){

        Recognizer rcg=new Recognizer();

        Shape[] s={new Rectangle(2, 3),new Circle(10),new SanJiao1(2,3),new SanJiao1(2,3,4)};

        for(int i=0; i<s.length; i++)   rcg.showInfo(s[i]);

    }

}

class Recognizer{ //智能识别器类

    public void showInfo(Shape s){//输入出形状名，面积、周长和属性

        System.out.println("类型："+s.getType()+s);   }

}

class Shape{//形状类
        public String getType(){
            return "Shape";
        }
        public double getArea(){
            return 0;
        }
        public String getPerimeter(){
            return "无法计算";
        }
        public String toString(){
            return "面积为：" + getArea() + ",周长为：" + getPerimeter();
        }
    }

    class Rectangle extends Shape{ //矩形类

        private int height;
        private int width;
        public String getType() { return "矩形"; }
        public Rectangle(int height,int width){
            this.width=width;
            this.height=height;
        }
        public double getArea(){
            return width*height;
        }
        public String getPerimeter(){
            return String.valueOf((double)2*(width+height));

            }
        public String toString(){
            return super.toString() + ",属性为：高=" + height + " 宽=" + width;
        }
    }


    class Circle extends Shape{//圆形类
        private int radius;
        double PI=3.14;
        public String getType() { return "圆形"; }
        public Circle(int radius){
            this.radius=radius;
        }
        public double getArea(){
            return PI*radius*radius;
        }
        public  String getPerimeter(){
            return String.valueOf(2*PI*radius);
        }
        public String toString(){
            return super.toString() + ",属性为：半径=" + radius;
        }
    }

    class SanJiao1 extends Shape {//三角形
        private double a;
        private double b;
        private double c;
        private double ha;
        private boolean hasThreeSides;
        public String getType() { return "三角形"; }
        public SanJiao1(double a, double b, double c) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.hasThreeSides = true;
        }

        public SanJiao1(double a, double ha) {
            this.a = a;
            this.ha = ha;
            this.hasThreeSides = false;
        }

        public double getArea() {
            if (hasThreeSides) {
                double p = (a + b + c) / 2;
                return Math.sqrt(p * (p - a) * (p - b) * (p - c));
            } else {
                return a * ha / 2;
            }
        }

        public String getPerimeter() {
            if (hasThreeSides) {
                return String.valueOf(a + b + c);
            } else {
                return "只有底和高无法计算周长";
            }
        }

        public String toString() {
            if (hasThreeSides) {
                return super.toString() + ",属性为：三边为：a=" + a + ",b=" + b + ",c=" + c;
            } else {
                return super.toString() + "。属性为：底a=" + a + ",高ha=" + ha;
            }
        }
    }
