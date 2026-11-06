package com.de200319.mvc.service.impl;

import com.de200319.mvc.entity.Category;
import com.de200319.mvc.repository.CategoryRepository;
import com.de200319.mvc.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}