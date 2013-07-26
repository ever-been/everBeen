package cz.cuni.mff.d3s.been.core.persistence;

/**
 * This class holds {@link EntityID} constants associated with storage destinations
 *
 * @author darklight
 */
public final class Entities {

    /**
     * Non-instantiable
     */
    private Entities(){}


    public static final EntityID LOG_SERVICE = new EntityID().withKind("log").withGroup("service");
    public static final EntityID LOG_TASK = new EntityID().withKind("log").withGroup("task");

    public static final EntityID RESULT_EVALUATOR = new EntityID().withKind("result").withGroup("evaluation");
}
