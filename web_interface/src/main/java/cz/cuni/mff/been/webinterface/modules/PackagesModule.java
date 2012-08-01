/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: David Majda
 *
 *  GNU Lesser General Public License Version 2.1
 *  ---------------------------------------------
 *  Copyright (C) 2004-2006 Distributed Systems Research Group,
 *  Faculty of Mathematics and Physics, Charles University in Prague
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License version 2.1, as published by the Free Software Foundation.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *  MA  02111-1307  USA
 */
package cz.cuni.mff.been.webinterface.modules;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import cz.cuni.mff.been.common.ComponentInitializationException;
import cz.cuni.mff.been.common.DownloadHandle;
import cz.cuni.mff.been.common.UploadHandle;
import cz.cuni.mff.been.common.UploadStatus;
import cz.cuni.mff.been.common.Version;
import cz.cuni.mff.been.services.Service;
import cz.cuni.mff.been.softwarerepository.AttributeInfo;
import cz.cuni.mff.been.softwarerepository.MatchException;
import cz.cuni.mff.been.softwarerepository.PackageMetadata;
import cz.cuni.mff.been.softwarerepository.PackageType;
import cz.cuni.mff.been.softwarerepository.SoftwareRepositoryInterface;
import cz.cuni.mff.been.softwarerepository.SoftwareRepositoryService;
import cz.cuni.mff.been.webinterface.InvalidParamValueException;
import cz.cuni.mff.been.webinterface.MissingParamException;
import cz.cuni.mff.been.webinterface.Routines;
import cz.cuni.mff.been.webinterface.UploadResult;
import cz.cuni.mff.been.webinterface.event.Event;
import cz.cuni.mff.been.webinterface.event.EventListener;
import cz.cuni.mff.been.webinterface.packages.Condition;
import cz.cuni.mff.been.webinterface.packages.Operator;
import cz.cuni.mff.been.webinterface.packages.PackageDetailsQueryCallback;
import cz.cuni.mff.been.webinterface.packages.PackageListQueryCallback;
import cz.cuni.mff.been.webinterface.ref.ServiceReference;
import cz.cuni.mff.been.webinterface.ref.TaskManagerReference;

/**
 * Web interface module for the Software Repository.
 * 
 * @author David Majda
 */
public class PackagesModule extends Module implements EventListener {
	private static final String ATOMS_SESSION_KEY = "packagesListAtoms";
	private static final String CONDITIONS_SESSION_KEY = "packagesListConditions";

	/** Class instance (singleton pattern). */
	private static PackagesModule instance;

	private TaskManagerReference taskManager = new TaskManagerReference();
	private ServiceReference<SoftwareRepositoryInterface> softwareRepository
		= new ServiceReference<SoftwareRepositoryInterface>(
			taskManager,
			SoftwareRepositoryService.SERVICE_NAME,
			Service.RMI_MAIN_IFACE,
			SoftwareRepositoryService.SERVICE_HUMAN_NAME
		);
	
	/** Enumerates reasons, why no item is displayed in the package list. */
	public enum NoPackagesReason {
		/** Initial list display, filtering conditions not specified yet. */
		INITIAL,
		/** No package matches given filtering conditions. */
		NO_MATCH,
		/** Some error occured when specifiing filtering conditions. */
		ERROR
	}
	
	/**
	 * Little utility class, which compares package metadata (
	 * <code>PackageMetadata</code> objects) in the package list, so the packages
	 * can be sorted alphabetically by their package name.
	 * 
	 * @author David Majda
	 */
	private static class PackageListComparator implements Comparator<PackageMetadata> {
		/**
		 * Compares two package metadata (<code>PackageMetadata</code> objects).
		 * Comparison key is the package name (comparison is alphabetical and
		 * case-insensitive).
		 *   
		 * @param o1 first package metadata
		 * @param o2 second package metadata
		 * @return a negative integer, zero, or a positive integer as the first
		 *          argument is less than, equal to, or greater than the second
		 */
		public int compare(PackageMetadata o1, PackageMetadata o2) {
			return o1.getName().compareToIgnoreCase(o2.getName());
		}
	}

	/**
	 * Allocates a new <code>PackagesModule</code> object. Construcor is private so only
	 * instance in <code>instance</code> field can be constructed (singleton
	 * pattern).
	 */
	private PackagesModule() {
		super();
		
		/* Initialize general module info... */
		id = "packages";
		name = "Packages";
		defaultAction = "list";
		
		menu = new MenuItem[] {
				new MenuItem("list", "Packages"), 
				new MenuItem("upload", "Upload package"),
		};

		eventManager.registerEventListener(this);
	}

	/**
	 * Returns the only class instance (singleton pattern).
	 * 
	 * @return class instance
	 */
	public static PackagesModule getInstance() {
		if (instance == null) {
			 instance = new PackagesModule();
		}
		return instance;
	}

	/**
	 * Called by the event manager when status of some service changes
	 * programatically or when the configuation changes.
	 * 
	 * We invalidate remote reference because they could be meaningless now.
	 * 
	 * @param event sent event 
	 */
	public void receiveEvent(Event event) {
		taskManager.drop();
		softwareRepository.drop();
	}

	/**
	 * Invokes method for given action, which is found by reflection.
	 * 
	 * The method is overriden in this class to allow catching and processing
	 * exceptions thrown in executed methods in one place (so no big ugly
	 * <code>try { ... } catch { ... }</code> is needed in each method).
	 * 
	 * @param request HTTP request
	 * @param response HTTP response
	 * @param action action to invoke
	 * @throws IOException if some I/O error occurs
	 * @throws ServletException if some kind of methode invocation error occurs 
	 * @throws InvocationTargetException wraps an exception thrown by the invoked
	 *                                    method
	 *                                      
	 * @see cz.cuni.mff.been.webinterface.modules.Module#invokeMethodForAction(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.String)
	 */
	@Override
	public void invokeMethodForAction(HttpServletRequest request,
			HttpServletResponse response, String action) throws ServletException,
			IOException, InvocationTargetException {
		try {
			super.invokeMethodForAction(request, response, action);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof ConnectException) {
				throw new InvocationTargetException(
					new ConnectException(
						"<strong>Can't execute remote call to the Software Repository."
						+ "</strong><br /><br />"
						+ "Try to reload the page. If the error persists after multiple reloads, "
						+ "go to the <a href=\"../../services/>Services</a> tab and make "
						+ "sure the Software Repository is running.<br /><br/>"
						+ "Most probale causes of this error are network-related problems or "
						+ "crash of the service."
					),
					e.getMessage()
				);
			} else {
				throw e;
			}
		}
	}

	private AttributeInfo getAttributeInfoByName(String attributeName) {
		for (AttributeInfo info: PackageMetadata.ATTRIBUTE_INFO) {
			if (attributeName.equals(info.getName())) {
				return info;
			}
		}
		throw new IllegalArgumentException("Attribute \"" + attributeName + "\" doesn't exist.");
	}

	private Operator getOperatorByKlassAndName(Class< ? > klass, String name) {
		Operator operator;
		
		if ( ( operator = Operator.forClassAndName( klass, name ) ) == null ) {
			throw new IllegalArgumentException("Operator \"" + name + "\" doesn't exist.");
		} else {
			return operator;
		}
	}

	private void ensureValueByKlassAndValue(Class< ? > klass, String value) {
		if (klass.getName().equals("cz.cuni.mff.been.softwarerepository.PackageType")) {
			PackageType.realValueOf(value);
		}
	}

	private Object getValueByKlassAndValue(Class< ? > klass, String value) {
		if (klass.getName().equals("java.lang.String")) {
			return value;
		} else if (klass.getName().equals("java.util.Date")) {
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy H:m:s");
			Date result;
			try {
				result = format.parse(value);
			} catch (ParseException e) {
				result = null;
			}
			if (result == null) {
				throw new IllegalArgumentException("Enter valid date and time (format: \""
					+ format.toPattern() + "\").");
			}
			return result;
		} else if (klass.getName().equals("java.util.ArrayList")) {
			return value;
		} else if (klass.getName().equals("cz.cuni.mff.been.common.Version")) {
			return new Version(value);
		} else if (klass.getName().equals("cz.cuni.mff.been.softwarerepository.PackageType")) {
			return PackageType.realValueOf(value);
		} else {
			assert false: "Should not happen.";
		}
		return null;
	}

	private void ensureConditions(HttpServletRequest request) throws 
			MissingParamException, InvalidParamValueException {
		String[] conditionIds = Routines.split2(",", request.getParameter("condition-ids"));
		
		for (int i = 0; i < conditionIds.length; i++) {
			String attributeParam = params.makeIndexed("attribute", conditionIds[i]);
			String operatorParam = params.makeIndexed("operator", conditionIds[i]);
			String valueParam = params.makeIndexed("value", conditionIds[i]);
			
			params.ensureExist(attributeParam, operatorParam, valueParam);
			
			String attributeName = request.getParameter(attributeParam);
			AttributeInfo attributeInfo = null; // initialized to shut up the compiler 
			
			try {
				attributeInfo = getAttributeInfoByName(attributeName);
				try {
					getOperatorByKlassAndName(
						attributeInfo.getKlass(),
						request.getParameter(operatorParam)
					);
				} catch (IllegalArgumentException e) {
					params.ensureCondition(operatorParam, false);
				}
				try {
					ensureValueByKlassAndValue(
						attributeInfo.getKlass(),
						request.getParameter(params.makeIndexed("value", conditionIds[i]))
					);
				} catch (IllegalArgumentException e) {
					params.ensureCondition(valueParam, false);
				}
			} catch (IllegalArgumentException e) {
				params.ensureCondition(attributeParam, false);
			}
		}
	}
	
	private void checkConditions(HttpServletRequest request) {
		String[] conditionIds = Routines.split2(",", request.getParameter("condition-ids"));
		
		for (int i = 0; i < conditionIds.length; i++) {
			String attributeName = request.getParameter(params.makeIndexed("attribute", conditionIds[i]));
			AttributeInfo attributeInfo = getAttributeInfoByName(attributeName);
			
			try {
				getValueByKlassAndValue(
					attributeInfo.getKlass(),
					request.getParameter(params.makeIndexed("value", conditionIds[i]))
				);
			} catch (IllegalArgumentException e) {
				params.checkCondition(false, e.getMessage());
			}
		}
	}
	
	private ArrayList<HashMap<String, String>> buildConditions(HttpServletRequest request) {
		String[] conditionIds = Routines.split2(",", request.getParameter("condition-ids"));
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		
		for (int i = 0; i < conditionIds.length; i++) {
			HashMap<String, String> item = new HashMap<String, String>();
			item.put("attribute", request.getParameter(params.makeIndexed("attribute", conditionIds[i])));
			item.put("operator", request.getParameter(params.makeIndexed("operator", conditionIds[i])));
			item.put("value", request.getParameter(params.makeIndexed("value", conditionIds[i])));
			result.add(item);
		}
		
		return result;
	}
	
	private Condition[] buildAtoms(HttpServletRequest request) {
		String[] conditionIds = Routines.split2(",", request.getParameter("condition-ids"));
		Condition[] result = new Condition[conditionIds.length];
		
		for (int i = 0; i < conditionIds.length; i++) {
			String attributeName = request.getParameter(params.makeIndexed("attribute", conditionIds[i]));
			AttributeInfo attributeInfo = getAttributeInfoByName(attributeName);
			Operator operator = getOperatorByKlassAndName(
				attributeInfo.getKlass(),
				request.getParameter(params.makeIndexed("operator", conditionIds[i]))
			);
			Object value;
			try {
				value = getValueByKlassAndValue(
					attributeInfo.getKlass(),
					request.getParameter(params.makeIndexed("value", conditionIds[i]))
				);
			} catch (IllegalArgumentException e) {
				value = null;
			}
			result[i] = new Condition(attributeName, operator, value);
		}
		
		return result;
	}
	
	private PackageMetadata[] filterPackages(PackageMetadata[] packages, PackageType type) {
		List<PackageMetadata> result = new LinkedList<PackageMetadata>();
		for (PackageMetadata pakkage: packages) {
			if (pakkage.getType().equals(type)) {
				result.add(pakkage);
			}
		}
		return result.toArray(new PackageMetadata[result.size()]);
	}

	/**
	 * Handles the "list" action. 
	 * 
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws IOException if some I/O error occurs
	 * @throws MissingParamException if some required parameter is missing
	 * @throws ServletException if including the template file fails
	 * @throws InvalidParamValueException if required parameter contains invalid
	 *          value
	 * @throws ComponentInitializationException if the Software Repository can't
	 *                                           be initialized
	 */
	@SuppressWarnings("unchecked") /* This is needed to avoid warning when
	                                  extracting "conditions" variable from the
	                                  session. */
	public void list(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, InvalidParamValueException,
			ComponentInitializationException, MissingParamException {
		if (params.exists("action")) {
			String action = request.getParameter("action");
			if (action.equals("deleted")) {
				infoMessages.addTextMessage("Package deleted successfully.");
			} else {
				throw new InvalidParamValueException("Parameter \"action\" has invalid value.");
			}
		}

		HttpSession session = request.getSession();

		Condition[] atoms;
		ArrayList<HashMap<String, String>> conditions;
		NoPackagesReason noPackagesReason; 
		if (params.exists("condition-ids")) {
			ensureConditions(request);
			checkConditions(request);
			
			conditions = buildConditions(request);
			atoms = buildAtoms(request);
			noPackagesReason = NoPackagesReason.NO_MATCH;
			
			if (errorMessages.isEmpty()) {
				session.setAttribute(CONDITIONS_SESSION_KEY, conditions);
				session.setAttribute(ATOMS_SESSION_KEY, atoms);
			}
		} else {
			/* This is used by Selenium tests - it ensures there will be no conditions
			 * remembered from previous queries.
			 */ 
			if (params.exists("reset-conditions")) {
				session.setAttribute(CONDITIONS_SESSION_KEY, null);
				session.setAttribute(ATOMS_SESSION_KEY, null);
			}
			
			/// TODO: GENERICS enabled API should be used here.
			conditions = (ArrayList<HashMap<String, String>>) session.getAttribute(CONDITIONS_SESSION_KEY);
			if (conditions == null) {
				conditions = new ArrayList<HashMap<String, String>>();
			}
			atoms = (Condition[]) session.getAttribute(ATOMS_SESSION_KEY);
			if (atoms == null) {
				atoms = new Condition[] {};
			}
			noPackagesReason = NoPackagesReason.INITIAL;
		}

		
		PackageMetadata[] packages = {};
		if (errorMessages.isEmpty()) {
			PackageListQueryCallback queryInterface = new PackageListQueryCallback(atoms);
			try {
				packages = softwareRepository.get().queryPackages(queryInterface);
			} catch (MatchException e) {
				assert false: "If you end up here, you are doomed"; 
			}
		} else {
			noPackagesReason = NoPackagesReason.ERROR;
		}
		
		Arrays.sort(packages, new PackageListComparator());
		PackageMetadata[] sourcePackages = filterPackages(packages, PackageType.SOURCE);
		PackageMetadata[] binaryPackages = filterPackages(packages, PackageType.BINARY);
		PackageMetadata[] taskPackages = filterPackages(packages, PackageType.TASK);
		PackageMetadata[] dataPackages = filterPackages(packages, PackageType.DATA);
                PackageMetadata[] modulePackages = filterPackages(packages, PackageType.MODULE);
		
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("conditions", conditions);
		data.put("atoms", atoms);
		data.put("sourcePackages", sourcePackages);
		data.put("binaryPackages", binaryPackages);
		data.put("taskPackages", taskPackages);
		data.put("dataPackages", dataPackages);
                data.put("modulePackages", modulePackages);
		data.put("noPackagesReason", noPackagesReason);

		page.setTitle("Packages");
		page.writeHeader();
		page.writeTemplate("packages-list", data);
		page.writeFooter();
	}

	/**
	 * Uploads the package from specified <code>InputStream</code>
	 * to the software respository.
	 * 
	 * @param inputStream input stream providing package contents
	 * @return package upload result (value form <code>UploadStatus</code>
	 *          enumeration and error messages)
	 * @throws IOException if some I/O error occurs
	 * @throws ComponentInitializationException if the Software Repository can't
	 *                                           be initialized
	 */
	private UploadResult uploadPackage(InputStream inputStream) throws IOException,
			ComponentInitializationException {
		UploadStatus status = UploadStatus.ACCEPTED;
		String[] errorMessages = new String[0];
	    
		ServerSocket serverSocket = new ServerSocket(0); // 0 = use any port
		UploadHandle handle = softwareRepository.get().beginPackageUpload(
				InetAddress.getLocalHost(), 
				serverSocket.getLocalPort());
		byte[] buffer = new byte[SoftwareRepositoryInterface.UPLOAD_BUFFER_SIZE];
		int bytesRead;
		Socket socket = serverSocket.accept();
		try {
			OutputStream outputStream = new BufferedOutputStream(
				socket.getOutputStream(),
				SoftwareRepositoryInterface.UPLOAD_BUFFER_SIZE
			);
			InputStream bufferedInputStream = new BufferedInputStream(
				inputStream,
				SoftwareRepositoryInterface.UPLOAD_BUFFER_SIZE
			);
			try {
				while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
			} finally {
				outputStream.close();
				bufferedInputStream.close();
			}
		} finally {
			socket.close();
			serverSocket.close();
	
			/*
			 * Wait until the Software Repository finishes its processing and sets
			 * some meaningful state. Finish the upload then.
			 */
			do {
				status = softwareRepository.get().getPackageUploadStatus(handle);
			} while (status == UploadStatus.UPLOADING || status == UploadStatus.INITIALIZING);
			if (status == UploadStatus.REJECTED) {
				errorMessages = softwareRepository.get().getUploadErrorMessages(handle);
			}
			softwareRepository.get().endPackageUpload(handle);
		}
		return new UploadResult(status, errorMessages);
	}

	/**
	 * Handles the "upload" action.
	 * 
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws MissingParamException if some required parameter is missing
	 * @throws IOException if some I/O error occurs
	 * @throws InvalidParamValueException if required parameter contains invalid
	 *          value
	 * @throws ServletException if including the template file fails
	 * @throws ComponentInitializationException if the Software Repository can't
	 *                                          be initialized
	 */
	@SuppressWarnings("unchecked")
	public void upload(HttpServletRequest request, HttpServletResponse response)
			throws MissingParamException, ServletException, IOException,
			ComponentInitializationException, InvalidParamValueException {	
		HashMap<String, Object> data = new HashMap<String, Object>();
	
		if (params.exists("action")) {
			String action = request.getParameter("action");
			if (action.equals("uploaded")) {
				infoMessages.addTextMessage("Package uploaded successfully.");
			} else {
				throw new InvalidParamValueException("Parameter \"action\" has invalid value.");
			}
		}
	
		/* Ideally, the presence of the query parameter "upload" should be checked
		 * here - for consistency with other forms. Unfortunately, when the form
		 * data is sent as "multipart/form-data" (necessary to upload files), Tomcat
		 * doesnt' understand it and leaves the request unparsed => we can't
		 * directly access the query parameters.
		 * 
		 * Workaround is to check the request type - if POSTing, we're uploading,
		 * if GETting, we're only viewing the form.
		 */ 
		if (params.requestMethodIsPost()) {
			ServletFileUpload upload = new ServletFileUpload();
			upload.setFileItemFactory(new DiskFileItemFactory());
			
			List< FileItem > fileItems = null;
			try {
				/// TODO: This is where the warning comes from. Use newer API to fix that.
				fileItems = upload.parseRequest(request);
			} catch (FileUploadException e) {
				errorMessages.addTextMessage("Error uploading package:" + e.getMessage());
			}
			
			FileItem fileItem = null;
			if (fileItems != null) {																// '!= null' == errMsg.isEmpty()
				/* Index "0" is the index of the field in the upload form. Needs to be
				 * synchronized with /webinterface/templates/package-upload.jsp.
				 */
				fileItem = fileItems.get(0);
				params.checkCondition(!fileItem.getName().equals(""), "Enter the package file.");
			}

			if (fileItem != null && errorMessages.isEmpty()) {
				InputStream inputStream = fileItem.getInputStream();
				try {
					try {
						UploadResult result = uploadPackage(inputStream);
						switch (result.getStatus()) {
							case REJECTED:
								String message = "<strong>Package was "
									+ "rejected by the Software Repository. Reported errors:</strong><br /><br />";
								message += "<ul>";
								for (String errorMessage: result.getErrorMessages()) {
									message += "<li>";
									message += Routines.htmlspecialchars(errorMessage);
									message += "</li>";
								}
								message += "</ul>";
								errorMessages.addHTMLMessage(message);
								break;
							case ERROR:
								errorMessages.addTextMessage("Error uploading package: Software Repository reports error.");
								break;
							default:
								/* Just pass... */
						}
					} finally {
						inputStream.close();
					}
				} catch (IOException e) {
					errorMessages.addTextMessage("Error uploading package:" + e.getMessage());
				}
			}
	
			if (errorMessages.isEmpty()) {
				HashMap<String, String> actionParams = new HashMap<String, String>();
				actionParams.put("action", "uploaded");
				page.redirectToAction("upload", actionParams);
				return;
			} else {
				data.put("file", fileItem == null ? "[NULL]" : fileItem.getName());
			}
		} else {
			data.put("file", "");
		}
		
		page.setTitle("Upload package");
		page.setFocusedElement(0, "file");
		page.writeHeader();
		page.writeTemplate("packages-upload", data);
		page.writeFooter();
	}

	/**
	 * Returns the metadata of the package with specified filename.
	 * 
	 * @param filename package filename
	 * @return metadata of the package with specified filename
	 *          or <code>null</code> if no such package exists in the Software
	 *          Repository
	 * @throws RemoteException when something in RMI goes bad
	 * @throws ComponentInitializationException if the Software Repository can't
	 *                                           be initialized
	 */
	private PackageMetadata getPackageMetadataByFilename(String filename)
			throws RemoteException, ComponentInitializationException {
		PackageDetailsQueryCallback callback = new PackageDetailsQueryCallback(filename);
		PackageMetadata[] metadata = null;
		try {
			metadata = softwareRepository.get().queryPackages(callback);
			return metadata.length == 1 ? metadata[0] : null; 
		} catch (MatchException e) {
			assert false: "If you end up here, you are doomed"; 
			return null;																			// Just formal...
		}
  }

	/**
	 * Handles the "details" action.
	 * 
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws MissingParamException if some required parameter is missing
	 * @throws InvalidParamValueException if required parameter contains invalid
	 *          value
	 * @throws IOException if some I/O error occurs
	 * @throws ServletException if including the template file fails
	 * @throws ComponentInitializationException if the Software Repository can't
	 *                                           be initialized
	 */
	public void details(HttpServletRequest request, HttpServletResponse response)
			throws MissingParamException, InvalidParamValueException, 
			ServletException, IOException, ComponentInitializationException {
		params.ensureExists("package");

		PackageMetadata metadata = getPackageMetadataByFilename(
			request.getParameter("package")
		);
		params.ensureCondition("package", metadata != null);

		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("metadata", metadata);

		page.setTitle("Package details: " + (null == metadata ? "[NULL]" : metadata.getName()));
		page.writeHeader();
		page.writeTemplate("packages-details", data);
		page.writeFooter();
	}

  /**
	 * Download the package with specified filename from the Software Repository
	 * and sends its contents directly to the output.
	 * 
	 * @param filename package filename
	 * @param response HTTP response
	 * @throws IOException if some I/O error occurs
	 * @throws ComponentInitializationException if the Software Repository can't
	 *                                           be initialized
	 * @throws RemoteException when something in RMI goes bad
	 */
	private void downloadPackage(String filename, HttpServletResponse response)
			throws IOException, ComponentInitializationException {
		ServerSocket serverSocket = new ServerSocket(0); // 0 = use any port
		DownloadHandle handle = softwareRepository.get().beginPackageDownload(
			filename, InetAddress.getLocalHost(),
			serverSocket.getLocalPort()
		);
		byte[] buffer = new byte[SoftwareRepositoryInterface.DOWNLOAD_BUFFER_SIZE];
		int bytesRead;
		Socket socket = serverSocket.accept();
		try {
			InputStream inputStream = new BufferedInputStream(
				socket.getInputStream(),
				SoftwareRepositoryInterface.DOWNLOAD_BUFFER_SIZE
			);
			OutputStream outputStream = new BufferedOutputStream(
				response.getOutputStream(),
				SoftwareRepositoryInterface.DOWNLOAD_BUFFER_SIZE
			);
			try {
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);     
				}
			}	finally {
				inputStream.close();
				outputStream.close();
			}
		} finally {
			socket.close();
			serverSocket.close();
			softwareRepository.get().endPackageDownload(handle);
		}
	}

  /**
	 * Handles the "download" action.
	 * 
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws MissingParamException if some required parameter is missing
	 * @throws InvalidParamValueException if required parameter contains invalid
	 *          value
	 * @throws IOException if some I/O error occurs
	 * @throws ServletException if including the template file fails
	 * @throws ComponentInitializationException if the Software Repository can't
	 *                                           be initialized
	 */
	public void download(HttpServletRequest request, HttpServletResponse response)
			throws MissingParamException, InvalidParamValueException, 
			ServletException, IOException, ComponentInitializationException {
		params.ensureExists("package");
		
		response.setContentType(SoftwareRepositoryInterface.PACKAGE_MIME_TYPE);
		response.setHeader("Content-Disposition", "attachment; filename=\""
			+ request.getParameter("package") + "\"");
		downloadPackage(request.getParameter("package"), response);
	}

	/**
 	 * Handles the "delete" action.
 	 * 
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws IOException if some I/O error occurs
	 * @throws MissingParamException if some required parameter is missing
	 * @throws InvalidParamValueException if required parameter contains invalid
	 *          value
	 * @throws ComponentInitializationException if the Software Repository can't
	 *                                           be initialized
	 */
	public void delete(HttpServletRequest request, HttpServletResponse response)
			throws IOException, MissingParamException, InvalidParamValueException,
			ComponentInitializationException {
		params.ensureExists("package");
		
		PackageMetadata metadata = getPackageMetadataByFilename(
			request.getParameter("package")
		);
		params.ensureCondition("package", metadata != null);
		
		softwareRepository.get().deletePackage(request.getParameter("package"));
		
		HashMap<String, String> actionParams = new HashMap<String, String>();
		actionParams.put("action", "deleted");
		page.redirectToAction("list", actionParams);
	}

	/**
	 * Handles the "javascript-attribute-info" action. 
	 * 
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws IOException if some I/O error occurs
	 * @throws ServletException if including the template file fails
	 */
	public void javascriptAttributeInfo(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/javascript");
		page.writeTemplate("packages-javascript-attribute-info", null);
	}
}
