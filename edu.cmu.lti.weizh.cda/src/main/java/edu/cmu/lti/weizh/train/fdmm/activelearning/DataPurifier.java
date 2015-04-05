package edu.cmu.lti.weizh.train.fdmm.activelearning;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DataPurifier {

	public static void main(String argv[]) throws IOException {

		String filepath = "/home/wei/Documents/activeLearning/completeData/";
		String fileName = "april";
		String phrase = fileName.toLowerCase();

		BufferedReader br = new BufferedReader(new FileReader(new File(filepath + fileName)));
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filepath +"/csv/files/"+ fileName + "-Readable.csv")));
		String line = null;
		int lineNo = -1;
		StringBuffer outString = new StringBuffer();
		while ((line = br.readLine()) != null) {	
			
			lineNo++;
			line = line.replaceAll("\t", " ");
			
			if (lineNo % 2 == 0) {
				outString.append(phrase).append("\t").append(" ").append("\t");
				outString.append(line).append("\t");
			} else {
				String lowered = line.toLowerCase();
				outString.append(line).append("\n");
				if (lowered.contains(phrase+" ")|| lowered.contains(" "+phrase)) {
					bw.write(outString.toString());
				}
				outString = new StringBuffer();
			}
		}
		
		bw.close();
	}

}
