package za.co.pms.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;
import java.util.Map;

/**
 * @author NMMkhungo
 * @since 2025/09/23
 **/

@Data
@Configuration
//@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrencyConfig {
    @JsonProperty("default")
    private String defaultCurrency;

    private List<Currency> supported;
    private String fxProvider;
    private List<String> backupFxProviders;
    private double markup;
    private boolean autoDetect;
    private boolean showTooltip;
    private Map<String, Formatting> formatting;
    private Map<String, String> regionalDefaults;
    private AdvancedFeatures advancedFeatures;
    private Compliance compliance;
    private PaymentMethods paymentMethods;
    private Localization localization;
    private Analytics analytics;

    @Data
    public static class Currency {
        private String code;
        private String symbol;
        private String name;
        private int precision;
    }

    @Data
    public static class Formatting {
        private String thousandsSeparator;
        private String decimalSeparator;
        private String symbolPosition;
        private boolean spaceBetweenSymbol;
    }

    @Data
    public static class AdvancedFeatures {
        private boolean realTimeConversion;
        private boolean historicalRates;
        private boolean multiCurrencyWallets;
        private boolean autoCurrencySwitching;
        private boolean offlineMode;
        private boolean rateAlerts;
    }

    @Data
    public static class Compliance {
        private SouthAfrica southAfrica;
        private Europe europe;
        private Nigeria nigeria;
        private Kenya kenya;
        private Global global;

        @Data
        public static class SouthAfrica {
            @JsonProperty("POPIA")
            private boolean popia;
            @JsonProperty("FICA")
            private boolean fica;
            @JsonProperty("VAT")
            private double vat;
        }

        @Data
        public static class Europe {
            @JsonProperty("GDPR")
            private boolean gdpr;
            @JsonProperty("PSD2")
            private boolean psd2;
            @JsonProperty("VAT")
            private boolean vat;
        }

        @Data
        public static class Nigeria {
            @JsonProperty("NDPR")
            private boolean ndpr;
            @JsonProperty("CBNGuidelines")
            private boolean cbnGuidelines;
        }

        @Data
        public static class Kenya {
            @JsonProperty("DataProtectionAct")
            private boolean dataProtectionAct;
            @JsonProperty("CBKRegulations")
            private boolean cbkRegulations;
        }

        @Data
        public static class Global {
            @JsonProperty("AML")
            private boolean aml;
            @JsonProperty("KYC")
            private boolean kyc;
            @JsonProperty("SanctionScreening")
            private boolean sanctionScreening;
        }
    }

    @Data
    public static class PaymentMethods {
        private List<String> southAfrica;
        private List<String> westAfrica;
        private List<String> eastAfrica;
        private List<String> europe;
        private List<String> global;
    }

    @Data
    public static class Localization {
        private List<String> supportedLanguages;
        private boolean rtlSupport;
        private boolean localeAwareFormatting;
        private CulturalAdaptations culturalAdaptations;

        @Data
        public static class CulturalAdaptations {
            private boolean islamicCalendar;
            private boolean localHolidays;
            private List<String> numberSystems;
        }
    }

    @Data
    public static class Analytics {
        private List<String> mostPopularCurrencies;
        private String conversionVolume;
        private double userSatisfaction;
        private double uptime;
    }
}
