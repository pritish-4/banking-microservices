import { useState, useEffect } from 'react';
import Navbar from '../../components/Navbar';
import { getAllAccounts, freezeAccount } from '../../api/accountApi';

const Accounts = () => {
  const [accounts, setAccounts] = useState([]);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(true);

  const fetchAccounts = () => {
    getAllAccounts()
      .then((res) => setAccounts(res.data))
      .catch(() => setError('Failed to load accounts'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchAccounts(); }, []);

  const handleFreeze = async (id) => {
    if (!window.confirm('Are you sure you want to freeze this account?')) return;
    setError('');
    setSuccess('');
    try {
      await freezeAccount(id);
      setSuccess('Account frozen successfully!');
      fetchAccounts();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to freeze account');
    }
  };

  return (
    <div className="min-h-screen bg-gray-100">
      <Navbar />
      <div className="max-w-5xl mx-auto p-6 space-y-6">
        <h2 className="text-2xl font-bold text-gray-800">All Accounts</h2>

        {error && <p className="bg-red-100 text-red-600 p-3 rounded text-sm">{error}</p>}
        {success && <p className="bg-green-100 text-green-600 p-3 rounded text-sm">{success}</p>}

        {loading ? (
          <p className="text-gray-500">Loading...</p>
        ) : (
          <div className="bg-white rounded-lg shadow overflow-hidden">
            <table className="w-full text-sm">
              <thead className="bg-gray-50 text-gray-500 text-left">
                <tr>
                  <th className="px-4 py-3">Account Number</th>
                  <th className="px-4 py-3">Customer ID</th>
                  <th className="px-4 py-3">Type</th>
                  <th className="px-4 py-3">Balance</th>
                  <th className="px-4 py-3">Status</th>
                  <th className="px-4 py-3">Action</th>
                </tr>
              </thead>
              <tbody>
                {accounts.map((acc) => (
                  <tr key={acc.accountId} className="border-t">
                    <td className="px-4 py-3 font-medium text-gray-700">{acc.accountNumber}</td>
                    <td className="px-4 py-3 text-gray-500">{acc.customerId}</td>
                    <td className="px-4 py-3 text-gray-500">{acc.accountType}</td>
                    <td className="px-4 py-3 font-medium text-gray-800">${acc.balance.toFixed(2)}</td>
                    <td className="px-4 py-3">
                      <span className={`text-xs px-2 py-1 rounded-full font-medium ${
                        acc.status === 'ACTIVE' ? 'bg-green-100 text-green-700' :
                        acc.status === 'FROZEN' ? 'bg-blue-100 text-blue-700' :
                        'bg-red-100 text-red-700'
                      }`}>
                        {acc.status}
                      </span>
                    </td>
                    <td className="px-4 py-3">
                      {acc.status === 'ACTIVE' && (
                        <button
                          onClick={() => handleFreeze(acc.accountId)}
                          className="text-xs bg-blue-100 text-blue-600 px-3 py-1 rounded hover:bg-blue-200 transition"
                        >
                          Freeze
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

export default Accounts;
