package cz.cuni.mff.d3s.been.web.pages.task;

import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.pages.DetailPage;
import cz.cuni.mff.d3s.been.web.pages.Page;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;

/**
 * User: donarus Date: 4/29/13 Time: 2:14 PM
 */
@Page.Navigation(section = Layout.Section.TASK_SUBMIT)
public class Submit extends Page {

    @InjectPage
    @Property
    private Detail detailPage;

}
