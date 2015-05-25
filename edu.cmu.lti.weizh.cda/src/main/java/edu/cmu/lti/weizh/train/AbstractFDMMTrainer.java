package edu.cmu.lti.weizh.train;



import edu.cmu.lti.weizh.feature.Feature;
import edu.cmu.lti.weizh.feature.Theta;
import edu.cmu.lti.weizh.io.Storable;
import edu.cmu.lti.weizh.mlmodel.FDMM;

public abstract class AbstractFDMMTrainer<FVTYPE,T,D> extends Storable<T> implements Trainable<FDMM, D>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String[] thetaHeaders;
	protected String thetaHeaderDelimiter;
	protected String thetaValueDelimiter;
	
	protected String[] featureHeaders;
	protected String featureHeaderDelimiter;
	protected String featureValueDelimiter;
	
	protected FDMM fdmm;

	public AbstractFDMMTrainer(String[] thetaHeaders, String thetaHeaderDelimiter, String thetaValueDelimiter,
			String[] featureHeaders, String featureHeaderDelimiter, String featureValueDelimiter) {
		
		this.fdmm = new FDMM();

		this.thetaHeaders=thetaHeaders;
		this.thetaHeaderDelimiter = thetaHeaderDelimiter;
		this.thetaValueDelimiter=thetaValueDelimiter;
		
		Theta.setTHETA_HEADERS(thetaHeaders);
		Theta.setHEADER_DELIMITER(thetaHeaderDelimiter);
		Theta.setVALUE_DELIMITER(thetaValueDelimiter);
		
		this.featureHeaders = featureHeaders;
		this.featureHeaderDelimiter=featureHeaderDelimiter;
		this.featureValueDelimiter=featureValueDelimiter;
		
		Feature.setHEADER_DELIMITER(featureHeaderDelimiter);
		Feature.setVALUE_DELIMITER(featureValueDelimiter);
	}

	public AbstractFDMMTrainer() {
	}

	public FDMM getModel() {
		return fdmm;
	}
	
	public String[] getFeatureHeaders() {
		return featureHeaders;
	}
	public String getFeatureHeaderDelimiter(){
		return featureHeaderDelimiter;
	}
	public String getFeatureValueDelimiter(){
		return featureValueDelimiter;
	}
	
	public String[] getThetaHeaders(){
		return thetaHeaders;
	}
	public String getThetaHeaderDelimiter(){
		return thetaHeaderDelimiter;
	}
	public String getThetaValueDelimiter(){
		return thetaValueDelimiter;
	}
	
}