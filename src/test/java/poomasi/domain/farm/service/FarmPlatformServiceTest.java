package poomasi.domain.farm.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import poomasi.domain.farm.dto.FarmResponse;
import poomasi.domain.farm.entity.Farm;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FarmPlatformServiceTest {

    @InjectMocks
    private FarmPlatformService farmPlatformService;

    @Mock
    private FarmService farmService;

    @Nested
    @DisplayName("ID로 농장 가져오기")
    class GetFarmByFarmId {
        @Test
        @DisplayName("농장 ID로 농장 정보를 성공적으로 가져온다")
        void should_returnFarmResponse_when_farmExists() {
            // given
            Long farmId = 1L;
            Farm farm = Farm.builder()
                    .id(farmId)
                    .name("Test Farm")
                    .ownerId(1L)
                    .build();
            given(farmService.getFarmByFarmId(farmId)).willReturn(farm);

            // when
            FarmResponse response = farmPlatformService.getFarmByFarmId(farmId);

            // then
            assertThat(response.id()).isEqualTo(farmId);
            assertThat(response.name()).isEqualTo("Test Farm");
            verify(farmService).getFarmByFarmId(farmId);  // farmService 호출 확인
        }

        @Test
        @DisplayName("농장 ID로 농장 정보를 가져오는데 농장이 존재하지 않는 경우 예외를 발생시킨다")
        void should_throwException_when_farmNotExist() {
            // given
            Long farmId = 1L;
            given(farmService.getFarmByFarmId(farmId)).willThrow(new BusinessException(BusinessError.FARM_NOT_FOUND));

            // when & then
            assertThatThrownBy(() -> farmPlatformService.getFarmByFarmId(farmId))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("businessError", BusinessError.FARM_NOT_FOUND);

            verify(farmService).getFarmByFarmId(farmId);
        }
    }

    @Nested
    @DisplayName("농장 리스트 가져오기")
    class GetFarmList {
        @Test
        @DisplayName("페이징 요청으로 농장 리스트를 성공적으로 가져온다")
        void should_returnFarmResponseList_when_farmsExist() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            Farm farm1 = Farm.builder()
                    .id(1L)
                    .name("Farm 1")
                    .ownerId(1L)
                    .build();
            Farm farm2 = Farm.builder()
                    .id(2L)
                    .name("Farm 2")
                    .ownerId(2L)
                    .build();
            List<Farm> farmList = List.of(farm1, farm2);

            given(farmService.getFarmList(pageable)).willReturn(farmList);

            // when
            List<FarmResponse> responseList = farmPlatformService.getFarmList(pageable);

            // then
            assertThat(responseList).hasSize(2);
            assertThat(responseList.get(0).name()).isEqualTo("Farm 1");
            assertThat(responseList.get(1).name()).isEqualTo("Farm 2");
            verify(farmService).getFarmList(pageable);  // farmService 호출 확인
        }
    }
}
