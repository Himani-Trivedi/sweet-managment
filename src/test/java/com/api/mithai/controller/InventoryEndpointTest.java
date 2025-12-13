package com.api.mithai.controller;

import com.api.mithai.base.constants.Constants;
import com.api.mithai.base.constants.Urls;
import com.api.mithai.base.exception.GlobalExceptionHandler;
import com.api.mithai.base.exception.ResponseStatusException;
import com.api.mithai.base.response.BaseResponse;
import com.api.mithai.base.response.ResponseHandler;
import com.api.mithai.sweet.controller.InventoryController;
import com.api.mithai.sweet.dto.PurchaseDto;
import com.api.mithai.sweet.dto.RestockDto;
import com.api.mithai.sweet.dto.SweetResponseDto;
import com.api.mithai.sweet.service.InventoryService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Inventory Controller Endpoint Tests")
@SuppressWarnings("rawtypes")
public class InventoryEndpointTest {

    @Mock
    private InventoryService inventoryService;

    @Mock
    private ResponseHandler responseHandler;

    @InjectMocks
    private InventoryController inventoryController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
        mockMvc = MockMvcBuilders.standaloneSetup(inventoryController)
                .setControllerAdvice(globalExceptionHandler)
                .build();
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("POST /api/v1/sweets/:id/purchase - Purchase Sweet Tests")
    class PurchaseSweetEndpointTests {

        private PurchaseDto purchaseDto;

        @BeforeEach
        void setUpPurchase() {
            purchaseDto = new PurchaseDto();
            purchaseDto.setPurchaseQuantity(10);
        }

        @Test
        @DisplayName("Should purchase sweet successfully and return 200 OK")
        void shouldPurchaseSweetSuccessfullyAndReturn200Ok() throws Exception {
            // Given
            String requestBody = objectMapper.writeValueAsString(purchaseDto);
            SweetResponseDto purchasedResponse = new SweetResponseDto(1L, "Gulab Jamun", 1L, "Milk Sweets", 150.0, 40);
            BaseResponse expectedResponse = new BaseResponse(purchasedResponse, true, Constants.SWEET_PURCHASED_SUCCESSFULLY);
            ResponseEntity<BaseResponse> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

            when(inventoryService.purchase(eq(1L), any(PurchaseDto.class))).thenReturn(purchasedResponse);
            when(responseHandler.okResponse(eq(purchasedResponse), eq(HttpStatus.OK), eq(Constants.SWEET_PURCHASED_SUCCESSFULLY)))
                    .thenReturn(responseEntity);

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.SWEETS_URL + "/1" + Urls.PURCHASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value(Constants.SWEET_PURCHASED_SUCCESSFULLY))
                    .andExpect(jsonPath("$.data.id").value(1L))
                    .andExpect(jsonPath("$.data.name").value("Gulab Jamun"))
                    .andExpect(jsonPath("$.data.quantity").value(40));

            verify(inventoryService, times(1)).purchase(eq(1L), any(PurchaseDto.class));
            verify(responseHandler, times(1)).okResponse(eq(purchasedResponse), eq(HttpStatus.OK), eq(Constants.SWEET_PURCHASED_SUCCESSFULLY));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when purchase quantity is null")
        void shouldReturn400BadRequestWhenPurchaseQuantityIsNull() throws Exception {
            // Given
            purchaseDto.setPurchaseQuantity(null);
            String requestBody = objectMapper.writeValueAsString(purchaseDto);

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.SWEETS_URL + "/1" + Urls.PURCHASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false));

            verify(inventoryService, never()).purchase(anyLong(), any(PurchaseDto.class));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when purchase quantity is zero")
        void shouldReturn400BadRequestWhenPurchaseQuantityIsZero() throws Exception {
            // Given
            purchaseDto.setPurchaseQuantity(0);
            String requestBody = objectMapper.writeValueAsString(purchaseDto);

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.SWEETS_URL + "/1" + Urls.PURCHASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false));

            verify(inventoryService, never()).purchase(anyLong(), any(PurchaseDto.class));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when purchase quantity is negative")
        void shouldReturn400BadRequestWhenPurchaseQuantityIsNegative() throws Exception {
            // Given
            purchaseDto.setPurchaseQuantity(-5);
            String requestBody = objectMapper.writeValueAsString(purchaseDto);

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.SWEETS_URL + "/1" + Urls.PURCHASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false));

            verify(inventoryService, never()).purchase(anyLong(), any(PurchaseDto.class));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when sweet not found")
        void shouldReturn400BadRequestWhenSweetNotFound() throws Exception {
            // Given
            String requestBody = objectMapper.writeValueAsString(purchaseDto);
            when(inventoryService.purchase(eq(999L), any(PurchaseDto.class)))
                    .thenThrow(new ResponseStatusException("Sweet not found", HttpStatus.BAD_REQUEST));

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.SWEETS_URL + "/999" + Urls.PURCHASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false))
                    .andExpect(jsonPath("$.message").value("Sweet not found"));

            verify(inventoryService, times(1)).purchase(eq(999L), any(PurchaseDto.class));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when purchase quantity exceeds available quantity")
        void shouldReturn400BadRequestWhenPurchaseQuantityExceedsAvailableQuantity() throws Exception {
            // Given
            purchaseDto.setPurchaseQuantity(100);
            String requestBody = objectMapper.writeValueAsString(purchaseDto);
            when(inventoryService.purchase(eq(1L), any(PurchaseDto.class)))
                    .thenThrow(new ResponseStatusException("Purchase quantity cannot exceed available quantity", HttpStatus.BAD_REQUEST));

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.SWEETS_URL + "/1" + Urls.PURCHASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false))
                    .andExpect(jsonPath("$.message").value("Purchase quantity cannot exceed available quantity"));

            verify(inventoryService, times(1)).purchase(eq(1L), any(PurchaseDto.class));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when invalid ID format")
        void shouldReturn400BadRequestWhenInvalidIdFormat() throws Exception {
            // Given
            String requestBody = objectMapper.writeValueAsString(purchaseDto);

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.SWEETS_URL + "/invalid" + Urls.PURCHASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());

            verify(inventoryService, never()).purchase(anyLong(), any(PurchaseDto.class));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/sweets/:id/restock - Restock Sweet Tests")
    class RestockSweetEndpointTests {

        private RestockDto restockDto;

        @BeforeEach
        void setUpRestock() {
            restockDto = new RestockDto();
            restockDto.setQuantity(20);
        }

        @Test
        @DisplayName("Should restock sweet successfully and return 200 OK")
        void shouldRestockSweetSuccessfullyAndReturn200Ok() throws Exception {
            // Given
            String requestBody = objectMapper.writeValueAsString(restockDto);
            SweetResponseDto restockedResponse = new SweetResponseDto(1L, "Gulab Jamun", 1L, "Milk Sweets", 150.0, 70);
            BaseResponse expectedResponse = new BaseResponse(restockedResponse, true, Constants.SWEET_RESTOCKED_SUCCESSFULLY);
            ResponseEntity<BaseResponse> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

            when(inventoryService.restock(eq(1L), any(RestockDto.class))).thenReturn(restockedResponse);
            when(responseHandler.okResponse(eq(restockedResponse), eq(HttpStatus.OK), eq(Constants.SWEET_RESTOCKED_SUCCESSFULLY)))
                    .thenReturn(responseEntity);

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.SWEETS_URL + "/1" + Urls.RESTOCK_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value(Constants.SWEET_RESTOCKED_SUCCESSFULLY))
                    .andExpect(jsonPath("$.data.id").value(1L))
                    .andExpect(jsonPath("$.data.name").value("Gulab Jamun"))
                    .andExpect(jsonPath("$.data.quantity").value(70));

            verify(inventoryService, times(1)).restock(eq(1L), any(RestockDto.class));
            verify(responseHandler, times(1)).okResponse(eq(restockedResponse), eq(HttpStatus.OK), eq(Constants.SWEET_RESTOCKED_SUCCESSFULLY));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when quantity is null")
        void shouldReturn400BadRequestWhenQuantityIsNull() throws Exception {
            // Given
            restockDto.setQuantity(null);
            String requestBody = objectMapper.writeValueAsString(restockDto);

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.SWEETS_URL + "/1" + Urls.RESTOCK_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false));

            verify(inventoryService, never()).restock(anyLong(), any(RestockDto.class));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when quantity is zero")
        void shouldReturn400BadRequestWhenQuantityIsZero() throws Exception {
            // Given
            restockDto.setQuantity(0);
            String requestBody = objectMapper.writeValueAsString(restockDto);

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.SWEETS_URL + "/1" + Urls.RESTOCK_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false));

            verify(inventoryService, never()).restock(anyLong(), any(RestockDto.class));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when quantity is negative")
        void shouldReturn400BadRequestWhenQuantityIsNegative() throws Exception {
            // Given
            restockDto.setQuantity(-10);
            String requestBody = objectMapper.writeValueAsString(restockDto);

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.SWEETS_URL + "/1" + Urls.RESTOCK_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false));

            verify(inventoryService, never()).restock(anyLong(), any(RestockDto.class));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when sweet not found")
        void shouldReturn400BadRequestWhenSweetNotFound() throws Exception {
            // Given
            String requestBody = objectMapper.writeValueAsString(restockDto);
            when(inventoryService.restock(eq(999L), any(RestockDto.class)))
                    .thenThrow(new ResponseStatusException("Sweet not found", HttpStatus.BAD_REQUEST));

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.SWEETS_URL + "/999" + Urls.RESTOCK_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false))
                    .andExpect(jsonPath("$.message").value("Sweet not found"));

            verify(inventoryService, times(1)).restock(eq(999L), any(RestockDto.class));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when invalid ID format")
        void shouldReturn400BadRequestWhenInvalidIdFormat() throws Exception {
            // Given
            String requestBody = objectMapper.writeValueAsString(restockDto);

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.SWEETS_URL + "/invalid" + Urls.RESTOCK_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());

            verify(inventoryService, never()).restock(anyLong(), any(RestockDto.class));
        }
    }
}

