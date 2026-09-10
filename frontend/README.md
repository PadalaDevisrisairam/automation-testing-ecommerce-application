# Frontend – React E-Commerce App

Built with **React 19**, **Vite 8**, and **React Router v7**.

## Setup

```bash
cd frontend
npm install
```

## Run

```bash
npm run dev
```

App URL: http://localhost:5173

## Pages

| Route | Description |
|-------|-------------|
| `/login` | Login page (redirects to products on success) |
| `/products` | Product listing with search |
| `/cart` | Shopping cart with totals |

## data-testid attributes

| Element | data-testid |
|---------|-------------|
| Email input | `email-input` |
| Password input | `password-input` |
| Login button | `login-button` |
| Login error | `login-error` |
| Search bar | `product-search` |
| Product card | `product-card` |
| Add to cart button | `add-to-cart-button` |
| Cart nav link | `cart-link` |
| Cart item row | `cart-item` |
| Remove button | `remove-from-cart-button` |
| Cart total | `cart-total` |
