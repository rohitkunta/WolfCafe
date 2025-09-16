import React, { useEffect, useState } from 'react';
import { getAllOrders, fulfillOrder, pickupOrder } from '../services/orderService';
import { useNavigate } from 'react-router-dom';

const ListOrdersComponent = () => {
  const [orders, setOrders] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    fetchOrders();
  }, []);

  function fetchOrders() {
      getAllOrders().then((response) => {
          setOrders(response.data);
      }).catch(error => {
          console.error(error);
      });
  }
  
  function fulfill(id, order) {
    fulfillOrder(id, order)
      .then(() => fetchOrders())
      .catch((error) => console.error(error));
  }
  
  function pickup(id) {
    pickupOrder(id)
      .then(() => fetchOrders())
      .catch((error) => console.error(error));
  }

return (
	<div className="container">
		<h2 className="text-center">Order List</h2>

		{["PENDING", "READY_FOR_PICKUP", "COMPLETE"].map(status => {
			const filteredOrders = orders.filter(
				order => order.status?.name === status
			);

			if (filteredOrders.length === 0) return null;

			return (
				<div key={status} className="mb-5">
					<h3>{status.replace(/_/g, " ")}</h3>
					<table className="table table-striped table-bordered">
						<thead>
							<tr>
								<th>#</th>
								<th>User</th>
								<th>Cost</th>
								<th>Status</th>
								<th>Actions</th>
							</tr>
						</thead>
						<tbody>
							{filteredOrders.map(order => (
								<tr key={order.id}>
									<td>{order.id}</td>
									<td>{order.customerUsername}</td>
									<td>{order.amountPaid}</td>
									<td>{order.status.name}</td>
									<td>
									  <button
									    className="btn btn-primary btn-sm me-2"
									    onClick={() => navigate(`/orders/${order.id}`)}
									  >
									    View
									  </button>

									  {order.status.name === "PENDING" && (
									    <button className="btn btn-success btn-sm" onClick={() => fulfill(order.id, order)}>
									      Fulfill
									    </button>
									  )}

									  {order.status.name === "READY_FOR_PICKUP" && (
									    <button className="btn btn-warning btn-sm" onClick={() => pickup(order.id)}>
									      Pickup
									    </button>
									  )}
									</td>
								</tr>
							))}
						</tbody>
					</table>
				</div>
			);
		})}
	</div>
);

};

export default ListOrdersComponent;
