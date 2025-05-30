package org.uw.nwgc.multiomics.core.integrations;

import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
public class SamplifyIntegration implements Integration {

    @Override
    public List<ExternalSampleDto> queryExternalSamples(String queryString) {
        RestTemplate rest = new RestTemplate();

        // Build a properly encoded URI with your query param
        URI uri = UriComponentsBuilder
                .fromHttpUrl("http://localhost:8080/samplify/ExternalSampleAjax")
                .queryParam("query", queryString)
                .build()
                .encode()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-auth-token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtdWx0aW9taWNzIiwiaXNzIjoic2FtcGxpZnkiLCJleHAiOjE3NDk0NDEyMDl9.5sPlNiqYu41b-cDqDe4Kkp3x4RFraQn6UiSJ6cQHUGU");

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<ExternalSampleDto>> resp = rest.exchange(
                uri,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        return resp.getBody();
    }

    @Override
    public List<SampleFileDto> querySampleFiles(String queryString) {
        RestTemplate rest = new RestTemplate();

        // Build a properly encoded URI with your query param
        URI uri = UriComponentsBuilder
                .fromHttpUrl("http://localhost:8080/samplify/ProjectSampleFilesAjax")
                .queryParam("query", queryString)
                .build()
                .encode()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-auth-token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtdWx0aW9taWNzIiwiaXNzIjoic2FtcGxpZnkiLCJleHAiOjE3NDk0NDEyMDl9.5sPlNiqYu41b-cDqDe4Kkp3x4RFraQn6UiSJ6cQHUGU");

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<SampleFileDto>> resp = rest.exchange(
                uri,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<SampleFileDto>>() {}
        );

        return resp.getBody();
    }
}
