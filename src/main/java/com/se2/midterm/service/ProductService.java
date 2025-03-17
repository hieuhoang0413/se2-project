package com.se2.midterm.service;

import com.se2.midterm.entity.Product;
import com.se2.midterm.entity.ProductImage;
import com.se2.midterm.repository.ProductImageRepository;
import com.se2.midterm.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Lấy sản phẩm theo ID
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    // Thêm sản phẩm (Chỉ ADMIN mới gọi được)
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    // Cập nhật sản phẩm (Chỉ ADMIN mới gọi được)
    public Product updateProduct(Long id, Product productDetails) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setName(productDetails.getName());
            product.setPrice(productDetails.getPrice());
            product.setDescription(productDetails.getDescription());
            return productRepository.save(product);
        } else {
            throw new RuntimeException("Product with ID " + id + " not found");
        }
    }

    // Xóa sản phẩm (Chỉ ADMIN mới gọi được)
    public void deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else {
            throw new RuntimeException("Product with ID " + id + " not found");
        }
    }

    public void addImagesToProduct(Long productId, List<String> imageUrls) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        for (String imageUrl : imageUrls) {
            ProductImage productImage = new ProductImage(imageUrl, product);
            productImageRepository.save(productImage);
        }
    }

    public List<ProductImage> getProductImages(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return productImageRepository.findByProduct(product);
    }
}
