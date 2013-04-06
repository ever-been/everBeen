package cz.cuni.mff.d3s.been.detectors;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import cz.cuni.mff.d3s.been.core.ri.OperatingSystem;
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

	public OperatingSystem detectOperatingSystem() {
		OperatingSystem os = new OperatingSystem();

		try {
			loadSigar();

			org.hyperic.sigar.OperatingSystem sys = org.hyperic.sigar.OperatingSystem.getInstance();
			os.setName(sys.getName());
			os.setVersion(sys.getVersion());
			os.setArch(sys.getArch());
			os.setVendor(sys.getVendor());
			os.setVendorVersion(sys.getVendorVersion());
			os.setDataModel(sys.getDataModel());
			os.setEndian(sys.getCpuEndian());
		} catch (SigarException e) {
			// do nothing
		}

		return os;
	}

	public List<Filesystem> detectFilesystems() {
		ArrayList<Filesystem> fslist = new ArrayList<>();

		try {
			loadSigar();

			for (FileSystem fs : sigar.getFileSystemList()) {
				FileSystemUsage usage = sigar.getFileSystemUsage(fs.getDirName());

				Filesystem f = new Filesystem();
				f.setDeviceName(fs.getDevName());
				f.setDirectory(fs.getDirName());
				f.setType(fs.getTypeName());
				f.setTotal(usage.getTotal() * 1024);
				f.setFree(usage.getFree() * 1024);
				fslist.add(f);
			}
		} catch (SigarException e) {
			// do nothing
		}

		return fslist;
	}

	public MonitorSample generateSample() {
		MonitorSample sample = new MonitorSample();

		try {
			loadSigar();

			if (sigar == null)
				return null;

			// memory
			Mem mem = sigar.getMem();
			sample.setFreeMemory(mem.getFree());

			// processes
			sample.setProcessCount(sigar.getProcList().length);

			// network interfaces
			for (String ifname : sigar.getNetInterfaceList()) {
				NetworkSample networkSample = new NetworkSample();
				NetInterfaceStat stat = sigar.getNetInterfaceStat(ifname);
				networkSample.setName(ifname);
				networkSample.setBytesIn(stat.getRxBytes());
				networkSample.setBytesOut(stat.getTxBytes());

				sample.getInterfaces().add(networkSample);
			}

			// filesystems
			for (FileSystem fs : sigar.getFileSystemList()) {
				if (fs.getType() == FileSystem.TYPE_LOCAL_DISK) {
					FileSystemUsage usage = sigar.getFileSystemUsage(fs.getDirName());
					FilesystemSample fsSample = new FilesystemSample();

					fsSample.setDeviceName(fs.getDevName());
					fsSample.setDirectory(fs.getDirName());
					fsSample.setReadBytes(usage.getDiskReadBytes());
					fsSample.setReads(usage.getDiskReads());
					fsSample.setWriteBytes(usage.getDiskWriteBytes());
					fsSample.setWrites(usage.getDiskWrites());

					sample.getFilesystems().add(fsSample);
				}
			}

		} catch (SigarException e) {
			// do nothing
		}

		return sample;
	}
}
