package labs.franklee.engine.logic.path;

import java.util.ArrayList;
import java.util.List;

public class PathGroup {
    private List<Path> paths = new ArrayList<>();

    public PathGroup() {}

    public PathGroup(Path path) {
        this.paths.add(path);
    }

    public List<Path> paths() {
        return this.paths;
    }

    public void addPath(Path path) {
        this.paths.add(path);
    }

}
