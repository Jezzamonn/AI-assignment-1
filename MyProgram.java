import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MyProgram {

	public static void main (String[] args) throws FileNotFoundException{
		if (args[2].matches("NB")) {
			naiveBayes(args[0], args[1]);
		} else {
			int K = Integer.parseInt(args[2].substring(0, args[2].length()-2)); //facilitates a two digit K
			kNearestNeighbour(K, args[0], args[1]);
		}
	}

	private static void kNearestNeighbour(int k, String learn, String test) {
		// TODO Auto-generated method stub
		
	}

	private static void naiveBayes(String learn, String test) throws FileNotFoundException {
		Scanner scanner = new Scanner(new File(learn));
        scanner.useDelimiter(",|\r\n");
        
        //Finding the sum of each value, according to the result
        double[] meanyes = new double[8];
        double[] meanno = new double[8];
        double[] curr = new double[8];
        double[] sdyes = new double[8];
        double[] sdno = new double[8];
        int totalyes = 0;
        int totalno = 0;
		int total = 0;
        String result;
        while (scanner.hasNext()){
        	for (int i = 0; i < 8; ++i){
        		curr[i] = Double.parseDouble(scanner.next());
        	}
        	result = scanner.next();
        	if (result.matches("yes")){
        		for (int i = 0; i < 8; ++i){
        			meanyes[i] = meanyes[i] + curr[i];
        		}
        		totalyes++;
        	} else {
        		for (int i = 0; i < 8; ++i){
        			meanno[i] = meanno[i] + curr[i];
        		}
        		totalno++;
        	}
        	total++;
        }
        
        //Calculate means
        for (int i = 0; i < 8; ++i){
        	meanyes[i] = meanyes[i]/totalyes;
        	meanno[i] = meanno[i]/totalno;
        }
        
        scanner = new Scanner(new File(learn));
        scanner.useDelimiter(",|\r\n");
        
        //Standard deviation
        while (scanner.hasNext()){
        	for (int i = 0; i < 8; ++i){
        		curr[i] = Double.parseDouble(scanner.next());
        	}
        	result = scanner.next();
        	if (result.matches("yes")){
        		for (int i = 0; i < 8; ++i){
        			sdyes[i] = Math.pow((curr[i] - meanyes[i]), 2);
        		}
        	} else {
        		for (int i = 0; i < 8; ++i){
        			sdno[i] = Math.pow((curr[i] - meanno[i]), 2);
        		}
        	}
        }  
        for (int i = 0; i < 8; ++i){
        	sdyes[i] = Math.sqrt(sdyes[i]/(totalyes-1));
        	sdno[i] = Math.sqrt(sdno[i]/(totalno-1));
        	
        }
        
        //Testing time baby
        scanner = new Scanner(new File(test));
        scanner.useDelimiter(",|\r\n");
        double Pyes = (double) ((double) totalyes/(double) total);
        double Pno = 1-Pyes;
        double yesvalue;
        double novalue;
        double currtestval;
        while (scanner.hasNext()){
        	yesvalue = Pyes;
        	novalue = Pno;
        	for(int i = 0; i < 8; ++i){
        		currtestval = Double.parseDouble(scanner.next());
        		yesvalue = yesvalue * probDensityFunc(currtestval, sdyes[i], meanyes[i]);
        		novalue = novalue * probDensityFunc(currtestval, sdno[i], meanno[i]); 
        	}
        	if (novalue > yesvalue){
        		System.out.println("no");
        	} else {
        		System.out.println("yes");

        	}
        }
        scanner.close();
       
		
	}

	//currently underflowing, woo!
	private static double probDensityFunc(double currtestval, double sd, double mean) {
		double power = (((currtestval-mean)*(currtestval-mean))/ (2*sd*sd));
		double frac = 1/(sd*Math.sqrt(2*Math.PI));
		double result = frac * Math.pow(Math.E, -power);
		System.out.println(result);
		return result;
		
	}
}
