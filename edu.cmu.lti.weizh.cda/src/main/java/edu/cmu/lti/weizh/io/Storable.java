package edu.cmu.lti.weizh.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public abstract class Storable <T> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected abstract T self();
	
	public void store(String string) {
		try {
			FileOutputStream fileOut = new FileOutputStream(string);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(self());
			out.close();
			fileOut.close();
			System.out.println("Model serialized to " + string);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public T load(String string) {
		ObjectInputStream is = null;
		try {
			is = new ObjectInputStream(new FileInputStream(string));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		T t = null;
		try {
			t = (T) is.readObject();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return t;
	}
	
}
