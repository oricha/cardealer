'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import { Car } from '@/types/car';
import { carService } from '@/lib/api/cars';
import { FavoriteButton } from '@/components/favorites/FavoriteButton';

interface CarDetailPageProps {
  params: Promise<{
    id: string;
  }>;
}

export default function CarDetailPage({ params }: CarDetailPageProps) {
  const [car, setCar] = useState<Car | null>(null);
  const [similarCars, setSimilarCars] = useState<Car[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isLoadingSimilar, setIsLoadingSimilar] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [currentImageIndex, setCurrentImageIndex] = useState(0);
  const [showAllImages, setShowAllImages] = useState(false);

  const handleContactDealer = () => {
    if (car?.dealer?.email) {
      window.location.href = `mailto:${car.dealer.email}?subject=Inquiry about ${car.make} ${car.model}`;
    }
  };

  useEffect(() => {
    const loadCar = async () => {
      try {
        const { id } = await params;
        const carData = await carService.getCarById(id);
        setCar(carData);

        // Load similar cars
        setIsLoadingSimilar(true);
        try {
          const similar = await carService.getSimilarCars(id);
          setSimilarCars(similar);
        } catch (similarErr) {
          console.error('Failed to load similar cars:', similarErr);
          // Don't set main error for similar cars failure
        } finally {
          setIsLoadingSimilar(false);
        }
      } catch (err: unknown) {
        const errorMessage = err instanceof Error ? err.message : 'Failed to load car details';
        setError(errorMessage);
      } finally {
        setIsLoading(false);
      }
    };

    loadCar();
  }, [params]);

  if (isLoading) {
    return (
      <main className="main">
        <div className="container py-5">
          <div className="text-center">
            <div className="spinner"></div>
            <p>Loading car details...</p>
          </div>
        </div>
      </main>
    );
  }

  if (error || !car) {
    return (
      <main className="main">
        <div className="container py-5">
          <div className="text-center">
            <h2>Car Not Found</h2>
            <p>{error || 'The car you are looking for does not exist.'}</p>
            <Link href="/cars" className="theme-btn">
              Back to Cars
            </Link>
          </div>
        </div>
      </main>
    );
  }

  const images = car?.images || [
    { imageUrl: '/assets/img/car/single-1.jpg', altText: car ? `${car.make} ${car.model}` : 'Car image' }
  ];

  return (
    <main className="main">
      {/* Breadcrumb */}
      <div className="site-breadcrumb" style={{ backgroundImage: 'url(/assets/img/breadcrumb/01.jpg)' }}>
        <div className="container">
          <h2 className="breadcrumb-title">Car Details</h2>
          <ul className="breadcrumb-menu">
            <li><Link href="/">Home</Link></li>
            <li><Link href="/cars">Cars</Link></li>
            <li className="active">{car.make} {car.model}</li>
          </ul>
        </div>
      </div>

      {/* Car Single */}
      <div className="car-item-single bg py-120">
        <div className="container">
          <div className="car-single-wrapper">
            <div className="row">
              <div className="col-lg-8">
                <div className="car-single-details">
                  <div className="car-single-widget">
                    <div className="car-single-top">
                      <span className={`car-status status-${car.condition.toLowerCase()}`}>
                        {car.condition}
                      </span>
                      <h3 className="car-single-title">{car.make} {car.model}</h3>
                      <ul className="car-single-meta">
                        <li>
                          <i className="far fa-clock"></i> 
                          Listed On: {new Date(car.createdAt).toLocaleDateString()}
                        </li>
                        <li>
                          <i className="far fa-eye"></i> 
                          Views: {Math.floor(Math.random() * 1000) + 100}
                        </li>
                      </ul>
                    </div>
                    
                    <div className="car-single-slider">
                       <div className="item-gallery">
                         <div className="flexslider-thumbnails">
                           <div className="main-image-container">
                             <img
                               src={images[currentImageIndex]?.imageUrl}
                               alt={images[currentImageIndex]?.altText || `${car.make} ${car.model}`}
                               className="main-car-image"
                               onClick={() => setShowAllImages(true)}
                               style={{ cursor: 'pointer' }}
                             />
                             {images.length > 1 && (
                               <>
                                 <button
                                   className="image-nav-btn prev-btn"
                                   onClick={() => setCurrentImageIndex(prev =>
                                     prev === 0 ? images.length - 1 : prev - 1
                                   )}
                                 >
                                   <i className="far fa-chevron-left"></i>
                                 </button>
                                 <button
                                   className="image-nav-btn next-btn"
                                   onClick={() => setCurrentImageIndex(prev =>
                                     prev === images.length - 1 ? 0 : prev + 1
                                   )}
                                 >
                                   <i className="far fa-chevron-right"></i>
                                 </button>
                               </>
                             )}
                           </div>
                           {images.length > 1 && (
                             <div className="thumbnail-nav">
                               <ul className="thumbnail-list">
                                 {images.map((image, index) => (
                                   <li key={index}>
                                     <img
                                       src={image.imageUrl}
                                       alt={image.altText || `Thumbnail ${index + 1}`}
                                       className={`thumbnail ${index === currentImageIndex ? 'active' : ''}`}
                                       onClick={() => setCurrentImageIndex(index)}
                                     />
                                   </li>
                                 ))}
                               </ul>
                             </div>
                           )}
                         </div>
                       </div>
                     </div>
                  </div>

                  <div className="car-single-widget">
                    <h4 className="mb-4">Key Information</h4>
                    <div className="car-key-info">
                      <div className="row">
                        <div className="col-lg-3 col-md-4 col-6">
                          <div className="car-key-item">
                            <div className="car-key-icon">
                              <i className="flaticon-drive"></i>
                            </div>
                            <div className="car-key-content">
                              <span>Vehicle Type</span>
                              <h6>{car.vehicleType}</h6>
                            </div>
                          </div>
                        </div>
                        <div className="col-lg-3 col-md-4 col-6">
                          <div className="car-key-item">
                            <div className="car-key-icon">
                              <i className="flaticon-drive"></i>
                            </div>
                            <div className="car-key-content">
                              <span>Condition</span>
                              <h6>{car.condition}</h6>
                            </div>
                          </div>
                        </div>
                        <div className="col-lg-3 col-md-4 col-6">
                          <div className="car-key-item">
                            <div className="car-key-icon">
                              <i className="flaticon-speedometer"></i>
                            </div>
                            <div className="car-key-content">
                              <span>Mileage</span>
                              <h6>{car.mileage?.toLocaleString()} mi</h6>
                            </div>
                          </div>
                        </div>
                        <div className="col-lg-3 col-md-4 col-6">
                          <div className="car-key-item">
                            <div className="car-key-icon">
                              <i className="flaticon-settings"></i>
                            </div>
                            <div className="car-key-content">
                              <span>Transmission</span>
                              <h6>{car.transmission}</h6>
                            </div>
                          </div>
                        </div>
                        <div className="col-lg-3 col-md-4 col-6">
                          <div className="car-key-item">
                            <div className="car-key-icon">
                              <i className="flaticon-drive"></i>
                            </div>
                            <div className="car-key-content">
                              <span>Year</span>
                              <h6>{car.year}</h6>
                            </div>
                          </div>
                        </div>
                        <div className="col-lg-3 col-md-4 col-6">
                          <div className="car-key-item">
                            <div className="car-key-icon">
                              <i className="flaticon-gas-station"></i>
                            </div>
                            <div className="car-key-content">
                              <span>Fuel Type</span>
                              <h6>{car.fuelType}</h6>
                            </div>
                          </div>
                        </div>
                        <div className="col-lg-3 col-md-4 col-6">
                          <div className="car-key-item">
                            <div className="car-key-icon">
                              <i className="flaticon-drive"></i>
                            </div>
                            <div className="car-key-content">
                              <span>Make</span>
                              <h6>{car.make}</h6>
                            </div>
                          </div>
                        </div>
                        <div className="col-lg-3 col-md-4 col-6">
                          <div className="car-key-item">
                            <div className="car-key-icon">
                              <i className="flaticon-drive"></i>
                            </div>
                            <div className="car-key-content">
                              <span>Model</span>
                              <h6>{car.model}</h6>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div className="car-single-widget">
                    <h4 className="mb-4">Description</h4>
                    <div className="car-description">
                      <p>{car.description}</p>
                    </div>
                  </div>

                  {car.features && (
                    <div className="car-single-widget">
                      <h4 className="mb-4">Features</h4>
                      <div className="car-features">
                        <div className="row">
                          <div className="col-md-6">
                            <ul className="feature-list">
                              {car.features.airbags && <li><i className="far fa-check"></i> Airbags</li>}
                              {car.features.absBrakes && <li><i className="far fa-check"></i> ABS Brakes</li>}
                              {car.features.airConditioning && <li><i className="far fa-check"></i> Air Conditioning</li>}
                              {car.features.powerSteering && <li><i className="far fa-check"></i> Power Steering</li>}
                            </ul>
                          </div>
                          <div className="col-md-6">
                            <ul className="feature-list">
                              {car.features.centralLocking && <li><i className="far fa-check"></i> Central Locking</li>}
                              {car.features.electricWindows && <li><i className="far fa-check"></i> Electric Windows</li>}
                            </ul>
                          </div>
                        </div>
                      </div>
                    </div>
                  )}
                </div>
              </div>

              <div className="col-lg-4">
                <div className="car-single-sidebar">
                  <div className="car-single-widget">
                    <div className="car-price">
                      <h3>${car.price.toLocaleString()}</h3>
                      <p>Asking Price</p>
                    </div>
                    <div className="car-single-btn">
                       <button
                         className="theme-btn w-100 mb-3"
                         onClick={handleContactDealer}
                       >
                         <i className="far fa-phone"></i> Contact Dealer
                       </button>
                       <FavoriteButton
                         car={car}
                         variant="button"
                         showText={true}
                       />
                     </div>
                  </div>

                  <div className="car-single-widget">
                    <h4 className="widget-title">Dealer Information</h4>
                    <div className="dealer-info">
                      <div className="dealer-avatar">
                        <img src="/assets/img/dealer/01.png" alt={car.dealer?.name || "Dealer"} />
                      </div>
                      <div className="dealer-content">
                        <h5>{car.dealer?.name || 'Dealer Name'}</h5>
                        <p>Trusted Dealer</p>
                        <div className="dealer-rating">
                          <i className="far fa-star"></i>
                          <i className="far fa-star"></i>
                          <i className="far fa-star"></i>
                          <i className="far fa-star"></i>
                          <i className="far fa-star"></i>
                          <span>(4.5)</span>
                        </div>
                      </div>
                    </div>
                    <div className="dealer-contact">
                      {car.dealer?.phone && (
                        <a href={`tel:${car.dealer.phone}`} className="theme-btn w-100 mb-2">
                          <i className="far fa-phone"></i> Call Now
                        </a>
                      )}
                      {car.dealer?.email && (
                        <a href={`mailto:${car.dealer.email}`} className="theme-btn theme-btn2 w-100">
                          <i className="far fa-envelope"></i> Send Email
                        </a>
                      )}
                    </div>
                  </div>

                  <div className="car-single-widget">
                    <h4 className="widget-title">Share This Car</h4>
                    <div className="social-share">
                      <a href="#" className="social-btn facebook">
                        <i className="fab fa-facebook-f"></i>
                      </a>
                      <a href="#" className="social-btn twitter">
                        <i className="fab fa-twitter"></i>
                      </a>
                      <a href="#" className="social-btn linkedin">
                        <i className="fab fa-linkedin-in"></i>
                      </a>
                      <a href="#" className="social-btn whatsapp">
                        <i className="fab fa-whatsapp"></i>
                      </a>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Related Cars */}
       {similarCars.length > 0 && (
         <div className="related-cars py-120">
           <div className="container">
             <div className="row">
               <div className="col-12">
                 <div className="site-heading text-center">
                   <h2 className="site-title">Similar Cars</h2>
                   <p>You might also like these similar vehicles</p>
                 </div>
               </div>
             </div>
             <div className="row">
               {similarCars.slice(0, 3).map((similarCar) => (
                 <div key={similarCar.id} className="col-lg-4 col-md-6">
                   <div className="car-item">
                     <div className="car-item-img">
                       <Link href={`/cars/${similarCar.id}`}>
                         <img
                           src={similarCar.images?.[0]?.imageUrl || '/assets/img/car/01.jpg'}
                           alt={`${similarCar.make} ${similarCar.model}`}
                         />
                       </Link>
                     </div>
                     <div className="car-item-content">
                       <h4>
                         <Link href={`/cars/${similarCar.id}`}>
                           {similarCar.make} {similarCar.model}
                         </Link>
                       </h4>
                       <p>
                         {similarCar.condition} • {similarCar.mileage?.toLocaleString() || 'N/A'} mi • {similarCar.transmission}
                       </p>
                       <div className="car-price">${similarCar.price.toLocaleString()}</div>
                     </div>
                   </div>
                 </div>
               ))}
             </div>
           </div>
         </div>
       )}

       {/* Image Modal */}
       {showAllImages && (
         <div className="image-modal-overlay" onClick={() => setShowAllImages(false)}>
           <div className="image-modal" onClick={(e) => e.stopPropagation()}>
             <button className="modal-close-btn" onClick={() => setShowAllImages(false)}>
               <i className="far fa-times"></i>
             </button>
             <div className="modal-image-container">
               <img
                 src={images[currentImageIndex]?.imageUrl}
                 alt={images[currentImageIndex]?.altText || `${car?.make} ${car?.model}`}
                 className="modal-image"
               />
             </div>
             {images.length > 1 && (
               <div className="modal-nav">
                 <button
                   className="modal-nav-btn prev"
                   onClick={() => setCurrentImageIndex(prev =>
                     prev === 0 ? images.length - 1 : prev - 1
                   )}
                 >
                   <i className="far fa-chevron-left"></i>
                 </button>
                 <button
                   className="modal-nav-btn next"
                   onClick={() => setCurrentImageIndex(prev =>
                     prev === images.length - 1 ? 0 : prev + 1
                   )}
                 >
                   <i className="far fa-chevron-right"></i>
                 </button>
               </div>
             )}
             <div className="modal-counter">
               {currentImageIndex + 1} / {images.length}
             </div>
           </div>
         </div>
       )}
     </main>
   );
 }