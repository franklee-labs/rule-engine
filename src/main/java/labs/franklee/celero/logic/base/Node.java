package labs.franklee.celero.logic.base;

import java.util.ArrayList;
import java.util.List;

public abstract class Node {
    protected String id;
    protected NodeType type;
    private List<Node> children = new ArrayList<>();
    private String name = "Node";
    private String description = "no description.";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public NodeType getType() {
        return type;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public String name() {
        return this.name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public String description() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public abstract Relation resolve() throws Exception;

}
