package labs.franklee.engine.logic.impl;

import labs.franklee.engine.logic.base.Condition;
import labs.franklee.engine.logic.base.Node;
import labs.franklee.engine.logic.base.Relation;
import labs.franklee.engine.logic.path.Path;
import labs.franklee.engine.logic.path.PathGroup;

public class And extends Relation {

    @Override
    public PathGroup calculate() {
        if (this.getChildren().isEmpty()) {
            throw new RuntimeException("And node mush have at least one node.");
        }
        if (this.getChildren().size() == 1) {
            Node node = this.getChildren().get(0);
            if (node instanceof Relation) {
                return ((Relation) node).calculate();
            }
            return new PathGroup(new Path().addCondition((Condition) node));
        }
        PathGroup previousPaths = null;
        for (int i = 1; i < this.getChildren().size(); i++) {
            if (previousPaths == null) {
                Node n0 = this.getChildren().get(0);
                if (n0 instanceof Relation) {
                    previousPaths = ((Relation) n0).calculate();
                } else {
                    previousPaths = new PathGroup(new Path().addCondition((Condition) n0));
                }
            }
            Node current = this.getChildren().get(i);
            PathGroup currentPaths;
            if (current instanceof Relation) {
                currentPaths = ((Relation) current).calculate();
            } else {
                currentPaths = new PathGroup((new Path().addCondition((Condition) current)));
            }
            PathGroup pathGroup = new PathGroup();
            previousPaths.paths().forEach(p1 -> {
                currentPaths.paths().forEach(p2 -> {
                    Path path = new Path();
                    path.addAllCondition(p1.conditions());
                    path.addAllCondition(p2.conditions());
                    pathGroup.addPath(path);
                });
            });
            previousPaths = pathGroup;
        }
        return previousPaths;
    }
}
