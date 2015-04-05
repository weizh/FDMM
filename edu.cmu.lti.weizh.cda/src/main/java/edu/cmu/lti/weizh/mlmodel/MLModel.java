package edu.cmu.lti.weizh.mlmodel;

import java.io.IOException;
import java.io.Serializable;

public abstract class MLModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public abstract void store(String file) throws IOException;
}
