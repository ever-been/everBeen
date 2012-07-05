package cz.cuni.mff.been.common.scripting;

import junit.framework.Assert;

import org.junit.Test;


public class ScriptLauncherTest {
	
	
	@Test 
	public void testRunShellScript() throws Exception {
		ScriptLauncher module = new ScriptLauncher();
		
		String[] script = new String[] {
			"#!/bin/bash",
			"",
			"echo something",
			"echo this is error output >&2",
			"echo $TESTVAR",
			"export OUTVAR=outputvariablecontent"
		
		};
		
		ScriptEnvironment env = new ScriptEnvironment();
		env.putEnv("TESTVAR", "enviroment variables work");
		
		Assert.assertEquals(0, module.runShellScript(script, env));
		
	}
	
	@Test 
	public void testRunShellScript2() throws Exception {
		ScriptLauncher module = new ScriptLauncher();
		
		String[] script = new String[] {
			"#!/bin/bash",
			"echo blabla >&2",
			
			"some_erroneous_command || echo 'dfasfaf' ; exit 1",
			
			//"some_erroneous_command",
			//"if [ \"${?}\" -ne \"0\" ]; then echo 'Error occured' >&2; exit 1; fi",
			//"echo this code should not be reached"
		};
		
		ScriptEnvironment env = new ScriptEnvironment();
		env.putEnv("TESTVAR", "enviroment variables work");
		
		Assert.assertEquals(1, module.runShellScript(script, env));
		
	}
		

}
