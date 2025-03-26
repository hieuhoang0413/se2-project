package com.se2.midterm.repository;

import com.se2.midterm.entity.Category;
import com.se2.midterm.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByNameIgnoreCase(String name);
}

