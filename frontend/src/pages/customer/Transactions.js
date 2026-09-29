import { useState, useEffect } from 'react';
import Navbar from '../../components/Navbar';
import { deposit, withdraw, transfer, getMyTransactions } from '../../api/transactionApi';
import { getMyAccounts, resolveAccount } from '../../api/accountApi';

const Transactions = () => {
  const [accounts, setAccounts] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [activeTab, setActiveTab] = useState('deposit');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(true);

  const [depositForm, setDepositForm] = useState({ accountId: '', amount: '' });
  const [withdrawForm, setWithdrawForm] = useState({ accountId: '', amount: '' });
  const [transferForm, setTransferForm] = useState({ fromAccountId: '', toAccountNumber: '', toAccountId: '', amount: '' });
  const [transferMode, setTransferMode] = useState('own');

  const fetchData = () => {
    Promise.all([getMyAccounts(), getMyTransactions()])
      .then(([accRes, txRes]) => {
        setAccounts(accRes.data);
        setTransactions(txRes.data);
      })
      .catch(() => setError('Failed to load data'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchData(); }, []);

  const handleTransfer = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    try {
      let toAccountId;
      if (transferMode === 'own') {
        toAccountId = parseInt(transferForm.toAccountId);
      } else {
        const balRes = await resolveAccount(transferForm.toAccountNumber);
        toAccountId = balRes.data.accountId;
      }
      await transfer({ fromAccountId: parseInt(transferForm.fromAccountId), toAccountId, amount: parseFloat(transferForm.amount) });
      setSuccess('Transfer successful!');
      setTransferForm({ fromAccountId: '', toAccountNumber: '', toAccountId: '', amount: '' });
      fetchData();
    } catch (err) {
      setError(err.response?.data?.message || 'Transfer failed');
    }
  };

  const handleAction = async (action) => {
    setError('');
    setSuccess('');
    try {
      await action();
      setSuccess('Transaction successful!');
      fetchData();
    } catch (err) {
      setError(err.response?.data?.message || 'Transaction failed');
    }
  };

  const activeAccounts = accounts.filter((a) => a.status === 'ACTIVE');

  return (
    <div className="min-h-screen bg-gray-100">
      <Navbar />
      <div className="max-w-4xl mx-auto p-6 space-y-6">
        <h2 className="text-2xl font-bold text-gray-800">Transactions</h2>

        {error && <p className="bg-red-100 text-red-600 p-3 rounded text-sm">{error}</p>}
        {success && <p className="bg-green-100 text-green-600 p-3 rounded text-sm">{success}</p>}

        {/* Tabs */}
        <div className="bg-white rounded-lg shadow p-5">
          <div className="flex gap-2 mb-5">
            {['deposit', 'withdraw', 'transfer'].map((tab) => (
              <button
                key={tab}
                onClick={() => setActiveTab(tab)}
                className={`px-4 py-2 rounded text-sm font-medium capitalize transition ${
                  activeTab === tab ? 'bg-blue-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                }`}
              >
                {tab}
              </button>
            ))}
          </div>

          {/* Deposit */}
          {activeTab === 'deposit' && (
            <form onSubmit={(e) => { e.preventDefault(); handleAction(() => deposit({ accountId: parseInt(depositForm.accountId), amount: parseFloat(depositForm.amount) })); }} className="space-y-4">
              <select value={depositForm.accountId} onChange={(e) => setDepositForm({ ...depositForm, accountId: e.target.value })} required className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
                <option value="">Select Account</option>
                {activeAccounts.map((a) => <option key={a.accountId} value={a.accountId}>{a.accountNumber} — ${a.balance.toFixed(2)}</option>)}
              </select>
              <input type="number" placeholder="Amount" value={depositForm.amount} onChange={(e) => setDepositForm({ ...depositForm, amount: e.target.value })} required min="0.01" step="0.01" className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
              <button type="submit" className="bg-blue-600 text-white px-5 py-2 rounded hover:bg-blue-700 transition text-sm">Deposit</button>
            </form>
          )}

          {/* Withdraw */}
          {activeTab === 'withdraw' && (
            <form onSubmit={(e) => { e.preventDefault(); handleAction(() => withdraw({ accountId: parseInt(withdrawForm.accountId), amount: parseFloat(withdrawForm.amount) })); }} className="space-y-4">
              <select value={withdrawForm.accountId} onChange={(e) => setWithdrawForm({ ...withdrawForm, accountId: e.target.value })} required className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
                <option value="">Select Account</option>
                {activeAccounts.map((a) => <option key={a.accountId} value={a.accountId}>{a.accountNumber} — ${a.balance.toFixed(2)}</option>)}
              </select>
              <input type="number" placeholder="Amount" value={withdrawForm.amount} onChange={(e) => setWithdrawForm({ ...withdrawForm, amount: e.target.value })} required min="0.01" step="0.01" className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
              <button type="submit" className="bg-blue-600 text-white px-5 py-2 rounded hover:bg-blue-700 transition text-sm">Withdraw</button>
            </form>
          )}

          {/* Transfer */}
          {activeTab === 'transfer' && (
            <form onSubmit={handleTransfer} className="space-y-4">
              {/* Transfer Mode Toggle */}
              <div className="flex gap-2">
                <button type="button" onClick={() => setTransferMode('own')} className={`px-4 py-1 rounded text-sm font-medium transition ${transferMode === 'own' ? 'bg-blue-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'}`}>My Accounts</button>
                <button type="button" onClick={() => setTransferMode('other')} className={`px-4 py-1 rounded text-sm font-medium transition ${transferMode === 'other' ? 'bg-blue-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'}`}>Other Account</button>
              </div>
              <select value={transferForm.fromAccountId} onChange={(e) => setTransferForm({ ...transferForm, fromAccountId: e.target.value })} required className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
                <option value="">From Account</option>
                {activeAccounts.map((a) => <option key={a.accountId} value={a.accountId}>{a.accountNumber} — ${a.balance.toFixed(2)}</option>)}
              </select>
              {transferMode === 'own' ? (
                <select value={transferForm.toAccountId} onChange={(e) => setTransferForm({ ...transferForm, toAccountId: e.target.value })} required className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
                  <option value="">To Account</option>
                  {activeAccounts.filter((a) => a.accountId !== parseInt(transferForm.fromAccountId)).map((a) => <option key={a.accountId} value={a.accountId}>{a.accountNumber} — ${a.balance.toFixed(2)}</option>)}
                </select>
              ) : (
                <input type="text" placeholder="To Account Number (e.g. ACC-XXXXXXXX)" value={transferForm.toAccountNumber} onChange={(e) => setTransferForm({ ...transferForm, toAccountNumber: e.target.value })} required className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
              )}
              <input type="number" placeholder="Amount" value={transferForm.amount} onChange={(e) => setTransferForm({ ...transferForm, amount: e.target.value })} required min="0.01" step="0.01" className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
              <button type="submit" className="bg-blue-600 text-white px-5 py-2 rounded hover:bg-blue-700 transition text-sm">Transfer</button>
            </form>
          )}
        </div>

        {/* Transaction History */}
        <div className="bg-white rounded-lg shadow p-5">
          <h3 className="text-lg font-semibold text-gray-700 mb-4">Transaction History</h3>
          {loading ? (
            <p className="text-gray-500 text-sm">Loading...</p>
          ) : transactions.length === 0 ? (
            <p className="text-gray-500 text-sm">No transactions yet.</p>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="text-left text-gray-500 border-b">
                    <th className="pb-2">ID</th>
                    <th className="pb-2">Type</th>
                    <th className="pb-2">Amount</th>
                    <th className="pb-2">Status</th>
                    <th className="pb-2">Date</th>
                  </tr>
                </thead>
                <tbody>
                  {transactions.map((tx) => (
                    <tr key={tx.transactionId} className="border-b last:border-0">
                      <td className="py-2 text-xs text-gray-400">{tx.transactionId.slice(0, 8)}...</td>
                      <td className="py-2">
                        <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                          tx.type === 'DEPOSIT' ? 'bg-green-100 text-green-700' :
                          tx.type === 'WITHDRAW' ? 'bg-red-100 text-red-700' :
                          'bg-blue-100 text-blue-700'
                        }`}>{tx.type}</span>
                      </td>
                      <td className="py-2 font-medium">${tx.amount.toFixed(2)}</td>
                      <td className="py-2 text-xs text-gray-500">{tx.status}</td>
                      <td className="py-2 text-xs text-gray-400">{new Date(tx.createdAt).toLocaleString()}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Transactions;
