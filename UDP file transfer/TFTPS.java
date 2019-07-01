import java.io.*;
import java.net.*;
import java.lang.*;
public class TFTPS
{
 public static void main(String args[]) throws Exception
 {
   DatagramSocket ds=new DatagramSocket(1500);
   byte s[]=new byte[1024];
   DatagramPacket dp=new DatagramPacket(s,1024);
   ds.receive(dp);   
   String data=new String(dp.getData(),0,dp.getLength());
   int count=0;
   System.out.println("ENTER THE FILE NAME TO TRANFER:" +data);
   FileInputStream fs=new FileInputStream (data);
   while(fs.available()!=0)
   {
   if(fs.available()<1024)
     count=fs.available();

 else
     count=1024;
   s=new byte[count];     
   fs.read(s);
dp=new DatagramPacket(s,s.length,InetAddress.getLocalHost(),1501);
 ds.send(dp);
}
fs.close();
s=new byte[3];
s="   ".getBytes();            
ds.send(new DatagramPacket(s,s.length,InetAddress.getLocalHost(),1501));
ds.close();
  }
}
