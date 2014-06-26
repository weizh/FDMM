package edu.cmu.lti.weizh.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import edu.gatech.gtri.bktree.BkTreeSearcher;
import edu.gatech.gtri.bktree.BkTreeSearcher.Match;
import edu.gatech.gtri.bktree.Metric;
import edu.gatech.gtri.bktree.MutableBkTree;
import gnu.trove.map.hash.TObjectIntHashMap;

public class BKTree {

	MutableBkTree<TreeNode> bkTree;
	BkTreeSearcher<TreeNode> searcher;
	Set<Match<? extends TreeNode>> matches;
	
	private int findMinimum(int a, int b, int c) {
		int min = a;
		if (b < min) {
			min = b;
		}
		if (c < min) {
			min = c;
		}
		return min;
	}

	public int LDistance(Object object1, Object object2) {

		String string1 = (String) object1;
		String string2 = (String) object2;

		int distance[][]; // distance matrix
		int n; // length of first string
		int m; // length of second string
		int i; // iterates through first string
		int j; // iterates through second string
		char s_i; // ith character of first string
		char t_j; // jth character of second string
		int cost; // cost

		// Step 1
		n = string1.length();
		m = string2.length();
		if (n == 0)
			return m;
		if (m == 0)
			return n;
		distance = new int[n + 1][m + 1];

		// Step 2
		for (i = 0; i <= n; i++)
			distance[i][0] = i;
		for (j = 0; j <= m; j++)
			distance[0][j] = j;

		// Step 3
		for (i = 1; i <= n; i++) {
			s_i = string1.charAt(i - 1);

			// Step 4
			for (j = 1; j <= m; j++) {
				t_j = string2.charAt(j - 1);

				// Step 5
				if (s_i == t_j)
					cost = 0;
				else
					cost = 1;

				// Step 6
				distance[i][j] = findMinimum(distance[i - 1][j] + 1, distance[i][j - 1] + 1, distance[i - 1][j - 1] + cost);
			}
		}
		// Step 7
		return distance[n][m];
	}

	public void init(String res) throws IOException {
		// The Hamming distance is a simple metric that counts the number
		// of positions on which the strings (of equal length) differ.
		Metric<TreeNode> LevDistance = new Metric<TreeNode>() {
			@Override
			public int distance(TreeNode x, TreeNode y) {
				return LDistance(x.s, y.s);

			}
		};

		bkTree = new MutableBkTree<TreeNode>(LevDistance);

		BufferedReader br = new BufferedReader(new FileReader(res));
		String line = "";
		int count = 0;
		while ((line = br.readLine()) != null) {
			count++;
			if (count % 1000000 == 0)
				System.err.println("loading gazetteer:" + count);
			String[] column = line.trim().split("\t");

			String utfname = column[1].toLowerCase();

			String altnames = column[3];

			String featureclass = column[6];
			String feature = column[7];
			String country = column[8];
			String population = column[14];
			long longpop;
			if (population == null)
				longpop = -1;
			longpop = Long.parseLong(population);

			if (longpop > 0 && longpop < 6200)
				continue;
			if (longpop == 0) {
				continue;
				// features.adjustOrPutValue(featureclass, 1, 1);
				// if (featureclass.equals("H") || featureclass.equals("L") ||
				// featureclass.equals("R") || featureclass.equals("S")
				// ||
				// featureclass.equals("T")||featureclass.equals("U")||featureclass.equals("V"))
				// continue;
			}
			// To Do: set values to document d, and index it

			String[] names = altnames.split(",");
			TreeNode treenode = new TreeNode(utfname, (names.length)*(longpop+1));
			for ( String name : names){
				if (name.length()>13)
					continue;
				name = name.trim().toLowerCase();
				TreeNode namenode = new TreeNode(name, (names.length)*(longpop));
				bkTree.add(namenode);
			}
			bkTree.add(treenode);
		}

		searcher = new BkTreeSearcher<TreeNode>(bkTree);

	}

	public static void main(String argv[]) throws IOException {
		BKTree bbt = new BKTree();
		bbt.init("GeoNames/allCountries.txt");

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String line;
		while ((line = br.readLine()) != null) {
			System.out.println(bbt.findBest(line).s);
		}

	}

	public TreeNode findBest(String query) {

		String q = query.trim().toLowerCase();
		matches = searcher.search(new TreeNode(q, 1), 2);

		ArrayList<TreeNode> amatches = new ArrayList<TreeNode>();
		for (Match<? extends TreeNode> match : matches) {
//			System.out.println(String.format("%s (distance %d), weight %f", match.getMatch().s, match.getDistance(),
//					match.getMatch().weight));
			if (match.getMatch().s.charAt(0) == q.charAt(0))
				amatches.add(match.getMatch());
		}
		Collections.sort(amatches);
		if (amatches.size() != 0){
//			System.out.println("The correction is: " + amatches.get(amatches.size() - 1).s);
			return amatches.get(amatches.size() - 1);}
		else
			return null;
			
		
	}

	public boolean inGaz(String query) {

		String q = query.trim().toLowerCase();
		matches = searcher.search(new TreeNode(q, 1), 0);
		if (matches.size() != 0)
			return true;
		else
			return false;
			
		
	}
}
