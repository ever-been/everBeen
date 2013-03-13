package cz.cuni.mff.d3s.been.swrepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.datastore.StorePersister;
import cz.cuni.mff.d3s.been.util.CopyStream;

/**
 * 
 * A {@link FSBasedStore}-related implementation of {@link StorePersister}.
 * 
 * @author darklight
 * 
 */
public class FSBasedStorePersister implements StorePersister {

	/** Class log */
	private static final Logger log = LoggerFactory.getLogger(FSBasedStorePersister.class);

	private final File storedFile;
	private final String entityId;

	FSBasedStorePersister(String entityId, File storedFile) {
		this.entityId = entityId;
		this.storedFile = storedFile;
	}

	@Override
	public boolean dump(InputStream content) {
		if (!storedFile.exists()) {
			final File parent = storedFile.getParentFile();
			if (parent == null) {
				log.error(
						"Failed to store BPK because expected storage file \"{}\" doesn't have a parent folder",
						storedFile.getPath());
				return false;
			}
			parent.mkdirs(); // TODO handle security exception
			try {
				storedFile.createNewFile();
			} catch (IOException e) {
				log.error(
						"Could not create file to hold entity {}: I/O error {}",
						entityId,
						e.getMessage());
				return false;
			}
		}
		OutputStream os = null;
		try {
			os = new FileOutputStream(storedFile);
		} catch (IOException e) {
			log.error(
					"Failed to persist entity {}: I/O error {}",
					entityId,
					e.getMessage());
			return false;
		}
		CopyStream copyStream = new CopyStream(content, true, os, true, true);
		try {
			copyStream.copy();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
