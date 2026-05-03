export function safeClassSuffix(value, fallback = 'none') {
  const normalized = String(value || fallback)
    .trim()
    .toLowerCase()
    .replace(/[^a-z0-9_-]+/g, '-');
  return normalized || fallback;
}
