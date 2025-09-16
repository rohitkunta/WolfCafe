import React from 'react';
import { Link } from 'react-router-dom';

const FooterComponent = () => {
  return (
    <div>
      <footer className='footer'>
        <p className='text-center'>
          <Link to="/privacy-policy" style={{ textDecoration: 'none', color: 'inherit' }}>
            WolfCafe &copy; 2024 - Privacy Policy
          </Link>
        </p>
      </footer>
    </div>
  );
};

export default FooterComponent;
