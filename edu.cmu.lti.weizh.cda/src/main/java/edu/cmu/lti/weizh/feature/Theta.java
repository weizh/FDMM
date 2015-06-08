package edu.cmu.lti.weizh.feature;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.lti.weizh.docmodel.Word;

/**
 * header name Compond: header+[header_delimiter]+header value compond:
 * hvalue+[value_delimiter]+hvalue
 * 
 * @author wei
 *
 * @param <VALUETYPE>
 */
public class Theta<VALUETYPE> {

	private static String[] THETA_HEADERS;
	private static String HEADER_DELIMITER;
	private static String VALUE_DELIMITER;

	public static void setTHETA_HEADERS(String[] headers) {
		THETA_HEADERS = headers;
	}

	public static void setHEADER_DELIMITER(String s) {
		HEADER_DELIMITER = s;
	}

	public static void setVALUE_DELIMITER(String vALUE_DELIMITER) {
		VALUE_DELIMITER = vALUE_DELIMITER;
	}

	public static String[] getTHETA_HEADERS() {
		return THETA_HEADERS;
	}

	public static String getHEADER_DELIMITER() {
		return HEADER_DELIMITER;
	}

	public static String getVALUE_DELIMITER() {
		return VALUE_DELIMITER;
	}

	Word w;
	List<VALUETYPE> value;

	public String toString(){
		return w + "\t" + value;
	}
	public Theta(Word w) {
		this.w = w;
		build();
	}

	public Word getWord() {
		return w;
	}

	public List<VALUETYPE> getValue() {
		if (value == null)
			throw new UnsupportedOperationException("Build theta before calling getValue()");
		return value;
	}

	/**
	 * Asssume combinatorial headers possible. Now build the String feature
	 * values only.
	 * 
	 * @return
	 */
	private Theta<VALUETYPE> build() {

		List<VALUETYPE> values = new ArrayList<VALUETYPE>();

		for (String h : THETA_HEADERS) {
			values.add(getCompondFeatureValue(h, w));
		}
		value = values;
		return this;
	}

	private VALUETYPE getCompondFeatureValue(String h, Word w) {
		String[] header = h.trim().split(Theta.HEADER_DELIMITER);
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (String hd : header) {
			if (i > 0)
				sb.append(Theta.VALUE_DELIMITER);
			sb.append(getSingleStringFeatureValue(hd, w));
			i++;
		}
		return (VALUETYPE) sb.toString();
	}

	private String getSingleStringFeatureValue(String fh, Word w) {

		switch (fh) {
		case FCONST.T_WORD:
			return w.getWord();
		case FCONST.T_LEMMA:
			return w.getTrimLowered();
		case FCONST.T_CAP:
			return w.isCapitalizedFirst();
		case FCONST.T_WORDFORM:
			return w.getWordForm();
		case FCONST.T_PREFIX:
			return w.getPreffix();
		case FCONST.T_SUFFIX:
			return w.getSuffix();
		case FCONST.T_POS:
			return w.getPartOfSpeech();
		case FCONST.T_NE:
			return w.getEntityType();
		case FCONST.T_CHUNK:
			return w.getChunkType();
		case FCONST.DUMMY:
			return "[DUMMY]";
		default:
			return null;
		}
	}

	static String thd = "-thd-";
	static String tvd = "-tvd-";
	
	static String[] basicNerThetaHeaders = new String[] { 
		//basic
		FCONST.T_WORD, 
		FCONST.T_LEMMA, 
		FCONST.T_WORDFORM + thd + FCONST.T_SUFFIX,
		FCONST.T_PREFIX + thd + FCONST.T_SUFFIX,
		FCONST.T_POS + thd + FCONST.T_SUFFIX,
		FCONST.T_POS, 
		FCONST.T_WORDFORM,
		FCONST.T_SUFFIX
		};
	
	public static String[] getOnfNerThetaHeaders() {
		return basicNerThetaHeaders;
	}
	
	 static String[] nlpbaThetaHeaders = new String[] { 
		//basic
		FCONST.T_WORD, 
		FCONST.T_LEMMA, 
		FCONST.T_CAP,
		FCONST.T_WORDFORM,
		FCONST.T_SUFFIX,
		FCONST.T_PREFIX,
		FCONST.T_CAP +thd+FCONST.T_PREFIX,
		FCONST.T_CAP +thd + FCONST.T_WORDFORM,
		FCONST.T_CAP +thd + FCONST.T_SUFFIX,
		FCONST.T_WORDFORM + thd + FCONST.T_SUFFIX,
		FCONST.T_WORDFORM + thd + FCONST.T_PREFIX,
		FCONST.T_PREFIX + thd + FCONST.T_SUFFIX,

		};
	 public static String[] getNlpbaNerThetaHeaders(){
		 return nlpbaThetaHeaders;
	 }
	 

	static 	String[] conll2kChunkingthetaHeaders = new String[] { FCONST.T_WORD, FCONST.T_LEMMA, FCONST.T_WORDFORM + thd + FCONST.T_SUFFIX,
				FCONST.T_POS + thd + FCONST.T_SUFFIX, FCONST.T_POS, FCONST.T_WORDFORM, FCONST.T_SUFFIX };

	public static String[] getConll2kChunkingthetaHeaders(){
		 return conll2kChunkingthetaHeaders;
	 }
	
	static 
	String[] POSthetaHeaders = new String[] { 
		//basic
		FCONST.T_WORD, 
		FCONST.T_LEMMA, 
		FCONST.T_WORDFORM + thd + FCONST.T_SUFFIX,
		FCONST.T_PREFIX + thd + FCONST.T_SUFFIX,
		FCONST.T_WORDFORM,
		FCONST.T_SUFFIX
		};
	
	public static String[] getPOSthetaHeaders(){
		 return POSthetaHeaders;
	 }
	
	public static String getThd() {
		return thd;
	}

	public static String getTvd() {
		return tvd;
	}
	
}
