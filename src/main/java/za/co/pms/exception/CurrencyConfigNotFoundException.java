package za.co.pms.exception;

import java.io.Serializable;

/**
 * @author NMMkhungo
 * @since 2025/09/23
 **/
public class CurrencyConfigNotFoundException extends CurrencyConfigException implements Serializable {
    public CurrencyConfigNotFoundException(String location) {
        super("Currency config file not found at: " + location);
    }
}
