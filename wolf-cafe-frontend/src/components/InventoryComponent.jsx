import { useEffect, useState } from 'react';
import { getInventory, updateInventory } from '../services/InventoryService';
import { useNavigate } from 'react-router-dom'



/** Creates the page for viewing and updating the inventory. */
const InventoryComponent = () => {
	
    const [inventory, setInventory] = useState({});
	const [id, setId] = useState({});
    const [ingredients, setIngredients] = useState([]);
    const [errors, setErrors] = useState({ general: "", ingredients: {} });
	const navigator = useNavigate();
	const [updateSuccess, setUpdateSuccess] = useState(false);

	
    useEffect(() => {
        getInventory()
            .then((response) => {
                setInventory(response.data);
				setId(response.data.id);
				setIngredients(response.data.ingredients);
            })
            .catch((error) => {
                console.error(error);
            });
    }, []);

	function modifyInventory(e) {
	    e.preventDefault();

	    if (validateForm()) {
	        const inv = { id, ingredients };
	        updateInventory(inv)
	            .then((response) => {
	                console.log(response.data);
					setUpdateSuccess(true);
					setTimeout(() => setUpdateSuccess(false), 3000); // hide after 3s

	                // Refresh inventory state to reflect any backend-calculated changes
	                getInventory()
	                    .then((res) => {
	                        setInventory(res.data);
	                        setId(res.data.id);
	                        setIngredients(res.data.ingredients);
	                    })
	                    .catch((err) => {
	                        console.error("Failed to refresh inventory:", err);
	                    });
	            })
	            .catch((error) => {
	                console.error(error);
	                alert("Failed to update inventory.");
	            });
	    }
	}




    function validateForm() {
        let valid = true;
        let newErrors = { general: "", ingredients: {} };
        ingredients.forEach((ingredient, index) => {
			newErrors.ingredients[index] = {}; // Ensure each item has an object for its fields

            if (!ingredient.name.trim()) {
				newErrors.ingredients[index] = "Select an Ingredient";
                valid = false;
            }
			if (ingredient.amount === "" || isNaN(ingredient.amount) || ingredient.amount < 0) {
                newErrors.ingredients[index] = "Amount must be a positive number";
                valid = false;
            }
			if (!ingredient.units) {
                newErrors.ingredients[index] = "Invalid Units";
                valid = false;
            }
        });
	
        setErrors(newErrors);
        return valid;
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

    function getGeneralErrors() {
        if (errors.general) {
            return <div className="p-3 mb-2 bg-danger text-white">{errors.general}</div>;
        }
    }

    return (
        <div className="container">
            <br /><br />
            <div className="row">
                <div className="card col-md-6 offset-md-3">
                    <h2 className="text-center">Add Inventory</h2>
                    <div className="card-body">
					{updateSuccess && (
					  <div className="alert alert-success text-center" role="alert">
					    Inventory updated successfully!
					  </div>
					)}

                        {getGeneralErrors()}
                        <form>
                            {ingredients.map((ingredient, index) => (
                                <div className="mb-3" key={index}>
                                    <label className="form-label d-block">
                                        Ingredient #{index + 1}:
                                    </label>
									<div className="d-flex gap-2 align-items-center">
									{/* Ingredient Input - Larger */}
									<div className="col">
											<label className="form-label"></label>
											<input
												type="text"
												placeholder="Ingredient"
												className={`form-control ${typeof errors.ingredients[index] === 'string' ? "is-invalid" : ""}`}
												value={ingredient.name}
												onChange={(e) => handleIngredientChange(e, index, 'name')}
											/>
											</div>
										    {/* Amount Input */}
										    <div className="col">
												<label className="form-label"></label>	
										        <input
										            type="text"
										            placeholder="Amount"
													className={`form-control ${typeof errors.ingredients[index] === 'string' ? "is-invalid" : ""}`}
										            value={ingredient.amount}
										            onChange={(e) => handleIngredientChange(e, index, 'amount')}
										        />
												{errors.ingredients && errors.ingredients[index] && typeof errors.ingredients[index] === 'string' && (
												  <div className="invalid-feedback">{errors.ingredients[index]}</div>
										        )}
										    </div>

										    {/* Units Input */}
										    <div className="col">
												<label className="form-label"></label>
												<input
										            type="text"
										            placeholder="Units"
													className={`form-control ${typeof errors.ingredients[index] === 'string' ? "is-invalid" : ""}`}
										            value={ingredient.units || ""}
										            onChange={(e) => handleIngredientChange(e, index, 'units')}
										        />
										    </div>
										</div>
		                               </div>	   
		                           ))}
                            {/* Update Button */}
                            <div>
								<button className="btn btn-success mt-3" type="submit" onClick={(e) => modifyInventory(e)}>
									Update Inventory
								</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default InventoryComponent;