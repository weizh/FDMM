package edu.cmu.lti.weizh.models;

public class Phrase {

  int start, end;
  
  public int getStart(){
    return start;
  }
  public int getEnd(){
    return end;
  }
  
  public int setStart(int s){
    this.start=s;
    return start;
  }
  public int getEnd(int e){
    this.end=e;
    return end;
  }
}
