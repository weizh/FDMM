package edu.cmu.lti.weizh.Interface;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public abstract class MLModel implements Serializable{

  public abstract void store(String file) throws IOException;
}
