package labs.franklee.celero.rules;

import labs.franklee.celero.exceptions.InvalidNodeException;
import labs.franklee.celero.logic.base.Node;
import labs.franklee.celero.logic.base.Relation;
import labs.franklee.celero.logic.base.Validation;
import labs.franklee.celero.logic.path.PathGroup;

public class Rule {

    /**
     * unique id
     */
    private String id;

    /**
     * rule's name, no unique constraint
     */
    private String name;

    /**
     * description of this rule
     */
    private String description;

    /**
     * root of logic tree
     */
    private Node root;

    /**
     * paths to match this rule
     */
    private PathGroup pathGroup;

    public void build() throws Throwable {
        Validation b = this.root.validateAll();
        if (!b.isValid()) {
            throw new InvalidNodeException(b.getMessage());
        }
        Relation relation = this.root.resolve();
        this.pathGroup = relation.getPathGroup();
    }

    private void buildRoot() {

    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public PathGroup getPathGroup() {
        return pathGroup;
    }
}
