import { useState } from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Navbar from "./components/Navbar";
import Login from "./pages/Login";
import Products from "./pages/Products";
import Cart from "./pages/Cart";
import "./App.css";

function ProtectedRoute({ user, children }) {
  if (!user) return <Navigate to="/login" replace />;
  return children;
}

export default function App() {
  const [user, setUser] = useState(() => {
    // Persist login across page refreshes via sessionStorage
    const stored = sessionStorage.getItem("user");
    return stored ? JSON.parse(stored) : null;
  });

  function handleLogin(userData) {
    setUser(userData);
    sessionStorage.setItem("user", JSON.stringify(userData));
  }

  function handleLogout() {
    setUser(null);
    sessionStorage.removeItem("user");
  }

  return (
    <BrowserRouter>
      <Navbar user={user} onLogout={handleLogout} />
      <main>
        <Routes>
          <Route
            path="/login"
            element={
              user ? <Navigate to="/products" replace /> : <Login onLogin={handleLogin} />
            }
          />
          <Route
            path="/products"
            element={
              <ProtectedRoute user={user}>
                <Products user={user} />
              </ProtectedRoute>
            }
          />
          <Route
            path="/cart"
            element={
              <ProtectedRoute user={user}>
                <Cart user={user} />
              </ProtectedRoute>
            }
          />
          <Route path="*" element={<Navigate to={user ? "/products" : "/login"} replace />} />
        </Routes>
      </main>
    </BrowserRouter>
  );
}
