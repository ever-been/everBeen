package cz.cuni.mff.d3s.been.web.components;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.BeanEditForm;

public class BeenBeanEditForm extends BeanEditForm {

	@Property
	private String banner = "Connection failed.";

}
