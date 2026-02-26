package com.javanators.RetailWeb.Service;


import com.javanators.RetailWeb.Dto.CartRequest;
import com.javanators.RetailWeb.Entity.CartItem;
import com.javanators.RetailWeb.Entity.Product;
import com.javanators.RetailWeb.Entity.User;
import com.javanators.RetailWeb.Repository.CartRepository;
import com.javanators.RetailWeb.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public List<CartItem> getCartItems(User user) {
        return cartRepository.findByUserId(user.getId());
    }

    public CartItem addToCart(User user, CartRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // If item already in cart, update quantity
        return cartRepository.findByUserIdAndProductId(user.getId(), product.getId())
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + request.getQuantity());
                    return cartRepository.save(existing);
                })
                .orElseGet(() -> cartRepository.save(
                        CartItem.builder()
                                .user(user)
                                .product(product)
                                .quantity(request.getQuantity())
                                .build()));
    }

    public void removeFromCart(Long itemId) {
        cartRepository.deleteById(itemId);
    }

    @Transactional
    public void clearCart(User user) {
        cartRepository.deleteByUserId(user.getId());
    }
}

