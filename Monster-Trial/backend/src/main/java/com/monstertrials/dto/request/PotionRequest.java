package com.monstertrials.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PotionRequest(@NotBlank String potionId) {
}
