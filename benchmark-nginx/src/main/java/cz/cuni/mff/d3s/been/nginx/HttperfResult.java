package cz.cuni.mff.d3s.been.nginx;

import java.util.Date;

import cz.cuni.mff.d3s.been.results.Result;

/**
 * Created with IntelliJ IDEA. User: Kuba Date: 21.04.13 Time: 16:54 To change
 * this template use File | Settings | File Templates.
 */
public class HttperfResult extends Result {

	class MetaInfo {
		String hostname;
		Date timestamp;
		String taskId;
		String taskContextId;
	}

	MetaInfo metaInformation;

	class Parameters {
		int revision;
		int numberOfClients;
		int numberOfRuns;
		int numberOfConnections;
		int requestsPerConnection;
		int sendBuffer;
		int recvBuffer;
	}

	Parameters parameters;

	public HttperfResult(NginxClientTask task) {
		metaInformation = new MetaInfo();
		metaInformation.timestamp = new Date();
		metaInformation.hostname = MyUtils.getHostname();
		metaInformation.taskId = task.getId();
		metaInformation.taskContextId = task.getContextId();

		parameters = new Parameters();
		parameters.revision = Integer.parseInt(task.getProperty("revision"));
		parameters.numberOfClients = Integer.parseInt(task.getProperty("numberOfClients"));
		parameters.numberOfRuns = Integer.parseInt(task.getProperty("numberOfRuns"));
		parameters.numberOfConnections = Integer.parseInt(task.getProperty("numberOfConnections"));
		parameters.requestsPerConnection = Integer.parseInt(task.getProperty("requestsPerConnection"));
		parameters.sendBuffer = Integer.parseInt(task.getProperty("sendBuffer"));
		parameters.recvBuffer = Integer.parseInt(task.getProperty("recvBuffer"));
	}

	int connections;
	int requests;
	int replies;
	double testDuration;

	double connectionRate;
	double connectionTimeMin;
	double connectionTimeAvg;
	double connectionTimeMax;
	double connectionTimeMedian;
	double connectionTimeStdDev;
	double connectionTimeConnect;

	double requestRate;
	int requestSize;
	int replySizeTotal;

	int numberOf1xx;
	int numberOf2xx;
	int numberOf3xx;
	int numberOf4xx;
	int numberOf5xx;

	double cpuUser;
	double cpuSystem;

	double cpuPercentUser;
	double cpuPercentSystem;
	double cpuPercentTotal;

	double netIO;
}
