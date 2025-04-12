package com.se2.midterm.controller;

import com.se2.midterm.entity.Order;
import com.se2.midterm.entity.OrderStatus;
import com.se2.midterm.entity.Product;
import com.se2.midterm.repository.OrderStatusRepository;
import com.se2.midterm.service.OrderService;
import com.se2.midterm.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final ProductService productService;
    private final OrderService orderService;
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    public AdminController(ProductService productService, OrderService orderService, OrderStatusRepository orderStatusRepository) {
        this.productService = productService;
        this.orderService = orderService;
        this.orderStatusRepository = orderStatusRepository;
    }

    @GetMapping
    public String adminDashboard(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "admin"; // Trả về trang admin.html
    }

    @GetMapping("/orders")
    public String orderDashboard(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "3") int size,
                                 Model model) {
        Page<Order> orderPage = orderService.getAllOrdersPaged(page, size);
        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        return "orders";
    }

    // Trang chỉnh sửa trạng thái
    @GetMapping("/orders/{id}/edit")
    public String editOrderStatus(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id);
        List<OrderStatus> statuses = orderStatusRepository.findAll();

        model.addAttribute("order", order);
        model.addAttribute("statuses", statuses);
        return "editOrderStatus";
    }

    // Cập nhật trạng thái đơn hàng
    @PostMapping("/orders/{id}/update-status")
    public String updateOrderStatus(@PathVariable Long id,
                                    @RequestParam Long statusId) {
        Order order = orderService.getOrderById(id);
        OrderStatus newStatus = orderStatusRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Status not found"));

        order.setStatus(newStatus);
        orderService.save(order);
        return "redirect:/admin/orders";
    }

}