package com.se2.midterm.controller;

import com.se2.midterm.entity.Order;
import com.se2.midterm.entity.OrderStatus;
import com.se2.midterm.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    public String listOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "orderHistoryAdmin";
    }

    @GetMapping("/{orderId}")
    public String viewOrderDetails(@PathVariable Long orderId, Model model) {
        Order order = orderService.getOrderById(orderId);
        model.addAttribute("order", order);
        return "orderDetailAdmin";
    }

    // Lọc đơn hàng theo trạng thái (ví dụ PENDING, DELIVERING, DELIVERED)
    @GetMapping("/status/{status}")
    public String listOrdersByStatus(@PathVariable OrderStatus status, Model model) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        model.addAttribute("orders", orders);
        return "orderHistoryAdmin";
    }
}
