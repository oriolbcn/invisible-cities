package edu.invisiblecities.maps.IsochronicMap;

import processing.core.PApplet;
import processing.core.PFont;
import de.looksgood.ani.Ani;
import de.looksgood.ani.easing.Easing;
import edu.invisiblecities.maps.BaseMap;
import edu.invisiblecities.utils.mathFunctions;

// TODO All nodes must be connected, no independent regiment for now
public class IsochronicMap extends BaseMap {

    /**********  Basic attributes **********/
    public int numOfNodes;              // Total number of nodes in this map
    public float centerX, centerY;      // The center of this map
    public float radiusArray[]          // Nodes lies on concentric circles with different radiuses
                = {0, 0, 60, 120, 180, 240, 300, 360, 420, 480, 540}; // for testing
    public boolean connections[][];     // The adjacency matrix
    public Node[] nodes = null;         // Array of nodes in this map
    public float[][] toPositions;       // New positions after selecting a new center node
    public int selectedNode;            // The id of current selected node, initially 0
    
    /********** Colors & Fonts **********/
    public int sred, sgreen, sblue, salpha;     // Stroke color, if any
    public int fred, fgreen, fblue, falpha;     // Fillin color
    public int tred, tgreen, tblue, talpha;     // Text color
    public PFont font;
    
    /********** Accessories **********/
    private int[] queue = null;
    private int queueSize;
    private boolean[] visited = null;
    private int[] hierarchy = null;
    // BFS the graph and build up a new hierarchy
    private void BFS(int snode) {
        queue = new int[numOfNodes * 2];
        queueSize = numOfNodes * 2;
        hierarchy = new int[numOfNodes];
        visited = new boolean[numOfNodes];
        int head = 0;
        int tail = 0;
        queue[head++] = snode;
        hierarchy[snode] = 1;
        visited[snode] = true;
        while (head != tail) {
            int cnode = queue[tail];
            tail = (tail + 1) % queueSize;
            int cHierarchy = hierarchy[cnode];
            for (int i=0; i<numOfNodes; ++i) if (i != cnode) {
                if (connections[cnode][i] && !visited[i]) {
                    int newHierarchy = cHierarchy + 1;
                    hierarchy[i] = newHierarchy;
                    queue[head] = i;
                    head = (head + 1) % queueSize;
                    visited[i] = true;
                }
            }
        }
    }
    
    // Calculate the new positions of each station
    private int[][] level; // level[i][0] is the total number of nodes on hierarchy i
                           // level[i][j] (j > 0) records the id of the node on hierarchy i,
                           // the order might be changed according to requirement
    public void updateGraph() {
        BFS(selectedNode);
        // Reset total number of each level
        for (int i=0; i<numOfNodes; ++i) {
            level[i][0] = 0;
        }
        for (int i=0; i<numOfNodes; ++i) {
            level[hierarchy[i]][++level[hierarchy[i]][0]] = i;
        }
        for (int i=0; i<numOfNodes; ++i) if (level[i][0] > 0) {
            for (int j=1; j<=level[i][0]; ++j) {
                float theta = parent.map(j, 0, level[i][0], 0, 2 * parent.PI);
                float x = centerX + radiusArray[i] * parent.cos(theta);
                float y = centerY + radiusArray[i] * parent.sin(theta);
                toPositions[level[i][j]][0] = x;
                toPositions[level[i][j]][1] = y;
            }
        }
    }
    
    /********** For debug **********/
    public void generateRandomGraph() {
        boolean[] connected = new boolean[numOfNodes];
        for (int i=0; i<numOfNodes; ++i) {
            for (int j=i+1; j<numOfNodes; ++j) {
                float r = parent.random(100);
                boolean bv = false;
                float bound = 90.f;
                if (i % 2 == 1) bound = 80.f;
                if (r > bound) bv = true;
                connections[i][j] = connections[j][i] = bv;
                if (bv)
                    connected[i] = connected[j] = true;
            }
        }
        // Make sure all nodes are connected
        for (int i=0; i<numOfNodes; ++i) {
            if (!connected[i])
                connections[i][(i+1) % numOfNodes] 
                        = connections[(i+1) % numOfNodes][i] = true;
        }
    }
    
    /********** Override methods **********/
    // Initialize attributes that are not initialized in constructor
    @Override
    public void init() {
        parent.size(canvasWidth, canvasHeight);
        Ani.init(parent);
        generateRandomGraph();
        selectedNode = 0;
        updateGraph();
        nodes = new Node[numOfNodes];
        for (int i=0; i<numOfNodes; ++i) {
            nodes[i] = new Node(toPositions[i][0], toPositions[i][1], i, 20, 120, 170);
        }
    }
    
    public int getSelection() {
        for (int i=0; i<numOfNodes; ++i) {
            if (nodes[i].isInside(parent.mouseX, parent.mouseY))
                return i;
        }
        return -1;
    }
        
    @Override
    public void draw() {
        parent.noFill();
        parent.stroke(sred, sgreen, sblue, salpha);
        parent.strokeWeight(1);
        for (int i=2; i<radiusArray.length; ++i) {
            parent.ellipse(centerX, centerY, radiusArray[i] * 2, radiusArray[i] * 2);
        }
        
        for (int i=0; i<numOfNodes; ++i) {
            for (int j=0; j<numOfNodes; ++j) if (connections[i][j]) {
                if (i == selectedNode || j == selectedNode) {
                    parent.strokeWeight(5);
                } else {
                    parent.strokeWeight(1);
                }
                parent.line(nodes[i].x, nodes[i].y, nodes[j].x, nodes[j].y);
            }
        }
        for (int i=0; i<numOfNodes; ++i) 
            nodes[i].draw();
    }
    
    @Override
    public void mousePressed() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseReleased() {
        int id = getSelection();
        if (id >= 0) {
            selectedNode = id;
            BFS(id);
            updateGraph();
            for (int i=0; i<numOfNodes; ++i) {
                nodes[i].setAni(toPositions[i][0], toPositions[i][1]);
            }
        }
    }

    @Override
    public void keyPressed() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void keyReleased() {
        // TODO Auto-generated method stub
        
    }
    
    /********** Constructors **********/
    public IsochronicMap(PApplet p, int non) {
        super(p);
        numOfNodes = non;
        nodes = new Node[numOfNodes];
        toPositions = new float[numOfNodes][2];
        level = new int[numOfNodes+1][numOfNodes+1];
        connections = new boolean[numOfNodes][numOfNodes];
        fred = fgreen = fblue = 0;
        falpha = 100;
        sred = sgreen = sblue = 0;
        salpha = 50;
        centerX = canvasWidth / 2;
        centerY = canvasHeight / 2;
        selectedNode = 0;
    }    
    
    private class Node {
        
        // Basic attributes
        public float x;
        public float y;
        public float diameter = 40.f;
        public float radius = 20.f;
        public int id;
        // Colors & Fonts
        // Stroke color
        public int sred = 255, sgreen = 255, sblue = 255, salpha = 100; 
        // Fill-in color
        public int fred = 0, fgreen = 0, fblue = 0, falpha = 100;     
        // Text color
        public int tred = 0, tgreen = 0, tblue = 0, talpha = 100;    
        public PFont font;
        
        // Animation
        public float duration = 0.5f;
        public Easing easing = Ani.EXPO_IN_OUT;
        
        public Node(float _x, float _y, int _id, int fr, int fg, int fb) {
            x = _x;
            y = _y;
            id = _id;
            fred = fr;
            fgreen = fg;
            fblue = fb;
            font = parent.loadFont("AmericanTypewriter-16.vlw");
            parent.textFont(font);
        }
        
        public void setAni(float _x, float _y) {
            Ani.to(this, duration, "x", _x, easing);
            Ani.to(this, duration, "y", _y, easing);
        }
        
        public void draw() {
            parent.fill(fred, fgreen, fblue);
            parent.stroke(sred, sgreen, sblue, salpha);
            parent.ellipse(x, y, diameter, diameter);
            parent.fill(tred, tgreen, tblue);
            parent.text(""+id, x, y + 5);
        }
        
        public boolean isInside(float sx, float sy) {
            float distance = mathFunctions.getDistance(x, y, sx, sy);
            return distance < radius;
        }
    }
}
