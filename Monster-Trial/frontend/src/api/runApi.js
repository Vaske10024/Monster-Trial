import { apiRequest } from './client.js';

export const runApi = {
  startRun: (profile = {}) => apiRequest('/run/start', { method: 'POST', body: { weapon: profile.weapon } }),
  getRun: (runId) => apiRequest(`/run/${runId}`),
  buyUpgrade: (runId, upgradeId) => apiRequest(`/run/${runId}/shop/upgrade`, {
    method: 'POST',
    body: { upgradeId }
  }),
  buyPotion: (runId, potionId) => apiRequest(`/run/${runId}/shop/potion`, {
    method: 'POST',
    body: { potionId }
  }),
  equipSkills: (runId, skillIds) => apiRequest(`/run/${runId}/skills/equip`, {
    method: 'POST',
    body: { skillIds }
  }),
  startBattle: (runId) => apiRequest(`/run/${runId}/battle/start`, { method: 'POST' }),
  useSkill: (runId, skillId) => apiRequest(`/run/${runId}/battle/use-skill`, {
    method: 'POST',
    body: { skillId }
  }),
  usePotion: (runId, potionId) => apiRequest(`/run/${runId}/battle/use-potion`, {
    method: 'POST',
    body: { potionId }
  }),
  retryBattle: (runId) => apiRequest(`/run/${runId}/battle/retry`, { method: 'POST' }),
  replayBoss: (runId, bossId) => apiRequest(bossId ? `/run/${runId}/boss/${bossId}/replay` : `/run/${runId}/boss/replay`, { method: 'POST' }),
  nextBoss: (runId) => apiRequest(`/run/${runId}/boss/next`, { method: 'POST' }),
  clearBattle: (runId) => apiRequest(`/run/${runId}/battle/clear`, { method: 'POST' }),
  getSkills: () => apiRequest('/metadata/skills'),
  getBosses: () => apiRequest('/metadata/bosses'),
  getUpgrades: () => apiRequest('/metadata/upgrades'),
  getPotions: () => apiRequest('/metadata/potions')
};
