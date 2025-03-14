package funix.tca.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import funix.tca.entity.Language;
import funix.tca.repository.LanguageRepository;

@Service
public class LanguageService {

    @Autowired
    private LanguageRepository languageRepository;

    public List<Language> getAllLanguages() {
        return languageRepository.findAll();
    }

    public void addLanguage(Language language) {
        if (!languageRepository.existsByName(language.getName())) {
            languageRepository.save(language);
        }
    }

    public void deleteLanguage(Long id) {
        languageRepository.deleteById(id);
    }

    public Language getLanguageById(Long id) {
        return languageRepository.findById(id).orElse(null);
    }

    public void updateLanguage(Language language) {
        Language existingLanguage = languageRepository.findById(language.getId()).orElse(null);
        if (existingLanguage != null) {
            existingLanguage.setName(language.getName());
            languageRepository.save(existingLanguage);
        }
    }
}
