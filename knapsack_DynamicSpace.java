import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class knapsackDynamicSpace {
	
	static int knapsack(int Maxweight, int weight[],int value[], int number)
	{
		if(number <=0 || Maxweight <=0 )
		{
			return 0;
		}
		else
		{
		int i, j;
		int ans[][] = new int[2][Maxweight + 1];
		for (i = 1; i <= number; i++)
		{
			for (j = 1; j <= Maxweight; j++)
			{
				if (weight[i - 1] <= j)
					{int m = value[i - 1]+ ans[(i - 1)%2][j - weight[i - 1]];
					int n = ans[(i - 1)%2][j];
					if(m>n)
						ans[i%2][j] = m;
					else
						ans[i%2][j] = n;}
				else
					ans[i%2][j] = ans[(i - 1)%2][j];
			}
		}
		return ans[number%2][Maxweight];}
	}

	public static void main(String args[]) throws FileNotFoundException
	{
		List<Integer> l1 = new ArrayList<Integer>();
		List<Integer> l2 = new ArrayList<Integer>();
		Scanner s = new Scanner(new FileReader("/Users/sirawitwattano/Desktop/DataSet/Set3.txt"));
		while (s.hasNext()) {
			l1.add(s.nextInt());
			l2.add(s.nextInt());
		}
		s.close();
		int[] value = new int[l1.size()];
		int[] weight = new int[l2.size()];
		for (int i = 0; i < l1.size(); i++)
			{value[i] = l1.get(i);
			weight[i] = l2.get(i);}
		int Maxweight = weight[0];
		int number = value[0];
		long start,stop;
		double time;
		System.out.printf("Answer: ");
		start = System.nanoTime();
		System.out.println(knapsack(Maxweight, weight, value, number));
		stop = System.nanoTime();
    	time = (stop-start)/1E6;
    	System.out.printf("Runtime: %.3f milliseconds\n",time);
	}
}
