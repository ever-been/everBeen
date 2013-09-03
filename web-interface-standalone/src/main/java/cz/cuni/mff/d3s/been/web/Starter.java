package cz.cuni.mff.d3s.been.web;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;

import java.security.ProtectionDomain;
import java.net.URL;

/**
 * Runnable class capable of starting embedded Jetty container for the EverBEEN web interface.
 */
public class Starter {

	/**
	 * Main method of the runnable
	 *
	 * @param args Command-line arguments
	 */
    public static void main(String[] args) {
        Server server = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();

        // Set some timeout options to make debugging easier.
        connector.setMaxIdleTime(1000 * 60 * 60);
        connector.setSoLingerTime(-1);
        connector.setPort(8080);
        server.setConnectors(new Connector[]{connector});

        WebAppContext context = new WebAppContext();
        context.setServer(server);
        context.setContextPath("/");

        ProtectionDomain protectionDomain = Starter.class.getProtectionDomain();
        URL location = protectionDomain.getCodeSource().getLocation();
        context.setWar(location.toExternalForm());

        server.setHandler(context);
        try {
            server.start();
            System.in.read();
            server.stop();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(100);
        }
    }
}
