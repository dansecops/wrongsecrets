package org.owasp.wrongsecrets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.wrongsecrets.challenges.kubernetes.Challenge5;
import org.owasp.wrongsecrets.challenges.kubernetes.Challenge6;
import org.owasp.wrongsecrets.challenges.kubernetes.Challenge7;
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
    properties = {"ctf_enabled=true", "ctf_key=randomtextforkey", "SPECIAL_K8S_SECRET=test5", "SPECIAL_SPECIAL_K8S_SECRET=test6", "vaultpassword=test7"},
    classes = WrongSecretsApplication.class
)
@AutoConfigureMockMvc
class ChallengesControllerCTFModeWithPresetK8sValuesTest {

    @Autowired
    private MockMvc mvc;


    @Test
    void shouldNotSpoilWhenInCTFMode() throws Exception {
        mvc.perform(get("/spoil-5"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Spoils are disabled in CTF mode")));

    }

    @Test
    void shouldShowFlagWhenRespondingWithSuccessInCTFModeChallenge5() throws Exception {
        var spoil = new Challenge5(new InMemoryScoreCard(1), "test5").spoiler().solution();
        mvc.perform(post("/challenge/5")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("solution", spoil)
                .param("action", "submit")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("26d5e409100ca8dc3bd2dba115b81f5b7889fbbd")));
    }

    @Test
    void shouldShowFlagWhenRespondingWithSuccessInCTFModeChallenge6() throws Exception {
        var spoil = new Challenge6(new InMemoryScoreCard(1), "test6").spoiler().solution();
        mvc.perform(post("/challenge/6")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("solution", spoil)
                .param("action", "submit")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("18af49a1b18359e0bf9b9a0")));
    }

    @Test
    void shouldShowFlagWhenRespondingWithSuccessInCTFModeChallenge7() throws Exception {
        var spoil = new Challenge7(new InMemoryScoreCard(1), null, "test7").spoiler().solution();
        mvc.perform(post("/challenge/7")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("solution", spoil)
                .param("action", "submit")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("881951b59ea4818c2")));
    }

    @Test
    void shouldEnableK8sExercises() throws Exception{
        mvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("<td>&nbsp;<a href=\"/challenge/5\">Challenge 5</a></td>")))
            .andExpect(content().string(containsString("<td>&nbsp;<a href=\"/challenge/6\">Challenge 6</a></td>")))
            .andExpect(content().string(containsString("<td>&nbsp;<a href=\"/challenge/7\">Challenge 7</a></td>")));
    }

}
