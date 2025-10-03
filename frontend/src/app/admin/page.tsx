export default function AdminPage() {
  return (
    <div className="container mx-auto px-4 py-8">
      <div className="space-y-8">
        <div className="text-center space-y-4">
          <h1 className="text-3xl lg:text-4xl font-bold">Admin Dashboard</h1>
          <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
            Manage users, monitor system performance, and oversee platform operations
          </p>
        </div>

        {/* Admin content will go here */}
        <div className="bg-muted/50 rounded-lg p-8 text-center">
          <p className="text-muted-foreground">Admin interface functionality coming soon...</p>
        </div>
      </div>
    </div>
  );
}