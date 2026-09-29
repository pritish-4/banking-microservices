import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navbar = () => {
  const { role, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const customerLinks = [
    { to: '/customer/dashboard', label: 'Dashboard' },
    { to: '/customer/accounts', label: 'Accounts' },
    { to: '/customer/transactions', label: 'Transactions' },
    { to: '/customer/profile', label: 'Profile' },
  ];

  const adminLinks = [
    { to: '/admin/dashboard', label: 'Dashboard' },
    { to: '/admin/users', label: 'Users' },
    { to: '/admin/accounts', label: 'Accounts' },
    { to: '/admin/transactions', label: 'Transactions' },
  ];

  const links = role === 'ADMIN' ? adminLinks : customerLinks;

  return (
    <nav className="bg-blue-700 text-white px-6 py-3 flex items-center justify-between">
      <span className="font-bold text-lg">Banking App</span>
      <div className="flex items-center gap-6">
        {links.map((link) => (
          <Link key={link.to} to={link.to} className="text-sm hover:underline">
            {link.label}
          </Link>
        ))}
        <button
          onClick={handleLogout}
          className="text-sm bg-white text-blue-700 px-3 py-1 rounded hover:bg-gray-100 transition"
        >
          Logout
        </button>
      </div>
    </nav>
  );
};

export default Navbar;
