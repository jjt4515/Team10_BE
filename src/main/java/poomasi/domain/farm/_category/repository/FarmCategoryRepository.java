package poomasi.domain.farm._category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poomasi.domain.farm._category.domain.FarmCategory;

@Repository
public interface FarmCategoryRepository extends JpaRepository<FarmCategory, Long> {
}
