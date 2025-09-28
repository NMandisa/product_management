package za.co.pms.audit;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author NMMkhungo
 * @since 2025/09/24
 **/
@Slf4j
@Aspect
@Component
public class CurrencyConfigPerformanceMonitor {
    private final MeterRegistry meterRegistry;

    @Autowired
    public CurrencyConfigPerformanceMonitor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Around("execution(* za.co.pms.config.*Currency*.*(..))")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            return joinPoint.proceed();
        } finally {
            sample.stop(Timer.builder("currency.operations")
                    .tag("operation", joinPoint.getSignature().getName())
                    .register(meterRegistry));
            log.info("Executed {} in currency.operations timer", joinPoint.getSignature());
        }
    }
}
