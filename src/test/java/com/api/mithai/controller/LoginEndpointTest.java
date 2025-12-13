package com.api.mithai.controller;

import com.api.mithai.auth.controller.AuthController;
import com.api.mithai.auth.dto.LoginRequestDto;
import com.api.mithai.auth.dto.LoginResponseDto;
import com.api.mithai.auth.enums.Role;
import com.api.mithai.auth.service.AuthService;
import com.api.mithai.base.constants.Constants;
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
@DisplayName("Login Endpoint Tests - TDD Red Phase")
@SuppressWarnings("rawtypes")
public class LoginEndpointTest {

    @Mock
    private AuthService authService;

    @Mock
    private ResponseHandler responseHandler;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private LoginRequestDto loginRequest;
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(globalExceptionHandler)
                .build();
        objectMapper = new ObjectMapper();
        loginRequest = new LoginRequestDto();
        loginRequest.setEmail("user@example.com");
        loginRequest.setPassword("SecureP@1");
    }

    @Nested
    @DisplayName("Login Endpoint Success Test")
    class LoginEndpointSuccessTest {

        @Test
        @DisplayName("Should login successfully and return 200 OK with LoginResponseDto containing email, accessToken, and role")
        void shouldLoginSuccessfullyAndReturn200OkWithLoginResponseDto() throws Exception {
            // Given
            String requestBody = objectMapper.writeValueAsString(loginRequest);
            LoginResponseDto loginResponseDto = new LoginResponseDto(
                    "user@example.com",
                    "mock-access-token-12345",
                    Role.USER
            );
            BaseResponse expectedResponse = new BaseResponse(loginResponseDto, true, Constants.LOGIN_SUCCESSFUL);
            ResponseEntity<BaseResponse> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

            when(authService.login(any(LoginRequestDto.class))).thenReturn(loginResponseDto);
            when(responseHandler.okResponse(eq(loginResponseDto), eq(HttpStatus.OK), eq(Constants.LOGIN_SUCCESSFUL)))
                    .thenReturn(responseEntity);

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.AUTH_URL + Urls.LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value(Constants.LOGIN_SUCCESSFUL))
                    .andExpect(jsonPath("$.data.email").value("user@example.com"))
                    .andExpect(jsonPath("$.data.accessToken").value("mock-access-token-12345"))
                    .andExpect(jsonPath("$.data.role").value("USER"));

            // Verify service was called with correct DTO
            verify(authService, times(1)).login(argThat(dto ->
                    dto.getEmail().equals(loginRequest.getEmail()) &&
                    dto.getPassword().equals(loginRequest.getPassword())
            ));

            // Verify ResponseHandler was called with correct parameters
            verify(responseHandler, times(1)).okResponse(eq(loginResponseDto), eq(HttpStatus.OK), eq("Login successful"));
        }
    }

    @Nested
    @DisplayName("Login Endpoint Email Validation Tests")
    class LoginEndpointEmailValidationTests {

        @Test
        @DisplayName("Should return 400 BAD REQUEST when email is empty - @NotBlank validation")
        void shouldReturn400BadRequestWhenEmailIsEmpty() throws Exception {
            // Given
            loginRequest.setEmail(""); // Empty email - @NotBlank validation should catch this
            loginRequest.setPassword("SecureP@1");
            String requestBody = objectMapper.writeValueAsString(loginRequest);

            // When & Then - @NotBlank validation happens before service is called
            mockMvc.perform(post(Urls.BASE_URL + Urls.AUTH_URL + Urls.LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false))
                    .andExpect(jsonPath("$.message").exists());

            // Verify service was NEVER called because validation fails before reaching controller method
            verify(authService, never()).login(any(LoginRequestDto.class));
            // Verify ResponseHandler was never called
            verify(responseHandler, never()).okResponse(any(), any(HttpStatus.class), anyString());
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when email is null - @NotBlank validation")
        void shouldReturn400BadRequestWhenEmailIsNull() throws Exception {
            // Given
            loginRequest.setEmail(null); // Null email - @NotBlank validation should catch this
            loginRequest.setPassword("SecureP@1");
            String requestBody = objectMapper.writeValueAsString(loginRequest);

            // When & Then - @NotBlank validation happens before service is called
            mockMvc.perform(post(Urls.BASE_URL + Urls.AUTH_URL + Urls.LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false))
                    .andExpect(jsonPath("$.message").exists());

            // Verify service was NEVER called because validation fails before reaching controller method
            verify(authService, never()).login(any(LoginRequestDto.class));
            verify(responseHandler, never()).okResponse(any(), any(HttpStatus.class), anyString());
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when email format is invalid")
        void shouldReturn400BadRequestWhenEmailFormatIsInvalid() throws Exception {
            // Given
            loginRequest.setEmail("invalid-email"); // Invalid email format
            loginRequest.setPassword("SecureP@1");
            String requestBody = objectMapper.writeValueAsString(loginRequest);
            String errorMessage = Constants.INVALID_EMAIL_OR_PASSWORD;

            doThrow(new ResponseStatusException(errorMessage, HttpStatus.BAD_REQUEST))
                    .when(authService).login(any(LoginRequestDto.class));

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.AUTH_URL + Urls.LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false))
                    .andExpect(jsonPath("$.message").value(errorMessage));

            // Verify service was called with invalid email
            verify(authService, times(1)).login(argThat(dto ->
                    "invalid-email".equals(dto.getEmail()) &&
                    "SecureP@1".equals(dto.getPassword())
            ));
            // Verify ResponseHandler was never called
            verify(responseHandler, never()).okResponse(any(), any(HttpStatus.class), anyString());
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when email is blank (whitespace only) - @NotBlank validation")
        void shouldReturn400BadRequestWhenEmailIsBlank() throws Exception {
            // Given
            loginRequest.setEmail("   "); // Blank email - whitespace only - @NotBlank validation should catch this
            loginRequest.setPassword("SecureP@1");
            String requestBody = objectMapper.writeValueAsString(loginRequest);

            // When & Then - @NotBlank validation happens before service is called
            mockMvc.perform(post(Urls.BASE_URL + Urls.AUTH_URL + Urls.LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false))
                    .andExpect(jsonPath("$.message").exists());

            // Verify service was NEVER called because validation fails before reaching controller method
            verify(authService, never()).login(any(LoginRequestDto.class));
            verify(responseHandler, never()).okResponse(any(), any(HttpStatus.class), anyString());
        }
    }

    @Nested
    @DisplayName("Login Endpoint Password Validation Tests")
    class LoginEndpointPasswordValidationTests {

        @Test
        @DisplayName("Should return 400 BAD REQUEST when password is empty - @NotBlank validation")
        void shouldReturn400BadRequestWhenPasswordIsEmpty() throws Exception {
            // Given
            loginRequest.setEmail("user@example.com");
            loginRequest.setPassword(""); // Empty password - @NotBlank validation should catch this
            String requestBody = objectMapper.writeValueAsString(loginRequest);

            // When & Then - @NotBlank validation happens before service is called
            mockMvc.perform(post(Urls.BASE_URL + Urls.AUTH_URL + Urls.LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false))
                    .andExpect(jsonPath("$.message").exists());

            // Verify service was NEVER called because validation fails before reaching controller method
            verify(authService, never()).login(any(LoginRequestDto.class));
            // Verify ResponseHandler was never called
            verify(responseHandler, never()).okResponse(any(), any(HttpStatus.class), anyString());
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when password is null - @NotBlank validation")
        void shouldReturn400BadRequestWhenPasswordIsNull() throws Exception {
            // Given
            loginRequest.setEmail("user@example.com");
            loginRequest.setPassword(null); // Null password - @NotBlank validation should catch this
            String requestBody = objectMapper.writeValueAsString(loginRequest);

            // When & Then - @NotBlank validation happens before service is called
            mockMvc.perform(post(Urls.BASE_URL + Urls.AUTH_URL + Urls.LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false))
                    .andExpect(jsonPath("$.message").exists());

            // Verify service was NEVER called because validation fails before reaching controller method
            verify(authService, never()).login(any(LoginRequestDto.class));
            verify(responseHandler, never()).okResponse(any(), any(HttpStatus.class), anyString());
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when password is weak (too short)")
        void shouldReturn400BadRequestWhenPasswordIsWeak() throws Exception {
            // Given
            loginRequest.setEmail("user@example.com");
            loginRequest.setPassword("weak"); // Weak password - too short, no uppercase, no special char
            String requestBody = objectMapper.writeValueAsString(loginRequest);
            String errorMessage = Constants.INVALID_EMAIL_OR_PASSWORD;

            doThrow(new ResponseStatusException(errorMessage, HttpStatus.BAD_REQUEST))
                    .when(authService).login(any(LoginRequestDto.class));

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.AUTH_URL + Urls.LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false))
                    .andExpect(jsonPath("$.message").value(errorMessage));

            // Verify service was called with weak password
            verify(authService, times(1)).login(argThat(dto ->
                    "user@example.com".equals(dto.getEmail()) &&
                    "weak".equals(dto.getPassword())
            ));
            // Verify ResponseHandler was never called
            verify(responseHandler, never()).okResponse(any(), any(HttpStatus.class), anyString());
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when password lacks uppercase letter")
        void shouldReturn400BadRequestWhenPasswordLacksUppercase() throws Exception {
            // Given
            loginRequest.setEmail("user@example.com");
            loginRequest.setPassword("securep@1"); // Password without uppercase
            String requestBody = objectMapper.writeValueAsString(loginRequest);
            String errorMessage = Constants.INVALID_EMAIL_OR_PASSWORD;

            doThrow(new ResponseStatusException(errorMessage, HttpStatus.BAD_REQUEST))
                    .when(authService).login(any(LoginRequestDto.class));

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.AUTH_URL + Urls.LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false))
                    .andExpect(jsonPath("$.message").value(errorMessage));

            verify(authService, times(1)).login(any(LoginRequestDto.class));
            verify(responseHandler, never()).okResponse(any(), any(HttpStatus.class), anyString());
        }
    }

    @Nested
    @DisplayName("Login Endpoint Authentication Error Tests")
    class LoginEndpointAuthenticationErrorTests {

        @Test
        @DisplayName("Should return 400 BAD REQUEST when user not found")
        void shouldReturn400BadRequestWhenUserNotFound() throws Exception {
            // Given
            String requestBody = objectMapper.writeValueAsString(loginRequest);
            String errorMessage = Constants.INVALID_EMAIL_OR_PASSWORD;

            doThrow(new ResponseStatusException(errorMessage, HttpStatus.BAD_REQUEST))
                    .when(authService).login(any(LoginRequestDto.class));

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.AUTH_URL + Urls.LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false))
                    .andExpect(jsonPath("$.message").value(errorMessage));

            // Verify service was called
            verify(authService, times(1)).login(any(LoginRequestDto.class));
            // Verify ResponseHandler was never called
            verify(responseHandler, never()).okResponse(any(), any(HttpStatus.class), anyString());
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when password does not match")
        void shouldReturn400BadRequestWhenPasswordDoesNotMatch() throws Exception {
            // Given
            loginRequest.setPassword("WrongP@1"); // Wrong password
            String requestBody = objectMapper.writeValueAsString(loginRequest);
            String errorMessage = Constants.INVALID_EMAIL_OR_PASSWORD;

            doThrow(new ResponseStatusException(errorMessage, HttpStatus.BAD_REQUEST))
                    .when(authService).login(any(LoginRequestDto.class));

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.AUTH_URL + Urls.LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false))
                    .andExpect(jsonPath("$.message").value(errorMessage));

            // Verify service was called with wrong password
            verify(authService, times(1)).login(argThat(dto ->
                    "user@example.com".equals(dto.getEmail()) &&
                    "WrongP@1".equals(dto.getPassword())
            ));
            verify(responseHandler, never()).okResponse(any(), any(HttpStatus.class), anyString());
        }
    }

    @Nested
    @DisplayName("Login Endpoint Response DTO Tests")
    class LoginEndpointResponseDtoTests {

        @Test
        @DisplayName("Should return LoginResponseDto with correct email, accessToken, and role for USER")
        void shouldReturnLoginResponseDtoWithCorrectFieldsForUser() throws Exception {
            // Given
            String requestBody = objectMapper.writeValueAsString(loginRequest);
            LoginResponseDto loginResponseDto = new LoginResponseDto(
                    "user@example.com",
                    "user-access-token-12345",
                    Role.USER
            );
            BaseResponse expectedResponse = new BaseResponse(loginResponseDto, true, Constants.LOGIN_SUCCESSFUL);
            ResponseEntity<BaseResponse> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

            when(authService.login(any(LoginRequestDto.class))).thenReturn(loginResponseDto);
            when(responseHandler.okResponse(eq(loginResponseDto), eq(HttpStatus.OK), eq(Constants.LOGIN_SUCCESSFUL)))
                    .thenReturn(responseEntity);

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.AUTH_URL + Urls.LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.email").value("user@example.com"))
                    .andExpect(jsonPath("$.data.accessToken").value("user-access-token-12345"))
                    .andExpect(jsonPath("$.data.role").value("USER"));

            verify(authService, times(1)).login(any(LoginRequestDto.class));
            verify(responseHandler, times(1)).okResponse(eq(loginResponseDto), eq(HttpStatus.OK), eq("Login successful"));
        }

        @Test
        @DisplayName("Should return LoginResponseDto with correct email, accessToken, and role for ADMIN")
        void shouldReturnLoginResponseDtoWithCorrectFieldsForAdmin() throws Exception {
            // Given
            loginRequest.setEmail("admin@example.com");
            String requestBody = objectMapper.writeValueAsString(loginRequest);
            LoginResponseDto loginResponseDto = new LoginResponseDto(
                    "admin@example.com",
                    "admin-access-token-67890",
                    Role.ADMIN
            );
            BaseResponse expectedResponse = new BaseResponse(loginResponseDto, true, Constants.LOGIN_SUCCESSFUL);
            ResponseEntity<BaseResponse> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

            when(authService.login(any(LoginRequestDto.class))).thenReturn(loginResponseDto);
            when(responseHandler.okResponse(eq(loginResponseDto), eq(HttpStatus.OK), eq(Constants.LOGIN_SUCCESSFUL)))
                    .thenReturn(responseEntity);

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.AUTH_URL + Urls.LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.email").value("admin@example.com"))
                    .andExpect(jsonPath("$.data.accessToken").value("admin-access-token-67890"))
                    .andExpect(jsonPath("$.data.role").value("ADMIN"));

            verify(authService, times(1)).login(any(LoginRequestDto.class));
            verify(responseHandler, times(1)).okResponse(eq(loginResponseDto), eq(HttpStatus.OK), eq("Login successful"));
        }
    }
}

