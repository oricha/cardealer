'use client';

import { useState, useEffect } from 'react';
import { Car } from '@/types/car';
import { carService } from '@/lib/api/cars';
import { Button } from '@/components/ui/Button';

interface CarFormProps {
  car?: Car | null;
  onClose: () => void;
  onSuccess: () => void;
}

export function CarForm({ car, onClose, onSuccess }: CarFormProps) {
  const [formData, setFormData] = useState({
    make: '',
    model: '',
    year: new Date().getFullYear(),
    fuelType: 'GAS' as Car['fuelType'],
    transmission: 'MANUAL' as Car['transmission'],
    vehicleType: 'PASSENGER' as Car['vehicleType'],
    condition: 'USED' as Car['condition'],
    price: 0,
    mileage: 0,
    description: '',
  });

  const [features, setFeatures] = useState({
    airbags: false,
    absBrakes: false,
    airConditioning: false,
    powerSteering: false,
    centralLocking: false,
    electricWindows: false,
  });

  const [images, setImages] = useState<File[]>([]);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    if (car) {
      setFormData({
        make: car.make,
        model: car.model,
        year: car.year,
        fuelType: car.fuelType,
        transmission: car.transmission,
        vehicleType: car.vehicleType,
        condition: car.condition,
        price: car.price,
        mileage: car.mileage || 0,
        description: car.description || '',
      });
      setFeatures(car.features);
    }
  }, [car]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'year' || name === 'price' || name === 'mileage'
        ? parseInt(value) || 0
        : value
    }));
    // Clear error when user starts typing
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  const handleFeatureChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, checked } = e.target;
    setFeatures(prev => ({ ...prev, [name]: checked }));
  };

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = Array.from(e.target.files || []);
    setImages(files);
  };

  const validateForm = () => {
    const newErrors: Record<string, string> = {};

    if (!formData.make.trim()) newErrors.make = 'Make is required';
    if (!formData.model.trim()) newErrors.model = 'Model is required';
    if (formData.year < 1900 || formData.year > new Date().getFullYear() + 1) {
      newErrors.year = 'Please enter a valid year';
    }
    if (formData.price <= 0) newErrors.price = 'Price must be greater than 0';
    if (formData.mileage < 0) newErrors.mileage = 'Mileage cannot be negative';
    if (!formData.description.trim()) newErrors.description = 'Description is required';

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateForm()) return;

    setIsSubmitting(true);

    try {
      const carData = {
        ...formData,
        features,
        images: [],
        dealerId: '', // This should be set by the backend based on authenticated user
        isFeatured: false,
        isActive: true,
      };

      if (car) {
        // Update existing car
        await carService.updateCar(car.id, carData);
      } else {
        // Create new car
        await carService.createCar(carData);
      }

      onSuccess();
      onClose();
    } catch (err: unknown) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to save car';
      setErrors({ submit: errorMessage });
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="car-form-overlay">
      <div className="car-form-modal">
        <div className="car-form-header">
          <h3>{car ? 'Edit Car' : 'Add New Car'}</h3>
          <button type="button" className="close-btn" onClick={onClose}>
            <i className="far fa-times"></i>
          </button>
        </div>

        <form onSubmit={handleSubmit} className="car-form">
          <div className="form-row">
            <div className="form-group">
              <label htmlFor="make">Make *</label>
              <input
                type="text"
                id="make"
                name="make"
                value={formData.make}
                onChange={handleInputChange}
                className={errors.make ? 'form-control is-invalid' : 'form-control'}
                placeholder="e.g. Toyota"
              />
              {errors.make && <div className="invalid-feedback">{errors.make}</div>}
            </div>

            <div className="form-group">
              <label htmlFor="model">Model *</label>
              <input
                type="text"
                id="model"
                name="model"
                value={formData.model}
                onChange={handleInputChange}
                className={errors.model ? 'form-control is-invalid' : 'form-control'}
                placeholder="e.g. Camry"
              />
              {errors.model && <div className="invalid-feedback">{errors.model}</div>}
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="year">Year *</label>
              <input
                type="number"
                id="year"
                name="year"
                value={formData.year}
                onChange={handleInputChange}
                className={errors.year ? 'form-control is-invalid' : 'form-control'}
                min="1900"
                max={new Date().getFullYear() + 1}
              />
              {errors.year && <div className="invalid-feedback">{errors.year}</div>}
            </div>

            <div className="form-group">
              <label htmlFor="price">Price *</label>
              <input
                type="number"
                id="price"
                name="price"
                value={formData.price}
                onChange={handleInputChange}
                className={errors.price ? 'form-control is-invalid' : 'form-control'}
                min="0"
                step="100"
              />
              {errors.price && <div className="invalid-feedback">{errors.price}</div>}
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="fuelType">Fuel Type</label>
              <select
                id="fuelType"
                name="fuelType"
                value={formData.fuelType}
                onChange={handleInputChange}
                className="form-control"
              >
                <option value="GAS">Gasoline</option>
                <option value="DIESEL">Diesel</option>
                <option value="HYBRID">Hybrid</option>
                <option value="ELECTRIC">Electric</option>
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="transmission">Transmission</label>
              <select
                id="transmission"
                name="transmission"
                value={formData.transmission}
                onChange={handleInputChange}
                className="form-control"
              >
                <option value="MANUAL">Manual</option>
                <option value="AUTOMATIC">Automatic</option>
                <option value="CVT">CVT</option>
              </select>
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="vehicleType">Vehicle Type</label>
              <select
                id="vehicleType"
                name="vehicleType"
                value={formData.vehicleType}
                onChange={handleInputChange}
                className="form-control"
              >
                <option value="PASSENGER">Passenger</option>
                <option value="VAN">Van</option>
                <option value="TRUCK">Truck</option>
                <option value="MOTOR">Motorcycle</option>
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="condition">Condition</label>
              <select
                id="condition"
                name="condition"
                value={formData.condition}
                onChange={handleInputChange}
                className="form-control"
              >
                <option value="USED">Used</option>
                <option value="DAMAGED">Damaged</option>
                <option value="ACCIDENTED">Accidented</option>
                <option value="DERELICT">Derelict</option>
              </select>
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="mileage">Mileage</label>
              <input
                type="number"
                id="mileage"
                name="mileage"
                value={formData.mileage}
                onChange={handleInputChange}
                className={errors.mileage ? 'form-control is-invalid' : 'form-control'}
                min="0"
              />
              {errors.mileage && <div className="invalid-feedback">{errors.mileage}</div>}
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="description">Description *</label>
            <textarea
              id="description"
              name="description"
              value={formData.description}
              onChange={handleInputChange}
              className={errors.description ? 'form-control is-invalid' : 'form-control'}
              rows={4}
              placeholder="Describe the car's condition, features, and any damage..."
            />
            {errors.description && <div className="invalid-feedback">{errors.description}</div>}
          </div>

          <div className="form-group">
            <label>Features</label>
            <div className="features-grid">
              {Object.entries(features).map(([key, value]) => (
                <label key={key} className="feature-checkbox">
                  <input
                    type="checkbox"
                    name={key}
                    checked={value}
                    onChange={handleFeatureChange}
                  />
                  <span className="checkmark"></span>
                  {key.replace(/([A-Z])/g, ' $1').replace(/^./, str => str.toUpperCase())}
                </label>
              ))}
            </div>
          </div>

          {!car && (
            <div className="form-group">
              <label htmlFor="images">Car Images</label>
              <input
                type="file"
                id="images"
                multiple
                accept="image/*"
                onChange={handleImageChange}
                className="form-control"
              />
              <small className="form-text text-muted">
                Upload multiple images of the car. First image will be used as the main image.
              </small>
            </div>
          )}

          {errors.submit && (
            <div className="alert alert-danger">
              <i className="far fa-exclamation-triangle me-2"></i>
              {errors.submit}
            </div>
          )}

          <div className="form-actions">
            <Button
              type="button"
              onClick={onClose}
              className="btn btn-outline-secondary me-3"
              disabled={isSubmitting}
            >
              Cancel
            </Button>
            <Button
              type="submit"
              className="theme-btn"
              disabled={isSubmitting}
            >
              {isSubmitting ? (
                <>
                  <div className="spinner" style={{ width: '16px', height: '16px', marginRight: '8px' }}></div>
                  {car ? 'Updating...' : 'Adding...'}
                </>
              ) : (
                <>
                  <i className={`far fa-${car ? 'save' : 'plus'} me-2`}></i>
                  {car ? 'Update Car' : 'Add Car'}
                </>
              )}
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
}