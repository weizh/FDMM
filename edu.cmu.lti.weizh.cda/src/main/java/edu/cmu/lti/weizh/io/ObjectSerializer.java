package edu.cmu.lti.weizh.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ObjectSerializer {

	public void serialize(Object o,String destination){
		try
	      {
	         FileOutputStream fileOut =
	         new FileOutputStream(destination);
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(o);
	         out.close();
	         fileOut.close();
	         System.out.printf("Serialized data is saved in "+destination);
	      }catch(IOException i)
	      {
	          i.printStackTrace();
	      }
	}
}
