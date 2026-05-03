package com.monstertrials.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpgradeRequest(@NotBlank String upgradeId) {
}
