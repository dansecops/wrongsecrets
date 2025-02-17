package org.owasp.wrongsecrets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.wrongsecrets.challenges.docker.Challenge1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    properties = {"ctf_enabled=true", "ctf_key=randomtextforkey"},
    classes = WrongSecretsApplication.class
)
@AutoConfigureMockMvc
class ChallengesControllerCTFModeTest {

    @Autowired
    private MockMvc mvc;


    @Test
    void shouldNotSpoilWhenInCTFMode() throws Exception {
        mvc.perform(get("/spoil-1"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Spoils are disabled in CTF mode")));

    }

    @Test
    void shouldNotSpoilWhenInCTFModeEvenWhenChallengeUnsupported() throws Exception {
        mvc.perform(get("/spoil-5"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Spoils are disabled in CTF mode")));

    }

    @Test
    void shouldShowFlagWhenRespondingWithSuccessInCTFMode() throws Exception {
        var spoil = new Challenge1(new InMemoryScoreCard(1)).spoiler().solution();
        mvc.perform(post("/challenge/1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("solution", spoil)
                .param("action", "submit")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("ba9a72ac7057576344856")));

    }


    @Test
    void shouldEnableK8sExercises() throws Exception{
        mvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("class=\"disabled\">Challenge 5</a></td>")))
            .andExpect(content().string(containsString("class=\"disabled\">Challenge 6</a></td>")))
            .andExpect(content().string(containsString("class=\"disabled\">Challenge 7</a></td>")));
    }

    @Test
    void shouldStillDissableTestsIfNotPreconfigured() throws Exception {
        testK8sChallenge("/challenge/5");
        testK8sChallenge("/challenge/6");
        testK8sChallenge("/challenge/7");
        testForCloudCluster("/challenge/9");
        testForCloudCluster("/challenge/10");
        testForCloudCluster("/challenge/11");
    }

    private void testK8sChallenge(String url) throws Exception {
        mvc.perform(get(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("We are running outside a K8s cluster")));
    }

    private void testForCloudCluster(String url) throws Exception {
        mvc.perform(get(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("We are running outside a properly configured Cloud environment.")));
    }
}
