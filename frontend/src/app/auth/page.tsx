'use client';

import { useState } from 'react';
import Link from 'next/link';
import { LoginForm } from '@/components/auth/LoginForm';
import { RegisterForm } from '@/components/auth/RegisterForm';

export default function AuthPage() {
  const [isLogin, setIsLogin] = useState(true);

  return (
    <main className="main">
      {/* Breadcrumb */}
      <div className="site-breadcrumb" style={{ backgroundImage: 'url(/assets/img/breadcrumb/01.jpg)' }}>
        <div className="container">
          <h2 className="breadcrumb-title">{isLogin ? 'Login' : 'Register'}</h2>
          <ul className="breadcrumb-menu">
            <li><Link href="/">Home</Link></li>
            <li className="active">{isLogin ? 'Login' : 'Register'}</li>
          </ul>
        </div>
      </div>

      {/* Auth Area */}
      <div className="login-area py-120">
        <div className="container">
          <div className="col-md-5 mx-auto">
            <div className="login-form">
              <div className="login-header">
                <img src="/assets/img/logo/logo.png" alt="Crashed Car Sales" />
                <p>Login with your Crashed Car Sales account</p>
              </div>
              
              {isLogin ? (
                <LoginForm onToggleMode={() => setIsLogin(false)} />
              ) : (
                <RegisterForm onToggleMode={() => setIsLogin(true)} />
              )}
              
              <div className="login-footer">
                <p>
                  {isLogin ? "Don't have an account?" : "Already have an account?"}{' '}
                  <button 
                    type="button" 
                    className="link-btn"
                    onClick={() => setIsLogin(!isLogin)}
                  >
                    {isLogin ? 'Register.' : 'Login.'}
                  </button>
                </p>
                <div className="social-login">
                  <p>Continue with social media</p>
                  <div className="social-login-list">
                    <a href="#" aria-label="Login with Facebook">
                      <i className="fab fa-facebook-f"></i>
                    </a>
                    <a href="#" aria-label="Login with Google">
                      <i className="fab fa-google"></i>
                    </a>
                    <a href="#" aria-label="Login with Twitter">
                      <i className="fab fa-twitter"></i>
                    </a>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  );
}