import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class MyProgram {

	public static void main(String[] args) throws FileNotFoundException {
		if (args.length < 2) {
			System.err.println(
					"Invalid usage. You need to give 3 command line arguments. Here are some examples:\n" +
					"$ java MyProgram pima.csv examples.csv NB\n" +
					"$ java MyProgram pima-CFS.csv examples.csv 4NN\n" +
					"$ java MyProgram cross-validate NB\n" +
					"Please do better next time.");
			System.exit(1);
		}
		if (args[0].equals("cross-validate")) {
			FoldPair[] folds = getFolds();
			int numCorrect = 0;
			int total = 0;
			for (FoldPair fold : folds) {
				ArrayList<String> output;
				if (args[1].matches("NB")) {
					output = (ArrayList<String>) naiveBayes(fold.train, fold.test, true);
				}
				else {
					int K = getK(args[1]);
					output = (ArrayList<String>) kNearestNeighbour(K, fold.train, fold.test, true);
				}

				// Check the accuracy of the prediction
				for (int i = 0; i < fold.testClasses.size(); i ++) {
					if (fold.testClasses.get(i).equals(output.get(i))) {
						numCorrect ++;
					}
					total ++;
				}
			}
			System.out.print("Accuracy: ");
			System.out.println(((double)numCorrect) / total);
		}
		else {
			String learn = readEntireFile(args[0]);
			String test = readEntireFile(args[1]);
			if (args[2].matches("NB")) {
				naiveBayes(learn, test, false);
			} else {
				int K = getK(args[2]);
				kNearestNeighbour(K, learn, test, false);
			}
		}
	}

	private static int getK(String knnString) {
		String kString = knnString.substring(0, 1);
		return Integer.parseInt(kString);
	}

	private static String readEntireFile(String fileName) throws FileNotFoundException {
		Scanner scanner = new Scanner(new File(fileName));
		// Use the start of the file as a delimiter.
		scanner.useDelimiter("\\A");
		String contents = scanner.next();
		return contents;
	}

	private static Object kNearestNeighbour(int k, String learn, String test, boolean returnResult) throws FileNotFoundException {
		double neighbours[][] = new double[k][2];
		Scanner testscan = new Scanner(test);
		testscan.useDelimiter(",|\r\n|\n");
		Scanner learnscan = new Scanner(learn);
		learnscan.useDelimiter(",|\r\n|\n");
		double curres;
		double currdist;
		double[] testline = new double[8];

		ArrayList<String> output = null;
		if (returnResult) {
			output = new ArrayList<String>();
		}

		// Populate an array list with all the training inputs
		ArrayList<double[]> learning = new ArrayList<double[]>();
		while (learnscan.hasNext()) {
			double[] learnline = new double[9];
			for (int i = 0; i < 8; ++i) {
				learnline[i] = Double.parseDouble(learnscan.next());
			}
			String res = learnscan.next();
			if (res.matches("yes")) {
				learnline[8] = 1;
			} else {
				learnline[8] = 0;
			}
			learning.add(learnline);
			
		}
		learnscan.close();

		//iterate the testing file
		while (testscan.hasNext()) {
			for (int i = 0; i < 8; ++i) {
				testline[i] = Double.parseDouble(testscan.next());
			}

			// the first k variables begin as the closest
			for (int i = 0; i < k; ++i) {
				double[] learnline = new double[9];
				learnline = learning.get(i);
				neighbours[i][1] = learnline[8];
				neighbours[i][0] = calcDistance(learnline, testline);
			}

			// iterate all other lines
			for (int j = k; j < learning.size(); ++j){
				currdist = calcDistance(learning.get(j), testline);
				curres = learning.get(j)[8];
				neighbours = newNeighbours(neighbours, currdist, curres, k);
			}

			// Print the result
			int counter = 0; // keeps track of the yes to no ratio
			for (int i = 0; i < k; ++i) {
				if (neighbours[i][1] == 1.0) {
					counter++;
				} else {
					counter--;
				}
			}
			if (counter >= 0) {
				System.out.println("yes");
				if (returnResult) {
					output.add("yes");
				}
			} else {
				System.out.println("no");
				if (returnResult) {
					output.add("no");
				}
			}

		}
		testscan.close();
		learnscan.close();

		if (returnResult) {
			return output;
		}
		return null;
	}

	private static double[][] newNeighbours(double[][] neighbours, double currdist, double curres, int k) {
		// find max dist
		int maxpos = 0;
		for (int i = 1; i < k; ++i) {
			if (neighbours[i][0] > neighbours[maxpos][0]) {
				maxpos = i;
			}
		}
		// replace if necessary
		if (neighbours[maxpos][0] > currdist) {
			neighbours[maxpos][0] = currdist;
			neighbours[maxpos][1] = curres;
		}
		return neighbours;
	}

	private static double calcDistance(double[] learnline, double[] testline) {
		double distance = 0;
		for (int i = 0; i < 8; ++i) {
			distance = distance + ((learnline[i] - testline[i])*(learnline[i]-testline[i]));
		}
		return Math.sqrt(distance);

	}

	private static Object naiveBayes(String learn, String test, boolean returnResult) throws FileNotFoundException {
		Scanner scanner = new Scanner(learn);
		scanner.useDelimiter(",|\r\n|\n");

		ArrayList<String> output = null;
		if (returnResult) {
			output = new ArrayList<String>();
		}

		// Finding the sum of each value, according to the result
		double[] meanyes = new double[8];
		double[] meanno = new double[8];
		double[] curr = new double[8];
		double[] sdyes = new double[8];
		double[] sdno = new double[8];
		int totalyes = 0;
		int totalno = 0;
		int total = 0;
		String result;
		while (scanner.hasNext()) {
			for (int i = 0; i < 8; ++i) {
				curr[i] = Double.parseDouble(scanner.next());
			}
			result = scanner.next();
			if (result.matches("yes")) {
				for (int i = 0; i < 8; ++i) {
					meanyes[i] = meanyes[i] + curr[i];
				}
				totalyes++;
			} else {
				for (int i = 0; i < 8; ++i) {
					meanno[i] = meanno[i] + curr[i];
				}
				totalno++;
			}
			total++;
		}

		// Calculate means
		for (int i = 0; i < 8; ++i) {
			meanyes[i] = meanyes[i] / totalyes;
			meanno[i] = meanno[i] / totalno;
		}
		scanner = new Scanner(learn);
		scanner.useDelimiter(",|\r\n|\n");

		// Standard deviation
		while (scanner.hasNext()) {
			for (int i = 0; i < 8; ++i) {
				curr[i] = Double.parseDouble(scanner.next());
			}
			result = scanner.next();
			if (result.matches("yes")) {
				for (int i = 0; i < 8; ++i) {
					sdyes[i] = sdyes[i] + Math.pow((curr[i] - meanyes[i]), 2);
				}
			} else {
				for (int i = 0; i < 8; ++i) {
					sdno[i] = sdno[i] + Math.pow((curr[i] - meanno[i]), 2);
				}
			}
		}
		for (int i = 0; i < 8; ++i) {
			sdyes[i] = Math.sqrt(sdyes[i] / (totalyes - 1));
			sdno[i] = Math.sqrt(sdno[i] / (totalno - 1));

		}

		// Testing time baby
		scanner = new Scanner(test);
		scanner.useDelimiter(",|\r\n|\n");
		double Pyes = (double) ((double) totalyes / (double) total);
		double Pno = 1 - Pyes;
		double yesvalue;
		double novalue;
		double currtestval;
		while (scanner.hasNext()) {
			yesvalue = Pyes;
			novalue = Pno;
			for (int i = 0; i < 8; ++i) {
				currtestval = Double.parseDouble(scanner.next());
				yesvalue = yesvalue * probDensityFunc(currtestval, sdyes[i], meanyes[i]);
				novalue = novalue * probDensityFunc(currtestval, sdno[i], meanno[i]);
			}
			if (novalue > yesvalue) {
				System.out.println("no");
				if (returnResult) {
					output.add("no");
				}
			} else {
				System.out.println("yes");
				if (returnResult) {
					output.add("yes");
				}
			}
		}
		scanner.close();

		if (returnResult) {
			return output;
		}
		return null;
	}

	private static double probDensityFunc(double currtestval, double sd, double mean) {
		double power = (((currtestval - mean) * (currtestval - mean)) / (2 * sd * sd));
		double frac = 1 / (sd * Math.sqrt(2 * Math.PI));
		double result = frac * Math.pow(Math.E, -power);
		return result;
	}

	// CROSS VALIDATION

	public static class FoldPair {
		public String train;
		public String test;
		public ArrayList<String> testClasses;

		public FoldPair() {
			train = "";
			test = "";
			testClasses = new  ArrayList<String>();
		}
	}

	public static FoldPair[] getFolds() throws FileNotFoundException {
		final int numFolds = 10;
		FoldPair folds[] = new FoldPair[numFolds];
		for (int i = 0; i < numFolds; i ++) {
			folds[i] = new FoldPair();
		}

		Scanner scanner = new Scanner(new File("pima-folds.csv"));

		int curFold = -1;
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.isEmpty()) {
				continue;
			}
			else if (line.startsWith("fold")) {
				// The folds are 1-based indexed, whereas the array is 0-based. Hence we need the - 1.
				curFold = Integer.parseInt(line.substring(4)) - 1;
			}
			else {
				for (int i = 0; i < numFolds; i++) {
					if (i == curFold) {
						int commaIndex = line.lastIndexOf(',');
						folds[i].test += line.substring(0, commaIndex) + "\r\n";
						folds[i].testClasses.add(line.substring(commaIndex+1));
					} else {
						folds[i].train += line + "\r\n";
					}
				}
			}
		}
		scanner.close();

		return folds;
	}
}
