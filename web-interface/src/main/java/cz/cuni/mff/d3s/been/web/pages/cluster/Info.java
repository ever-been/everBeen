package cz.cuni.mff.d3s.been.web.pages.cluster;

import com.hazelcast.core.Member;
import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.core.service.ServiceInfo;
import cz.cuni.mff.d3s.been.core.service.ServiceState;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.pages.Page;
import org.apache.tapestry5.annotations.Property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Kuba Brecka
 */
@Page.Navigation(section = Layout.Section.CLUSTER_INFO)
public class Info extends Page {

    @Property
    Member member;

    public Collection<Member> getClusterMembers() throws BeenApiException {
        return this.api.getApi().getClusterMembers();
    }

    @Property
    ServiceInfo service;

    public Collection<ServiceInfo> getClusterServices() throws BeenApiException {
        ServiceInfo info;
        return this.api.getApi().getClusterServices();
    }

    public boolean isError(ServiceState state) {
        return ServiceState.ERROR.equals(state);
    }

    public boolean isWarn(ServiceState state) {
        return ServiceState.WARN.equals(state);
    }
}
