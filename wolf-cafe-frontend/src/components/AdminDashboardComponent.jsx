import React, { useEffect, useState } from 'react';
import { listUsers, deleteUser } from '../services/UserService';
import { useNavigate } from 'react-router-dom';


const AdminDashboardComponent = () => {
    const [users, setUsers] = useState([]);
    const navigator = useNavigate();

	 
    useEffect(() => {
        fetchUsers();
    }, []);

    function fetchUsers() {
        listUsers().then((response) => {
            setUsers(response.data);
        }).catch(error => {
            console.error(error);
        });
    }

	function removeUser(email) {
	    deleteUser(email).then(() => {
	        fetchUsers();
	    }).catch(error => {
	        console.error(error);
	    });
	}
	
	
	// Create user
	

	function editUser(user) {
	    console.log(user);
	    navigator('/editUser', { state: { user } });
	}


    return (
		<div className="container">
		            <h2 className="text-center">Admin Dashboard</h2>
		            <table className="table table-striped table-bordered">
		                <thead>
		                    <tr>
		                        <th>User</th>
		                        <th>Email</th>
		                        <th>Role</th>
		                        <th>Actions</th>
		                    </tr>
		                </thead>
		                <tbody>
		                    {users.map(user => (
		                        <tr key={user.email}>
		                            <td>{user.name}</td>
		                            <td>{user.email}</td>
		                            <td>{user.roles.map(role => role.name).join(", ")}</td>
								 	<td>
								      <button className='btn btn-info' onClick={() => editUser(user)}>Edit</button>
									  <button className="btn btn-danger ms-2" onClick={() => removeUser(user.email)}>Delete</button>
									</td>

		                        </tr>
		                    ))}
		                </tbody>
		            </table>
					<div className="text-center mt-3">
					  <button
					    className="btn btn-primary"
					    onClick={() => navigator('/editUser')}
					  >
					    Create New User
					  </button>
					</div>
		        </div>
    );
};

export default AdminDashboardComponent;
