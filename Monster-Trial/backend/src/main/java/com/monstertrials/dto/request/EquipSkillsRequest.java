package com.monstertrials.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record EquipSkillsRequest(@NotNull List<String> skillIds) {
}
