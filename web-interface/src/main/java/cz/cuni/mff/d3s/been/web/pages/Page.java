package cz.cuni.mff.d3s.been.web.pages;

import cz.cuni.mff.d3s.been.web.services.BeenApiService;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static cz.cuni.mff.d3s.been.web.components.Layout.Section;

/**
 * User: donarus Date: 4/27/13 Time: 1:05 PM
 */
public abstract class Page {

	@Inject
	@Property
	protected BeenApiService api;

	@Inject
	protected Logger log;


    public Section getSection() {

        Navigation sectionAnnotation = this.getClass().getAnnotation(Navigation.class);
        if (sectionAnnotation != null) {
            return sectionAnnotation.section();
        }
        return null;
    }

	Object onActivate() {
		if (!api.isConnected()) {
			log.info("Been Api is not connected. Redirecting to Connect page.");
			return Connect.class;
		}
		return null;
	}

    /**
     * User: donarus Date: 4/28/13 Time: 1:40 PM
     */

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface Navigation {
        public Section section();
    }
}
