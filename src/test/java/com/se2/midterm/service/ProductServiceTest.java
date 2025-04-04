package com.se2.midterm.service;
import com.se2.midterm.entity.Product;
import com.se2.midterm.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;
    @Test
    void getAllProducts() {
            Product p1 = new Product();
            Product p2 = new Product();
            when(productRepository.findAll()).thenReturn(Arrays.asList(p1,p2));
            List<Product> products = productService.getAllProducts();
            assertEquals(2, products.size());
    }

    @Test
    void getProductById() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Table");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals("Table", result.getName());
    }

    @Test
    void addProduct() {
        Product product = new Product();
        product.setName("Chair");

        when(productRepository.save(product)).thenReturn(product);

        Product saved = productService.addProduct(product);

        assertEquals("Chair", saved.getName());
    }

    @Test
    void updateProduct() {
        Product existing = new Product();
        existing.setId(1L);
        existing.setName("Old Name");

        Product updated = new Product();
        updated.setName("New Name");
        updated.setPrice(100.0);
        updated.setDescription("Updated desc");
        updated.setQuantity(10);
        updated.setImage("new-image.jpg");

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenReturn(existing);

        Product result = productService.updateProduct(1L, updated);

        assertEquals("New Name", result.getName());
        assertEquals(100.0, result.getPrice());
        assertEquals("Updated desc", result.getDescription());
        verify(productRepository, times(1)).save(existing);
    }

    @Test
    void deleteProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setImage("example.jpg");

        when(productRepository.existsById(1L)).thenReturn(true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteProducts() {
        Product p1 = new Product();
        p1.setId(1L);
        p1.setImage("img1.jpg");

        Product p2 = new Product();
        p2.setId(2L);
        p2.setImage("img2.jpg");

        List<Long> ids = Arrays.asList(1L, 2L);
        when(productRepository.findAllById(ids)).thenReturn(Arrays.asList(p1, p2));

        productService.deleteProducts(ids);

        verify(productRepository, times(1)).deleteAllById(ids);
    }

    @Test
    void searchByName() {
        Product product = new Product();
        product.setName("Lamp");

        when(productRepository.findByNameContainingIgnoreCase("lamp")).thenReturn(List.of(product));

        List<Product> results = productService.searchByName("lamp");

        assertEquals(1, results.size());
        assertEquals("Lamp", results.get(0).getName());
    }
}