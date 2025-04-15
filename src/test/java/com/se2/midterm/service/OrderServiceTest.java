package com.se2.midterm.service;

import com.se2.midterm.entity.Order;
import com.se2.midterm.entity.OrderStatus;
import com.se2.midterm.entity.User;
import com.se2.midterm.repository.OrderRepository;
import com.se2.midterm.repository.OrderStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderStatusRepository orderStatusRepository;

    private User mockUser;
    private Order mockOrder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUser = new User();
        mockUser.setId(1L);

        mockOrder = new Order();
        mockOrder.setId(10L);
        mockOrder.setUser(mockUser);
        mockOrder.setOrderDate(LocalDateTime.now());
        mockOrder.setTotalAmount(199.99);

        OrderStatus status = new OrderStatus();
        status.setStatus(OrderStatus.Status.PENDING); // ✅ no constructor needed
        mockOrder.setStatus(status);
    }


    @Test
    public void testGetAllOrders() {
        List<Order> orderList = Arrays.asList(mockOrder);
        when(orderRepository.findAllByOrderByOrderDateDesc()).thenReturn(orderList);

        List<Order> result = orderService.getAllOrders();

        assertEquals(1, result.size());
        assertEquals(mockOrder, result.get(0));
    }

    @Test
    public void testGetOrderById_Found() {
        when(orderRepository.findById(10L)).thenReturn(Optional.of(mockOrder));

        Order result = orderService.getOrderById(10L);

        assertNotNull(result);
        assertEquals(mockOrder, result);
    }

    @Test
    public void testGetOrderById_NotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.getOrderById(99L);
        });

        assertEquals("Order not found", exception.getMessage());
    }

    @Test
    public void testFindOrdersByUser() {
        List<Order> userOrders = Arrays.asList(mockOrder);
        when(orderRepository.findByUser(mockUser)).thenReturn(userOrders);

        List<Order> result = orderService.findOrdersByUser(mockUser);

        assertEquals(1, result.size());
        assertEquals(mockOrder, result.get(0));
    }

    @Test
    public void testSaveOrder() {
        when(orderRepository.save(mockOrder)).thenReturn(mockOrder);

        Order result = orderService.save(mockOrder);

        assertNotNull(result);
        assertEquals(mockOrder, result);
    }

    @Test
    public void testGetAllOrdersPaged() {
        List<Order> orderList = Arrays.asList(mockOrder);
        Page<Order> page = new PageImpl<>(orderList);
        Pageable pageable = PageRequest.of(0, 5, Sort.by("orderDate").descending());

        when(orderRepository.findAll(pageable)).thenReturn(page);

        Page<Order> result = orderService.getAllOrdersPaged(0, 5);

        assertEquals(1, result.getContent().size());
        assertEquals(mockOrder, result.getContent().get(0));
    }

    @Test
    public void testInitOrderStatuses_WhenEmpty() {
        when(orderStatusRepository.count()).thenReturn(0L);
        when(orderStatusRepository.save(any(OrderStatus.class))).thenAnswer(i -> i.getArgument(0));

        orderService.initOrderStatuses();

        verify(orderStatusRepository, times(OrderStatus.Status.values().length)).save(any(OrderStatus.class));
    }

    @Test
    public void testInitOrderStatuses_WhenNotEmpty() {
        when(orderStatusRepository.count()).thenReturn(5L);

        orderService.initOrderStatuses();

        verify(orderStatusRepository, never()).save(any(OrderStatus.class));
    }
}
