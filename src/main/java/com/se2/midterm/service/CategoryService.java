package com.se2.midterm.service;

import com.se2.midterm.entity.Category;
import com.se2.midterm.repository.CategoryRepository;
import com.se2.midterm.entity.Category;
import com.se2.midterm.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService {

        @Autowired
        private CategoryRepository categoryRepository;

        public List<Category> getAllCategories() {
            return categoryRepository.findAll();
        }

        public Category getById(Long id) {
            return categoryRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
        }

        public Category save(Category category) {
            return categoryRepository.save(category);
        }

        public Category findByName(String name) {
            return categoryRepository.findByNameIgnoreCase(name);
        }
    }

