package org.owasp.wrongsecrets;

import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.canaries.AdditionalCanaryData;
import org.owasp.wrongsecrets.canaries.CanaryToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientResponseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChallengeAPiControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplateBuilder builder;

    public ChallengeAPiControllerTest() {
    }

    @Test
    void shouldGetListOfChallenges() {
        var restTemplate = builder.build();

        var callbackAdress = "http://localhost:"+port+"/api/Challenges";

        try {
            var response = restTemplate.getForEntity(callbackAdress, String.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).contains("hint");
        } catch (RestClientResponseException e) {
           fail(e);
        }
    }
}

/*
"manageUrl" : "url", "memo" : "memo", "channel" : "channel", "time" : "time", "additionalData" : { "srcIp" : "soruce", "useragent" : "agent", "referer" : "referer", "location" : "locatoin"}}
 */
