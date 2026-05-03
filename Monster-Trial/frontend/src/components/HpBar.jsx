export default function HpBar({ current = 0, max = 1, label = 'HP' }) {
  const percent = Math.max(0, Math.min(100, (current / Math.max(1, max)) * 100));
  return (
    <div className="bar-block">
      <div className="bar-label"><span>{label}</span><span>{current} / {max}</span></div>
      <div className="meter hp-meter"><div style={{ width: `${percent}%` }} /></div>
    </div>
  );
}
