const DEFAULT_API_BASE = '/api';
const RAW_API_BASE = import.meta.env.VITE_API_BASE_URL || DEFAULT_API_BASE;
const API_BASE = RAW_API_BASE.replace(/\/+$/, '') || DEFAULT_API_BASE;
const REQUEST_TIMEOUT_MS = Number(import.meta.env.VITE_API_TIMEOUT_MS || 15000);

export class ApiError extends Error {
  constructor(message, status = 0) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
  }
}

function buildUrl(path) {
  const safePath = String(path || '').startsWith('/') ? path : `/${path}`;
  return `${API_BASE}${safePath}`;
}

function parsePayload(text) {
  if (!text) return null;
  try {
    return JSON.parse(text);
  } catch {
    return { message: text };
  }
}

export async function apiRequest(path, options = {}) {
  const controller = typeof AbortController !== 'undefined' && !options.signal ? new AbortController() : null;
  const timeoutMs = Number.isFinite(REQUEST_TIMEOUT_MS) && REQUEST_TIMEOUT_MS > 0 ? REQUEST_TIMEOUT_MS : 15000;
  const timeoutId = controller ? setTimeout(() => controller.abort(), timeoutMs) : null;

  const hasBody = options.body !== undefined && options.body !== null;
  const headers = {
    Accept: 'application/json',
    ...(hasBody ? { 'Content-Type': 'application/json' } : {}),
    ...(options.headers || {})
  };

  const config = {
    method: 'GET',
    credentials: 'same-origin',
    ...options,
    headers,
    signal: options.signal || controller?.signal
  };

  if (hasBody && typeof config.body !== 'string') {
    config.body = JSON.stringify(config.body);
  }

  let response;
  try {
    response = await fetch(buildUrl(path), config);
  } catch (error) {
    if (error?.name === 'AbortError') {
      throw new ApiError('Request timed out. Check the backend connection and try again.', 0);
    }
    throw new ApiError('Backend is not reachable. Check that the API server is running and accessible.', 0);
  } finally {
    if (timeoutId) clearTimeout(timeoutId);
  }

  const text = await response.text();
  const payload = parsePayload(text);

  if (!response.ok) {
    throw new ApiError(payload?.message || `Request failed with status ${response.status}`, response.status);
  }

  return payload;
}
