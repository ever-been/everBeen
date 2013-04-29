package cz.cuni.mff.d3s.been.web.pages;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.BeanEditForm;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

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

    public Class<Index> getSuccessPage() {
        return Index.class;
    }

}
