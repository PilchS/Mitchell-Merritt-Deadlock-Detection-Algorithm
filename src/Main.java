import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

class Node {
    private int publicLabel;
    private int privateLabel;
    private boolean blocked;
    private List<Node> neighbors;

    public Node(int publicLabel, int privateLabel) {
        this.publicLabel = publicLabel;
        this.privateLabel = privateLabel;
        this.blocked = false;
        this.neighbors = new ArrayList<>();
    }

    public int getPublicLabel() {
        return publicLabel;
    }

    public int getPrivateLabel() {
        return privateLabel;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public List<Node> getNeighbors() {
        return neighbors;
    }

    public void setPublicLabel(int publicLabel) {
        this.publicLabel = publicLabel;
    }

    public void setPrivateLabel(int privateLabel) {
        this.privateLabel = privateLabel;
    }
}

class DDDAlgorithm {

    private Map<Integer, Node> nodes;
    private List<Edge> blockedEdges;

    public DDDAlgorithm() {
        this.nodes = new HashMap<>();
        this.blockedEdges = new ArrayList<>();
    }

    private void initiate(int N) {
        Random random = new Random();
        for (int i = 1; i <= N; i++) {
            int publicLabel = random.nextInt(N) + 1;
            int privateLabel = publicLabel;
            nodes.put(i, new Node(publicLabel, privateLabel));
        }
    }

    private void blockEdge(int u, int v) {
        Node nodeU = nodes.get(u);
        Node nodeV = nodes.get(v);
        nodeU.setBlocked(true);
        blockedEdges.add(new Edge(nodeU, nodeV));
        int k = getUniqueLabel(nodeU.getPublicLabel(), nodeV.getPublicLabel());
        nodeU.setPrivateLabel(k);
        nodeV.setPrivateLabel(k);
    }

    private int getUniqueLabel(int u1, int u2) {
        int k = Math.max(u1, u2) + 1;
        while (k == u1 || k == u2) {
            k++;
        }
        return k;
    }

    private void transmit() {
        for (Edge edge : blockedEdges) {
            Node nodeU = edge.getNodeU();
            Node nodeV = edge.getNodeV();
            if (nodeU.getPublicLabel() > nodeV.getPublicLabel()) {
                propagate(nodeV, nodeU);
            } else if (nodeU.getPublicLabel() < nodeV.getPublicLabel()) {
                propagate(nodeU, nodeV);
            }
        }
    }

    private void propagate(Node source, Node target) {
        Queue<Node> queue = new LinkedList<>();
        Set<Node> visited = new HashSet<>();
        queue.add(source);

        while (!queue.isEmpty()) {
            Node node = queue.poll();
            visited.add(node);
            node.setPublicLabel(target.getPublicLabel());

            for (Node neighbor : node.getNeighbors()) {
                if (!visited.contains(neighbor) && neighbor.getPublicLabel() < target.getPublicLabel()) {
                    queue.add(neighbor);
                }
            }
        }
    }

    private boolean detectDeadlock() {
        for (Node node : nodes.values()) {
            if (node.isBlocked() && node.getPrivateLabel() == node.getPublicLabel()) {
                Node blockedProcess = null;
                for (Edge edge : blockedEdges) {
                    if (edge.getNodeU() == node) {
                        blockedProcess = edge.getNodeV();
                        break;
                    } else if (edge.getNodeV() == node) {
                        blockedProcess = edge.getNodeU();
                        break;
                    }
                }
                if (blockedProcess != null && blockedProcess.getPublicLabel() == node.getPrivateLabel()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void readInputFromFile(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        Scanner scanner = new Scanner(file);

        int N = scanner.nextInt();
        initiate(N);

        int M = scanner.nextInt();
        for (int i = 0; i < M; i++) {
            int u = scanner.nextInt();
            int v = scanner.nextInt();
            blockEdge(u, v);
        }

        scanner.close();
    }

    public static void main(String[] args) {
        DDDAlgorithm dddAlgorithm = new DDDAlgorithm();

        try {
            dddAlgorithm.readInputFromFile("data2.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        dddAlgorithm.transmit();
        boolean deadlockDetected = dddAlgorithm.detectDeadlock();
        System.out.println("Deadlock Detected: " + deadlockDetected);
        dddAlgorithm.transmit();
    }
}

class Edge {
    private Node nodeU;
    private Node nodeV;

    public Edge(Node nodeU, Node nodeV) {
        this.nodeU = nodeU;
        this.nodeV = nodeV;
    }

    public Node getNodeU() {
        return nodeU;
    }

    public Node getNodeV() {
        return nodeV;
    }
}
