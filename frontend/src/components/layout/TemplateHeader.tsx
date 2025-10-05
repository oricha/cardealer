'use client';

import Link from 'next/link';
import { useState } from 'react';

export function TemplateHeader() {
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  return (
    <header className="header">
      {/* Top Header */}
      <div className="header-top">
        <div className="container">
          <div className="header-top-wrapper">
            <div className="header-top-left">
              <div className="header-top-contact">
                <ul>
                  <li>
                    <a href="mailto:info@crashedcarsales.com">
                      <i className="far fa-envelopes"></i>
                      info@crashedcarsales.com
                    </a>
                  </li>
                  <li>
                    <a href="tel:+1234567890">
                      <i className="far fa-phone-volume"></i> +1 234 567 890
                    </a>
                  </li>
                  <li>
                    <a href="#">
                      <i className="far fa-alarm-clock"></i> Mon - Fri (08AM - 10PM)
                    </a>
                  </li>
                </ul>
              </div>
            </div>
            <div className="header-top-right">
              <div className="header-top-link">
                <Link href="/auth">
                  <i className="far fa-arrow-right-to-arc"></i> Login
                </Link>
                <Link href="/auth">
                  <i className="far fa-user-vneck"></i> Register
                </Link>
              </div>
              <div className="header-top-social">
                <span>Follow Us: </span>
                <a href="#"><i className="fab fa-facebook"></i></a>
                <a href="#"><i className="fab fa-twitter"></i></a>
                <a href="#"><i className="fab fa-instagram"></i></a>
                <a href="#"><i className="fab fa-linkedin"></i></a>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Main Navigation */}
      <div className="main-navigation">
        <nav className="navbar navbar-expand-lg">
          <div className="container position-relative">
            <Link className="navbar-brand" href="/">
              <img src="/assets/img/logo/logo.png" alt="Crashed Car Sales" />
            </Link>
            
            <div className="mobile-menu-right">
              <div className="search-btn">
                <button type="button" className="nav-right-link">
                  <i className="far fa-search"></i>
                </button>
              </div>
              <button 
                className="navbar-toggler" 
                type="button" 
                onClick={() => setIsMenuOpen(!isMenuOpen)}
                aria-expanded={isMenuOpen}
                aria-label="Toggle navigation"
              >
                <span className="navbar-toggler-mobile-icon">
                  <i className="far fa-bars"></i>
                </span>
              </button>
            </div>

            <div className={`collapse navbar-collapse ${isMenuOpen ? 'show' : ''}`} id="main_nav">
              <ul className="navbar-nav">
                <li className="nav-item dropdown">
                  <Link className="nav-link dropdown-toggle active" href="/">
                    Home
                  </Link>
                </li>
                <li className="nav-item">
                  <Link className="nav-link" href="/about">
                    About
                  </Link>
                </li>
                <li className="nav-item dropdown">
                  <a className="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown">
                    Inventory
                  </a>
                  <ul className="dropdown-menu fade-down">
                    <li>
                      <Link className="dropdown-item" href="/cars">
                        All Cars
                      </Link>
                    </li>
                    <li>
                      <Link className="dropdown-item" href="/cars?view=grid">
                        Grid View
                      </Link>
                    </li>
                    <li>
                      <Link className="dropdown-item" href="/cars?view=list">
                        List View
                      </Link>
                    </li>
                  </ul>
                </li>
                <li className="nav-item dropdown">
                  <a className="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown">
                    Pages
                  </a>
                  <ul className="dropdown-menu fade-down">
                    <li>
                      <Link className="dropdown-item" href="/about">
                        About Us
                      </Link>
                    </li>
                    <li className="dropdown-submenu">
                      <a className="dropdown-item dropdown-toggle" href="#">
                        My Account
                      </a>
                      <ul className="dropdown-menu">
                        <li>
                          <Link className="dropdown-item" href="/dashboard">
                            Dashboard
                          </Link>
                        </li>
                        <li>
                          <Link className="dropdown-item" href="/profile">
                            My Profile
                          </Link>
                        </li>
                        <li>
                          <Link className="dropdown-item" href="/favorites">
                            My Favorites
                          </Link>
                        </li>
                      </ul>
                    </li>
                    <li className="dropdown-submenu">
                      <a className="dropdown-item dropdown-toggle" href="#">
                        Authentication
                      </a>
                      <ul className="dropdown-menu">
                        <li>
                          <Link className="dropdown-item" href="/auth">
                            Login
                          </Link>
                        </li>
                        <li>
                          <Link className="dropdown-item" href="/auth">
                            Register
                          </Link>
                        </li>
                      </ul>
                    </li>
                  </ul>
                </li>
                <li className="nav-item dropdown">
                  <a className="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown">
                    Dealers
                  </a>
                  <ul className="dropdown-menu fade-down">
                    <li>
                      <Link className="dropdown-item" href="/dealers">
                        All Dealers
                      </Link>
                    </li>
                    <li>
                      <Link className="dropdown-item" href="/dealers/featured">
                        Featured Dealers
                      </Link>
                    </li>
                  </ul>
                </li>
                <li className="nav-item">
                  <Link className="nav-link" href="/contact">
                    Contact
                  </Link>
                </li>
              </ul>
              
              <div className="nav-right">
                <div className="search-btn">
                  <button type="button" className="nav-right-link">
                    <i className="far fa-search"></i>
                  </button>
                </div>
                <div className="nav-right-btn">
                  <Link href="/add-listing" className="theme-btn">
                    <i className="far fa-plus"></i>
                    Add Listing
                  </Link>
                </div>
              </div>
            </div>
          </div>
        </nav>
      </div>
    </header>
  );
}


