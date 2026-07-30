package com.codecanvas.paymentservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Configuration
@EnableConfigurationProperties(
        RazorpayConfig.RazorpayProperties.class
)
public class RazorpayConfig {

    @Bean
    public RazorpayClient razorpayClient(
            RazorpayProperties razorpayProperties)
            throws RazorpayException {

        validateProperties(razorpayProperties);

        return new RazorpayClient(
                razorpayProperties.getKeyId(),
                razorpayProperties.getKeySecret()
        );
    }

    private void validateProperties(
            RazorpayProperties razorpayProperties) {

        if (razorpayProperties.getKeyId() == null
                || razorpayProperties.getKeyId().isBlank()) {

            throw new IllegalStateException(
                    "Razorpay key ID is not configured"
            );
        }

        if (razorpayProperties.getKeySecret() == null
                || razorpayProperties.getKeySecret().isBlank()) {

            throw new IllegalStateException(
                    "Razorpay key secret is not configured"
            );
        }

        if (razorpayProperties.getWebhookSecret() == null
                || razorpayProperties
                .getWebhookSecret()
                .isBlank()) {

            throw new IllegalStateException(
                    "Razorpay webhook secret is not configured"
            );
        }
    }

    @ConfigurationProperties(prefix = "razorpay")
    public static class RazorpayProperties {

        private String keyId;
        private String keySecret;
        private String webhookSecret;

        public RazorpayProperties() {
        }

        public String getKeyId() {
            return keyId;
        }

        public void setKeyId(String keyId) {
            this.keyId = keyId;
        }

        public String getKeySecret() {
            return keySecret;
        }

        public void setKeySecret(String keySecret) {
            this.keySecret = keySecret;
        }

        public String getWebhookSecret() {
            return webhookSecret;
        }

        public void setWebhookSecret(
                String webhookSecret) {

            this.webhookSecret = webhookSecret;
        }
    }
}