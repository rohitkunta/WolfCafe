import { useState } from 'react'
import { createUser } from '../services/UserService'
import { useNavigate } from 'react-router-dom'
import { getToken } from '../services/AuthService'
import { useLocation } from 'react-router-dom';
import { useEffect } from 'react';



/** Form to create a new recipe. */
const EditUserComponent = () => {

	const [name, setName] = useState("");
    const [password, setPassword] = useState("");
	const [email, setEmail] = useState("");
    const [role, setRole] = useState({ id: 3, name: "ROLE_CUSTOMER" });
	const [fullName, setFullName] = useState("");


    const navigator = useNavigate()
	//the errors
	const [errors, setErrors] = useState({
        general: "",
        username: "",
        email: "",
        password: "",
    });
	
	
	const location = useLocation();
	const editingUser = location.state?.user;

	useEffect(() => {
	    if (editingUser) {
	        setFullName(editingUser.name || "");
	        setName(editingUser.username || "");        // Fill in username
	        setEmail(editingUser.email || "");
	        setPassword(editingUser.password || "");    // Fill in password if available
	        setRole(editingUser.roles?.[0] || { id: 3, name: "ROLE_CUSTOMER" });
	    }
	}, []);


	/**
	 * This will be the same as the EditRecipeComponent saveRecipe(e) function
	 * 
	 * Function for saving the recipe to the backend.
	 * Is triggered when the submit button is clicked at the bottom
	 * 
	 * @param e the event that triggered this function
	 */
	function saveUser(e) {
	    e.preventDefault();

	    if (validateForm()) {
	        const EditUserRequestDto = {
	            registerDto: {
					name: fullName,
	                username: name,   // assuming 'name' is the username
	                password: password,
	                email: email
	            },
	            role: role           // assuming 'role' is an object with id and name
	        };

	        console.log(EditUserRequestDto);
			console.log("Stored token:", getToken());
	        createUser(EditUserRequestDto).then((response) => {
	            console.log(response.data);
	            navigator("/dashboard");
	        }).catch(error => {
	            console.error(error);
	            const errorsCopy = { ...errors }
				if (error.response.status == 507) { 
                    errorsCopy.general = "Recipe list is at capacity."
                } 
                if (error.response.status == 409) {
                    errorsCopy.general = "Duplicate recipe name."
                }
				else {
	            	errorsCopy.general = "Something went wrong."
				}
	            setErrors(errorsCopy);
	        });
	    }
	}

	/**
	 * Validates for the fields not being empty.
	 */
	function validateForm() {
	        let valid = true;
	        const errorsCopy = { ...errors };

	        if (!name.trim()) {
	            errorsCopy.username = "Username is required.";
	            valid = false;
	        } else {
	            errorsCopy.username = "";
	        }

	        if (!email.trim()) {
	            errorsCopy.email = "Email is required.";
	            valid = false;
	        } else {
	            errorsCopy.email = "";
	        }

	        if (!password.trim()) {
	            errorsCopy.password = "Password is required.";
	            valid = false;
	        } else {
	            errorsCopy.password = "";
	        }

	        setErrors(errorsCopy);
	        return valid;
	    }

	function getGeneralErrors() {
        if (errors.general) {
            return <div className="p-3 mb-2 bg-danger text-white">{errors.general}</div>;
        }
    }

	return (
	        <div className="container mt-5">
	            <div className="row justify-content-center">
	                <div className="col-md-8">
	                    <h2 className="text-center mb-4">Edit User</h2>
	                    <div className="card p-4">
	                        {getGeneralErrors()}
	                        <form>
								<div className="form-group mb-3">
								  <label>Full Name</label>
								  <input
								    type="text"
								    className="form-control"
								    value={fullName}
								    onChange={(e) => setFullName(e.target.value)}
								  />
								</div>

	                            <div className="form-group mb-3">
	                                <label>Username</label>
	                                <input
	                                    type="text"
	                                    className={`form-control ${errors.username ? "is-invalid" : ""}`}
	                                    value={name}
	                                    onChange={(e) => setName(e.target.value)}
	                                />
	                                {errors.username && <div className="invalid-feedback">{errors.username}</div>}
	                            </div>

	                            <div className="form-group mb-3">
	                                <label>Role</label>
									<select
									  value={role.name.toLowerCase().replace("role_", "")}
									  onChange={(e) => {
									    const selected = e.target.value;
									    const roleMap = {
									      customer: { id: 3, name: "ROLE_CUSTOMER" },
									      staff: { id: 2, name: "ROLE_STAFF" },
									      admin: { id: 1, name: "ROLE_ADMIN" },
									    };
									    setRole(roleMap[selected]);
									  }}
									>
									  <option value="customer">Customer</option>
									  <option value="staff">Staff</option>
									  <option value="admin">Admin</option>
									</select>

	                            </div>

	                            <div className="form-group mb-3">
	                                <label>Email</label>
	                                <input
	                                    type="email"
	                                    className={`form-control ${errors.email ? "is-invalid" : ""}`}
	                                    value={email}
	                                    onChange={(e) => setEmail(e.target.value)}
	                                />
	                                {errors.email && <div className="invalid-feedback">{errors.email}</div>}
	                            </div>

	                            <div className="form-group mb-4">
	                                <label>Password</label>
	                                <input
	                                    type="password"
	                                    className={`form-control ${errors.password ? "is-invalid" : ""}`}
	                                    value={password}
	                                    onChange={(e) => setPassword(e.target.value)}
	                                />
	                                {errors.password && <div className="invalid-feedback">{errors.password}</div>}
	                            </div>

	                            <button
	                                className="btn w-100"
	                                style={{ backgroundColor: '#4e5cf4', color: 'white', fontWeight: 'bold' }}
	                                onClick={saveUser}
	                            >
	                                Create User
	                            </button>
	                        </form>
	                    </div>
	                </div>
	            </div>
	        </div>
	    )

}

export default EditUserComponent