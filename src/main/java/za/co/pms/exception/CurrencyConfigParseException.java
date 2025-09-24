package za.co.pms.exception;

import java.io.Serializable;

/**
 * @author NMMkhungo
 * @since 2025/09/23
 **/
public class CurrencyConfigParseException extends CurrencyConfigException implements Serializable {
    public CurrencyConfigParseException(String location, Throwable cause) {
        super("Failed to parse currency config at: " + location, cause);
    }
    public CurrencyConfigParseException(String message) {
        super(message);
    }
}
