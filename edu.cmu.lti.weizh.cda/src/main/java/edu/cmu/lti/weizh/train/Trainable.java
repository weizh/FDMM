package edu.cmu.lti.weizh.train;

/**
 * Trainable <Model, Dataset>
 * @author wei
 *
 * @param <M>
 * @param <D>
 */
public interface Trainable<M, D> {

	public void train(D d) throws Exception;
	public String[] getFeatureHeaders();
	public M getModel();
}
