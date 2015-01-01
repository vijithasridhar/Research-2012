import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class Binary {
    static final int GP_ATR_COUNT = 12;
    static final int SP_ATR_COUNT = 11;
    static final int DP_ATR_COUNT = 9;
    
    public static void main(String[] args) throws Exception {
    
        DataInputStream input = new DataInputStream(new BufferedInputStream(new FileInputStream(args[0])));
        Header binaryHeader = readHeader (input);
        
        int total = binaryHeader.nstar + binaryHeader.nsph + binaryHeader.ndark;
        
        float[][] gp = new float[binaryHeader.nsph][];
        for (int i = 0; i < 50; i++) { //binaryHeader.nsph; i++) {
            gp[i] = readParticle (input, GP_ATR_COUNT);
        }
        
        float[][] sp = new float[binaryHeader.nstar][];
        for (int i = 0; i < 50; i++) { //binaryHeader.nstar; i++) {
            sp[i] = readParticle (input, SP_ATR_COUNT);
        }
        
        float[][] dp = new float[binaryHeader.ndark][];
        for (int i = 0; i < 50; i++) { //binaryHeader.ndark; i++) {
            dp[i] = readParticle (input, DP_ATR_COUNT);
        }
        
        display(dp);
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
    
    public static void display (float [] [] G) {
		for (int i = 0; i < 50; i++) {
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
    
    // static final int MAXDIM = 3;
    // static class GasParticle {
        // float mass;
        // float pos[MAXDIM];
        // float vel[MAXDIM];
        // float rho; //density
        // float temp;
        // float hsmooth; //
        // float metals ; // metals in the gas
        // float phi; // gravity somethingsomething
    // };
    // static GasParticle readGasParticle (DataInputStream input) throws Exception {
        // GasParticle gp = new GasParticle();
        // gp.mass = input.readFloat();
        // for (int i = 0; i < MAXDIM; i++) {
            // gp.pos[i] = input.readFloat();
        // }
        // // ...
        // return gp;
    // }
    
    static float[] readParticle (DataInputStream input, int COUNT) throws Exception {
        float[] particle = new float[COUNT];
        for (int i =0; i < COUNT; i++) {
            particle[i] = readFloat(input);
        }
        return particle;
    }
    
}