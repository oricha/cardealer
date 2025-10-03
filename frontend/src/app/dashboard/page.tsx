export default function DashboardPage() {
  return (
    <div className="container mx-auto px-4 py-8">
      <div className="space-y-8">
        <div className="text-center space-y-4">
          <h1 className="text-3xl lg:text-4xl font-bold">Dealer Dashboard</h1>
          <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
            Manage your inventory, track sales, and grow your business
          </p>
        </div>

        {/* Dashboard content will go here */}
        <div className="bg-muted/50 rounded-lg p-8 text-center">
          <p className="text-muted-foreground">Dealer dashboard functionality coming soon...</p>
        </div>
      </div>
    </div>
  );
}