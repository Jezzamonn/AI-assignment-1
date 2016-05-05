import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MyProgram {

	public static void main(String[] args) throws FileNotFoundException {
		if (args[2].matches("NB")) {
			naiveBayes(args[0], args[1]);
		} else {
			int K = Integer.parseInt(args[2].substring(0, 1)); // 2
																					// digit
																					// K
			kNearestNeighbour(K, args[0], args[1]);
		}
	}

	private static void kNearestNeighbour(int k, String learn, String test) throws FileNotFoundException {
		double neighbours[][] = new double[k][2];
		// File learnfile = new File(learn);
		Scanner testscan = new Scanner(new File(test));
		testscan.useDelimiter(",|\r\n");
		Scanner learnscan = null;
		String curres = null;
		double result;
		double currdist;
		double[] learnline = new double[8];
		double[] testline = new double[8];
		while (testscan.hasNextLine()) {
			for (int i = 0; i < 8; ++i) {
				testline[i] = Double.parseDouble(testscan.next());
			}
			learnscan = new Scanner(new File(learn));
			learnscan.useDelimiter(",|\r\n");

			// the first k variables begin as the closest
			for (int i = 0; i < k; ++i) {
				for (int j = 0; j < 8; ++j) {
					learnline[j] = Double.parseDouble(learnscan.next());
				}
				String lres = learnscan.next();
				if (lres.matches("yes")) {
					result = 1;
				} else {
					result = 0;
				}
				neighbours[i][1] = result;
				neighbours[i][0] = calcDistance(learnline, testline);
			}
			
			// iterate all other lines
			while (learnscan.hasNextLine()) {
				for (int i = 0; i < 8; ++i) {
					
						learnline[i] = Double.parseDouble(learnscan.next());
					
				}
					currdist = calcDistance(learnline, testline);

						curres = learnscan.next();
						// does nothing if no new neighbours, severe time
						// wasting atm
						neighbours = newNeighbours(neighbours, currdist, curres, k);
					

				}
				// Print the result
				int counter = 0; // keeps track of the yes to no ratio
				for (int i = 0; i < k; ++i) {
					if (neighbours[i][1] == 1) {
						counter++;
					} else {
						counter--;
					}
				}
				if (counter >= 0) {
					System.out.println("yes");
				} else {
					System.out.println("no");
				}

			}
			testscan.close();
			learnscan.close();
		}



	private static double[][] newNeighbours(double[][] neighbours, double currdist, String curres, int k) {
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
			if (curres.matches("yes")) {
				neighbours[maxpos][1] = 1;
			} else {
				neighbours[maxpos][1] = 0;
			}
		}
		return neighbours;
	}

	private static double calcDistance(double[] learnline, double[] testline) {
		double distance = 0;
		for (int i = 0; i < 8; ++i) {
			distance = distance + Math.abs((learnline[i] - testline[i]));
		}
		return distance;

	}

	private static void naiveBayes(String learn, String test) throws FileNotFoundException {
		Scanner scanner = new Scanner(new File(learn));
		scanner.useDelimiter(",|\r\n");

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
		scanner = new Scanner(new File(learn));
		scanner.useDelimiter(",|\r\n");

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
		scanner = new Scanner(new File(test));
		scanner.useDelimiter(",|\r\n");
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
			} else {
				System.out.println("yes");
			}
		}
		scanner.close();
	}

	private static double probDensityFunc(double currtestval, double sd, double mean) {
		double power = (((currtestval - mean) * (currtestval - mean)) / (2 * sd * sd));
		double frac = 1 / (sd * Math.sqrt(2 * Math.PI));
		double result = frac * Math.pow(Math.E, -power);
		return result;
	}
}
