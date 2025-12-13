package com.api.mithai.controller;

import com.api.mithai.auth.controller.AuthController;
import com.api.mithai.auth.dto.RegisterAuthRequestDto;
import com.api.mithai.auth.service.AuthService;
import com.api.mithai.base.constants.Urls;
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
@DisplayName("AuthController Register Endpoint Tests")
@SuppressWarnings("unchecked")
public class AuthControllerRegisterTest {

    @Mock
    private AuthService authService;

    @Mock
    private ResponseHandler responseHandler;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private RegisterAuthRequestDto registerRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
        registerRequest = new RegisterAuthRequestDto();
        registerRequest.setEmailId("user@example.com");
        registerRequest.setPassword("SecureP@1");
    }

    @Nested
    @DisplayName("Register Endpoint Success Tests")
    class RegisterSuccessTests {

        @Test
        @DisplayName("Should register user successfully and return 201 CREATED with success message")
        void shouldRegisterUserSuccessfullyAndReturn201Created() throws Exception {
            // Given
            String requestBody = objectMapper.writeValueAsString(registerRequest);
            String expectedMessage = "User Created successfully";
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

            verify(authService, times(1)).register(any(RegisterAuthRequestDto.class));
            verify(responseHandler, times(1)).okResponse(eq(HttpStatus.CREATED), eq(expectedMessage));
        }

        @Test
        @DisplayName("Should pass RegisterAuthRequestDto to service layer")
        void shouldPassRegisterAuthRequestDtoToServiceLayer() throws Exception {
            // Given
            String requestBody = objectMapper.writeValueAsString(registerRequest);
            String expectedMessage = "User Created successfully";
            BaseResponse expectedResponse = new BaseResponse(true, expectedMessage);
            ResponseEntity<BaseResponse> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.CREATED);

            doNothing().when(authService).register(any(RegisterAuthRequestDto.class));
            when(responseHandler.okResponse(eq(HttpStatus.CREATED), eq(expectedMessage)))
                    .thenReturn(responseEntity);

            // When
            mockMvc.perform(post(Urls.BASE_URL + Urls.AUTH_URL + Urls.REGISTER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isCreated());

            // Then
            verify(authService, times(1)).register(argThat(dto ->
                    dto.getEmailId().equals(registerRequest.getEmailId()) &&
                    dto.getPassword().equals(registerRequest.getPassword())
            ));
        }

        @Test
        @DisplayName("Should use ResponseHandler to create response")
        void shouldUseResponseHandlerToCreateResponse() throws Exception {
            // Given
            String requestBody = objectMapper.writeValueAsString(registerRequest);
            String expectedMessage = "User Created successfully";
            BaseResponse expectedResponse = new BaseResponse(true, expectedMessage);
            ResponseEntity<BaseResponse> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.CREATED);

            doNothing().when(authService).register(any(RegisterAuthRequestDto.class));
            when(responseHandler.okResponse(eq(HttpStatus.CREATED), eq(expectedMessage)))
                    .thenReturn(responseEntity);

            // When
            mockMvc.perform(post(Urls.BASE_URL + Urls.AUTH_URL + Urls.REGISTER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isCreated());

            // Then
            verify(responseHandler, times(1)).okResponse(eq(HttpStatus.CREATED), eq(expectedMessage));
            verify(responseHandler, never()).okResponse(any(), any(), any());
        }

        @Test
        @DisplayName("Should return BaseResponse format in response")
        void shouldReturnBaseResponseFormatInResponse() throws Exception {
            // Given
            String requestBody = objectMapper.writeValueAsString(registerRequest);
            String expectedMessage = "User Created successfully";
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
                    .andExpect(jsonPath("$.status").exists())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value(expectedMessage));
        }
    }

    @Nested
    @DisplayName("Register Endpoint Request Validation Tests")
    class RegisterRequestValidationTests {

        @Test
        @DisplayName("Should accept valid emailId and password in request body")
        void shouldAcceptValidEmailIdAndPasswordInRequestBody() throws Exception {
            // Given
            registerRequest.setEmailId("newuser@example.com");
            registerRequest.setPassword("SecureP@1");
            String requestBody = objectMapper.writeValueAsString(registerRequest);
            String expectedMessage = "User Created successfully";
            BaseResponse expectedResponse = new BaseResponse(true, expectedMessage);
            ResponseEntity<BaseResponse> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.CREATED);

            doNothing().when(authService).register(any(RegisterAuthRequestDto.class));
            when(responseHandler.okResponse(eq(HttpStatus.CREATED), eq(expectedMessage)))
                    .thenReturn(responseEntity);

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.AUTH_URL + Urls.REGISTER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isCreated());

            verify(authService, times(1)).register(argThat(dto ->
                    "newuser@example.com".equals(dto.getEmailId()) &&
                    "SecureP@1".equals(dto.getPassword())
            ));
        }

        @Test
        @DisplayName("Should handle request with different email formats")
        void shouldHandleRequestWithDifferentEmailFormats() throws Exception {
            // Given
            registerRequest.setEmailId("test.user+tag@example.co.uk");
            registerRequest.setPassword("SecureP@1");
            String requestBody = objectMapper.writeValueAsString(registerRequest);
            String expectedMessage = "User Created successfully";
            BaseResponse expectedResponse = new BaseResponse(true, expectedMessage);
            ResponseEntity<BaseResponse> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.CREATED);

            doNothing().when(authService).register(any(RegisterAuthRequestDto.class));
            when(responseHandler.okResponse(eq(HttpStatus.CREATED), eq(expectedMessage)))
                    .thenReturn(responseEntity);

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.AUTH_URL + Urls.REGISTER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isCreated());

            verify(authService, times(1)).register(any(RegisterAuthRequestDto.class));
        }
    }

    @Nested
    @DisplayName("Register Endpoint Integration Tests")
    class RegisterIntegrationTests {

        @Test
        @DisplayName("Should complete full flow: receive request -> call service -> return response")
        void shouldCompleteFullFlow() throws Exception {
            // Given
            String requestBody = objectMapper.writeValueAsString(registerRequest);
            String expectedMessage = "User Created successfully";
            BaseResponse expectedResponse = new BaseResponse(true, expectedMessage);
            ResponseEntity<BaseResponse> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.CREATED);

            doNothing().when(authService).register(any(RegisterAuthRequestDto.class));
            when(responseHandler.okResponse(eq(HttpStatus.CREATED), eq(expectedMessage)))
                    .thenReturn(responseEntity);

            // When
            mockMvc.perform(post(Urls.BASE_URL + Urls.AUTH_URL + Urls.REGISTER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value(expectedMessage));

            // Then - Verify complete flow
            verify(authService, times(1)).register(any(RegisterAuthRequestDto.class));
            verify(responseHandler, times(1)).okResponse(eq(HttpStatus.CREATED), eq(expectedMessage));
            verifyNoMoreInteractions(authService, responseHandler);
        }
    }
}

