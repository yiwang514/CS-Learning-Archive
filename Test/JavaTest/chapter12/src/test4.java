class test4 {
    public static void main(String[] args) {
        Shelf shelf = new Shelf();
        shelf.onshelf(new book("《Java编程思想》",68.5));
        shelf.onshelf(new egoods("旧鼠标",25.0));
        shelf.onshelf(new book("《高数》",30.0));
        shelf.onshelf(new egoods("耳机",60.0));
        shelf.onshelf(new egoods("键盘",50.0));
        shelf.onshelf(new book("《Java》",59.0));
        shelf.show();
        shelf.offshelf();
        shelf.show();
    }
}

abstract class goods{
        String name;
        double price;
        String category;
        public goods(String name, double price, String category) {
            this.name = name;
            this.price = price;
            this.category = category;
        }
}

class book extends goods {
    public book(String name,double price) {
        super(name, price, "book");
    }
}

class egoods extends goods {
    public egoods(String name, double price) {
        super(name, price, "egoods");
    }
}

class Shelf{
    goods[] goods=new goods[5];
    int curnum=0;
    public void onshelf(goods g){

        if(curnum<goods.length){
            goods[curnum++]=g;
            System.out.println("上架成功！");
        }
        else {
            System.out.println("货架已满，上架失败");
        }
    }
    public void offshelf(){
        if(curnum>0){
            goods[--curnum]=null;
            System.out.println("下架成功！");
        }
        else {
            System.out.println("下架失败！");
        }
    }
    public void show(){
        System.out.println("当前货架：");
        double sum=0;
        for(int i=0;i<curnum;i++){
            System.out.println(" "+goods[i].name+" "+goods[i].category+" "+goods[i].price);
            sum+=goods[i].price;
        }
        System.out.println("货架商品总价为："+sum+"元");
    }
}
