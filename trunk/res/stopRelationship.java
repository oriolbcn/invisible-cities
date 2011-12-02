import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

class stopRelationship {
    static FileInputStream ifstream;
    static DataInputStream in;
    static BufferedReader br;
    static FileWriter ofstream;
    static BufferedWriter out;
    static String outfilename = "stoprelp.csv";
    static String stoprelfilename = "stoprel.csv";
    static String routetripfilename = "routetrip.csv";
    static String stopstationfilename = "stopstation.csv";
    static String stopidnamefilename = "stopidname.csv";
    
    static HashMap<String, String> triptoroute;
    static HashMap<String, String> stopstation;
    
    
    static class Station {
        String route_id;
        float lat;
        float lon;
        HashSet<String> relatedStations;
        String name;
        public Station() {
            route_id = null;
            relatedStations = new HashSet<String>();
        }
    }
    static final int STATIONSIZE = 170;
    static boolean[][] connection = null;
    
    public static void main(String[] arg) {
        Station[] stations = new Station[STATIONSIZE];
        try {
            connection = new boolean[STATIONSIZE][STATIONSIZE];
            ofstream = new FileWriter(outfilename);
            out = new BufferedWriter(ofstream);
            ifstream = new FileInputStream(routetripfilename);
            in = new DataInputStream(ifstream);
            br = new BufferedReader(new InputStreamReader(in));
            String line;
            triptoroute = new HashMap<String, String>();
            while ((line = br.readLine()) != null) {
                String[] split = line.split(";");
                triptoroute.put(split[1], split[0]);
            }
            br.close();
            in.close();
            System.out.println("Done loading routetrip info");
            
            stopstation = new HashMap<String, String>();
            ifstream = new FileInputStream(stopstationfilename);
            in = new DataInputStream(ifstream);
            br = new BufferedReader(new InputStreamReader(in));            
            while ((line = br.readLine()) != null) {
                String[] split = line.split(";");
                stopstation.put(split[0], split[1]);
            }
            br.close();
            in.close();
            System.out.println("Done loading stopstation info");
            
            ifstream = new FileInputStream(stoprelfilename);
            in = new DataInputStream(ifstream);
            br = new BufferedReader(new InputStreamReader(in));            
            
            for (int i=0; i<STATIONSIZE; ++i) stations[i] = new Station();
            line = br.readLine();
            while (true) {
                if (line == null) break;
                String[] split = line.split(";"); // 0: tripid; 1: stop_id, 2:distance
                String tripid = split[0];
                String routeid = triptoroute.get(tripid);
                int stationid = Integer.parseInt(stopstation.get(split[1])) / 10 - 4000;
                int dist = Integer.parseInt(split[2]);
                stations[stationid].route_id = routeid;
//                System.out.print("1 Station " + stationid);
                while ((line = br.readLine()) != null) {
                    String[] cursplit = line.split(";");
                    int curdist = Integer.parseInt(cursplit[2]);
                    if (!tripid.equals(cursplit[0])) break;
                    int curstationid = Integer.parseInt(stopstation.get(cursplit[1])) / 10 - 4000;
                    stations[curstationid].route_id = routeid;
                    if (curstationid == stationid) continue;
                    connection[stationid][curstationid] = 
                        connection[curstationid][stationid] = true;
                    stations[stationid].relatedStations.add(curstationid + ";" + (curdist-dist));
                    stations[curstationid].relatedStations.add(stationid + ";" + (curdist-dist));
                    if ((curdist-dist) < 0) System.out.println("route " + routeid + " curdist " + curdist + " dist " + dist + " line " + line);
                    //System.out.println("Connect " + stationid + " with " + curstationid);
                    stationid = curstationid;
                    dist = curdist;
                }
            }
            
            br.close();
            in.close();
            
            ifstream = new FileInputStream(stopidnamefilename);
            in = new DataInputStream(ifstream);
            br = new BufferedReader(new InputStreamReader(in));            
            
            while ((line = br.readLine()) != null) {
                String[] split = line.split(";");
                int stationid = Integer.parseInt(stopstation.get(split[0])) / 10 - 4000;
                stations[stationid].name = split[1];
                stations[stationid].lat = Float.parseFloat(split[2]);
                stations[stationid].lon = Float.parseFloat(split[3]);
            } 
            br.close();
            in.close();
            
            for (int i=0; i<STATIONSIZE; ++i) {
                if (stations[i].route_id != null) {
                    out.write(i + ";" + stations[i].route_id + ";" + stations[i].name + ";" + stations[i].lat + ";" + stations[i].lon + "\n");
                    for(Iterator it=stations[i].relatedStations.iterator();
                            it.hasNext();) {
                        out.write(it.next().toString() + ";");
                    }
                    out.write("\n");
                }
            }
            out.write("\n");
            
            out.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}



