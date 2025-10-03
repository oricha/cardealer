import Link from "next/link";
import { Button } from "@/components/ui/Button";
import { Car, Shield, TrendingUp, Users } from "lucide-react";

export default function Home() {
  return (
    <div className="flex flex-col">
      {/* Hero Section */}
      <section className="relative bg-gradient-to-br from-primary/10 via-background to-secondary/10 py-20 lg:py-32">
        <div className="container mx-auto px-4">
          <div className="grid lg:grid-cols-2 gap-12 items-center">
            <div className="space-y-8">
              <div className="space-y-4">
                <h1 className="text-4xl lg:text-6xl font-bold tracking-tight">
                  Quality <span className="text-primary">Salvage</span> Vehicles
                </h1>
                <p className="text-lg lg:text-xl text-muted-foreground max-w-lg">
                  Find premium crashed and repairable cars from trusted dealers.
                  Start your next project with confidence.
                </p>
              </div>

              <div className="flex flex-col sm:flex-row gap-4">
                <Button size="lg" asChild>
                  <Link href="/cars">
                    Browse Inventory
                  </Link>
                </Button>
                <Button variant="outline" size="lg" asChild>
                  <Link href="/dealers">
                    Find Dealers
                  </Link>
                </Button>
              </div>

              <div className="grid grid-cols-3 gap-8 pt-8">
                <div className="text-center">
                  <div className="text-2xl lg:text-3xl font-bold text-primary">1000+</div>
                  <div className="text-sm text-muted-foreground">Vehicles</div>
                </div>
                <div className="text-center">
                  <div className="text-2xl lg:text-3xl font-bold text-primary">500+</div>
                  <div className="text-sm text-muted-foreground">Dealers</div>
                </div>
                <div className="text-center">
                  <div className="text-2xl lg:text-3xl font-bold text-primary">50+</div>
                  <div className="text-sm text-muted-foreground">Cities</div>
                </div>
              </div>
            </div>

            <div className="relative">
              <div className="aspect-square bg-gradient-to-br from-primary/20 to-secondary/20 rounded-2xl flex items-center justify-center">
                <Car className="h-32 w-32 text-primary" />
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-20 lg:py-32">
        <div className="container mx-auto px-4">
          <div className="text-center space-y-4 mb-16">
            <h2 className="text-3xl lg:text-4xl font-bold">Why Choose Us</h2>
            <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
              We connect you with verified dealers offering quality salvage vehicles
              with transparent pricing and detailed condition reports.
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-8">
            <div className="text-center space-y-4">
              <div className="mx-auto w-16 h-16 bg-primary/10 rounded-full flex items-center justify-center">
                <Shield className="h-8 w-8 text-primary" />
              </div>
              <h3 className="text-xl font-semibold">Verified Dealers</h3>
              <p className="text-muted-foreground">
                All dealers are thoroughly vetted to ensure quality and reliability.
              </p>
            </div>

            <div className="text-center space-y-4">
              <div className="mx-auto w-16 h-16 bg-primary/10 rounded-full flex items-center justify-center">
                <TrendingUp className="h-8 w-8 text-primary" />
              </div>
              <h3 className="text-xl font-semibold">Best Prices</h3>
              <p className="text-muted-foreground">
                Competitive pricing on salvage vehicles from across the country.
              </p>
            </div>

            <div className="text-center space-y-4">
              <div className="mx-auto w-16 h-16 bg-primary/10 rounded-full flex items-center justify-center">
                <Users className="h-8 w-8 text-primary" />
              </div>
              <h3 className="text-xl font-semibold">Expert Support</h3>
              <p className="text-muted-foreground">
                Get help from our team of automotive experts throughout your purchase.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="bg-primary py-20 lg:py-32">
        <div className="container mx-auto px-4 text-center">
          <div className="max-w-2xl mx-auto space-y-8">
            <h2 className="text-3xl lg:text-4xl font-bold text-primary-foreground">
              Ready to Find Your Next Project?
            </h2>
            <p className="text-lg text-primary-foreground/80">
              Join thousands of satisfied customers who found their perfect salvage vehicle.
            </p>
            <Button size="lg" variant="secondary" asChild>
              <Link href="/cars">
                Start Browsing
              </Link>
            </Button>
          </div>
        </div>
      </section>
    </div>
  );
}
