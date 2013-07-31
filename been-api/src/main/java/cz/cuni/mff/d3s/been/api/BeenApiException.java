package cz.cuni.mff.d3s.been.api;

/**
 * @author donarus
 */
public class BeenApiException extends Exception {

    private String detailedReason;

    public BeenApiException(String message) {
        super(message);
    }

    public BeenApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeenApiException(Throwable cause) {
        super(cause);
    }

    public void setDetailedReason(String detailedReason) {
        this.detailedReason = detailedReason;
    }

    public String getDetailedReason() {
        return detailedReason;
    }

}
