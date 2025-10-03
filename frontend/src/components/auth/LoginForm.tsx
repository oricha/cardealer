'use client';

import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useAuth } from '@/contexts/AuthContext';
import { toast } from '@/lib/utils/toast';

const loginSchema = z.object({
  email: z.string().email('Please enter a valid email address'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
});

type LoginFormData = z.infer<typeof loginSchema>;

interface LoginFormProps {
  onToggleMode: () => void;
}

export const LoginForm: React.FC<LoginFormProps> = ({ onToggleMode }) => {
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const { login } = useAuth();

  const {
    register,
    handleSubmit,
    formState: { errors },
    setError,
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (data: LoginFormData) => {
    setIsLoading(true);
    try {
      await login(data);
      toast.success('Welcome back!');
    } catch (error: unknown) {
      const errorMessage = (error as any)?.response?.data?.message || 'Login failed. Please try again.';
      toast.error(errorMessage);

      // Set specific field errors if available
      if ((error as any)?.response?.data?.error === 'INVALID_CREDENTIALS') {
        setError('password', { message: 'Invalid email or password' });
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <div className="form-group">
        <label>Email Address</label>
        <input 
          type="email" 
          className={`form-control ${errors.email ? 'is-invalid' : ''}`}
          placeholder="Your Email"
          {...register('email')}
        />
        {errors.email && (
          <div className="invalid-feedback">{errors.email.message}</div>
        )}
      </div>
      
      <div className="form-group">
        <label>Password</label>
        <div className="password-input">
          <input 
            type={showPassword ? 'text' : 'password'}
            className={`form-control ${errors.password ? 'is-invalid' : ''}`}
            placeholder="Your Password"
            {...register('password')}
          />
          <button
            type="button"
            className="password-toggle"
            onClick={() => setShowPassword(!showPassword)}
          >
            <i className={`far ${showPassword ? 'fa-eye-slash' : 'fa-eye'}`}></i>
          </button>
        </div>
        {errors.password && (
          <div className="invalid-feedback">{errors.password.message}</div>
        )}
      </div>
      
      <div className="d-flex justify-content-between mb-4">
        <div className="form-check">
          <input className="form-check-input" type="checkbox" value="" id="remember" />
          <label className="form-check-label" htmlFor="remember">
            Remember Me
          </label>
        </div>
        <a href="#" className="forgot-pass">Forgot Password?</a>
      </div>
      
      <div className="d-flex align-items-center">
        <button 
          type="submit" 
          className="theme-btn w-100"
          disabled={isLoading}
        >
          {isLoading ? (
            <>
              <i className="far fa-spinner fa-spin"></i> Logging in...
            </>
          ) : (
            <>
              <i className="far fa-sign-in"></i> Login
            </>
          )}
        </button>
      </div>
    </form>
  );
};