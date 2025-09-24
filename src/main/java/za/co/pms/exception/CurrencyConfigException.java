package za.co.pms.exception;

import java.io.Serializable;

/**
 * @author NMMkhungo
 * @since 2025/09/23
 **/
public class CurrencyConfigException extends RuntimeException implements Serializable {
    public CurrencyConfigException(String message) {
        super(message);
    }
    public CurrencyConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
