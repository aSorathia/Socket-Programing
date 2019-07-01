import java.rmi.*;
import java.rmi.server.*;
import java.io.*;

class CalciClient
{
	public static void main(String args[])throws Exception
	{
		Calci obj=(Calci)Naming.lookup("rmi://localhost/CalciImplementation");
		System.out.println("Handle to Server Object Acquired");
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		int c=1;
		int n1,n2;
		float result=0.0f;
		while(c!=0)
		{
			result=0;
			System.out.println("\nPress 1:Add\n2:Subtract\n3:Divide\n0: Exit");
			c=Integer.parseInt(br.readLine()); 
			switch(c)
			{
				case 1: 
				{
					System.out.println("Enter n1 ");
					n1=Integer.parseInt(br.readLine());
					System.out.println("Enter n2 ");
					n2=Integer.parseInt(br.readLine());
					result=(float)obj.add(n1,n2);
					System.out.println("The Result is "+result);	
					break;
				}
				case 2:
				{
					System.out.println("Enter n1 ");
					n1=Integer.parseInt(br.readLine());
					System.out.println("Enter n2 ");
					n2=Integer.parseInt(br.readLine());
					result=(float)obj.sub(n1,n2);
					System.out.println("The Result is "+result);
					break;				
				}
				case 3: 
				{
					System.out.println("Enter n1 ");
					n1=Integer.parseInt(br.readLine());
					System.out.println("Enter n2 ");
					n2=Integer.parseInt(br.readLine());
					result=(float)obj.div(n1,n2);
					System.out.println("The Result is "+result);
					break;				
				}
			}
			
		}
	}
}