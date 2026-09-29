import { useState, useEffect } from 'react';
import Navbar from '../../components/Navbar';
import { getAllAccounts } from '../../api/accountApi';
import { getAllUsers } from '../../api/authApi';
import { getAllTransactions } from '../../api/transactionApi';

const Dashboard = () => {
  const [stats, setStats] = useState({ accounts: 0, users: 0, transactions: 0, totalBalance: 0 });
  const [error, setError] = useState('');

  useEffect(() => {
    Promise.all([getAllAccounts(), getAllUsers(), getAllTransactions()])
      .then(([accRes, userRes, txRes]) => {
        const accounts = accRes.data;
        const totalBalance = accounts.reduce((sum, acc) => sum + acc.balance, 0);
        setStats({
          accounts: accounts.length,
          users: userRes.data.length,
          transactions: txRes.data.length,
          totalBalance,
        });
      })
      .catch(() => setError('Failed to load dashboard data'));
  }, []);

  const cards = [
    { label: 'Total Users', value: stats.users, color: 'bg-blue-500' },
    { label: 'Total Accounts', value: stats.accounts, color: 'bg-green-500' },
    { label: 'Total Transactions', value: stats.transactions, color: 'bg-purple-500' },
    { label: 'Total Balance', value: `$${stats.totalBalance.toFixed(2)}`, color: 'bg-yellow-500' },
  ];

  return (
    <div className="min-h-screen bg-gray-100">
      <Navbar />
      <div className="max-w-4xl mx-auto p-6 space-y-6">
        <h2 className="text-2xl font-bold text-gray-800">Admin Dashboard</h2>

        {error && <p className="bg-red-100 text-red-600 p-3 rounded text-sm">{error}</p>}

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {cards.map((card) => (
            <div key={card.label} className={`${card.color} text-white rounded-lg shadow p-6`}>
              <p className="text-sm font-medium opacity-80">{card.label}</p>
              <p className="text-3xl font-bold mt-1">{card.value}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
