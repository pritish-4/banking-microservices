import { useState, useEffect } from 'react';
import Navbar from '../../components/Navbar';
import { getMe, changePassword, changeEmail } from '../../api/authApi';

const Profile = () => {
  const [profile, setProfile] = useState(null);
  const [passwordForm, setPasswordForm] = useState({ currentPassword: '', newPassword: '' });
  const [emailForm, setEmailForm] = useState({ newEmail: '' });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    getMe().then((res) => setProfile(res.data)).catch(() => setError('Failed to load profile'));
  }, []);

  const handlePasswordChange = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    try {
      await changePassword(passwordForm);
      setSuccess('Password changed successfully!');
      setPasswordForm({ currentPassword: '', newPassword: '' });
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to change password');
    }
  };

  const handleEmailChange = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    try {
      await changeEmail(emailForm);
      setSuccess('Email changed successfully!');
      setEmailForm({ newEmail: '' });
      getMe().then((res) => setProfile(res.data));
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to change email');
    }
  };

  return (
    <div className="min-h-screen bg-gray-100">
      <Navbar />
      <div className="max-w-2xl mx-auto p-6 space-y-6">
        <h2 className="text-2xl font-bold text-gray-800">My Profile</h2>

        {error && <p className="bg-red-100 text-red-600 p-3 rounded text-sm">{error}</p>}
        {success && <p className="bg-green-100 text-green-600 p-3 rounded text-sm">{success}</p>}

        {/* Profile Info */}
        {profile && (
          <div className="bg-white rounded-lg shadow p-5">
            <h3 className="text-lg font-semibold text-gray-700 mb-3">Account Info</h3>
            <div className="space-y-2 text-sm text-gray-600">
              <p><span className="font-medium text-gray-700">Username:</span> {profile.username}</p>
              <p><span className="font-medium text-gray-700">Email:</span> {profile.email}</p>
              <p><span className="font-medium text-gray-700">Role:</span> {profile.role}</p>
            </div>
          </div>
        )}

        {/* Change Password */}
        <div className="bg-white rounded-lg shadow p-5">
          <h3 className="text-lg font-semibold text-gray-700 mb-4">Change Password</h3>
          <form onSubmit={handlePasswordChange} className="space-y-4">
            <input
              type="password"
              placeholder="Current Password"
              value={passwordForm.currentPassword}
              onChange={(e) => setPasswordForm({ ...passwordForm, currentPassword: e.target.value })}
              required
              className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            <input
              type="password"
              placeholder="New Password"
              value={passwordForm.newPassword}
              onChange={(e) => setPasswordForm({ ...passwordForm, newPassword: e.target.value })}
              required
              minLength={8}
              className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            <button type="submit" className="bg-blue-600 text-white px-5 py-2 rounded hover:bg-blue-700 transition text-sm">
              Update Password
            </button>
          </form>
        </div>

        {/* Change Email */}
        <div className="bg-white rounded-lg shadow p-5">
          <h3 className="text-lg font-semibold text-gray-700 mb-4">Change Email</h3>
          <form onSubmit={handleEmailChange} className="space-y-4">
            <input
              type="email"
              placeholder="New Email"
              value={emailForm.newEmail}
              onChange={(e) => setEmailForm({ newEmail: e.target.value })}
              required
              className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            <button type="submit" className="bg-blue-600 text-white px-5 py-2 rounded hover:bg-blue-700 transition text-sm">
              Update Email
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Profile;
