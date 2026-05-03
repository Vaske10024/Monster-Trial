import { useGame } from './state/useGame.js';
import StartPage from './pages/StartPage.jsx';
import CharacterBuilderPage from './pages/CharacterBuilderPage.jsx';
import BossPreviewPage from './pages/BossPreviewPage.jsx';
import ShopPage from './pages/ShopPage.jsx';
import EquipSkillsPage from './pages/EquipSkillsPage.jsx';
import BattlePage from './pages/BattlePage.jsx';
import VictoryPage from './pages/VictoryPage.jsx';
import DefeatPage from './pages/DefeatPage.jsx';
import RunCompletePage from './pages/RunCompletePage.jsx';
import BossRoadmap from './components/BossRoadmap.jsx';
import AudioControls from './components/AudioControls.jsx';

export default function App() {
  const { run, view, loading, error, actions, characterProfile } = useGame();

  let content;
  if (view === 'builder') {
    content = <CharacterBuilderPage initialProfile={characterProfile} actions={actions} loading={loading} />;
  } else if (!run || view === 'start') {
    content = <StartPage onStart={actions.beginCharacterCreate} characterProfile={characterProfile} />;
  } else if (view === 'shop') {
    content = <ShopPage run={run} actions={actions} />;
  } else if (view === 'equip') {
    content = <EquipSkillsPage run={run} actions={actions} />;
  } else if (view === 'battle') {
    content = <BattlePage run={run} actions={actions} loading={loading} characterProfile={characterProfile} />;
  } else if (view === 'victory') {
    content = <VictoryPage run={run} actions={actions} characterProfile={characterProfile} />;
  } else if (view === 'defeat') {
    content = <DefeatPage run={run} actions={actions} characterProfile={characterProfile} />;
  } else if (view === 'complete') {
    content = <RunCompletePage run={run} actions={actions} characterProfile={characterProfile} />;
  } else {
    content = <BossPreviewPage run={run} actions={actions} characterProfile={characterProfile} />;
  }

  return (
    <div className="app-shell">
      {error && (
        <div className="error-toast">
          <span>{error}</span>
          <button onClick={actions.clearError} aria-label="Dismiss error">×</button>
        </div>
      )}
      {loading && <div className="loading-strip">Loading...</div>}
      <AudioControls view={view} run={run} />
      {content}
      {run && view !== 'start' && view !== 'builder' && (
        <BossRoadmap run={run} actions={actions} />
      )}
    </div>
  );
}
