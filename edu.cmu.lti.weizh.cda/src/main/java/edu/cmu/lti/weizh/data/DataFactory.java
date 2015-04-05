package edu.cmu.lti.weizh.data;

import java.io.FileNotFoundException;
import java.io.FileReader;

import edu.cmu.lti.weizh.data.conll2000.CONLL2kReader;
import edu.cmu.lti.weizh.eval.EVAL_CONSTS;

public class DataFactory {

	public CONLLFormatDataSet getCONLL2kTrain() {
		CONLLFormatDataSet data = new CONLLFormatDataSet(1000, EVAL_CONSTS.CHUNK_TYPE);
		CONLL2kReader c2kreader;
		try {
			c2kreader = new CONLL2kReader((new FileReader(DATA_PATHS.CONLL2KTrain)));

			new CONLLFormatDataFiller(data, c2kreader).fill();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}

	public CONLLFormatDataSet getCONLL2kTest() throws FileNotFoundException {
		CONLLFormatDataSet data = new CONLLFormatDataSet(1000, EVAL_CONSTS.CHUNK_TYPE);
		CONLL2kReader c2kreader;
		try {
			c2kreader = new CONLL2kReader((new FileReader(DATA_PATHS.CONLL2KTest)));

			new CONLLFormatDataFiller(data, c2kreader).fill();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}

	public CONLLFormatDataSet getCONLL02NEDTrain() {
		CONLLFormatDataSet data = new CONLLFormatDataSet(1000, EVAL_CONSTS.NER_TYPE);
		CONLL2kReader c2kreader;
		try {
			c2kreader = new CONLL2kReader((new FileReader(DATA_PATHS.CONLL02GERMENYTrain)));

			new CONLLFormatDataFiller(data, c2kreader).fill();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;

	}

	public CONLLFormatDataSet getCONLL02NEDTestA() {
		CONLLFormatDataSet data = new CONLLFormatDataSet(1000, EVAL_CONSTS.NER_TYPE);
		CONLL2kReader c2kreader;
		try {
			c2kreader = new CONLL2kReader((new FileReader(DATA_PATHS.CONLL02GERMENYTestA)));

			new CONLLFormatDataFiller(data, c2kreader).fill();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;

	}

	public CONLLFormatDataSet getCONLL02NEDTestB() {
		CONLLFormatDataSet data = new CONLLFormatDataSet(1000, EVAL_CONSTS.NER_TYPE);
		CONLL2kReader c2kreader;
		try {
			c2kreader = new CONLL2kReader((new FileReader(DATA_PATHS.CONLL02GERMENYTestB)));

			new CONLLFormatDataFiller(data, c2kreader).fill();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;

	}

	public CONLLFormatDataSet getCONLL02EspTrain() {
		CONLLFormatDataSet data = new CONLLFormatDataSet(1000, EVAL_CONSTS.NER_TYPE);
		CONLL2kReader c2kreader;
		try {
			c2kreader = new CONLL2kReader((new FileReader(DATA_PATHS.CONLL02SPANISHTrain)));

			new CONLLFormatDataFiller(data, c2kreader).fill();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;

	}

	public CONLLFormatDataSet getCONLL02EspTestA() {
		CONLLFormatDataSet data = new CONLLFormatDataSet(1000, EVAL_CONSTS.NER_TYPE);
		CONLL2kReader c2kreader;
		try {
			c2kreader = new CONLL2kReader((new FileReader(DATA_PATHS.CONLL02SPANISHTestA)));

			new CONLLFormatDataFiller(data, c2kreader).fill();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;


	}

	public CONLLFormatDataSet getCONLL02EspTestB() {
		CONLLFormatDataSet data = new CONLLFormatDataSet(1000, EVAL_CONSTS.NER_TYPE);
		CONLL2kReader c2kreader;
		try {
			c2kreader = new CONLL2kReader((new FileReader(DATA_PATHS.CONLL02SPANISHTestB)));

			new CONLLFormatDataFiller(data, c2kreader).fill();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;


	}

	public CONLLFormatDataSet getNLPBATrain() {
		CONLLFormatDataSet data = new CONLLFormatDataSet(1000, EVAL_CONSTS.NER_TYPE);
		CONLL2kReader c2kreader;
		try {
			c2kreader = new CONLL2kReader((new FileReader(DATA_PATHS.NLPBATrain)));

			new CONLLFormatDataFiller(data, c2kreader).fill();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;


	}

	public CONLLFormatDataSet getNLPBASampleTest() {
		CONLLFormatDataSet data = new CONLLFormatDataSet(1000, EVAL_CONSTS.NER_TYPE);
		CONLL2kReader c2kreader;
		try {
			c2kreader = new CONLL2kReader((new FileReader(DATA_PATHS.NLPBASampleTest)));

			new CONLLFormatDataFiller(data, c2kreader).fill();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;

	}

	public CONLLFormatDataSet getNLPBAEval() {
		CONLLFormatDataSet data = new CONLLFormatDataSet(1000, EVAL_CONSTS.NER_TYPE);
		CONLL2kReader c2kreader;
		try {
			c2kreader = new CONLL2kReader((new FileReader(DATA_PATHS.NLPBAEval)));

			new CONLLFormatDataFiller(data, c2kreader).fill();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;


	}

}
