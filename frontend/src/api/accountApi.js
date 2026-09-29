import axios from 'axios';

const BASE_URL = 'http://localhost:8080';

const getAuthHeader = () => ({
  headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
});

export const createAccount = (data) => axios.post(`${BASE_URL}/accounts`, data, getAuthHeader());
export const getMyAccounts = () => axios.get(`${BASE_URL}/accounts/my`, getAuthHeader());
export const getMyAccount = (accountNumber) => axios.get(`${BASE_URL}/accounts/my/${accountNumber}`, getAuthHeader());
export const getBalance = (accountNumber) => axios.get(`${BASE_URL}/accounts/balance/${accountNumber}`, getAuthHeader());
export const closeAccount = (id) => axios.put(`${BASE_URL}/accounts/${id}/close`, {}, getAuthHeader());
export const getAllAccounts = () => axios.get(`${BASE_URL}/accounts`, getAuthHeader());
export const getAccount = (id) => axios.get(`${BASE_URL}/accounts/${id}`, getAuthHeader());
export const resolveAccount = (accountNumber) => axios.get(`${BASE_URL}/accounts/resolve/${accountNumber}`, getAuthHeader());
export const freezeAccount = (id) => axios.put(`${BASE_URL}/accounts/admin/${id}/freeze`, {}, getAuthHeader());
