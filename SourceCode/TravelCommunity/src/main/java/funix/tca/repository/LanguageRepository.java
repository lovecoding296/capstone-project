package funix.tca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import funix.tca.entity.Language;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {
    boolean existsByName(String name);
}
