import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import PrivateRoute from './components/PrivateRoute';

import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';

import CustomerDashboard from './pages/customer/Dashboard';
import CustomerAccounts from './pages/customer/Accounts';
import CustomerTransactions from './pages/customer/Transactions';
import CustomerProfile from './pages/customer/Profile';

import AdminDashboard from './pages/admin/Dashboard';
import AdminUsers from './pages/admin/Users';
import AdminAccounts from './pages/admin/Accounts';
import AdminTransactions from './pages/admin/Transactions';

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Navigate to="/login" />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          <Route path="/customer/dashboard" element={<PrivateRoute requiredRole="CUSTOMER"><CustomerDashboard /></PrivateRoute>} />
          <Route path="/customer/accounts" element={<PrivateRoute requiredRole="CUSTOMER"><CustomerAccounts /></PrivateRoute>} />
          <Route path="/customer/transactions" element={<PrivateRoute requiredRole="CUSTOMER"><CustomerTransactions /></PrivateRoute>} />
          <Route path="/customer/profile" element={<PrivateRoute requiredRole="CUSTOMER"><CustomerProfile /></PrivateRoute>} />

          <Route path="/admin/dashboard" element={<PrivateRoute requiredRole="ADMIN"><AdminDashboard /></PrivateRoute>} />
          <Route path="/admin/users" element={<PrivateRoute requiredRole="ADMIN"><AdminUsers /></PrivateRoute>} />
          <Route path="/admin/accounts" element={<PrivateRoute requiredRole="ADMIN"><AdminAccounts /></PrivateRoute>} />
          <Route path="/admin/transactions" element={<PrivateRoute requiredRole="ADMIN"><AdminTransactions /></PrivateRoute>} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
