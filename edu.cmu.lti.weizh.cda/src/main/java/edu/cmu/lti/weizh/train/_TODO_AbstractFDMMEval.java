package edu.cmu.lti.weizh.train;

import edu.cmu.lti.weizh.docmodel.AbstractDataSet;

public abstract class _TODO_AbstractFDMMEval<
VTYPE, 
D extends AbstractDataSet, 
T extends AbstractFDMMTrainer<VTYPE, T, D>
>
implements Evaluatable<D> {

	protected T trainer;

	protected _TODO_AbstractFDMMEval(T trainer) {
		this.trainer = trainer;

		if (trainer == null)
			throw new UnsupportedOperationException("Trainer should not be NULL. Otherwise Evaluator not found.");
	}
	
	@Override
	public void evaluate(D d) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
