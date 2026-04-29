package com.jersa.persistence.rest.configs;

import com.jersa.persistence.rest.models.RJsonplaceholderProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RestClientConfig {
    private final RJsonplaceholderProperties properties;

    @Bean(name = "jsonplaceholder")
    @ConditionalOnProperty(
            prefix = "jsonplaceholder",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public RestClient restClient() {
        return RestClient.builder().
                baseUrl(properties.baseUrl())
                .requestInterceptors(interceptors -> {
                    interceptors.add(loggingInterceptor());
                    interceptors.add(errorLoggingInterceptor());
                })
                .defaultHeaders(headers -> {
                    headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                    headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
                })
                .build();
    }

    private ClientHttpRequestInterceptor loggingInterceptor() {
        return (req, body, exe) -> {
            log.info("Calling JsonPlaceHolder API");
            log.info("Method: {}", req.getMethod());
            log.info("URI: {}", req.getURI());
            log.info("Headers: {}", req.getHeaders());

            final long start = System.currentTimeMillis();
            final long end;
            try {
                var res = exe.execute(req, body);
                end = System.currentTimeMillis() - start;

                log.info("Status: {} ms", res.getStatusCode());
                log.info("Execution time: {} ms", end);
                return res;
            } catch (Exception e) {
                log.error("Error calling Jsonplaceholder API", e);
                throw e;
            }
        };
    }

    // if there's an error
    private ClientHttpRequestInterceptor errorLoggingInterceptor() {
        return (req, body, exe) -> {
            try {
                return exe.execute(req, body);
            } catch (Exception e) {
                log.error("Error message: {}", e.getMessage());
                throw e;
            }
        };
    }
}
