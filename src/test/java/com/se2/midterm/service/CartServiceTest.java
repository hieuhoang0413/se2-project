package com.se2.midterm.service;

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

        // 🔧 Make sure cartItems list is initialized
        testCart.setCartItems(new ArrayList<>());

        when(cartRepository.findByUser(any())).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any())).thenReturn(testCart); // ✅ consistent mocking
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
        testCart.getCartItems().add(item);

        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(cartRepository.save(any())).thenReturn(testCart);

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

        testCart.getCartItems().add(item); // Cho item vào giỏ hàng

        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(cartRepository.save(any())).thenReturn(testCart); // ✅ mock save()

        Cart result = cartService.removeFromCart(testUser, 1L);

        assertTrue(result.getCartItems().isEmpty()); // ✅ kiểm tra xoá thành công
        verify(cartItemRepository).delete(item); // ✅ đảm bảo repo gọi đúng
        verify(cartRepository).save(testCart);
    }
}

