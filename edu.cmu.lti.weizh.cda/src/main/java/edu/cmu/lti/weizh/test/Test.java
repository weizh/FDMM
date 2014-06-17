package edu.cmu.lti.weizh.test;

import gnu.trove.map.hash.TIntIntHashMap;

public class Test {

	public static void main(String argvp[]){
		TIntIntHashMap a = new TIntIntHashMap();
		a.adjustOrPutValue(4, 1, 2);
		a.adjustOrPutValue(4, 1, 2);
		a.adjustOrPutValue(4, 1, 2);
		a.adjustOrPutValue(4, 1, 2);
		
		System.out.println(a.contains(3));
		
		System.out.println("AAAAAaaaannnAAnnnAnnn".replaceAll("A[A]+", "A+"));
		System.out.println((int)1+(double)1.1043);
		
		String word = "Asdfs";int i = 1;
		System.out.println( word.substring(word.length()>i?(word.length()-i):0));
	}
}
