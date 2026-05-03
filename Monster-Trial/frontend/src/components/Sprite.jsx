import { useEffect, useState } from 'react';
import PlaceholderSprite from './PlaceholderSprite.jsx';

export default function Sprite({ src, alt, fallbackLabel, className = '', size = 96 }) {
  const [failed, setFailed] = useState(false);

  useEffect(() => {
    setFailed(false);
  }, [src]);

  if (!src || failed) {
    return <PlaceholderSprite label={fallbackLabel || alt} width={size} height={size} className={className} />;
  }

  return (
    <img
      className={`sprite-img ${className}`}
      src={src}
      alt={alt}
      width={size}
      height={size}
      style={{ width: `${size}px`, height: `${size}px` }}
      onError={() => setFailed(true)}
    />
  );
}
