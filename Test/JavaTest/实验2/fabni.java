class App{
   public static void main(String[] args)
   {
	Fabni f=new Fabni();
	f.Fab10();
   }
}

class Fabni{
      void Fab10()
      {
	int[] f=new int[10];
	f[0]=1;
	f[1]=1;
	for(int i=2;i<f.length;i++){
		f[i]=f[i-1]+f[i-2];
	}
	for(int i=0;i<f.length;i++){
		System.out.print("f["+i+"]="+f[i]+" ");
		if((i+1)%5==0)
			System.out.println();
	}
      }
}