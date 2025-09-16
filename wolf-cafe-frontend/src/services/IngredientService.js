import axios from "axios"

/** Base URL for the Recipe API - Correspond to methods in Backend's Recipe Controller. */
const REST_API_BASE_URL = "http://localhost:8080/api/ingredients"

/** POST Ingredient - creates a new ingredient */
export const createIngredient = (ingredient) => axios.post(REST_API_BASE_URL, ingredient)

/** GET Ingredient - gets a single ingredient by ID */
export const getIngredient = (id) => axios.get(REST_API_BASE_URL + '/' + id)

/** GET Ingredient - lists all ingredients */
export const getAllIngredients = () => axios.get(REST_API_BASE_URL)