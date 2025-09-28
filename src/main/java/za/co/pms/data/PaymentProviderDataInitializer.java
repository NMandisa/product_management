package za.co.pms.data;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import za.co.pms.model.settings.PaymentProvider;
import za.co.pms.model.settings.payment.*;
import za.co.pms.repository.*;

import java.util.Set;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Component
public class PaymentProviderDataInitializer {

    private final PaymentProviderRepository paymentProviderRepository;
    private final RegionProviderMappingRepository regionProviderMappingRepository;
    private final RoutingPriorityRuleRepository routingPriorityRuleRepository;
    private final RoutingFallbackRepository routingFallbackRepository;
    private final SettlementRulesRepository settlementRulesRepository;

    @Autowired
    public PaymentProviderDataInitializer(PaymentProviderRepository paymentProviderRepository, RegionProviderMappingRepository regionProviderMappingRepository,
    RoutingPriorityRuleRepository routingPriorityRuleRepository,
    RoutingFallbackRepository routingFallbackRepository, SettlementRulesRepository settlementRulesRepository){
        this.paymentProviderRepository=paymentProviderRepository;
        this.routingFallbackRepository=routingFallbackRepository;
        this.settlementRulesRepository=settlementRulesRepository;
        this.regionProviderMappingRepository=regionProviderMappingRepository;
        this.routingPriorityRuleRepository=routingPriorityRuleRepository;
    }



    @PostConstruct
    @Transactional
    public void initialize() {
        if (paymentProviderRepository.count() == 0) {
            initializePaymentProviders();
            initializeRegionMappings();
            initializeRoutingRules();
            initializeSettlementRules();
        }
    }

    private void initializePaymentProviders() {
        // SnapScan
        PaymentProvider snapscan = new PaymentProvider();
        snapscan.setId("pp_snapscan");
        snapscan.setName("SnapScan");
        snapscan.setType("mobile_wallet");
        snapscan.setStatus("active");
        snapscan.setVersion("2025-09-01");

        // Set up relationships
        snapscan.setRegions(Set.of(new ProviderRegion(snapscan, "ZA")));
        snapscan.setCurrencies(Set.of(new ProviderCurrency(snapscan, "ZAR")));
        snapscan.setCapabilities(Set.of(
                new ProviderCapability(snapscan, "qr"),
                new ProviderCapability(snapscan, "instant_refund"),
                new ProviderCapability(snapscan, "recurring"),
                new ProviderCapability(snapscan, "split_payments")
        ));

        // Settlement
        ProviderSettlement snapscanSettlement = new ProviderSettlement();
        snapscanSettlement.setProvider(snapscan);
        snapscanSettlement.setSettlementTime("T+0");
        snapscanSettlement.setSettlementMethod("bank_transfer");
        snapscanSettlement.setSuccessRate(99.8);
        snapscanSettlement.setMaxAmount(50000.0);
        snapscan.setSettlement(snapscanSettlement);

        // Fees
        ProviderFees snapscanFees = new ProviderFees();
        snapscanFees.setProvider(snapscan);
        snapscanFees.setFixedFee(0.0);
        snapscanFees.setPercentFee(0.015);
        snapscanFees.setMinAmount(1.0);
        snapscanFees.setMaxAmount(20.0);
        snapscanFees.setCurrency("ZAR");
        snapscan.setFees(snapscanFees);

        // Integration
        ProviderIntegration snapscanIntegration = new ProviderIntegration();
        snapscanIntegration.setProvider(snapscan);
        snapscanIntegration.setApiUrl("https://api.snapscan.co.za/v1");
        snapscanIntegration.setAuthType("oauth2");
        snapscanIntegration.setTimeoutMs(30000);
        snapscanIntegration.setRateLimit(100);
        snapscanIntegration.setWebhookUrl("/webhooks/snapscan");
        snapscan.setIntegration(snapscanIntegration);

        // Metadata
        Set<ProviderMetadata> snapscanMetadata = Set.of(
                new ProviderMetadata(snapscan, "supportedBanks", "absa,fnb,standard,nedbank"),
                new ProviderMetadata(snapscan, "dailyLimit", "50000"),
                new ProviderMetadata(snapscan, "kycRequired", "true")
        );
        snapscan.setMetadata(snapscanMetadata);

        paymentProviderRepository.save(snapscan);

        // Similarly initialize other providers: Paystack, M-Pesa, Flutterwave, Visa/Mastercard
        initializePaystack();
        initializeMpesa();
        initializeFlutterwave();
        initializeCardProcessor();
    }

    private void initializePaystack() {
        PaymentProvider paystack = new PaymentProvider();
        paystack.setId("pp_paystack");
        paystack.setName("Paystack");
        paystack.setType("gateway");
        paystack.setStatus("active");
        paystack.setVersion("2025-07-15");

        paystack.setRegions(Set.of(
                new ProviderRegion(paystack, "NG"),
                new ProviderRegion(paystack, "GH"),
                new ProviderRegion(paystack, "SN"),
                new ProviderRegion(paystack, "CI")
        ));

        paystack.setCurrencies(Set.of(
                new ProviderCurrency(paystack, "NGN"),
                new ProviderCurrency(paystack, "GHS"),
                new ProviderCurrency(paystack, "XOF")
        ));

        // Set up other relationships similarly...
        paymentProviderRepository.save(paystack);
    }

    private void initializeRegionMappings() {
        // South Africa
        Set<RegionProviderMapping> southAfricaMappings = Set.of(
                new RegionProviderMapping("southAfrica", "pp_snapscan", 1),
                new RegionProviderMapping("southAfrica", "pp_flutterwave", 2),
                new RegionProviderMapping("southAfrica", "pp_card_visa_master", 3)
        );
        regionProviderMappingRepository.saveAll(southAfricaMappings);

        // Similarly initialize other region mappings...
    }

    private void initializeRoutingRules() {
        // Rule 1: ZAR currency, amount <= 50000
        RoutingPriorityRule rule1 = new RoutingPriorityRule();
        rule1.setWeight(10);
        rule1.setConditionJson("{\"currency\": \"ZAR\", \"amount\": {\"max\": 50000}}");

        Set<RoutingPreferredProvider> rule1Providers = Set.of(
                new RoutingPreferredProvider(rule1, "pp_snapscan", 1),
                new RoutingPreferredProvider(rule1, "pp_flutterwave", 2)
        );
        rule1.setPreferredProviders(rule1Providers);
        routingPriorityRuleRepository.save(rule1);

        // Similarly initialize other routing rules...

        // Initialize fallback order
        Set<RoutingFallback> fallbacks = Set.of(
                new RoutingFallback("regionPreferred", 1),
                new RoutingFallback("globalGateway", 2),
                new RoutingFallback("manualReview", 3)
        );
        routingFallbackRepository.saveAll(fallbacks);
    }

    private void initializeSettlementRules() {
        SettlementRules settlementRules = new SettlementRules();
        settlementRules.setInstantSettlementMax(5000.0);
        settlementRules.setBatchProcessingInterval("PT1H");
        settlementRules.setReconciliationWindow("PT2H");
        settlementRules.setAutoRetryAttempts(3);
        settlementRulesRepository.save(settlementRules);
    }

    private void initializeMpesa() {
        PaymentProvider mpesa = new PaymentProvider();
        mpesa.setId("pp_mpesa");
        mpesa.setName("M-Pesa");
        mpesa.setType("mobile_wallet");
        mpesa.setStatus("active");
        mpesa.setVersion("2025-06-01");

        // Regions
        Set<ProviderRegion> mpesaRegions = Set.of(
                new ProviderRegion(mpesa, "KE"),
                new ProviderRegion(mpesa, "TZ"),
                new ProviderRegion(mpesa, "UG")
        );
        mpesa.setRegions(mpesaRegions);

        // Currencies
        Set<ProviderCurrency> mpesaCurrencies = Set.of(
                new ProviderCurrency(mpesa, "KES"),
                new ProviderCurrency(mpesa, "TZS"),
                new ProviderCurrency(mpesa, "UGX")
        );
        mpesa.setCurrencies(mpesaCurrencies);

        // Capabilities
        Set<ProviderCapability> mpesaCapabilities = Set.of(
                new ProviderCapability(mpesa, "ussd"),
                new ProviderCapability(mpesa, "mobile_app"),
                new ProviderCapability(mpesa, "agent_network"),
                new ProviderCapability(mpesa, "bill_payments")
        );
        mpesa.setCapabilities(mpesaCapabilities);

        // Settlement
        ProviderSettlement mpesaSettlement = new ProviderSettlement();
        mpesaSettlement.setProvider(mpesa);
        mpesaSettlement.setSettlementTime("T+0");
        mpesaSettlement.setSettlementMethod("instant");
        mpesaSettlement.setSuccessRate(99.9);
        mpesaSettlement.setMaxAmount(500000.0);
        mpesa.setSettlement(mpesaSettlement);

        // Fees
        ProviderFees mpesaFees = new ProviderFees();
        mpesaFees.setProvider(mpesa);
        mpesaFees.setFixedFee(0.1);
        mpesaFees.setPercentFee(0.01);
        mpesaFees.setMinAmount(0.0);
        mpesaFees.setMaxAmount(100.0);
        mpesaFees.setCurrency("USD");
        mpesa.setFees(mpesaFees);

        // Integration
        ProviderIntegration mpesaIntegration = new ProviderIntegration();
        mpesaIntegration.setProvider(mpesa);
        mpesaIntegration.setApiUrl("https://api.mpesa.co.ke");
        mpesaIntegration.setAuthType("api_key");
        mpesaIntegration.setTimeoutMs(60000);
        mpesaIntegration.setRateLimit(200);
        mpesaIntegration.setWebhookUrl("/webhooks/mpesa");
        mpesa.setIntegration(mpesaIntegration);

        // Metadata
        Set<ProviderMetadata> mpesaMetadata = Set.of(
                new ProviderMetadata(mpesa, "agentNetworkSize", "500000"),
                new ProviderMetadata(mpesa, "dailyUserLimit", "150000"),
                new ProviderMetadata(mpesa, "offlineSupport", "true")
        );
        mpesa.setMetadata(mpesaMetadata);

        paymentProviderRepository.save(mpesa);
    }

    private void initializeFlutterwave() {
        PaymentProvider flutterwave = new PaymentProvider();
        flutterwave.setId("pp_flutterwave");
        flutterwave.setName("Flutterwave");
        flutterwave.setType("gateway");
        flutterwave.setStatus("active");
        flutterwave.setVersion("2025-08-20");

        // Regions
        Set<ProviderRegion> flutterwaveRegions = Set.of(
                new ProviderRegion(flutterwave, "NG"),
                new ProviderRegion(flutterwave, "GH"),
                new ProviderRegion(flutterwave, "KE"),
                new ProviderRegion(flutterwave, "ZA"),
                new ProviderRegion(flutterwave, "UG")
        );
        flutterwave.setRegions(flutterwaveRegions);

        // Currencies
        Set<ProviderCurrency> flutterwaveCurrencies = Set.of(
                new ProviderCurrency(flutterwave, "NGN"),
                new ProviderCurrency(flutterwave, "GHS"),
                new ProviderCurrency(flutterwave, "KES"),
                new ProviderCurrency(flutterwave, "ZAR"),
                new ProviderCurrency(flutterwave, "UGX"),
                new ProviderCurrency(flutterwave, "USD")
        );
        flutterwave.setCurrencies(flutterwaveCurrencies);

        // Capabilities
        Set<ProviderCapability> flutterwaveCapabilities = Set.of(
                new ProviderCapability(flutterwave, "cross_border"),
                new ProviderCapability(flutterwave, "multi_currency"),
                new ProviderCapability(flutterwave, "barter"),
                new ProviderCapability(flutterwave, "invoice")
        );
        flutterwave.setCapabilities(flutterwaveCapabilities);

        // Settlement
        ProviderSettlement flutterwaveSettlement = new ProviderSettlement();
        flutterwaveSettlement.setProvider(flutterwave);
        flutterwaveSettlement.setSettlementTime("T+2");
        flutterwaveSettlement.setSettlementMethod("multi_currency");
        flutterwaveSettlement.setSuccessRate(99.2);
        flutterwaveSettlement.setMaxAmount(10000000.0);
        flutterwave.setSettlement(flutterwaveSettlement);

        // Fees
        ProviderFees flutterwaveFees = new ProviderFees();
        flutterwaveFees.setProvider(flutterwave);
        flutterwaveFees.setFixedFee(0.5);
        flutterwaveFees.setPercentFee(0.025);
        flutterwaveFees.setMinAmount(0.0);
        flutterwaveFees.setMaxAmount(5000.0);
        flutterwaveFees.setCurrency("USD");
        flutterwave.setFees(flutterwaveFees);

        // Integration
        ProviderIntegration flutterwaveIntegration = new ProviderIntegration();
        flutterwaveIntegration.setProvider(flutterwave);
        flutterwaveIntegration.setApiUrl("https://api.flutterwave.com/v3");
        flutterwaveIntegration.setAuthType("secret_key");
        flutterwaveIntegration.setTimeoutMs(40000);
        flutterwaveIntegration.setRateLimit(300);
        flutterwaveIntegration.setWebhookUrl("/webhooks/flutterwave");
        flutterwave.setIntegration(flutterwaveIntegration);

        // Metadata
        Set<ProviderMetadata> flutterwaveMetadata = Set.of(
                new ProviderMetadata(flutterwave, "supportedCountries", "34"),
                new ProviderMetadata(flutterwave, "fxSettlement", "true"),
                new ProviderMetadata(flutterwave, "riskEngine", "advanced")
        );
        flutterwave.setMetadata(flutterwaveMetadata);

        paymentProviderRepository.save(flutterwave);
    }

    private void initializeCardProcessor() {
        PaymentProvider cardProcessor = new PaymentProvider();
        cardProcessor.setId("pp_card_visa_master");
        cardProcessor.setName("Visa/Mastercard Gateway");
        cardProcessor.setType("card_processor");
        cardProcessor.setStatus("active");
        cardProcessor.setVersion("2025-01-01");

        // Regions - GLOBAL
        Set<ProviderRegion> cardRegions = Set.of(
                new ProviderRegion(cardProcessor, "GLOBAL")
        );
        cardProcessor.setRegions(cardRegions);

        // Currencies - ALL
        Set<ProviderCurrency> cardCurrencies = Set.of(
                new ProviderCurrency(cardProcessor, "ALL")
        );
        cardProcessor.setCurrencies(cardCurrencies);

        // Capabilities
        Set<ProviderCapability> cardCapabilities = Set.of(
                new ProviderCapability(cardProcessor, "3d_secure"),
                new ProviderCapability(cardProcessor, "tokenization"),
                new ProviderCapability(cardProcessor, "chargebacks"),
                new ProviderCapability(cardProcessor, "recurring")
        );
        cardProcessor.setCapabilities(cardCapabilities);

        // Settlement
        ProviderSettlement cardSettlement = new ProviderSettlement();
        cardSettlement.setProvider(cardProcessor);
        cardSettlement.setSettlementTime("T+2");
        cardSettlement.setSettlementMethod("card_network");
        cardSettlement.setSuccessRate(98.5);
        cardSettlement.setMaxAmount(50000.0);
        cardProcessor.setSettlement(cardSettlement);

        // Fees
        ProviderFees cardFees = new ProviderFees();
        cardFees.setProvider(cardProcessor);
        cardFees.setFixedFee(0.3);
        cardFees.setPercentFee(0.029);
        cardFees.setMinAmount(0.0);
        cardFees.setMaxAmount(10.0);
        cardFees.setCurrency("USD");
        cardProcessor.setFees(cardFees);

        // Integration
        ProviderIntegration cardIntegration = new ProviderIntegration();
        cardIntegration.setProvider(cardProcessor);
        cardIntegration.setApiUrl("https://api.stripe.com/v1");
        cardIntegration.setAuthType("secret_key");
        cardIntegration.setTimeoutMs(30000);
        cardIntegration.setRateLimit(1000);
        cardIntegration.setWebhookUrl("/webhooks/cards");
        cardProcessor.setIntegration(cardIntegration);

        // Metadata
        Set<ProviderMetadata> cardMetadata = Set.of(
                new ProviderMetadata(cardProcessor, "supportedNetworks", "visa,mastercard,amex"),
                new ProviderMetadata(cardProcessor, "fraudDetection", "true"),
                new ProviderMetadata(cardProcessor, "pciCompliant", "true")
        );
        cardProcessor.setMetadata(cardMetadata);

        paymentProviderRepository.save(cardProcessor);
    }

}
