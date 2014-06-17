package edu.cmu.lti.weizh.fda;

import edu.cmu.lti.weizh.utils.BKTree;
import edu.cmu.lti.weizh.utils.Dictionary;

public class FDAGeoParser {
	FDA_Inferencer inferencer;
	static FDAGeoParser fda;

	public static FDAGeoParser getInstance() {
		if (fda == null)
			try {
				fda = new FDAGeoParser();
			} catch (Exception e) {
				e.printStackTrace();
			}
		return fda;
	}

	FDAGeoParser() {
		try {
			int foldId = 0;
			FDA_MLModel nerModel = FDA_MLModel.load("resources/rich-alltok-fold10." + foldId
					+ "(p5-n5_pos-tok-cap)(ctok-cpos-cclps-ctype).en.FDA_MLModel");
			FDA_MLModel posSentModel = FDA_MLModel.load("resources/POS-alltok-fold10." + foldId
					+ "(p5-n5_pos-tok-cap)(ctok-cpos-cclps-ctype).en.FDA_MLModel");
			Dictionary dict = new Dictionary("resources/en-dict.txt");
			BKTree bbt = new BKTree();
			bbt.init("GeoNames/allCountries.txt");
			inferencer = new FDA_Inferencer(posSentModel, nerModel, bbt, dict);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
