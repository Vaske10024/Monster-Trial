package com.monstertrials.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UseSkillRequest(@NotBlank String skillId) {
}
