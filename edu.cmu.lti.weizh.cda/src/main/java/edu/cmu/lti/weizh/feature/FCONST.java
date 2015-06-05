package edu.cmu.lti.weizh.feature;

/**
 * The feature headers and theta header types;
 * 
 * modifier to get the header names by position k. This is used in feature header construction.
 * 
 * @author wei
 *
 */
public class FCONST {

	public static final String SENTSTART = "<SENTSTART>", SENTEND = "<SENTEND>";

	public static final String F_WORD = "FT_WORD";
	public static final String F_LEMMA = "FT_LEMMA";
	public static final String F_POS = "FT_POS";
	public static final String F_CHUNK = "FT_CHUNK";
	public static final String F_NE = "FT_NE";
	public static final String F_PREFIX = "FT_PREFIX";
	public static final String F_SUFFIX = "FT_SUFFIX";
	public static final String F_WORDFORM = "FT_WORDFORM";
	public static final String F_INT = "FT_INT";
	public static final String F_DOUBLE = "FT_DOUBLE";
	public static final String F_CAP = "FT_CAP";

	public static final String T_WORD = "HT_WORD";
	public static final String T_LEMMA = "HT_LEMMA";
	public static final String T_POS = "HT_POS";
	public static final String T_CHUNK = "HT_CHUNK";
	public static final String T_NE = "HT_NE";
	public static final String T_PREFIX = "HT_PREFIX";
	public static final String T_SUFFIX = "HT_SUFFIX";
	public static final String T_WORDFORM = "HT_WORDFORM";
	public static final String T_CAP="HT_CAP";
	
	public static final String DUMMY="HT_DUMMY";


	public static String p(String ftype, int p) {
		return ftype +"@P"+p;
	}

	public static String n(String ftype, int n) {
		return ftype+"@N"+n;
	}
	
}
