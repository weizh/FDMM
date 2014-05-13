package edu.cmu.lti.weizh.fda;

import edu.cmu.lti.weizh.models.MLModel;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;

import java.util.HashMap;

public class FDA_MLModel extends MLModel {

  /*
   * Store dictionary.  String to Id (int) map. Dic also includes Feature values for each token.
   */
  TObjectIntMap<String> dictIdMap;
  
  /*
   * Store the tag and id map
   */
  TObjectIntMap<String> tagIdMap;
  
  /*
   * Store the Distribution of each entity over tags.
   */
  TIntObjectMap<TIntIntMap> theta;
  
  /*
   * Define an arbitary number of phi_i s.
   */
  TObjectIntMap<String> phi_iIdMap;
    
  /*
   * Define the feature distribution over 
   * <phi_iID:<tagID<dicID,Freq>>>
   */  
  TIntObjectMap<TIntObjectMap<TIntIntMap>> phis;
  
  
}
