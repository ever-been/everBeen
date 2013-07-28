package cz.cuni.mff.d3s.been.web.pages.bpkpackage;

import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.bpk.BpkConfigurationException;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.pages.Page;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.got5.tapestry5.jquery.JQueryEventConstants;

/**
 * User: donarus
 * Date: 4/28/13
 * Time: 12:34 PM
 */
@Page.Navigation(section = Layout.Section.PACKAGE_UPLOAD)
public class Upload extends Page {

	@Property
	private String message;

	@InjectComponent
	private Zone uploadResult;

	@Inject
	private ComponentResources resources;

	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;

	@OnEvent(component = "uploadBpk", value = JQueryEventConstants.AJAX_UPLOAD)
	void onUpload(UploadedFile uploadedFile) throws BeenApiException {

		try {
			api.getApi().uploadBpk(uploadedFile.getStream());
		} catch (BpkConfigurationException e) {
			message = "Cannot store uploaded BPK in repository: " + e.getMessage();
			ajaxResponseRenderer.addRender("uploadResult", uploadResult);
			return;
		}

		message = "Successfully uploaded package.";
		ajaxResponseRenderer.addRender("uploadResult", uploadResult);
	}

}
