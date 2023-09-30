package com.skypro.simplebanking.controller;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpHeaders;
import com.skypro.simplebanking.entity.Account;
import com.skypro.simplebanking.entity.User;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.util.Base64Utils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;



public class TransferControllerTest extends IntegrationTest {

    private long getAccountId(String userName) {
        long userId = userRepository.findByUsername(userName).orElseThrow().getId();
        Collection<Account> account = accountRepository.findByUserId(userId);
        List<Account> accountList = new ArrayList<>(account);
        return accountList.get(0).getId();

    }

    private User getUserByUserName(String userName) {
        return userRepository.findByUsername(userName).orElseThrow();
    }

    private long getUserIdByUserName(String userName) {
        return getUserByUserName(userName).getId();
    }

    private String base64Encoded(String userName, String password) {
        return "Basic " + Base64Utils.encodeToString((userName + ":" + password).getBytes(StandardCharsets.UTF_8));
    }

    private JSONObject getTransferRequest() throws JSONException {
        JSONObject transferRequest = new JSONObject();
        transferRequest.put("fromAccountId", getAccountId("Lila"));
        transferRequest.put("toUserId", getUserIdByUserName("Dima"));
        transferRequest.put("toAccountId", getAccountId("Dima"));
        transferRequest.put("amount", 1L);
        return transferRequest;
    }

    @Test
    void transfer_test() throws Exception {
        getTransferRequest();
        mockMvc.perform(post("/transfer")
                        .header(HttpHeaders.AUTHORIZATION, base64Encoded("Lila", "dodo"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getTransferRequest().toString()))
                .andExpect(status().isOk());
    }
    @Test
    void transfer_AccountNotFoundExeptiontest() throws Exception {
        JSONObject transferRequest = new JSONObject();
        transferRequest.put("fromAccountId", 1);
        transferRequest.put("toUserId", 2);
        transferRequest.put("toAccountId",-1);
        transferRequest.put("amount", 1L);
        mockMvc.perform(post("/transfer")
                        .header(HttpHeaders.AUTHORIZATION, base64Encoded("Lila", "dodo"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferRequest.toString()))
                .andExpect(status().isNotFound());
    }
}

