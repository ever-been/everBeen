package cz.cuni.mff.d3s.been.detectors;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;
import org.hyperic.jni.ArchLoader;
import org.hyperic.jni.ArchNotSupportedException;
import org.hyperic.sigar.*;

import cz.cuni.mff.d3s.been.core.ri.*;
import cz.cuni.mff.d3s.been.core.ri.Cpu;

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
						e.printStackTrace();
					}
				}
			});

			System.setProperty("org.hyperic.sigar.path", dirPath.toString());

			sigar = new Sigar();

		} catch (IOException | ArchNotSupportedException e) {
			// cannot load sigar native lib, continue
		}
	}

	public Hardware detectHardware() {
		try {
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

			Mem mem = sigar.getMem();
			Swap swap = sigar.getSwap();

			hw.setMemory(new Memory());
			hw.getMemory().setRam(mem.getTotal());
			hw.getMemory().setSwap(swap.getTotal());

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
		} catch (SigarException e) {
			return null;
		}
	}

	public MonitorSample generateSample() {
		MonitorSample sample = new MonitorSample();

		try {
			loadSigar();

			if (sigar == null)
				return null;

			Mem mem = sigar.getMem();
			sample.setFreeMemory(mem.getFree());
			sample.setProcessCount(sigar.getProcList().length);

			for (String ifname : sigar.getNetInterfaceList()) {
				NetworkSample networkSample = new NetworkSample();
				NetInterfaceStat stat = sigar.getNetInterfaceStat(ifname);
				networkSample.setName(ifname);
				networkSample.setBytesIn(stat.getRxBytes());
				networkSample.setBytesOut(stat.getTxBytes());

				sample.getInterfaces().add(networkSample);
			}

		} catch (SigarException e) {
			// do nothing
		}

		return sample;
	}
}
