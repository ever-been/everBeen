package cz.cuni.mff.d3s.been.web.components.task;

import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;

import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.web.components.Component;
import cz.cuni.mff.d3s.been.web.pages.DetailPage;

/**
 * User: donarus Date: 4/29/13 Time: 2:16 PM
 */
public class Submit extends Component {

	@Property
	private TaskDescriptor taskDescriptor;

	@InjectComponent
	private Zone submitZone;

	@Parameter(required = true)
	private DetailPage<TaskDescriptor> detailPage;

	public Object onSubmitFromBeanEditForm() {
		try {
			String id = api.getApi().submitTask(taskDescriptor);
			detailPage.set(id);
            return detailPage;
		} catch (Exception e) {
            alertManager.alert(Duration.TRANSIENT, Severity.ERROR,e.getMessage());
		}
		return submitZone;
	}
}
