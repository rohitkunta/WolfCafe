// src/services/OrderService.js
import axios from 'axios';
import { getToken } from './AuthService';

const BASE_URL = 'http://localhost:8080/api/orders';
const TAX_URL = 'http://localhost:8080/api/taxRate';
const TAX_URL2 = 'http://localhost:8080/api/taxRate/change';

axios.interceptors.request.use(config => {
  const token = getToken();
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`;
  }
  return config;
}, error => Promise.reject(error));

export const placeOrder = (order) => axios.post(BASE_URL, order);
export const pickupOrder = (id) => axios.put(`${BASE_URL}/pickup/${id}`);
export const fulfillOrder = (id, order) => axios.put(`${BASE_URL}/fulfill/${id}`, order);
export const getAllOrders = () => axios.get(BASE_URL);
export const getOrder = (id) => axios.get(`${BASE_URL}/${id}`);
export const updateTaxRate = (rate) =>
  axios.post(TAX_URL2, JSON.stringify(rate), {
    headers: {
      'Content-Type': 'application/json',
    },
  });export const createTaxRate = (rate) => axios.post(TAX_URL2, rate);
export const getTaxRate = () => axios.get(TAX_URL);
