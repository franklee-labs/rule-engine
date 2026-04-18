package labs.franklee.celero.rules;

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
     * all paths to match this rule
     */
    private PathGroup pathGroup;


}
