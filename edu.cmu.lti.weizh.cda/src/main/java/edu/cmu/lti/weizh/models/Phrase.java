package edu.cmu.lti.weizh.models;

public class Phrase {

	Sentence sent;

	int start, end;

	public Phrase(){
		
	}
	public Phrase(Sentence sent, int start, int end){
		this.sent=sent;
		this.start=start;
		this.end=end;
	}
	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public int setStart(int s) {
		this.start = s;
		return start;
	}

	public int getEnd(int e) {
		this.end = e;
		return end;
	}

	public String getLemma() {
		if (start == end)
			return sent.getWords().get(start).getLemma().toString();
		else if (start == end - 1)
			return sent.getWords().get(start).getLemma() + '-' + sent.getWords().get(end).getLemma();
		else if (start == end - 2)
			return sent.getWords().get(start).getLemma() + '-' + sent.getWords().get(start + 1).getLemma() + '-'
					+ sent.getWords().get(end).getLemma();
		else {
			StringBuilder sb = new StringBuilder(sent.getWords().get(start).getLemma());
			for (int i = start + 1; i <= end; i++)
				sb.append("-").append(sent.getWords().get(i).getLemma());
			return sb.toString();
		}
	}

	public String getPOS() {
		if (start == end)
			return sent.getWords().get(start).getPOS();
		else if (start + 1 == end)
			return sent.getWords().get(start).getPOS() + '-' + sent.getWords().get(end).getPOS();
		else if (start + 2 == end)
			return sent.getWords().get(start).getPOS() + '-' + sent.getWords().get(start + 1).getPOS() + '-'
					+ sent.getWords().get(end).getPOS();
		else {
			StringBuilder pos = new StringBuilder(sent.getWords().get(start).getPOS());
			for (int s = start + 1; s <= end; s++)
				pos.append('-').append(sent.getWords().get(s).getPOS());
			return pos.toString();
		}
	}
}
