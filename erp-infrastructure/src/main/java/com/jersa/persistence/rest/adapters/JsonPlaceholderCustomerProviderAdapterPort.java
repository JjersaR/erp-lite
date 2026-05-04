package com.jersa.persistence.rest.adapters;

import com.jersa.ports.services.ICustomerProviderServicePort;
import com.jersa.entities.customer.RCustomerInfo;
import com.jersa.persistence.rest.mappers.ICustomerInfoMapper;
import com.jersa.persistence.rest.dtos.RUserDTO;
import com.jersa.persistence.rest.models.RJsonplaceholderProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

@Slf4j
@Service
public class JsonPlaceholderCustomerProviderAdapterPort implements ICustomerProviderServicePort {

    private final RestClient jsonClient;
    private final ICustomerInfoMapper mapper;
    private final String endpoint;

    public JsonPlaceholderCustomerProviderAdapterPort(@Qualifier("jsonplaceholder") RestClient restClient, ICustomerInfoMapper mapper, RJsonplaceholderProperties props) {
        this.jsonClient = restClient;
        this.mapper = mapper;
        this.endpoint = props.usersEndpoint();
    }

    @Override
    public Optional<RCustomerInfo> findById(Long id) {
        log.info("findById: {}", id);

        try {
            final RUserDTO response = this.jsonClient.get().uri(endpoint, id).retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        log.error("Error on client side: {}", req);
                    }).onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                        log.error("Error on server side: {}", req);
                    }).body(RUserDTO.class);

            if (response == null) {
                log.warn("NO user found");
                return Optional.empty();
            }
            log.info("User found: {}", response);

            return Optional.of(this.mapper.toCustomerInfo(response));
        } catch (RestClientException rce) {
            log.error("Error on findById while call api, ", rce);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error on findById", e);
            return Optional.empty();
        }
    }

    @Override
    public boolean existsById(Long id) {
        log.info("ExistsById: {}", id);
        return false;
    }
}
