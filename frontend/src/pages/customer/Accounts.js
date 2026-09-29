import { useState, useEffect } from 'react';
import Navbar from '../../components/Navbar';
import { getMyAccounts, createAccount, closeAccount } from '../../api/accountApi';

const Accounts = () => {
  const [accounts, setAccounts] = useState([]);
  const [form, setForm] = useState({ accountType: 'SAVINGS', initialBalance: '' });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(true);

  const fetchAccounts = () => {
    getMyAccounts()
      .then((res) => setAccounts(res.data))
      .catch(() => setError('Failed to load accounts'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchAccounts(); }, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    try {
      await createAccount({ ...form, initialBalance: parseFloat(form.initialBalance) });
      setSuccess('Account created successfully!');
      setForm({ accountType: 'SAVINGS', initialBalance: '' });
      fetchAccounts();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create account');
    }
  };

  const handleClose = async (id) => {
    if (!window.confirm('Are you sure you want to close this account?')) return;
    setError('');
    setSuccess('');
    try {
      await closeAccount(id);
      setSuccess('Account closed successfully!');
      fetchAccounts();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to close account');
    }
  };

  return (
    <div className="min-h-screen bg-gray-100">
      <Navbar />
      <div className="max-w-4xl mx-auto p-6 space-y-6">
        <h2 className="text-2xl font-bold text-gray-800">My Accounts</h2>

        {error && <p className="bg-red-100 text-red-600 p-3 rounded text-sm">{error}</p>}
        {success && <p className="bg-green-100 text-green-600 p-3 rounded text-sm">{success}</p>}

        {/* Create Account Form */}
        <div className="bg-white rounded-lg shadow p-5">
          <h3 className="text-lg font-semibold text-gray-700 mb-4">Open New Account</h3>
          <form onSubmit={handleCreate} className="flex flex-col md:flex-row gap-4">
            <select
              value={form.accountType}
              onChange={(e) => setForm({ ...form, accountType: e.target.value })}
              className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="SAVINGS">Savings</option>
              <option value="CURRENT">Current</option>
            </select>
            <input
              type="number"
              placeholder="Initial Balance"
              value={form.initialBalance}
              onChange={(e) => setForm({ ...form, initialBalance: e.target.value })}
              required
              min="0.01"
              step="0.01"
              className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 flex-1"
            />
            <button
              type="submit"
              className="bg-blue-600 text-white px-5 py-2 rounded hover:bg-blue-700 transition text-sm"
            >
              Create
            </button>
          </form>
        </div>

        {/* Accounts List */}
        {loading ? (
          <p className="text-gray-500">Loading...</p>
        ) : accounts.length === 0 ? (
          <p className="text-gray-500">No accounts found. Create one above!</p>
        ) : (
          <div className="space-y-3">
            {accounts.map((acc) => (
              <div key={acc.accountId} className="bg-white rounded-lg shadow p-5 flex justify-between items-center">
                <div>
                  <p className="text-sm font-medium text-gray-500">{acc.accountType}</p>
                  <p className="text-xs text-gray-400">{acc.accountNumber}</p>
                  <p className="text-xl font-bold text-gray-800 mt-1">${acc.balance.toFixed(2)}</p>
                </div>
                <div className="flex items-center gap-3">
                  <span className={`text-xs px-2 py-1 rounded-full font-medium ${
                    acc.status === 'ACTIVE' ? 'bg-green-100 text-green-700' :
                    acc.status === 'FROZEN' ? 'bg-blue-100 text-blue-700' :
                    'bg-red-100 text-red-700'
                  }`}>
                    {acc.status}
                  </span>
                  {acc.status === 'ACTIVE' && (
                    <button
                      onClick={() => handleClose(acc.accountId)}
                      className="text-xs bg-red-100 text-red-600 px-3 py-1 rounded hover:bg-red-200 transition"
                    >
                      Close
                    </button>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default Accounts;
