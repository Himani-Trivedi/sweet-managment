package com.api.mithai.controller;

import com.api.mithai.base.constants.Constants;
import com.api.mithai.auth.controller.AuthController;
import com.api.mithai.auth.dto.RegisterAuthRequestDto;
import com.api.mithai.auth.service.AuthService;
import com.api.mithai.base.constants.Urls;
import com.api.mithai.base.exception.GlobalExceptionHandler;
import com.api.mithai.base.exception.ResponseStatusException;
import com.api.mithai.base.response.BaseResponse;
import com.api.mithai.base.response.ResponseHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Register Endpoint Tests")
@SuppressWarnings("rawtypes")
public class RegisterEndpointTest {

    @Mock
    private AuthService authService;

    @Mock
    private ResponseHandler responseHandler;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private RegisterAuthRequestDto registerRequest;
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(globalExceptionHandler)
                .build();
        objectMapper = new ObjectMapper();
        registerRequest = new RegisterAuthRequestDto();
        registerRequest.setEmailId("user@example.com");
        registerRequest.setPassword("SecureP@1");
    }

    @Nested
    @DisplayName("Register Endpoint Success Test")
    class RegisterEndpointSuccessTest {

        @Test
        @DisplayName("Should register user successfully and return 201 CREATED with message 'User Created successfully'")
        void shouldRegisterUserSuccessfullyAndReturn201Created() throws Exception {
            // Given
            String requestBody = objectMapper.writeValueAsString(registerRequest);
            String expectedMessage = Constants.USER_CREATED_SUCCESSFULLY;
            BaseResponse expectedResponse = new BaseResponse(true, expectedMessage);
            ResponseEntity<BaseResponse> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.CREATED);

            doNothing().when(authService).register(any(RegisterAuthRequestDto.class));
            when(responseHandler.okResponse(eq(HttpStatus.CREATED), eq(expectedMessage)))
                    .thenReturn(responseEntity);

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.AUTH_URL + Urls.REGISTER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value(expectedMessage))
                    .andExpect(jsonPath("$.data").doesNotExist());

            // Verify service was called with correct DTO
            verify(authService, times(1)).register(argThat(dto ->
                    dto.getEmailId().equals(registerRequest.getEmailId()) &&
                    dto.getPassword().equals(registerRequest.getPassword())
            ));

            // Verify ResponseHandler was called with correct parameters
            verify(responseHandler, times(1)).okResponse(eq(HttpStatus.CREATED), eq(expectedMessage));
        }
        }

    @Nested
    @DisplayName("Register Endpoint Error Tests")
    class RegisterEndpointErrorTests {

        @Test
        @DisplayName("Should return 400 BAD REQUEST when email already exists")
        void shouldReturn400BadRequestWhenEmailAlreadyExists() throws Exception {
            // Given
            registerRequest.setEmailId("existing@example.com");
            registerRequest.setPassword("SecureP@1");
            String requestBody = objectMapper.writeValueAsString(registerRequest);
            String errorMessage = Constants.USER_EMAIL_ALREADY_EXISTS;

            doThrow(new ResponseStatusException(errorMessage, HttpStatus.BAD_REQUEST))
                    .when(authService).register(any(RegisterAuthRequestDto.class));

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.AUTH_URL + Urls.REGISTER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false))
                    .andExpect(jsonPath("$.message").value(errorMessage));

            // Verify service was called
            verify(authService, times(1)).register(argThat(dto ->
                    "existing@example.com".equals(dto.getEmailId()) &&
                    "SecureP@1".equals(dto.getPassword())
            ));
            // Verify ResponseHandler was never called
            verify(responseHandler, never()).okResponse(any(HttpStatus.class), anyString());
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when password is weak")
        void shouldReturn400BadRequestWhenPasswordIsWeak() throws Exception {
            // Given
            registerRequest.setEmailId("user@example.com");
            registerRequest.setPassword("weak"); // Weak password - too short, no uppercase, no special char
            String requestBody = objectMapper.writeValueAsString(registerRequest);
            String errorMessage = Constants.INVALID_EMAIL_OR_PASSWORD;

            doThrow(new ResponseStatusException(errorMessage, HttpStatus.BAD_REQUEST))
                    .when(authService).register(any(RegisterAuthRequestDto.class));

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.AUTH_URL + Urls.REGISTER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false));

            // Verify service was called with weak password
            verify(authService, times(1)).register(argThat(dto ->
                    "user@example.com".equals(dto.getEmailId()) &&
                    "weak".equals(dto.getPassword())
            ));
            // Verify ResponseHandler was never called
            verify(responseHandler, never()).okResponse(any(HttpStatus.class), anyString());
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when email is invalid")
        void shouldReturn400BadRequestWhenEmailIsInvalid() throws Exception {
            // Given
            registerRequest.setEmailId("invalid-email"); // Invalid email format
            registerRequest.setPassword("SecureP@1");
            String requestBody = objectMapper.writeValueAsString(registerRequest);
            String errorMessage = Constants.INVALID_EMAIL_OR_PASSWORD;

            doThrow(new ResponseStatusException(errorMessage, HttpStatus.BAD_REQUEST))
                    .when(authService).register(any(RegisterAuthRequestDto.class));

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.AUTH_URL + Urls.REGISTER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false));

            // Verify service was called with invalid email
            verify(authService, times(1)).register(argThat(dto ->
                    "invalid-email".equals(dto.getEmailId()) &&
                    "SecureP@1".equals(dto.getPassword())
            ));
            // Verify ResponseHandler was never called
            verify(responseHandler, never()).okResponse(any(HttpStatus.class), anyString());
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when email is empty - @NotBlank validation")
        void shouldReturn400BadRequestWhenEmailIsEmpty() throws Exception {
            // Given
            registerRequest.setEmailId(""); // Empty email - @NotBlank validation should catch this
            registerRequest.setPassword("SecureP@1");
            String requestBody = objectMapper.writeValueAsString(registerRequest);

            // When & Then - @NotBlank validation happens before service is called
            mockMvc.perform(post(Urls.BASE_URL + Urls.AUTH_URL + Urls.REGISTER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false))
                    .andExpect(jsonPath("$.message").exists());

            // Verify service was NEVER called because validation fails before reaching controller method
            verify(authService, never()).register(any(RegisterAuthRequestDto.class));
            // Verify ResponseHandler was never called
            verify(responseHandler, never()).okResponse(any(HttpStatus.class), anyString());
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when password is empty - @NotBlank validation")
        void shouldReturn400BadRequestWhenPasswordIsEmpty() throws Exception {
            // Given
            registerRequest.setEmailId("user@example.com");
            registerRequest.setPassword(""); // Empty password - @NotBlank validation should catch this
            String requestBody = objectMapper.writeValueAsString(registerRequest);

            // When & Then - @NotBlank validation happens before service is called
            mockMvc.perform(post(Urls.BASE_URL + Urls.AUTH_URL + Urls.REGISTER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false))
                    .andExpect(jsonPath("$.message").exists());

            // Verify service was NEVER called because validation fails before reaching controller method
            verify(authService, never()).register(any(RegisterAuthRequestDto.class));
            // Verify ResponseHandler was never called
            verify(responseHandler, never()).okResponse(any(HttpStatus.class), anyString());
        }
    }
}

