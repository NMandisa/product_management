package za.co.pms.exception;

import java.io.Serializable;

/**
 * @author NMMkhungo
 * @since 2025/09/30
 **/
public class InitializationException extends RuntimeException implements Serializable {
    public InitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
