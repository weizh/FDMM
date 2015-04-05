package edu.cmu.lti.weizh.train;


public interface Inferencer<M, D> {

	public void infer(M model, D d) throws Exception;
}
