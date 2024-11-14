package poomasi.domain.farm.controller;

import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import poomasi.domain.farm.dto.response.FarmDetailResponse;
import poomasi.domain.farm.service.FarmPlatformService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/farms")
@Description("인증이 필요 없는 Farm 메소드")
public class FarmController {
    private final FarmPlatformService farmPlatformService;

    @Description("Farm 단건 조회")
    @GetMapping("/{farmId}")
    public ResponseEntity<?> getFarm(@PathVariable Long farmId) {
        return ResponseEntity.ok(farmPlatformService.getFarmByFarmId(farmId));
    }

    @Description("Farm 상세 조회")
    @GetMapping("/{farmId}/detail")
    public ResponseEntity<FarmDetailResponse> getFarmDetail(@PathVariable Long farmId) {
        return ResponseEntity.ok(farmPlatformService.getFarmDetailByFarmId(farmId));
    }

    @GetMapping("Farm 다건 조회")
    public ResponseEntity<?> getFarmList(Pageable pageable) {
        return ResponseEntity.ok(farmPlatformService.getFarmList(pageable));
    }

    @GetMapping("byFarmer/{farmerId}")
    public ResponseEntity<?> getFarmsByFarmerId(@PathVariable Long farmerId) {
        return ResponseEntity.ok(farmPlatformService.getFarmsByFarmerId(farmerId));
    }
}
