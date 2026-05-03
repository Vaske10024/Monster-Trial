import { useEffect, useMemo, useRef, useState } from 'react';
import { musicAssets } from '../assets/assetMap.js';
import { getStoredValue, setStoredValue } from '../utils/safeStorage.js';

const ENABLED_KEY = 'monster-trial.music.enabled';
const VOLUME_KEY = 'monster-trial.music.volume';
const DEFAULT_VOLUME = 0.45;
const VOLUME_STEP = 0.1;

function clampVolume(value) {
  return Math.max(0, Math.min(1, Number(value) || 0));
}

function readInitialVolume() {
  const stored = Number(getStoredValue(VOLUME_KEY, DEFAULT_VOLUME));
  return Number.isFinite(stored) ? clampVolume(stored) : DEFAULT_VOLUME;
}

function trackForGameState(view, run) {
  const bossId = run?.battle?.boss?.id;
  if (view === 'battle' && bossId && musicAssets.bosses[bossId]) {
    return {
      src: musicAssets.bosses[bossId],
      label: run?.battle?.boss?.name || bossId.replaceAll('_', ' ')
    };
  }

  return {
    src: musicAssets.main,
    label: 'Main theme'
  };
}

export default function AudioControls({ view, run }) {
  const audioRef = useRef(null);
  const [enabled, setEnabled] = useState(() => getStoredValue(ENABLED_KEY, 'false') === 'true');
  const [volume, setVolume] = useState(readInitialVolume);
  const [needsGesture, setNeedsGesture] = useState(false);

  const activeTrack = useMemo(() => trackForGameState(view, run), [view, run?.battle?.boss?.id, run?.battle?.boss?.name]);

  useEffect(() => {
    const audio = audioRef.current;
    if (!audio) return;
    audio.loop = true;
    audio.preload = 'auto';
  }, []);

  useEffect(() => {
    setStoredValue(ENABLED_KEY, String(enabled));
  }, [enabled]);

  useEffect(() => {
    setStoredValue(VOLUME_KEY, String(volume));
    if (audioRef.current) {
      audioRef.current.volume = volume;
    }
  }, [volume]);

  useEffect(() => {
    const audio = audioRef.current;
    if (!audio || !activeTrack.src) return;

    const wasPlaying = enabled && !audio.paused;
    const needsSourceSwap = audio.getAttribute('src') !== activeTrack.src;

    if (needsSourceSwap) {
      audio.src = activeTrack.src;
      audio.currentTime = 0;
    }

    audio.volume = volume;

    if (enabled || wasPlaying) {
      audio.play()
        .then(() => setNeedsGesture(false))
        .catch(() => setNeedsGesture(true));
    }
  }, [activeTrack.src, enabled, volume]);

  async function playCurrentTrack() {
    const audio = audioRef.current;
    if (!audio || !activeTrack.src) return;

    if (audio.getAttribute('src') !== activeTrack.src) {
      audio.src = activeTrack.src;
      audio.currentTime = 0;
    }

    audio.volume = volume;
    await audio.play();
  }

  async function toggleMusic() {
    const audio = audioRef.current;
    if (!audio) return;

    if (enabled && !needsGesture) {
      audio.pause();
      setEnabled(false);
      setNeedsGesture(false);
      return;
    }

    setEnabled(true);
    try {
      await playCurrentTrack();
      setNeedsGesture(false);
    } catch {
      setNeedsGesture(true);
    }
  }

  async function nudgeVolume(delta) {
    const nextVolume = clampVolume(Math.round((volume + delta) * 10) / 10);
    setVolume(nextVolume);

    if (enabled) {
      try {
        await playCurrentTrack();
        setNeedsGesture(false);
      } catch {
        setNeedsGesture(true);
      }
    }
  }

  const percent = Math.round(volume * 100);

  return (
    <div className="audio-controls panel" aria-label="Background music controls">
      <audio ref={audioRef} loop preload="auto" />
      <div className="audio-status">
        <span className={`audio-led ${enabled && !needsGesture ? 'on' : ''}`} />
        <div>
          <strong>{enabled ? activeTrack.label : 'Music off'}</strong>
          <small>{needsGesture ? 'Tap play to start audio' : `${percent}% volume`}</small>
        </div>
      </div>
      <div className="audio-buttons">
        <button type="button" className="ghost-btn audio-toggle" onClick={toggleMusic}>
          {enabled && !needsGesture ? 'Pause' : 'Play'}
        </button>
        <button type="button" className="ghost-btn audio-step" onClick={() => nudgeVolume(-VOLUME_STEP)} aria-label="Lower music volume">−</button>
        <button type="button" className="ghost-btn audio-step" onClick={() => nudgeVolume(VOLUME_STEP)} aria-label="Raise music volume">+</button>
      </div>
    </div>
  );
}
