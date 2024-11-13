package poomasi.domain.farm._category.controller;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import poomasi.domain.farm._category.dto.response.FarmCategoryResponse;
import poomasi.domain.farm._category.service.FarmCategoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/farm/category")
public class FarmCategoryController {
    private final FarmCategoryService farmCategoryService;

    public ResponseEntity<List<FarmCategoryResponse>> getFarmCategories() {
        return ResponseEntity.ok(
                farmCategoryService.getFarmCategories().stream()
                        .map(FarmCategoryResponse::toResponse)
                        .toList()
        );
    }
}
