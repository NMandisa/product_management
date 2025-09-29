package za.co.pms.exception;

import java.io.Serializable;

/**
 * @author NMMkhungo
 * @since 2025/09/29
 **/
public class PriceNotFoundException extends RuntimeException implements Serializable {
    public PriceNotFoundException(String message) {
        super(message);
    }
    public PriceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
