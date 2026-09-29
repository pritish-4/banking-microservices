import axios from 'axios';

const BASE_URL = 'http://localhost:8080';

const getAuthHeader = () => ({
  headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
});

export const login = (data) => axios.post(`${BASE_URL}/auth/login`, data);
export const register = (data) => axios.post(`${BASE_URL}/auth/register`, data);
export const getMe = () => axios.get(`${BASE_URL}/auth/me`, getAuthHeader());
export const changePassword = (data) => axios.put(`${BASE_URL}/auth/me/password`, data, getAuthHeader());
export const changeEmail = (data) => axios.put(`${BASE_URL}/auth/me/email`, data, getAuthHeader());
export const getAllUsers = () => axios.get(`${BASE_URL}/auth/admin/users`, getAuthHeader());
export const createAdminUser = (data) => axios.post(`${BASE_URL}/auth/admin/create-user`, data, getAuthHeader());
export const deactivateUser = (id) => axios.delete(`${BASE_URL}/auth/admin/users/${id}`, getAuthHeader());
