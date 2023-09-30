package com.skypro.simplebanking.controller;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.Base64Utils;


import java.nio.charset.StandardCharsets;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



public class UserControllerTest extends IntegrationTest {

    private String base64Encoded(String userName, String password) {
        return "Basic " + Base64Utils.encodeToString((userName + ":" + password).getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void createUser_Ok() throws Exception {
        JSONObject createUserRequest = new JSONObject();
        createUserRequest.put("username", "username");
        createUserRequest.put("password", "password");
        mockMvc.perform(post("/user")
                        .header("X-SECURITY-ADMIN-KEY", "SUPER_SECRET_KEY_FROM_ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUserRequest.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void createUser_WhenUserIsExistException() throws Exception {
        JSONObject createUserRequest = new JSONObject();
        createUserRequest.put("username", "Ilya");
        createUserRequest.put("password", "Nik");
        mockMvc.perform(post("/user")
                        .header("X-SECURITY-ADMIN-KEY", "SUPER_SECRET_KEY_FROM_ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUserRequest.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getListUsers_Test() throws Exception {
        mockMvc.perform(get("/user/list")
                        .header(HttpHeaders.AUTHORIZATION, base64Encoded("Ilya", "Nik")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void getListUsers_WhenAdminTryToGet() throws Exception {
        JSONObject createUserRequest = new JSONObject();
        createUserRequest.put("username", "admin");
        createUserRequest.put("password", "****");
        mockMvc.perform(get("/user/list")
                        .header("X-SECURITY-ADMIN-KEY", "SUPER_SECRET_KEY_FROM_ADMIN"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getMyProfile_Test() throws Exception {
        mockMvc.perform(get("/user/me")
                        .header(HttpHeaders.AUTHORIZATION, base64Encoded("Ilya", "Nik")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Ilya"))
                .andExpect(jsonPath("$.accounts.length()").value(3));

    }
}
