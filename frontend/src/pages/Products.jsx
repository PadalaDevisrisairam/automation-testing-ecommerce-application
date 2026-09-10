import { useState, useEffect, useCallback } from "react";
import { getProducts, addToCart } from "../services/api";
import ProductCard from "../components/ProductCard";
import SearchBar from "../components/SearchBar";

export default function Products({ user }) {
  const [products, setProducts] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [notification, setNotification] = useState("");

  const fetchProducts = useCallback(async (searchTerm) => {
    setLoading(true);
    try {
      const data = await getProducts(searchTerm);
      setProducts(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, []);

  // Debounce search — refetch 300ms after the user stops typing
  useEffect(() => {
    const timer = setTimeout(() => {
      fetchProducts(search);
    }, 300);
    return () => clearTimeout(timer);
  }, [search, fetchProducts]);

  async function handleAddToCart(product) {
    if (!user) return;
    try {
      await addToCart(user.user_id, product.id);
      showNotification(`"${product.name}" added to cart!`);
    } catch (err) {
      showNotification(`Error: ${err.message}`);
    }
  }

  function showNotification(message) {
    setNotification(message);
    setTimeout(() => setNotification(""), 3000);
  }

  return (
    <div className="page-container">
      <div className="page-header">
        <h1>Products</h1>
        <SearchBar value={search} onChange={setSearch} />
      </div>

      {notification && (
        <div className="notification" role="status">
          {notification}
        </div>
      )}

      {loading ? (
        <div className="loading">Loading products...</div>
      ) : products.length === 0 ? (
        <div className="empty-state">
          <p>No products found{search ? ` for "${search}"` : ""}.</p>
        </div>
      ) : (
        <div className="products-grid" data-testid="products-grid">
          {products.map((product) => (
            <ProductCard
              key={product.id}
              product={product}
              onAddToCart={handleAddToCart}
            />
          ))}
        </div>
      )}
    </div>
  );
}
