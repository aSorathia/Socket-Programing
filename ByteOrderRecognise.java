import java.nio.*;
class ByteOrderRecognise
{    
	public static void main(String argv[]) throws Exception       
	{          
		System.out.println("The Host Byte Order is "+ByteOrder.nativeOrder());          
	} 
}