import { useEffect, useMemo, useState } from 'react';
import SkillCard from '../components/SkillCard.jsx';
import PlayAgainBossPanel from '../components/PlayAgainBossPanel.jsx';

export default function EquipSkillsPage({ run, actions }) {
  const [selected, setSelected] = useState(run.hero.equippedSkillIds);
  const [message, setMessage] = useState('');

  useEffect(() => {
    setSelected(run.hero.equippedSkillIds);
  }, [run.hero.equippedSkillIds]);

  const selectedSkillMap = useMemo(() => new Set(selected), [selected]);
  const hasOffensive = run.hero.learnedSkills.some((skill) => selectedSkillMap.has(skill.id) && skill.offensive);

  function toggleSkill(skillId) {
    setMessage('');
    setSelected((current) => {
      if (current.includes(skillId)) {
        return current.filter((id) => id !== skillId);
      }
      if (current.length >= run.hero.maxEquippedSkills) {
        setMessage(`You can equip only ${run.hero.maxEquippedSkills} skills.`);
        return current;
      }
      return [...current, skillId];
    });
  }

  async function saveOnly() {
    const updated = await actions.equipSkills(selected);
    if (updated) {
      setMessage('Equipped skills saved.');
    }
  }

  async function saveAndStart() {
    if (!hasOffensive) {
      setMessage('Equip at least one offensive skill before battle.');
      return;
    }
    const saved = await actions.equipSkills(selected);
    if (!saved) return;
    await actions.startBattle();
  }

  return (
    <main className="page equip-page">
      <section className="page-header">
        <p className="eyebrow">Loadout</p>
        <h1>Equip Skills</h1>
        <p>Choose up to {run.hero.maxEquippedSkills} learned skills. Equipped skills persist until changed.</p>
      </section>

      <section className="panel">
        <div className="section-title-row">
          <h2>Available Skills</h2>
          <span>{selected.length} / {run.hero.maxEquippedSkills} equipped</span>
        </div>
        {message && <div className="info-banner">{message}</div>}
        {!hasOffensive && <div className="warning-banner">At least one offensive skill is required to start battle.</div>}
        <div className="skill-grid">
          {run.hero.learnedSkills.map((skill) => (
            <SkillCard
              key={skill.id}
              skill={skill}
              selected={selectedSkillMap.has(skill.id)}
              equipped={selectedSkillMap.has(skill.id)}
              actionLabel={selectedSkillMap.has(skill.id) ? 'Unequip' : 'Equip'}
              onClick={() => toggleSkill(skill.id)}
              tooltipContext={{ boss: run.currentBoss }}
            />
          ))}
        </div>
      </section>

      <PlayAgainBossPanel run={run} actions={actions} />

      <div className="action-row sticky-actions">
        <button className="secondary-btn" onClick={actions.goBack}>Back</button>
        <button className="secondary-btn" onClick={saveOnly}>Save Loadout</button>
        <button className="primary-btn" disabled={!hasOffensive || selected.length === 0} onClick={saveAndStart}>Save and Start Battle</button>
      </div>
    </main>
  );
}
