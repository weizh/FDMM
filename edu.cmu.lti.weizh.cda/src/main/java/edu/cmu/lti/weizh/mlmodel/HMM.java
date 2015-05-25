package edu.cmu.lti.weizh.mlmodel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import edu.cmu.lti.weizh.docmodel.Sentence;
import edu.cmu.lti.weizh.docmodel.Word;
import edu.cmu.lti.weizh.nlp.Stemmer;
import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.set.hash.TIntHashSet;

public class HMM extends MLModel {

	TObjectIntHashMap<String> DID, LID;
	TIntObjectHashMap<String> LID2LString;
	static int did, lid;
	public final static String StartSymbol = "[START_SYMBOL]", EndSymbol = "[END_SYMBOL]", Total = "[TOTAL]",
			StartPOS = "[START_POS]", EndPOS = "[END_POS]";

	// transition and emission probabilities.
	// token< <token,count>>
	TIntObjectHashMap<TIntIntHashMap> trans, emit;

	HMM() {
		this.DID = new TObjectIntHashMap<String>();
		this.LID = new TObjectIntHashMap<String>();
		did = lid = 0;
		LID.put(Total, lid++);
		DID.put(Total, did++);

		this.LID2LString = new TIntObjectHashMap<String>();

		this.trans = new TIntObjectHashMap<TIntIntHashMap>();
		this.emit = new TIntObjectHashMap<TIntIntHashMap>();
	}

	@Override
	public void store(String file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream out = new ObjectOutputStream(fos);
		out.writeObject(this);
		out.close();
	}

	public static HMM load(String file) throws IOException, ClassNotFoundException {
		FileInputStream in = new FileInputStream(file);
		ObjectInputStream ins = new ObjectInputStream(in);
		HMM copy = (HMM) ins.readObject();
		in.close();
		ins.close();
		return copy;
	}

	public void add(Sentence s, boolean lemmatized) {

		String prevw, curw, prevpos, curpos;
		for (int i = 0; i <= s.getWords().size(); i++) {
			if (i > 0 && i < s.getWords().size()) {
				prevw = s.getWords().get(i - 1).getTrimLowered();
				prevpos = s.getWords().get(i - 1).getPartOfSpeech();
				curw = s.getWords().get(i).getTrimLowered();
				curpos = s.getWords().get(i).getPartOfSpeech();
				if (lemmatized)
					curw = Stemmer.stemTerm(curw);
			} else if (i == 0) {
				prevw = StartSymbol;
				prevpos = StartPOS;
				curw = s.getWords().get(i).getTrimLowered();
				curpos = s.getWords().get(i).getPartOfSpeech();
				if (lemmatized)
					curw = Stemmer.stemTerm(curw);
			} else {
				prevw = s.getWords().get(i - 1).getTrimLowered();
				prevpos = s.getWords().get(i - 1).getPartOfSpeech();
				curw = EndSymbol;
				curpos = EndPOS;
				
			}

			addToModel(prevpos, curw, curpos);
		}
	}

	private void addToModel(String ppos, String cword, String cpos) {

		if (!DID.containsKey(cword))
			DID.put(cword, did++);

		if (!LID.containsKey(ppos)) {
			LID.put(ppos, lid);
			LID2LString.put(lid++, ppos);
		}

		if (!LID.containsKey(cpos)) {
			LID.put(cpos, lid);
			LID2LString.put(lid++, cpos);
		}

		int pposid = LID.get(ppos);
		int cwordid = DID.get(cword);
		int cposid = LID.get(cpos);
		int totalid = DID.get(Total);

		if (!trans.contains(pposid))
			trans.put(pposid, new TIntIntHashMap());
		trans.get(pposid).adjustOrPutValue(cposid, 1, 1);
		trans.get(pposid).adjustOrPutValue(totalid, 1, 1);

		if (!emit.contains(cposid))
			emit.put(cposid, new TIntIntHashMap());
		emit.get(cposid).adjustOrPutValue(cwordid, 1, 1);
		emit.get(cposid).adjustOrPutValue(totalid, 1, 1);
	}

	public String[] veterbiDecode(Sentence sent) {

		DecodingElement[][] matrix = new DecodingElement[LID.size()][sent.getWords().size() + 2];

		int startPOSId = LID.get(StartPOS);
		int totalid = LID.get(Total);

		matrix[startPOSId][0] = new DecodingElement().setLogValue(0).setPrevious(-1);

		for (int i = 1; i < sent.getWords().size() + 2; i++) {
			String cw;
			
			if (i == sent.getWords().size() + 1)
				cw = EndSymbol;
			else
				cw =  Stemmer.stemTerm(sent.getWords().get(i-1).getTrimLowered());

			System.out.println(cw);

			int cwid = -1;
			if (DID.contains(cw) == false)
				System.err.println("OOV!");
			else
				cwid = DID.get(cw);
			
			TIntIntHashMap poscand;
			if (emit.contains(cwid)) {
				poscand = emit.get(cwid);
			} else {// emit does not have current word first get a list of part
					// of speech
				poscand = new TIntIntHashMap();
				for (int j = 0; j < LID.size(); j++) {
					if (matrix[j][i - 1] == null)
						continue;
					if (trans.contains(j)) {
						TIntIntIterator iter = trans.get(j).iterator();
						while (iter.hasNext()) {
							iter.advance();
							poscand.put(iter.key(), 1);
						}
					}
				}
			}
			int emitpostotal =-1;
			if (poscand.contains(totalid))
				emitpostotal = poscand.get(totalid);
			
			TIntIntIterator iter = poscand.iterator();
			while (iter.hasNext()) { // loop over each POS for current word
				iter.advance();
				int cposid = iter.key();
				
				if (cposid ==totalid)continue;
				
				double cposprob = emitpostotal==-1? 1:(double) iter.value() / (double) emitpostotal;
				double tempmax = -99999;
				int templabel = -1;
				for (int j = 0; j < LID.size(); j++) {
					// loop over pos for each previous word
					if (matrix[j][i-1] == null)
						continue;
					if (trans.contains(j) && trans.get(j).contains(cposid)) {
						double transprob = trans.get(j).get(i) / trans.get(j).get(totalid);
						if (tempmax < transprob) {
							tempmax = transprob;
							templabel = j;
						}
					}
				}
				if (templabel != -1) {
					
					DecodingElement cell = new DecodingElement().setPrevious(templabel).setLogValue(
							matrix[templabel][i - 1].getLogValue() + Math.log(tempmax * cposprob));
					matrix[cposid][i] = cell;
				}
			}
		}

		int[] labels = new int[sent.getWords().size()];

		int worditer = sent.getWords().size() + 1;
		double maxval = Double.NEGATIVE_INFINITY;
		int label = -1;
		for (int i = 0; i < LID.size(); i++) {
			if (matrix[i][worditer] == null)
				continue;
			if (maxval < matrix[i][worditer].getLogValue()) {
				maxval = matrix[i][worditer].getLogValue();
				label = matrix[i][worditer].getPrevious();
			}
		}
		if (label == -1){
			System.err.println("Decoding error!");return null;}
		else
			labels[labels.length - 1] = label;

		int previous = label;

		for (worditer = sent.getWords().size(); worditer > 0; worditer--) {
			previous = matrix[previous][worditer].getPrevious();
			labels[worditer - 2] = previous;
		}

		String[] slabels = new String[sent.getWords().size()];
		for (int i = 0; i < labels.length; i++) {
			slabels[i] = LID2LString.get(labels[i]);
		}
		return slabels;
	}

	class DecodingElement {

		public double getLogValue() {
			return logValue;
		}

		public DecodingElement setLogValue(double logValue) {
			this.logValue = logValue;
			return this;
		}

		int previous;

		public int getPrevious() {
			return previous;
		}

		public DecodingElement setPrevious(int previous) {
			this.previous = previous;
			return this;
		}

		double logValue;
	}
}
