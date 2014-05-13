package edu.cmu.lti.weizh.models;


public class NamedEntity extends Phrase{

  int entityid;
  public NamedEntity(String type, int start, int end){
    this.start=start;
    this.end = end;
    this.entityid = Global.E2IDMap.get(type);
  }
}
