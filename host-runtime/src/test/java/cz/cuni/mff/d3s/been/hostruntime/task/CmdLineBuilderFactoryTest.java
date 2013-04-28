package cz.cuni.mff.d3s.been.hostruntime.task;

import java.io.File;

import cz.cuni.mff.d3s.been.hostruntime.task.CmdLineBuilderFactory;
import cz.cuni.mff.d3s.been.hostruntime.task.JVMCmdLineBuilder;
import cz.cuni.mff.d3s.been.hostruntime.task.NativeCmdLineBuilder;
import org.junit.Assert;
import org.junit.Test;

import cz.cuni.mff.d3s.been.bpk.BpkRuntime;
import cz.cuni.mff.d3s.been.bpk.JavaRuntime;
import cz.cuni.mff.d3s.been.bpk.NativeRuntime;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;

public class CmdLineBuilderFactoryTest extends Assert {

	@Test
	public void testJVMCmdLineBuilderIsSelectedOnJVMRuntime() throws Exception {
		BpkRuntime runtime = new JavaRuntime();
		TaskDescriptor taskDescriptor = null;
		File taskDir = null;
		assertEquals(JVMCmdLineBuilder.class, CmdLineBuilderFactory.create(runtime, taskDescriptor, taskDir).getClass());
	}

	@Test
	public void testNativeCmdLineBuilderIsSelectedOnNativeRuntime() throws Exception {
		BpkRuntime runtime = new NativeRuntime();
		TaskDescriptor taskDescriptor = null;
		File taskDir = null;
		assertEquals(NativeCmdLineBuilder.class, CmdLineBuilderFactory.create(runtime, taskDescriptor, taskDir).getClass());
	}
}
