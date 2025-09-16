import React, { useState, useEffect } from "react";
import { getAllOrders, pickupOrder } from "../services/orderService";

const CustomerPickupOrder = () => {
  const [readyOrders, setReadyOrders] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [pickedUpOrders, setPickedUpOrders] = useState([]);

  // load orders
  useEffect(() => {
    getAllOrders()
      .then((response) => {
        const allOrders = response.data;
        const ready = allOrders.filter((order) => order.status.name === "READY_FOR_PICKUP");
        const pickedUp = allOrders.filter((order) => order.status.name === "COMPLETE");

        setReadyOrders(ready);
        setPickedUpOrders(pickedUp);
      })
      .catch((error) => {
        console.error("Failed to fetch orders:", error);
      });
  }, []);

  return (
    <div className="container mt-4">
      <h2 className="text-center">Pickup Orders</h2>

      <h4 className="mt-5 text-muted">Picked Up</h4>
      <table className="table table-striped">
        <thead>
          <tr>
            <th>#</th>
            <th>User</th>
            <th>Items</th>
            <th>Cost</th>
          </tr>
        </thead>
        <tbody>
          {pickedUpOrders.map((order, index) => (
            <tr key={order.id}>
              <td>{index + 1}</td>
              <td>{order.customerUsername}</td>
              <td>{order.itemsOrdered?.join(", ") || "—"}</td>
              <td>${order.amountPaid ? order.amountPaid.toFixed(2) : "—"}</td>
            </tr>
          ))}
        </tbody>
      </table>

      <h4 className="mt-5 text-success">Ready for Pickup</h4>
      <table className="table table-striped">
        <thead>
          <tr>
            <th>#</th>
            <th>User</th>
            <th>Items</th>
            <th>Cost</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {readyOrders.map((order, index) => (
            <tr key={order.id}>
              <td>{index + 1}</td>
              <td>{order.customerUsername}</td>
              <td>{order.itemsOrdered?.join(", ") || "—"}</td>
              <td>${order.amountPaid ? order.amountPaid.toFixed(2) : "—"}</td>
              <td>
                <button
                  className="btn btn-success btn-sm"
                  onClick={() => {
                    setSelectedOrder(order);
                    setShowModal(true);
                  }}
                >
                  Pickup and Verify
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {showModal && selectedOrder && (
        <div className="modal show d-block" tabIndex="-1" role="dialog">
          <div className="modal-dialog" role="document">
            <div className="modal-content">
              <div className="modal-header">
                <h5 className="modal-title">Confirm Pickup</h5>
                <button type="button" className="btn-close" onClick={() => setShowModal(false)}></button>
              </div>
              <div className="modal-body">
                <p>
                  Please confirm that the following order from <strong>{selectedOrder.customerUsername}</strong> has been picked up correctly:
                </p>
                <ul>
                  {selectedOrder.itemsOrdered?.map((item, i) => (
                    <li key={i}>{item}</li>
                  ))}
                </ul>
              </div>
              <div className="modal-footer">
                <button
                  className="btn btn-secondary"
                  onClick={() => {
                    setSelectedOrder(null);
                    setShowModal(false);
                  }}
                >
                  Cancel
                </button>
                <button
                  className="btn btn-danger"
                  onClick={() => {
                    alert("Order issue reported. A staff member will review it.");
                    setShowModal(false);
                    setSelectedOrder(null);
                  }}
                >
                  Report Issue
                </button>
                <button
                  className="btn btn-success"
                  onClick={() => {
                    pickupOrder(selectedOrder.id)
                      .then(() => {
                        setReadyOrders((prev) => prev.filter((o) => o.id !== selectedOrder.id));
                        setPickedUpOrders((prev) => [
                          ...prev,
                          { ...selectedOrder },
                        ]);
                        setShowModal(false);
                        setSelectedOrder(null);
                      })
                      .catch((error) => {
                        console.error("Failed to update order:", error);
                        alert("Something went wrong while confirming pickup.");
                      });
                  }}
                >
                  Confirm Pickup
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default CustomerPickupOrder;
