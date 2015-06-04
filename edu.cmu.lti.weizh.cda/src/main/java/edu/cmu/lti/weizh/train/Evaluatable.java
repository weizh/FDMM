package edu.cmu.lti.weizh.train;


public interface Evaluatable<D>{

	public void evaluate(D d) throws Exception;
}
