package funix.tca.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import funix.tca.entity.Language;
import funix.tca.repository.LanguageRepository;

@Controller
@RequestMapping("/admin/manage/languages")
public class LanguageController {

    @Autowired
    private LanguageRepository languageRepository;

    @GetMapping
    public String listLanguages(Model model) {
        model.addAttribute("languages", languageRepository.findAll());
        model.addAttribute("newLanguage", new Language()); // Thêm dòng này
        return "admin/languages/language-list";
    }


    @PostMapping("/add")
    public String createLanguage(@ModelAttribute Language language) {
        languageRepository.save(language);
        return "redirect:/admin/manage/languages";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Language language = languageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Language not found"));
        model.addAttribute("language", language);
        return "admin/languages/language-edit";
    }

    @PostMapping("/update/{id}")
    public String updateLanguage(@PathVariable Long id, @ModelAttribute Language updatedLanguage) {
        Language language = languageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Language not found"));
        language.setName(updatedLanguage.getName());
        languageRepository.save(language);
        return "redirect:/admin/manage/languages";
    }

    @PostMapping("/delete/{id}")
    public String deleteLanguage(@PathVariable Long id) {
        languageRepository.deleteById(id);
        return "redirect:/admin/manage/languages";
    }
}
