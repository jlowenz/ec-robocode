package ec;

import com.imaginaryday.ec.gp.Node;

import java.io.Serializable;

public class AgentBrains implements Serializable {
    private static final long serialVersionUID = 12312398890213L;
    public Node radarTree;
    public Node turretTree;
    public Node firingTree;
    public Node directionTree;

    public AgentBrains(Node radarTree, Node turretTree, Node firingTree, Node directionTree) {
        this.radarTree = radarTree;
        this.turretTree = turretTree;
        this.firingTree = firingTree;
        this.directionTree = directionTree;
    }
}
