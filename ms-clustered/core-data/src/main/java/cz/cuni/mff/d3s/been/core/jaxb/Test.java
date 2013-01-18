package cz.cuni.mff.d3s.been.core.jaxb;


import cz.cuni.mff.d3s.been.core.td.*;


/**
 * @author Martin Sixta
 */
public class Test {
	public static void main(String[] args) {
		try {

			/*
			BindingComposer<TaskDescriptor> bindingComposer = XSD.TD.createComposer(TaskDescriptor.class);
			TaskDescriptor taskDescriptor = Factory.TD.createTaskDescriptor();
			taskDescriptor.setContextId("system");
			taskDescriptor.setTaskId("clinterface-tid");
			taskDescriptor.setTreeAddress("/legacy/clinterface");
			taskDescriptor.setName("clinterface");

			cz.cuni.mff.d3s.been.bom.td.Package pkg = Factory.TD.createPackage();
			pkg.setName("clinterface-1.0.bpk");

			HostRuntimes hostRuntimes = Factory.TD.createHostRuntimes();
			hostRuntimes.getName().add("localhost");

			taskDescriptor.setPackage(pkg);
			taskDescriptor.setHostRuntimes(hostRuntimes);

			bindingComposer.compose(taskDescriptor, System.out);

			*/

			/*
			BindingComposer<HostInfo> bC = XSD.HOSTINFO.createComposer(HostInfo.class);

			HostInfo hostInfo = Factory.HOSTINFO.createHostInfo();

			hostInfo.setOs("linux");
			hostInfo.setMemory(1024L);
			hostInfo.setCpu("Intel(R) Core(TM) i7-2670QM CPU @ 2.20GHz");
			hostInfo.setJava("1.7.0_09");
			hostInfo.setName("ds9");

			bC.compose(hostInfo, System.out);


			if (args.length == 1) {
				BindingParser<HostInfo> bindingParser = XSD.HOSTINFO.createParser(HostInfo.class);

				HostInfo hi = bindingParser.parse(new File(args[0]));

				System.out.println(hi.getCpu());
				System.out.println(hi.getJava());
				System.out.println(hi.getMemory());
				System.out.println(hi.getName());
				System.out.println(hi.getOs());
			}

			*/









		} catch (Exception e) {
			e.printStackTrace();

		}
	}
}
