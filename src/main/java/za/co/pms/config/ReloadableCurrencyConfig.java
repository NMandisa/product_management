package za.co.pms.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author NMMkhungo
 * @since 2025/09/24
 **/
@Slf4j
@Component
@RefreshScope
public class ReloadableCurrencyConfig {
    private final CurrencyConfigLoader configLoader;  // <-- your existing loader
    @Getter
    private volatile CurrencyConfig currentConfig;

    @Autowired
    public ReloadableCurrencyConfig(CurrencyConfigLoader configLoader) {
        this.configLoader = configLoader;
        this.currentConfig = configLoader.getCurrencyConfig(); // initial load
    }

    /**
     * Reload configuration when Spring Cloud refresh event is triggered
     */
    @EventListener
    public void onConfigUpdate(EnvironmentChangeEvent event) {
        if (event.getKeys().stream().anyMatch(key -> key.startsWith("currency."))) {
            log.info("Currency config change detected via EnvironmentChangeEvent, reloading...");
            reloadConfiguration();
        }
    }

    /**
     * Scheduled polling for external changes (fallback mechanism)
     */
    @Scheduled(fixedRate = 300_000) // every 5 minutes
    public void scheduledReload() {
        if (isConfigUpdatedExternally()) {
            log.info("External currency config update detected, reloading...");
            reloadConfiguration();
        }
    }

    private void reloadConfiguration() {
        try {
            CurrencyConfig newConfig = configLoader.loadCurrencyConfigAndReturn(); // new helper in loader
            if (newConfig != null) {
                this.currentConfig = newConfig;
                log.info("Currency config successfully reloaded.");
            }
        } catch (Exception e) {
            log.error("Failed to reload currency config", e);
        }
    }

    /**
     * Example: checksum/hash comparison, lastModified, or a version property
     */
    private boolean isConfigUpdatedExternally() {
        // TODO: implement external check (file lastModified, DB version, etc.)
        return false;
    }

}
