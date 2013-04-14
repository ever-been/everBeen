package cz.cuni.mff.d3s.been.bpkplugin;

import java.io.IOException;
import java.nio.file.Path;
import java.util.jar.JarFile;

/**
 * 
 * @author "Tadeas Palusga"
 * 
 */
public class MainClassExtractor {

	/**
	 * 
	 * Determines Main-Class of a jar.
	 * 
	 * @param bpkJarPath
	 * @return Main-Class of the parameter
	 * @throws IOException
	 *           when Main-Class cannot be determined (file no found, file is not
	 *           valid jar, or another IO exception)
	 */
	public static String getMainClass(Path bpkJarPath) throws IOException {
		try (JarFile jarFile = new JarFile(bpkJarPath.toFile())) {
			return jarFile.getManifest().getMainAttributes().getValue("Main-Class");
		} catch (Exception e) {
			throw e;
		}
	}
}
