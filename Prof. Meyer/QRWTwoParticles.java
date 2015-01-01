public class QRWTwoParticles {

	public static final int ROWCOUNT = 21; // # of lattice points; must be an odd # since 0 to 2n is odd, not even
	public static final double THETA = Math.PI/4;
	public static final double PHI = Math.PI/15;
	public static final int RUNTIMES = 20;
	public static final int STARTI = 5; // if odd it'll go right
	public static final int STARTJ = 8; // if even it'll go left
	
	public static void main(String[] args) {
        // TODO Auto-generated method stub

        Complex [] [] matrixI = new Complex [2*ROWCOUNT][1]; // P1 initial location+velocity
        Complex [] [] matrixJ = new Complex [2*ROWCOUNT][1]; //P2 initial location+velocity
        for (int i = 0; i < 2*ROWCOUNT; i++) {
            matrixI[i][0] = new Complex(0,0);
            matrixJ[i][0] = new Complex(0,0);
        }
        matrixI[STARTI][0] = new Complex(1,0);
        matrixJ[STARTJ][0] = new Complex(1,0);
        
        Complex [] [] matrixS = initialize_S(ROWCOUNT, THETA);
        Complex [] [] matrixT = initialize_S(ROWCOUNT, PHI);
        
        for (int i = 1; i <= RUNTIMES; ++i) {
            MatrixPair result = updatePosition(matrixI, matrixJ);
            Complex [] [] matrixIntermediate = result.m1;
            Complex [] [] matrixMiddle = result.m2;
        
            matrixI = multiply(matrixS, matrixIntermediate);
            matrixJ = multiply(matrixT, matrixMiddle);
            double [] [] probAmps = probabilityAmplitudes(matrixI, matrixJ);
            display(probAmps);
            //generateCSV("C:\\Users\Vijitha\Desktop\csvtest.csv", probAmps);
           /*  System.out.println("The lighter particle on the left:");
            display(matrixI);
            System.out.println("The heavier particle on the right:");
            display(matrixJ);
            System.out.println("FIN");
            System.out.println(" "); */
        }
    }
	
    public static Complex [] [] initialize_S (int n, double theta) {
        Complex [] [] matrix = new Complex [2*n][2*n];
        for (int i = 0; i < 2*n; i++) {
            for (int j = 0; j < 2*n; j++) {
                matrix[i][j] = new Complex(0,0);
            }
        }
        for (int i = 0; i < 2*n; i++) {
            matrix[i][i] = new Complex(Math.cos(theta), 0);
        }
        for (int i = 0; i < 2*n; i = 2 + i) {
            matrix[i][i + 1] = new Complex(0, Math.sin(theta));
        }
        for (int i = 1; i < 2*n; i = 2 + i) {
            matrix[i][i - 1] = new Complex(0, Math.sin(theta));
        }
        
        // force the particle to change velocities at boundaries
        matrix[0][0] = new Complex(0, 0); // left wall at 0
        matrix[1][0] = new Complex(1, 0);
        matrix[2*n - 1][2*n - 1] = new Complex(0,0); // right wall at 2n
        matrix[2*n - 2][2*n - 1] = new Complex(1,0);
        
        return matrix;
    }
    
    public static MatrixPair updatePosition (Complex [] [] V, Complex [] [] U) { //velocity updates position
        Complex [] [] p1 = new Complex [V.length][1];
        Complex [] [] p2 = new Complex [U.length][1]; // P2 SHOULD ALWAYS BE RIGHT OF P1
        for (int i = 0; i < V.length; i++) {
            p1[i][0] = new Complex (0,0);
        }
        for (int i = 0; i < U.length; i++) {
            p2[i][0] = new Complex (0,0);
        }
        
        for (int i = 1; i < V.length - 2; i = 2 + i) { // moves right if odd index
			p1[i + 2][0] = p1[i + 2][0].plus (V[i][0]);
		}
		for (int i = 2;  i < V.length - 1; i += 2) { //moves left if even index
			p1[i - 2][0] = p1[i - 2][0].plus (V[i][0]);
		}
        
        for (int i = 1; i < U.length - 1; i = 2 + i) { // P2
			p2[i + 2][0] = p2[i + 2][0].plus (U[i][0]);
		}
		for (int i = 2;  i < U.length; i = 2 + i) {
			p2[i - 2][0] = p2[i - 2][0].plus (U[i][0]);
		}
        
		// if a collision is due to happen (if they will land on the same site)
        for (int i = 1; i < V.length - 3; i += 2) { 
            if (!p1[i][0].equals (new Complex(0,0)) && !p2[i + 3][0].equals (new Complex(0,0))) { 
                p1[i + 1][0] = p1[i + 1][0].plus (p1[i][0]);
                p2[i - 1][0] = p2[i - 1][0].plus (p2[i][0]);
                p1[i][0] = new Complex(0,0);
                p2[i + 3][0] = new Complex(0,0);
            }
        }
        
        return new MatrixPair (p1, p2);
    }
    
    public static Complex [] [] multiply (Complex [] [] A, Complex [] [] B) {
        Complex [] [] C;
        C = new Complex [A.length][B[0].length];
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < B[0].length; j++) {
                C[i][j] = new Complex(0,0);
            }
        }
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < B[0].length; j++) {
                for (int k = 0; k < A[0].length; k++) {
                    if (B[k][j] == null) System.out.println ("null at " + k + ", " + j);
                    C[i][j] = C[i][j].plus(A[i][k].times(B[k][j]));
                }
            }
        }
        return C;
    }
    
    public static void display (double [] [] G) {
        for (int i = 0; i < G.length; i++) {
            for (int j = 0; j < G[0].length; j++) {
                System.out.print ((j > 0 ? "\t" : "") + G[i][j]);
            }
            System.out.println ("");
        }
        System.out.println();
    }
    
    public static double [] [] probabilityAmplitudes (Complex [] [] matrixI, Complex [] [] matrixJ) {
        /* produce (x, y, z) pairs where x is the lattice point for P1, 
        y is the lattice point for P2, and z is the probability amplitude. */        
        double [] [] probAmps = new double [ROWCOUNT][ROWCOUNT];
        for (int i = 0; i < ROWCOUNT; i++) {
            for (int j = 0; j < ROWCOUNT; j++) {
                probAmps[i][j] =  Math.pow(matrixI[2*i][0].mod(), 2) + Math.pow(matrixI[(2*i) + 1][0].mod(), 2) + Math.pow(matrixJ[2*j][0].mod(), 2) + Math.pow(matrixJ[(2*j) + 1][0].mod(), 2);
            }
        }
        
        return probAmps;
    }
    
    /*private static void generateCsvFile(String sFileName, double [] [] probAmps) {
        try (FileWriter writer = new FileWriter(sFileName);
        for (int j = 0; j < ROWCOUNT; j++) {    
            for int (i = 0; i < ROWCOUNT; i++) {
                writer.append(probAmps[i][j]);
            }
        }
    }*/
    
    static class MatrixPair {
        Complex[][] m1;
        Complex[][] m2;
        
        MatrixPair (Complex[][] p1, Complex[][] p2) {
            this.m1 = p1;
            this.m2 = p2;
        }
    }
}