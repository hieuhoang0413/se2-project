package com.se2.midterm.controller;

import com.se2.midterm.entity.Category;
import com.se2.midterm.entity.Product;
import com.se2.midterm.service.CategoryService;
import com.se2.midterm.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categoryList", categoryService.getAllCategories());
        return "addProduct";
    }

    @PostMapping("/add")
    public String addProduct(
            @ModelAttribute Product product,
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "newCategory", required = false) String newCategory
            ) {

        try {
            if (!imageFile.isEmpty()) {
                String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
                Path uploadPath = Paths.get("uploads/images");

                // Tạo thư mục nếu chưa tồn tại
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Path filePath = uploadPath.resolve(fileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                product.setImage(fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to store image file.");
        }

        // Xử lý danh mục
        Category category = null;
        if (newCategory != null && !newCategory.trim().isEmpty()) {
            category = categoryService.findByName(newCategory.trim());
            if (category == null) {
                category = new Category();
                category.setName(newCategory.trim());
                category.setDescription("Created via form");
                category.setActive(true);
                category = categoryService.save(category);
            }
        } else if (categoryId != null) {
            category = categoryService.getById(categoryId);
        }

        if (category == null) {
            throw new IllegalArgumentException("Category not found or not selected");
        }

        product.setCategory(category);
        productService.addProduct(product);

        return "redirect:/admin";
    }

    // Xóa sản phẩm
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/admin";
    }

    @PostMapping("/delete-selected")
    public String deleteSelectedProducts(@RequestParam("selectedProducts") List<Long> selectedProductIds) {
        productService.deleteProducts(selectedProductIds);
        return "redirect:/admin";
    }

    // Tra cứu sản phẩm admin
    @GetMapping("/admin")
    public String showAdminPage(@RequestParam(value = "search", required = false) String searchQuery, Model model) {
        List<Product> products;

        if (searchQuery != null && !searchQuery.isEmpty()) {
            products = productService.searchByName(searchQuery);
        } else {
            products = productService.getAllProducts();
        }

        model.addAttribute("products", products);
        model.addAttribute("searchQuery", searchQuery);
        return "admin";
    }

    @GetMapping("/{id}")
    public String getProductDetail(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "productDetail";
    }

    // Edit Product
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("categoryList", categoryService.getAllCategories());
        return "editProduct";
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Long id,
            @ModelAttribute Product product,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "newCategory", required = false) String newCategory,
            @RequestParam(value = "oldImage", required = false) String oldImage) {

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                // Xóa ảnh cũ nếu có
                if (oldImage != null && !oldImage.isEmpty()) {
                    Path oldImagePath = Paths.get("uploads/images", oldImage);
                    Files.deleteIfExists(oldImagePath);
                }

                // Lưu ảnh mới
                String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
                Path uploadPath = Paths.get("uploads/images");
                if (!Files.exists(uploadPath))
                    Files.createDirectories(uploadPath);

                Files.copy(imageFile.getInputStream(), uploadPath.resolve(fileName),
                        StandardCopyOption.REPLACE_EXISTING);
                product.setImage(fileName);
            } else {
                // Không chọn ảnh mới → giữ lại ảnh cũ
                product.setImage(oldImage);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to store or replace image");
        }

        // Xử lý category
        Category category = null;
        if (newCategory != null && !newCategory.trim().isEmpty()) {
            category = categoryService.findByName(newCategory.trim());
            if (category == null) {
                category = new Category();
                category.setName(newCategory.trim());
                category.setDescription("Created via edit form");
                category.setActive(true);
                category = categoryService.save(category);
            }
        } else if (categoryId != null) {
            category = categoryService.getById(categoryId);
        }

        product.setCategory(category);

        // Cập nhật vào database
        productService.updateProduct(id, product);
        return "redirect:/admin";
    }

}
