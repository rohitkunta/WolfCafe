import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { getOrder } from '../services/orderService' 

/** Form to create a new recipe. */
const OrderComponent = () => {
	const { id } = useParams()
	    const navigate = useNavigate()

	    const [order, setOrder] = useState(null)
	    const [error, setError] = useState("")

	    useEffect(() => {
	        getOrder(id)
	            .then(response => {
	                setOrder(response.data)
	            })
	            .catch(err => {
	                console.error(err)
	                setError("Failed to load order.")
	            })
	    }, [id])
		if (error) {
		        return <div className="container p-3 mb-2 bg-danger text-white">{error}</div>
		    }

		    if (!order) {
		        return <div className="container">Loading...</div>
		    }

		    return (
		        <div className="container">
		            <br /><br />
		            <div className="row">
		                <div className="card col-md-6 offset-md-3">
		                    <h2 className="text-center">View Order</h2>
		                    <div className="card-body">
		                        <div className="mb-3">
		                            <strong>Order ID:</strong> {order.id}
		                        </div>
		                        <div className="mb-3">
		                            <strong>Customer Username:</strong> {order.customerUsername}
		                        </div>
		                        <div className="mb-3">
		                            <strong>Total Price:</strong> ${order.amountPaid}
		                        </div>
		                        <div className="mb-3">
		                            <strong>Status:</strong> {order.status?.name}
		                        </div>

		                        {/* Items/Contents of the order (assuming it's something like ingredients) */}
								<div className="form-group mb-2">
								                            <label className="form-label">Items Ordered</label>
								                            <ul className="list-group">
								                                {order.itemsOrdered.map((item, index) => (
								                                    <li key={index} className="list-group-item">
								                                        {item}
								                                    </li>
								                                ))}
								                            </ul>
								                        </div>

		                        <button className="btn btn-secondary mt-3" onClick={() => navigate(-1)}>
		                            Back
		                        </button>
		                    </div>
		                </div>
		            </div>
		        </div>
		    )
}

export default OrderComponent