package cz.cuni.mff.d3s.been.web.pages.bpkpackage;

import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.web.pages.Page;
import org.apache.tapestry5.annotations.Property;

import java.util.Collection;

import static cz.cuni.mff.d3s.been.web.components.Layout.Section;

/**
 * User: donarus Date: 4/28/13 Time: 12:35 PM
 */
@Page.Navigation(section = Section.PACKAGE_LIST)
public class List extends Page {

	@Property
	Collection<BpkIdentifier> packages;

	@Property
	BpkIdentifier pkg;

	private void onActivate() {
		this.packages = this.api.getApi().getBpks();
	}

}
