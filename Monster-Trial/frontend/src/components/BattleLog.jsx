export default function BattleLog({ entries = [] }) {
  const visibleEntries = entries.slice(-18).reverse();
  return (
    <section className="battle-log panel">
      <h2>Battle Log</h2>
      <div className="log-list">
        {visibleEntries.length === 0 && <p>No actions yet.</p>}
        {visibleEntries.map((entry, index) => (
          <p key={`${entry}-${index}`}>{entry}</p>
        ))}
      </div>
    </section>
  );
}
