package cz.cuni.mff.d3s.been.swrepository;


/**
 * 
 * @author donarus
 * 
 */
public class SoftwareRepositoryRunner {

	public static void main(String[] args) {
		HttpServer httpServer = new HttpServer("localhost", 8000);
		SoftwareRepository runner = new SoftwareRepository(httpServer);
		runner.start();
	}
}
