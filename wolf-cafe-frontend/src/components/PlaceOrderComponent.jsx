import React, { useEffect, useState } from "react";
import { getAllItems } from "../services/ItemService";
import { placeOrder } from "../services/orderService";
import { getLoggedInUser } from "../services/AuthService";
import { getTaxRate } from "../services/orderService";


const getImageForItem = (itemName) => {
  switch (itemName.toLowerCase()) {
    case "mocha":
      return "https://images.unsplash.com/photo-1618576230663-9714aecfb99a";
    case "latte":
      return "https://images.unsplash.com/photo-1509042239860-f550ce710b93";
    case "espresso":
      return "https://images.unsplash.com/photo-1579992357154-faf4bde95b3d";
	case "cappuccino":
	  return "https://images.unsplash.com/photo-1534778101976-62847782c213?q=80&w=987&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D";
	case "iced coffee":
	  return "https://images.unsplash.com/photo-1517701550927-30cf4ba1dba5?q=80&w=987&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D";
 	case "flat white":
	  return "https://images.unsplash.com/photo-1616084521924-3dbdfb2e5234?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8ZmxhdCUyMHdoaXRlfGVufDB8fDB8fHww";
	case "americano":
	  return "https://images.unsplash.com/photo-1580661869408-55ab23f2ca6e?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8YW1lcmljYW5vfGVufDB8fDB8fHww";	  
	case "cold brew":
	  return "https://images.unsplash.com/photo-1558122104-355edad709f6?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8Y29sZCUyMGJyZXd8ZW58MHx8MHx8fDA%3D";	
    default:
      return "https://plus.unsplash.com/premium_photo-1677607237201-64668c2266ab?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MjF8fGNvZmZlZXxlbnwwfHwwfHx8MA%3D%3D";
  }
};


const PlaceOrderComponent = () => {
  const [menuItems, setMenuItems] = useState([]);
  const [basket, setBasket] = useState({});
  const [tipPercent, setTipPercent] = useState(0.2);
  const [customTip, setCustomTip] = useState("");
  const [isCustom, setIsCustom] = useState(false);
  const [taxRate, setTaxRate] = useState(null);


  useEffect(() => {
    getAllItems()
      .then((res) => setMenuItems(res.data))
      .catch((err) => console.error("Failed to fetch items", err));
	  
	  getTaxRate()
	    .then((res) => {
	      if (res.data !== undefined) {
	        setTaxRate(res.data / 100);
	      }
	    })
	    .catch((err) => console.error("Failed to fetch tax rate", err));

  }, []);


  
  const addToBasket = (item) => {
    const updated = { ...basket };
    updated[item.id] = (updated[item.id] || 0) + 1;
    setBasket(updated);
  };
  
  const removeFromBasket = (itemId) => {
    const updated = { ...basket };
    if (updated[itemId] > 1) {
      updated[itemId] -= 1;
    } else {
      delete updated[itemId];
    }
    setBasket(updated);
  };
  
  
  const calculateSubtotal = () => {
    return Object.entries(basket).reduce((acc, [id, qty]) => {
      const item = menuItems.find((item) => item.id === parseInt(id));
      return acc + (item?.price || 0) * qty;
    }, 0);
  };

  const calculateTax = (subtotal) => subtotal * taxRate;
  const calculateTip = (subtotal) =>
    isCustom ? parseFloat(customTip || 0) : subtotal * tipPercent;
  
  const subtotal = calculateSubtotal();
  const tax = calculateTax(subtotal);
  const tip = isCustom ? parseFloat(customTip || 0) : calculateTip(subtotal);
  const total = subtotal + tax + tip;
  
  
  const handleCheckout = async () => {
    const username = getLoggedInUser();
    if (!username) {
      username = "guest";
    }

    let latestTaxRate = taxRate;
    try {
		const res = await getTaxRate();
		if (res.data !== undefined) {
		  latestTaxRate = res.data / 100;
		  setTaxRate(latestTaxRate);
		}
    } catch (err) {
      console.warn("Failed to refresh tax rate, using cached value.");
    }

    const itemsOrdered = Object.entries(basket).flatMap(([id, qty]) => {
      const item = menuItems.find((i) => i.id === parseInt(id));
      return Array(qty).fill(item.name);
    });

    const subtotal = calculateSubtotal();
    const tax = subtotal * latestTaxRate;
    const tip = isCustom ? parseFloat(customTip || 0) : subtotal * tipPercent;
    const total = subtotal + tax + tip;

    const orderDto = {
      customerUsername: username,
      amountPaid: total,
      itemsOrdered,
      tipAmountDollars: tip,
      taxRate: latestTaxRate,
    };

    placeOrder(orderDto)
      .then(() => {
        alert("Order placed!");
        setBasket({});
      })
      .catch((err) => {
        console.error("Order failed:", err);
        alert("Failed to place order.");
      });
  };





    return (
      <div className="container mt-4">
        <h2>Drink Menu</h2>

        <div className="row">
          {menuItems.map((item) => (
            <div key={item.id} className="col-md-4 mb-3">
              <div className="card">
		     	<img
			      src={getImageForItem(item.name)}
			      alt={item.name}
			      className="card-img-top"
			      style={{ height: "200px", objectFit: "cover" }}
			    />

                <div className="card-body">
                  <h5 className="card-title">{item.name}</h5>
                  <p className="card-text">${item.price.toFixed(2)}</p>
                  <button
                    className="btn btn-primary"
                    onClick={() => addToBasket(item)}
                  >
                    Add
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>

        <h4 className="mt-5">My Basket</h4>
        <ul className="list-group mb-3">
          {Object.entries(basket).map(([id, qty]) => {
            const item = menuItems.find((i) => i.id === parseInt(id));
            return (
              <li
                key={id}
                className="list-group-item d-flex justify-content-between align-items-center"
              >
                {item?.name} (x{qty})
                <div>
                  <button
                    className="btn btn-sm btn-danger me-2"
                    onClick={() => removeFromBasket(id)}
                  >
                    -
                  </button>
                  <button
                    className="btn btn-sm btn-success"
                    onClick={() => addToBasket(item)}
                  >
                    +
                  </button>
                </div>
              </li>
            );
          })}
        </ul>

        <div className="mb-3">
          <label className="form-label me-2">Tip:</label>
          {[0.15, 0.2, 0.25].map((val) => (
            <button
              key={val}
              onClick={() => {
                setTipPercent(val);
                setIsCustom(false);
              }}
              className={`btn btn-sm me-2 ${
                !isCustom && tipPercent === val
                  ? "btn-success"
                  : "btn-outline-secondary"
              }`}
            >
              {(val * 100).toFixed(0)}%
            </button>
          ))}
          <button
            className={`btn btn-sm ${
              isCustom ? "btn-success" : "btn-outline-secondary"
            }`}
            onClick={() => {
              setIsCustom(true);
              setTipPercent(0);
            }}
          >
            Custom
          </button>

          {isCustom && (
            <input
              type="number"
              className="form-control mt-2"
              placeholder="Enter custom tip $"
              value={customTip}
              onChange={(e) => setCustomTip(e.target.value)}
            />
          )}
        </div>

        <h5>Subtotal: ${subtotal.toFixed(2)}</h5>
		<h5>Tax ({(taxRate * 100).toFixed(2)}%): ${tax.toFixed(2)}</h5>
        <h5>Tip: ${tip.toFixed(2)}</h5>
        <h4>Total: ${total.toFixed(2)}</h4>

        <button className="btn btn-lg btn-success mt-3" onClick={handleCheckout}>
          Checkout
        </button>
      </div>
    );
  };

  export default PlaceOrderComponent;