'use client';

import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { useRouter } from 'next/navigation';

interface LanguageContextType {
  currentLanguage: string;
  setLanguage: (lang: string) => void;
  t: (key: string, params?: Record<string, string | number>) => string;
  formatCurrency: (amount: number, locale?: string) => string;
  formatDate: (date: Date | string, locale?: string) => string;
  formatNumber: (num: number, locale?: string) => string;
}

const LanguageContext = createContext<LanguageContextType | undefined>(undefined);

interface LanguageProviderProps {
  children: ReactNode;
  initialLanguage?: string;
}

export function LanguageProvider({ children, initialLanguage }: LanguageProviderProps) {
  const [currentLanguage, setCurrentLanguage] = useState(initialLanguage || 'en');
  const [translations, setTranslations] = useState<Record<string, unknown>>({});
  const router = useRouter();

  // Load translations for current language
  useEffect(() => {
    loadTranslations(currentLanguage);
  }, [currentLanguage]);

  // Load language preference from localStorage on mount
  useEffect(() => {
    const savedLanguage = localStorage.getItem('language');
    if (savedLanguage && ['en', 'es'].includes(savedLanguage)) {
      setCurrentLanguage(savedLanguage);
    }
  }, []);

  const loadTranslations = async (lang: string) => {
    try {
      const response = await fetch(`/locales/${lang}/common.json`);
      const data = await response.json();
      setTranslations(data);
    } catch (error) {
      console.error(`Failed to load translations for ${lang}:`, error);
      // Fallback to English if translation loading fails
      if (lang !== 'en') {
        loadTranslations('en');
      }
    }
  };

  const setLanguage = (lang: string) => {
    if (['en', 'es'].includes(lang)) {
      setCurrentLanguage(lang);
      localStorage.setItem('language', lang);

      // Update URL locale if using Next.js i18n
      if (typeof window !== 'undefined') {
        const currentPath = window.location.pathname;
        const newPath = currentPath.startsWith('/es') || currentPath.startsWith('/en')
          ? `/${lang}${currentPath.substring(3)}`
          : `/${lang}${currentPath}`;

        router.push(newPath);
      }
    }
  };

  // Translation function with parameter interpolation
  const t = (key: string, params?: Record<string, string | number>): string => {
    const keys = key.split('.');
    let value: unknown = translations;

    // Navigate through nested object structure
    for (const k of keys) {
      if (value && typeof value === 'object' && k in value) {
        value = (value as Record<string, unknown>)[k];
      } else {
        value = undefined;
        break;
      }
    }

    if (typeof value !== 'string') {
      console.warn(`Translation key "${key}" not found`);
      return key;
    }

    // Replace parameters in the translation
    if (params) {
      return value.replace(/\{\{(\w+)\}\}/g, (match: string, paramKey: string) => {
        return params[paramKey]?.toString() || match;
      });
    }

    return value;
  };

  // Format currency based on locale
  const formatCurrency = (amount: number, locale?: string): string => {
    const localeToUse = locale || currentLanguage;

    try {
      return new Intl.NumberFormat(`${localeToUse}-${localeToUse.toUpperCase()}`, {
        style: 'currency',
        currency: localeToUse === 'es' ? 'EUR' : 'USD',
        minimumFractionDigits: 0,
        maximumFractionDigits: 0,
      }).format(amount);
    } catch (error) {
      // Fallback to simple formatting
      return `${localeToUse === 'es' ? '€' : '$'}${amount.toLocaleString()}`;
    }
  };

  // Format date based on locale
  const formatDate = (date: Date | string, locale?: string): string => {
    const localeToUse = locale || currentLanguage;
    const dateObj = typeof date === 'string' ? new Date(date) : date;

    try {
      return new Intl.DateTimeFormat(`${localeToUse}-${localeToUse.toUpperCase()}`, {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
      }).format(dateObj);
    } catch (error) {
      // Fallback to simple formatting
      return dateObj.toLocaleDateString();
    }
  };

  // Format number based on locale
  const formatNumber = (num: number, locale?: string): string => {
    const localeToUse = locale || currentLanguage;

    try {
      return new Intl.NumberFormat(`${localeToUse}-${localeToUse.toUpperCase()}`).format(num);
    } catch (error) {
      // Fallback to simple formatting
      return num.toLocaleString();
    }
  };

  const value: LanguageContextType = {
    currentLanguage,
    setLanguage,
    t,
    formatCurrency,
    formatDate,
    formatNumber,
  };

  return (
    <LanguageContext.Provider value={value}>
      {children}
    </LanguageContext.Provider>
  );
}

export function useLanguage() {
  const context = useContext(LanguageContext);
  if (context === undefined) {
    throw new Error('useLanguage must be used within a LanguageProvider');
  }
  return context;
}