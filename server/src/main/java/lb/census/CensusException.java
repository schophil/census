package lb.census;

/**
 * Created by philippe on 05/05/16.
 */
public class CensusException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public CensusException() {
    }

    public CensusException(String message) {
        super(message);
    }

    public CensusException(String message, Throwable cause) {
        super(message, cause);
    }

    public CensusException(Throwable cause) {
        super(cause);
    }
}
