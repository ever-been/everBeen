package cz.cuni.mff.d3s.been.bpk;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;

public class BpkResolver {

	public static BpkConfiguration resolve(File bpkFile) throws IOException, JAXBException {
		BpkConfiguration config = null;
		ZipArchiveInputStream bpkZipStream = new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(bpkFile)));
		for (ArchiveEntry entry = bpkZipStream.getNextEntry(); entry != null; entry = bpkZipStream.getNextEntry()) {
			if (PackageNames.CONFIG_FILE.equals(entry.getName()) && bpkZipStream.canReadEntryData(entry)) {
				config = loadConfigWithJAXB(bpkZipStream);
			}
		}
		bpkZipStream.close();
		return config;
	}

	private static BpkConfiguration loadConfigWithJAXB(InputStream is) throws JAXBException {
		JAXBContext unmarshalContext = JAXBContext.newInstance(BpkConfiguration.class);
		Unmarshaller configUnmarshaller = unmarshalContext.createUnmarshaller();
		return (BpkConfiguration) configUnmarshaller.unmarshal(is);
	}

}
