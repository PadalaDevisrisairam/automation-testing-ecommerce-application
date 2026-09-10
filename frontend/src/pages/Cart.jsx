import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getCart, removeFromCart } from "../services/api";

export default function Cart({ user }) {
  const [cart, setCart] = useState({ items: [], total: 0 });
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    if (!user) {
      navigate("/login");
      return;
    }
    fetchCart();
  }, [user]);

  async function fetchCart() {
    setLoading(true);
    try {
      const data = await getCart(user.user_id);
      setCart(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  }

  async function handleRemove(productId) {
    try {
      const updated = await removeFromCart(user.user_id, productId);
      setCart(updated);
    } catch (err) {
      console.error(err);
    }
  }

  if (loading) return <div className="loading">Loading cart...</div>;

  return (
    <div className="page-container">
      <div className="page-header">
        <h1>Shopping Cart</h1>
      </div>

      {cart.items.length === 0 ? (
        <div className="empty-state">
          <p>Your cart is empty.</p>
          <button className="btn btn-primary" onClick={() => navigate("/products")}>
            Browse Products
          </button>
        </div>
      ) : (
        <div className="cart-container">
          <div className="cart-items">
            {cart.items.map((item) => (
              <div
                key={item.product_id}
                className="cart-item"
                data-testid="cart-item"
              >
                <div className="cart-item-info">
                  <h3 className="cart-item-name">{item.name}</h3>
                  <p className="cart-item-price">
                    ${item.price.toFixed(2)} × {item.quantity}
                  </p>
                </div>
                <div className="cart-item-right">
                  <span className="cart-item-subtotal">
                    ${item.subtotal.toFixed(2)}
                  </span>
                  <button
                    className="btn btn-danger"
                    data-testid="remove-from-cart-button"
                    onClick={() => handleRemove(item.product_id)}
                    aria-label={`Remove ${item.name} from cart`}
                  >
                    Remove
                  </button>
                </div>
              </div>
            ))}
          </div>

          <div className="cart-summary">
            <div className="cart-total">
              <span>Total</span>
              <span data-testid="cart-total">${cart.total.toFixed(2)}</span>
            </div>
            <button className="btn btn-primary btn-full">
              Proceed to Checkout
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
