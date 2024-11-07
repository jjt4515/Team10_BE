package poomasi.domain.farm;

import poomasi.domain.farm.entity.Farm;

public class FarmTestHelper {
    public static Farm makeRandomFarm() {
        return Farm.builder()
                .id(1L)
                .name("RandomFarm")
                .build();
    }
}
