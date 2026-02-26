package com.javanators.RetailWeb.Repository;

import com.javanators.RetailWeb.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByCategoryName(String categoryName);

    List<Product> findByBrandContainingIgnoreCase(String brand);

    @Query("SELECT p FROM Product p WHERE p.stock <= :threshold")
    List<Product> findLowStockProducts(@Param("threshold") int threshold);
}
