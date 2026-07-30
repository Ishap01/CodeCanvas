package com.codecanvas.paymentservice.config;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import feign.Logger.Level;
import feign.Response;
import feign.codec.ErrorDecoder;

public class FeignConfig {

    @Bean
    public Level feignLoggerLevel() {
        return Level.BASIC;
    }

    @Bean
    public ErrorDecoder feignErrorDecoder() {
        return new UserServiceFeignErrorDecoder();
    }

    private static class UserServiceFeignErrorDecoder
            implements ErrorDecoder {

        private static final Logger LOGGER =
                LoggerFactory.getLogger(
                        UserServiceFeignErrorDecoder.class
                );

        private final ErrorDecoder defaultErrorDecoder =
                new ErrorDecoder.Default();

        @Override
        public Exception decode(
                String methodKey,
                Response response) {

            String responseBody =
                    readResponseBody(response);

            LOGGER.error(
                    "Feign request failed. method={}, status={}, reason={}, body={}",
                    methodKey,
                    response.status(),
                    response.reason(),
                    responseBody
            );

            return defaultErrorDecoder.decode(
                    methodKey,
                    response
            );
        }

        private String readResponseBody(
                Response response) {

            if (response.body() == null) {
                return "";
            }

            try {
                byte[] bodyBytes =
                        response.body()
                                .asInputStream()
                                .readAllBytes();

                return new String(
                        bodyBytes,
                        StandardCharsets.UTF_8
                );

            } catch (Exception exception) {

                LOGGER.warn(
                        "Unable to read Feign error response body",
                        exception
                );

                return "";
            }
        }
    }
}