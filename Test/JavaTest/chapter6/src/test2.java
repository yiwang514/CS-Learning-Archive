class test2{

    public static void main(String[] args){

        SanJiao s1=SanJiao.creatSanJiao(2,3,4);

        s1.showInfo();

        System.out.println("面积为："+s1.heronArea());//海伦公式计算面积

        s1.resetEdges(1,1,3);//重设三边

        s1.resetEdges(2,2,3);

        s1.showInfo();

        SanJiao s2=SanJiao.creatSanJiao(5,6);

        s2.showInfo();

        System.out.println("面积为："+s2.area());//底*高计算面积



    }

}

class SanJiao {
    private double a, b, c, ha, hb, hc;
    private boolean hasThreeSides;
    private static int count = 0;
    private static int maxcount = 3;

    private SanJiao(double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;
        hasThreeSides = true;
        count++;
    }

    SanJiao(double base, double height) {
        this.a = base;
        this.ha = height;
        hasThreeSides = false;
        count++;
    }

    private static boolean validSide(double a, double b, double c) {
        return a > 0 && b > 0 && c > 0 && (a + b > c) && (a + c > b) && (b + c > a);
    }

    private static boolean validbase(double base, double height) {
        return base > 0 && height > 0;
    }

    public static SanJiao creatSanJiao(double a, double b, double c) {
        if (count >= maxcount) {
            System.out.println("无法创建更多三角形对象，最多只能创建" + maxcount + "个");
            return null;
        }
        if (validSide(a, b, c)) {
            return new SanJiao(a, b, c);
        } else {
            System.out.println("无法创建三角形，三边参数不正确！");
            return null;
        }
    }

    public static SanJiao creatSanJiao(double base, double height) {
        if (count >= maxcount) {
            System.out.println("无法创建更多三角形对象，最多只能创建" + maxcount + "个");
            return null;
        }
        if (validbase(base, height)) {
            return new SanJiao(base, height);
        } else {
            System.out.println("无法创建三角形，底和高参数不正确！");
            return null;
        }
    }

    public void resetEdges(double a, double b, double c) {
        if (validSide(a, b, c)) {
            this.a = a;
            this.b = b;
            this.c = c;
            hasThreeSides = true;
        } else {
            System.out.println("无法重新设置，三边参数不正确！");
        }
    }

    public void setHeight(char side, double height) {
        if (height <= 0) {
            System.out.println("高必须大于0");
            return;
        }
        switch (side) {
            case 'a':
                this.ha = height;
                break;
            case 'b':
                this.hb = height;
                break;
            case 'c':
                this.hc = height;
                break;
            default:
                System.out.println("无效的边标识，请使用'a', 'b'或'c'");
        }
    }

    public double heronArea() {
        if (!hasThreeSides || !validSide(a, b, c)) {
            System.out.println("无法使用海伦公式计算面积，三边信息不完整或不正确");
            return 0;
        }
        double p = (a + b + c) / 2;
        return Math.sqrt(p * (p - a) * (p - b) * (p - c));
    }

    public double area() {
        if (a > 0 && ha > 0) {
            return a * ha / 2;
        } else if (b > 0 && hb > 0) {
            return b * hb / 2;
        } else if (c > 0 && hc > 0) {
            return c * hc / 2;
        } else {
            System.out.println("无法计算面积，缺少足够的底和高信息");
            return 0;
        }
    }

    public void showInfo() {
        if (hasThreeSides) {
            System.out.println("本三角形三条边为：a=" + a + ",b=" + b + ",c=" + c);
        } else {
            System.out.println("本三角形三条边为：a=" + a + ",b=" + b + ",c=" + c);
            if (ha > 0) {
                System.out.println("该三角形的高ha为：" + ha);
            }
            if (hb > 0) {
                System.out.println("该三角形的高hb为：" + hb);
            }
            if (hc > 0) {
                System.out.println("该三角形的高hc为：" + hc);
            }
        }
    }
}