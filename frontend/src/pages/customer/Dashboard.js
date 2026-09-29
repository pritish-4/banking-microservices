import { useState, useEffect } from 'react';
import Navbar from '../../components/Navbar';
import { getMyAccounts } from '../../api/accountApi';

const Dashboard = () => {
  const [accounts, setAccounts] = useState([]);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getMyAccounts()
      .then((res) => setAccounts(res.data))
      .catch(() => setError('Failed to load accounts'))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="min-h-screen bg-gray-100">
      <Navbar />
      <div className="max-w-4xl mx-auto p-6">
        <h2 className="text-2xl font-bold text-gray-800 mb-6">My Dashboard</h2>

        {loading && <p className="text-gray-500">Loading accounts...</p>}
        {error && <p className="bg-red-100 text-red-600 p-3 rounded">{error}</p>}

        {!loading && accounts.length === 0 && (
          <p className="text-gray-500">You have no accounts yet. Go to Accounts to create one.</p>
        )}

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {accounts.map((acc) => (
            <div key={acc.accountId} className="bg-white rounded-lg shadow p-5">
              <div className="flex justify-between items-center mb-2">
                <span className="text-sm font-medium text-gray-500">{acc.accountType}</span>
                <span className={`text-xs px-2 py-1 rounded-full font-medium ${
                  acc.status === 'ACTIVE' ? 'bg-green-100 text-green-700' :
                  acc.status === 'FROZEN' ? 'bg-blue-100 text-blue-700' :
                  'bg-red-100 text-red-700'
                }`}>
                  {acc.status}
                </span>
              </div>
              <p className="text-xs text-gray-400 mb-3">{acc.accountNumber}</p>
              <p className="text-2xl font-bold text-gray-800">${acc.balance.toFixed(2)}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
