import React from 'react'
import { NavLink } from 'react-router-dom'
import { useNavigate } from 'react-router-dom'
import { isUserLoggedIn, logout, getRole } from '../services/AuthService'

const HeaderComponent = () => {
	const navigator = useNavigate()
	const isAuth = isUserLoggedIn()
	const role = getRole();

	function handleLogout() {
	    logout()
	    navigator('/login')
	}


  return (
    <div>
        <header>
            <nav className='navbar navbar-expand-md navbar-dark bg-dark'>
                <div>
                    <a href='http://localhost:3000' className='navbar-brand'>
                        WolfCafe
                    </a>
                </div>
                <div className='collapse navbar-collapse'>
					<ul className='navbar-nav'>
					{
						isAuth &&
						<li className='nav-item'>
							<NavLink to='/items' className='nav-link'>Items</NavLink>
						</li>
						
					}
					{
						/* No longer using recipe, calling it item
					{
					    isAuth &&
					    <li className='nav-item'>
					      <NavLink to='/recipes' className='nav-link'>Recipes</NavLink>
					    </li>
					}
					*/
				}
					{
						isAuth && (role === 'ROLE_STAFF' || role === 'ROLE_ADMIN') && (
						<li className='nav-item'>
							<NavLink to='/ingredients' className='nav-link'>Ingredients</NavLink>
						</li>
						
					)}
					{
						isAuth && (role === 'ROLE_STAFF' || role === 'ROLE_ADMIN') && (
						<li className='nav-item'>
							<NavLink to='/inventory' className='nav-link'>Inventory</NavLink>
						</li>
						
					)}
					{
						isAuth && ( role === 'ROLE_ADMIN') && (
						<li className='nav-item'>
							<NavLink to='/dashboard' className='nav-link'>Dashboard</NavLink>
						</li>
						
					)}
					
					{
						isAuth && (role === 'ROLE_STAFF' || role === 'ROLE_ADMIN') && (
						<li className='nav-item'>
							<NavLink to='/orders' className='nav-link'>Orders</NavLink>
						</li>
						
					)}
					
					{
						isAuth &&
						<li className='nav-item'>
							<NavLink to='/placeorder' className='nav-link'>Place Order</NavLink>
						</li>
						
					}
					
					{
						isAuth &&
						<li className='nav-item'>
							<NavLink to='/pickup' className='nav-link'>Customer Pickup Order</NavLink>
						</li>
						
					}
					
					{
						isAuth && (role === 'ROLE_STAFF' || role === 'ROLE_ADMIN') && (
						<li className='nav-item'>
							<NavLink to='/taxrate' className='nav-link'>Tax Rate</NavLink>
						</li>
						
					)}
					</ul>
					
				</div>
				<ul className='navbar-nav'>
                    {
                        !isAuth && 
                        <li className='nav-item'>
                            <NavLink to='/register' className='nav-link'>Register</NavLink>
                        </li>
                    }
                    {
                        !isAuth &&
                        <li className='nav-item'>
                            <NavLink to='/login' className='nav-link'>Login</NavLink>
                        </li>
                    } 
                    {
                        isAuth &&
                        <li className='nav-item'>
                            <NavLink to='/login' className='nav-link' onClick={handleLogout}>Logout</NavLink>
                        </li>
                    }   
                </ul>
            </nav>
        </header>
    </div>
  )
}

export default HeaderComponent