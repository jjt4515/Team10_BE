package poomasi.domain.product._tag.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import poomasi.domain.product._tag.dto.TagRequest;
import poomasi.domain.product._tag.service.TagService;

@Controller
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @Secured("ROLE_ADMIN")
    @PostMapping("/api/products/tag")
    public ResponseEntity<?> addTag(@RequestBody TagRequest tagRequest) {
        Long tagId = tagService.addTag(tagRequest);
        return new ResponseEntity<>(tagId, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/api/products/tag")
    public ResponseEntity<?> deleteTag(@RequestBody TagRequest tagRequest) {
        tagService.deleteTag(tagRequest);
        return ResponseEntity.ok().build();
    }
}
