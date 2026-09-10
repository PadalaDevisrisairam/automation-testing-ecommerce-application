import { Link, useNavigate } from "react-router-dom";

export default function Navbar({ user, onLogout }) {
  const navigate = useNavigate();

  function handleLogout() {
    onLogout();
    navigate("/login");
  }

  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <Link to="/products">🛍️ ShopPOC</Link>
      </div>

      {user && (
        <div className="navbar-links">
          <Link to="/products">Products</Link>
          <Link to="/cart" data-testid="cart-link">
            🛒 Cart
          </Link>
          <span className="navbar-user">👤 {user.email}</span>
          <button className="btn btn-outline" onClick={handleLogout}>
            Logout
          </button>
        </div>
      )}
    </nav>
  );
}
