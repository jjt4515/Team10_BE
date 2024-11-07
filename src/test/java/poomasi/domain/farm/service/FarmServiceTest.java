package poomasi.domain.farm.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import poomasi.domain.farm.FarmTestHelper;
import poomasi.domain.farm.entity.Farm;
import poomasi.domain.farm.repository.FarmRepository;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FarmServiceTest {
    @InjectMocks
    private FarmService farmService;

    @Mock
    private FarmRepository farmRepository;

    @Nested
    @DisplayName("ID로 농장 가져오기")
    class getFarmByFarmId {
        @Test
        @DisplayName("농장이 존재하는 경우 농장 가져오기에 성공한다")
        void should_successGetFarm_when_farmExist() {
            // given
            Farm farm = FarmTestHelper.makeRandomFarm();
            given(farmRepository.findByIdAndDeletedAtIsNull(farm.getId())).willReturn(Optional.of(farm));

            // when
            Farm result = farmService.getFarmByFarmId(farm.getId());

            // then
            assertThat(result.getId()).isEqualTo(farm.getId());
            assertThat(result.getName()).isEqualTo(farm.getName());
            assertThat(result.getStatus()).isEqualTo(farm.getStatus());
        }
    }

}
