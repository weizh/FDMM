package edu.cmu.lti.weizh.train.ontonotes.cli;

import java.util.ArrayList;

import edu.cmu.lti.weizh.docmodel.NamedEntity;
import edu.cmu.lti.weizh.mlmodel.FDMM;
import edu.cmu.lti.weizh.utils.BKTree;
import edu.cmu.lti.weizh.utils.Dictionary;

public class FDAGeoParser {
	FDA_Inferencer inferencer;
	static FDAGeoParser fda;

	public static FDAGeoParser getInstance(String dic, String posfile, String nerfile, String geonames) {
		if (fda == null)
			try {
				fda = new FDAGeoParser(dic, posfile, nerfile, geonames);
			} catch (Exception e) {
				e.printStackTrace();
			}
		return fda;
	}

	FDAGeoParser(String dic, String posfile, String nerfile, String geonames) {
		try {
			int foldId = 0;
			FDMM nerModel = FDMM.load(nerfile);
			FDMM posSentModel = FDMM.load(posfile);
			Dictionary dict = new Dictionary(dic);
			BKTree bbt = new BKTree();
			bbt.init(geonames);
			inferencer = new FDA_Inferencer(posSentModel, nerModel, bbt, dict);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<NamedEntity> parse(String line) {
		return inferencer.infer(line);
	}

	public static void main(String argv[]) {
		ArrayList<NamedEntity> a = FDAGeoParser.getInstance("src/main/resources/en-dict.txt",
				"src/main/resources/POS-alltok-fold10.0(p5-n5_pos-tok-cap)(ctok-cpos-cclps-ctype).en.FDA_MLModel",
				"src/main/resources/rich-alltok-fold10.0(p5-n5_pos-tok-cap)(ctok-cpos-cclps-ctype).en.FDA_MLModel",
				"GeoNames/allCountries.txt").parse("I love Pitsburgh");

		System.out.println(a);
	}

}
