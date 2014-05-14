package edu.cmu.lti.weizh.fda;

import edu.cmu.lti.weizh.models.MLModel;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

public class FDA_MLModel extends MLModel {

	/*
	 * Store dictionary. String to Id (int) map. Dic also includes Feature
	 * values for each token.
	 * label id, and phi id.
	 */
	TObjectIntHashMap<String> dID, lID,pID;


	/*
	 * Store the Distribution of each entity over labels. <EntityID <LabelID,
	 * FREQ>>
	 */
	TIntObjectHashMap<TIntIntHashMap> thetaNum, thetaDenom;


	/*
	 * Define the feature distribution over <phi_iID:<labelID<dicID,Freq>>>
	 */
	TIntObjectHashMap<TIntObjectHashMap<TIntIntHashMap>> phisNum, phisDenom;

	FDA_MLModel() {
		this.dID = new TObjectIntHashMap<String>();
		this.lID = new TObjectIntHashMap<String>();
		this.thetaNum = new TIntObjectHashMap<TIntIntHashMap>();
		this.thetaDenom = new TIntObjectHashMap<TIntIntHashMap>();
		this.pID = new TObjectIntHashMap<String>();
		this.phisNum = new TIntObjectHashMap<TIntObjectHashMap<TIntIntHashMap>>();
		this.phisDenom = new TIntObjectHashMap<TIntObjectHashMap<TIntIntHashMap>>();
	}

	public void add(String ctok, String entityType, String f_PTOK, String ptok) {
		// TODO Auto-generated method stub
		
	}
	
	

}
