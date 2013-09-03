package cz.cuni.mff.d3s.been.socketworks;

/**
 * Socket connection facade
 */
public final class Socketworks {

    /**
     * Socket connection protocols
     */
    public enum Protocol {
		/** TCP scheme */
        TCP("tcp"),
		/** Intraprocedural scheme */
        INPROC("inproc");

        private final String scheme;

        private Protocol(String scheme) {
            this.scheme = scheme;
        }

		/**
		 * Create a binding address
		 *
		 * @param hostname Host name to bind
		 *
		 * @return The binding address
		 */
        public String bindAddr(String hostname) {
            return String.format("%s://%s", this.scheme, hostname);
        }

		/**
		 * Create a connection string
		 *
		 * @param hostname Host name of targeted node
		 * @param port Listening port on the targeted node
		 *
		 * @return The connection string
		 */
        public String connection(String hostname, Integer port) {
            return String.format("%s://%s:%d", this.scheme, hostname, port);
        }
    }
}
