package com.javanators.RetailWeb.Controller;

import com.javanators.RetailWeb.Entity.Product;
import com.javanators.RetailWeb.Service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAll(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String category) {
        if (categoryId != null) {
            return ResponseEntity.ok(productService.getByCategory(categoryId));
        }
        if (category != null) {
            return ResponseEntity.ok(productService.getByCategoryName(category));
        }
        return ResponseEntity.ok(productService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }
}

