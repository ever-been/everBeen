package cz.cuni.mff.d3s.been.nginx;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

/**
 * Created with IntelliJ IDEA.
 * User: Kuba
 * Date: 21.04.13
 * Time: 17:39
 * To change this template use File | Settings | File Templates.
 */
public class MyUtils {
	public static String exec(String cwd, String program, String[] args) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		CommandLine cmdLine = new CommandLine(program);
		for (String arg : args)
			cmdLine.addArgument(arg);
		DefaultExecutor executor = new DefaultExecutor();
		executor.setWorkingDirectory(new File(cwd));
		int exitValue = 0;
		try {
			PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
			executor.setStreamHandler(streamHandler);
			exitValue = executor.execute(cmdLine);
		} catch (IOException e) {
			throw new RuntimeException(String.format("Executing %s failed.", program), e);
		}

		if (exitValue != 0)
			throw new RuntimeException(String.format("%s returned an error (exit code %s).", program, exitValue));

		return outputStream.toString();
	}

	public static String getResourceAsString(String resourseName) {
		InputStream is = MyUtils.class.getResourceAsStream(resourseName);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {
			IOUtils.copy(is, outputStream);
			is.close();
			outputStream.close();
		} catch (IOException e) {
			throw new RuntimeException("Cannot read resource.", e);
		}

		return outputStream.toString();
	}

	public static void saveFileFromString(File destinationFile, String contents) {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(contents.getBytes());
			FileOutputStream fos = new FileOutputStream(destinationFile);
			IOUtils.copy(inputStream, fos);
			fos.close();
		} catch (IOException e) {
			throw new RuntimeException("Writing to file failed.", e);
		}
	}

	public static int findRandomPort() {
		try {
			ServerSocket socket = new ServerSocket(0);
			int port = socket.getLocalPort();
			socket.close();
			return port;
		} catch (IOException e) {
			throw new RuntimeException("Cannot find random port.", e);
		}
	}

	public static String getHostname() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			throw new RuntimeException("Cannot get hostname.", e);
		}
	}
}
