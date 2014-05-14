package edu.cmu.lti.weizh.models;

import gnu.trove.map.TObjectIntMap;

public class Global {

	public static final String E_PERSON = "PERSON", E_NORP = "NORP", E_FAC = "FAC", E_ORG = "ORG", E_LOC = "LOC",
			E_PRODUCT = "PRODUCT", E_EVENT = "EVENT", E_WORK_OF_ART = "WORK_OF_ART", E_LAW = "LAW", E_LANGUAGE = "LANGUAGE",
			E_DATE = "DATE", E_TIME = "TIME", E_PERCENT = "PERCENT", E_MONEY = "MONEY", E_QUANTITY = "QUANTITY",
			E_ORDINAL = "ORDINAL", E_CARDINAL = "CARDINAL";

	public static String P_CC = "CC", P_CD = "CD", P_DT = "DT", P_EX = "EX", P_FW = "FW", P_IN = "IN", P_JJ = "JJ",
			P_JJR = "JJR", P_JJS = "JJS", P_LS = "LS", P_MD = "MD", P_NN = "NN", P_NNS = "NNS", P_NNP = "NNP", P_NNPS = "NNPS",
			P_PDT = "PDT", P_POS = "POS", P_PRP = "PRP", P_PRP$ = "PRP$", P_RB = "RB", P_RBR = "RBR", P_RBS = "RBS", P_RP = "RP",
			P_SYM = "SYM", P_TO = "TO", P_UH = "UH", P_VB = "VB", P_VBD = "VBD", P_VBG = "VBG", P_VBN = "VBN", P_VBP = "VBP",
			P_VBZ = "VBZ", P_WDT = "WDT", P_WP = "WP", P_WP$ = "WP$", P_WRB = "WRB";

	public static String F_PTOK = "PTOK", F_NTOK = "NTOK";

	public static TObjectIntMap<String> E2IDMap;
	public static TObjectIntMap<String> POS2IDMap;
	public static TObjectIntMap<String> feature2IDMap;

}
