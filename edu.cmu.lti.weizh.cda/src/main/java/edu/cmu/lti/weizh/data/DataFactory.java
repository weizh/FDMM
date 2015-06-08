package edu.cmu.lti.weizh.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import edu.cmu.lti.weizh.data.conll2000.CONLL2kReader;
import edu.cmu.lti.weizh.data.conll2002.CONLL02Reader;
import edu.cmu.lti.weizh.data.nlpba.NLPBAReader;
import edu.cmu.lti.weizh.data.ontonotes.OntoNotesDataFiller;
import edu.cmu.lti.weizh.docmodel.DataSet;
import edu.cmu.lti.weizh.eval.EVAL_CONSTS;

public class DataFactory {


	public static DataSet getCONLL2kTrain() {
		DataSet data = new DataSet(1000, EVAL_CONSTS.CHUNK_TYPE);
		CONLL2kReader c2kreader;
		try {
			c2kreader = new CONLL2kReader((new FileReader(DATA_PATHS.CONLL2KTrain)));

			new CONLLFormatDataFiller(data, c2kreader).fill();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	public static DataSet getCONLL2kTest() {
		DataSet data = new DataSet(1000, EVAL_CONSTS.CHUNK_TYPE);
		CONLL2kReader c2kreader;
		try {
			c2kreader = new CONLL2kReader((new FileReader(DATA_PATHS.CONLL2KTest)));

			new CONLLFormatDataFiller(data, c2kreader).fill();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	public static DataSet getCONLL02NEDTrain() {
		DataSet data = new DataSet(1000, EVAL_CONSTS.NER_TYPE);
		CONLL02Reader c02reader;
		try {
			c02reader = new CONLL02Reader((new FileReader(DATA_PATHS.CONLL02GERMENYTrain)));

			new CONLLFormatDataFiller(data, c02reader).fill();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;

	}

	public static DataSet getCONLL02NEDTestA() {
		DataSet data = new DataSet(1000, EVAL_CONSTS.NER_TYPE);
		CONLL02Reader c02reader;
		try {
			c02reader = new CONLL02Reader((new FileReader(DATA_PATHS.CONLL02GERMENYTestA)));

			new CONLLFormatDataFiller(data, c02reader).fill();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;

	}

	public static DataSet getCONLL02NEDTestB() {
		DataSet data = new DataSet(1000, EVAL_CONSTS.NER_TYPE);
		CONLL02Reader c02reader;
		try {
			c02reader = new CONLL02Reader((new FileReader(DATA_PATHS.CONLL02GERMENYTestB)));

			new CONLLFormatDataFiller(data, c02reader).fill();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;

	}

	public static DataSet getCONLL02EspTrain() {
		DataSet data = new DataSet(1000, EVAL_CONSTS.NER_TYPE);
		CONLL02Reader c02reader;
		try {
			c02reader = new CONLL02Reader((new FileReader(DATA_PATHS.CONLL02SPANISHTrain)));

			new CONLLFormatDataFiller(data, c02reader).fill();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;

	}

	public static DataSet getCONLL02EspTestA() {
		DataSet data = new DataSet(1000, EVAL_CONSTS.NER_TYPE);
		CONLL02Reader c02reader;
		try {
			c02reader = new CONLL02Reader((new FileReader(DATA_PATHS.CONLL02SPANISHTestA)));

			new CONLLFormatDataFiller(data, c02reader).fill();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;


	}

	public static DataSet getCONLL02EspTestB() {
		DataSet data = new DataSet(1000, EVAL_CONSTS.NER_TYPE);
		CONLL02Reader c02reader;
		try {
			c02reader = new CONLL02Reader((new FileReader(DATA_PATHS.CONLL02SPANISHTestB)));

			new CONLLFormatDataFiller(data, c02reader).fill();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;


	}

	public static DataSet getNLPBATrain() {
		DataSet data = new DataSet(1000, EVAL_CONSTS.NER_TYPE);
		NLPBAReader nlpbareader;
		try {
			nlpbareader = new NLPBAReader((new FileReader(DATA_PATHS.NLPBATrain)));

			new CONLLFormatDataFiller(data, nlpbareader).fill();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;


	}

	public static DataSet getNLPBASampleTest() {
		DataSet data = new DataSet(1000, EVAL_CONSTS.NER_TYPE);
		NLPBAReader nlpbareader;
		try {
			nlpbareader = new NLPBAReader((new FileReader(DATA_PATHS.NLPBASampleTest)));

			new CONLLFormatDataFiller(data, nlpbareader).fill();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;

	}

	public static DataSet getNLPBAEval() {
		DataSet data = new DataSet(1000, EVAL_CONSTS.NER_TYPE);
		NLPBAReader nlpbareader;
		try {
			nlpbareader = new NLPBAReader((new FileReader(DATA_PATHS.NLPBAEval)));

			new CONLLFormatDataFiller(data, nlpbareader).fill();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * For file path, see DATA_PATHS.XXX.
	 * @param filePath
	 * @param collapseTag 
	 * @return
	 */
	public static DataSet getONFDataSet(String filePath, boolean collapseTag) {
		System.err.println("Loading" + filePath);
		DataSet fdad = new DataSet(19999, EVAL_CONSTS.NER_TYPE);
		try {
			new OntoNotesDataFiller(fdad).fill(new File(filePath),collapseTag);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.err.println( filePath + "loaded.");

		return fdad;
	}

}
