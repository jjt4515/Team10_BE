package poomasi.domain.member._profile.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@DiscriminatorValue("FARMER")
public class FarmerProfile extends MemberProfile{
    @Column(nullable = true, length = 100, unique = true)
    private String storeName;

    @Column(nullable = true, length = 100)
    private String storeAddress;

    @Column(nullable = true, length = 100)
    private String storeAddressDetail;

    @Column(nullable=true, length=100)
    private Long storeCoordinateX;

    @Column(nullable=true, length=100)
    private Long storeCoordinateY;

    @Column(nullable = true, length = 100, unique = true)
    private String farmName;

    @Column(nullable = true, length = 100)
    private String farmAddress;

    @Column(nullable = true, length = 100)
    private String farmAddressDetail;

    @Column(nullable=true, length=100)
    private Long farmCoordinateX;

    @Column(nullable=true, length=100)
    private Long farmCoordinateY;

    @ElementCollection
    @CollectionTable(name = "business_registration_numbers", joinColumns = @JoinColumn(name = "farmer_profile_id"))
    @Column(nullable = true, length=255)
    private List<String> businessRegistrationNumbers;

}
