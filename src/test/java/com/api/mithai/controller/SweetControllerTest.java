package com.api.mithai.controller;

import com.api.mithai.base.constants.Constants;
import com.api.mithai.base.constants.Urls;
import com.api.mithai.base.exception.GlobalExceptionHandler;
import com.api.mithai.base.exception.ResponseStatusException;
import com.api.mithai.base.response.BaseResponse;
import com.api.mithai.base.response.PaginatedBaseResponse;
import com.api.mithai.base.response.ResponseHandler;
import com.api.mithai.sweet.controller.SweetController;
import com.api.mithai.sweet.dto.SweetRequestDto;
import com.api.mithai.sweet.dto.SweetResponseDto;
import com.api.mithai.sweet.service.SweetService;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Sweet Controller Endpoint Tests")
@SuppressWarnings("rawtypes")
public class SweetControllerTest {

    @Mock
    private SweetService sweetService;

    @Mock
    private ResponseHandler responseHandler;

    @InjectMocks
    private SweetController sweetController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private SweetRequestDto sweetRequestDto;
    private SweetResponseDto sweetResponseDto;
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
        mockMvc = MockMvcBuilders.standaloneSetup(sweetController)
                .setControllerAdvice(globalExceptionHandler)
                .build();
        objectMapper = new ObjectMapper();

        sweetRequestDto = new SweetRequestDto();
        sweetRequestDto.setName("Gulab Jamun");
        sweetRequestDto.setCategoryId(1L);
        sweetRequestDto.setPrice(150.0);
        sweetRequestDto.setQuantity(50);

        sweetResponseDto = new SweetResponseDto();
        sweetResponseDto.setId(1L);
        sweetResponseDto.setName("Gulab Jamun");
        sweetResponseDto.setCategoryId(1L);
        sweetResponseDto.setCategoryName("Milk Sweets");
        sweetResponseDto.setPrice(150.0);
        sweetResponseDto.setQuantity(50);
    }

    @Nested
    @DisplayName("POST /api/v1/sweets - Create Sweet Tests")
    class CreateSweetEndpointTests {

        @Test
        @DisplayName("Should create sweet successfully and return 201 CREATED")
        void shouldCreateSweetSuccessfullyAndReturn201Created() throws Exception {
            // Given
            String requestBody = objectMapper.writeValueAsString(sweetRequestDto);
            BaseResponse expectedResponse = new BaseResponse(sweetResponseDto, true, Constants.SWEET_CREATED_SUCCESSFULLY);
            ResponseEntity<BaseResponse> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.CREATED);

            when(sweetService.create(any(SweetRequestDto.class))).thenReturn(sweetResponseDto);
            when(responseHandler.okResponse(eq(sweetResponseDto), eq(HttpStatus.CREATED), eq(Constants.SWEET_CREATED_SUCCESSFULLY)))
                    .thenReturn(responseEntity);

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.SWEETS_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value(Constants.SWEET_CREATED_SUCCESSFULLY))
                    .andExpect(jsonPath("$.data.id").value(1L))
                    .andExpect(jsonPath("$.data.name").value("Gulab Jamun"))
                    .andExpect(jsonPath("$.data.categoryId").value(1L))
                    .andExpect(jsonPath("$.data.categoryName").value("Milk Sweets"))
                    .andExpect(jsonPath("$.data.price").value(150.0))
                    .andExpect(jsonPath("$.data.quantity").value(50));

            verify(sweetService, times(1)).create(any(SweetRequestDto.class));
            verify(responseHandler, times(1)).okResponse(eq(sweetResponseDto), eq(HttpStatus.CREATED), eq(Constants.SWEET_CREATED_SUCCESSFULLY));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when name is blank")
        void shouldReturn400BadRequestWhenNameIsBlank() throws Exception {
            // Given
            sweetRequestDto.setName("");
            String requestBody = objectMapper.writeValueAsString(sweetRequestDto);

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.SWEETS_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false))
                    .andExpect(jsonPath("$.message").exists());

            verify(sweetService, never()).create(any(SweetRequestDto.class));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when name is null")
        void shouldReturn400BadRequestWhenNameIsNull() throws Exception {
            // Given
            sweetRequestDto.setName(null);
            String requestBody = objectMapper.writeValueAsString(sweetRequestDto);

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.SWEETS_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false));

            verify(sweetService, never()).create(any(SweetRequestDto.class));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when categoryId is null")
        void shouldReturn400BadRequestWhenCategoryIdIsNull() throws Exception {
            // Given
            sweetRequestDto.setCategoryId(null);
            String requestBody = objectMapper.writeValueAsString(sweetRequestDto);

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.SWEETS_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false));

            verify(sweetService, never()).create(any(SweetRequestDto.class));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when price is null")
        void shouldReturn400BadRequestWhenPriceIsNull() throws Exception {
            // Given
            sweetRequestDto.setPrice(null);
            String requestBody = objectMapper.writeValueAsString(sweetRequestDto);

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.SWEETS_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false));

            verify(sweetService, never()).create(any(SweetRequestDto.class));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when price is less than 1")
        void shouldReturn400BadRequestWhenPriceIsLessThanOne() throws Exception {
            // Given
            sweetRequestDto.setPrice(0.5);
            String requestBody = objectMapper.writeValueAsString(sweetRequestDto);

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.SWEETS_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false));

            verify(sweetService, never()).create(any(SweetRequestDto.class));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when quantity is null")
        void shouldReturn400BadRequestWhenQuantityIsNull() throws Exception {
            // Given
            sweetRequestDto.setQuantity(null);
            String requestBody = objectMapper.writeValueAsString(sweetRequestDto);

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.SWEETS_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false));

            verify(sweetService, never()).create(any(SweetRequestDto.class));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when quantity is negative")
        void shouldReturn400BadRequestWhenQuantityIsNegative() throws Exception {
            // Given
            sweetRequestDto.setQuantity(-1);
            String requestBody = objectMapper.writeValueAsString(sweetRequestDto);

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.SWEETS_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false));

            verify(sweetService, never()).create(any(SweetRequestDto.class));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when sweet name already exists")
        void shouldReturn400BadRequestWhenSweetNameAlreadyExists() throws Exception {
            // Given
            String requestBody = objectMapper.writeValueAsString(sweetRequestDto);
            when(sweetService.create(any(SweetRequestDto.class)))
                    .thenThrow(new ResponseStatusException(Constants.SWEET_NAME_ALREADY_EXISTS, HttpStatus.BAD_REQUEST));

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.SWEETS_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false))
                    .andExpect(jsonPath("$.message").value(Constants.SWEET_NAME_ALREADY_EXISTS));

            verify(sweetService, times(1)).create(any(SweetRequestDto.class));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when category not found")
        void shouldReturn400BadRequestWhenCategoryNotFound() throws Exception {
            // Given
            String requestBody = objectMapper.writeValueAsString(sweetRequestDto);
            when(sweetService.create(any(SweetRequestDto.class)))
                    .thenThrow(new ResponseStatusException("Category not found", HttpStatus.BAD_REQUEST));

            // When & Then
            mockMvc.perform(post(Urls.BASE_URL + Urls.SWEETS_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false))
                    .andExpect(jsonPath("$.message").value("Category not found"));

            verify(sweetService, times(1)).create(any(SweetRequestDto.class));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/sweets - List All Sweets Tests")
    class ListAllSweetsEndpointTests {

        @Test
        @DisplayName("Should return 200 OK with paginated list of sweets")
        void shouldReturn200OkWithPaginatedListOfSweets() throws Exception {
            // Given
            List<SweetResponseDto> sweets = new ArrayList<>();
            sweets.add(sweetResponseDto);
            PaginatedBaseResponse<SweetResponseDto> paginatedResponse = new PaginatedBaseResponse<>();
            paginatedResponse.setList(sweets);
            paginatedResponse.setTotalRecords(1L);
            paginatedResponse.setCurrentPage(0L);

            BaseResponse expectedResponse = new BaseResponse(paginatedResponse, true, Constants.SWEETS_RETRIEVED_SUCCESSFULLY);
            ResponseEntity<BaseResponse> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

            when(sweetService.listAll(any(Map.class))).thenReturn(paginatedResponse);
            when(responseHandler.okResponse(eq(paginatedResponse), eq(HttpStatus.OK), eq(Constants.SWEETS_RETRIEVED_SUCCESSFULLY)))
                    .thenReturn(responseEntity);

            // When & Then
            mockMvc.perform(get(Urls.BASE_URL + Urls.SWEETS_URL))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value(Constants.SWEETS_RETRIEVED_SUCCESSFULLY))
                    .andExpect(jsonPath("$.data.list").isArray())
                    .andExpect(jsonPath("$.data.list[0].id").value(1L))
                    .andExpect(jsonPath("$.data.list[0].name").value("Gulab Jamun"))
                    .andExpect(jsonPath("$.data.totalRecords").value(1L))
                    .andExpect(jsonPath("$.data.currentPage").value(0L));

            verify(sweetService, times(1)).listAll(any(Map.class));
        }

        @Test
        @DisplayName("Should return 200 OK with empty list when no sweets exist")
        void shouldReturn200OkWithEmptyListWhenNoSweetsExist() throws Exception {
            // Given
            PaginatedBaseResponse<SweetResponseDto> paginatedResponse = new PaginatedBaseResponse<>();
            paginatedResponse.setList(new ArrayList<>());
            paginatedResponse.setTotalRecords(0L);
            paginatedResponse.setCurrentPage(0L);

            BaseResponse expectedResponse = new BaseResponse(paginatedResponse, true, Constants.SWEETS_RETRIEVED_SUCCESSFULLY);
            ResponseEntity<BaseResponse> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

            when(sweetService.listAll(any(Map.class))).thenReturn(paginatedResponse);
            when(responseHandler.okResponse(eq(paginatedResponse), eq(HttpStatus.OK), eq(Constants.SWEETS_RETRIEVED_SUCCESSFULLY)))
                    .thenReturn(responseEntity);

            // When & Then
            mockMvc.perform(get(Urls.BASE_URL + Urls.SWEETS_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.list").isArray())
                    .andExpect(jsonPath("$.data.list").isEmpty())
                    .andExpect(jsonPath("$.data.totalRecords").value(0L));

            verify(sweetService, times(1)).listAll(any(Map.class));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/sweets/search - Search Sweets Tests")
    class SearchSweetsEndpointTests {

        @Test
        @DisplayName("Should return 200 OK with filtered sweets by search parameters")
        void shouldReturn200OkWithFilteredSweetsBySearchParameters() throws Exception {
            // Given
            List<SweetResponseDto> sweets = new ArrayList<>();
            sweets.add(sweetResponseDto);
            PaginatedBaseResponse<SweetResponseDto> paginatedResponse = new PaginatedBaseResponse<>();
            paginatedResponse.setList(sweets);
            paginatedResponse.setTotalRecords(1L);
            paginatedResponse.setCurrentPage(0L);

            BaseResponse expectedResponse = new BaseResponse(paginatedResponse, true, Constants.SWEETS_RETRIEVED_SUCCESSFULLY);
            ResponseEntity<BaseResponse> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

            when(sweetService.listAll(any(Map.class))).thenReturn(paginatedResponse);
            when(responseHandler.okResponse(eq(paginatedResponse), eq(HttpStatus.OK), eq(Constants.SWEETS_RETRIEVED_SUCCESSFULLY)))
                    .thenReturn(responseEntity);

            // When & Then
            mockMvc.perform(get(Urls.BASE_URL + Urls.SWEETS_URL + Urls.SEARCH_URL)
                            .param("searchValue", "Gulab")
                            .param("minValue", "100")
                            .param("maxValue", "200")
                            .param("page", "0")
                            .param("size", "10")
                            .param("sortField", "name")
                            .param("sortOrder", "ASC"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.data.list").isArray())
                    .andExpect(jsonPath("$.data.totalRecords").value(1L));

            verify(sweetService, times(1)).listAll(any(Map.class));
        }

        @Test
        @DisplayName("Should return 200 OK with filtered sweets by price range only")
        void shouldReturn200OkWithFilteredSweetsByPriceRangeOnly() throws Exception {
            // Given
            PaginatedBaseResponse<SweetResponseDto> paginatedResponse = new PaginatedBaseResponse<>();
            paginatedResponse.setList(new ArrayList<>());
            paginatedResponse.setTotalRecords(0L);
            paginatedResponse.setCurrentPage(0L);

            BaseResponse expectedResponse = new BaseResponse(paginatedResponse, true, Constants.SWEETS_RETRIEVED_SUCCESSFULLY);
            ResponseEntity<BaseResponse> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

            when(sweetService.listAll(any(Map.class))).thenReturn(paginatedResponse);
            when(responseHandler.okResponse(eq(paginatedResponse), eq(HttpStatus.OK), eq(Constants.SWEETS_RETRIEVED_SUCCESSFULLY)))
                    .thenReturn(responseEntity);

            // When & Then
            mockMvc.perform(get(Urls.BASE_URL + Urls.SWEETS_URL + Urls.SEARCH_URL)
                            .param("minValue", "100")
                            .param("maxValue", "200"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true));

            verify(sweetService, times(1)).listAll(any(Map.class));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/sweets/:id - Update Sweet Tests")
    class UpdateSweetEndpointTests {

        @Test
        @DisplayName("Should update sweet successfully and return 200 OK")
        void shouldUpdateSweetSuccessfullyAndReturn200Ok() throws Exception {
            // Given
            String requestBody = objectMapper.writeValueAsString(sweetRequestDto);
            SweetResponseDto updatedResponse = new SweetResponseDto(1L, "Rasgulla", 2L, "Traditional Sweets", 200.0, 75);
            BaseResponse expectedResponse = new BaseResponse(updatedResponse, true, Constants.SWEET_UPDATED_SUCCESSFULLY);
            ResponseEntity<BaseResponse> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

            when(sweetService.update(eq(1L), any(SweetRequestDto.class))).thenReturn(updatedResponse);
            when(responseHandler.okResponse(eq(updatedResponse), eq(HttpStatus.OK), eq(Constants.SWEET_UPDATED_SUCCESSFULLY)))
                    .thenReturn(responseEntity);

            // When & Then
            mockMvc.perform(put(Urls.BASE_URL + Urls.SWEETS_URL + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value(Constants.SWEET_UPDATED_SUCCESSFULLY))
                    .andExpect(jsonPath("$.data.id").value(1L))
                    .andExpect(jsonPath("$.data.name").value("Rasgulla"));

            verify(sweetService, times(1)).update(eq(1L), any(SweetRequestDto.class));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when sweet not found")
        void shouldReturn400BadRequestWhenSweetNotFound() throws Exception {
            // Given
            String requestBody = objectMapper.writeValueAsString(sweetRequestDto);
            when(sweetService.update(eq(999L), any(SweetRequestDto.class)))
                    .thenThrow(new ResponseStatusException("Sweet not found", HttpStatus.BAD_REQUEST));

            // When & Then
            mockMvc.perform(put(Urls.BASE_URL + Urls.SWEETS_URL + "/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false))
                    .andExpect(jsonPath("$.message").value("Sweet not found"));

            verify(sweetService, times(1)).update(eq(999L), any(SweetRequestDto.class));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when name is blank")
        void shouldReturn400BadRequestWhenNameIsBlank() throws Exception {
            // Given
            sweetRequestDto.setName("");
            String requestBody = objectMapper.writeValueAsString(sweetRequestDto);

            // When & Then
            mockMvc.perform(put(Urls.BASE_URL + Urls.SWEETS_URL + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false));

            verify(sweetService, never()).update(anyLong(), any(SweetRequestDto.class));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when sweet name already exists")
        void shouldReturn400BadRequestWhenSweetNameAlreadyExists() throws Exception {
            // Given
            String requestBody = objectMapper.writeValueAsString(sweetRequestDto);
            when(sweetService.update(eq(1L), any(SweetRequestDto.class)))
                    .thenThrow(new ResponseStatusException(Constants.SWEET_NAME_ALREADY_EXISTS, HttpStatus.BAD_REQUEST));

            // When & Then
            mockMvc.perform(put(Urls.BASE_URL + Urls.SWEETS_URL + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false))
                    .andExpect(jsonPath("$.message").value(Constants.SWEET_NAME_ALREADY_EXISTS));

            verify(sweetService, times(1)).update(eq(1L), any(SweetRequestDto.class));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/sweets/:id - Delete Sweet Tests")
    class DeleteSweetEndpointTests {

        @Test
        @DisplayName("Should delete sweet successfully and return 200 OK")
        void shouldDeleteSweetSuccessfullyAndReturn200Ok() throws Exception {
            // Given
            BaseResponse expectedResponse = new BaseResponse(true, Constants.SWEET_DELETED_SUCCESSFULLY);
            ResponseEntity<BaseResponse> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

            doNothing().when(sweetService).delete(1L);
            when(responseHandler.okResponse(eq(HttpStatus.OK), eq(Constants.SWEET_DELETED_SUCCESSFULLY)))
                    .thenReturn(responseEntity);

            // When & Then
            mockMvc.perform(delete(Urls.BASE_URL + Urls.SWEETS_URL + "/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value(Constants.SWEET_DELETED_SUCCESSFULLY));

            verify(sweetService, times(1)).delete(1L);
            verify(responseHandler, times(1)).okResponse(eq(HttpStatus.OK), eq(Constants.SWEET_DELETED_SUCCESSFULLY));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when sweet not found")
        void shouldReturn400BadRequestWhenSweetNotFound() throws Exception {
            // Given
            doThrow(new ResponseStatusException("Sweet not found", HttpStatus.BAD_REQUEST))
                    .when(sweetService).delete(999L);

            // When & Then
            mockMvc.perform(delete(Urls.BASE_URL + Urls.SWEETS_URL + "/999"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false))
                    .andExpect(jsonPath("$.message").value("Sweet not found"));

            verify(sweetService, times(1)).delete(999L);
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when invalid ID format")
        void shouldReturn400BadRequestWhenInvalidIdFormat() throws Exception {
            // When & Then
            mockMvc.perform(delete(Urls.BASE_URL + Urls.SWEETS_URL + "/invalid"))
                    .andExpect(status().isBadRequest());

            verify(sweetService, never()).delete(anyLong());
        }
    }
}

