const API_BASE = import.meta.env.VITE_API_BASE_URL || "http://localhost:8000";

/**
 * Login a user with email and password.
 * Returns { user_id, email, message } on success.
 * Throws an Error with the server message on failure.
 */
export async function login(email, password) {
  const response = await fetch(`${API_BASE}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password }),
  });

  const data = await response.json();

  if (!response.ok) {
    throw new Error(data.detail || "Login failed.");
  }

  return data;
}

/**
 * Fetch all products, optionally filtered by name.
 */
export async function getProducts(search = "") {
  const url = search
    ? `${API_BASE}/products?search=${encodeURIComponent(search)}`
    : `${API_BASE}/products`;

  const response = await fetch(url);
  if (!response.ok) throw new Error("Failed to fetch products.");
  return response.json();
}

/**
 * Add a product to a user's cart.
 */
export async function addToCart(userId, productId, quantity = 1) {
  const response = await fetch(`${API_BASE}/cart`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ user_id: userId, product_id: productId, quantity }),
  });

  const data = await response.json();
  if (!response.ok) throw new Error(data.detail || "Failed to add to cart.");
  return data;
}

/**
 * Get the cart for a user.
 */
export async function getCart(userId) {
  const response = await fetch(`${API_BASE}/cart/${userId}`);
  if (!response.ok) throw new Error("Failed to fetch cart.");
  return response.json();
}

/**
 * Remove a product from the user's cart.
 */
export async function removeFromCart(userId, productId) {
  const response = await fetch(`${API_BASE}/cart/${userId}/${productId}`, {
    method: "DELETE",
  });

  const data = await response.json();
  if (!response.ok) throw new Error(data.detail || "Failed to remove item.");
  return data;
}
