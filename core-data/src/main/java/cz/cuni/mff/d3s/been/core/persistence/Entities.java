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


    public static final EntityID SERVICE_LOG = new EntityID().withKind("log").withGroup("service");
}
