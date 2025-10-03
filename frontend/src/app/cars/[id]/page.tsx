interface CarDetailPageProps {
  params: {
    id: string;
  };
}

export default function CarDetailPage({ params }: CarDetailPageProps) {
  return (
    <div className="container mx-auto px-4 py-8">
      <div className="space-y-8">
        <div className="text-center space-y-4">
          <h1 className="text-3xl lg:text-4xl font-bold">Car Details</h1>
          <p className="text-lg text-muted-foreground">
            Detailed information for car ID: {params.id}
          </p>
        </div>

        {/* Car details will go here */}
        <div className="bg-muted/50 rounded-lg p-8 text-center">
          <p className="text-muted-foreground">Car detail page functionality coming soon...</p>
        </div>
      </div>
    </div>
  );
}