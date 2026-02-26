package com.javanators.RetailWeb.Service;


import com.javanators.RetailWeb.Dto.ProductRequest;
import com.javanators.RetailWeb.Entity.Category;
import com.javanators.RetailWeb.Entity.Product;
import com.javanators.RetailWeb.Repository.CategoryRepository;
import com.javanators.RetailWeb.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public List<Product> getByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> getByCategoryName(String name) {
        return productRepository.findByCategoryName(name);
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Product create(ProductRequest req) {
        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Product product = Product.builder()
                .name(req.getName())
                .category(category)
                .brand(req.getBrand())
                .price(req.getPrice())
                .stock(req.getStock())
                .packaging(req.getPackaging())
                .imageUrl(req.getImageUrl())
                .build();

        return productRepository.save(product);
    }

    public Product update(Long id, ProductRequest req) {
        Product product = getById(id);
        if (req.getName() != null)
            product.setName(req.getName());
        if (req.getPrice() != null)
            product.setPrice(req.getPrice());
        if (req.getStock() != null)
            product.setStock(req.getStock());
        if (req.getBrand() != null)
            product.setBrand(req.getBrand());
        if (req.getPackaging() != null)
            product.setPackaging(req.getPackaging());
        if (req.getImageUrl() != null)
            product.setImageUrl(req.getImageUrl());
        if (req.getCategoryId() != null) {
            Category category = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            product.setCategory(category);
        }
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> getLowStockProducts(int threshold) {
        return productRepository.findLowStockProducts(threshold);
    }
}
