class yanghui{
	public static void main(String[] s) {
		int[][] a=new int[5][];
		for(int i=0;i<5;i++){
			a[i]=new int[i+1];
			for(int j=0;j<=i;j++){
				if(j==0 || j==i)
					a[i][j]=1;	
				else
					a[i][j]=a[i-1][j]+a[i-1][j-1];
				System.out.print(a[i][j]+" ");
			}
		System.out.println();
		}
	}
}