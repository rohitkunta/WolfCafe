import React, { useState, useEffect } from 'react';
import { updateTaxRate } from '../services/orderService'; // Adjust the import path as needed
import { getTaxRate } from '../services/orderService';

function TaxRateComponent() {
  const [currentRate, setCurrentRate] = useState(0); // Default starting rate
  const [newRate, setNewRate] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  
  useEffect(() => {
    getTaxRate()
      .then((res) => {
		if (res.data !== undefined) {
		  setCurrentRate(res.data);
		}
      })
      .catch((err) => {
        console.error('Failed to fetch tax rate:', err);
      });
  }, []);
  
  
  function handleSetRate(e) {
    e.preventDefault();
    setError('');
    setSuccess('');

    const parsedRate = parseFloat(newRate);

    if (isNaN(parsedRate) || parsedRate < 0 || parsedRate > 100) {
      setError('Invalid Tax Rate, please try again');
      return;
    }

	
	
	updateTaxRate(parsedRate)
	    .then(() => {
	      return getTaxRate();
	    })
	    .then((res) => {
	      if (res.data?.rate !== undefined) {
	        setCurrentRate(res.data.rate);
	        setNewRate('');
	        setSuccess('Tax rate updated successfully.');
	      }
	    })
	    .catch(() => {
	      setError('Invalid Tax Rate, please try again');
	    });
	}

  return (
    <div className="container mt-5">
      <div className="card p-4 shadow-sm" style={{ maxWidth: '400px', margin: '0 auto' }}>
        <h2 className="text-center mb-3">Tax Rate</h2>
        <p className="text-center">Current Rate: {currentRate}%</p>

        <form onSubmit={handleSetRate}>
          <div className="mb-3">
            <label className="form-label">New Rate:</label>
            <div className="input-group">
              <input
                type="text"
                className="form-control"
                placeholder="Enter Rate"
                value={newRate}
                onChange={function (e) { setNewRate(e.target.value) }}
              />
              <span className="input-group-text">%</span>
            </div>
          </div>

          <div className="d-grid">
            <button type="submit" className="btn btn-primary">Set Rate</button>
          </div>

          {error && <p className="text-danger mt-2 text-center">{error}</p>}
          {success && <p className="text-success mt-2 text-center">{success}</p>}
        </form>
      </div>
    </div>
  );
}

export default TaxRateComponent;
