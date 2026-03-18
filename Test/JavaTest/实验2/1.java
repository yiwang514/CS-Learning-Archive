import java.util.Scanner;

class delete{
	public static void main(String arg[] ){
		int[] a={31,12,5,10,19,4,33};
		int[] b=a;
		Scanner sc=new Scanner(System.in);
		int x=sc.nextInt();
		int cout=0;
		for(int i=0;i<a.length;i++){
			if(a[i]==x)
				cout++;
		}
		if(cout==0){
			System.out.println("数组中没有值为"+67+"的元素,删除失败");
			System.out.print("数组中的数据:");
			for(int i=0;i<a.length;i++){
				if(i!=a.length-1)
					System.out.print(a[i]+" ");
				else
					System.out.print(a[i]);
			}
		}
		else{
			a=new int[a.length-cout];
			System.out.println("已将数组中值为"+19+"的元素删除");
			System.out.print("数组中的数据:");
			for(int i=0;i<a.length;i++){
				for(int j=i;j<b.length;j++){
					if(b[j]!=x){
						a[i]=b[j];
						if(i!=a.length-1)
							System.out.print(a[i]+" ");
						else
							System.out.print(a[i]);
						i++;
					}
				}		
			}
		}		
	}
}