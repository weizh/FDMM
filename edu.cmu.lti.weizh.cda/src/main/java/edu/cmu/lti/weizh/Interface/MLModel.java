package edu.cmu.lti.weizh.Interface;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;


public abstract class MLModel{

  public abstract void store(String file) throws IOException;
}
