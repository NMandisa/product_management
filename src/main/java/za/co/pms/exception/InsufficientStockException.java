package za.co.pms.exception;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author NMMkhungo
 * @since 2025/09/14
 **/
public class InsufficientStockException extends RuntimeException implements Serializable {
    @Serial
    private static final long serialVersionUID = -9137293788578834873L;
    public InsufficientStockException() {super();}
    public InsufficientStockException(String message) {super(message);}
    public InsufficientStockException(String message, Throwable cause) {super(message,cause);}
}
