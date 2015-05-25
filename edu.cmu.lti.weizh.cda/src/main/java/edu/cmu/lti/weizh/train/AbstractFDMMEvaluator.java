package edu.cmu.lti.weizh.train;

import edu.cmu.lti.weizh.io.Storable;
import edu.cmu.lti.weizh.mlmodel.FDMM;

public abstract class AbstractFDMMEvaluator<D,T,E> extends Storable<E> implements Evaluatable<FDMM, D>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected T trainer;
	protected AbstractFDMMEvaluator(T trainer){
		this.trainer = trainer;
		
		if (trainer==null)
			throw new UnsupportedOperationException("Trainer should not be NULL. Otherwise Evaluator not found.");
	}

}
