import { useEffect, useState } from 'react'
import { createIngredient } from '../services/IngredientService'
import { useNavigate, useParams } from 'react-router-dom'

/** Form to create a new ingredient. */
const IngredientComponent = () => {
    const [name, setName] = useState("")
    const [amount, setAmount] = useState("")
    const [units, setUnits] = useState("")
    const [errors, setErrors] = useState({
        general: "",
        name: "",
        amount: "",
    })
    const navigator = useNavigate()

    // Validates that the ingredient has a name and a positive amount.
    function validateForm() {
        let valid = true;

        const errorsCopy = {... errors}

        if (name.trim()) {
            errorsCopy.name = ""
        } else {
            errorsCopy.name = "Name is required."
            valid = false
        }

        if (amount >= 0) {
            errorsCopy.amount = ""
        } else {
            errorsCopy.amount = "Amount must be positive."
            valid = false
        }

        setErrors(errorsCopy)

        return valid
    }

    // Helper function for adding a new ingredient to the repository
    function addNewIngredient(e) {
        e.preventDefault()

        if (validateForm()) {
            const ingredient = {name, amount, units}
            console.log(ingredient)

            createIngredient(ingredient).then((response) => {
                console.log(response.data)
                navigator("/inventory")
            }).catch(error => {
                console.error(error)
                const errorsCopy = {... errors}
                errorsCopy.general = "Duplicate ingredient name."
                setErrors(errorsCopy)
            })
        }
    }

    function getGeneralErrors() {
        if (errors.general) {
            return <div className="p-3 mb-2 bg-danger text-white">{errors.general}</div>
        }
    }

    return(
        <div className="container">
            <br /><br />
            <div className="row">
                <div className="card col-md-6 offset-md-3">
                    <h2 className="text-center">Add Ingredient</h2>
                    <br></br>
                    <div className="card-body text-center">
                        { getGeneralErrors() }
                        <form>
                            <div className="form-group mb-2">
                            <label className="form-label d-block">Name</label>
                                <input 
                                    type="text"
                                    name="ingredientName"
                                    placeholder="Enter Ingredient Name"
                                    value = {name}
                                    onChange={(e) => setName(e.target.value)}
                                    className={`form-control ${errors.name ? "is-invalid":""}`}
                                >
                                </input>
                                {errors.name && <div className="invalid-feedback">{errors.name}</div>}
                            </div>
                            <div className="form-group mb-2">
                            <label className="form-label d-block">Amount</label>
                                <input 
                                    type="text"
                                    name="ingredientAmount"
                                    placeholder="Enter Ingredient Amount"
                                    value = {amount}
                                    onChange={(e) => setAmount(e.target.value)}
                                    className={`form-control ${errors.amount ? "is-invalid":""}`}
                                >
                                </input>
                                {errors.amount && <div className="invalid-feedback">{errors.amount}</div>}
                            </div>
                            <div className="form-group mb-2">
                            <label className="form-label d-block">Units</label>
                                <input 
                                    type="text"
                                    name="ingredientUnits"
                                    placeholder="Enter Ingredient Units"
                                    value={units}
                                    onChange={(e) => setUnits(e.target.value)}
                                    className="form-control"
                                >
                                </input>
                            </div>
                        </form>        
                        <br></br>
                        <br></br>
                        <button className="btn btn-success" onClick={(e) => addNewIngredient(e)}>Add Ingredient</button>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default IngredientComponent