import React from 'react';

export default class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError() {
    return { hasError: true };
  }

  componentDidCatch(error, info) {
    if (import.meta.env.DEV) {
      console.error('Uncaught UI error:', error, info);
    }
  }

  render() {
    if (this.state.hasError) {
      return (
        <main className="page fatal-error-page">
          <section className="panel empty-state fatal-error-card">
            <p className="eyebrow">Unexpected UI error</p>
            <h1>Monster-Trial needs a refresh</h1>
            <p>The app state is safe. Refresh the page and continue your run from the saved run id.</p>
            <button className="primary-btn" onClick={() => window.location.reload()}>Refresh app</button>
          </section>
        </main>
      );
    }

    return this.props.children;
  }
}
