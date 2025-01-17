package edu.cmu.lti.weizh.train.__deprecated__;

import edu.cmu.lti.weizh.docmodel.AbstractDataSet;
import edu.cmu.lti.weizh.train.Evaluatable;

public abstract class AbstractFDMMEval<
VTYPE, 
D extends AbstractDataSet, 
T extends AbstractFDMMTrainer<VTYPE, T, D>
>
implements Evaluatable<D> {

	protected T trainer;

	protected AbstractFDMMEval(T trainer) {
		this.trainer = trainer;

		if (trainer == null)
			throw new UnsupportedOperationException("Trainer should not be NULL. Otherwise Evaluator not found.");
	}
	
	@Override
	public void evaluate(D d) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
