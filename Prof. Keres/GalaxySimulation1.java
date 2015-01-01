import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.StringTokenizer;
 
public class GalaxySimulation1 {

    public static final int ROWCOUNT = 383785; // 1138006 Star, 931505 Dark, 383785 Gas
    public static final int COLCOUNT = 7;
    public static final double INCREMENT = 5;
    public static final int DISTANCE = 200;
    public static final int BINCOUNT = (int) (DISTANCE/INCREMENT);
    public static void main(String[] args) throws Exception {
 
        double [] [] matrixInitial = makeMatrix(args[0]);
        double [] [] scaledMatrix = scaleMatrix(matrixInitial);
        double [] [] radiusMatrix = addColumnRadius(scaledMatrix);
        double [] [] sumMatrix = sumRadiiMass(radiusMatrix);
        double [] [] velocityMatrix = addVelocityMatrix(sumMatrix);
        display(velocityMatrix);
        
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
            scaledMatrix[i][0] = newMatrix[i][0]*3.965E14;
        }
        for (int j = 1; j < 4; j++) {
            for (int i = 0; i < ROWCOUNT; i++) {
                scaledMatrix[i][j] = newMatrix[i][j]*14285.7; // yields kpc
            }
        }
        for (int j = 4; j <= 6; j++) {
            for (int i = 0; i < ROWCOUNT; i++) {
                scaledMatrix[i][j] = newMatrix[i][j]*241.8; //yields km/s
            }
        }
        return scaledMatrix;
    }
        
    public static double [] [] addColumnRadius (double [] [] scaledMatrix) {
        double [] [] radiusMatrix = new double [ROWCOUNT][8];
        for (int j = 0; j < 7; j++) {
            for (int i = 0; i < ROWCOUNT; i++) {
                radiusMatrix[i][j] = scaledMatrix[i][j];
            }
        }
        for (int i = 0; i < ROWCOUNT; i++) {
            radiusMatrix[i][7] = Math.sqrt(Math.pow(scaledMatrix[i][1] + 197.448374, 2) + Math.pow(scaledMatrix[i][2] + 5695.46573, 2) + Math.pow(scaledMatrix[i][3] - 2257.15489, 2));
        }
        return radiusMatrix;
    }

    public static double [] [] sumRadiiMass (double [] [] radiusMatrix) {
        double [] [] sumMatrix = new double [BINCOUNT][2];
        for (int i = 0; i < BINCOUNT; i++) { //sets up column2, the radius values
            sumMatrix[i][1] = INCREMENT*(i + 1);
        }
        
        for (int i = 0; i < radiusMatrix.length; i++) {
            double mass = radiusMatrix[i][0];
            double radius = radiusMatrix[i][7];
            if (radius < DISTANCE) {
                int rowNo = (int) Math.floor (radius/INCREMENT);
                sumMatrix[rowNo][0] += mass;
                // for (int j = 1; j < radiusMatrix.length; j++) {
                    // sumMatrix[j][0] = sumMatrix[j - 1][0] + radiusMatrix[j][0]; 
                // }
            }
        }     
        for (int k = 1; k < BINCOUNT; k++) {
            sumMatrix[k][0] += sumMatrix[k - 1][0];
        }
        // boolean underFive = true;
        // for (int j = 5; j < 200; j += 5) {
            // for (int i = 0; i < ROWCOUNT; i++) {
                // if (radiusMatrix[i][8] >= j;) {
                    // underFive = false;
                // }
            // }
            // for (int i = 0; i < 40; i++) {
                // if (underFive) {
                    // sumMatrix[i][0] = 
                // }
            // }
        // }
        return sumMatrix;
    }
    
    public static double [] [] addVelocityMatrix (double [] [] sumMatrix) {
        double [] [] velocityMatrix = new double [BINCOUNT][3];
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < DISTANCE/INCREMENT; i++) {
                velocityMatrix[i][j] = sumMatrix[i][j];
            }
        }
        for (int i = 0; i < DISTANCE/INCREMENT; i++) {
            double mass = sumMatrix[i][0];
            double radius = sumMatrix[i][1];
            velocityMatrix[i][2] = (Math.sqrt((6.673E-11*mass*1.98892E30)/(radius*3.08568025E19)))/1000;
        }
        return velocityMatrix;
    }
}