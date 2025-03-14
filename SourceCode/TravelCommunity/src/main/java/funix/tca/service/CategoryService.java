package funix.tca.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import funix.tca.entity.Category;
import funix.tca.repository.CategoryRepository;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public void addCategory(Category category) {
        if (!categoryRepository.existsByName(category.getName())) {
            categoryRepository.save(category);
        }
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
    
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public void updateCategory(Category category) {
        Category existingCategory = categoryRepository.findById(category.getId()).orElse(null);
        if (existingCategory != null) {
            existingCategory.setName(category.getName());
            categoryRepository.save(existingCategory);
        }
    }

	public Object findAll() {
		// TODO Auto-generated method stub
		return categoryRepository.findAll();
	}
}
