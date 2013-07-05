package cz.cuni.mff.d3s.been.web.pages;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.services.HttpError;

/**
 * @author Kuba Brecka
 */
public class Index extends Page {
	Object onActivate(EventContext context) {
		if (context.getCount() == 0) return Overview.class;

		return new HttpError(404, "Resource not found.");
	}
}
