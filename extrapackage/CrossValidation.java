package extrapackage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by Jeremy on 2016-05-05.
 */
public class CrossValidation {

    private static class FoldPair {
        public String train;
        public String test;

        public FoldPair() {
            train = "";
            test = "";
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
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
                        folds[i].test += line + "\r\n";
                    } else {
                        folds[i].train += line + "\r\n";
                    }
                }
            }
        }
        scanner.close();

        for (FoldPair fold : folds) {
            // Call the KNN and NB methods here.
        }
    }

    private static int countLines(String str) {
        if(str == null || str.isEmpty())
        {
            return 0;
        }
        int lines = 1;
        int pos = 0;
        while ((pos = str.indexOf("\n", pos) + 1) != 0) {
            lines++;
        }
        return lines;
    }
}
