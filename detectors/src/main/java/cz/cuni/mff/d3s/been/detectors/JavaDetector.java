package cz.cuni.mff.d3s.been.detectors;

import cz.cuni.mff.d3s.been.core.ri.*;
import org.apache.commons.collections.EnumerationUtils;
import org.apache.commons.lang.StringUtils;
import org.hyperic.sigar.NetInterfaceConfig;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.net.*;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * @author Kuba Brecka
 */
public class JavaDetector {
    public Java detectJava() {
        Java java = new Java();

        java.setVersion(System.getProperty("java.version"));
        java.setVendor(System.getProperty("java.vendor"));
        java.setVersion(System.getProperty("java.version"));
        java.setRuntimeName(System.getProperty("java.runtime.name"));
        java.setVMVersion(System.getProperty("java.vm.version"));
        java.setVMVendor(System.getProperty("java.vm.vendor"));
        java.setRuntimeVersion(System.getProperty("java.runtime.version"));
        java.setSpecificationVersion(System.getProperty("java.specification.version"));

        return java;
    }

	public void detectOperatingSystem(RuntimeInfo runtimeInfo) {
		OperatingSystem os = new OperatingSystem();
		os.setName(System.getProperty("os.name"));
		os.setArch(System.getProperty("os.arch"));
		os.setVersion(System.getProperty("os.version"));
		runtimeInfo.setOperatingSystem(os);
	}

	public void detectHardware(RuntimeInfo runtimeInfo) {
		Hardware hw = new Hardware();

		Memory mem = new Memory();
		mem.setRam(getTotalMemoryFromReflection());
		hw.setMemory(mem);

		for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
			Cpu cpu = new Cpu();
			hw.getCpu().add(cpu);
		}

		try {
			Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();

			for (Object i : EnumerationUtils.toList(ifs)) {
				NetworkInterface iface = (NetworkInterface) i;
				cz.cuni.mff.d3s.been.core.ri.NetworkInterface networkInterface = new cz.cuni.mff.d3s.been.core.ri.NetworkInterface();
				networkInterface.setName(iface.getName());
				networkInterface.setMtu(iface.getMTU());

				for (Object o : EnumerationUtils.toList(iface.getInetAddresses())) {
					InetAddress a = (InetAddress) o;
					networkInterface.getAddress().add(a.getHostAddress());
				}

				hw.getNetworkInterface().add(networkInterface);
			}
		} catch (SocketException e) {
			// do nothing
		}

		runtimeInfo.setHardware(hw);
	}

	public void detectFilesystems(RuntimeInfo runtimeInfo) {
		for (File root : File.listRoots()) {
			Filesystem f = new Filesystem();
			f.setDirectory(root.getAbsolutePath());
			f.setFree(root.getFreeSpace());
			f.setTotal(root.getTotalSpace());
			runtimeInfo.getFilesystem().add(f);
		}
	}

	private long getTotalMemoryFromReflection()
	{
		OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();

		try {
			Method a = os.getClass().getMethod("getTotalPhysicalMemorySize");
			a.setAccessible(true);
			Object o = a.invoke(os);
			return (Long) o;
		} catch (Throwable e) {
			// do nothing
		}

		return 0;
	}

	private long getFreeMemoryFromReflection()
	{
		OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();

		try {
			Method a = os.getClass().getMethod("getFreePhysicalMemorySize");
			a.setAccessible(true);
			Object o = a.invoke(os);
			return (Long) o;
		} catch (Throwable e) {
			// do nothing
		}

		return 0;
	}

	public MonitorSample generateSample() {

		OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();

		MonitorSample sample = new MonitorSample();
		LoadAverage la = new LoadAverage();
		la.setLoad1(os.getSystemLoadAverage());
		sample.setLoadAverage(la);
		sample.setFreeMemory(getFreeMemoryFromReflection());

		return sample;
	}
}
