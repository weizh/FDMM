package edu.cmu.lti.weizh.models;

import gnu.trove.map.TObjectIntMap;

public class Global {

	public static String[] etypes = { "PERSON", "NORP", "FAC", "ORG", "LOC", "PRODUCT", "EVENT", "WORK_OF_ART", "LAW",
			"LANGUAGE", "DATE", "TIME", "PERCENT", "MONEY", "QUANTITY", "ORDINAL", "CARDINAL" };

	public static String[] pos = { "CC", "CD", "DT", "EX", "FW", "IN", "JJ", "JJR", "JJS", "LS", "MD", "NN", "NNS", "NNP",
			"NNPS", "PDT", "POS", "PRP", "PRP$", "RB", "RBR", "RBS", "RP", "SYM", "TO", "UH", "VB", "VBD", "VBG", "VBN", "VBP",
			"VBZ", "WDT", "WP", "WP$", "WRB" };
	
	public static String[] featureType = {"ptok","ctok","ntok","ppos","cpos","npos","pcap","ccap","ncap"};
	
	public static TObjectIntMap<String> E2IDMap;
	public static TObjectIntMap<String> POS2IDMap;
	public static TObjectIntMap<String> feature2IDMap;
	static {
		int id = 0;
		for (String s : etypes)
			E2IDMap.put(s, id++);
		id = 0 ;
		for (String s: pos)
			POS2IDMap.put(s, id);
		
		id = 0 ;
		for (String s: featureType)
			feature2IDMap.put(s, id);
	}

}
