'use client';

import { useLanguage } from '@/contexts/LanguageContext';

interface LanguageSwitcherProps {
  variant?: 'dropdown' | 'buttons';
  className?: string;
}

export function LanguageSwitcher({ variant = 'dropdown', className = '' }: LanguageSwitcherProps) {
  const { currentLanguage, setLanguage, t } = useLanguage();

  const languages = [
    { code: 'en', name: 'English', flag: '🇺🇸' },
    { code: 'es', name: 'Español', flag: '🇪🇸' },
  ];

  if (variant === 'buttons') {
    return (
      <div className={`language-buttons ${className}`}>
        {languages.map((lang) => (
          <button
            key={lang.code}
            onClick={() => setLanguage(lang.code)}
            className={`language-btn ${currentLanguage === lang.code ? 'active' : ''}`}
            title={lang.name}
          >
            <span className="language-flag">{lang.flag}</span>
            <span className="language-code">{lang.code.toUpperCase()}</span>
          </button>
        ))}
      </div>
    );
  }

  // Dropdown variant (default)
  return (
    <div className={`language-dropdown ${className}`}>
      <select
        value={currentLanguage}
        onChange={(e) => setLanguage(e.target.value)}
        className="language-select"
      >
        {languages.map((lang) => (
          <option key={lang.code} value={lang.code}>
            {lang.flag} {lang.name}
          </option>
        ))}
      </select>
    </div>
  );
}