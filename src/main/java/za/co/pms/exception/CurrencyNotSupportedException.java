package za.co.pms.exception;

import java.io.Serializable;

/**
 * @author NMMkhungo
 * @since 2025/09/29
 **/
public class CurrencyNotSupportedException extends CurrencyConfigException implements Serializable {
    public CurrencyNotSupportedException(String message) {
        super(message);
    }
    public CurrencyNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }
}
