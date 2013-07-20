package cz.cuni.mff.d3s.been.web.pages.cluster;

import com.hazelcast.core.Member;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.pages.Page;
import org.apache.tapestry5.annotations.Property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * @author Kuba Brecka
 */
@Page.Navigation(section = Layout.Section.CLUSTER_INFO)
public class Info extends Page {

	@Property
	Member member;

	public Collection<Member> getClusterMembers() {
		return this.api.getApi().getClusterMembers();
	}

	public class ServiceEntry {
		public String name;
		public String info;
	}

	@Property
	ServiceEntry service;

	public Collection<ServiceEntry> getClusterServices() {
		ArrayList<ServiceEntry> list = new ArrayList<ServiceEntry>();

		for (Map.Entry<String, String> entry : this.api.getApi().getClusterServices().entrySet()) {
			ServiceEntry se = new ServiceEntry();
			se.name = entry.getKey();
			se.info = entry.getValue();
			list.add(se);
		}

		return list;
	}
}
