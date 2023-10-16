package com.example.rqchallenge.employees.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
public class HttpRestUtil {

    public static final Logger logger = LoggerFactory.getLogger(HttpRestUtil.class);

    private static RestTemplate restTemplate = new RestTemplate();

    public static Optional<String> callRestAPI(String url, HttpHeaders headers, String methodType, Optional<String> requestBody) {
        logger.debug("Calling REST URL : {}", url);
        HttpEntity<String> entity = new HttpEntity<>(requestBody.orElse("parameters"), headers);
        HttpMethod method;
        switch (methodType) {
            case AppConstants.GET:
                method = HttpMethod.GET;
                break;
            case AppConstants.POST:
                method = HttpMethod.POST;
                break;
            case AppConstants.PUT:
                method = HttpMethod.PUT;
                break;
            case AppConstants.DELETE:
                method = HttpMethod.DELETE;
                break;
            default:
                logger.error("Provided methodType : {} is not supported by HttpUtil.", methodType);
                return Optional.empty();
        }
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, method, entity, String.class);
            return Optional.ofNullable(response.getBody());
        } catch (Exception ex) {
            logger.error("Error occurred while calling REST API : {} ", url, ex);
            throw ex;
        }
    }
}
