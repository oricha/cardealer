import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";
import { Navigation } from "@/components/layout/Navigation";
import { Footer } from "@/components/layout/Footer";
import { Toaster } from "@/components/ui/Toaster";

const inter = Inter({
  subsets: ["latin"],
  variable: "--font-inter",
});

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
    <html lang="en" className="h-full">
      <body className={`${inter.variable} font-sans antialiased min-h-full bg-background`}>
        <div className="relative flex min-h-screen flex-col">
          <Navigation />
          <main className="flex-1">
            {children}
          </main>
          <Footer />
        </div>
        <Toaster />
      </body>
    </html>
  );
}
