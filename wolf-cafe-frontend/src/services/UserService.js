import axios from "axios"
import { getToken } from './AuthService';

/** Base URL for the Recipe API - Correspond to methods in Backend's Recipe Controller. */
const REST_API_BASE_URL = "http://localhost:8080/api/auth"

axios.interceptors.request.use(function (config) {
  const token = getToken()
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`
  }
  return config;
}, function (error) {
  return Promise.reject(error);
});

/** GET Recipes - lists all recipes */
export const listUsers = () => axios.get(REST_API_BASE_URL + "/users")

/** POST Recipe - creates a new recipe */
export const createUser = (CreateUserRequestDto) => axios.post(REST_API_BASE_URL + "/createuser", CreateUserRequestDto)

/** PUT Recipe - edits an existing recipe */
export const editRecipe = (recipe) => axios.put(REST_API_BASE_URL, recipe)

/** GET Recipe - gets a single recipe by id */
export const getRecipe = (id) => axios.get(REST_API_BASE_URL + "/" + id)

/** DELETE Recipe - deletes the recipe with the given id */
export const deleteUser = (email) => {
  console.log("Deleting user:", email)
  console.log("Token being sent:", getToken())
  return axios.delete(`${REST_API_BASE_URL}/user/${email}`)
}
