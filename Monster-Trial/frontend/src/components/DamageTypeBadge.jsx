import { safeClassSuffix } from '../utils/cssClass.js';

export default function DamageTypeBadge({ type }) {
  const label = type || 'None';
  return <span className={`damage-badge type-${safeClassSuffix(label)}`}>{label}</span>;
}
