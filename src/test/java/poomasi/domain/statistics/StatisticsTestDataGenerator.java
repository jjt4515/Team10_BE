package poomasi.domain.statistics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.member.entity.LoginType;
import poomasi.domain.member.entity.Member;
import poomasi.domain.member.entity.Role;
import poomasi.domain.member.repository.MemberRepository;
import poomasi.domain.order.entity.Order;
import poomasi.domain.order.entity.OrderedProduct;
import poomasi.domain.order.entity.OrderedProductStatus;
import poomasi.domain.order.repository.OrderRepository;
import poomasi.domain.product.entity.Product;
import poomasi.domain.product.repository.ProductRepository;
import poomasi.domain.product._category.entity.Category;
import poomasi.domain.product._category.repository.CategoryRepository;
import poomasi.domain.store.entity.Store;
import poomasi.domain.store.repository.StoreRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatisticsTestDataGenerator {

    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final JdbcTemplate jdbcTemplate;

    private final Random random = new Random(12345);

    private static final int BATCH_SIZE = 500;
    private static final int STORE_COUNT = 5;
    private static final int CATEGORY_COUNT = 5;
    private static final int PRODUCT_PER_STORE = 3;
    private static final int MEMBER_COUNT = 200;

    @Transactional
    public void generateFullTestData() {
        log.info("=== 테스트 데이터 생성 시작 ===");
        long startTime = System.currentTimeMillis();

        try {
            log.info("1. 회원 데이터 생성 중...");
            List<Member> members = createMembers();

            log.info("2. 카테고리 데이터 생성 중...");
            List<Category> categories = createCategories();

            log.info("3. 상점 데이터 생성 중...");
            List<Store> stores = createStores(members);

            log.info("4. 상품 데이터 생성 중...");
            List<Product> products = createProducts(stores, categories);

            log.info("5. 주문 데이터 생성 중... (25,000건).. 총 주문 상품 수는 50,000건");
            createOrdersInBatches(members, products, 25000);

            long endTime = System.currentTimeMillis();
            log.info("=== 테스트 데이터 생성 완료! 소요 시간: {}초 ===", (endTime - startTime) / 1000.0);

            printDataStatistics();

        } catch (Exception e) {
            log.error("테스트 데이터 생성 실패", e);
            throw new RuntimeException("테스트 데이터 생성 중 오류 발생", e);
        }
    }

    private List<Member> createMembers() {
        List<Member> members = new ArrayList<>();

        for (int i = 0; i < MEMBER_COUNT; i++) {
            Member member = Member.builder()
                    .name("테스트회원" + i)
                    .email("test" + i + "@test.com")
                    .password("password" + i)
                    .role(i < 20 ? Role.ROLE_FARMER : Role.ROLE_CUSTOMER)
                    .loginType(LoginType.LOCAL)
                    .build();
            members.add(member);
        }

        return memberRepository.saveAll(members);
    }

    private List<Category> createCategories() {
        List<Category> categories = new ArrayList<>();
        String[] categoryNames = {
                "채소", "과일", "곡물", "육류", "수산물",
                "유제품", "가공식품", "건강식품", "음료", "기타"
        };

        for (int i = 0; i < CATEGORY_COUNT; i++) {
            Category category = Category.builder()
                    .name(categoryNames[i])
                    .build();
            categories.add(category);
        }

        return categoryRepository.saveAll(categories);
    }

    private List<Store> createStores(List<Member> members) {
        List<Store> stores = new ArrayList<>();

        for (int i = 0; i < STORE_COUNT; i++) {
            Member owner = members.get(i);

            Store store = Store.builder()
                    .name("테스트농장" + i)
                    .address("서울시 강남구 테스트로 " + i)
                    .phone("010-" + String.format("%04d", i) + "-" + String.format("%04d", i))
                    .owner(owner)
                    .businessNumber("123-45-" + String.format("%05d", i))
                    .build();

            stores.add(store);
        }

        return storeRepository.saveAll(stores);
    }

    private List<Product> createProducts(List<Store> stores, List<Category> categories) {
        List<Product> products = new ArrayList<>();

        for (Store store : stores) {
            for (int i = 0; i < PRODUCT_PER_STORE; i++) {
                Category category = categories.get(random.nextInt(categories.size()));

                Product product = Product.builder()
                        .categoryId(category.getId())
                        .farmerId(store.getOwner().getId())
                        .name(store.getName() + "_상품" + i)
                        .description("테스트 상품 설명")
                        .stock(10000)
                        .price(BigDecimal.valueOf(5000 + random.nextInt(45000)))
                        .growEnv("테스트 재배환경")
                        .shippingFee(BigDecimal.valueOf(3000))
                        .orderLimit(10)
                        .store(store)
                        .build();

                products.add(product);
            }
        }

        return productRepository.saveAll(products);
    }

    private void createOrdersInBatches(List<Member> members, List<Product> products, int totalOrders) {
        LocalDateTime startDate = LocalDateTime.of(2020, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 6, 30, 23, 59);

        int batchCount = (totalOrders + BATCH_SIZE - 1) / BATCH_SIZE;

        for (int batch = 0; batch < batchCount; batch++) {
            int batchStart = batch * BATCH_SIZE;
            int batchEnd = Math.min(batchStart + BATCH_SIZE, totalOrders);
            int currentBatchSize = batchEnd - batchStart;

            List<Order> orders = new ArrayList<>(currentBatchSize);

            for (int i = 0; i < currentBatchSize; i++) {
                Member member = members.get(20 + random.nextInt(MEMBER_COUNT - 20));

                Order order = Order.builder()
                        .member(member)
                        .totalAmount(BigDecimal.ZERO)
                        .address("서울시 강남구 배송지 " + (batchStart + i))
                        .addressDetail("상세주소 " + (batchStart + i))
                        .deliveryRequest("문 앞에 놔주세요")
                        .build();

                int productCount = random.nextInt(3) + 1;
                BigDecimal totalAmount = BigDecimal.ZERO;

                for (int j = 0; j < productCount; j++) {
                    Product product = products.get(random.nextInt(products.size()));
                    int quantity = random.nextInt(3) + 1;

                    BigDecimal itemTotal = product.getPrice()
                            .multiply(BigDecimal.valueOf(quantity))
                            .add(product.getShippingFee());

                    OrderedProduct orderedProduct = OrderedProduct.builder()
                            .product(product)
                            .productName(product.getName())
                            .productDescription(product.getDescription())
                            .price(product.getPrice())
                            .count(quantity)
                            .deliveryFee(product.getShippingFee())
                            .imageUrl("test-image.jpg")
                            .growEnv(product.getGrowEnv())
                            .build();

                    orderedProduct.setOrderedProductStatus(OrderedProductStatus.DELIVERED);
                    order.addOrderedProduct(orderedProduct);

                    totalAmount = totalAmount.add(itemTotal);
                }

                order.addTotalAmount(totalAmount);
                orders.add(order);
            }

            orderRepository.saveAll(orders);
            updateOrderDates(orders, startDate, endDate);

            log.info("주문 데이터 생성 진행률: {}/{} ({}%)",
                    batchEnd, totalOrders,
                    String.format("%.1f", (batchEnd * 100.0 / totalOrders)));
        }
    }

    private void updateOrderDates(List<Order> orders, LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "UPDATE product_order SET updated_at = ? WHERE id = ?";

        List<Object[]> batchArgs = new ArrayList<>();
        long daysBetween = java.time.Duration.between(startDate, endDate).toDays();

        for (Order order : orders) {
            LocalDateTime randomDateTime = startDate.plusDays(random.nextInt((int) daysBetween + 1))
                    .plusHours(random.nextInt(24))
                    .plusMinutes(random.nextInt(60));

            batchArgs.add(new Object[]{randomDateTime, order.getId()});
        }

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private void printDataStatistics() {
        Long memberCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM member WHERE email LIKE 'test%@test.com'", Long.class);
        Long storeCount = storeRepository.count();
        Long categoryCount = categoryRepository.count();
        Long productCount = productRepository.count();
        Long orderCount = orderRepository.count();
        Long orderedProductCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM ordered_products", Long.class);

        log.info("\n========================================");
        log.info("생성된 데이터 통계");
        log.info("========================================");
        log.info("테스트 회원:  {} 명", memberCount);
        log.info("상점:         {} 개", storeCount);
        log.info("카테고리:     {} 개", categoryCount);
        log.info("상품:         {} 개", productCount);
        log.info("주문:         {} 건", orderCount);
        log.info("주문 상품:    {} 건", orderedProductCount);
        log.info("========================================\n");
    }

    @Transactional
    public void cleanupTestData() {
        log.info("=== 테스트 데이터 완전 삭제 시작 ===");

        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");

        jdbcTemplate.execute("TRUNCATE TABLE ordered_products");
        jdbcTemplate.execute("TRUNCATE TABLE product_order");
        jdbcTemplate.execute("TRUNCATE TABLE product");
        jdbcTemplate.execute("TRUNCATE TABLE store");
        jdbcTemplate.execute("TRUNCATE TABLE category");
        jdbcTemplate.execute("TRUNCATE TABLE member");  // 전체 삭제
        jdbcTemplate.execute("TRUNCATE TABLE member_profile");  // 전체 삭제

        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");

        log.info("=== 테스트 데이터 완전 삭제 완료 ===");
    }
}