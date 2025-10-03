export default function AboutPage() {
  return (
    <div className="container mx-auto px-4 py-8">
      <div className="max-w-4xl mx-auto space-y-12">
        <div className="text-center space-y-4">
          <h1 className="text-3xl lg:text-4xl font-bold">About Us</h1>
          <p className="text-lg text-muted-foreground">
            Learn more about our mission to connect buyers with quality salvage vehicles
          </p>
        </div>

        <div className="grid lg:grid-cols-2 gap-12 items-center">
          <div className="space-y-6">
            <h2 className="text-2xl font-semibold">Our Mission</h2>
            <p className="text-muted-foreground">
              We believe that every salvage vehicle deserves a second chance. Our platform connects
              buyers with trusted dealers to find quality repairable vehicles at fair prices.
            </p>
            <p className="text-muted-foreground">
              Whether you're a professional mechanic, a hobbyist restorer, or someone looking for
              an affordable project car, we're here to help you find the perfect vehicle.
            </p>
          </div>

          <div className="space-y-6">
            <h2 className="text-2xl font-semibold">Why Choose Us?</h2>
            <ul className="space-y-3 text-muted-foreground">
              <li>• Verified dealers and quality vehicles</li>
              <li>• Transparent pricing and condition reports</li>
              <li>• Secure transaction process</li>
              <li>• Expert support and guidance</li>
              <li>• Extensive inventory from across the country</li>
            </ul>
          </div>
        </div>

        <div className="text-center space-y-6">
          <h2 className="text-2xl font-semibold">Get Started Today</h2>
          <p className="text-muted-foreground max-w-2xl mx-auto">
            Join thousands of satisfied customers who found their perfect salvage vehicle through our platform.
          </p>
        </div>
      </div>
    </div>
  );
}