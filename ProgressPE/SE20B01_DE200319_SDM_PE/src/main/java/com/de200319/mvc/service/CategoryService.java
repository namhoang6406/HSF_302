package com.de200319.mvc.service;

import com.de200319.mvc.entity.Category;
import java.util.List;

public interface CategoryService {
    // Q3.1 / Q5.2.9 - Category dropdown must be populated dynamically from the database
    List<Category> getAllCategories();
}