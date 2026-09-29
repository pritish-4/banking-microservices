import { useState, useEffect } from 'react';
import Navbar from '../../components/Navbar';
import { getAllTransactions, getTransactionsByAccount } from '../../api/transactionApi';

const Transactions = () => {
  const [transactions, setTransactions] = useState([]);
  const [accountId, setAccountId] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  const fetchAll = () => {
    setLoading(true);
    getAllTransactions()
      .then((res) => setTransactions(res.data))
      .catch(() => setError('Failed to load transactions'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchAll(); }, []);

  const handleFilter = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await getTransactionsByAccount(accountId);
      setTransactions(res.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to fetch transactions');
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setAccountId('');
    fetchAll();
  };

  return (
    <div className="min-h-screen bg-gray-100">
      <Navbar />
      <div className="max-w-5xl mx-auto p-6 space-y-6">
        <h2 className="text-2xl font-bold text-gray-800">All Transactions</h2>

        {error && <p className="bg-red-100 text-red-600 p-3 rounded text-sm">{error}</p>}

        {/* Filter by Account */}
        <div className="bg-white rounded-lg shadow p-4">
          <form onSubmit={handleFilter} className="flex gap-3 items-center">
            <input
              type="number"
              placeholder="Filter by Account ID"
              value={accountId}
              onChange={(e) => setAccountId(e.target.value)}
              className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 flex-1"
            />
            <button type="submit" className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition text-sm">
              Filter
            </button>
            <button type="button" onClick={handleReset} className="bg-gray-100 text-gray-600 px-4 py-2 rounded hover:bg-gray-200 transition text-sm">
              Reset
            </button>
          </form>
        </div>

        {/* Transactions Table */}
        {loading ? (
          <p className="text-gray-500">Loading...</p>
        ) : transactions.length === 0 ? (
          <p className="text-gray-500">No transactions found.</p>
        ) : (
          <div className="bg-white rounded-lg shadow overflow-hidden">
            <table className="w-full text-sm">
              <thead className="bg-gray-50 text-gray-500 text-left">
                <tr>
                  <th className="px-4 py-3">Transaction ID</th>
                  <th className="px-4 py-3">Type</th>
                  <th className="px-4 py-3">From</th>
                  <th className="px-4 py-3">To</th>
                  <th className="px-4 py-3">Amount</th>
                  <th className="px-4 py-3">Status</th>
                  <th className="px-4 py-3">Date</th>
                </tr>
              </thead>
              <tbody>
                {transactions.map((tx) => (
                  <tr key={tx.transactionId} className="border-t">
                    <td className="px-4 py-3 text-xs text-gray-400">{tx.transactionId.slice(0, 8)}...</td>
                    <td className="px-4 py-3">
                      <span className={`text-xs px-2 py-1 rounded-full font-medium ${
                        tx.type === 'DEPOSIT' ? 'bg-green-100 text-green-700' :
                        tx.type === 'WITHDRAW' ? 'bg-red-100 text-red-700' :
                        'bg-blue-100 text-blue-700'
                      }`}>
                        {tx.type}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-gray-500">{tx.sourceAccountId ?? '—'}</td>
                    <td className="px-4 py-3 text-gray-500">{tx.targetAccountId ?? '—'}</td>
                    <td className="px-4 py-3 font-medium text-gray-800">${tx.amount.toFixed(2)}</td>
                    <td className="px-4 py-3 text-xs text-gray-500">{tx.status}</td>
                    <td className="px-4 py-3 text-xs text-gray-400">{new Date(tx.createdAt).toLocaleString()}</td>
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

export default Transactions;
