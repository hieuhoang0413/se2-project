package com.se2.midterm.service;

import com.se2.midterm.entity.Product;
import com.se2.midterm.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // Lấy tất cả sản phẩm
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Lấy sản phẩm theo ID
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    // Thêm sản phẩm mới
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    // Cập nhật sản phẩm
    public Product updateProduct(Long id, Product updatedProduct) {
        Product existingProduct = getProductById(id);

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setQuantity(updatedProduct.getQuantity());
        existingProduct.setStatusId(updatedProduct.getStatusId());
        existingProduct.setImage(updatedProduct.getImage()); // đã xử lý image trước đó
        existingProduct.setCategory(updatedProduct.getCategory());

        return productRepository.save(existingProduct);
    }



    // Xoá 1 product
    public void deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            Product product = productRepository.findById(id).orElseThrow(() ->
                    new RuntimeException("Product with ID " + id + " not found"));

            // Xóa ảnh nếu có
            if (product.getImage() != null) {
                Path imagePath = Paths.get("uploads/images", product.getImage());
                System.out.println("Đường dẫn ảnh cần xóa: " + imagePath.toAbsolutePath());
                try {
                    Files.deleteIfExists(imagePath);
                } catch (IOException e) {
                    System.err.println("Cannot delete image file: " + imagePath);
                    e.printStackTrace();
                }
            }

            productRepository.deleteById(id);
        } else {
            throw new RuntimeException("Product with ID " + id + " not found");
        }
    }

    // Xóa nhiều products
    public void deleteProducts(List<Long> ids) {
        List<Product> products = productRepository.findAllById(ids);

        for (Product product : products) {
            if (product.getImage() != null) {
                Path imagePath = Paths.get("uploads/images", product.getImage());
                try {
                    Files.deleteIfExists(imagePath);
                } catch (IOException e) {
                    System.err.println("Không thể xóa file ảnh: " + imagePath);
                    e.printStackTrace();
                }
            }
        }

        productRepository.deleteAllById(ids);
    }

    // Search
    public List<Product> searchByName(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }
}
