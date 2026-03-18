public class App2 {
    public static void main(String[] args) {
        Cat c = new Cat();
        Dog d = new Dog();

        Qiwei[] qA = {
                new FishQiwei(),
                new TigerQiwei(),
                new MasterQiwei(),
                new BoneQiwei()
        };

        System.out.println("猫闻这组气味，结果为：");
        for (Qiwei q : qA) {
            c.smell(q);
        }

        System.out.println("\n狗闻这组气味，结果为：");
        for (Qiwei q : qA) {
            d.smell(q);
        }
        System.out.println("\n重载方法");
        c.smell("鱼");
        d.smell("骨头");
    }
}
class Qiwei{}
class FishQiwei extends Qiwei {}
class TigerQiwei extends Qiwei {}
class MasterQiwei extends Qiwei {}
class BoneQiwei extends Qiwei {}

class Animal{
    public void smell(Qiwei q){
        System.out.println("动物闻到某种气味");
    }
}

class Cat extends Animal{
    @Override
    public void smell(Qiwei q) {
        if (q instanceof FishQiwei) {
            System.out.println("猫：是鱼的味道！");
        } else if (q instanceof TigerQiwei) {
            System.out.println("猫：老虎的气味");
        } else if (q instanceof MasterQiwei) {
            System.out.println("猫：是铲屎官的味道");
        } else if (q instanceof BoneQiwei) {
            System.out.println("猫：骨头的气味");
        } else {
            super.smell(q); // 调用父类方法
        }
    }
    public void smell(String smellType) {
        switch(smellType) {
            case "鱼":
                System.out.println("猫：是鱼的味道！");
                break;
            case "老虎":
                System.out.println("猫：老虎的气味");
                break;
            case "骨头":
                System.out.println("猫：骨头的气味");
                break;
            case "主人":
                System.out.println("猫：是铲屎官的味道");
                break;
            default:
                System.out.println("猫：未知气味");
        }
    }
}
class Dog extends Animal {

    @Override
    public void smell(Qiwei q) {
        if (q instanceof FishQiwei) {
            System.out.println("狗：鱼！");
        } else if (q instanceof TigerQiwei) {
            System.out.println("狗：老虎的气味");
        } else if (q instanceof MasterQiwei) {
            System.out.println("狗：主人的味道");
        } else if (q instanceof BoneQiwei) {
            System.out.println("狗：骨头！");
        } else {
            super.smell(q);
        }
    }


    public void smell(String smellType) {
        switch(smellType) {
            case "鱼":
                System.out.println("狗：鱼！");
                break;
            case "老虎":
                System.out.println("狗：老虎的气味");
                break;
            case "骨头":
                System.out.println("狗：骨头！");
                break;
            case "主人":
                System.out.println("狗：主人的味道");
                break;
            default:
                System.out.println("狗：未知气味");
        }
    }
}