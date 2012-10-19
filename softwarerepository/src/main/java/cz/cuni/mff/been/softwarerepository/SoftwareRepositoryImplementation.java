/*
 * BEEN: Benchmarking Environment ==============================
 * 
 * File author: David Majda
 * 
 * GNU Lesser General Public License Version 2.1
 * --------------------------------------------- Copyright (C) 2004-2006
 * Distributed Systems Research Group, Faculty of Mathematics and Physics,
 * Charles University in Prague
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License version 2.1, as published
 * by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package cz.cuni.mff.been.softwarerepository;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cz.cuni.mff.been.common.DownloadHandle;
import cz.cuni.mff.been.common.DownloadStatus;
import cz.cuni.mff.been.common.UploadHandle;
import cz.cuni.mff.been.common.UploadStatus;
import cz.cuni.mff.been.hostruntime.PackageConfiguration;
import cz.cuni.mff.been.task.CurrentTaskSingleton;
import cz.cuni.mff.been.task.Task;
import cz.cuni.mff.been.task.TaskException;
import static cz.cuni.mff.been.softwarerepository.PackageNames.*;

/**
 * Implementation of the main Software Repository interface.
 * 
 * @author David Majda
 */
public class SoftwareRepositoryImplementation extends UnicastRemoteObject
		implements SoftwareRepositoryInterface {

	private static final long serialVersionUID = 429458673782781390L;

	/** Class instance (singleton pattern). */
	private static SoftwareRepositoryImplementation instance;

	/** Prefix for temporary files used by Software Repository. */
	private static final String TEMPFILE_PREFIX = "been-software-repository";
	/** Suffix for temporary files used by Software Repository. */
	private static final String TEMPFILE_SUFFIX = null; // null = use default

	/** Name of the file where the autoincremented counter value is stored. */
	private static final String COUNTER_FILE = "counter.dat";

	/** Name of the directory with data files. */
	private String dataDir;
	/** Name of the directory with temporary files. */
	private String tempDir;
	/** Software Repository's task. */
	private final Task task;

	/** Package metadata. */
	private final List<PackageMetadata> packageMetadata = new LinkedList<PackageMetadata>();
	/**
	 * Counter used to generate unique file names for packages in Software
	 * Repository. It is incremented automatically when needed.
	 */
	private long autoIncrementedCounter;

	/** Download statuses. */
	private final Map<DownloadHandle, DownloadStatus> downloadStatuses = Collections
			.synchronizedMap(new HashMap<DownloadHandle, DownloadStatus>());
	/** Upload statuses. */
	private final Map<UploadHandle, UploadStatus> uploadStatuses = Collections
			.synchronizedMap(new HashMap<UploadHandle, UploadStatus>());
	/** Upload error messages. */
	private final Map<UploadHandle, String[]> uploadErrorMessages = Collections
			.synchronizedMap(new HashMap<UploadHandle, String[]>());

	/**
	 * Allocates a new <code>SoftwareRepositoryImplementation</code> object.
	 * Construcor is private so only instance in <code>instance</code> field can
	 * be constructed (singleton pattern).
	 * 
	 * @throws RemoteException
	 *             if failed to export object
	 */
	private SoftwareRepositoryImplementation() throws RemoteException {
		super();
		this.task = CurrentTaskSingleton.getTaskHandle();
	}

	/**
	 * @return the dataDir
	 */
	public String getDataDir() {
		return dataDir;
	}

	/**
	 * @return the tempDir
	 */
	public String getTempDir() {
		return tempDir;
	}

	/**
	 * Returns the only class instance (singleton pattern).
	 * 
	 * @return class instance
	 * @throws RemoteException
	 *             if failed to export object s
	 */
	public static SoftwareRepositoryImplementation getInstance()
			throws RemoteException {
		if (instance == null) {
			instance = new SoftwareRepositoryImplementation();
		}
		return instance;
	}

	/**
	 * Initializes the service, i.e. reads the metadata form the index file or
	 * tries to extract it form the packages.
	 * 
	 * @param dataDir
	 *            name of the directory with data files
	 * @param tempDir
	 *            name of the directory with temporary files
	 * @throws IOException
	 *             if some I/O error occurs
	 * @throws ClassNotFoundException
	 *             if error during loading metadata occurs
	 */
	public void initialize(String dataDir, String tempDir) throws IOException,
			ClassNotFoundException {
		this.dataDir = dataDir;
		this.tempDir = tempDir;
		if (counterFileExists()) {
			logInfo("Counter file found - loading...");
			loadCounter();
			logInfo("Counter loaded OK.");
		}
		logInfo("Extracting metadata from packages...");
		extractMetadataFromPackages();
		logInfo("Metadata extracted and saved OK.");
	}

	/**
	 * Logs an information message. If the Software Repository is run as a task
	 * (i.e. <code>task</code> field is not <code>null</code>), it uses task
	 * logging facility to write the message, otherwise it prints it to the
	 * error output.
	 * 
	 * @param message
	 *            message text
	 */
	private void logInfo(String message) {
		if (task != null) {
			task.logInfo(message);
		} else {
			System.out.println(message);
		}
	}

	/**
	 * Logs an error message. If the Software Repository is run as a task (i.e.
	 * <code>task</code> field is not <code>null</code>), it uses task logging
	 * facility to write the message, otherwise it prints it to the error
	 * output.
	 * 
	 * @param message
	 *            message text
	 */
	private void logError(String message) {
		if (task != null) {
			task.logError(message);
		} else {
			System.err.println(message);
		}
	}

	/**
	 * Reads XML document from file in a ZIP archive.
	 * 
	 * @param zipFile
	 *            the archive file
	 * @param xmlFilename
	 *            name of the file in the archive with the XML document
	 * @return XML document if the ZIP archive is succesfully opened, file
	 *         <code>xmlFilename</code> found and read and it contains valid XML
	 *         document; <code>null</code> otherwise
	 * @throws IOException
	 *             if some I/O error occurs
	 * @throws ParserConfigurationException
	 *             if serious parser configuration error occurs
	 * @throws FactoryConfigurationError
	 *             if serious parser factory configuration error occurs
	 */
	// @SuppressWarnings("null")
	private Document readXMLDocumentFromZippedFile(
			ZipFile zipFile,
			String xmlFilename) throws IOException,
			ParserConfigurationException {

		Document result;
		InputStream inputStream = null;
		try {
			inputStream = zipFile.getInputStream(zipFile.getEntry(xmlFilename));
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			try {
				result = builder.parse(inputStream);
			} catch (SAXException e) {
				result = null;
			}
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}

		return result;
	}

	/**
	 * Determines, what package type is given XML document describing.
	 * 
	 * @param document
	 *            document to examine
	 * @return resulting package type
	 */
	private PackageType getDocumentPackageType(Document document) {
		AttributeHelper<PackageType> helper = PackageTypeAttributeHelper
				.getInstance();
		Element typeElement = getAttributeElementByName(document, "type");
		if (typeElement == null) {
			return null;
		}
		if (helper.validateInXML(typeElement) != null) {
			return null;
		}
		return helper.readValueFromElement(typeElement);
	}

	/**
	 * Retreives an element associated with given metadata attribute name from
	 * given XML document.
	 * 
	 * @param document
	 *            document to examine
	 * @param name
	 *            attribute name
	 * @return associated element if exactly one element associated with given
	 *         metadata attribute is found; <code>null</code> otherwise
	 */
	private Element getAttributeElementByName(Document document, String name) {
		NodeList list = document.getElementsByTagName(name);
		if (list.getLength() == 1) {
			return (Element) list.item(0);
		} else {
			return null;
		}
	}

	/**
	 * Determines, whether metadata attribute exists in given XML document and
	 * has valid value.
	 * 
	 * @param document
	 *            document to examine
	 * @param info
	 *            information about the attribute
	 * @return empty <code>String</code> array if metadata attribute exists in
	 *         given XML document and has valid value for given attribute type;
	 *         list of found errors in human-readable form otherwise
	 */
	private String validateAttributeExiststenceAndValue(
			Document document,
			AttributeInfo info) {
		Element attributeElement = getAttributeElementByName(
				document,
				info.getName());
		if (attributeElement == null) {
			return METADATA_FILE + ": Missing <" + info.getName()
					+ "> element.";
		}
		String validationResult = info.getHelper().validateInXML(
				attributeElement);
		return validationResult != null ? METADATA_FILE + ": "
				+ validationResult : null;
	}

	/**
	 * Determines, whether document contains all required metadata attributes
	 * for its package type and those attributes contain valid values.
	 * 
	 * @param document
	 *            document to examine
	 * @param packageType
	 *            package type
	 * @return empty <code>String</code> array if if all metadata attributes
	 *         required for given package type exist in given XML document and
	 *         have valid values for their attribute types; list of found errors
	 *         in human-readable form otherwise
	 */
	private String[] validateRequiredAttributesAndValues(
			Document document,
			PackageType packageType) {
		/*
		 * Go through all metadata attributes and if the attribute is required
		 * for given package type, check its presence and validity of contained
		 * information.
		 */
		List<String> result = new LinkedList<String>();
		for (int i = 0; i < PackageMetadata.ATTRIBUTE_INFO.length; i++) {
			AttributeInfo attributeInfo = PackageMetadata.ATTRIBUTE_INFO[i];
			if (attributeInfo.getRequired().contains(packageType)) {
				String errorMessage = validateAttributeExiststenceAndValue(
						document,
						attributeInfo);
				if (errorMessage != null) {
					result.add(errorMessage);
				}
			}
		}
		return result.toArray(new String[result.size()]);
	}

	/**
	 * Determines, whether given package contains valid package metadata file
	 * and reports errors in human-readable form.
	 * 
	 * @param zipFile
	 *            package file
	 * @return empty <code>String</code> array if the package contains valid
	 *         metadata file; list of found errors in human-readable form
	 *         otherwise
	 * @throws IOException
	 *             if some I/O error occurs
	 * @throws ParserConfigurationException
	 *             if serious parser configuration error occurs
	 * @throws FactoryConfigurationError
	 *             if serious parser factory configuration error occurs
	 */
	private String[] validatePackageMetadataFile(ZipFile zipFile)
			throws IOException, ParserConfigurationException {
		Document document = readXMLDocumentFromZippedFile(
				zipFile,
				METADATA_FILE);

		/*
		 * Check that document is not null. If it is, it means it isn't valid
		 * XML file, so report error.
		 */
		if (document == null) {
			return new String[] { METADATA_FILE
					+ ": Not valid XML file." };
		}

		Element documentElement = document.getDocumentElement();

		/* Check that root element is <package>. */
		if (!documentElement.getNodeName().equals("package")) {
			return new String[] { METADATA_FILE
					+ ": Root element must be <package>." };
		}

		/* Determine package type. */
		PackageType packageType = getDocumentPackageType(document);
		if (packageType == null) {
			return new String[] {METADATA_FILE
					+ ": Missing <type> element or invalid value of element <type>." };
		}

		/* Check attributes for given package type. */
		return validateRequiredAttributesAndValues(document, packageType);
	}

	/**
	 * Determines, whether given package contains valid package configuration
	 * file and reports errors in human-readable form. This validation is
	 * performed only for tasks, not for pluggable modules
	 * 
	 * @param zipFile
	 *            package file
	 * @return empty <code>String</code> array if the package contains valid
	 *         configuration file; list of found errors in human-readable form
	 *         otherwise
	 * @throws IOException
	 *             if some I/O error occurs
	 * @throws ParserConfigurationException
	 *             if serious parser configuration error occurs
	 */
	private String[] validatePackageConfigFile(ZipFile zipFile)
			throws IOException, ParserConfigurationException {
		Document document = readXMLDocumentFromZippedFile(
				zipFile,
				CONFIG_FILE);

		try {
			PackageConfiguration.validate(document);
			return new String[0];
		} catch (TaskException e) {
			return new String[] { e.getMessage() };
		}
	}

	/**
	 * Determines, whether given file contains valid package and reports errors
	 * in human-readable form.
	 * 
	 * @param filename
	 *            file to examine
	 * @return empty <code>String</code> array if given file contains valid
	 *         package; list of found errors in human-readable form otherwise
	 * @throws IOException
	 *             if some I/O error occurs
	 * @throws ParserConfigurationException
	 *             if serious parser configuration error occurs
	 * @throws FactoryConfigurationError
	 *             if serious parser factory configuration error occurs
	 */
	private String[] validatePackage(String filename) throws IOException,
			ParserConfigurationException {
		/* Open the package file. In fact, it is a ZIP file. */
		ZipFile zipFile;
		try {
			zipFile = new ZipFile(filename);
		} catch (ZipException e) {
			return new String[] { "Error reading package file." };
		}

		try {
			List<String> result = new LinkedList<String>();

			/* Check, if directory "files" and file "metadata.xml" are present. */
			ZipEntry filesDirEntry = zipFile.getEntry(FILES_DIR + "/");
			if (filesDirEntry == null) {
				result.add("Missing \"" + FILES_DIR + "\" directory.");
			}
			ZipEntry metadataFileEntry = zipFile
					.getEntry(METADATA_FILE);
			if (metadataFileEntry == null) {
				result.add("Missing \"" + METADATA_FILE + "\" file.");
			}
			if (!result.isEmpty()) {
				return result.toArray(new String[result.size()]);
			}

			/* Check the validity of "metadata.xml". */
			result = Arrays.asList(validatePackageMetadataFile(zipFile));
			if (!result.isEmpty()) {
				return result.toArray(new String[result.size()]);
			}

			/*
			 * Note that reading the whole metadata at this point is suboptimal,
			 * but this is no big deal, because the file is small. We may
			 * optimize this later, if necessary.
			 */
			PackageMetadata metadata = readPackageMetadata(filename);

			/*
			 * Check if package file name is consistent with package metadata
			 * content. Check is performed only if validatePackageFilename is
			 * set.
			 */
			/*
			 * Jan Tattermusch: Not checking package filename, the rule for
			 * naming packages didn't make sense (there would be no way how to
			 * generate package names for uploaded packages.
			 * 
			 * if (validatePackageFilename) { result =
			 * Arrays.asList(validatePackageFileName(filename, metadata)); if
			 * (!result.isEmpty()) { return result.toArray(new
			 * String[result.size()]); } }
			 */

			/*
			 * If the package type is "task", we also check if the "config.xml"
			 * exists and is valid.
			 */
			if (metadata.getType().equals(PackageType.TASK)) {
				ZipEntry configFileEntry = zipFile
						.getEntry(CONFIG_FILE);
				if (configFileEntry == null) {
					return new String[] { "Missing \"" + CONFIG_FILE
							+ "\" file." };
				}
				return validatePackageConfigFile(zipFile);
			}
		} finally {
			zipFile.close();
		}

		/* We've passed all the tests now. */
		return new String[0];
	}

	/**
	 * Validates package filename against package metadata content. Package's
	 * filename must be in form "PACKAGE_NAME-VERSION.bpk", where .bpk is
	 * PACKAGE_FILE_SUFFIX.
	 * 
	 * This check is not performed anymore, because there is no universal rule
	 * how packages should be named.
	 * 
	 * @param filename
	 *            package's filename
	 * @param metadata
	 *            package metadata.
	 * @return human readable list of errors. Empty array if package filename is
	 *         consistent.
	 */
	@SuppressWarnings("unused")
	private String[] validatePackageFileName(
			String filename,
			PackageMetadata metadata) {
		String name = metadata.getName();
		String version = metadata.getVersion().toString();
		String expectedFilename = name + "-" + version + FILE_SUFFIX;
		File file = new File(filename);
		if (!file.getName().equals(expectedFilename)) {
			return new String[] { "Package file \"" + filename
					+ "\" must be named \"" + expectedFilename
					+ "\" to be registered as a valid BEEN package." };
		}

		return new String[0];
	}

	/**
	 * Reads metadata form the package. Assumes the package format is correct
	 * (pre-validated).
	 * 
	 * @param filename
	 *            package file name
	 * @return package metadata
	 * @throws IOException
	 *             if some I/O error occurs
	 * @throws ParserConfigurationException
	 *             if serious parser configuration error occurs
	 * @throws FactoryConfigurationError
	 *             if serious parser factory configuration error occurs
	 */
	private PackageMetadata readPackageMetadata(String filename)
			throws IOException, ParserConfigurationException {
		/* Open the package file. In fact, it is a ZIP file. */
		ZipFile zipFile = new ZipFile(filename);
		try {
			/* Extract only file name (without path). */
			int separatorPos = filename.lastIndexOf(File.separator);
			filename = filename.substring(separatorPos + 1);

			/*
			 * Read XML document from the "metadata.xml" and extract metadata
			 * attributes.
			 */
			Document document = readXMLDocumentFromZippedFile(
					zipFile,
					METADATA_FILE);

			Element nameElement = getAttributeElementByName(document, "name");
			Element versionElement = getAttributeElementByName(
					document,
					"version");
			Element hardwarePlatformsElement = getAttributeElementByName(
					document,
					"hardwarePlatforms");
			Element softwarePlatformsElement = getAttributeElementByName(
					document,
					"softwarePlatforms");
			Element typeElement = getAttributeElementByName(document, "type");
			Element humanNameElement = getAttributeElementByName(
					document,
					"humanName");
			Element downloadURLElement = getAttributeElementByName(
					document,
					"downloadURL");
			Element downloadDateElement = getAttributeElementByName(
					document,
					"downloadDate");
			Element sourcePackageFilenameElement = getAttributeElementByName(
					document,
					"sourcePackageFilename");
			Element binaryIdentifierElement = getAttributeElementByName(
					document,
					"binaryIdentifier");
			Element buildConfigurationElement = getAttributeElementByName(
					document,
					"buildConfiguration");
			Element providedInterfacesElement = getAttributeElementByName(
					document,
					"providedInterfaces");

			return new PackageMetadata(
					filename,
					new File(filename).length(),
					StringAttributeHelper.getInstance().readValueFromElement(
							nameElement),
					VersionAttributeHelper.getInstance().readValueFromElement(
							versionElement),
					hardwarePlatformsElement != null ? ArrayListAttributeHelper
							.getInstance().readValueFromElement(
									hardwarePlatformsElement)
							: new ArrayList<Object>(),
					softwarePlatformsElement != null ? ArrayListAttributeHelper
							.getInstance().readValueFromElement(
									softwarePlatformsElement)
							: new ArrayList<Object>(),
					PackageTypeAttributeHelper.getInstance()
							.readValueFromElement(typeElement),
					StringAttributeHelper.getInstance().readValueFromElement(
							humanNameElement),
					downloadURLElement != null ? (String) StringAttributeHelper
							.getInstance().readValueFromElement(
									downloadURLElement) : null,
					downloadDateElement != null ? (Date) DateAttributeHelper
							.getInstance().readValueFromElement(
									downloadDateElement) : new Date(0),
					sourcePackageFilenameElement != null ? (String) StringAttributeHelper
							.getInstance().readValueFromElement(
									sourcePackageFilenameElement) : null,
					binaryIdentifierElement != null ? (String) StringAttributeHelper
							.getInstance().readValueFromElement(
									binaryIdentifierElement) : null,
					buildConfigurationElement != null ? (String) StringAttributeHelper
							.getInstance().readValueFromElement(
									buildConfigurationElement) : null,
					providedInterfacesElement != null ? ArrayListAttributeHelper
							.getInstance().readValueFromElement(
									providedInterfacesElement)
							: new ArrayList<Object>());
		} finally {
			zipFile.close();
		}
	}

	/**
	 * Adds new metadata to the <code>packageMetadata</code> list.
	 * 
	 * @param metadata
	 *            metadata to add
	 * @throws IOException
	 *             if some I/O error occurs
	 */
	private void addMetadata(PackageMetadata metadata) throws IOException {
		synchronized (packageMetadata) {
			packageMetadata.add(metadata);
			saveCounter();
		}
	}

	/**
	 * Deletes the metadata for package specified by it's filename.
	 * 
	 * @param filename
	 *            package do delete
	 */
	private void deleteMetadataForFilename(String filename) {
		synchronized (packageMetadata) {
			/*
			 * Firstly go through the metadata and determine if there is
			 * anything to delete.
			 */
			int index = -1;
			for (int i = 0; i < packageMetadata.size(); i++) {
				if (packageMetadata.get(i).getFilename().equals(filename)) {
					index = i;
					break;
				}
			}

			/* If no item to delete was found, blame the caller. */
			if (index == -1) {
				throw new IllegalArgumentException("Package \""
						+ "\" doesn't exist.");
			}

			/* Do the actual deletion. */
			packageMetadata.remove(index);
		}
	}

	/**
	 * Creates unique package file name from the package metadata and increases
	 * internal counter, which guarantees that the name is unique. This
	 * increment operation must be synchronized.
	 * 
	 * @param metadata
	 *            package metadata
	 * @return name of the package
	 */
	private String createPackageFilename(PackageMetadata metadata) {
		/* Concatenate all hardware platforms into "+"-separated string. */
		String hardwarePlatforms = "";
		ListIterator<?> hardwarePlatformsIterator = metadata
				.getHardwarePlatforms().listIterator();
		while (hardwarePlatformsIterator.hasNext()) {
			if (hardwarePlatformsIterator.nextIndex() > 0) {
				hardwarePlatforms += "+";
			}
			hardwarePlatforms += hardwarePlatformsIterator.next();
		}

		/* Concatenate all software platforms into "+"-separated string. */
		String softwarePlatforms = "";
		ListIterator<?> softwarePlatformsIterator = metadata
				.getSoftwarePlatforms().listIterator();
		while (softwarePlatformsIterator.hasNext()) {
			if (softwarePlatformsIterator.nextIndex() > 0) {
				softwarePlatforms += "+";
			}
			softwarePlatforms += softwarePlatformsIterator.next();
		}

		/* Create the package name. */
		synchronized (this) {
			String result = metadata.getName() + "-" + hardwarePlatforms + "-"
					+ softwarePlatforms + "-" + metadata.getType().getSuffix()
					+ "." + autoIncrementedCounter + FILE_SUFFIX;
			autoIncrementedCounter++;
			return result;
		}
	}

	/**
	 * Walks through packages in the data directory and extracts metadata from
	 * them.
	 * 
	 * @throws IOException
	 *             if some I/O error occurs
	 */
	private void extractMetadataFromPackages() throws IOException {
		synchronized (packageMetadata) {
			packageMetadata.clear();

			/* Java's ugly and verbose way to say "get me files like *.bpk"... */
			File[] packages = new File(dataDir).listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return (name.endsWith(FILE_SUFFIX));
				}
			});

			String[] validationErrors;
			for (File packageFile : packages) {
				try {
					validationErrors = validatePackage(packageFile.getPath());
					if (validationErrors.length == 0) {
						packageMetadata.add(readPackageMetadata(packageFile
								.getPath()));
						logInfo(packageFile.getName() + ": metadata indexed.");
					} else {
						logError(packageFile.getName() + ": invalid package: "
								+ validationErrors[0]);
					}
				} catch (IOException e) {
					/*
					 * Ignore - if the package reading fails, we just don't care
					 * about that file.
					 */
					logError(packageFile.getName()
							+ ": exception while reading: " + e.toString());
				} catch (ParserConfigurationException e) {
					/*
					 * Ignore - if the package reading fails, we just don't care
					 * about that file.
					 */
					logError(packageFile.getName()
							+ ": exception while reading: " + e.toString());
				} catch (FactoryConfigurationError e) {
					/*
					 * Ignore - if the package reading fails, we just don't care
					 * about that file.
					 */
					logError(packageFile.getName()
							+ ": exception while reading: " + e.toString());
				}
			}
			saveCounter();
		}
	}

	/**
	 * Thread used for handling the package upload. It connects to given host
	 * and port, downloads the package file form there, validates its contents
	 * and adds the package into the Software Repository.
	 * 
	 * @author David Majda
	 * @author Andrej Podzimek
	 */
	private abstract class PackageUploadThread extends Thread {

		/**
		 * How many times we should try to create a temp file. See
		 * <code>createTempFile</code> method for explanation, why we need this
		 * constant.
		 */
		private static final int MAX_CREATE_TEMP_FILE_TRY_COUNT = 1024;

		/** Upload handle. */
		private final UploadHandle handle;

		/**
		 * Allocates a new <code>PackageUploadThread</code> object with
		 * specified parameters.
		 * 
		 * @param handle
		 *            upload handle
		 */
		public PackageUploadThread(UploadHandle handle) {
			this.handle = handle;
			setStatus(UploadStatus.INITIALIZING);
			setErrorMessages(new String[] {});
		}

		/**
		 * Sets the upload status to given value.
		 * 
		 * @param status
		 *            upload status
		 */
		private void setStatus(UploadStatus status) {
			uploadStatuses.put(handle, status);
		}

		/**
		 * Sets the error messages.
		 * 
		 * @param errorMessages
		 *            error messages
		 */
		private void setErrorMessages(String[] errorMessages) {
			uploadErrorMessages.put(handle, errorMessages);
		}

		/**
		 * Uploads the package from the uploader to the specified file.
		 * 
		 * @param file
		 *            file to download
		 * @throws IOException
		 *             if some I/O error occurs
		 */
		protected abstract void uploadPackage(File file) throws IOException;

		/**
		 * Deletes specified file if it exists.
		 * 
		 * @param file
		 *            file to delete
		 */
		private void deleteFileIfExists(File file) {
			if (file.exists()) {
				file.delete();
			}
		}

		/**
		 * Creates teporary file in the <code>tmpDir</code> directory.
		 * 
		 * @return the temporary file
		 * 
		 * @throws IOException
		 *             if the temporary file can not be created
		 */
		private File createTempFile() throws IOException {
			/*
			 * There is a bug in the Sun's JVM which causes the
			 * File.createTempFile call throw IOException with message
			 * "Access is denied" occasionally on Windows. We work around the
			 * bug by simply retrying the call multiple times. After a while we
			 * give up, to avoid hanging-up.
			 * 
			 * See http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6325169
			 * for more information about the bug.
			 */
			File result = null;
			int tryCount = 0;
			IOException thrownException;

			do {
				thrownException = null;
				try {
					result = File.createTempFile(
							TEMPFILE_PREFIX,
							TEMPFILE_SUFFIX,
							new File(tempDir));
				} catch (IOException e) {
					thrownException = e;
				}
				tryCount++;
			} while (null != thrownException
					&& tryCount < MAX_CREATE_TEMP_FILE_TRY_COUNT);

			if (null == thrownException) {
				return result;
			} else {
				throw thrownException;
			}
		}

		/**
		 * Connects to given host and port, downloads the package file form
		 * there, validates its contents and adds the package into the Software
		 * Repository.
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			setStatus(UploadStatus.UPLOADING);
			try {
				/* Create the temp file. */
				File tempFile = createTempFile();
				try {
					/*
					 * Download the file form specified host and port to the
					 * temp file.
					 */
					uploadPackage(tempFile);

					/* OK, now let's dig in and see if it is a valid package. */
					String[] errorMessages = validatePackage(tempFile.getPath());
					if (errorMessages.length > 0) {
						throw new PackageUploadException(
								"Tried to upload invalid package.",
								errorMessages);
					}

					/*
					 * Extract the metadata from the file and create an unique
					 * file name. Corresponding method needs to be synchronized
					 * as two uploads can try to create unique name in the same
					 * time.
					 */
					PackageMetadata metadata = readPackageMetadata(tempFile
							.getPath());
					String packageFilename = createPackageFilename(metadata);

					/*
					 * Try to move the file. If the move fails, bail everything
					 * and delete the temp file. The file name won't be reused
					 * and is lost forever.
					 */
					File packageFile = new File(dataDir + File.separator
							+ packageFilename);
					if (!tempFile.renameTo(packageFile)) {
						throw new IOException("Cant't rename file \""
								+ tempFile.getPath() + "\" to \""
								+ packageFile.getPath() + "\".");
					}

					/*
					 * If copying succeded, re-read the metadata (this is hack
					 * only to change the filename) and try adding the package
					 * metadata and serialize it.
					 */
					metadata = readPackageMetadata(packageFile.getPath());
					addMetadata(metadata);
					logInfo("Successfully uploaded package \""
							+ packageFile.getPath() + "\".");
					setStatus(UploadStatus.ACCEPTED);

					/*
					 * After all operations finish (either succesfully or due to
					 * some error), delete the temporary file if it still
					 * exists.
					 */
				} finally {
					deleteFileIfExists(tempFile);
				}
			} catch (IOException e) {
				logError("0 " + e.getMessage());
				setStatus(UploadStatus.ERROR);
			} catch (PackageUploadException e) {
				String[] errorMessages = e.getErrorMessages();
				logError(e.getMessage() + " "
						+ Integer.toString(errorMessages.length)
						+ " messages reported:");
				for (String errorMessage : errorMessages) {
					logError(" " + errorMessage);
				}
				setErrorMessages(errorMessages);
				setStatus(UploadStatus.REJECTED);
			} catch (ParserConfigurationException e) {
				logError("1 " + e.getMessage());
				setStatus(UploadStatus.ERROR);
			} catch (FactoryConfigurationError e) {
				logError("2 " + e.getMessage());
				setStatus(UploadStatus.ERROR);
			}
		}
	}

	/**
	 * Package upload thread implementation using a direct TCP connection.
	 * 
	 * @author Andrej Podzimek
	 */
	private class TCPPackageUploadThread extends PackageUploadThread {

		/** IP address of host to download the package from. */
		private final InetAddress ip;

		/** Port to download the package from. */
		private final int port;

		public TCPPackageUploadThread(UploadHandle handle, InetAddress ip,
				int port) {
			super(handle);
			this.ip = ip;
			this.port = port;
		}

		@Override
		protected void uploadPackage(File file) throws IOException {
			byte[] buffer = new byte[UPLOAD_BUFFER_SIZE];
			int bytesRead;
			Socket socket = new Socket(ip, port);
			try {
				InputStream inputStream = new BufferedInputStream(
						socket.getInputStream(),
						UPLOAD_BUFFER_SIZE);
				OutputStream outputStream = new BufferedOutputStream(
						new FileOutputStream(file.getPath()),
						UPLOAD_BUFFER_SIZE);
				try {
					while ((bytesRead = inputStream.read(buffer)) != -1) {
						outputStream.write(buffer, 0, bytesRead);
					}
				} finally {
					inputStream.close();
					outputStream.close();
				}
			} finally {
				socket.close();
			}
		}
	}

	/**
	 * Package upload thread implementation using the RMIIO library.
	 * 
	 * @author Andrej Podzimek
	 */
	private class RMIIOPackageUploadThread extends PackageUploadThread {

		/** The remote stream through which the package will be received. */
		private final InputStreamInterface stream;

		/**
		 * Creates a new {@code RMIIOPackageDownloadThread} instance and
		 * initializes all the status information.
		 * 
		 * @param handle
		 *            The download handle to be used for this operation.
		 * @param stream
		 */
		public RMIIOPackageUploadThread(UploadHandle handle,
				InputStreamInterface stream) {
			super(handle);
			this.stream = stream;
		}

		@Override
		protected void uploadPackage(File file) throws IOException {
			byte[] buffer = new byte[UPLOAD_BUFFER_SIZE];
			int bytesRead;
			boolean success;

			success = true;
			try {
				InputStream inputStream = stream.getInputStream();
				OutputStream outputStream = new BufferedOutputStream(
						new FileOutputStream(file.getPath()),
						UPLOAD_BUFFER_SIZE);
				try {
					while ((bytesRead = inputStream.read(buffer)) != -1) {
						outputStream.write(buffer, 0, bytesRead);
					}
				} catch (Exception exception) {
					success = false;
				} finally { // Don't close InputStream!!!
					outputStream.close();
				}
			} finally {
				stream.close(success); // Closing InputStream here.
			}
		}
	}

	/**
	 * Thread used for handling the package download.
	 * 
	 * @author David Majda
	 * @author Andrej Podzimek
	 */
	private abstract class PackageDownloadThread extends Thread {

		/** Download handle. */
		private final DownloadHandle handle;

		/** Filename of the package. */
		private final String filename;

		/**
		 * Allocates a new <code>PackageDownloadThread</code> object with
		 * specified parameters.
		 * 
		 * @param handle
		 *            download handle
		 * @param filename
		 *            filename of the package
		 */
		public PackageDownloadThread(DownloadHandle handle, String filename) {
			this.handle = handle;
			this.filename = filename;
			setStatus(DownloadStatus.INITIALIZING);
		}

		/**
		 * Sets the download status to given value.
		 * 
		 * @param status
		 *            download status
		 */
		private void setStatus(DownloadStatus status) {
			downloadStatuses.put(handle, status);
		}

		/**
		 * Uploads the package to the downloader from the specified file.
		 * 
		 * @param file
		 *            file to upload
		 * @throws IOException
		 *             if some I/O error occurs
		 */
		protected abstract void downloadPackage(File file) throws IOException;

		/**
		 * Connects to given host and port and uploads the package file there.
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			setStatus(DownloadStatus.DOWNLOADING);
			File packageFile = new File(dataDir + File.separator + filename);
			try {
				downloadPackage(packageFile);
				setStatus(DownloadStatus.SUCCEEDED);
			} catch (UnknownHostException e) {
				setStatus(DownloadStatus.ERROR);
				logError(e.getMessage());
			} catch (IOException e) {
				setStatus(DownloadStatus.ERROR);
				logError(e.getMessage());
			}
		}

	}

	/**
	 * Package download thread implementation using a direct TCP connection.
	 * 
	 * @author Andrej Podzimek
	 */
	private class TCPPackageDownloadThread extends PackageDownloadThread {

		/** IP of host to upload the package to. */
		private final InetAddress ip;

		/** Port to upload the package to. */
		private final int port;

		/**
		 * Creates a new {@code TCPPackageDownloadThread} instance and
		 * initializes all the status information.
		 * 
		 * @param handle
		 *            The download handle used for this operation.
		 * @param fileName
		 *            Name of the file to transfer.
		 * @param ip
		 *            IP address of the recipient.
		 * @param port
		 *            Port where the recipient is listening.
		 */
		public TCPPackageDownloadThread(DownloadHandle handle, String fileName,
				InetAddress ip, int port) {
			super(handle, fileName);
			this.ip = ip;
			this.port = port;
		}

		@Override
		protected void downloadPackage(File file) throws IOException {
			byte[] buffer = new byte[DOWNLOAD_BUFFER_SIZE];
			int bytesRead;
			Socket socket = new Socket(ip, port);

			try {
				OutputStream outputStream = new BufferedOutputStream(
						socket.getOutputStream(),
						DOWNLOAD_BUFFER_SIZE);
				InputStream inputStream = new BufferedInputStream(
						new FileInputStream(file.getPath()),
						DOWNLOAD_BUFFER_SIZE);
				try {
					while ((bytesRead = inputStream.read(buffer)) != -1) {
						outputStream.write(buffer, 0, bytesRead);
					}
				} finally {
					outputStream.close();
					inputStream.close();
				}
			} finally {
				socket.close();
			}
		}
	}

	/**
	 * Package download thread implementation using the RMIIO library.
	 * 
	 * @author Andrej Podzimek
	 */
	private class RMIIOPackageDownloadThread extends PackageDownloadThread {

		/** The remote stream through which the package will be sent. */
		private final OutputStreamInterface stream;

		/**
		 * Creates a new {@code RMIIOPackageDownloadThread} instance and
		 * initializes all the status information.
		 * 
		 * @param handle
		 *            The download handle used for this operation.
		 * @param fileName
		 *            Name of the file to transfer.
		 * @param stream
		 *            The remote stream through which the file will be sent.
		 */
		public RMIIOPackageDownloadThread(DownloadHandle handle,
				String fileName, OutputStreamInterface stream) {
			super(handle, fileName);
			this.stream = stream;
		}

		@Override
		protected void downloadPackage(File file) throws IOException {
			byte[] buffer = new byte[DOWNLOAD_BUFFER_SIZE];
			int bytesRead;
			boolean success;

			success = true;
			try {
				OutputStream outputStream = stream.getOutputStream();
				InputStream inputStream = new BufferedInputStream(
						new FileInputStream(file.getPath()),
						DOWNLOAD_BUFFER_SIZE);
				try {
					while ((bytesRead = inputStream.read(buffer)) != -1) {
						outputStream.write(buffer, 0, bytesRead);
					}
					outputStream.flush();
				} catch (Exception exception) {
					success = false;
				} finally { // Don't close OutputStream!!!
					inputStream.close();
				}
			} finally {
				stream.close(success); // Closing OutputStream here.
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DownloadHandle beginPackageDownload(
			String filename,
			InetAddress ip,
			int port) throws RemoteException {
		File packageFile = new File(dataDir + File.separator + filename);
		if (!packageFile.exists()) {
			throw new IllegalArgumentException("Package \"" + filename
					+ "\" doesn't exist.");
		}
		DownloadHandle handle = DownloadHandle.createDownloadHandle();
		new TCPPackageDownloadThread(handle, filename, ip, port).start();
		return handle;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DownloadHandle beginPackageDownload(
			String fileName,
			OutputStreamInterface stream) throws RemoteException {
		File packageFile = new File(dataDir + File.separator + fileName);
		if (!packageFile.exists()) {
			throw new IllegalArgumentException("Package \"" + fileName
					+ "\" doesn't exist.");
		}
		DownloadHandle handle = DownloadHandle.createDownloadHandle();
		new RMIIOPackageDownloadThread(handle, fileName, stream).start();
		return handle;
	}

	/**
	 * @see cz.cuni.mff.been.softwarerepository.SoftwareRepositoryInterface#getPackageDownloadStatus(cz.cuni.mff.been.common.DownloadHandle)
	 */
	@Override
	public DownloadStatus getPackageDownloadStatus(DownloadHandle handle)
			throws RemoteException {
		DownloadStatus result = downloadStatuses.get(handle);
		if (result != null) {
			return result;
		} else {
			throw new IllegalArgumentException("Invalid handle.");
		}
	}

	/**
	 * @see cz.cuni.mff.been.softwarerepository.SoftwareRepositoryInterface#endPackageDownload(cz.cuni.mff.been.common.DownloadHandle)
	 */
	@Override
	public void endPackageDownload(DownloadHandle handle)
			throws RemoteException {
		downloadStatuses.remove(handle);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UploadHandle beginPackageUpload(InetAddress ip, int port)
			throws RemoteException {
		UploadHandle handle = UploadHandle.createUploadHandle();
		new TCPPackageUploadThread(handle, ip, port).start();
		return handle;
	}

	@Override
	public UploadHandle beginPackageUpload(InputStreamInterface stream)
			throws RemoteException {
		UploadHandle handle = UploadHandle.createUploadHandle();
		new RMIIOPackageUploadThread(handle, stream).start();
		return handle;
	}

	/**
	 * @see cz.cuni.mff.been.softwarerepository.SoftwareRepositoryInterface#getPackageUploadStatus(cz.cuni.mff.been.common.UploadHandle)
	 */
	@Override
	public UploadStatus getPackageUploadStatus(UploadHandle handle)
			throws RemoteException {
		UploadStatus result = uploadStatuses.get(handle);
		if (result != null) {
			return result;
		} else {
			throw new IllegalArgumentException("Invalid handle.");
		}
	}

	/**
	 * @see cz.cuni.mff.been.softwarerepository.SoftwareRepositoryInterface#getUploadErrorMessages(cz.cuni.mff.been.common.UploadHandle)
	 */
	@Override
	public String[] getUploadErrorMessages(UploadHandle handle)
			throws RemoteException {
		UploadStatus uploadStatus = uploadStatuses.get(handle);
		if (uploadStatus == null) {
			throw new IllegalArgumentException("Invalid handle.");
		}
		if (uploadStatus != UploadStatus.REJECTED) {
			throw new IllegalStateException("Package not rejected.");
		}
		return uploadErrorMessages.get(handle);
	}

	/**
	 * @see cz.cuni.mff.been.softwarerepository.SoftwareRepositoryInterface#endPackageUpload(cz.cuni.mff.been.common.UploadHandle)
	 */
	@Override
	public void endPackageUpload(UploadHandle handle) throws RemoteException {
		uploadStatuses.remove(handle);
		uploadErrorMessages.remove(handle);
	}

	/**
	 * @see cz.cuni.mff.been.softwarerepository.SoftwareRepositoryInterface#deletePackage(java.lang.String)
	 */
	@Override
	public boolean deletePackage(String filename) {
		File packageFile = new File(dataDir + File.separator + filename);
		if (!packageFile.exists()) {
			return false;
		}
		try {
			deleteMetadataForFilename(filename);
		} catch (IllegalArgumentException e) {
			return false;
		}
		if (packageFile.delete()) {
			logInfo("Successfully deleted package \"" + packageFile.getPath()
					+ "\".");
			return true;
		} else {
			logError("Error deleting package \"" + packageFile.getPath()
					+ "\".");
			return true;
		}
	}

	/**
	 * @see cz.cuni.mff.been.softwarerepository.SoftwareRepositoryInterface#queryPackages(cz.cuni.mff.been.softwarerepository.PackageQueryCallbackInterface)
	 */
	@Override
	public PackageMetadata[] queryPackages(
			PackageQueryCallbackInterface callback) throws RemoteException,
			MatchException {
		ArrayList<PackageMetadata> resultList = new ArrayList<PackageMetadata>();
		for (int i = 0; i < packageMetadata.size(); i++) {
			PackageMetadata metadata = packageMetadata.get(i);
			if (callback.match(metadata)) {
				resultList.add(metadata);
			}
		}
		PackageMetadata[] result = new PackageMetadata[resultList.size()];
		return resultList.toArray(result);
	}

	/**
	 * Determines whether the <code>COUNTER_FILE</code> exists.
	 * 
	 * @return <code>true</code> if the file with autoincremented counter
	 *         exists; <code>false</code> otherwise
	 */
	private boolean counterFileExists() {
		File hashFile = new File(dataDir + File.separator + COUNTER_FILE);
		return hashFile.exists();
	}

	/**
	 * Loads autoincremented counter form the <code>COUNTER_FILE</code>.
	 * 
	 * @throws IOException
	 *             if some I/O error occurs
	 */
	private void loadCounter() throws IOException {
		ObjectInputStream inputStream = new ObjectInputStream(
				new FileInputStream(dataDir + File.separator + COUNTER_FILE));
		try {
			autoIncrementedCounter = inputStream.readLong();
		} finally {
			inputStream.close();
		}
	}

	/**
	 * Saved autoincremented counter to the <code>COUNTER_FILE</code>.
	 * 
	 * @throws IOException
	 *             if some I/O error occurs
	 */
	private void saveCounter() throws IOException {
		ObjectOutputStream outputStream = new ObjectOutputStream(
				new FileOutputStream(dataDir + File.separator + COUNTER_FILE));
		try {
			outputStream.writeLong(autoIncrementedCounter);
		} finally {
			outputStream.close();
		}
	}
}
