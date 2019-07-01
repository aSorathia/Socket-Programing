import java.io.*;
import java.net.*;
import java.lang.*;
public class TFTPC
{
	public static void main(String args[]) throws Exception
	{
		DatagramSocket ds=new DatagramSocket(1501);
		BufferedReader input=new BufferedReader(new InputStreamReader(System.in));
		System.out.println("ENTER THE FILE NAME TO SAVE:");
		String file=input.readLine();
                             FileOutputStream fos=new FileOutputStream(file);
		System.out.println("ENTER THE FILE NAME TO TRANFER:");
		file=input.readLine();
		byte s[]=new byte[file.length()];
		s=file.getBytes();
                             String data=null;
		ds.send(new DatagramPacket(s,s.length,InetAddress.getLocalHost(),1500));
		while(true)	
		{
			s=new byte[1024];
	DatagramPacket dp=new DatagramPacket(s,1024);
			ds.receive(dp);
	data=new String(dp.getData(),0,dp.getLength());
			if(data.equals("   ")) // to break while loop after transfer
			break;
			fos.write(data.getBytes());
		}
		fos.close();
		ds.close();
	}
}
