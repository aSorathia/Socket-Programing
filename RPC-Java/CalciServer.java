import java.rmi.*;
import java.io.*;
import java.rmi.server.*;
public class CalciServer
{
	public static void main(String args[])throws Exception
	{
			CalciImp li=new CalciImp();
			System.out.println("Server Started");
			Naming.rebind("CalciImplementation",li);
			System.out.println("Server Object Registered");
	}
}