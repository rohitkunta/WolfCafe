import { useState } from 'react'
import { createRecipe } from '../services/RecipesService'
import { useNavigate } from 'react-router-dom'

/** Form to create a new recipe. */
const RecipeComponent = () => {

    const [name, setName] = useState("")
    const [price, setPrice] = useState("")
	//this gets the ingredients field in the recipe, the list of ingredients. 
	//every call of setIngredient causes react to reload its version of the ingredients
	//list to the new, updated one that is passed to it .
	const [ingredients, setIngredients] = useState([])
	// don't need to have id here because we are creating a new recupe, not editing an old one.
	// a new id will be created automatically. 

    const navigator = useNavigate()
	//the errors
	const [errors, setErrors] = useState({
	        general: "",
	        name: "",
	        price: "",
	        ingredients: "",
	    })
	
	/**
	 * helper function for changing a field (name/units/amount) on an existing ingredient row
	 * @param event the change that the user has made to an ingredient
	 * @param index the index of the ingredient in the ingredients list being changed
	 * @param field the field of the ingredient that is being changed (name, units, amount)
	 */
		const handleIngredientChange = (event, index, field) => {
			//this will copy the ingredients array to an array I can safely modify (ex: removing an element) without changing the original ingredients array
			const updatedIngredients = [...ingredients]
			
			//this will modidy this copy of the ingredients array with the changes
			updatedIngredients[index][field] = event.target.value
			
			//this will set the original ingredients array to the updated one we just modified. 
			setIngredients(updatedIngredients)
		}
	
	/**
	 * helper function for removing an ingredient row
	 * @param index the index of the ingredient being removed.
	 */
	const removeIngredient = (index) => {
		//this will copy the ingredients array to an array I can safely modify (ex: removing an element) without changing the original ingredients array
		const updatedIngredients = [...ingredients]
		
		//removes the ingredient from the updatedIngredients list
		updatedIngredients.splice(index, 1)
		//sets the ingredients of the state variable ingredients to the updated version, with the removed ingredient
		setIngredients(updatedIngredients)
	}
	
	/**
	 * helper function for adding a new blank ingredient row
	 * 
	 * Important Note: Have to make sure that when we are adding ingredients here that are not already in the 
	 * ingredients and/or inventory tables in the backend, we have to add them there too. If a user tries to make 
	 * a recipe with an ingredient entity that only exists in the recipe, then the system will throw an error. 
	 */
	const addIngredient = () => {
		
		//this will refresh react's ingredient list with the updated version below 
		setIngredients([
			...ingredients, //creates a new array from the original ingredients array
			{ id: null, name: '', units: '', amount: 0 }
			//this then appends an empty ingredient to the ingredients list.
		])
	}

	/**
	 * This will be the same as the EditRecipeComponent saveRecipe(e) function
	 * 
	 * Function for saving the recipe to the backend.
	 * Is triggered when the submit button is clicked at the bottom
	 * 
	 * @param e the event that triggered this function
	 */
	function saveRecipe(e) {
		e.preventDefault();
		
		if (validateForm()){
			//this is the new recipe with all the changes
			// adds the name, price, and the ingredients list to it
			// remember, the ingredients list here is updated because
			// changing, removing, or adding an ingredient makes that change to this array and refresh's it in react (the helper functions do this)
			// deleted the id here because we don't need it. 
			const recipe = {name, price, ingredients} // ingredient is automatically created with an id if it is not already in the DB.
			console.log(recipe)
			
			createRecipe(recipe).then((response) => {
                console.log(response.data)
                navigator("/recipes")
            }).catch(error => {
                console.error(error)
                const errorsCopy = {... errors}
                if (error.response.status == 507) { 
                    errorsCopy.general = "Recipe list is at capacity."
                } 
                if (error.response.status == 409) {
                    errorsCopy.general = "Duplicate recipe name."
                }

                setErrors(errorsCopy)
            })
			
		}
	}

	/**
	 * Validates for the name field not being empty.
	 */
    function validateForm() {
        let valid = true
        
        const errorsCopy = {... errors}

        if (name.trim()) {
            errorsCopy.name = ""
        } else {
            errorsCopy.name = "Name is required."
            valid = false
        }

        

        setErrors(errorsCopy)

        return valid
    }

    function getGeneralErrors() {
        if (errors.general) {
            return <div className="p-3 mb-2 bg-danger text-white">{errors.general}</div>
        }
    }

    return (
        <div className="container">
            <br /><br />
            <div className="row">
                <div className="card col-md-6 offset-md-3">
                    <h2 className="text-center">Add Recipe</h2>

                    <div className="card-body">
                        { getGeneralErrors() }
                        <form>
                            <div className="form-group mb-2">
                                <label className="form-label">Recipe Name</label>
                                <input 
                                    type="text"
                                    name="recipeName"
                                    placeholder="Enter Recipe Name"
                                    value={name}
                                    onChange={(e) => setName(e.target.value)}
                                    className={`form-control ${errors.name ? "is-invalid":""}`}
                                >
                                </input>
                                {errors.name && <div className="invalid-feedback">{errors.name}</div>}
                            </div>

                            <div className="form-group mb-2">
                                <label className="form-label">Recipe Price</label>
                                <input 
                                    type="text"
                                    name="recipePrice"
                                    placeholder="Enter Recipe Price (as an integer)"
                                    value={price}
                                    onChange={(e) => setPrice(e.target.value)}
                                    className="form-control"
                                >
                                </input>
                            </div>
							
							{/**This is where you add the Ingredient section, where the user can add ingredients, and their respective name, unit, and amount. Or could just copy from UC4.*/}
							{/**Ended up copying from UC4, this is literally the same thing.*/}
							{/** ------------------ THIS IS THE INGREDIENTS SECTION ------------------ */}
							<div className="form-group mb-2">
                                <label className="form-label">Ingredients</label>
                                {ingredients.map((ingredient, index) => (
                                    <div key={index} className="mb-3">
                                        <div className="row">
                                            <div className="col">
                                                <label className="form-label">Name</label>
                                                <input
                                                    type="text"
                                                    className="form-control"
                                                    placeholder="Ingredient"
                                                    value={ingredient.name}
                                                    onChange={(e) => handleIngredientChange(e, index, 'name')}
                                                />
                                            </div>
                                            <div className="col">
                                                <label className="form-label">Units</label>
                                                <input
                                                    type="text"
                                                    className="form-control"
                                                    placeholder="Units"
                                                    value={ingredient.units || ''}
                                                    onChange={(e) => handleIngredientChange(e, index, 'units')}
                                                />
                                            </div>
                                            <div className="col">
                                                <label className="form-label">Amount</label>
                                                <input
                                                    type="number"
                                                    className="form-control"
                                                    placeholder="Amount"
                                                    value={ingredient.amount}
                                                    onChange={(e) => handleIngredientChange(e, index, 'amount')}
                                                />
                                            </div>
                                            <div className="col-auto d-flex align-items-end">
                                                <button
                                                    type="button"
                                                    className="btn btn-danger"
                                                    onClick={() => removeIngredient(index)}
                                                >
                                                    Remove Ingredient
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                ))}
                                <button
                                    type="button"
                                    className="btn btn-primary ml-5"
                                    onClick={addIngredient}
                                >
                                    Add Ingredient
                                </button>
                            </div>
                            {/** --------------------------------------------------- */}

                            <button className="btn btn-success mt-3" onClick={(e) => saveRecipe(e)}>Submit</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    )

}

export default RecipeComponent