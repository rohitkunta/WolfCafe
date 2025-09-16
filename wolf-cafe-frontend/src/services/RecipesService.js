// RecipeService.js
import axios from "axios";
import { getToken } from './AuthService';

// Create a custom axios instance
const REST_API_BASE_URL = "http://localhost:8080/api/recipes"

// Set up the interceptor on the custom instance
axios.interceptors.request.use(function (config) {
  const token = getToken();
  if (token) {
    // Add Bearer only if it's not already present
    if (!token.startsWith('Bearer ')) {
      config.headers['Authorization'] = `Bearer ${token}`;
    } else {
      config.headers['Authorization'] = token;
    }
  }
  return config;
}, function (error) {
  return Promise.reject(error);
});

/** GET Recipes - lists all recipes */
export const listRecipes = () => axios.get(REST_API_BASE_URL)

/** POST Recipe - creates a new recipe */
export const createRecipe = (recipe) => axios.post(REST_API_BASE_URL, recipe)

/** PUT Recipe - edits an existing recipe */
export const editRecipe = (recipe) => axios.put(REST_API_BASE_URL, recipe)

/** GET Recipe - gets a single recipe by id */
export const getRecipe = (id) => axios.get(REST_API_BASE_URL + "/" + id)

/** DELETE Recipe - deletes the recipe with the given id */
export const deleteRecipe = (id) => axios.delete(REST_API_BASE_URL + "/" + id)
