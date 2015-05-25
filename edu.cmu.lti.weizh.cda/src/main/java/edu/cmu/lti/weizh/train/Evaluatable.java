package edu.cmu.lti.weizh.train;


public interface Evaluatable<T, D>{

	public void evaluate(D d) throws Exception;
}
