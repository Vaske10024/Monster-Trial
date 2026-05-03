import { useEffect, useMemo, useState } from 'react';
import PlaceholderSprite from './PlaceholderSprite.jsx';

const imageLoadCache = new Map();
const loadedImageSources = new Set();

function clampPositiveNumber(value, fallback) {
  const numeric = Number(value);
  return Number.isFinite(numeric) && numeric > 0 ? numeric : fallback;
}

function loadImage(src) {
  if (!src) {
    return Promise.reject(new Error('Missing image source'));
  }
  if (imageLoadCache.has(src)) {
    return imageLoadCache.get(src);
  }
  const promise = new Promise((resolve, reject) => {
    const image = new Image();
    image.onload = () => {
      loadedImageSources.add(src);
      resolve(image);
    };
    image.onerror = () => reject(new Error(`Failed to load image: ${src}`));
    image.src = src;
  });
  imageLoadCache.set(src, promise);
  return promise;
}

export function preloadSprite(src) {
  return loadImage(src).catch(() => null);
}

export default function AnimatedSprite({
  src,
  fallbackLabel = 'Sprite',
  frameWidth = 256,
  frameHeight = 256,
  frameCount = 1,
  fps = 8,
  loop = true,
  scale = 1,
  className = ''
}) {
  const [loaded, setLoaded] = useState(() => loadedImageSources.has(src));
  const [failed, setFailed] = useState(false);
  const [frame, setFrame] = useState(0);

  const safe = useMemo(() => ({
    frameWidth: clampPositiveNumber(frameWidth, 256),
    frameHeight: clampPositiveNumber(frameHeight, 256),
    frameCount: Math.max(1, Math.floor(clampPositiveNumber(frameCount, 1))),
    fps: clampPositiveNumber(fps, 8),
    scale: clampPositiveNumber(scale, 1)
  }), [frameWidth, frameHeight, frameCount, fps, scale]);

  useEffect(() => {
    setFrame(0);
    setLoaded(loadedImageSources.has(src));
    setFailed(false);
    if (!src) {
      setFailed(true);
      return undefined;
    }
    let cancelled = false;
    loadImage(src)
      .then(() => {
        if (!cancelled) setLoaded(true);
      })
      .catch(() => {
        if (!cancelled) setFailed(true);
      });
    return () => {
      cancelled = true;
    };
  }, [src]);

  useEffect(() => {
    if (!(loaded || loadedImageSources.has(src)) || failed || safe.frameCount <= 1) return undefined;
    const interval = window.setInterval(() => {
      setFrame((current) => {
        if (current >= safe.frameCount - 1) {
          return loop ? 0 : current;
        }
        return current + 1;
      });
    }, Math.max(16, 1000 / safe.fps));
    return () => window.clearInterval(interval);
  }, [loaded, failed, safe.frameCount, safe.fps, loop, src]);

  const isReady = loaded || loadedImageSources.has(src);

  if (!isReady || failed) {
    return <PlaceholderSprite label={fallbackLabel} width={safe.frameWidth} height={safe.frameHeight} scale={safe.scale} className={className} />;
  }

  const style = {
    width: `${safe.frameWidth * safe.scale}px`,
    height: `${safe.frameHeight * safe.scale}px`,
    backgroundImage: `url(${src})`,
    backgroundPosition: `-${frame * safe.frameWidth * safe.scale}px 0px`,
    backgroundSize: `${safe.frameWidth * safe.frameCount * safe.scale}px ${safe.frameHeight * safe.scale}px`
  };

  return <div className={`animated-sprite ${className}`} style={style} role="img" aria-label={fallbackLabel} />;
}
