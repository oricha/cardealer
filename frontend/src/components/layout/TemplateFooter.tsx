'use client';

import Link from 'next/link';

export function TemplateFooter() {
  return (
    <footer className="footer-area">
      <div className="footer-widget">
        <div className="container">
          <div className="row footer-widget-wrapper pt-100 pb-70">
            <div className="col-md-6 col-lg-4">
              <div className="footer-widget-box about-us">
                <Link href="/" className="footer-logo">
                  <img src="/assets/img/logo/logo-light.png" alt="Crashed Car Sales" />
                </Link>
                <p className="mb-3">
                  We are the leading platform for quality crashed and salvage vehicles. 
                  Find the perfect repairable car from trusted dealers nationwide.
                </p>
                <ul className="footer-contact">
                  <li>
                    <a href="tel:+1234567890">
                      <i className="far fa-phone"></i>+1 234 567 890
                    </a>
                  </li>
                  <li>
                    <i className="far fa-map-marker-alt"></i>123 Auto Street, Car City, CC 12345
                  </li>
                  <li>
                    <a href="mailto:info@crashedcarsales.com">
                      <i className="far fa-envelope"></i>info@crashedcarsales.com
                    </a>
                  </li>
                </ul>
              </div>
            </div>
            
            <div className="col-md-6 col-lg-2">
              <div className="footer-widget-box list">
                <h4 className="footer-widget-title">Quick Links</h4>
                <ul className="footer-list">
                  <li>
                    <Link href="/about">
                      <i className="fas fa-caret-right"></i> About Us
                    </Link>
                  </li>
                  <li>
                    <Link href="/cars">
                      <i className="fas fa-caret-right"></i> Browse Cars
                    </Link>
                  </li>
                  <li>
                    <Link href="/dealers">
                      <i className="fas fa-caret-right"></i> Our Dealers
                    </Link>
                  </li>
                  <li>
                    <Link href="/terms">
                      <i className="fas fa-caret-right"></i> Terms Of Service
                    </Link>
                  </li>
                  <li>
                    <Link href="/privacy">
                      <i className="fas fa-caret-right"></i> Privacy Policy
                    </Link>
                  </li>
                  <li>
                    <Link href="/contact">
                      <i className="fas fa-caret-right"></i> Contact Us
                    </Link>
                  </li>
                </ul>
              </div>
            </div>
            
            <div className="col-md-6 col-lg-3">
              <div className="footer-widget-box list">
                <h4 className="footer-widget-title">Support Center</h4>
                <ul className="footer-list">
                  <li>
                    <Link href="/faq">
                      <i className="fas fa-caret-right"></i> FAQ's
                    </Link>
                  </li>
                  <li>
                    <Link href="/how-it-works">
                      <i className="fas fa-caret-right"></i> How It Works
                    </Link>
                  </li>
                  <li>
                    <Link href="/buying-tips">
                      <i className="fas fa-caret-right"></i> Buying Tips
                    </Link>
                  </li>
                  <li>
                    <Link href="/sell-vehicle">
                      <i className="fas fa-caret-right"></i> Sell Vehicle
                    </Link>
                  </li>
                  <li>
                    <Link href="/contact">
                      <i className="fas fa-caret-right"></i> Contact Us
                    </Link>
                  </li>
                  <li>
                    <Link href="/sitemap">
                      <i className="fas fa-caret-right"></i> Sitemap
                    </Link>
                  </li>
                </ul>
              </div>
            </div>
            
            <div className="col-md-6 col-lg-3">
              <div className="footer-widget-box list">
                <h4 className="footer-widget-title">Newsletter</h4>
                <div className="footer-newsletter">
                  <p>Subscribe Our Newsletter To Get Latest Update And News</p>
                  <div className="subscribe-form">
                    <form action="#">
                      <input 
                        type="email" 
                        className="form-control" 
                        placeholder="Your Email"
                        required
                      />
                      <button className="theme-btn" type="submit">
                        Subscribe Now <i className="far fa-paper-plane"></i>
                      </button>
                    </form>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div className="copyright">
        <div className="container">
          <div className="row">
            <div className="col-md-6 align-self-center">
              <p className="copyright-text">
                &copy; Copyright <span id="date">{new Date().getFullYear()}</span>{' '}
                <Link href="/">Crashed Car Sales</Link> All Rights Reserved.
              </p>
            </div>
            <div className="col-md-6 align-self-center">
              <ul className="footer-social">
                <li>
                  <a href="#" aria-label="Facebook">
                    <i className="fab fa-facebook-f"></i>
                  </a>
                </li>
                <li>
                  <a href="#" aria-label="Twitter">
                    <i className="fab fa-twitter"></i>
                  </a>
                </li>
                <li>
                  <a href="#" aria-label="LinkedIn">
                    <i className="fab fa-linkedin-in"></i>
                  </a>
                </li>
                <li>
                  <a href="#" aria-label="YouTube">
                    <i className="fab fa-youtube"></i>
                  </a>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </footer>
  );
}
