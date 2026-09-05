package com.syfe.pfm.config;

import com.syfe.pfm.entity.Category;
import com.syfe.pfm.entity.CategoryType;
import com.syfe.pfm.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    public DataSeeder(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (categoryRepository.count() == 0) {
            List<Category> defaultCategories = Arrays.asList(
                new Category("Salary", CategoryType.INCOME, false, null),
                new Category("Food", CategoryType.EXPENSE, false, null),
                new Category("Rent", CategoryType.EXPENSE, false, null),
                new Category("Transportation", CategoryType.EXPENSE, false, null),
                new Category("Entertainment", CategoryType.EXPENSE, false, null),
                new Category("Healthcare", CategoryType.EXPENSE, false, null),
                new Category("Utilities", CategoryType.EXPENSE, false, null)
            );
            categoryRepository.saveAll(defaultCategories);
        }
    }
}
