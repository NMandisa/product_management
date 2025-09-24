package za.co.pms.exception;

import jakarta.validation.ConstraintViolation;
import lombok.Getter;
import za.co.pms.config.CurrencyConfig;

import java.io.Serializable;
import java.util.Set;

/**
 * @author NMMkhungo
 * @since 2025/09/23
 **/
@Getter
public class CurrencyConfigValidationException extends CurrencyConfigException implements Serializable {
    private final Set<? extends ConstraintViolation<?>> violations;

    public CurrencyConfigValidationException(String message, Set<? extends ConstraintViolation<?>> violations) {
        super(message);
        this.violations = violations;
    }

    public CurrencyConfigValidationException(Set<? extends ConstraintViolation<?>> violations) {
        super("Currency configuration validation failed with " + violations.size() + " violation(s)");
        this.violations = violations;
    }

    @Override
    public String getMessage() {
        if (violations == null || violations.isEmpty()) {
            return super.getMessage();
        }

        StringBuilder sb = new StringBuilder(super.getMessage());
        for (ConstraintViolation<?> violation : violations) {
            sb.append("\n- ").append(violation.getPropertyPath()).append(": ").append(violation.getMessage());
        }
        return sb.toString();
    }
}
