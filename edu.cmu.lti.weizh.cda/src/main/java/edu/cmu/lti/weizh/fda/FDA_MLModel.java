package edu.cmu.lti.weizh.fda;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import edu.cmu.lti.weizh.Interface.MLModel;
import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

public class FDA_MLModel extends MLModel implements Serializable {

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

	FDA_MLModel() {
		this.DID = new TObjectIntHashMap<String>();
		did = fid = lid = 0;
		DID.put(TOTAL, did++);// dummy variable for storing the sum.
		this.FID = new TObjectIntHashMap<String>();
		this.LID = new TObjectIntHashMap<String>();
		this.LID2LString = new TIntObjectHashMap<String>();
		this.theta = new TIntObjectHashMap<TIntIntHashMap>();
		this.phis = new TIntObjectHashMap<TIntObjectHashMap<TIntIntHashMap>>();
	}

	public void print() {
		System.out.println("DID");
		System.out.println(DID);
		System.out.println("LID");
		System.out.println(LID);
		System.out.println("FID");
		System.out.println(FID);
		System.out.println("THETA");
		System.out.println(theta);
		System.out.println("PHI");
		System.out.println(phis);
	}

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

			if (dDistribution.containsKey(etype))
				dDistribution.increment(etype);
			else {
				dDistribution.put(etype, 1);
			}
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

				wordDist.increment(tot);

				if (wordDist.containsKey(fval))
					wordDist.increment(fval);
				else
					wordDist.put(fval, 1);

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
		FDA_MLModel model = FDA_MLModel.load("en.FDA_MLModel");

		System.out.println();

	}

	public static FDA_MLModel load(String file) throws IOException, ClassNotFoundException {
		FileInputStream in = new FileInputStream(file);
		ObjectInputStream ins = new ObjectInputStream(in);
		FDA_MLModel copy = (FDA_MLModel) ins.readObject();
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

	public String predict(String[] thetaIndex, double alpha, double beta, String... args) throws Exception {
		if ((args.length & 0x01) != 0)
			throw new Exception(" Number of arguments for features should not be odd.");

		// if (DID.containsKey(ctok))
		TIntIntHashMap entitytheta = theta.get(DID.get(thetaIndex[0]));
		if (entitytheta == null) {

			entitytheta = theta.get(DID.get(thetaIndex[1]));
			// return null;
		}

		int tsize = entitytheta.size() - 1;

		TIntIntIterator ei = entitytheta.iterator();
		int maxlabel = -1;
		double maxval = -1;

		while (ei.hasNext()) {
			ei.advance();
			int label = ei.key();
			if (label == entitytheta.get(DID.get(TOTAL)))
				continue;
			int tnum = ei.value();
			int tdenom = entitytheta.get(DID.get(TOTAL));

			double tempval = ((tnum + alpha) / (tdenom + tsize * alpha));
			for (int i = 0; i < args.length; i += 2) {
				TIntIntHashMap tempphi = phis.get(FID.get(args[i])).get(label);
				int num = tempphi.get(DID.get(args[i + 1]));
				int denom = tempphi.get(DID.get(TOTAL));
				tempval *= (num + beta) / (denom + beta * (tempphi.size() - 1));
			}

			if (tempval > maxval) {
				maxval = tempval;
				maxlabel = label;
			}
		}
		return LID2LString.get(maxlabel);
	}

	public void collapse() {

		TIntObjectIterator<TIntObjectHashMap<TIntIntHashMap>> ti = phis.iterator();
		while (ti.hasNext()) {
			ti.advance();
			TIntObjectHashMap<TIntIntHashMap> m = ti.value();
			TIntObjectIterator<TIntIntHashMap> mi = m.iterator();
			while (mi.hasNext()) {
				mi.advance();
				TIntIntHashMap map = mi.value();
				TObjectIntIterator<String> Diter = DID.iterator();
				while (Diter.hasNext()) {
					Diter.advance();
					if (Diter.key().equals(TOTAL))
						continue;
					String lower = Diter.key().toLowerCase();
					if (lower.equals(Diter.key()))
						continue;
					else {
						// if contains capitalized
						if (map.containsKey(Diter.value()))
							if (map.containsKey(DID.get(lower)))
								map.put(DID.get(lower), map.get(DID.get(lower)) + map.get(Diter.value()));
							else
								map.put(DID.get(lower), map.get(Diter.value()));
					}
				}
			}
		}
	}

}
