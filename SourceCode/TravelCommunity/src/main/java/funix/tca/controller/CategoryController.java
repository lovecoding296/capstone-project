package funix.tca.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import funix.tca.entity.Category;
import funix.tca.service.CategoryService;

@Controller
@RequestMapping("/admin/manage/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Hiển thị danh sách danh mục
    @GetMapping
    public String showCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("newCategory", new Category());
        return "admin/categories/category-list";
    }

    // Xử lý thêm danh mục
    @PostMapping("/add")
    public String addCategory(@ModelAttribute("newCategory") Category category) {
        categoryService.addCategory(category);
        return "redirect:/categories";
    }

    // Xóa danh mục
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/admin/manage/categories";
    }
    
 // Hiển thị form chỉnh sửa danh mục
    @GetMapping("/edit/{id}")
    public String editCategory(@PathVariable Long id, Model model) {
        Category category = categoryService.getCategoryById(id);
        if (category != null) {
            model.addAttribute("category", category);
            return "admin/categories/category-edit";
        }
        return "redirect:/admin/manage/categories";
    }

    // Cập nhật danh mục
    @PostMapping("/update")
    public String updateCategory(@ModelAttribute("category") Category category) {
        categoryService.updateCategory(category);
        return "redirect:/admin/manage/categories";
    }
}
