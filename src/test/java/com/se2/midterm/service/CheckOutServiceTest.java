package com.se2.midterm.service;
import com.se2.midterm.entity.*;
import com.se2.midterm.repository.OrderDetailRepository;
import com.se2.midterm.repository.OrderRepository;
import com.se2.midterm.repository.OrderStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class CheckOutServiceTest {

    @Mock
    private CartService cartService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderDetailRepository orderDetailRepository;

    @Mock
    private OrderStatusRepository orderStatusRepository;

    @InjectMocks
    private CheckOutService checkOutService;

    private User mockUser;
    private Cart mockCart;
    private CartItem mockCartItem;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Khởi tạo đối tượng User và Cart giả lập
        mockUser = new User();
        mockUser.setId(1L);

        mockCart = new Cart();
        mockCart.setUser(mockUser);

        // Đảm bảo giỏ hàng không rỗng
        mockCartItem = new CartItem();
        mockCartItem.setProduct(new Product(11L, "Product_Test", "This is test", 199.11, 2, 1, "image.png", new Category("Test", "This is also test", true)));  // Ví dụ đơn giản về sản phẩm
        System.out.println("Created product: " + mockCartItem.getProduct().getName());

        // Giả lập CartService để trả về giỏ hàng giả lập
        when(cartService.getOrCreateCart(mockUser)).thenReturn(mockCart);

        // Giả lập các repository khác nếu cần
        OrderStatus mockStatus = new OrderStatus();
        mockStatus.setStatus(OrderStatus.Status.PENDING);
        when(orderStatusRepository.findByStatus(OrderStatus.Status.PENDING)).thenReturn(mockStatus);

        // Giả lập lưu Order
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    public void testCheckout_SuccessWithItems() {
        // ✅ Create product
        Product mockProduct = new Product(
                1L, "Product_Test", "This is test", 199.11, 2, 1, "image.png",
                new Category("Test", "This is also test", true)
        );

        // ✅ Create CartItem
        CartItem existingCartItem = new CartItem();
        existingCartItem.setProduct(mockProduct);
        existingCartItem.setQuantity(2);
        existingCartItem.setPrice(mockProduct.getPrice()); // must be set!

        // ✅ Create Cart
        Cart preparedCart = new Cart();
        preparedCart.setUser(mockUser);
        preparedCart.setCartItems(new ArrayList<>());
        existingCartItem.setCart(preparedCart); // link item back to cart
        preparedCart.getCartItems().add(existingCartItem);

        // ✅ No spy needed — use real cart logic
        when(cartService.getOrCreateCart(any(User.class))).thenReturn(preparedCart);

        // ✅ Order status mock
        OrderStatus mockStatus = new OrderStatus();
        mockStatus.setStatus(OrderStatus.Status.PENDING);
        when(orderStatusRepository.findByStatus(OrderStatus.Status.PENDING)).thenReturn(mockStatus);

        // ✅ Save order mock
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ✅ Call checkout
        Order order = checkOutService.checkout(mockUser.getId());

        // ✅ Calculate expected total manually
        double expectedTotal = mockProduct.getPrice() * existingCartItem.getQuantity(); // 199.11 * 2 = 398.22

        // ✅ Assertions
        assertNotNull(order, "Order should not be null");
        assertEquals(expectedTotal, order.getTotalAmount(), 0.001, "Total amount should match cart's total price");
        assertEquals(OrderStatus.Status.PENDING, order.getStatus().getStatus(), "Order status should be PENDING");
        assertNotNull(order.getCode(), "Order code should not be null");
        assertFalse(order.getCode().isEmpty(), "Order code should not be empty");

        // ✅ Ensure order details are saved
        verify(orderDetailRepository, times(preparedCart.getCartItems().size())).save(any(OrderDetail.class));
    }



    @Test
    public void testCheckout_EmptyCart() {
        // Giả lập giỏ hàng trống
        when(cartService.getOrCreateCart(mockUser)).thenReturn(new Cart(mockUser)); // Trả về giỏ hàng mới và trống

        // Gọi phương thức checkout với giỏ hàng trống và kiểm tra xem có lỗi không
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            checkOutService.checkout(mockUser.getId());
        });

        assertEquals("Cart is empty or not found!", thrown.getMessage());
    }
}


