package poomasi.domain.image.test;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import poomasi.domain.image.entity.Image;
import poomasi.domain.image.entity.ImageType;
import poomasi.domain.image.repository.ImageRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RequiredArgsConstructor
@Transactional
@ActiveProfiles("test")
public class ImageDataGenerationTest {

    @Autowired
    private ImageRepository imageRepository;

    @Test
    @Commit
    public void insertTestImages() {
        List<Image> images = new ArrayList<>();

        for (int i = 1; i <= 50000; i++) {
            ImageType type = (i % 2 == 0) ? ImageType.PRODUCT_INTRO : ImageType.MEMBER_PROFILE;
            Long referenceId = (long) (i % 1000); // 0 ~ 999 사이

            Image image = Image.builder()
                    .objectKey("image-test-object-" + i)
                    .imageUrl("https://image-test.example.com/" + i + ".jpg")
                    .type(type)
                    .referenceId(referenceId)
                    .createdAt(LocalDateTime.now())
                    .deletedAt((i % 5 == 0) ? LocalDateTime.now() : null) // 20% 삭제 처리
                    .build();

            images.add(image);

            if (images.size() == 1000) {
                imageRepository.saveAll(images);
                images.clear();
            }
        }

        if (!images.isEmpty()) {
            imageRepository.saveAll(images);
        }

        System.out.println("테스트 데이터 5만 건 삽입 완료");
    }
}
