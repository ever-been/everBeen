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
package cz.cuni.mff.been.hostruntime;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.been.common.DownloadHandle;
import cz.cuni.mff.been.softwarerepository.MatchException;
import cz.cuni.mff.been.softwarerepository.PackageMetadata;
import cz.cuni.mff.been.softwarerepository.PackageQueryCallbackInterface;
import cz.cuni.mff.been.softwarerepository.PackageType;
import cz.cuni.mff.been.softwarerepository.SoftwareRepositoryInterface;
import cz.cuni.mff.been.utils.FileUtils;
import cz.cuni.mff.been.utils.ZipUtils;

/**
 * Manages the Host Runtime's package cache.
 * 
 * The cache consists of two directories: <em>cache directory</em> and
 * <em>boot packages direcotory</em>. In the constructor, the cache scans the
 * boot packages direcotry and permanenetly adds found packages to the cache.
 * These packages are not counted to the cache size. The cache directory stores
 * packages which were once downloaded from the Software Repository and they are
 * counted to the cache size.
 * 
 * The only operation of the package cache is <code>extractPackage</code>
 * method, which extracts the contents of the package with given filename to the
 * specified directory (possibly downloading the package from the Software
 * Repository and placing it to the cache).
 * 
 * The class maintains invariant that size of all packages in the package cache
 * direcotry is less than <code>maxCacheSize</code>. If the invariant is about
 * to be broken when downloading new package form the Software Repository, it
 * deletes oldest packages until there is enough space
 * 
 * The class is designed to be thread-safe.
 * 
 * @author David Majda
 */
public class PackageCacheManager {

	private static final Logger logger = LoggerFactory.getLogger(PackageCacheManager.class);

	/**
	 * Immutable class containing information about one package in the cache.
	 * 
	 * @author David Majda
	 */
	private static class Package {

		/**
		 * Types of packages stored in the cache.
		 * 
		 * @author David Majda
		 */
		public static enum Type {

			/** Boot package. */
			BOOT,
			/** Non-boot package. */
			NON_BOOT
		};

		/** Package file name in the cache. */
		private final String filename;
		/** Package size in bytes. */
		private final long size;
		/** Package download time. */
		private final Date downloadTime;
		/**
		 * Package type (boot/non-boot, not the type from the package metadatata).
		 */
		private final Type type;

		/** @return package file name in the cache */
		public String getFilename() {
			return filename;
		}

		/** @return package size in bytes */
		public long getSize() {
			return size;
		}

		/** @return package download time */
		public Date getDownloadTime() {
			return new Date(downloadTime.getTime());
		}

		/**
		 * @return Package type (boot/non-boot, not the type from the package
		 *         metadatata)
		 */
		public Type getType() {
			return type;
		}

		/**
		 * Allocates a new <code>Package</code> object.
		 * 
		 * @param filename
		 *          name package filename in the cache
		 * @param size
		 *          package size in bytes
		 * @param downloadTime
		 *          package download time
		 * @param type
		 *          package type (boot/non-boot, not the type from the package
		 *          metadatata)
		 */
		public Package(String filename, long size, Date downloadTime, Type type) {
			this.filename = filename;
			this.size = size;
			this.downloadTime = downloadTime;
			this.type = type;
		}
	}

	/**
	 * Callback interface to the Software Repository, which checks if the package
	 * has specified type (task by default) and its file name equals to the
	 * specified value.
	 * 
	 * @author David Majda
	 */
	private static class PackageFilenameQueryCallback implements PackageQueryCallbackInterface, Serializable {

		private static final long serialVersionUID = -4917724355368780085L;

		/** Package file name to check. */
		private final String packageFilename;

		/** Package type to check */
		private final PackageType packageType;

		/**
		 * Allocates a new <code>PackageFilenameQueryCallback</code> object.
		 * 
		 * Packages of arbitrary PackageType are matched.
		 * 
		 * @param packageFilename
		 *          package file name to check
		 * @param packageType
		 *          package type to check
		 */
		public PackageFilenameQueryCallback(String packageFilename, PackageType packageType) {
			this.packageFilename = packageFilename;
			this.packageType = packageType;
		}

		/**
		 * @see cz.cuni.mff.been.softwarerepository.PackageQueryCallbackInterface#match(cz.cuni.mff.been.softwarerepository.PackageMetadata)
		 */
		@Override
		public boolean match(PackageMetadata metadata) throws MatchException {
			return metadata.getType().equals(packageType) && metadata.getFilename().equals(packageFilename);
		}
	}

	/**
	 * Callback interface to the Software Repository, which checks if the package
	 * has given type and its name and version match the specified values.
	 * 
	 * @author Jan Tattermusch
	 */
	private static class PackageNameVersionQueryCallback implements PackageQueryCallbackInterface, Serializable {

		private static final long serialVersionUID = -4917724355368780085L;

		/** Package name to check. */
		private final String packageName;

		/** Package version to check. */
		private final String packageVersion;

		/** Package type to check */
		private final PackageType packageType;

		/**
		 * Allocates a new <code>PackageNameVersionQueryCallback</code> object.
		 * 
		 * @param packageName
		 *          package file name to check
		 * @param packageVersion
		 *          package file name to check
		 * @param packageType
		 *          package type to check
		 */
		public PackageNameVersionQueryCallback(String packageName, String packageVersion, PackageType packageType) {
			this.packageName = packageName;
			this.packageVersion = packageVersion;
			this.packageType = packageType;
		}

		/**
		 * @see cz.cuni.mff.been.softwarerepository.PackageQueryCallbackInterface#match(cz.cuni.mff.been.softwarerepository.PackageMetadata)
		 */
		@Override
		public boolean match(PackageMetadata metadata) throws MatchException {
			return metadata.getType().equals(packageType) && metadata.getName().equals(packageName) && metadata.getVersion().toString().equals(packageVersion);
		}
	}

	/** Cache directory. */
	private final String cacheDir;

	/** Directory with boot packages. */
	private final String bootPackagesDir;

	/** Limit of the cache size. */
	private long maxCacheSize;

	/** RMI reference to the Software Repository. */
	private SoftwareRepositoryInterface softwareRepository;

	/** List of packages in the cache. */
	private final List<Package> packages = new LinkedList<Package>();

	/** @return cache directory */
	public String getCacheDir() {
		return cacheDir;
	}

	/** @return directory with boot packages */
	public String getBootPackagesDir() {
		return bootPackagesDir;
	}

	/** @return limit of the cache size */
	public synchronized long getMaxCacheSize() {
		return maxCacheSize;
	}

	/**
	 * Sets the limit of the cache size.
	 * 
	 * @param sizeLimit
	 *          the limit of the cache size to set
	 */
	public synchronized void setMaxCacheSize(long sizeLimit) {
		this.maxCacheSize = sizeLimit;
	}

	/** @return RMI reference to the Software Repository */
	public synchronized SoftwareRepositoryInterface getSoftwareRepository() {
		return softwareRepository;
	}

	/**
	 * Sets the RMI reference to the Software Repository.
	 * 
	 * @param softwareRepository
	 *          the RMI reference to the Software Repository to set
	 */
	public synchronized void setSoftwareRepository(
			SoftwareRepositoryInterface softwareRepository) {
		this.softwareRepository = softwareRepository;
	}

	/**
	 * Finds package in the cache by its name.
	 * 
	 * @param packageFilename
	 *          package name
	 * @return object representing found package or <code>null</code> if the cache
	 *         does not contain package with given name
	 */
	private Package findPackage(String packageFilename) {
		for (Package p : packages) {
			if (p.getFilename().equals(FileUtils.constructPath(cacheDir, packageFilename)) || p.getFilename().equals(FileUtils.constructPath(bootPackagesDir, packageFilename))) {
				return p;
			}
		}
		return null;
	}

	/**
	 * Finds the oldest non-boot package in the cache.
	 * 
	 * @return object representing the oldest non-boot package in the cache or
	 *         <code>null</code> if there are no non-boot packages in the cache
	 */
	private Package findOldestNonBootPackage() {
		Package result = null;
		for (Package p : packages) {
			if (p.getType().equals(Package.Type.NON_BOOT) && (result == null || p.getDownloadTime().compareTo(result.getDownloadTime()) < 0)) {
				result = p;
			}
		}
		return result;
	}

	/**
	 * Deletes a package from the cache.
	 * 
	 * @param pakkage
	 *          package to delete
	 * @return <code>true</code> if the package was successfully deleted;
	 *         <code>false</code> otherwise
	 */
	private boolean deletePackage(Package pakkage) {
		if (new File(pakkage.getFilename()).delete()) {
			packages.remove(pakkage);
			return true;
		}

		return false;
	}

	/**
	 * Deletes the oldest non-boot package in the cache.
	 */
	private void deleteOldestNonBootPackage() {
		deletePackage(findOldestNonBootPackage());
	}

	/**
	 * Calculates total size of the cache.
	 * 
	 * @return total size of non-boot packages in the cache in bytes
	 */
	private long calculateCacheSize() {
		long result = 0;
		for (Package p : packages) {
			if (p.type.equals(Package.Type.NON_BOOT)) {
				result += p.getSize();
			}
		}
		return result;
	}

	/**
	 * Makes sure that size of the cache is smaller than the limit, deleting old
	 * non-boot packages if necessary.
	 * 
	 * @param limit
	 *          size limit
	 */
	private void ensureSize(long limit) {
		while (calculateCacheSize() > limit) {
			deleteOldestNonBootPackage();
		}
	}

	/**
	 * Downloads package from the Software Repository and adds it to the cache.
	 * 
	 * @param packageFilename
	 *          file name of the package to download
	 * @param packageType
	 *          type of requested package.
	 * 
	 * @return object representing the downloaded package, or <code>null</code> if
	 *         the package is not found in the Software Repository or the Software
	 *         Repository reference is not set
	 * 
	 * @throws IOException
	 *           if the download fails
	 */
	private Package tryDownloadPackage(String packageFilename,
			PackageType packageType) throws IOException {
		/* No Software Repository reference set? */
		if (softwareRepository == null) {
			return null;
		}

		/*
		 * Retrieve the package size and ensure we have enough space in the
		 * cache.
		 */
		PackageMetadata metadata = findPackageByFilename(packageFilename, packageType);
		if (metadata == null) {
			return null;
		}

		long packageSize = metadata.getSize();
		ensureSize(maxCacheSize - packageSize);

		String saveTo = FileUtils.constructPath(cacheDir, packageFilename);
		downloadPackage(metadata.getFilename(), saveTo);

		/* Add package to internal data structues. */
		Package result = new Package(saveTo, packageSize, new Date(), Package.Type.NON_BOOT);
		packages.add(result);
		return result;
	}

	/**
	 * Downloads package from the Software Repository and adds it to the cache.
	 * 
	 * @param packageName
	 *          name of the package to download
	 * @param packageVersion
	 *          version of the package to download
	 * @param packageType
	 *          type of requested package.
	 * 
	 * @return object representing the downloaded package, or <code>null</code> if
	 *         the package is not found in the Software Repository or the Software
	 *         Repository reference is not set
	 * 
	 * @throws IOException
	 *           if the download fails
	 */
	private Package tryDownloadPackage(String packageName, String packageVersion,
			PackageType packageType) throws IOException {
		/* No Software Repository reference set? */
		if (softwareRepository == null) {
			return null;
		}

		/*
		 * Retrieve the package size and ensure we have enough space in the
		 * cache.
		 */
		PackageMetadata metadata = findPackageByNameVersion(packageName, packageVersion, packageType);
		if (metadata == null) {
			return null;
		}

		long packageSize = metadata.getSize();
		ensureSize(maxCacheSize - packageSize);

		String saveTo = FileUtils.constructPath(cacheDir, metadata.getFilename());
		downloadPackage(metadata.getFilename(), saveTo);

		/* Add package to internal data structues. */
		Package result = new Package(saveTo, packageSize, new Date(), Package.Type.NON_BOOT);
		packages.add(result);
		return result;
	}

	/**
	 * Downloads a file from software repository
	 * 
	 * @param packageFilename
	 *          name of file to be downloaded
	 * @param saveTo
	 *          name of file to which file should be downloaded.
	 */
	private void downloadPackage(String packageFilename, String saveTo) throws IOException {
		/* Download the package. */
		ServerSocket serverSocket = new ServerSocket(0); // 0 = use any port
		DownloadHandle handle = softwareRepository.beginPackageDownload(packageFilename, InetAddress.getLocalHost(), serverSocket.getLocalPort());
		byte[] buffer = new byte[SoftwareRepositoryInterface.DOWNLOAD_BUFFER_SIZE];
		int bytesRead;
		Socket socket = serverSocket.accept();
		try {
			InputStream inputStream = new BufferedInputStream(socket.getInputStream(), SoftwareRepositoryInterface.DOWNLOAD_BUFFER_SIZE);
			OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(saveTo), SoftwareRepositoryInterface.DOWNLOAD_BUFFER_SIZE);
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
			serverSocket.close();
			softwareRepository.endPackageDownload(handle);
		}
	}

	/**
	 * Searches software repository's packages by filename and returns the first
	 * hit.
	 * 
	 * @param packageFilename
	 *          filename to match
	 * @param packageType
	 *          package type to match
	 * @return matching package's metadata
	 */
	private PackageMetadata findPackageByFilename(String packageFilename,
			PackageType packageType) throws RemoteException {
		try {
			PackageMetadata[] metadata = softwareRepository.queryPackages(new PackageFilenameQueryCallback(packageFilename, packageType));

			if (metadata.length == 0) {
				return null;
			} else {
				return metadata[0];
			}
		} catch (MatchException e) {
			String message = "MatchException should be never thrown here.";
			logger.error(message, e);
			throw new AssertionError(message);
		}
	}

	/**
	 * Searches software repository's packages by name and version and returns the
	 * first hit.
	 * 
	 * @param packageName
	 *          package name to match
	 * @param packageVersion
	 *          package version to match
	 * @param packageType
	 *          package type to match
	 * @return matching package's metadata
	 */
	private PackageMetadata findPackageByNameVersion(String packageName,
			String packageVersion, PackageType packageType) throws RemoteException {
		try {
			PackageMetadata[] metadata = softwareRepository.queryPackages(new PackageNameVersionQueryCallback(packageName, packageVersion, packageType));

			if (metadata.length == 0) {
				return null;
			} else {
				return metadata[0];
			}
		} catch (MatchException e) {
			String message = "MatchException should be never thrown here.";
			logger.error(message, e);
			throw new AssertionError(message);
		}
	}

	/**
	 * Extracts the contents of the package of given type with given name to the
	 * specified directory (possibly downloading the package from the Software
	 * Repository and placing it to the cache).
	 * 
	 * Maintains invariant that size of all packages in the package cache
	 * directory is less than <code>maxCacheSize</code>. If the invariant is about
	 * to be broken when downloading new package form the Software Repository, we
	 * delete oldest packages until there is enough space.
	 * 
	 * @param packageName
	 *          name of the package to extract (package's filename, not the one
	 *          from packages metadata.
	 * @param path
	 *          directory where the package will be extracted
	 * @param packageType
	 *          type of requested package (only packages of given type will be
	 *          searched).
	 * @throws IOException
	 *           if the download or extraction fails
	 * @throws HostRuntimeException
	 *           if the package can not be found in the Software Repository or the
	 *           Software Repository reference is not set
	 */
	public synchronized void extractPackage(String packageName, String path,
			PackageType packageType) throws IOException, HostRuntimeException {
		Package pakkage = findPackage(packageName);
		if (pakkage == null) {
			pakkage = tryDownloadPackage(packageName, packageType);
		}

		if (pakkage != null) {
			ZipUtils.extractZipFile(new File(pakkage.getFilename()), new File(path));
		} else {
			throw new HostRuntimeException("Can't find package: " + packageName);
		}
	}

	/**
	 * Extracts the contents of the package of given type with given name to the
	 * specified directory (possibly downloading the package from the Software
	 * Repository and placing it to the cache).
	 * 
	 * Maintains invariant that size of all packages in the package cache
	 * directory is less than <code>maxCacheSize</code>. If the invariant is about
	 * to be broken when downloading new package form the Software Repository, we
	 * delete oldest packages until there is enough space.
	 * 
	 * @param packageName
	 *          name of the package to extract (name from package's metadata)
	 * @param packageVersion
	 *          version of the package to extract (version from package's
	 *          metadata)
	 * @param path
	 *          directory where the package will be extracted
	 * @param packageType
	 *          type of requested package (only packages of given type will be
	 *          searched).
	 * @throws IOException
	 *           if the download or extraction fails
	 * @throws HostRuntimeException
	 *           if the package can not be found in the Software Repository or the
	 *           Software Repository reference is not set
	 */
	public synchronized void extractPackage(String packageName,
			String packageVersion, String path, PackageType packageType) throws IOException, HostRuntimeException {

		/* ask software repository for filename of file we want */
		PackageMetadata metadata = findPackageByNameVersion(packageName, packageVersion, packageType);

		/* build filename from packageName and packageVersion */
		String packageFilename = metadata.getFilename();

		Package pakkage = findPackage(packageFilename);
		if (pakkage == null) {
			pakkage = tryDownloadPackage(packageName, packageVersion, packageType);
		}

		if (pakkage != null) {
			ZipUtils.extractZipFile(new File(pakkage.getFilename()), new File(path));
		} else {
			throw new HostRuntimeException("Can't find package: " + packageName);
		}
	}

	/**
	 * Allocates a new <code>PackageCacheManager</code> object.
	 * 
	 * Note that for proper initialization the user must also set the RMI
	 * reference to the Software Repository (using
	 * <code>setSoftwareRepository</code> method) and limit of the cache size
	 * (using <code>setMaxCacheSize</code> method). Those two attributes are not
	 * set in the constructor, because it is assumed that user (Host Runtime) does
	 * not know them in the time of the construction.
	 * 
	 * @param cacheDir
	 *          cache directory
	 * @param bootPackagesDir
	 *          directory with boot packages
	 * @throws IOException
	 *           if the list of boot packages can't be retrieved
	 */
	public PackageCacheManager(String cacheDir, String bootPackagesDir)
			throws IOException {
		this.cacheDir = cacheDir;
		this.bootPackagesDir = bootPackagesDir;

		File[] files = new File(bootPackagesDir).listFiles();
		if (files == null) {
			throw new IOException("Error getting list of boot packages.");
		}

		Date now = new Date();
		for (File f : files) {
			packages.add(new Package(f.getPath(), f.length(), now, Package.Type.BOOT));
		}
	}
}
