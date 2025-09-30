package za.co.pms.exception;

import java.io.Serializable;

/**
 * @author NMMkhungo
 * @since 2025/09/30
 **/
public class RegionNotFoundException extends RuntimeException implements Serializable {
    public RegionNotFoundException(String message) {
        super(message);
    }
    public RegionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
