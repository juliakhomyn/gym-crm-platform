package com.gym.crm.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gia.openapi.model.ErrorResponse;
import com.gia.openapi.model.LoginChangeRequest;
import com.gia.openapi.model.LoginRequest;
import com.gia.openapi.model.LoginResponse;
import com.gym.crm.core.exception.ApiError;
import com.gym.crm.core.exception.BadCredentialsException;
import com.gym.crm.core.exception.EntityNotFoundException;
import com.gym.crm.core.exception.UserAuthenticationException;
import com.gym.crm.core.exception.UserAuthorizationException;
import com.gym.crm.core.facade.GymFacade;
import com.gym.crm.core.security.CustomUserDetailsService;
import com.gym.crm.core.security.JwtService;
import com.gym.crm.core.security.TokenBlacklistService;
import com.gym.crm.core.utils.TestDataProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.gym.crm.core.utils.JsonUtil.readJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
    private static final String USERNAME = "Simone.Radcliffe";
    private static final String PASSWORD = "password123";
    private static final String NEW_PASSWORD = "newPassword";
    private static final String BASE_URL = "/api/v1/auth";

    private final LoginChangeRequest loginChangeRequest = buildLoginChangeRequest();

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GymFacade facade;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private TokenBlacklistService tokenBlacklistService;

    @Test
    void login_shouldReturnOk() throws Exception {
        String requestBody = readJson("json/auth/login_request.json");
        String expectedResponse = readJson("json/auth/login_response.json");
        LoginResponse response = TestDataProvider.buildLoginResponse();

        when(facade.login(any(LoginRequest.class))).thenReturn(response);

        String actualResponse = mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JSONAssert.assertEquals(expectedResponse, actualResponse, true);
        verify(facade).login(any(LoginRequest.class));
    }

    @Test
    void login_shouldReturnErrorResponse_whenUsernameNull() throws Exception {
        String requestBody = readJson("json/auth/login_request_invalid.json");
        String expectedResponse = readJson("json/auth/username_null_error_response.json");

        String actualResponse = mockMvc.perform(post(BASE_URL + "/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JSONAssert.assertEquals(expectedResponse, actualResponse, true);
        verifyNoInteractions(facade);
    }

    @Test
    void login_shouldReturnBadCredentialsException_whenInvalidPassword() throws Exception {
        String requestBody = readJson("json/auth/login_request.json");
        String expectedResponse = readJson("json/auth/bad_credentials_error_response.json");

        doThrow(new BadCredentialsException("Invalid credentials for user")).when(facade).login(any(LoginRequest.class));

        String actualResponse = mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JSONAssert.assertEquals(expectedResponse, actualResponse, true);
        verify(facade).login(any(LoginRequest.class));
    }

    @Test
    void login_shouldEntityNotFoundException_whenUserNotFound() throws Exception {
        String requestBody = readJson("json/auth/login_request.json");
        String expectedResponse = readJson("json/auth/user_not_found_error_response.json");

        doThrow(new EntityNotFoundException("User not found")).when(facade).login(any(LoginRequest.class));

        String actualResponse = mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JSONAssert.assertEquals(expectedResponse, actualResponse, true);
        verify(facade).login(any(LoginRequest.class));
    }

    @Test
    void changePassword_shouldReturnOk() throws Exception {
        mockMvc.perform(put(BASE_URL + "/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginChangeRequest)))
                .andExpect(status().isOk());
        verify(facade).changePassword(any(LoginChangeRequest.class));
    }

    @Test
    void changePassword_shouldReturnUnauthorized_whenNoUserAuthenticated() throws Exception {
        doThrow(new UserAuthenticationException("No user authenticated"))
                .when(facade).changePassword(any(LoginChangeRequest.class));

        String content = mockMvc.perform(put(BASE_URL + "/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginChangeRequest)))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse errorResponse = mapper.readValue(content, ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ApiError.AUTHENTICATION_ERROR.getCode());
        assertThat(errorResponse.getErrorMessage()).isEqualTo("Authentication fails: No user authenticated");
        verify(facade).changePassword(any(LoginChangeRequest.class));
    }

    @Test
    void changePassword_shouldReturnForbidden_whenUserNotAuthorized() throws Exception {
        doThrow(new UserAuthorizationException("Authenticated user with username: other does not match with requested user with username: " + USERNAME))
                .when(facade).changePassword(any(LoginChangeRequest.class));

        String content = mockMvc.perform(put(BASE_URL + "/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginChangeRequest)))
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse errorResponse = mapper.readValue(content, ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ApiError.AUTHORIZATION_ERROR.getCode());
        assertThat(errorResponse.getErrorMessage()).isEqualTo("User is not authorized for request operation: Authenticated user with username: other does not match with requested user with username: " + USERNAME);
        verify(facade).changePassword(any(LoginChangeRequest.class));
    }

    @Test
    void changePassword_shouldReturnNotFound_whenUserNotFound() throws Exception {
        doThrow(new EntityNotFoundException("User not found")).when(facade).changePassword(any(LoginChangeRequest.class));

        String content = mockMvc.perform(put(BASE_URL + "/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginChangeRequest)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse errorResponse = mapper.readValue(content, ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ApiError.NOT_FOUND_ERROR.getCode());
        assertThat(errorResponse.getErrorMessage()).isEqualTo("Requested data was not found: User not found");
        verify(facade).changePassword(any(LoginChangeRequest.class));
    }

    @Test
    void logout_shouldReturnOk_whenLogoutIsSuccessful() throws Exception {
        mockMvc.perform(post(BASE_URL + "/logout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void logout_shouldReturnClientError_whenLogoutFails() throws Exception {
        doThrow(new UserAuthenticationException("Missing or malformed Authorization header")).when(facade).logout(any(HttpServletRequest.class));

        mockMvc.perform(post(BASE_URL + "/logout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    private LoginChangeRequest buildLoginChangeRequest() {
        return new LoginChangeRequest(USERNAME, PASSWORD, NEW_PASSWORD);
    }
}
