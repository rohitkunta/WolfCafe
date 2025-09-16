import React from 'react'
import { useEffect, useState } from 'react'
import { getItemById, saveItem, updateItem } from '../services/ItemService'
import { useNavigate, useParams } from 'react-router-dom'
import { getAllIngredients } from '../services/IngredientService';


const ItemComponent = () => {

    const [name, setName] = useState('')
    const [description, setDescription] = useState('')
	const [price, setPrice] = useState('')
    const { id } = useParams()

    const navigate = useNavigate()
	
	
	// Adding in state to track ingredients
	const [ingredientInput, setIngredientInput] = useState("");
	const [ingredients, setIngredients] = useState([]);
	const [validIngredientNames, setValidIngredientNames] = useState([]);



    useEffect(() => {
        if(id) {
            getItemById(id).then((response) => {
                console.log(response.data)
                setName(response.data.name)
                setDescription(response.data.description)
				setPrice(response.data.price)
				setIngredients(response.data.ingredients || []);

            }
			
			).catch(error => {
                console.error(error)
            })
        }
		  getAllIngredients()
		    .then((res) => {
		      const names = res.data.map((ing) => ing.name.toLowerCase());
		      setValidIngredientNames(names);
		    })
		    .catch((err) => {
		      console.error("Failed to load ingredients:", err);
		    });
		}, [id]);

    function saveOrUpdateItem(e) {
        e.preventDefault()
		const item = { name, description, price, ingredients };
        console.log(item)

        if (id) {
            updateItem(id, item).then((response) => {
                console.log(response.data)
                navigate('/items')
            }).catch(error => {
                console.error(error)
            })
        } else {
            saveItem(item).then((response) => {
                console.log(response.data)
                navigate('/items')
            }).catch(error => {
                console.error(error)
            })
        }
    }

    function pageTitle() {
        if (id) {
            return <h2 className='text-center'>Update Item</h2>
        } else {
            return <h2 className='text-center'>Add Item</h2>
        }
    }

  return (
    <div className='container'>
        <br /> <br />
        <div className='row'>
            <div className='card col-md-6 offset-md-3 offset-md-3'>
                { pageTitle() }
                
                <div className='card-body'>
                    <form>
                        <div className='form-group mb-2'>
                            <label className='form-label'>Item Name:</label>
                            <input 
                                type='text'
                                className='form-control'
                                placeholder='Enter Item Name'
                                name='name'
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                            >
                            </input>
                        </div>

                        <div className='form-group mb-2'>
                            <label className='form-label'>Item Description:</label>
                            <input 
                                type='text'
                                className='form-control'
                                placeholder='Enter Item Description'
                                name='description'
                                value={description}
                                onChange={(e) => setDescription(e.target.value)}
                            >
                            </input>
                        </div>

                        <div className='form-group mb-2'>
                            <label className='form-label'>Item Price:</label>
							<input 
                                type='text'
                                className='form-control'
                                placeholder='Enter Item Price'
                                name='price'
                                value={price}
                                onChange={(e) => setPrice(e.target.value)}
                            >
                            </input>
                        </div>
						
						<div className='form-group mb-3'>
						  <label className='form-label'>Ingredients:</label>

						  <div className='input-group mb-2'>
						    <input
						      type='text'
						      className='form-control'
						      placeholder='Enter ingredient name'
						      value={ingredientInput}
						      onChange={(e) => setIngredientInput(e.target.value)}
						    />
						    <button
						      className='btn btn-outline-primary'
						      type='button'
						      onClick={() => {
								const trimmed = ingredientInput.trim();
								if (trimmed === '') return;

								if (!validIngredientNames.includes(trimmed.toLowerCase())) {
								  alert(`"${trimmed}" is not in inventory!`);
								  return;
								}

								setIngredients([...ingredients, { name: trimmed }]);
								setIngredientInput('');
						      }}
						    >
						      Add
						    </button>
						  </div>

						  <ul className='list-group'>
						    {ingredients.map((ing, idx) => (
						      <li key={idx} className='list-group-item d-flex justify-content-between align-items-center'>
						        {ing.name}
						        <button
						          type='button'
						          className='btn btn-sm btn-danger'
						          onClick={() => {
						            const updated = [...ingredients];
						            updated.splice(idx, 1);
						            setIngredients(updated);
						          }}
						        >
						          Remove
						        </button>
						      </li>
						    ))}
						  </ul>
						</div>


                        <button type='submit' className='btn btn-success' onClick={(e) => saveOrUpdateItem(e)}>Submit</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
  )
}

export default ItemComponent;
