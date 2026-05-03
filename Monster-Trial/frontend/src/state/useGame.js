import { useCallback, useEffect, useMemo, useState } from 'react';
import { runApi } from '../api/runApi.js';
import { defaultCharacterProfile, normalizeCharacterProfile } from '../data/characterOptions.js';
import { getStoredJson, getStoredValue, removeStoredValue, setStoredJson, setStoredValue } from '../utils/safeStorage.js';

const STORAGE_KEY = 'monster-trial.runId';
const PROFILE_KEY = 'monster-trial.characterProfile';

function viewForRun(run) {
  if (!run) return 'start';
  if (run.battle?.result === 'HERO_WIN') return 'victory';
  if (run.battle?.result === 'HERO_LOSE') return 'defeat';
  if (run.battle?.result === 'ONGOING') return 'battle';
  if (run.runComplete) return 'complete';
  return 'preview';
}

export function useGame() {
  const [run, setRun] = useState(null);
  const [view, setView] = useState('start');
  const [previousView, setPreviousView] = useState('preview');
  const [metadata, setMetadata] = useState({ skills: [], bosses: [], upgrades: [], potions: [] });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [characterProfile, setCharacterProfile] = useState(defaultCharacterProfile);

  const saveCharacterProfile = useCallback((profile) => {
    const normalized = normalizeCharacterProfile(profile);
    setCharacterProfile(normalized);
    setStoredJson(PROFILE_KEY, normalized);
    return normalized;
  }, []);

  const setRunAndPersist = useCallback((nextRun, route = true) => {
    setRun(nextRun);
    if (nextRun?.runId) {
      setStoredValue(STORAGE_KEY, nextRun.runId);
    }
    if (route) {
      setView(viewForRun(nextRun));
    }
  }, []);

  const runAction = useCallback(async (action, { route = true } = {}) => {
    setLoading(true);
    setError('');
    try {
      const nextRun = await action();
      setRunAndPersist(nextRun, route);
      return nextRun;
    } catch (err) {
      setError(err.message || 'Something went wrong.');
      return null;
    } finally {
      setLoading(false);
    }
  }, [setRunAndPersist]);

  const loadMetadata = useCallback(async () => {
    try {
      const [skills, bosses, upgrades, potions] = await Promise.all([
        runApi.getSkills(),
        runApi.getBosses(),
        runApi.getUpgrades(),
        runApi.getPotions()
      ]);
      setMetadata({ skills, bosses, upgrades, potions });
    } catch (err) {
      setError(err.message || 'Could not load game metadata.');
    }
  }, []);

  const loadSavedRun = useCallback(async () => {
    const storedProfile = getStoredJson(PROFILE_KEY, null);
    if (storedProfile) {
      setCharacterProfile(normalizeCharacterProfile(storedProfile));
    }

    const runId = getStoredValue(STORAGE_KEY, '');
    if (!runId) return;
    setLoading(true);
    setError('');
    try {
      const savedRun = await runApi.getRun(runId);
      setRunAndPersist(savedRun, true);
    } catch {
      removeStoredValue(STORAGE_KEY);
      setRun(null);
      setView('start');
      setError('Saved run could not be loaded. Start a new run when the backend is available.');
    } finally {
      setLoading(false);
    }
  }, [setRunAndPersist]);

  useEffect(() => {
    loadMetadata();
    loadSavedRun();
  }, [loadMetadata, loadSavedRun]);

  const goTo = useCallback((nextView) => {
    setPreviousView(view);
    setView(nextView);
  }, [view]);

  const goBack = useCallback(() => {
    setView(previousView || 'preview');
  }, [previousView]);

  const actions = useMemo(() => ({
    beginCharacterCreate: () => {
      setPreviousView('start');
      setView('builder');
      setError('');
    },
    startNewRunFromProfile: async (profile) => {
      const normalized = saveCharacterProfile(profile);
      return runAction(() => runApi.startRun(normalized));
    },
    refreshRun: () => run?.runId ? runAction(() => runApi.getRun(run.runId)) : Promise.resolve(null),
    buyUpgrade: (upgradeId) => run?.runId ? runAction(() => runApi.buyUpgrade(run.runId, upgradeId), { route: false }) : Promise.resolve(null),
    buyPotion: (potionId) => run?.runId ? runAction(() => runApi.buyPotion(run.runId, potionId), { route: false }) : Promise.resolve(null),
    equipSkills: (skillIds) => run?.runId ? runAction(() => runApi.equipSkills(run.runId, skillIds), { route: false }) : Promise.resolve(null),
    startBattle: () => run?.runId ? runAction(() => runApi.startBattle(run.runId)) : Promise.resolve(null),
    useSkill: (skillId) => run?.runId ? runAction(() => runApi.useSkill(run.runId, skillId), { route: false }) : Promise.resolve(null),
    usePotion: (potionId) => run?.runId ? runAction(() => runApi.usePotion(run.runId, potionId), { route: false }) : Promise.resolve(null),
    retryBattle: () => run?.runId ? runAction(() => runApi.retryBattle(run.runId)) : Promise.resolve(null),
    replayBoss: (bossId) => run?.runId ? runAction(() => runApi.replayBoss(run.runId, bossId)) : Promise.resolve(null),
    nextBoss: () => run?.runId ? runAction(() => runApi.nextBoss(run.runId)) : Promise.resolve(null),
    clearBattle: () => run?.runId ? runAction(() => runApi.clearBattle(run.runId)) : Promise.resolve(null),
    clearRun: () => {
      removeStoredValue(STORAGE_KEY);
      setRun(null);
      setView('start');
      setError('');
    },
    restartAtBuilder: () => {
      removeStoredValue(STORAGE_KEY);
      setRun(null);
      setPreviousView('start');
      setView('builder');
      setError('');
    },
    updateCharacterProfile: saveCharacterProfile,
    routeAfterBattle: (nextRun) => setView(viewForRun(nextRun)),
    goTo,
    goBack,
    clearError: () => setError('')
  }), [goBack, goTo, run?.runId, runAction, saveCharacterProfile]);

  return {
    run,
    view,
    metadata,
    loading,
    error,
    previousView,
    characterProfile,
    actions
  };
}
