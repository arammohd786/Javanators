package com.javanators.RetailWeb.Controller;


import com.javanators.RetailWeb.Entity.CartItem;
import com.javanators.RetailWeb.Entity.User;
import com.javanators.RetailWeb.Service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(cartService.getCartItems(user));
    }

    @PostMapping("/add")
    public ResponseEntity<CartItem> addToCart(@AuthenticationPrincipal User user,
                                              @RequestBody CartRequest request) {
        return ResponseEntity.ok(cartService.addToCart(user, request));
    }

    @DeleteMapping("/remove/{itemId}")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long itemId) {
        cartService.removeFromCart(itemId);
        return ResponseEntity.noContent().build();
    }
}

