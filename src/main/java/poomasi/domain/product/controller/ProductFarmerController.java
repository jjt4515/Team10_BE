package poomasi.domain.product.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.member.entity.Member;
import poomasi.domain.product.dto.ProductRegisterRequest;
import poomasi.domain.product.dto.ProductRegisterResponse;
import poomasi.domain.product.dto.UpdateProductQuantityRequest;
import poomasi.domain.product.service.ProductFarmerService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@Slf4j
public class ProductFarmerController {

    private final ProductFarmerService productFarmerService;

    @Secured({"ROLE_FARMER", "ROLE_ADMIN"})
    @PostMapping("")
    public ResponseEntity<?> registerProduct
            (@AuthenticationPrincipal UserDetailsImpl userDetails,
                    @RequestBody ProductRegisterRequest product) {
        Member member = userDetails.getMember();
        ProductRegisterResponse response = productFarmerService.registerProduct(member, product);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Secured({"ROLE_FARMER", "ROLE_ADMIN"})
    @PutMapping("/{productId}")
    public ResponseEntity<?> modifyProduct(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody ProductRegisterRequest product,
            @PathVariable Long productId) {
        Member member = userDetails.getMember();
        productFarmerService.modifyProduct(member, product, productId);
        return new ResponseEntity<>(productId, HttpStatus.OK);
    }

    @Secured("ROLE_FARMER")
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long productId) {
        Member member = userDetails.getMember();
        productFarmerService.deleteProduct(member, productId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Secured("ROLE_FARMER")
    @PatchMapping("/{productId}")
    public ResponseEntity<?> updateProductQuantity(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long productId,
            @RequestBody UpdateProductQuantityRequest request) {
        log.debug("Product ID: {}", productId);
        log.debug("Update Request: {}", request);
        Member member = userDetails.getMember();
        productFarmerService.addQuantity(member, productId, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
