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
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

public class FDA_MLModel extends MLModel implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Store dictionary. String to Id (int) map. Dic also includes Feature
	 * values for each token. and phi type id. entity type id.
	 */
	TObjectIntHashMap<String> dID, pID, eID;

	int did, pid, eid;
	public final static String DUMMY = "[ROW_TOTAL]";
	TIntObjectHashMap<String> ID2ELabel;
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
		this.dID = new TObjectIntHashMap<String>();
		dID.put(DUMMY, did++);// dummy variable for storing the sum.
		this.pID = new TObjectIntHashMap<String>();
		this.eID = new TObjectIntHashMap<String>();
		this.ID2ELabel = new TIntObjectHashMap<String>();
		did = pid = eid = 0;
		this.theta = new TIntObjectHashMap<TIntIntHashMap>();
		this.phis = new TIntObjectHashMap<TIntObjectHashMap<TIntIntHashMap>>();
	}

	public void print(){
		System.out.println("dID");
		System.out.println(dID);
		System.out.println("EID");
		System.out.println(eID);
		System.out.println("PID");
		System.out.println(pID);
		System.out.println("THETA");
		System.out.println(theta.toString());
		System.out.println("PHI");
		System.out.println(phis);
	}
	public void add(String ctok, String entityType, String f_PTOK, String ptok) {

		if (!eID.containsKey(entityType)) {
			eID.put(entityType, eid);
			ID2ELabel.put(eid++, entityType);
		}
		if (!dID.contains(ctok))
			dID.put(ctok, did++);

		if (!pID.contains(f_PTOK))
			pID.put(f_PTOK, pid++);

		if (!dID.contains(ptok))
			dID.put(ptok, did++);

		int d = dID.get(ctok);
		int e = eID.get(entityType);
		int p = dID.get(ptok);
		int pid = pID.get(f_PTOK);

		int dummy = dID.get(DUMMY);

		// fill out theta
		if (theta.containsKey(d)) {
			TIntIntHashMap dDistribution = theta.get(d);

			// increment total number.
			dDistribution.increment(dummy);

			if (dDistribution.containsKey(e))
				dDistribution.increment(e);
			else
				dDistribution.put(e, 1);
		} else {
			TIntIntHashMap dDistribution = new TIntIntHashMap();
			dDistribution.put(e, 1);
			dDistribution.put(dummy, 1);
			theta.put(d, dDistribution);
		}
		;

		// fill out the feature
		if (phis.contains(pid)) {
			TIntObjectHashMap<TIntIntHashMap> phi_i = phis.get(pid);
			if (phi_i.contains(e)) {
				TIntIntHashMap wordDist = phi_i.get(e);

				wordDist.increment(dummy);

				if (wordDist.contains(p))
					wordDist.increment(p);
				else
					wordDist.put(p, 1);

			} else {
				TIntIntHashMap wordDist = new TIntIntHashMap();
				wordDist.put(dummy, 1);
				wordDist.put(p, 1);
				phi_i.put(e, wordDist);
			}
		} else {
			TIntIntHashMap wordDist = new TIntIntHashMap();
			wordDist.put(dummy, 1);
			wordDist.put(p, 1);
			TIntObjectHashMap<TIntIntHashMap> phi_i = new TIntObjectHashMap<TIntIntHashMap>();
			phi_i.put(e, wordDist);
			phis.put(pid, phi_i);
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

	public String predict(String ctok, double alpha, double beta, String... args) throws Exception {
		if ((args.length & 0x01) !=0)
			throw new Exception(" Number of arguments for features should not be odd.");

		TIntIntHashMap entitytheta = theta.get(dID.get(ctok));
		int tsize = entitytheta.size() - 1;
		TIntIntIterator ei = entitytheta.iterator();
		int maxlabel = -1;
		double maxval = -1;

		while (ei.hasNext()) {
			 ei.advance();
			int label = ei.key();
			int tnum = ei.value();
			int tdenom = entitytheta.get(dID.get(DUMMY));

			double tempval = ((tnum + alpha) / (tdenom + tsize * alpha));
			for ( int i = 0 ; i < args.length; i+=2){
				TIntIntHashMap tempphi = phis.get(pID.get(args[i])).get(label);
				int num = tempphi.get(dID.get(args[i+1]));
				int denom = tempphi.get(dID.get(DUMMY));
				tempval *= (num+beta)/(denom+beta*(tempphi.size()-1));					
			}

			if (tempval>maxval){
				maxval = tempval;
				maxlabel = label;
			}
		}
		return ID2ELabel.get(maxlabel);
	}

}
