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
		Scanner learnscan = new Scanner(new File(learn));
        learnscan.useDelimiter(",");
        
        //Finding the sum of each value, according to the result
        double[] meanyes = new double[8];
        double[] meanno = new double[8];
        double[] curr = new double[8];
        double[] sdyes = new double[8];
        double[] sdno = new double[8];
        int totalyes = 0;
        int totalno = 0;
        String result;
        while (learnscan.hasNext()){
        	for (int i = 0; i < 8; ++i){
        		curr[i] = Double.parseDouble(learnscan.next());
        	}
        	result = learnscan.next();
        	if (result.matches("yes")){
        		for (int i = 0; i < 8; ++i){
        			meanyes[i] = meanyes[i] + curr[i];
        			totalyes++;
        		}
        	} else {
        		for (int i = 0; i < 8; ++i){
        			meanno[i] = meanno[i] + curr[i];
        			totalno++;
        		}
        	}
        }
        
        //Calculate means
        for (int i = 0; i < 8; ++i){
        	meanyes[i] = meanyes[i]/totalyes;
        	meanno[i] = meanno[i]/totalno;
        }
        learnscan.close();
        learnscan = new Scanner(new File(learn));
        learnscan.useDelimiter(",");
        
        //Standard deviation
        while (learnscan.hasNext()){
        	for (int i = 0; i < 8; ++i){
        		curr[i] = Double.parseDouble(learnscan.next());
        	}
        	result = learnscan.next();
        	if (result.matches("yes")){
        		for (int i = 0; i < 8; ++i){
        			sdyes[i] = Math.pow((curr[i] - meanyes[i]), 2);
        			totalyes++;
        		}
        	} else {
        		for (int i = 0; i < 8; ++i){
        			sdno[i] = Math.pow((curr[i] - meanno[i]), 2);
        			totalno++;
        		}
        	}
        }  
        for (int i = 0; i < 8; ++i){
        	sdyes[i] = Math.sqrt(sdyes[i]/(totalyes-1));
        	sdno[i] = Math.sqrt(sdno[i]/(totalno-1));
        }
        learnscan.close();
        

        
        //Testing time baby
        Scanner testscan = new Scanner(new File(test));
        testscan.useDelimiter(",");
        //Probability of yes or no
        double Pyes = totalyes/(totalyes+totalno);
        double Pno = 1-Pyes;
        double yesvalue;
        double novalue;
        double currtestval;
        while (testscan.hasNext()){
        	yesvalue = 1;
        	novalue = 1;
        	for(int i = 0; i < 8; ++i){
        		currtestval = Double.parseDouble(testscan.next());
        		yesvalue = yesvalue * helpfulFunction(currtestval, sdyes[i], meanyes[i]);
        		novalue = novalue * helpfulFunction(currtestval, sdno[i], meanno[i]);
        	}
        	yesvalue = yesvalue * Pyes;
        	novalue = novalue * Pno;
        	if (novalue > yesvalue){
        		System.out.println("no");
        	} else {
        		System.out.println("yes");
        	}
        }
        testscan.close();
       
		
	}

	private static double helpfulFunction(double currtestval, double sd, double mean) {
		return (1/(sd*Math.sqrt(2*Math.PI))*Math.pow(Math.E, (((currtestval-mean)*(currtestval-mean))/ 2*sd*sd)));
	}
}
