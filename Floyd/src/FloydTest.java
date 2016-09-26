import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;



/**
 * 弗洛伊德算法
 * @author 
 *
 */
public class FloydTest {

	private static int MAX = Integer.MAX_VALUE; //表示不可达
	
	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		int n = 4;
		
		int[][] cost = intputGragh("D:\\testFloyd.txt" , 4);
		
//	类似	int[][] cost = new int[][]{{0,20,50,30,MAX,MAX,MAX},
//							      {MAX,0,25,MAX,MAX,70,MAX},
//							      {MAX,MAX,0,40,25,50,MAX},
//							      {MAX,MAX,MAX,0,55,MAX,MAX},
//							      {MAX,MAX,MAX,MAX,0,10,70},
//							      {MAX,MAX,MAX,MAX,MAX,0,50},
//							      {MAX,MAX,MAX,MAX,MAX,MAX,0}};
		
		floyd(n, cost);
	}

	 public static int[][] intputGragh(String path , int num) throws FileNotFoundException{
	        int[][] G = new int[num][num];
	        for(int i=0;i<num;i++){
	            for(int j=0;j<num;j++){
	                if(i==j) G[i][j]=0;
	                else G[i][j]=MAX;
	            }
	        }
	        
	        Scanner in = new Scanner(new FileInputStream(path));
	        while (in.hasNext()) {
	            int i = in.nextInt();
	            int j = in.nextInt();
	            int weight = in.nextInt();
	            G[i][j] = weight;
	        } 
	        return G;
	    }
	
	/**
	 * 弗洛伊德求解过程
	 * @param n
	 * @param cost
	 */
	public static void floyd(int n, int[][] cost) {
		int[][] path = new int[n][n]; //path用来存放最后的结果
		//init
		for(int p=0;p<n;p++) {
			for(int q=0;q<n;q++)
				path[p][q] = cost[p][q];
		}
		//floyd O(n^3)
		for(int k=0;k<n;k++)
			for(int i=0;i<n;i++) 
				for(int j=0;j<n;j++) {
					if(path[i][k]!=MAX && path[k][j]!=MAX) { //如果有MAX最大值，那么相加后，会变成负数，导致结果出错
						if(path[i][k]+path[k][j] < path[i][j])
							path[i][j] = path[i][k]+path[k][j];
					}
				}
		//print
		for(int m=0;m<n;m++) {
			for(int l=0;l<n;l++)
				System.out.print(path[m][l] + " ");
			System.out.println();
		}
	}
}
