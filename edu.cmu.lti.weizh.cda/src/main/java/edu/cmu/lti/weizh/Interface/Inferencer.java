package edu.cmu.lti.weizh.Interface;


public interface Inferencer<M, D> {

	public void infer(M model, D d) throws Exception;
}
