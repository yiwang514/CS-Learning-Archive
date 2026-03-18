import java.util.Scanner;

public class Gobang
{

    /*public static void main(String[] args)
    {
        System.out.println("┌────┬────┬────┬────┬────┬────┬────┬────┬────┐");
        System.out.println("│    │    │    │    │    │    │    │    │    │");
        System.out.println("├────┼────┼────┼────┼────┼────┼────┼────┼────┤");
        System.out.println("│    │    │    │    │    │    │    │    │    │");
        System.out.println("├────┼────┼────┼────┼────┼────┼────┼────┼────┤");
        System.out.println("│    │    │    │    │    │    │    │    │    │");
        System.out.println("├────┼────┼────┼────┼────┼────┼────┼────┼────┤");
        System.out.println("│    │    │    │    │    │    │    │    │    │");
        System.out.println("├────┼────┼────┼────┼────┼────┼────┼────┼────┤");
        System.out.println("│    │    │    │    │    │    │    │    │    │");
        System.out.println("├────┼────┼────┼────┼────┼────┼────┼────┼────┤");
        System.out.println("│    │    │    │    │    │    │    │    │    │");
        System.out.println("├────┼────┼────┼────┼────┼────┼────┼────┼────┤");
        System.out.println("│    │    │    │    │    │    │    │    │    │");
        System.out.println("├────┼────┼────┼────┼────┼────┼────┼────┼────┤");
        System.out.println("│    │    │    │    │    │    │    │    │    │");
        System.out.println("├────┼────┼────┼────┼────┼────┼────┼────┼────┤");
        System.out.println("│    │    │    │    │    │    │    │    │    │");
        System.out.println("└────┴────┴────┴────┴────┴────┴────┴────┴────┘");
    */

    public static char[][] chessboard = {
            {'┌','┬','┬','┬','┬','┬','┬','┬','┬','┐'},
            {'├','┼','┼','┼','┼','┼','┼','┼','┼','┤'},
            {'├','┼','┼','┼','┼','┼','┼','┼','┼','┤'},
            {'├','┼','┼','┼','┼','┼','┼','┼','┼','┤'},
            {'├','┼','┼','┼','┼','┼','┼','┼','┼','┤'},
            {'├','┼','┼','┼','┼','┼','┼','┼','┼','┤'},
            {'├','┼','┼','┼','┼','┼','┼','┼','┼','┤'},
            {'├','┼','┼','┼','┼','┼','┼','┼','┼','┤'},
            {'├','┼','┼','┼','┼','┼','┼','┼','┼','┤'},
            {'└','┴','┴','┴','┴','┴','┴','┴','┴','┘'}

    };

    public static String separater="────";
    public static char pieceB='○';
    public static char pieceA='●';
    public static int times = 0;

    public static void showChessboard(){
        System.out.println("    0    1    2    3    4    5    6    7    8    9");
        for(int i=0;i<chessboard.length;i++){
            System.out.print(i+"   ");
            for(int j=0;j<chessboard[i].length;j++) {
                if (j == chessboard[i].length - 1) {
                    System.out.print(chessboard[i][j]);
                } else {
                    System.out.print(chessboard[i][j] + separater);
                }
            }
            System.out.println();
            if(i<chessboard.length-1){
                System.out.println("    │    │    │    │    │    │    │    │    │    │");
            }
        }
    }


    public static  void main(String[] args)
    {
        showChessboard();
        int totalposion = chessboard.length*chessboard[0].length;
        Scanner sc = new Scanner(System.in);
        outer:
        while(times<totalposion){
            System.out.print(times%2==0?"请A玩家落子":"请B玩家落子");
            char currentPiece = (times%2==0)?pieceA:pieceB;
            int position;
            while(true){
                if(sc.hasNextInt()){
                    position = sc.nextInt();
                    if(position>=0&&position<totalposion){

                        int row =position/chessboard.length;//位置除以棋盘数组长度得行号
                        int col =position%chessboard[0].length;//位置除以棋盘数组总列数得列号
                        if(chessboard[row][col]==pieceA||chessboard[row][col]==pieceB){
                            System.out.println("无效落子，请重新输入");
                            continue;
                        }
                        else{
                            chessboard[row][col]=currentPiece;
                            break;
                        }

                    }
                    else
                        System.out.println("无效落子，请重新输入");
                }
                else {
                    System.out.println("无效落子，请重新输入");
                    sc.next();          //将sc里的内容取出，防止死循环
                }


            }
            //落子之后棋盘需要重新展示
            showChessboard();
            //落完子之后看是否获胜
            for(int i=0;i<chessboard.length;i++){
                for(int j=0;j<chessboard[i].length;j++){
                    boolean case1 = (j+4<chessboard[i].length)&&chessboard[i][j]==currentPiece
                            &&chessboard[i][j+1]==currentPiece
                            &&chessboard[i][j+2]==currentPiece
                            &&chessboard[i][j+3]==currentPiece
                            &&chessboard[i][j+4]==currentPiece;

                    boolean case2 = (i+4<chessboard.length)&&chessboard[i][j]==currentPiece
                            &&chessboard[i+1][j]==currentPiece
                            &&chessboard[i+2][j]==currentPiece
                            &&chessboard[i+3][j]==currentPiece
                            &&chessboard[i+4][j]==currentPiece;

                    boolean case3 = (i+4<chessboard.length)&&(j+4<chessboard[0].length)
                            &&chessboard[i][j]==currentPiece
                            &&chessboard[i+1][j+1]==currentPiece
                            &&chessboard[i+2][j+2]==currentPiece
                            &&chessboard[i+3][j+3]==currentPiece
                            &&chessboard[i+4][j+4]==currentPiece;

                    boolean case4 = (i>4)&&(j+4<chessboard[i].length)
                            &&chessboard[i][j]==currentPiece
                            &&chessboard[i-1][j+1]==currentPiece
                            &&chessboard[i-2][j+2]==currentPiece
                            &&chessboard[i-3][j+3]==currentPiece
                            &&chessboard[i-4][j+4]==currentPiece;
                    if(case1||case2||case3||case4){
                        System.out.println(times%2==0?"A玩家win":"B玩家win");
                        break outer;
                    }
                }
            }
            times++;
            if(times==totalposion){
                System.out.println("平局");
            }
        }
    }
}
