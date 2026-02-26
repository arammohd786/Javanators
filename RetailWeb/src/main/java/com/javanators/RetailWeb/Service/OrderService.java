package com.javanators.RetailWeb.Service;

import com.javanators.RetailWeb.Dto.OrderRequest;
import com.javanators.RetailWeb.Entity.*;
import com.javanators.RetailWeb.Repository.CartRepository;
import com.javanators.RetailWeb.Repository.OrderRepository;
import com.javanators.RetailWeb.Repository.ProductRepository;
import com.javanators.RetailWeb.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
//    private final CouponRepository couponRepository;
    private final UserRepository userRepository;

    @Transactional
    public Order placeOrder(User user, OrderRequest request) {
        List<CartItem> cartItems = cartRepository.findByUserId(user.getId());

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Validate stock and compute subtotal
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
            subtotal = subtotal.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        // Apply coupon if provided
        BigDecimal discountAmount = BigDecimal.ZERO;
        String couponCode = null;
//        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
//            Coupon coupon = couponRepository.findByCodeAndActiveTrue(request.getCouponCode())
//                    .orElseThrow(() -> new RuntimeException("Invalid or expired coupon"));
//            if (coupon.getExpiryDate().isBefore(LocalDate.now())) {
//                throw new RuntimeException("Coupon has expired");
//            }
//            discountAmount = subtotal.multiply(coupon.getDiscountPercent())
//                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
//            couponCode = request.getCouponCode();
//        }

        BigDecimal totalAmount = subtotal.subtract(discountAmount);

        // Build order items + deduct stock (Phase 4 inventory logic)
        List<OrderItem> orderItems = cartItems.stream().map(item -> {
            Product product = item.getProduct();
            // Deduct stock automatically
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);

            return OrderItem.builder()
                    .product(product)
                    .quantity(item.getQuantity())
                    .price(product.getPrice())
                    .build();
        }).collect(Collectors.toList());

        Order order = Order.builder()
                .user(user)
                .totalAmount(totalAmount)
                .status("PENDING")
                .address(request.getAddress())
                .couponCode(couponCode)
                .discountAmount(discountAmount)
                .build();

        order = orderRepository.save(order);

        // Set order reference on items
        Order finalOrder = order;
        orderItems.forEach(i -> i.setOrder(finalOrder));
        orderRepository.save(finalOrder);

        // Award loyalty points (10 per order)
        user.setLoyaltyPoints(user.getLoyaltyPoints() + 10);
        userRepository.save(user);

        // Clear cart
        cartRepository.deleteByUserId(user.getId());

        return order;
    }

    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Transactional
    public Order reorder(User user, Long orderId) {
        Order original = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Add all original items back to cart
        original.getItems().forEach(item -> {
            cartRepository.findByUserIdAndProductId(user.getId(), item.getProduct().getId())
                    .ifPresentOrElse(
                            existing -> {
                                existing.setQuantity(existing.getQuantity() + item.getQuantity());
                                cartRepository.save(existing);
                            },
                            () -> cartRepository.save(CartItem.builder()
                                    .user(user)
                                    .product(item.getProduct())
                                    .quantity(item.getQuantity())
                                    .build()));
        });

        return placeOrder(user, new OrderRequest() {
            {
                setAddress(original.getAddress());
            }
        });
    }
}

