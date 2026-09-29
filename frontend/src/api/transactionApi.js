import axios from 'axios';

const BASE_URL = 'http://localhost:8080';

const getAuthHeader = () => ({
  headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
});

export const deposit = (data) => axios.post(`${BASE_URL}/transactions/deposit`, data, getAuthHeader());
export const withdraw = (data) => axios.post(`${BASE_URL}/transactions/withdraw`, data, getAuthHeader());
export const transfer = (data) => axios.post(`${BASE_URL}/transactions/transfer`, data, getAuthHeader());
export const getMyTransactions = (type) => axios.get(`${BASE_URL}/transactions/my${type ? `?type=${type}` : ''}`, getAuthHeader());
export const getTransactionById = (id) => axios.get(`${BASE_URL}/transactions/${id}`, getAuthHeader());
export const getAllTransactions = () => axios.get(`${BASE_URL}/transactions`, getAuthHeader());
export const getTransactionsByAccount = (accountId) => axios.get(`${BASE_URL}/transactions/account/${accountId}`, getAuthHeader());
