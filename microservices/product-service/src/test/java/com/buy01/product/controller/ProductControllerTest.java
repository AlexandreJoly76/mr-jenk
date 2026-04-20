package com.buy01.product.controller;

import com.buy01.product.model.Product;
import com.buy01.product.repository.ProductRepository;
import com.buy01.product.service.ProductProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductProducer productProducer;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private ProductController productController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @org.springframework.web.bind.annotation.RestControllerAdvice
    static class TestExceptionHandler {
        @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
        public org.springframework.http.ResponseEntity<String> handleException(Exception e) {
            return org.springframework.http.ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setControllerAdvice(new com.buy01.product.config.GlobalExceptionHandler(), new TestExceptionHandler())
                .build();
    }

    private String createTestToken(String id, String role) {
        String payload = "{\"id\":\"" + id + "\", \"role\":\"" + role + "\"}";
        String encodedPayload = Base64.getEncoder().encodeToString(payload.getBytes());
        return "header." + encodedPayload + ".signature";
    }

    @Test
    void getAllProducts_ShouldReturnList() throws Exception {
        Product product = new Product();
        product.setId("1");
        when(productRepository.findAll()).thenReturn(Arrays.asList(product));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"));
    }

    @Test
    void getProductById_ShouldReturnProduct() throws Exception {
        Product product = new Product();
        product.setId("1");
        when(productRepository.findById("1")).thenReturn(Optional.of(product));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void getProductById_ShouldThrowException_WhenNotFound() throws Exception {
        when(productRepository.findById("1")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createProduct_ShouldSaveProduct_WhenSeller() throws Exception {
        String token = createTestToken("user1", "SELLER");
        Product product = Product.builder()
                .name("Product 1")
                .description("Desc 1")
                .category("Cat 1")
                .price(new BigDecimal("10.0"))
                .quantity(5)
                .userId("user1")
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Product 1"));
        
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProduct_ShouldThrowException_WhenNotSeller() throws Exception {
        String token = createTestToken("user1", "USER");
        Product product = Product.builder()
                .name("Product 1")
                .description("Desc 1")
                .category("Cat 1")
                .price(new BigDecimal("10.0"))
                .quantity(5)
                .userId("user1")
                .build();
        
        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getProductsBySeller_ShouldReturnList() throws Exception {
        Product product = new Product();
        product.setUserId("user1");
        when(productRepository.findByUserId("user1")).thenReturn(Arrays.asList(product));

        mockMvc.perform(get("/api/products/seller/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value("user1"));
    }

    @Test
    void deleteProduct_ShouldDelete_WhenSeller() throws Exception {
        String token = createTestToken("user1", "SELLER");
        Product product = new Product();
        product.setId("1");
        product.setImageIds(Arrays.asList("img1"));
        
        when(productRepository.findById("1")).thenReturn(Optional.of(product));

        mockMvc.perform(delete("/api/products/1")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        verify(productProducer).sendImageDeletionRequest("img1");
        verify(productRepository).deleteById("1");
    }

    @Test
    void searchProducts_ShouldReturnPage() throws Exception {
        Product product = new Product();
        product.setName("Search Result");
        
        Page<Product> page = new PageImpl<>(Collections.singletonList(product), PageRequest.of(0, 10), 1);
        
        when(mongoTemplate.count(any(Query.class), eq(Product.class))).thenReturn(1L);
        when(mongoTemplate.find(any(Query.class), eq(Product.class))).thenReturn(Collections.singletonList(product));

        mockMvc.perform(get("/api/products/search")
                .param("keyword", "test")
                .param("category", "cat")
                .param("minPrice", "10")
                .param("maxPrice", "100")
                .param("availableOnly", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Search Result"));
    }
}
