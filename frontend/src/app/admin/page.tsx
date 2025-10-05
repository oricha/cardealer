'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import { useLanguage } from '@/contexts/LanguageContext';
import { Button } from '@/components/ui/Button';
import { Card } from '@/components/ui/Card';
import { Badge } from '@/components/ui/Badge';

interface AdminStats {
  totalUsers: number;
  totalCars: number;
  totalRevenue: number;
  activeUsers: number;
  recentRegistrations: number;
  systemHealth: 'good' | 'warning' | 'critical';
}

interface User {
  id: string;
  email: string;
  role: 'ADMIN' | 'DEALER' | 'BUYER';
  isActive: boolean;
  createdAt: string;
  lastLogin?: string;
}

export default function AdminPage() {
  const { t, formatCurrency, formatDate } = useLanguage();
  const [stats, setStats] = useState<AdminStats>({
    totalUsers: 0,
    totalCars: 0,
    totalRevenue: 0,
    activeUsers: 0,
    recentRegistrations: 0,
    systemHealth: 'good',
  });
  const [users, setUsers] = useState<User[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isLoadingUsers, setIsLoadingUsers] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [roleFilter, setRoleFilter] = useState<string>('all');
  const [statusFilter, setStatusFilter] = useState<string>('all');
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    loadAdminData();
  }, [currentPage]);

  const loadAdminData = async () => {
    try {
      setIsLoading(true);
      setError(null);

      // TODO: Replace with actual API calls when backend is ready
      // const statsResponse = await adminService.getSystemStats();
      // const usersResponse = await adminService.getUsers({ page: currentPage, search: searchTerm, role: roleFilter, status: statusFilter });

      // Mock data for demonstration
      setStats({
        totalUsers: 1247,
        totalCars: 8934,
        totalRevenue: 2847593,
        activeUsers: 892,
        recentRegistrations: 47,
        systemHealth: 'good',
      });

      // Mock users data
      const mockUsers: User[] = [
        {
          id: '1',
          email: 'dealer1@example.com',
          role: 'DEALER',
          isActive: true,
          createdAt: '2024-01-15T10:30:00Z',
          lastLogin: '2024-02-10T14:22:00Z',
        },
        {
          id: '2',
          email: 'buyer1@example.com',
          role: 'BUYER',
          isActive: true,
          createdAt: '2024-01-20T09:15:00Z',
          lastLogin: '2024-02-09T16:45:00Z',
        },
        {
          id: '3',
          email: 'dealer2@example.com',
          role: 'DEALER',
          isActive: false,
          createdAt: '2024-01-10T11:45:00Z',
          lastLogin: '2024-01-25T13:20:00Z',
        },
      ];

      setUsers(mockUsers);
      setTotalPages(5); // Mock total pages

    } catch (err: unknown) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to load admin data';
      setError(errorMessage);
    } finally {
      setIsLoading(false);
      setIsLoadingUsers(false);
    }
  };

  const handleUserStatusChange = async (userId: string, newStatus: boolean) => {
    try {
      // TODO: Replace with actual API call
      // await adminService.updateUserStatus(userId, newStatus);

      // Update local state
      setUsers(users.map(user =>
        user.id === userId ? { ...user, isActive: newStatus } : user
      ));

      // Update stats
      setStats(prev => ({
        ...prev,
        activeUsers: newStatus ? prev.activeUsers + 1 : prev.activeUsers - 1,
      }));

    } catch (err: unknown) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to update user status';
      setError(errorMessage);
    }
  };

  const handleDeleteUser = async (userId: string) => {
    if (!confirm(t('common.confirm') + ': ' + t('dashboard.confirmDelete'))) return;

    try {
      // TODO: Replace with actual API call
      // await adminService.deleteUser(userId);

      // Update local state
      setUsers(users.filter(user => user.id !== userId));
      setStats(prev => ({
        ...prev,
        totalUsers: prev.totalUsers - 1,
        activeUsers: users.find(u => u.id === userId)?.isActive ? prev.activeUsers - 1 : prev.activeUsers,
      }));

    } catch (err: unknown) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to delete user';
      setError(errorMessage);
    }
  };

  const filteredUsers = users.filter(user => {
    const matchesSearch = user.email.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesRole = roleFilter === 'all' || user.role === roleFilter;
    const matchesStatus = statusFilter === 'all' ||
      (statusFilter === 'active' && user.isActive) ||
      (statusFilter === 'inactive' && !user.isActive);

    return matchesSearch && matchesRole && matchesStatus;
  });

  if (isLoading) {
    return (
      <div className="main">
        <div className="site-breadcrumb" style={{ backgroundImage: 'url(/assets/img/breadcrumb/01.jpg)' }}>
          <div className="container">
            <h2 className="breadcrumb-title">Admin Dashboard</h2>
            <ul className="breadcrumb-menu">
              <li><Link href="/">Home</Link></li>
              <li className="active">Admin</li>
            </ul>
          </div>
        </div>

        <div className="admin-section py-120">
          <div className="container">
            <div className="text-center py-5">
              <div className="spinner"></div>
              <p className="mt-3">Loading admin dashboard...</p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="main">
      {/* Breadcrumb */}
      <div className="site-breadcrumb" style={{ backgroundImage: 'url(/assets/img/breadcrumb/01.jpg)' }}>
        <div className="container">
          <h2 className="breadcrumb-title">Admin Dashboard</h2>
          <ul className="breadcrumb-menu">
            <li><Link href="/">Home</Link></li>
            <li className="active">Admin</li>
          </ul>
        </div>
      </div>

      {/* Admin Content */}
      <div className="admin-section py-120">
        <div className="container">
          {/* Header */}
          <div className="admin-header mb-5">
            <h2 className="admin-title">System Administration</h2>
            <p className="admin-subtitle">Manage users, monitor system performance, and oversee platform operations</p>
          </div>

          {/* System Stats */}
          <div className="row mb-5">
            <div className="col-lg-3 col-md-6">
              <Card className="admin-stat-card">
                <div className="stat-icon">
                  <i className="flaticon-user"></i>
                </div>
                <div className="stat-content">
                  <h3>{stats.totalUsers.toLocaleString()}</h3>
                  <p>Total Users</p>
                </div>
              </Card>
            </div>
            <div className="col-lg-3 col-md-6">
              <Card className="admin-stat-card">
                <div className="stat-icon">
                  <i className="flaticon-car"></i>
                </div>
                <div className="stat-content">
                  <h3>{stats.totalCars.toLocaleString()}</h3>
                  <p>Total Cars</p>
                </div>
              </Card>
            </div>
            <div className="col-lg-3 col-md-6">
              <Card className="admin-stat-card">
                <div className="stat-icon">
                  <i className="flaticon-dollar-sign"></i>
                </div>
                <div className="stat-content">
                  <h3>{formatCurrency(stats.totalRevenue)}</h3>
                  <p>Total Revenue</p>
                </div>
              </Card>
            </div>
            <div className="col-lg-3 col-md-6">
              <Card className="admin-stat-card">
                <div className="stat-icon">
                  <i className="flaticon-activity"></i>
                </div>
                <div className="stat-content">
                  <h3>{stats.activeUsers.toLocaleString()}</h3>
                  <p>Active Users</p>
                </div>
              </Card>
            </div>
          </div>

          {/* System Health */}
          <Card className="system-health mb-5">
            <div className="health-header">
              <h3>System Health</h3>
              <Badge className={`health-status status-${stats.systemHealth}`}>
                {stats.systemHealth.toUpperCase()}
              </Badge>
            </div>
            <div className="health-metrics">
              <div className="health-item">
                <span className="health-label">Recent Registrations (24h):</span>
                <span className="health-value">{stats.recentRegistrations}</span>
              </div>
              <div className="health-item">
                <span className="health-label">Server Response Time:</span>
                <span className="health-value">245ms</span>
              </div>
              <div className="health-item">
                <span className="health-label">Database Connections:</span>
                <span className="health-value">12/50</span>
              </div>
            </div>
          </Card>

          {/* Error Display */}
          {error && (
            <div className="alert alert-danger mb-4">
              <i className="far fa-exclamation-triangle me-2"></i>
              {error}
            </div>
          )}

          {/* User Management */}
          <Card className="user-management">
            <div className="user-management-header">
              <h3>User Management</h3>
              <div className="user-management-actions">
                <Button className="theme-btn">
                  <i className="far fa-download me-2"></i>Export Users
                </Button>
              </div>
            </div>

            {/* Filters */}
            <div className="user-filters mb-4">
              <div className="row">
                <div className="col-md-4">
                  <input
                    type="text"
                    placeholder="Search users by email..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="form-control"
                  />
                </div>
                <div className="col-md-3">
                  <select
                    value={roleFilter}
                    onChange={(e) => setRoleFilter(e.target.value)}
                    className="form-control"
                  >
                    <option value="all">All Roles</option>
                    <option value="ADMIN">Admin</option>
                    <option value="DEALER">Dealer</option>
                    <option value="BUYER">Buyer</option>
                  </select>
                </div>
                <div className="col-md-3">
                  <select
                    value={statusFilter}
                    onChange={(e) => setStatusFilter(e.target.value)}
                    className="form-control"
                  >
                    <option value="all">All Status</option>
                    <option value="active">Active</option>
                    <option value="inactive">Inactive</option>
                  </select>
                </div>
                <div className="col-md-2">
                  <Button
                    onClick={loadAdminData}
                    className="theme-btn w-100"
                  >
                    <i className="far fa-search me-2"></i>Filter
                  </Button>
                </div>
              </div>
            </div>

            {/* Users Table */}
            {isLoadingUsers ? (
              <div className="text-center py-5">
                <div className="spinner"></div>
                <p className="mt-3">Loading users...</p>
              </div>
            ) : (
              <>
                <div className="users-table-container">
                  <div className="table-responsive">
                    <table className="users-table">
                      <thead>
                        <tr>
                          <th>User</th>
                          <th>Role</th>
                          <th>Status</th>
                          <th>Registered</th>
                          <th>Last Login</th>
                          <th>Actions</th>
                        </tr>
                      </thead>
                      <tbody>
                        {filteredUsers.map((user) => (
                          <tr key={user.id}>
                            <td>
                              <div className="user-info">
                                <div className="user-avatar">
                                  <i className="far fa-user"></i>
                                </div>
                                <div className="user-details">
                                  <h5>{user.email}</h5>
                                  <small className="text-muted">ID: {user.id}</small>
                                </div>
                              </div>
                            </td>
                            <td>
                              <Badge className={`role-badge role-${user.role.toLowerCase()}`}>
                                {user.role}
                              </Badge>
                            </td>
                            <td>
                              <Badge className={user.isActive ? 'status-active' : 'status-inactive'}>
                                {user.isActive ? 'Active' : 'Inactive'}
                              </Badge>
                            </td>
                            <td>
                              {formatDate(user.createdAt)}
                            </td>
                            <td>
                              {user.lastLogin ? formatDate(user.lastLogin) : 'Never'}
                            </td>
                            <td>
                              <div className="action-buttons">
                                <Button
                                  onClick={() => handleUserStatusChange(user.id, !user.isActive)}
                                  className={`btn btn-sm me-2 ${
                                    user.isActive ? 'btn-warning' : 'btn-success'
                                  }`}
                                >
                                  <i className={`far fa-${user.isActive ? 'pause' : 'play'}`}></i>
                                </Button>
                                <Button
                                  onClick={() => handleDeleteUser(user.id)}
                                  className="btn btn-sm btn-outline-danger"
                                >
                                  <i className="far fa-trash"></i>
                                </Button>
                              </div>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>

                {/* Pagination */}
                {totalPages > 1 && (
                  <div className="pagination-container">
                    <div className="pagination-info">
                      Showing page {currentPage + 1} of {totalPages}
                    </div>
                    <div className="pagination-buttons">
                      <Button
                        onClick={() => setCurrentPage(prev => Math.max(0, prev - 1))}
                        disabled={currentPage === 0}
                        className="btn btn-outline-secondary me-2"
                      >
                        Previous
                      </Button>
                      <Button
                        onClick={() => setCurrentPage(prev => Math.min(totalPages - 1, prev + 1))}
                        disabled={currentPage === totalPages - 1}
                        className="btn btn-outline-secondary"
                      >
                        Next
                      </Button>
                    </div>
                  </div>
                )}
              </>
            )}
          </Card>
        </div>
      </div>
    </div>
  );
}