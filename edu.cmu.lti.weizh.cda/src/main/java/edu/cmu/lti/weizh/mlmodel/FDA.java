package edu.cmu.lti.weizh.mlmodel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import edu.cmu.lti.weizh.docmodel.Document;
import edu.cmu.lti.weizh.docmodel.NamedEntity;
import edu.cmu.lti.weizh.docmodel.Sentence;
import edu.cmu.lti.weizh.eval.Prediction;
import edu.cmu.lti.weizh.feature.ONF_CONSTS;
import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

public class FDA extends MLModel {

	String modelname;

	private static final long serialVersionUID = 1L;

	/*
	 * Store dictionary. String to Id (int) map. Dic also includes Feature
	 * values for each token. and phi type id. entity type id.
	 */
	TObjectIntHashMap<String> DID, FID, LID;

	int did, fid, lid;
	public final static String TOTAL = "[ROW_TOTAL]";
	TIntObjectHashMap<String> LID2LString;
	/*
	 * Store the Distribution of each entity over labels. <EntityID <LabelID,
	 * FREQ>>
	 */
	TIntObjectHashMap<TIntIntHashMap> theta;

	/*
	 * Define the feature distribution over <phi_iID:<labelID<dicID,Freq>>> Here
	 * we use a specific String for denoting the sum. This string should be:
	 * [PHI_I_SUM]. The put of this string is in first time filling out the
	 * value.
	 */
	TIntObjectHashMap<TIntObjectHashMap<TIntIntHashMap>> phis;

	public FDA() {
		this.DID = new TObjectIntHashMap<String>();
		this.LID = new TObjectIntHashMap<String>();
		this.LID2LString = new TIntObjectHashMap<String>();
		did = fid = lid = 0;
		DID.put(TOTAL, did++);// dummy variable for storing the sum.
		LID.put(TOTAL, lid++);

		this.FID = new TObjectIntHashMap<String>();

		this.theta = new TIntObjectHashMap<TIntIntHashMap>();
		this.phis = new TIntObjectHashMap<TIntObjectHashMap<TIntIntHashMap>>();
	}

	// total is added to feature distribution in <INTINThashMap> of
	// phi[feature][label],
	// and to <INTINTHashMap> for theta[word].
	public void add(String ctok, String entityType, String f_PTOK, String ptok) {

		if (!DID.containsKey(ctok))
			DID.put(ctok, did++);

		if (!LID.containsKey(entityType)) {
			LID.put(entityType, lid);
			LID2LString.put(lid++, entityType);
		}

		if (!FID.containsKey(f_PTOK))
			FID.put(f_PTOK, fid++);

		if (!DID.containsKey(ptok))
			DID.put(ptok, did++);

		int c = DID.get(ctok);
		int etype = LID.get(entityType);
		int fheader = FID.get(f_PTOK);
		int fval = DID.get(ptok);

		int tot = DID.get(TOTAL);

		// fill out theta
		if (theta.containsKey(c)) {
			TIntIntHashMap dDistribution = theta.get(c);

			// increment total number.
			dDistribution.increment(tot);
			dDistribution.adjustOrPutValue(etype, 1, 1);

		} else {
			TIntIntHashMap dDistribution = new TIntIntHashMap();
			dDistribution.put(etype, 1);
			dDistribution.put(tot, 1);
			theta.put(c, dDistribution);
		}

		// fill out the feature
		if (phis.containsKey(fheader)) {
			TIntObjectHashMap<TIntIntHashMap> phi_i = phis.get(fheader);

			if (phi_i.containsKey(etype)) {
				TIntIntHashMap wordDist = phi_i.get(etype);

				wordDist.adjustOrPutValue(fval, 1, 1);
				wordDist.increment(tot);

			} else {
				TIntIntHashMap wordDist = new TIntIntHashMap();
				wordDist.put(tot, 1);
				wordDist.put(fval, 1);
				phi_i.put(etype, wordDist);
			}
		} else {
			TIntIntHashMap wordDist = new TIntIntHashMap();
			wordDist.put(tot, 1);
			wordDist.put(fval, 1);
			TIntObjectHashMap<TIntIntHashMap> phi_i = new TIntObjectHashMap<TIntIntHashMap>();
			phi_i.put(etype, wordDist);
			phis.put(fheader, phi_i);
		}
	}

	public static void main(String args[]) throws IOException, ClassNotFoundException {
		FDA model = FDA.load("en.FDA_MLModel");

		System.out.println();

	}

	public static FDA load(String file) throws IOException, ClassNotFoundException {
		FileInputStream in = new FileInputStream(file);
		ObjectInputStream ins = new ObjectInputStream(in);
		FDA copy = (FDA) ins.readObject();
		copy.modelname = file;
		in.close();
		ins.close();
		return copy;
	}

	@Override
	public void store(String file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream out = new ObjectOutputStream(fos);
		out.writeObject(this);
		out.close();
	}

	public void train(Document startData) {
		// TODO Auto-generated method stub

	}

	
	public Prediction predict(String[] thetaIndex, double alpha, double beta, String... args) throws Exception {
		if ((args.length & 0x01) != 0)
			throw new Exception(" Number of arguments for features should not be odd.");

		// if (DID.containsKey(ctok))
		TIntIntHashMap entitytheta = theta.get(DID.get(thetaIndex[0]));
		int et = 1;
		while (entitytheta == null) {

			entitytheta = theta.get(DID.get(thetaIndex[et++]));
		}

		int tsize = entitytheta.size() - 1;

		TIntIntIterator ei = entitytheta.iterator();
		int maxlabel = -1;
		double maxval = Double.NEGATIVE_INFINITY;
		String maxStringlabel = null;
		ArrayList<String> predNames = new ArrayList<String>();
		ArrayList<Double> predValues = new ArrayList<Double>();
		
		double sum = 0;
		while (ei.hasNext()) {
			ei.advance();
			int label = ei.key();
			
			if (label == (LID.get(TOTAL))) {
				continue;
			}
			String strLabel = LID2LString.get(label);
			
			int tnum = ei.value();
			int tdenom = entitytheta.get(LID.get(TOTAL));

			double tempval = ((tnum + alpha) / (tdenom + tsize * alpha));
			for (int i = 0; i < args.length; i += 2) {
				TIntIntHashMap tempphi = phis.get(FID.get(args[i])).get(label);
				int num = tempphi.get(DID.get(args[i + 1]));
				int denom = tempphi.get(DID.get(TOTAL));
				tempval *= (num + beta) / (denom + beta * (tempphi.size() - 1));
			}
			predNames.add(strLabel);
			predValues.add(tempval);
			
			sum += tempval;
			
			if (tempval>maxval){
				maxval = tempval;
				maxStringlabel = strLabel;
			}
		}
		for ( int i = 0 ; i< predValues.size(); i++){
			predValues.set(i, predValues.get(i)/sum);
		}
		
		return new Prediction(predNames,predValues,maxStringlabel, maxval/sum);
	}
}
