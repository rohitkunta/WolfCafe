import axios from 'axios'
import { getToken } from './AuthService'

const BASE_REST_API_URL = 'http://localhost:8080/api/items'

axios.interceptors.request.use(function (config) {
  const token = getToken();
  if (token) {
    // Add Bearer only if it's not already present
    if (!token.startsWith('Bearer ')) {
		console.log("Token w/o bearer :", token);
      config.headers['Authorization'] = `Bearer ${token}`;
    } else {
		console.log("Token with bearer :", token);
      config.headers['Authorization'] = `${token}`;
    }
  }
  return config;
}, function (error) {
  return Promise.reject(error);
});


export const saveItem = (item) => axios.post(BASE_REST_API_URL, item)

export const getItemById = (id) => axios.get(BASE_REST_API_URL + '/' + id)

export const getAllItems = () => axios.get(BASE_REST_API_URL)

export const updateItem = (id, item) => axios.put(BASE_REST_API_URL + '/' + id, item)

export const deleteItemById = (id) => axios.delete(BASE_REST_API_URL + '/' + id)