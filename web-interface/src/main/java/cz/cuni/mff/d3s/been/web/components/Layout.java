package cz.cuni.mff.d3s.been.web.components;

import cz.cuni.mff.d3s.been.web.pages.Page;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class Layout {

	@Property
	@Parameter(required = true, defaultPrefix = BindingConstants.LITERAL)
	private String section;

	public String classNameForSection(String mySection) {
		if (section.equals(mySection))
			return "active";

		return "";
	}

}
