package com.javanators.RetailWeb.Controller;


import com.javanators.RetailWeb.Dto.OrderRequest;
import com.javanators.RetailWeb.Entity.Order;
import com.javanators.RetailWeb.Entity.User;
import com.javanators.RetailWeb.Service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/place")
    public ResponseEntity<Order> placeOrder(@AuthenticationPrincipal User user,
                                            @RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.placeOrder(user, request));
    }

    @GetMapping("/my")
    public ResponseEntity<List<Order>> myOrders(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(orderService.getUserOrders(user));
    }

    @PostMapping("/reorder/{orderId}")
    public ResponseEntity<Order> reorder(@AuthenticationPrincipal User user,
                                         @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.reorder(user, orderId));
    }
}

