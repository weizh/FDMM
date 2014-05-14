package edu.cmu.lti.weizh.models;

public class NamedEntity extends Phrase {

	String entityType;

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String ty) {
		this.entityType	 = ty;
	}

	public NamedEntity(String type, int start, int end, Sentence sent) {
		this.start = start;
		this.end = end;
		this.entityType= type;
		this.sent = sent;
	}
	
	public String toString() {
		return "id:" + entityType + "start:" + start + " end:" + end;
	}

	public String getCap() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLemma() {
		// TODO Auto-generated method stub
		return super.getLemma();
	}
}
