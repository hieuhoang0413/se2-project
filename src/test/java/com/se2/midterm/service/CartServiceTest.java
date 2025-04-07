package com.se2.midterm;

import com.se2.midterm.entity.*;
import com.se2.midterm.repository.*;
import com.se2.midterm.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderDetailRepository orderDetailRepository;
    @Mock
    private OrderStatusRepository orderStatusRepository;

    private User testUser;
    private Product testProduct;
    private Cart testCart;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setPrice(100);
        testProduct.setQuantity(10);
        testProduct.setName("Test Chair");

        testCart = new Cart(testUser);
        testCart.setId(1L);

        when(cartRepository.findByUser(any())).thenReturn(Optional.of(testCart));
    }

    @Test
    public void testAddToCart_NewItem() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(cartItemRepository.findByCartAndProduct(any(), any())).thenReturn(Optional.empty());

        cartService.addToCart(testUser, 1L, 2);

        verify(cartItemRepository).save(any(CartItem.class));
        verify(cartRepository).save(testCart);
    }

    @Test
    public void testUpdateCartItemQuantity() {
        CartItem item = new CartItem(testCart, testProduct, 1);
        item.setId(1L);

        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(item));

        Cart result = cartService.updateCartItemQuantity(testUser, 1L, 3);

        assertEquals(testCart, result);
        assertEquals(3, item.getQuantity());
        verify(cartItemRepository).save(item);
        verify(cartRepository).save(testCart);
    }

    @Test
    public void testRemoveFromCart() {
        CartItem item = new CartItem(testCart, testProduct, 1);
        item.setId(1L);

        testCart.getCartItems().add(item);

        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(item));

        Cart result = cartService.removeFromCart(testUser, 1L);

        assertTrue(result.getCartItems().isEmpty());
        verify(cartItemRepository).delete(item);
        verify(cartRepository).save(result);
    }

    @Test
    public void testCheckout() {
        CartItem item = new CartItem(testCart, testProduct, 2);
        testCart.getCartItems().add(item);

        OrderStatus status = new OrderStatus();
        status.setId(1L);
        status.setStatus(OrderStatus.Status.PENDING);

        when(orderStatusRepository.findByStatus(OrderStatus.Status.PENDING)).thenReturn(status);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        Cart result = cartService.checkout(testUser);

        verify(orderRepository).save(any(Order.class));
        verify(orderDetailRepository).save(any(OrderDetail.class));
        verify(cartItemRepository).deleteAll(any());
        assertEquals(0, result.getCartItems().size());
    }
}

