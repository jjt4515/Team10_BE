package poomasi.domain.product._category.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import poomasi.domain.product._category.dto.CategoryRequest;
import poomasi.domain.product.entity.Product;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "categoryId")
    List<Product> products = new ArrayList<>();

    @Builder
    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
        this.products = new ArrayList<>();
    }

    public Category(String name) {
        this.name = name;
    }

    public void modifyName(CategoryRequest categoryRequest) {
        this.name = categoryRequest.name();
    }

    public void deleteProduct(Product product) {
        this.products.remove(product);
    }

    public void addProduct(Product saveProduct) {
        this.products.add(saveProduct);
    }
}
