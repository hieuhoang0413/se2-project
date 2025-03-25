package com.se2.midterm.controller;

import com.se2.midterm.entity.Product;
import com.se2.midterm.entity.ProductImage;
import com.se2.midterm.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String getAllProduct() {
        return "index";
    }

    // Lấy sản phẩm theo ID và hiển thị trang chi tiết
    @GetMapping("/{id}")
    public String getProductById(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "productDetail";
    }

    // Hiển thị danh sách sản phẩm
    @GetMapping
    public String listProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "index"; // Hiển thị trang list.html
    }

    // Hiển thị trang thêm sản phẩm
    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "addProduct";
    }

    // Xử lý thêm sản phẩm (chỉ ADMIN mới truy cập)
    @PostMapping("/add")
    public String addProduct(@ModelAttribute Product product) {
        productService.addProduct(product);
        return "redirect:/admin";
    }

    // Hiển thị trang sửa sản phẩm
    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        Optional<Product> product = Optional.ofNullable(productService.getProductById(id));
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            return "editProduct";
        } else {
            return "redirect:/admin";
        }
    }

    // Xử lý cập nhật sản phẩm
    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute Product product) {
        productService.updateProduct(id, product);
        return "redirect:/admin";
    }

    // Xóa sản phẩm (chỉ ADMIN mới truy cập)
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/admin";
    }
}
