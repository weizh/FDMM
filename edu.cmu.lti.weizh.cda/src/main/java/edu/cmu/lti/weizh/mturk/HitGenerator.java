package edu.cmu.lti.weizh.mturk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

import edu.cmu.lti.weizh.data.ontonotes.OntoNotesDataFiller;
import edu.cmu.lti.weizh.data.ontonotes.OntonotesDataSet;
import edu.cmu.lti.weizh.docmodel.Document;
import edu.cmu.lti.weizh.docmodel.NamedEntity;
import edu.cmu.lti.weizh.docmodel.Paragraph;
import edu.cmu.lti.weizh.docmodel.Sentence;
import edu.cmu.lti.weizh.docmodel.Word;
import edu.cmu.lti.weizh.eval.EVAL_CONSTS;
import edu.cmu.lti.weizh.feature.ONF_CONSTS;
import edu.cmu.lti.weizh.mlmodel.FDMM;
import edu.cmu.lti.weizh.nlp.Stemmer;
import edu.cmu.lti.weizh.train.Evaluatable;

public class HitGenerator {

	public static void main(String arv[]) throws Exception {

		OntonotesDataSet onf = new OntonotesDataSet(199999, EVAL_CONSTS.NER_TYPE);
		String path = "C:/Users/Wynn/Documents/wynnzh/data/LDC/data/english/annotations/";

		System.out.println("loading data:");
		new OntoNotesDataFiller(onf).fill(new File(path));

		// onf.fillCVFolds("10-crf-split.txt");

		generateMockupFiles(onf);

	}

	public static void generateMockupFiles(OntonotesDataSet fdadata) throws Exception {

		BufferedReader r1 = new BufferedReader(new FileReader("mturk-template1.txt"));
		BufferedReader r2 = new BufferedReader(new FileReader("mturk-template2.txt"));
		String line = "";
		StringBuilder text1 = new StringBuilder(), text2 = new StringBuilder();
		while ((line = r1.readLine()) != null)
			text1.append(line);
		while ((line = r2.readLine()) != null)
			text2.append(line);

		int sentcount = 0;
		StringBuilder text, sure, nsure;
		BufferedWriter bw;

		/*
		 * populate the dataset into the model.
		 */
		for (Document doc : fdadata.getDocuments()) {
			System.out.println("processing doc " + doc.getDocId());
			for (Paragraph para : doc.getParagraphs()) {

				int content = 0;

				StringBuilder tables = new StringBuilder();
				for (Sentence sent : para.getSentences()) {
					if (sent.getNamedEntities().size() == 0)
						continue;
					boolean b = false;
//					for (NamedEntity n : sent.getNamedEntities()) {
//						if (n.getEntityType().equals("FAC"))
//							b = true;
//					}
					if (sent.getPlainSentence().toLowerCase().contains("street")||
							sent.getPlainSentence().toLowerCase().contains("lake")||
							sent.getPlainSentence().toLowerCase().contains("province")||
							sent.getPlainSentence().toLowerCase().contains("state")||
							sent.getPlainSentence().toLowerCase().contains("neighborhood")||
							sent.getPlainSentence().toLowerCase().contains("hill")||
							sent.getPlainSentence().toLowerCase().contains("wheat fields")||
							sent.getPlainSentence().toLowerCase().contains("ponds")||
							sent.getPlainSentence().toLowerCase().contains("pond")||
							sent.getPlainSentence().toLowerCase().contains("lake")||
							sent.getPlainSentence().toLowerCase().contains("lakes")||
							sent.getPlainSentence().toLowerCase().contains("streets")||
							sent.getPlainSentence().toLowerCase().contains("store")||
							sent.getPlainSentence().toLowerCase().contains("restaurant")||
							sent.getPlainSentence().toLowerCase().contains("diner")||
							sent.getPlainSentence().toLowerCase().contains("woods")||
							sent.getPlainSentence().toLowerCase().contains("stadium")||
							sent.getPlainSentence().toLowerCase().contains("hall")||
							sent.getPlainSentence().toLowerCase().contains("park")||
							sent.getPlainSentence().toLowerCase().contains("mountain")||
							sent.getPlainSentence().toLowerCase().contains("apartment")||
							sent.getPlainSentence().toLowerCase().contains("plaza")||
							sent.getPlainSentence().toLowerCase().contains("place")||
							sent.getPlainSentence().toLowerCase().contains("palace")||
							sent.getPlainSentence().toLowerCase().contains("parkway")||
							sent.getPlainSentence().toLowerCase().contains("mall")||
							sent.getPlainSentence().toLowerCase().contains("town center")||
							sent.getPlainSentence().toLowerCase().contains("state capitol")||
							sent.getPlainSentence().toLowerCase().contains("arena")||
							sent.getPlainSentence().toLowerCase().contains("road")||
							sent.getPlainSentence().toLowerCase().contains("blvd")||
							sent.getPlainSentence().toLowerCase().contains("highway"))
						b=true;
					if (b == false)
						continue;

					if (sent.getWords().size() < 10)
						continue;

					if (sentcount % 10 != 0) {
						sentcount++;
						continue;
					}

					content++;

					text = new StringBuilder().append("<table align=\"center\" style=\"width:1000px\">").append("<tbody>")
							.append("<tr>").append("<td align=\"center\">&nbsp;</td>");
					sure = new StringBuilder().append("<tr>").append("<td align=\"center\">Sure:</td>");
					nsure = new StringBuilder().append("<tr>").append("<td align=\"center\">Not Sure:</td>");

					List<Word> words = sent.getWords();
					int i = 0;
					for (Word word : words) {
						if (word.getWord().startsWith("*"))
							continue;
						text.append("<td align=\"center\">").append(word.getWord()).append("</td>");
						sure.append("<td align=\"center\"><label><input name=\"QAnswer\" type=\"checkbox\" value=\"s").append(i)
								.append("\" /></label></td>");
						nsure.append("<td align=\"center\"><label><input name=\"QAnswer\" type=\"checkbox\" value=\"u").append(i)
								.append("\" /></label></td>");
						i++;
					}
					text.append("</tr>");
					sure.append("</tr>");
					nsure.append("</tr>").append("</tbody>").append("</table>");
					/*
					 * get the named ent.ities.
					 */
					// List<NamedEntity> nes = sent.getNamedEntities();

					tables.append(text.toString());
					tables.append(sure.toString());
					tables.append(nsure.toString());
					sentcount++;
				}

				if (content > 0) {
					// System.out.println(sent.getPlainSentence());
					String[] s = doc.getDocId().split("\\\\");
					bw = new BufferedWriter(new FileWriter("html/" + s[s.length - 1] + ".html"));
					System.out.println("html/" + s[s.length - 1] + ".html");

					bw.write(text1.toString());
					bw.write(tables.toString());
					bw.write(text2.toString());
					bw.close();
				}

			}
		}
	}

}
