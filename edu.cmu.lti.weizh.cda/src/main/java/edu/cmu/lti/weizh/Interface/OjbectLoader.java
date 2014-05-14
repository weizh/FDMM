package edu.cmu.lti.weizh.Interface;

import java.io.IOException;

public interface OjbectLoader<T> {

  public T load(String s ) throws IOException, ClassNotFoundException;
}
