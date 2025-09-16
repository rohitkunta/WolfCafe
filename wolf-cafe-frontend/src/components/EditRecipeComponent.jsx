import { useState, useEffect } from 'react'
import { editRecipe, getRecipe } from '../services/RecipesService'
import { useParams, useNavigate } from 'react-router-dom'

const EditRecipeComponent = () => {
	
	const [name, setName] = useState("")
	const [price, setPrice] = useState("")
	//this gets the ingredients field in the recipe, the list of ingredients. 
	//every call of setIngredient causes react to reload its version of the ingredients
	//list to the new, updated one that is passed to it .
	const [ingredients, setIngredients] = useState([])
	const [id, setId] = useState(null);
	
	//the errors
	const [errors, setErrors] = useState({
	        general: "",
	        name: "",
	        price: "",
	        ingredients: "",
	    })
		
	
	
	//might be susceptible to SQL injection
	const { incomingName } = useParams()

	//used for sending user back to the recipes page after hitting submit
	const navigator = useNavigate()
	
	useEffect(() => {
		console.log(incomingName)
		if(incomingName) {
			getRecipe(incomingName).then((response) => {
				console.log("here")
				console.log(response.data)
				setId(response.data.id);
				setName(response.data.name)
				setPrice(response.data.price)
				setIngredients(response.data.ingredients)
			}).catch(error => {
				console.error(error)
			})
		} else {
			console.log("not here")
		}
	}, [incomingName])
	
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
		 * Function for saving the recipe to the backend.
		 * Is triggered when the submit button is clicked at the bottom
		 * 
		 * @param e the submit button being pressed (event)
		 */
			function saveRecipe(e) {
				e.preventDefault();
				
				if (validateForm()){
					//this is the updated recipe with all the changes
					// adds the name, price, and the ingredients list to it
					// remember, the ingredients list here is updated because
					// changing, removing, or adding an ingredient makes that change to this array and refresh's it in react (the helper functions do this)
					// need to add the recipe's ID here because we are not creating a new recipe, we are modifying the old recipe with an
					//exisiting id. 
					const recipe = {id, name, price, ingredients} // ingredient is automatically created with an id if it is not already in the DB.
					console.log(recipe)
					
					editRecipe(recipe).then((response) => {
		                console.log(response.data)
		                navigator("/recipes")
		            }).catch(error => {
		                console.error(error)
		                const errorsCopy = {... errors}
		                if (error.response.status == 507) { // updateRecipe in RecipeController does not throw any specified codes. 
															// These will not get called. If we want to add that functionality later, we have to first change updateRecipe
															// and then update the responses here to the correct codes. Added a note in updateRecipe as well. 
		                    errorsCopy.general = "Recipe list is at capacity."
		                } 
		                if (error.response.status == 409) {
		                    errorsCopy.general = "Duplicate recipe name."
		                }
		
		                setErrors(errorsCopy)
		            })
					
				}
			}
	
	
	return (
		<div className='container'>
				<div className='row'>
			    	<div className='card mt-5 mb-5 col-md-6 offset-md-3 offset-md-3'> {/**Added 5 margin to the top and bottom of the card */}
						<h2 className="text-center">Edit Recipe {incomingName}</h2>
						<div className="card-body">
							<form>
								<div className="form-group mb-2">
									<label className="form-label">Recipe Name</label>
									<input
										type="text"
										className="form-control"
										placeholder="Enter Recipe Name"
										name='name'
										value={name}
										onChange={(e) => setName(e.target.value)}
									>
									</input>
								</div>
								<div className="form-group mb-2">
									<label className="form-label">Recipe Price</label>
									<input
										type="text"
										className="form-control"
										placeholder="Enter Recipe Price"
										name='price'
										value={price}
										onChange={(e) => setPrice(e.target.value)}
									>
									</input>
								</div>
								
								{/** ------------------ THIS IS THE INGREDIENTS SECTION ------------------ */}
									<div className="form-group mb-2">
										<label className="form-label">Ingredients</label>

										{ingredients.map((ingredient, index) => (
											<div key={index} className="mb-3"> {/**The mb stands for margin bottom */}
												<div className="row">
													<div className="col">
														<label className="form-label"></label>
														<input
															type="text"
															placeholder="Ingredient"
															className="form-control"
															value={ingredient.name}
															onChange={(e) => handleIngredientChange(e, index, 'name')}
														/>
													</div>
													<div className="col">
														<label className="form-label">Units</label>
														<input
															type="text"
															className="form-control"
															value={ingredient.units || ''}
															onChange={(e) => handleIngredientChange(e, index, 'units')}
														/>
													</div>
													<div className="col">
														<label className="form-label">Amount</label>
														<input
															type="number"
															className="form-control"
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
											className="btn btn-primary"
											onClick={addIngredient}
										>
											Add Ingredient
										</button>
									</div>
									{/** -------------------------------------------------------- */}
									
									{/**This will save the recipe and its ingredients when the user clicks submit */}
									<button className="btn btn-success mt-3" type="submit" onClick={(e) => saveRecipe(e)}>
										Save
									</button>
							</form>
						</div>
					</div>
				</div>	
			</div>
	  )
}

export default EditRecipeComponent