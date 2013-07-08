package cz.cuni.mff.d3s.been.storage;

import cz.cuni.mff.d3s.been.core.persistence.EntityCarrier;
import cz.cuni.mff.d3s.been.persistence.PersistAction;
import cz.cuni.mff.d3s.been.persistence.DAOException;
import cz.cuni.mff.d3s.been.storage.Storage;

/**
 * Created with IntelliJ IDEA.
 * User: darklight
 * Date: 5/4/13
 * Time: 3:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class StoragePersistAction implements PersistAction<EntityCarrier>{
    private  final Storage storage;

    /**
     * Create a persist action over a Storage instance.
     *
     * @param storage Storage to use with this action
     *
     * @return The persist action
     */
    public static StoragePersistAction createForStore(Storage storage) {
        return new StoragePersistAction(storage);
    }

    private StoragePersistAction(Storage storage) {
        this.storage = storage;
    }

    @Override
    public void persist(EntityCarrier what) throws DAOException {
        storage.store(what.getEntityId(),what.getEntityJSON());
    }
}
