import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.StringTokenizer;
 
public class VelocityCheck {

    public static final int ROWCOUNT = 383785; // 1138006 Star, 931505 Dark, 383785 Gas
    public static final int COLCOUNT = 7;
    public static final double INCREMENT = .5;
    public static final int DISTANCE = 10;
    public static final int BINCOUNT = (int) (DISTANCE/INCREMENT);
    public static void main(String[] args) throws Exception {
 
        double [] [] matrixInitial = makeMatrix(args[0]);
        double [] [] scaledMatrix = scaleMatrix(matrixInitial);
        double [] [] allData = addColumnRadiusVelocity(scaledMatrix);
        // double [] [] display = addColumnRadiusVelocity(scaledMatrix);
        double [] [] sumVelocitiesMatrix = sumVelocities(allData);
        display(sumVelocitiesMatrix);
    }
 
    public static void display (double [] [] G) {
		for (int i = 0; i < G.length; i++) {
			for (int j = 0; j < G[0].length; j++) {
				System.out.print("\t" + G[i][j]);
			}
            System.out.println();
		}
		System.out.println();
    }
    public static double [] [] makeMatrix (String dataFileName) throws Exception {
        double [][] matrix = new double [ROWCOUNT][COLCOUNT]; //plug in values corresponding to the file Prof. Keres gave me
        String line = null;	
        int row = 0;	
        int col = 0; 	
     
        BufferedReader reader  = new BufferedReader(new FileReader(dataFileName)); //read each line of text file
        while((line = reader.readLine()) != null && row < matrix.length) {
            StringTokenizer st = new StringTokenizer(line, " ");
            while (st.hasMoreTokens()) { //get next token and store it in the array
                matrix[row][col] = Double.parseDouble(st.nextToken());
                col++;
            }
            col = 0;
            row++;
        }
        return matrix;
     
    }
     
    public static double [] [] scaleMatrix (double [] [] newMatrix) {
        double [] [] scaledMatrix = new double [ROWCOUNT][7]; //CHANGE WHEN NEEDED
        for (int i = 0; i < ROWCOUNT; i++) {
            scaledMatrix[i][0] = newMatrix[i][0]*3.965E14; // Msun
        }
        for (int j = 1; j < 4; j++) {
            for (int i = 0; i < ROWCOUNT; i++) {
                scaledMatrix[i][j] = newMatrix[i][j]*4.40811023E23; // yields meters; for kpc use 14285.7
            }
        }
        for (int j = 4; j <= 6; j++) {
            for (int i = 0; i < ROWCOUNT; i++) {
                scaledMatrix[i][j] = newMatrix[i][j]*3.455E5; //yields m/s
            }
        }
        return scaledMatrix;
    }
    
    public static double [] [] addColumnRadiusVelocity (double [] [] scaledMatrix) {
        double [] [] allData = new double [ROWCOUNT][9];
        for (int j = 0; j < 7; j++) {
            for (int i = 0; i < ROWCOUNT; i++) {
                allData[i][j] = scaledMatrix[i][j];
            }
        }
        for (int i = 0; i < ROWCOUNT; i++) {
            allData[i][7] = Math.sqrt(Math.pow(scaledMatrix[i][1] + 6.09262548E21, 2) + Math.pow(scaledMatrix[i][2] + 1.75743861E23, 2) + Math.pow(scaledMatrix[i][3] - 6.96485826E22, 2)); //in meters; 197.448374, 5695.46573, 2257.15489 kpc
            // allData[i][8] = Math.sqrt(Math.pow(scaledMatrix[i][4] - 8.95387818, 2) + Math.pow(scaledMatrix[i][2] + 33.6750024, 2) + Math.pow(scaledMatrix[i][3] - 18.25295, 2));
            // allData[i][7] = Math.sqrt(Math.pow(scaledMatrix[i][4], 2) + Math.pow(scaledMatrix[i][2], 2) + Math.pow(scaledMatrix[i][3], 2)) - 39.3363433;
            allData[i][4] -= 12793.8996;
            allData[i][5] += (-0.139268*3.455E5);
            allData[i][6] -= (0.0754878*3.455E5);
            double velocityRadial = ((allData[i][4]*(scaledMatrix[i][1] + 6.09262548E21)) + (allData[i][5]*(scaledMatrix[i][2] + 1.75743861E23)) + (allData[i][6]*(scaledMatrix[i][3] - 6.96485826E22)))/(allData[i][7]);
            double velocity =(Math.pow(allData[i][4], 2) + Math.pow(allData[i][5], 2) + Math.pow(allData[i][6], 2));
            allData[i][8] = (Math.sqrt(velocity - Math.pow(velocityRadial, 2)))/1000;
        }
        
        // for (int j = 0; j < 4; j++) {
            // for (int i = 0; i < ROWCOUNT; i++) {
                // display[i][j] = allData[i][j + 4];
            // }
        // }
        
        return allData;
    }

    public static double [] [] sumVelocities (double [] [] allData) {
        double [] [] sumVelocitiesMatrix = new double [BINCOUNT][2];
        int [] count = new int [BINCOUNT];
        for (int i = 0; i < BINCOUNT; i++) { // sets up column1, the radius values
            sumVelocitiesMatrix[i][1] = INCREMENT*(i + 1);
        }
        
        for (int i = 0; i < allData.length; i++) {
            double velocity = allData[i][8];
            double radius = (3.24077649E-20)*allData[i][7];
            if (radius < DISTANCE) {
                int rowNo = (int) Math.floor (radius/INCREMENT);
                sumVelocitiesMatrix[rowNo][0] += velocity;
                count[rowNo]++;
            }
        }
        
        for (int i = 0; i < BINCOUNT; i++) {
            if (count[i] > 0) sumVelocitiesMatrix[i][0] /= count[i];
        }
        
        return sumVelocitiesMatrix;
    }
}