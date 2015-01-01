import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class BinaryGalaxySimulation {
    static final int GP_ATR_COUNT = 12;
    static final int SP_ATR_COUNT = 11;
    static final int DP_ATR_COUNT = 9;
    public static final int ROWCOUNT = 2906304; // Gas: 2091950, Star: 1474901, Dark: 2906304
    public static final int COLCOUNT = 7;
    public static final double INCREMENT = .5;
    public static final int DISTANCE = 20;
    public static final int BINCOUNT = (int) (DISTANCE/INCREMENT);
    
    public static void main(String[] args) throws Exception {
    
        DataInputStream input = new DataInputStream(new BufferedInputStream(new FileInputStream(args[0])));
        Header binaryHeader = readHeader (input);
        
        int total = binaryHeader.nstar + binaryHeader.nsph + binaryHeader.ndark;
        
        float[][] gp = new float[binaryHeader.nsph][];
        for (int i = 0; i < binaryHeader.nsph; i++) { //50; i++) { 
            gp[i] = readParticle (input, GP_ATR_COUNT);
        }
        
        float[][] sp = new float[binaryHeader.nstar][];
        for (int i = 0; i < binaryHeader.nstar; i++) { //50; i++) {
            sp[i] = readParticle (input, SP_ATR_COUNT);
        }
        
        float[][] dp = new float[binaryHeader.ndark][];
        for (int i = 0; i < binaryHeader.ndark; i++) { //50; i++) {
            dp[i] = readParticle (input, DP_ATR_COUNT);
        }
        
        double [] [] scaledMatrix = scaleMatrix(dp); // CHANGE WHEN NEEDED
        double [] [] radiusMatrix = addColumnRadius(scaledMatrix);
        double [] [] sumMatrix = sumRadiiMass(radiusMatrix);
        double [] [] velocityMatrix = addVelocityMatrix(sumMatrix);
        display(velocityMatrix);
        
    }
    
    private static Header readHeader (DataInputStream input) throws Exception {
        Header aHeader = new Header();
        aHeader.time = readDouble(input);
        aHeader.nbodies = readInt(input);
        aHeader.ndim = readInt(input);
        aHeader.nsph = readInt(input); //number of gas particles in the file (nsph)
        aHeader.ndark = readInt(input); //# of dark particless
        aHeader.nstar = readInt(input); //number of star particles
        return aHeader;
    }
    
    static int readInt (DataInputStream is) throws Exception {
        byte[] b = new byte[4];
        int v = is.read (b);
        if (v < 4) throw new java.io.EOFException ("End of file");
        return b[0] + (b[1] << 8) + (b[2] << 16) + (b[3] << 24);
    }
    
    static float readFloat (DataInputStream is) throws Exception {
        byte[] b = new byte[4];
        int v = is.read (b);
        if (v < 4) throw new java.io.EOFException ("End of file");
        byte[] c = new byte[4];
        for (int i = 3; i >= 0; i--) {
            c[i] = b[3-i];
        }
        DataInputStream dis = new DataInputStream (new java.io.ByteArrayInputStream (c));
        return dis.readFloat();
    }
    
    static double readDouble (DataInputStream is) throws Exception {
        byte[] b = new byte[8];
        int v = is.read (b);
        if (v < 8) throw new java.io.EOFException ("End of file");
        byte[] c = new byte[8];
        for (int i = 7; i >= 0; i--) {
            c[i] = b[7 - i];
        }
        DataInputStream dis = new DataInputStream (new java.io.ByteArrayInputStream (c));
        return dis.readDouble();   
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
     
    static class Header {
        double time;
        int nbodies;
        int ndim;
        int nsph;
        int ndark;
        int nstar;
    }
    
    static float[] readParticle (DataInputStream input, int COUNT) throws Exception {
        float[] particle = new float[COUNT];
        for (int i =0; i < COUNT; i++) {
            particle[i] = readFloat(input);
        }
        return particle;
    }
    
    public static double [] [] scaleMatrix (float [] [] newMatrix) {
        double [] [] scaledMatrix = new double [ROWCOUNT][7];
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
            }
        }     
        for (int k = 1; k < BINCOUNT; k++) {
            sumMatrix[k][0] += sumMatrix[k - 1][0];
        }
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