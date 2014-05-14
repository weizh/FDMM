package edu.cmu.lti.weizh.fda;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import edu.cmu.lti.weizh.Interface.OjbectLoader;
import edu.cmu.lti.weizh.Interface.MLModel;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

public class FDA_MLModel extends MLModel implements OjbectLoader<FDA_MLModel>, Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /*
   * Store dictionary. String to Id (int) map. Dic also includes Feature values for each token. and
   * phi type id. entity type id.
   */
  TObjectIntHashMap<String> dID, pID, eID;

  int did, pid, eid;

  /*
   * Store the Distribution of each entity over labels. <EntityID <LabelID, FREQ>>
   */
  TIntObjectHashMap<TIntIntHashMap> theta;

  /*
   * Define the feature distribution over <phi_iID:<labelID<dicID,Freq>>> Here we use a specific
   * String for denoting the sum. This string should be: [PHI_I_SUM]. The put of this string is in
   * first time filling out the value.
   */
  TIntObjectHashMap<TIntObjectHashMap<TIntIntHashMap>> phis;

  FDA_MLModel() {
    this.dID = new TObjectIntHashMap<String>();
    this.pID = new TObjectIntHashMap<String>();
    this.eID = new TObjectIntHashMap<String>();
    did = pid = eid = 0;
    this.theta = new TIntObjectHashMap<TIntIntHashMap>();
    this.phis = new TIntObjectHashMap<TIntObjectHashMap<TIntIntHashMap>>();
  }

  public void add(String ctok, String entityType, String f_PTOK, String ptok) {

    if (!eID.containsKey(entityType))
      eID.put(entityType, eid++);

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

    // fill out theta
    if (theta.containsKey(d)) {
      TIntIntHashMap dDistribution = theta.get(d);
      if (dDistribution.containsKey(e))
        dDistribution.increment(e);
      else
        dDistribution.put(e, 1);
    } else {
      TIntIntHashMap dDistribution = new TIntIntHashMap();
      dDistribution.put(e, 1);
      theta.put(d, dDistribution);
    }
    ;

    // fill out the feature
    if (phis.contains(pid)) {
      TIntObjectHashMap<TIntIntHashMap> phi_i = phis.get(pid);
      if (phi_i.contains(e)) {
        TIntIntHashMap wordDist = phi_i.get(e);
        if (wordDist.contains(d))
          wordDist.increment(d);
        else
          wordDist.put(d, 1);
      } else {
        TIntIntHashMap wordDist = new TIntIntHashMap();
        wordDist.put(d, 1);
        phi_i.put(e, wordDist);
      }
    } else {
      TIntIntHashMap wordDist = new TIntIntHashMap();
      wordDist.put(d, 1);
      TIntObjectHashMap<TIntIntHashMap> phi_i = new TIntObjectHashMap<TIntIntHashMap>();
      phi_i.put(e, wordDist);
      phis.put(pid, phi_i);
    }
  }

  public static void main(String args[]) throws IOException, ClassNotFoundException {

    System.out.println();

  }

  public FDA_MLModel load(String file) throws IOException, ClassNotFoundException {
    FileInputStream in = new FileInputStream(file);
    ObjectInputStream ins = new ObjectInputStream(in);
    FDA_MLModel copy = (FDA_MLModel) ins.readObject();
    in.close();
    ins.close();
    return copy;
  }

}
