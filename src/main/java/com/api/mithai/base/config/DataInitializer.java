package com.api.mithai.base.config;

import com.api.mithai.auth.entity.User;
import com.api.mithai.auth.enums.Role;
import com.api.mithai.auth.repository.UserRepository;
import com.api.mithai.sweet.entity.SweetCategory;
import com.api.mithai.sweet.repository.SweetCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SweetCategoryRepository sweetCategoryRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeAdminUser();
        initializeSweetCategories();
    }

    private void initializeAdminUser() {
        String adminEmail = "himanitrivedi1874@gmail.com";
        
        if (!userRepository.existsByEmailId(adminEmail)) {
            User adminUser = new User();
            adminUser.setUsername("himani");
            adminUser.setEmailId(adminEmail);
            adminUser.setPassword("Admin@123");
            adminUser.setRoleName(Role.ADMIN);
            
            userRepository.save(adminUser);
            log.info("Admin user initialized: {}", adminEmail);
        } else {
            log.info("Admin user already exists: {}", adminEmail);
        }
    }

    private void initializeSweetCategories() {
        List<String> categoryNames = Arrays.asList(
                "Milk Sweets",
                "Dry Fruits Sweets",
                "Traditional Sweets",
                "Modern Sweets",
                "Sugar-Free Sweets",
                "Festival Special",
                "Bengali Sweets",
                "Gujarati Sweets",
                "Rajasthani Sweets",
                "South Indian Sweets"
        );

        for (String categoryName : categoryNames) {
            if (!sweetCategoryRepository.existsByName(categoryName)) {
                SweetCategory category = new SweetCategory();
                category.setName(categoryName);
                sweetCategoryRepository.save(category);
                log.info("Sweet category initialized: {}", categoryName);
            } else {
                log.info("Sweet category already exists: {}", categoryName);
            }
        }
    }
}

