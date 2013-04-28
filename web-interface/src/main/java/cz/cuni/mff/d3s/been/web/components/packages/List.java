package cz.cuni.mff.d3s.been.web.components.packages;

import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.web.components.Component;
import org.apache.tapestry5.annotations.Property;

import java.util.Collection;

/**
 * User: donarus
 * Date: 4/28/13
 * Time: 11:58 AM
 */
public class List extends Component {

    public Collection<BpkIdentifier> getBpkIdentifiers() {
        return this.api.getApi().getBpks();
    }

    @Property
    private BpkIdentifier bpkIdentifier;

}
