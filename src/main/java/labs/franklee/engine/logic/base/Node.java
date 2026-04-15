package labs.franklee.engine.logic.base;

import java.util.ArrayList;
import java.util.List;

public abstract class Node {
    protected String id;
    protected NodeType type;
    private List<Node> children = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }
}
