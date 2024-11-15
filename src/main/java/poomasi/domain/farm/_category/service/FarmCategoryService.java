package poomasi.domain.farm._category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poomasi.domain.farm._category.domain.FarmCategory;
import poomasi.domain.farm._category.repository.FarmCategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FarmCategoryService {
    private final FarmCategoryRepository farmCategoryRepository;

    public List<FarmCategory> getFarmCategories() {
        return farmCategoryRepository.findAll();
    }
}
