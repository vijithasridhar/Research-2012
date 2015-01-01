import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class ReadThreeNumbers {
    public static void main(String[] args) throws Exception {
        
        DataInputStream input = new DataInputStream(new FileInputStream(args[0]));
        float particle1 = readFloat(input);
        float particle2 = readFloat(input);
        float particle3 = readFloat(input);
        // int particle1 = readInt (input);
        // int particle2 = readInt (input);
        // int particle3 = readInt (input);
        System.out.println(particle1);
        System.out.println(particle2);
        System.out.println(particle3);
  
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
    
    
}