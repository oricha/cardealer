import type { Metadata } from "next";
import "./globals.css";
import { TemplateLayout } from "@/components/layout/TemplateLayout";
import { Toaster } from "@/components/ui/Toaster";
import { AuthProvider } from "@/contexts/AuthContext";
import { FavoritesProvider } from "@/contexts/FavoritesContext";
import { LanguageProvider } from "@/contexts/LanguageContext";

export const metadata: Metadata = {
  title: "Crashed Car Sales - Quality Salvage Vehicles",
  description: "Find quality crashed and salvage vehicles from trusted dealers. Browse our extensive inventory of repairable cars, trucks, and motorcycles.",
  keywords: "crashed cars, salvage vehicles, repairable cars, damaged cars for sale, auto salvage",
  authors: [{ name: "Crashed Car Sales" }],
  creator: "Crashed Car Sales",
  publisher: "Crashed Car Sales",
  formatDetection: {
    email: false,
    address: false,
    telephone: false,
  },
  metadataBase: new URL(process.env.NEXT_PUBLIC_APP_URL || 'http://localhost:3000'),
  openGraph: {
    title: "Crashed Car Sales - Quality Salvage Vehicles",
    description: "Find quality crashed and salvage vehicles from trusted dealers.",
    url: "/",
    siteName: "Crashed Car Sales",
    locale: "en_US",
    type: "website",
  },
  twitter: {
    card: "summary_large_image",
    title: "Crashed Car Sales - Quality Salvage Vehicles",
    description: "Find quality crashed and salvage vehicles from trusted dealers.",
  },
  robots: {
    index: true,
    follow: true,
    googleBot: {
      index: true,
      follow: true,
      "max-video-preview": -1,
      "max-image-preview": "large",
      "max-snippet": -1,
    },
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <head>
        <link rel="icon" type="image/x-icon" href="/assets/img/logo/favicon.png" />
        {/* Template CSS */}
        <link rel="stylesheet" href="/assets/css/bootstrap.min.css" />
        <link rel="stylesheet" href="/assets/css/all-fontawesome.min.css" />
        <link rel="stylesheet" href="/assets/css/flaticon.css" />
        <link rel="stylesheet" href="/assets/css/animate.min.css" />
        <link rel="stylesheet" href="/assets/css/magnific-popup.min.css" />
        <link rel="stylesheet" href="/assets/css/owl.carousel.min.css" />
        <link rel="stylesheet" href="/assets/css/jquery-ui.min.css" />
        <link rel="stylesheet" href="/assets/css/nice-select.min.css" />
        <link rel="stylesheet" href="/assets/css/style.css" />
      </head>
      <body>
        <AuthProvider>
          <LanguageProvider>
            <FavoritesProvider>
              <TemplateLayout>
                {children}
              </TemplateLayout>
            </FavoritesProvider>
          </LanguageProvider>
        </AuthProvider>
        <Toaster />
        
        {/* Template Scripts */}
        <script src="/assets/js/jquery-3.6.0.min.js"></script>
        <script src="/assets/js/bootstrap.bundle.min.js"></script>
        <script src="/assets/js/jquery-ui.min.js"></script>
        <script src="/assets/js/jquery.easing.min.js"></script>
        <script src="/assets/js/jquery.nice-select.min.js"></script>
        <script src="/assets/js/jquery.magnific-popup.min.js"></script>
        <script src="/assets/js/owl.carousel.min.js"></script>
        <script src="/assets/js/flex-slider.js"></script>
        <script src="/assets/js/counter-up.js"></script>
        <script src="/assets/js/jquery.appear.min.js"></script>
        <script src="/assets/js/wow.min.js"></script>
        <script src="/assets/js/main.js"></script>
      </body>
    </html>
  );
}
