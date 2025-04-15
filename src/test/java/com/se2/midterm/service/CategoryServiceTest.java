package com.se2.midterm.service;

import com.se2.midterm.entity.Category;
import com.se2.midterm.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    private Category testCategory;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Chairs");
        testCategory.setDescription("All types of chairs");
        testCategory.setActive(true);
    }

    @Test
    public void testGetAllCategories_ReturnsList() {
        List<Category> categoryList = Arrays.asList(testCategory);
        when(categoryRepository.findAll()).thenReturn(categoryList);

        List<Category> result = categoryService.getAllCategories();

        assertEquals(1, result.size());
        assertEquals("Chairs", result.get(0).getName());
    }

    @Test
    public void testGetAllCategories_EmptyList() {
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

        List<Category> result = categoryService.getAllCategories();

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetById_Found() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        Category result = categoryService.getById(1L);

        assertNotNull(result);
        assertEquals("Chairs", result.getName());
    }

    @Test
    public void testGetById_NotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            categoryService.getById(99L);
        });

        assertEquals("Category not found", exception.getMessage());
    }

    @Test
    public void testSaveCategory() {
        when(categoryRepository.save(testCategory)).thenReturn(testCategory);

        Category result = categoryService.save(testCategory);

        assertNotNull(result);
        assertEquals("Chairs", result.getName());
    }

    @Test
    public void testFindByName_CaseInsensitive() {
        when(categoryRepository.findByNameIgnoreCase("chairs")).thenReturn(testCategory);

        Category result = categoryService.findByName("chairs");

        assertNotNull(result);
        assertEquals("Chairs", result.getName());
    }

    @Test
    public void testFindByName_NotFound() {
        when(categoryRepository.findByNameIgnoreCase("tables")).thenReturn(null);

        Category result = categoryService.findByName("tables");

        assertNull(result);
    }
}
