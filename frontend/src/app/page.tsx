'use client';

import Link from 'next/link';
import { useState } from 'react';

export default function HomePage() {
  const [searchForm, setSearchForm] = useState({
    condition: '',
    brand: '',
    model: '',
    priceRange: '',
    location: ''
  });

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    // Handle search logic here
    console.log('Searching for:', searchForm);
  };

  return (
    <main className="main">
      {/* Hero Slider */}
      <div className="hero-section">
        <div className="hero-slider owl-carousel owl-theme">
          <div className="hero-single" style={{ backgroundImage: 'url(/assets/img/slider/slider-1.jpg)' }}>
            <div className="container">
              <div className="row align-items-center">
                <div className="col-md-12 col-lg-6">
                  <div className="hero-content">
                    <h6 className="hero-sub-title" data-animation="fadeInUp" data-delay=".25s">
                      Welcome To Crashed Car Sales!
                    </h6>
                    <h1 className="hero-title" data-animation="fadeInRight" data-delay=".50s">
                      Best Way To Find Your <span>Dream</span> Car
                    </h1>
                    <p data-animation="fadeInLeft" data-delay=".75s">
                      Discover quality crashed and salvage vehicles from trusted dealers nationwide. 
                      Find the perfect repairable car that fits your budget and needs.
                    </p>
                    <div className="hero-btn" data-animation="fadeInUp" data-delay="1s">
                      <Link href="/cars" className="theme-btn">
                        Browse Cars<i className="fas fa-arrow-right-long"></i>
                      </Link>
                      <Link href="/about" className="theme-btn theme-btn2">
                        Learn More<i className="fas fa-arrow-right-long"></i>
                      </Link>
                    </div>
                  </div>
                </div>
                <div className="col-md-12 col-lg-6">
                  <div className="hero-right">
                    <div className="hero-img">
                      <img src="/assets/img/slider/hero-1.png" alt="Hero Car" data-animation="fadeInRight" data-delay=".25s" />
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <div className="hero-single" style={{ backgroundImage: 'url(/assets/img/slider/slider-2.jpg)' }}>
            <div className="container">
              <div className="row align-items-center">
                <div className="col-md-12 col-lg-6">
                  <div className="hero-content">
                    <h6 className="hero-sub-title" data-animation="fadeInUp" data-delay=".25s">
                      Quality Salvage Vehicles
                    </h6>
                    <h1 className="hero-title" data-animation="fadeInRight" data-delay=".50s">
                      Find Your Perfect <span>Project</span> Car
                    </h1>
                    <p data-animation="fadeInLeft" data-delay=".75s">
                      From minor damage to major projects, we have vehicles for every skill level 
                      and budget. Start your restoration journey today.
                    </p>
                    <div className="hero-btn" data-animation="fadeInUp" data-delay="1s">
                      <Link href="/cars" className="theme-btn">
                        View Inventory<i className="fas fa-arrow-right-long"></i>
                      </Link>
                      <Link href="/dealers" className="theme-btn theme-btn2">
                        Find Dealers<i className="fas fa-arrow-right-long"></i>
                      </Link>
                    </div>
                  </div>
                </div>
                <div className="col-md-12 col-lg-6">
                  <div className="hero-right">
                    <div className="hero-img">
                      <img src="/assets/img/slider/hero-2.png" alt="Hero Car" data-animation="fadeInRight" data-delay=".25s" />
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Find Car Form */}
      <div className="find-car">
        <div className="container">
          <div className="find-car-form">
            <h4 className="find-car-title">Let's Find Your Perfect Car</h4>
            <form onSubmit={handleSearch}>
              <div className="row">
                <div className="col-lg-3">
                  <div className="form-group">
                    <label>Car Condition</label>
                    <select 
                      className="select"
                      value={searchForm.condition}
                      onChange={(e) => setSearchForm({...searchForm, condition: e.target.value})}
                    >
                      <option value="">All Conditions</option>
                      <option value="damaged">Damaged</option>
                      <option value="used">Used</option>
                      <option value="accidented">Accidented</option>
                      <option value="derelict">Derelict</option>
                    </select>
                  </div>
                </div>
                <div className="col-lg-3">
                  <div className="form-group">
                    <label>Brand Name</label>
                    <select 
                      className="select"
                      value={searchForm.brand}
                      onChange={(e) => setSearchForm({...searchForm, brand: e.target.value})}
                    >
                      <option value="">All Brands</option>
                      <option value="toyota">Toyota</option>
                      <option value="honda">Honda</option>
                      <option value="ford">Ford</option>
                      <option value="chevrolet">Chevrolet</option>
                      <option value="bmw">BMW</option>
                      <option value="mercedes">Mercedes-Benz</option>
                      <option value="audi">Audi</option>
                      <option value="nissan">Nissan</option>
                    </select>
                  </div>
                </div>
                <div className="col-lg-3">
                  <div className="form-group">
                    <label>Price Range</label>
                    <select 
                      className="select"
                      value={searchForm.priceRange}
                      onChange={(e) => setSearchForm({...searchForm, priceRange: e.target.value})}
                    >
                      <option value="">All Prices</option>
                      <option value="0-5000">$0 - $5,000</option>
                      <option value="5000-10000">$5,000 - $10,000</option>
                      <option value="10000-20000">$10,000 - $20,000</option>
                      <option value="20000-50000">$20,000 - $50,000</option>
                      <option value="50000+">$50,000+</option>
                    </select>
                  </div>
                </div>
                <div className="col-lg-3">
                  <div className="form-group">
                    <label>Location</label>
                    <select 
                      className="select"
                      value={searchForm.location}
                      onChange={(e) => setSearchForm({...searchForm, location: e.target.value})}
                    >
                      <option value="">All Locations</option>
                      <option value="california">California</option>
                      <option value="texas">Texas</option>
                      <option value="florida">Florida</option>
                      <option value="new-york">New York</option>
                      <option value="illinois">Illinois</option>
                    </select>
                  </div>
                </div>
              </div>
              <div className="find-car-btn">
                <button type="submit" className="theme-btn">
                  <i className="far fa-search"></i>Search Car
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>

      {/* Features Section */}
      <div className="feature-area py-120">
        <div className="container">
          <div className="row">
            <div className="col-lg-6 col-xl-3 col-md-6">
              <div className="feature-item">
                <div className="feature-icon">
                  <i className="flaticon-car"></i>
                </div>
                <div className="feature-content">
                  <h4>Quality Vehicles</h4>
                  <p>Carefully inspected crashed and salvage vehicles from trusted dealers.</p>
                </div>
              </div>
            </div>
            <div className="col-lg-6 col-xl-3 col-md-6">
              <div className="feature-item">
                <div className="feature-icon">
                  <i className="flaticon-shield"></i>
                </div>
                <div className="feature-content">
                  <h4>Trusted Dealers</h4>
                  <p>Work with verified and reputable dealers across the country.</p>
                </div>
              </div>
            </div>
            <div className="col-lg-6 col-xl-3 col-md-6">
              <div className="feature-item">
                <div className="feature-icon">
                  <i className="flaticon-support"></i>
                </div>
                <div className="feature-content">
                  <h4>24/7 Support</h4>
                  <p>Get help whenever you need it with our round-the-clock support.</p>
                </div>
              </div>
            </div>
            <div className="col-lg-6 col-xl-3 col-md-6">
              <div className="feature-item">
                <div className="feature-icon">
                  <i className="flaticon-money"></i>
                </div>
                <div className="feature-content">
                  <h4>Best Prices</h4>
                  <p>Find competitive prices on quality salvage vehicles.</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* CTA Section */}
      <div className="cta-area">
        <div className="container">
          <div className="cta-wrapper">
            <div className="row align-items-center">
              <div className="col-lg-8">
                <div className="cta-content">
                  <h2>Ready to Find Your Perfect Car?</h2>
                  <p>Join thousands of satisfied customers who found their dream project car with us.</p>
                </div>
              </div>
              <div className="col-lg-4">
                <div className="cta-btn">
                  <Link href="/cars" className="theme-btn">
                    Browse Cars Now<i className="fas fa-arrow-right-long"></i>
                  </Link>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  );
}