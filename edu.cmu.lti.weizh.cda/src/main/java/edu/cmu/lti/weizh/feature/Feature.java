package edu.cmu.lti.weizh.feature;

import edu.cmu.lti.weizh.docmodel.Sentence;
import edu.cmu.lti.weizh.docmodel.Word;

/**
 * header name Compond: header+[header_delimiter]+header; <br>
 * value compond:hvalue+[value_delimiter]+hvalue; <br>
 * header name position modifier: header@Pk, or header@Nk<br>
 * value position modifier: value@Pk, or value@Nk<br>
 * where k is the position relative to the current token
 * 
 * @author wei
 */
public class Feature<VALUETYPE> {

	private static String[] FEATURE_HEADER;
	private static String HEADER_DELIMITER;
	private static String VALUE_DELIMITER;

	public static void setFEATURE_HEADERS(String[] featureHeaders) {
		FEATURE_HEADER=featureHeaders;
	}
	public static String[] getFEATURE_HEADERS(){
		return FEATURE_HEADER;
	}
	public static void setHEADER_DELIMITER(String s) {
		HEADER_DELIMITER = s;
	}

	public static void setVALUE_DELIMITER(String vALUE_DELIMITER) {
		VALUE_DELIMITER = vALUE_DELIMITER;
	}

	public static String getHEADER_DELIMITER() {
		return HEADER_DELIMITER;
	}

	public static String getVALUE_DELIMITER() {
		return VALUE_DELIMITER;
	}

	public String toString(){
		return featureName + "\t" + value + "\tat position\t" + position;
		
	}
	String featureName;
	Sentence s;
	VALUETYPE value;
	int position;

	public Feature(String featureName, Sentence s, int position) {
		this.featureName = featureName;
		this.s = s;
		this.position = position;
		build();
	}

	public String getName() {
		return featureName;
	}

	/**
	 * Get the word in sentence with position "position".
	 * 
	 * @return
	 */
	public Word getWord() {
		if (position == -1 || position == s.getWords().size())
			throw new UnsupportedOperationException("Sentence start or end. No Word.");
		return s.getWords().get(position);
	}

	public VALUETYPE getValue() {
		if (value == null)
			throw new UnsupportedOperationException("Build the feature before calling getValue()");
		return value;
	}

	/**
	 * Asssume combinatorial headers possible. Now build the String feature
	 * values only.
	 * 
	 * @return
	 */
	private Feature<VALUETYPE> build() {
		String[] featureHeaders = featureName.trim().split(HEADER_DELIMITER);
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < featureHeaders.length; i++) {
			String value = getSingleStringFeatureValue(featureHeaders[i], position);
			if (value == null)
				return null;
			if (i == 0)
				sb.append(value);
			else
				sb.append(VALUE_DELIMITER).append(value);
		}
		this.value = (VALUETYPE) sb.toString();
		return this;
	}

	/**
	 * Feature value formatting: (Single feature) Value+FeatureName
	 * If feature value can not be formed, return feature only.
	 * 
	 * @param fh
	 * @param position
	 * @return
	 */
	private String getSingleStringFeatureValue(String fh, int position) {
		if (position == -1) {
			if (!fh.substring(fh.length() - 3, fh.length() - 1).equals("@N"))
				return fh;
			else
				return getSingleStringFeatureValue(fh, fh.charAt(fh.length() - 1) - '0');
		} else if (position == s.getWords().size()) {
			return fh + FCONST.SENTEND;
		}

		Word w = s.getWords().get(position);

		switch (fh) {
		case FCONST.F_WORD:
			return fh + w.getWord();
		case FCONST.F_LEMMA:
			return fh + w.getTrimLowered();
		case FCONST.F_CAP:
			return w.isCapitalizedFirst();
		case FCONST.F_WORDFORM:
			return fh + w.getWordForm();
		case FCONST.F_PREFIX:
			return fh + w.getPreffix();
		case FCONST.F_SUFFIX:
			return fh + w.getSuffix();
		case FCONST.F_POS:
			return fh + w.getPartOfSpeech();
		case FCONST.F_NE:
			return fh + w.getEntityType();
		case FCONST.F_CHUNK:
			return fh + w.getChunkType();
		default:

			if (fh.charAt(fh.length() - 3) == '@' && fh.charAt(fh.length() - 1) >= '0' && fh.charAt(fh.length() - 1) <= '9'
					&& (fh.charAt(fh.length() - 2) == 'P') || fh.charAt(fh.length() - 2) == 'N') {
				char direction = fh.charAt(fh.length() - 2);
				int offset = (int) (fh.charAt(fh.length() - 1) - '0');
				int newposition = getNewOffset(direction, offset);
				if (newposition == Integer.MIN_VALUE) {
					return fh;
				}
				return getSingleStringFeatureValue(fh.substring(0, fh.length() - 3), newposition);
			}
			return fh;
		}
	}

	/**
	 * return Integer.MinValue if out of bound.
	 * 
	 * @param direction
	 * @param offset
	 * @return
	 */
	private int getNewOffset(char direction, int offset) {

		int slength = s.getWords().size();
		int newpos = 0;

		if (direction == 'P') {
			newpos = position - offset;
			if (newpos < 0)
				return Integer.MIN_VALUE;
			return newpos;
		} else if (direction == 'N') {
			newpos = position + offset;
			if (newpos > slength - 1)
				return Integer.MIN_VALUE;
			return newpos;
		} else
			return Integer.MIN_VALUE;
	}


	static String fhd = "-fhd-";
	static String fvd = "-fvd-";

	static String[] basicNerFeatureHeaders = new String[] { 
			// basic

			FCONST.p(FCONST.F_POS, 2) + fhd + FCONST.p(FCONST.F_POS, 1), 
			FCONST.p(FCONST.F_POS, 1) + fhd + FCONST.n(FCONST.F_POS, 1),
			FCONST.n(FCONST.F_POS, 1) + fhd + FCONST.n(FCONST.F_POS, 2),
			
			FCONST.p(FCONST.F_WORDFORM, 2) + fhd + FCONST.p(FCONST.F_WORDFORM, 1), 
			FCONST.p(FCONST.F_WORDFORM, 1) + fhd + FCONST.n(FCONST.F_WORDFORM, 1),
			FCONST.n(FCONST.F_WORDFORM, 1) + fhd + FCONST.n(FCONST.F_WORDFORM, 2),
			
			FCONST.p(FCONST.F_LEMMA, 1), 
			FCONST.p(FCONST.F_LEMMA, 2), 
			FCONST.n(FCONST.F_LEMMA, 1),
			FCONST.n(FCONST.F_LEMMA, 2),
	};


	public static String[] getOnfNerFeatureHeaders() {
		return basicNerFeatureHeaders;
	}
	
	static String[] nlpaFeatureHeaders = new String[] { 
			// basic
		FCONST.p(FCONST.F_LEMMA, 1), 
		FCONST.p(FCONST.F_LEMMA, 2), 
		FCONST.n(FCONST.F_LEMMA, 1),
		FCONST.n(FCONST.F_LEMMA, 2),
		
		FCONST.p(FCONST.F_LEMMA, 2) + fhd + FCONST.p(FCONST.F_LEMMA, 1), 
		FCONST.p(FCONST.F_LEMMA, 1) + fhd + FCONST.n(FCONST.F_LEMMA, 1),
		FCONST.n(FCONST.F_LEMMA, 1) + fhd + FCONST.n(FCONST.F_LEMMA, 2),
		
		FCONST.p(FCONST.F_CAP, 1), 
		FCONST.p(FCONST.F_CAP, 2), 
		FCONST.n(FCONST.F_CAP, 1),
		FCONST.n(FCONST.F_CAP, 2),
		
		FCONST.p(FCONST.F_CAP, 2) + fhd + FCONST.p(FCONST.F_CAP, 1), 
		FCONST.p(FCONST.F_CAP, 1) + fhd + FCONST.n(FCONST.F_CAP, 1),
		FCONST.n(FCONST.F_CAP, 1) + fhd + FCONST.n(FCONST.F_CAP, 2),
		
		FCONST.p(FCONST.F_WORDFORM, 1), 
		FCONST.p(FCONST.F_WORDFORM, 2), 
		FCONST.n(FCONST.F_WORDFORM, 1),
		FCONST.n(FCONST.F_WORDFORM, 2),
		FCONST.p(FCONST.F_WORDFORM, 2) + fhd + FCONST.p(FCONST.F_WORDFORM, 1), 
		FCONST.p(FCONST.F_WORDFORM, 1) + fhd + FCONST.n(FCONST.F_WORDFORM, 1),
		FCONST.n(FCONST.F_WORDFORM, 1) + fhd + FCONST.n(FCONST.F_WORDFORM, 2),
		
		FCONST.p(FCONST.F_SUFFIX, 1), 
		FCONST.p(FCONST.F_SUFFIX, 2), 
		FCONST.n(FCONST.F_SUFFIX, 1),
		FCONST.n(FCONST.F_SUFFIX, 2),
		
		FCONST.p(FCONST.F_SUFFIX, 2) + fhd + FCONST.p(FCONST.F_SUFFIX, 1), 
		FCONST.p(FCONST.F_SUFFIX, 1) + fhd + FCONST.n(FCONST.F_SUFFIX, 1),
		FCONST.n(FCONST.F_SUFFIX, 1) + fhd + FCONST.n(FCONST.F_SUFFIX, 2),
		
		FCONST.p(FCONST.F_PREFIX, 1), 
		FCONST.p(FCONST.F_PREFIX, 2), 
		FCONST.n(FCONST.F_PREFIX, 1),
		FCONST.n(FCONST.F_PREFIX, 2),
		
		FCONST.p(FCONST.F_PREFIX, 2) + fhd + FCONST.p(FCONST.F_PREFIX, 1), 
		FCONST.p(FCONST.F_PREFIX, 1) + fhd + FCONST.n(FCONST.F_PREFIX, 1),
		FCONST.n(FCONST.F_PREFIX, 1) + fhd + FCONST.n(FCONST.F_PREFIX, 2),
			
	};

	public static String[] getNlpbaNerFeatureHeaders() {
		return nlpaFeatureHeaders;
	}
	
	static String[] conll2kChunkingfeatureHeaders = new String[] { FCONST.p(FCONST.F_POS, 1), FCONST.n(FCONST.F_POS, 1),
			FCONST.p(FCONST.F_POS, 2),
			FCONST.n(FCONST.F_POS, 2),
			//
			FCONST.p(FCONST.F_LEMMA, 1), FCONST.p(FCONST.F_LEMMA, 2), FCONST.n(FCONST.F_LEMMA, 1),
			FCONST.n(FCONST.F_LEMMA, 2),

			FCONST.p(FCONST.F_SUFFIX, 1), FCONST.n(FCONST.F_SUFFIX, 1), FCONST.p(FCONST.F_PREFIX, 2),
			FCONST.n(FCONST.F_PREFIX, 2),

	};
	
	public static String[] getCONLL2KChunkingFeatureHeaders() {
		return conll2kChunkingfeatureHeaders;
	}
	
	static String[] POSfeatureHeaders = new String[] { 
		// basic

		FCONST.p(FCONST.F_SUFFIX, 2) + fhd + FCONST.p(FCONST.F_SUFFIX, 1), 
		FCONST.p(FCONST.F_SUFFIX, 1) + fhd + FCONST.n(FCONST.F_SUFFIX, 1),
		FCONST.n(FCONST.F_SUFFIX, 1) + fhd + FCONST.n(FCONST.F_SUFFIX, 2),
		
		FCONST.p(FCONST.F_WORDFORM, 2) + fhd + FCONST.p(FCONST.F_WORDFORM, 1), 
		FCONST.p(FCONST.F_WORDFORM, 1) + fhd + FCONST.n(FCONST.F_WORDFORM, 1),
		FCONST.n(FCONST.F_WORDFORM, 1) + fhd + FCONST.n(FCONST.F_WORDFORM, 2),
		
		FCONST.p(FCONST.F_LEMMA, 1), 
		FCONST.p(FCONST.F_LEMMA, 2), 
		FCONST.n(FCONST.F_LEMMA, 1),
		FCONST.n(FCONST.F_LEMMA, 2),
};
	
	public static String[] getPOSfeatureHeaders() {
		return POSfeatureHeaders;
	}
	
	public static String getFhd() {
		return fhd;
	}

	public static String getFvd() {
		return fvd;
	}

	
}
