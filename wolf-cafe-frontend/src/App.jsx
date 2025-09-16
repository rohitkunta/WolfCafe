import './App.css'
import {BrowserRouter, Routes, Route, Navigate} from 'react-router-dom'
import HeaderComponent from './components/HeaderComponent'
import FooterComponent from './components/FooterComponent'
import ListItemsComponent from './components/ListItemsComponent'
import ItemComponent from './components/ItemComponent'
import RegisterComponent from './components/RegisterComponent'
import LoginComponent from './components/LoginComponent'
import { isUserLoggedIn } from './services/AuthService'
import RecipeComponent from './components/RecipeComponent'
import IngredientComponent from './components/IngredientComponent'
import InventoryComponent from './components/InventoryComponent'
import ListRecipesComponent from './components/ListRecipesComponent'
import EditRecipeComponent from './components/EditRecipeComponent'
import AdminDashboardComponent from './components/AdminDashboardComponent'
import EditUserComponent from './components/EditUserComponent'
// import OrdersComponent from './components/OrdersComponent'
import PlaceOrderComponent from './components/PlaceOrderComponent';
import CustomerPickupOrderComponent from './components/CustomerPickupOrderComponent';

import ListOrdersComponent from './components/ListOrdersComponent'
import OrderComponent from './components/OrderComponent'
import TaxRateComponent from './components/TaxRateComponent'
import PrivacyPolicyComponent from './components/PrivacyPolicyComponent';


function App() {

  function AuthenticatedRoute({children}) {
    const isAuth = isUserLoggedIn()
	if (isAuth) {
	  return children
	}
	return <Navigate to='/' />
  }
  

  <Routes>
    <Route
      path='/recipes'
      element={
        <AuthenticatedRoute>
          <ListRecipesComponent />
        </AuthenticatedRoute>
      }
    />
    
    <Route
      path='/ingredients'
      element={
        <AuthenticatedRoute>
          <IngredientComponent />
        </AuthenticatedRoute>
      }
    />
	
	<Route
	  path='/inventory'
	  element={
	    <AuthenticatedRoute>
	      <InventoryComponent />
	    </AuthenticatedRoute>
	  }
	/>
	
	<Route
	  path='/placeorder'
	  element={
	    <AuthenticatedRoute>
	      <PlaceOrderComponent />
	    </AuthenticatedRoute>
	  }
	/>
	
	<Route
	  path='/pickup'
	  element={
	    <AuthenticatedRoute>
	      <CustomerPickupOrderComponent />
	    </AuthenticatedRoute>
	  }
	/>


  </Routes>



  return (
    <>
      <BrowserRouter>
	  <HeaderComponent />
	  <Routes>
	  	<Route path='/' element={<LoginComponent />}></Route>
		<Route path='/register' element={<RegisterComponent />}></Route>
		<Route path='/login' element={<LoginComponent />}></Route>
		<Route path='/items' element={<AuthenticatedRoute><ListItemsComponent /></AuthenticatedRoute>}></Route>
		<Route path='/add-item' element={<AuthenticatedRoute><ItemComponent /></AuthenticatedRoute>}></Route>
		<Route path='/update-item/:id' element={<AuthenticatedRoute><ItemComponent /></AuthenticatedRoute>}></Route>
		<Route path='/recipes' element={<AuthenticatedRoute><ListRecipesComponent /></AuthenticatedRoute>}></Route>
		<Route path='/add-recipe'element={<AuthenticatedRoute><RecipeComponent /></AuthenticatedRoute>}></Route>
		<Route path='/edit-recipe/:incomingName' element = {<AuthenticatedRoute><EditRecipeComponent /></AuthenticatedRoute>}></Route>
		<Route path='/ingredients' element={<AuthenticatedRoute><IngredientComponent /></AuthenticatedRoute>}></Route>
		<Route path='/inventory' element={<AuthenticatedRoute><InventoryComponent /></AuthenticatedRoute>}></Route>
		<Route path='/dashboard' element={<AuthenticatedRoute><AdminDashboardComponent /></AuthenticatedRoute>}></Route>
		<Route path='/editUser' element={<AuthenticatedRoute><EditUserComponent /></AuthenticatedRoute>}></Route>
		<Route path='/placeorder' element={<AuthenticatedRoute><PlaceOrderComponent /></AuthenticatedRoute>}></Route>
		<Route path='/placeorder' element={<AuthenticatedRoute><PlaceOrderComponent /></AuthenticatedRoute>}></Route>
		<Route path='/pickup' element={<AuthenticatedRoute><CustomerPickupOrderComponent /></AuthenticatedRoute>}></Route>
		<Route path='/orders' element={<AuthenticatedRoute><ListOrdersComponent /></AuthenticatedRoute>}></Route>
		<Route path='/orders/:id' element={<AuthenticatedRoute><OrderComponent /></AuthenticatedRoute>}></Route>
		<Route path='/taxrate' element={<AuthenticatedRoute><TaxRateComponent /></AuthenticatedRoute>}></Route>
		<Route path="/privacy-policy" element={<PrivacyPolicyComponent />} />

	  </Routes>
	  <FooterComponent />
	  </BrowserRouter>
    </>
  )
}

export default App
