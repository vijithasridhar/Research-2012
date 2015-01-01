
public class QuantumRandomWalk {

    public static final int ROWCOUNT = 4;
    public static final double THETA = Math.PI/3;
    public static final int RUNTIMES = 2;
   
    public static void main(String[] args) {

        Complex [] [] matrixS;
        Complex [] [] matrixI = new Complex [2*ROWCOUNT][1];
        for (int i = 0; i < 2*ROWCOUNT; i++) {
            matrixI[i][0] = new Complex(0,0);
        }
        matrixS = initialize_S(ROWCOUNT, THETA);

        matrixI[3][0] = new Complex(1,0);

        for (int i = 1; i <= RUNTIMES; ++i) {
            Complex [] [] matrixIntermediate = updatePosition(matrixI);
            matrixI = multiply(matrixS, matrixIntermediate);
            display(matrixI);
        }
    }
    
    public static Complex [] [] initialize_S(int ROWCOUNT, double THETA){ //** DO I NEED TO SPECIFY THAT IT'S A DOUBLE/INT?
        Complex [] [] matrix = new Complex [2*ROWCOUNT][2*ROWCOUNT];
        // do some stuff
        for (int i = 0; i < 2*ROWCOUNT; i++) {
            for (int j = 0; j < 2*ROWCOUNT; j++) {
                matrix[i][j] = new Complex(0,0);
            }
        }
        for (int i = 0; i < 2*ROWCOUNT; i++) {
            matrix[i][i] = new Complex(Math.cos(THETA), 0); // new Complex(Math.cos(THETA), 0) ;
        }
        for (int i = 0; i < 2*ROWCOUNT; i = 2 + i) {
            matrix[i][i + 1] = new Complex(0, Math.sin(THETA)); //new Complex(0, Math.sin(THETA));
        }
        for (int i = 1; i < 2*ROWCOUNT; i = 2 + i) {
            matrix[i][i - 1] = new Complex(0, Math.sin(THETA)); // add in i
        }

        return matrix;
    }
    public static Complex [] [] updatePosition(Complex [] [] V) {
        Complex [] [] matrix = new Complex [V.length][1];
//         for (int i = 1; i < V.length - 2; i = 2 + i) {
//             matrix[i + 2][0] = V[i][0];
//         }
//         for (int i = 2; i < V.length; i = 2 + i) {
//             matrix[i - 2][0] = V[i][0];
//         }
        for (int i = 0; i < V.length; i += 2) { // Operate on the even indices (move downwards)
            matrix [i][0] = V[(i+2) % V.length][0];
        }
        for (int i = V.length-1; i >= 0; i -= 2) { // Operate on the odd indices (move upwards)
            matrix [(i + 2) % V.length][0] = V[i][0];
        }
        return matrix;
    }
    public static Complex [] [] multiply(Complex [] [] A, Complex [] [] B) {
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
    public static void display (Complex [] [] G) {
        for (int i = 0; i < G.length; i++) {
            for (int j = 0; j < G[0].length; j++) {
                System.out.print ((j > 0 ? "\t" : "") + G[i][j]);
            }
            System.out.println ("");
        }
        System.out.println();
    }
}
