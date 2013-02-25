package cz.cuni.mff.d3s.been.detectors;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import cz.cuni.mff.d3s.been.core.hwi.Cpu;
import cz.cuni.mff.d3s.been.core.hwi.Hardware;
import cz.cuni.mff.d3s.been.core.hwi.NetworkInterface;
import org.apache.commons.io.IOUtils;
import org.hyperic.jni.ArchLoader;
import org.hyperic.jni.ArchNotSupportedException;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

/**
 * Created with IntelliJ IDEA. User: Kuba Date: 24.02.13 Time: 13:46 To change
 * this template use File | Settings | File Templates.
 */
public class SigarDetector {

	private Sigar sigar;

	private void loadSigar() throws SigarException {
		if (sigar != null)
			return;

		try {

			ArchLoader archLoader = new ArchLoader();
			archLoader.setName("sigar");
			String libName = archLoader.getArchLibName();
			archLoader.setLibName(libName);
			libName = archLoader.getLibraryName();
			InputStream libStream = this.getClass().getResourceAsStream(libName);

			final Path dirPath = Files.createTempDirectory("been_native_lib");
			final Path filePath = dirPath.resolve(libName);
			OutputStream outputStream = Files.newOutputStream(filePath);

			IOUtils.copy(libStream, outputStream);

			outputStream.close();
			libStream.close();

			// shutdown hook to delete the lib file and temporary directory
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					try {
						Files.delete(filePath);
						Files.delete(dirPath);
					} catch (IOException e) {
						e.printStackTrace(); // TODO
					}
				}
			});

			System.setProperty("org.hyperic.sigar.path", dirPath.toString());

			sigar = new Sigar();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ArchNotSupportedException e) {
			e.printStackTrace();
		}
	}

	public Hardware detectHardware() throws SigarException {
		loadSigar();

		if (sigar == null)
			return null;

        Hardware hw = new Hardware();

        for (CpuInfo i : sigar.getCpuInfoList()) {
            Cpu cpu = new Cpu();
            cpu.setVendor(i.getVendor());
            cpu.setModel(i.getModel());
            cpu.setMhz(i.getMhz());
            cpu.setCacheSize(i.getCacheSize());
            hw.getCpu().add(cpu);
        }

        for (String s : sigar.getNetInterfaceList()) {
            NetInterfaceConfig c = sigar.getNetInterfaceConfig(s);

            NetworkInterface networkInterface = new NetworkInterface();
            networkInterface.setName(c.getName());
            networkInterface.setHwaddr(c.getHwaddr());
            networkInterface.setType(c.getType());
            networkInterface.setMtu(c.getMtu());
            networkInterface.setAddress(c.getAddress());
            networkInterface.setNetmask(c.getNetmask());
            networkInterface.setBroadcast(c.getBroadcast());

            hw.getNetworkInterface().add(networkInterface);
        }

		return hw;
	}
}
