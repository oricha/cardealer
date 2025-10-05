'use client';

import { ReactNode } from 'react';
import { TemplateHeader } from './TemplateHeader';
import { TemplateFooter } from './TemplateFooter';

interface TemplateLayoutProps {
  children: ReactNode;
}

export function TemplateLayout({ children }: TemplateLayoutProps) {
  return (
    <>
      {/* Preloader */}
      <div className="preloader">
        <div className="loader-ripple">
          <div></div>
          <div></div>
        </div>
      </div>

      {/* Header */}
      <TemplateHeader />

      {/* Main Content */}
      <main>
        {children}
      </main>

      {/* Footer */}
      <TemplateFooter />
    </>
  );
}

