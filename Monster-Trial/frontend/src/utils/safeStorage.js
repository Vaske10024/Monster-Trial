function getStorage() {
  if (typeof window === 'undefined' || !window.localStorage) {
    return null;
  }
  return window.localStorage;
}

export function getStoredValue(key, fallback = null) {
  try {
    const storage = getStorage();
    return storage ? storage.getItem(key) : fallback;
  } catch {
    return fallback;
  }
}

export function setStoredValue(key, value) {
  try {
    const storage = getStorage();
    if (storage) {
      storage.setItem(key, value);
    }
  } catch {
    // Storage can be unavailable in private or restricted browser modes.
  }
}

export function removeStoredValue(key) {
  try {
    const storage = getStorage();
    if (storage) {
      storage.removeItem(key);
    }
  } catch {
    // Ignore storage failures; the in-memory app state remains authoritative.
  }
}

export function getStoredJson(key, fallback = null) {
  const value = getStoredValue(key, null);
  if (!value) return fallback;
  try {
    return JSON.parse(value);
  } catch {
    return fallback;
  }
}

export function setStoredJson(key, value) {
  setStoredValue(key, JSON.stringify(value));
}
