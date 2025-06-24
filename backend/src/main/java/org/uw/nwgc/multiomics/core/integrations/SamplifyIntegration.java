package org.uw.nwgc.multiomics.core.integrations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
public class SamplifyIntegration implements Integration {

    @Value("${SAMPLIFY_SERVER_TOKEN:}")
    private String TOKEN;

    @Value("${SAMPLIFY_SERVER_URL:}")
    private String API_URL;

    @Override
    public String integrationName() {
        return "Samplify";
    }

    @Override
    public List<ExternalSampleDTO> queryExternalSamples(String queryString) {
        if (TOKEN == null || TOKEN.isEmpty()) {
            return List.of();
        }

        RestTemplate rest = new RestTemplate();

        // Build a properly encoded URI with your query param
        URI uri = UriComponentsBuilder
                .fromHttpUrl(API_URL + "/samplify/ExternalSampleAjax")
                .queryParam("query", queryString)
                .build()
                .encode()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-auth-token", TOKEN);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<ExternalSampleDTO>> resp = rest.exchange(
                uri,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        return resp.getBody();
    }

    @Override
    public List<SampleFileDTO> querySampleFiles(String queryString) {
        if (TOKEN == null || TOKEN.isEmpty()) {
            return List.of();
        }

        RestTemplate rest = new RestTemplate();

        // Build a properly encoded URI with your query param
        URI uri = UriComponentsBuilder
                .fromHttpUrl(API_URL + "/samplify/ProjectSampleFilesAjax")
                .queryParam("query", queryString)
                .build()
                .encode()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-auth-token", TOKEN);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<SampleFileDTO>> resp = rest.exchange(
                uri,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<SampleFileDTO>>() {}
        );

        return resp.getBody();
    }
}
