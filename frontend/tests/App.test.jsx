import { render, screen } from '@testing-library/react';
import App from '../src/App.jsx';

test('renders URL Shortener heading', () => {
  render(<App />);
  const headingElement = screen.getByText(/URL Shortener/i);
  expect(headingElement).toBeInTheDocument();
});
