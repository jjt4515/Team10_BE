package poomasi.domain.statistics;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class StatisticsDataGenerationTest {

    @Autowired
    private StatisticsTestDataGenerator testDataGenerator;

    @Test
    void generateTestData() {
        log.info("기존 테스트 데이터 삭제 중...");
        testDataGenerator.cleanupTestData();

        log.info("테스트 데이터 생성 시작...");
        testDataGenerator.generateFullTestData();
        log.info("테스트 데이터 생성 완료!");
    }
}