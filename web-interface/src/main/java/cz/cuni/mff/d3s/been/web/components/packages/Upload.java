package cz.cuni.mff.d3s.been.web.components.packages;

import java.io.File;
import java.util.ArrayList;

import cz.cuni.mff.d3s.been.bpk.BpkConfigurationException;
import cz.cuni.mff.d3s.been.web.components.Component;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.got5.tapestry5.jquery.JQueryEventConstants;
import org.got5.tapestry5.jquery.components.AjaxUpload;

import cz.cuni.mff.d3s.been.api.BeenApi;

/**
 * User: donarus Date: 4/28/13 Time: 11:59 AM
 */
public class Upload extends Component {

	@Persist(PersistenceConstants.FLASH)
	private String message;

	@Persist
	@Property
	private java.util.List<UploadedFile> uploadedFiles;

	@InjectComponent
	private Zone uploadResult;

	@Inject
	private ComponentResources resources;

	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;

    @SetupRender
	void setupRender() {
		if (uploadedFiles == null) {
			uploadedFiles = new ArrayList<UploadedFile>();
		}
	}

	@OnEvent(component = "uploadBpk", value = JQueryEventConstants.AJAX_UPLOAD)
	void onUpload(UploadedFile uploadedFile) {
		if (uploadedFile != null) {
			this.uploadedFiles.add(uploadedFile);
		}

		storeInRepository(uploadedFile);
		message = "This upload was: AJAX_UPLOAD";

		ajaxResponseRenderer.addRender("uploadResult", uploadResult);
	}

	@OnEvent(component = "uploadBpk", value = JQueryEventConstants.NON_XHR_UPLOAD)
	Object onNonXHRUpload(UploadedFile uploadedFile) {
		if (uploadedFile != null) {
			this.uploadedFiles.add(uploadedFile);
		}

		storeInRepository(uploadedFile);
		final JSONObject result = new JSONObject();
		final JSONObject params = new JSONObject().put("url", resources.createEventLink("uploadBpkEvent", "NON_XHR_UPLOAD").toURI()).put("zoneId", "uploadResult");

		result.put(AjaxUpload.UPDATE_ZONE_CALLBACK, params);

		return result;
	}

	private void storeInRepository(UploadedFile uploadedFile) {
        try {
            api.getApi().uploadBpk(uploadedFile.getStream());
        } catch (BpkConfigurationException e) {
            message = "Cannot store uploaded bpk in repository: " + e.getMessage();
            ajaxResponseRenderer.addRender("uploadResult", uploadResult);
        }
    }

	@OnEvent(value = "uploadBpkEvent")
	void onUploadBpkEvent(final String someParam) {
		message = "This upload was: " + someParam;
		ajaxResponseRenderer.addRender("uploadResult", uploadResult);
	}

	void onUploadException(FileUploadException ex) {
		message = "Upload exception: " + ex.getMessage();
		ajaxResponseRenderer.addRender("uploadResult", uploadResult);
	}

	public String getMessage() {
		return message;
	}

}
