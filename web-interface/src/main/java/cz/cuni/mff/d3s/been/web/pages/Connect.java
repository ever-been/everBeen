package cz.cuni.mff.d3s.been.web.pages;

import cz.cuni.mff.d3s.been.web.components.Layout;

/**
 * User: donarus Date: 4/27/13 Time: 11:29 AM
 */

@Page.Navigation(section = Layout.Section.CONNECT)
public class Connect extends Page {

	@Override
	Object onActivate() {
		return null;
	}

    public Class<Overview> getSuccessPage() {
        return Overview.class;
    }

}
