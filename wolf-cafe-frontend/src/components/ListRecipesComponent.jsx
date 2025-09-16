import React, { useEffect, useState } from 'react'
import { listRecipes, deleteRecipe } from '../services/RecipesService'
import { useNavigate } from 'react-router-dom'

/** Lists all the recipes and provide the option to create a new recipe
 * and delete an existing recipe.
 */
const ListRecipesComponent = () => {

    const [recipes, setRecipes] = useState([])

    const navigator = useNavigate();

    useEffect(() => {
        getAllRecipes()
    }, [])

    function getAllRecipes() {
        listRecipes().then((response) => {
            setRecipes(response.data)
        }).catch(error => {
            console.error(error)
        })
    }

    function addNewRecipe() {
        navigator('/add-recipe')
    }

    function removeRecipe(id) {
        console.log(id)

        deleteRecipe(id).then((response) => {
            getAllRecipes()
        }).catch(error => {
            console.error(error)
        })
    }
	
	function editRecipe(name) {
		console.log(name)
		navigator(`/edit-recipe/${name}`)
	}	

    return (
        <div className="container">
            <h2 className="text-center">List of Recipes</h2>
            <button className="btn btn-primary mb-2" onClick={ addNewRecipe }>Add Recipe</button>
			<table className="table table-bordered">
			                <thead>
			                    <tr>
			                        <th className="col">Name</th>
			                        <th className="col">Price</th>
			                        <th className="col">Actions</th>
			                    </tr>
			                </thead>
			                <tbody>
			                    {recipes.map(recipe => (
			                        <tr key={recipe.id}>
			                            <td>{recipe.name}</td>
			                            <td>{recipe.price}</td>
			                            <td>
										<button className='btn btn-info' onClick={() => editRecipe(recipe.name)}>Edit</button>
										<button className="btn btn-danger" onClick={() => removeRecipe(recipe.id)}>Delete</button>
			                            </td>
			                        </tr>
			                    ))}
			                </tbody>
			            </table>
        </div>
    )

}

export default ListRecipesComponent