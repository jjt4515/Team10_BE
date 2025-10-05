package poomasi.domain.farm.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import poomasi.domain.farm.FarmTestHelper;
import poomasi.domain.farm.dto.request.FarmRegisterRequest;
import poomasi.domain.farm.entity.Farm;
import poomasi.domain.farm.repository.FarmRepository;
import poomasi.domain.member.entity.Member;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FarmFarmerServiceTest {

    @InjectMocks
    private FarmFarmerService farmFarmerService;
    @Mock
    private FarmInfoService farmInfoService;
    @Mock
    private FarmService farmService;
    @Mock
    private FarmRepository farmRepository;


    @Nested
    @DisplayName("농장 등록")
    class RegisterFarm {
        @Test
        @DisplayName("농장이 이미 존재하는 경우 예외를 발생시킨다")
        void should_throwException_when_farmAlreadyExists() {
            // given
            Member member = Member.builder()
                    .id(1L)
                    .build();
            Farm existingFarm = Farm.builder()
                    .id(1L)
                    .name("Existing Farm")
                    .ownerId(1L)
                    .build();

            given(farmRepository.getFarmByOwnerIdAndDeletedAtIsNull(member.getId())).willReturn(Optional.of(existingFarm));

            FarmRegisterRequest request = FarmRegisterRequest
                    .builder()
                    .name("New Farm")
                    .address("Address")
                    .addressDetail("Detail")
                    .phoneNumber("010-1234-5678")
                    .latitude(1.0)
                    .longitude(1.0)
                    .phoneNumber("010-123-123")
                    .experiencePrice(10000)
                    .maxPeople(10)
                    .maxTeam(10)
                    .categoryId(1L)
                    .imageUrl("10")
                    .price(10).build();

            // when & then
            assertThatThrownBy(() -> farmFarmerService.registerFarm(member, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("businessError", BusinessError.FARM_ALREADY_EXISTS);
        }
    }


    @Nested
    @DisplayName("농장 삭제")
    class DeleteFarm {
        @Test
        @DisplayName("소유자가 아닌 경우 예외를 발생시킨다")
        void should_throwException_when_ownerMismatchOnDelete() {
            // given
            Long farmId = 1L;
            Long farmerId = 2L;
            Farm farm = Farm.builder()
                    .id(farmId)
                    .name("Farm")
                    .ownerId(3L)
                    .build();
            given(farmService.getFarmByFarmId(farmId)).willReturn(farm);

            // when & then
            assertThatThrownBy(() -> farmFarmerService.deleteFarm(farmerId, farmId))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("businessError", BusinessError.FARM_OWNER_MISMATCH);
        }

        @Test
        @DisplayName("농장 삭제에 성공한다")
        void should_deleteFarm_when_ownerMatches() {
            // given
            Long farmId = 1L;
            Long farmerId = 1L;
            Farm farm = Farm.builder()
                    .id(farmId)
                    .name("Farm")
                    .ownerId(farmerId)
                    .build();

            given(farmService.getFarmByFarmId(farmId)).willReturn(farm);

            // when
            farmFarmerService.deleteFarm(farmerId, farmId);

            // then
            verify(farmService).delete(farm);
            verify(farmInfoService).deleteFarmInfo(farmId);
        }

        @Test
        @DisplayName("농장이 존재하지 않는 경우 예외를 발생시킨다")
        void should_throwException_when_farmNotExistOnDelete() {
            // given
            Long farmId = 3L;
            Long farmerId = 1L;

            // farmService에서 농장이 없을 때 null을 반환하도록 설정
            given(farmService.getFarmByFarmId(farmId)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> farmFarmerService.deleteFarm(farmerId, farmId))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("businessError", BusinessError.FARM_NOT_FOUND);
        }

        @Test
        @DisplayName("농장이 이미 삭제된 경우 예외를 발생시킨다")
        void should_throwException_when_farmAlreadyDeleted() {
            // given
            Long farmId = 1L;
            Long farmerId = 1L;
            Farm farm = Farm.builder()
                    .id(farmId)
                    .name("Farm")
                    .ownerId(farmerId)
                    .deletedAt(LocalDateTime.now()) // 이미 삭제된 상태
                    .build();

            given(farmService.getFarmByFarmId(farmId)).willReturn(farm);

            // when & then
            assertThatThrownBy(() -> farmFarmerService.deleteFarm(farmerId, farmId))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("businessError", BusinessError.FARM_ALREADY_DELETED);

            // delete 메서드가 호출되지 않았는지 확인
            verify(farmService, never()).delete(farm);
            verify(farmInfoService, never()).deleteFarmInfo(farmId);
        }
    }
}
