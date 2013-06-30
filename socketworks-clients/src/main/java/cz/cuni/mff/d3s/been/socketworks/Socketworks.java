package cz.cuni.mff.d3s.been.socketworks;

/**
 * Socket connection facade
 */
public final class Socketworks {

    /**
     * Socket connection protocols
     */
    public enum Protocol {
        TCP("tcp"),
        INPROC("inproc");

        private final String scheme;

        private Protocol(String scheme) {
            this.scheme = scheme;
        }

        public String bindAddr(String hostname) {
            return String.format("%s://%s", this.scheme, hostname);
        }

        public String connection(String hostname, Integer port) {
            return String.format("%s://%s:%d", this.scheme, hostname, port);
        }
    }
}
