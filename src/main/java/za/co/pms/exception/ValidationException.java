package za.co.pms.exception;

import java.io.Serializable;

/**
 * @author NMMkhungo
 * @since 2025/09/30
 **/
public class ValidationException extends RuntimeException implements Serializable {
    public ValidationException(String message) {
        super(message);
    }
}