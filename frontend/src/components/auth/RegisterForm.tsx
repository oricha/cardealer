'use client';

import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useAuth } from '@/contexts/AuthContext';
import { toast } from '@/lib/utils/toast';

const registerSchema = z.object({
  email: z.string().email('Please enter a valid email address'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
  confirmPassword: z.string(),
  name: z.string().min(2, 'Name must be at least 2 characters'),
  role: z.enum(['DEALER', 'BUYER']),
  phone: z.string().optional(),
  address: z.string().optional(),
}).refine((data) => data.password === data.confirmPassword, {
  message: "Passwords don't match",
  path: ["confirmPassword"],
});

type RegisterFormData = z.infer<typeof registerSchema>;

interface RegisterFormProps {
  onToggleMode: () => void;
}

export const RegisterForm: React.FC<RegisterFormProps> = ({ onToggleMode }) => {
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const { register: registerUser } = useAuth();

  const {
    register,
    handleSubmit,
    formState: { errors },
    setError,
  } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
  });

  const onSubmit = async (data: RegisterFormData) => {
    setIsLoading(true);
    try {
      await registerUser({
        email: data.email,
        password: data.password,
        role: data.role,
        name: data.name,
        phone: data.phone,
        address: data.address,
      });
      toast.success('Account created successfully!');
    } catch (error: unknown) {
      const errorMessage = (error as any)?.response?.data?.message || 'Registration failed. Please try again.';
      toast.error(errorMessage);

      // Set specific field errors if available
      if ((error as any)?.response?.data?.error === 'EMAIL_EXISTS') {
        setError('email', { message: 'Email already exists' });
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <div className="row">
        <div className="col-md-6">
          <div className="form-group">
            <label>Full Name</label>
            <input 
              type="text" 
              className={`form-control ${errors.name ? 'is-invalid' : ''}`}
              placeholder="Your Full Name"
              {...register('name')}
            />
            {errors.name && (
              <div className="invalid-feedback">{errors.name.message}</div>
            )}
          </div>
        </div>
        <div className="col-md-6">
          <div className="form-group">
            <label>I am a</label>
            <select 
              className={`form-control ${errors.role ? 'is-invalid' : ''}`}
              {...register('role')}
            >
              <option value="">Select Role</option>
              <option value="BUYER">Car Buyer</option>
              <option value="DEALER">Car Dealer</option>
            </select>
            {errors.role && (
              <div className="invalid-feedback">{errors.role.message}</div>
            )}
          </div>
        </div>
      </div>

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

      <div className="row">
        <div className="col-md-6">
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
        </div>
        <div className="col-md-6">
          <div className="form-group">
            <label>Confirm Password</label>
            <div className="password-input">
              <input 
                type={showConfirmPassword ? 'text' : 'password'}
                className={`form-control ${errors.confirmPassword ? 'is-invalid' : ''}`}
                placeholder="Confirm Password"
                {...register('confirmPassword')}
              />
              <button
                type="button"
                className="password-toggle"
                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
              >
                <i className={`far ${showConfirmPassword ? 'fa-eye-slash' : 'fa-eye'}`}></i>
              </button>
            </div>
            {errors.confirmPassword && (
              <div className="invalid-feedback">{errors.confirmPassword.message}</div>
            )}
          </div>
        </div>
      </div>

      <div className="row">
        <div className="col-md-6">
          <div className="form-group">
            <label>Phone (Optional)</label>
            <input 
              type="tel" 
              className="form-control"
              placeholder="Your Phone"
              {...register('phone')}
            />
          </div>
        </div>
        <div className="col-md-6">
          <div className="form-group">
            <label>Address (Optional)</label>
            <input 
              type="text" 
              className="form-control"
              placeholder="Your Address"
              {...register('address')}
            />
          </div>
        </div>
      </div>

      <div className="d-flex align-items-center">
        <button 
          type="submit" 
          className="theme-btn w-100"
          disabled={isLoading}
        >
          {isLoading ? (
            <>
              <i className="far fa-spinner fa-spin"></i> Creating Account...
            </>
          ) : (
            <>
              <i className="far fa-user-plus"></i> Register
            </>
          )}
        </button>
      </div>
    </form>
  );
};