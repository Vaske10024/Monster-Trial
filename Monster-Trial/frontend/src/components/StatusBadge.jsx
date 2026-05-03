import { safeClassSuffix } from '../utils/cssClass.js';

export default function StatusBadge({ status }) {
  if (!status) return null;
  const stacks = status.stacks && status.stacks > 1 ? ` x${status.stacks}` : '';
  const name = status.name || status.id || 'Status';
  const remainingTurns = Number.isFinite(status.remainingTurns) ? status.remainingTurns : 0;

  return (
    <span className={`status-badge status-${safeClassSuffix(status.id)}`} title={`${name}: ${remainingTurns}${stacks}`}>
      {name}{stacks} <small>{remainingTurns}</small>
    </span>
  );
}
