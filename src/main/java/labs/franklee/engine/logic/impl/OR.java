package labs.franklee.engine.logic.impl;

import labs.franklee.engine.logic.base.*;
import labs.franklee.engine.logic.path.Path;
import labs.franklee.engine.logic.path.PathGroup;

import java.util.ArrayList;
import java.util.List;

public class OR extends Relation {

    public OR() {
        super();
    }

    @Override
    public boolean validate() {
        return this.getChildren().size() >= 2;
    }

    @Override
    public RelationType relation() {
        return RelationType.Or;
    }

    @Override
    public Relation resolve() throws Exception {
        if (this.getChildren().size() < 2) {
            throw new RuntimeException("Or node mush have at least two node.");
        }
        Relation currentNode = null;
        for (int i = 1; i < this.getChildren().size(); i++) {
            if (currentNode == null) {
                Node n0 = this.getChildren().getFirst();
                currentNode = n0.resolve();
            }
            Node next = this.getChildren().get(i);
            Relation nextNode = next.resolve();
            PathGroup pathGroup = new PathGroup();
            currentNode.getPathGroup().paths().forEach(p1 -> pathGroup.addPath(new Path().addAllCondition(p1.conditions())));
            nextNode.getPathGroup().paths().forEach(p2 -> pathGroup.addPath(new Path().addAllCondition(p2.conditions())));
            Relation newCurrent = new OR();
            newCurrent.setPathGroup(pathGroup);
            currentNode = newCurrent;
        }
        return currentNode;
    }

    @Override
    public Relation negate() throws Exception {
        Relation and = new AND();
        List<Node> nodes = new ArrayList<>();
        for (Node child : this.getChildren()) {
            if (child instanceof Negatable) {
                nodes.add(((Negatable<?>) child).negate());
            } else {
                throw new UnsupportedOperationException("Negatable child node is required. " + child.getClass().getName() + " is not negatable!");
            }
        }
        and.setChildren(nodes);
        return and;
    }

}
