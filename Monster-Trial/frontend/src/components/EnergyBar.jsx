export default function EnergyBar({ current = 0, max = 1 }) {
  const percent = Math.max(0, Math.min(100, (current / Math.max(1, max)) * 100));
  return (
    <div className="bar-block">
      <div className="bar-label"><span>Energy</span><span>{current} / {max}</span></div>
      <div className="meter energy-meter"><div style={{ width: `${percent}%` }} /></div>
    </div>
  );
}
