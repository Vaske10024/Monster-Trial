export default function PlaceholderSprite({ label = 'Missing Asset', width = 160, height = 160, scale = 1, className = '' }) {
  const style = {
    width: `${width * scale}px`,
    height: `${height * scale}px`
  };

  return (
    <div className={`placeholder-sprite ${className}`} style={style} role="img" aria-label={label}>
      <div className="placeholder-content">
        <div className="placeholder-rune">✦</div>
        <span>{label}</span>
      </div>
    </div>
  );
}
