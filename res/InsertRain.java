import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.*;
import java.util.ArrayList;

class InsertRain {
    static FileInputStream ifstream;
    static DataInputStream in;
    static BufferedReader br;
    static FileWriter ofstream;
    static BufferedWriter out;
    static String outfilename = "rainrtdInsert.csv";
    static String rstdfilename = "RainRouteTimeDist.csv";
    static int Interval = 15;
    
    public static void main(String[] arg) {
        try {
            
            ofstream = new FileWriter(outfilename);
            out = new BufferedWriter(ofstream);
            ifstream = new FileInputStream(rstdfilename);
            in = new DataInputStream(ifstream);
            br = new BufferedReader(new InputStreamReader(in));
            String line;
            line = br.readLine();
            
            while (true) {
                if (line == null) break;
                String[] presplit = line.split(";");
                int length = 0, starttime = Integer.parseInt(presplit[1]);
                int endtime = starttime;
                while ((line = br.readLine()) != null) {
                    String[] split = line.split(";");
                    int dist = Integer.parseInt(split[2]);
                    if (dist == 0) break;
                    length = dist;
                    endtime = Integer.parseInt(split[1]);
                }
                int nou = (endtime - starttime) / Interval;
                if (nou == 0) continue;
                int diameterUnit = length / nou;
                for (int i=0; i<=nou; ++i) {
                    out.write(presplit[0] + ";" + (starttime + i * Interval)
                              + ";" + (diameterUnit * (nou - i)) + "\n");
                }
            }
            br.close();
            in.close();
            out.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}