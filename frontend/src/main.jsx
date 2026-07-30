import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client'; //React'ın HTML sayfasına bağlanmasını sağlar.

import './index.css';


import App from './App.jsx';

createRoot(document.getElementById('root')).render(
    <StrictMode>
        <App />
    </StrictMode>
);
