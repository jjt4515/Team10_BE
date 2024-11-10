package poomasi.domain.store.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import poomasi.domain.member.entity.Member;
import poomasi.domain.store.dto.StoreRegisterRequest;
import poomasi.domain.product.entity.Product;

@Entity
@NoArgsConstructor
@Getter
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;
    private String phone;

    @OneToOne(fetch = FetchType.LAZY)
    private Member owner;

    @Comment("사업자 번호")
    private String businessNumber;
    @Comment("배송비")
    private Integer shipingFee;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    List<Product> products = new ArrayList<>();

    @Builder
    public Store(Long id, String name, String address, String phone, Member owner,
            String businessNumber, Integer shipingFee) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.owner = owner;
        this.businessNumber = businessNumber;
        this.shipingFee = shipingFee;
    }

    public void updateStore(StoreRegisterRequest storeRegisterRequest) {
        this.name = storeRegisterRequest.name();
        this.address = storeRegisterRequest.address();
        this.phone = storeRegisterRequest.phone();
        this.businessNumber = storeRegisterRequest.businessNumber();
        this.shipingFee = storeRegisterRequest.shipingFee();
    }

    public void addProduct(Product saveProduct) {
        this.products.add(saveProduct);
    }
}
