package edu.cmu.lti.weizh.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Dictionary implements Set<String>{

	private HashSet<String> dict;
	public Dictionary(String file){
		this.dict = new HashSet<String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String line;
		try {
			while((line=br.readLine())!=null){
				dict.add(line.trim().toLowerCase());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void main(String argv[]){
		
	}


	@Override
	public boolean add(String e) {
		// TODO Auto-generated method stub
		dict.add(e);
		return false;
	}


	@Override
	public boolean addAll(Collection<? extends String> c) {
		// TODO Auto-generated method stub
		dict.addAll(c);
		return false;
	}


	@Override
	public void clear() {
		// TODO Auto-generated method stub
		dict=new HashSet<String>();
	}


	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return dict.contains(o);
	}


	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return dict.containsAll(c);
	}


	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return dict.isEmpty();
	}


	@Override
	public Iterator<String> iterator() {
		// TODO Auto-generated method stub
		return dict.iterator();
	}


	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return dict.remove(o);
	}


	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return dict.removeAll(c);
	}


	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return dict.retainAll(c);
	}


	@Override
	public int size() {
		// TODO Auto-generated method stub
		return dict.size();
	}


	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return dict.toArray();
	}


	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return dict.toArray(a);
	}
}
