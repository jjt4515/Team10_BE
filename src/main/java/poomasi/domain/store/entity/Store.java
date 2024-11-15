package poomasi.domain.store.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import poomasi.domain.member.entity.Member;
import poomasi.domain.product.entity.Product;
import poomasi.domain.store.dto.StoreRegisterRequest;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String name;

    @Setter
    private String address;
    private String phone;

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    private Member owner;

    @Comment("사업자 번호")
    private String businessNumber;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    List<Product> products = new ArrayList<>();

    @Builder
    public Store(Long id, String name, String address, String phone, Member owner,
            String businessNumber) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.owner = owner;
        this.businessNumber = businessNumber;
    }

    public void updateStore(StoreRegisterRequest storeRegisterRequest) {
        this.name = storeRegisterRequest.name();
        this.address = storeRegisterRequest.address();
        this.phone = storeRegisterRequest.phone();
        this.businessNumber = storeRegisterRequest.businessNumber();
    }

    public void addProduct(Product saveProduct) {
        this.products.add(saveProduct);
    }
}
