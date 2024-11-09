package poomasi.domain.member._profile.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CUSTOMER")
public class CustomerProfile extends MemberProfile{
    @Column(nullable = true, length = 255)
    private String address;

    @Column(nullable = true, length = 255)
    private String addressDetail;

    @Column(nullable=true, length=255)
    private Long coordinateX;

    @Column(nullable=true, length=255)
    private Long coordinateY;
}
