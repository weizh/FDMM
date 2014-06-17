package edu.cmu.lti.weizh.utils;

public class TreeNode implements Comparable{
	public TreeNode(String utfname, long l) {
		// TODO Auto-generated constructor stub
		this.s=utfname;
		this.weight = l;
	}
	public double weight;
	public String s;
	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		if(arg0 instanceof TreeNode){
			return Double.compare(this.weight, ((TreeNode)arg0).weight);
		}
		return 0;
	}
	
}
