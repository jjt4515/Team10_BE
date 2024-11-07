package poomasi.domain.product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import poomasi.domain.product.dto.ProductTagRequest;
import poomasi.domain.product.service.ProductTagService;

@Controller
@RequiredArgsConstructor
public class ProductTagController {

    private final ProductTagService productTagService;

    @Secured("ROLE_ADMIN")
    @PostMapping("/api/products/tag")
    public ResponseEntity<?> addTag(@RequestBody ProductTagRequest productTagRequest) {
        productTagService.addTag(productTagRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/api/products/tag")
    public ResponseEntity<?> deleteTag(@RequestBody ProductTagRequest productTagRequest) {
        productTagService.deleteTag(productTagRequest);
        return ResponseEntity.ok().build();
    }
}
