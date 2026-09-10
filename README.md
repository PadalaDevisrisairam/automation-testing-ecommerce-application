# E-Commerce Web Application with Automated Testing

A learning POC and portfolio project demonstrating a full-stack e-commerce application with end-to-end automated testing.

---

## Project Overview

A simple, functional e-commerce app where users can log in, browse products, search by name, add items to a cart, and remove them. The entire user journey is covered by an automated Selenium test suite.

---

## Architecture

### Application Stack

```
React Frontend  (Vite, port 5173)
      │
      │  REST API (JSON over HTTP)
      ▼
FastAPI Backend  (Uvicorn, port 8000)
      │
      │  SQLAlchemy ORM
      ▼
 SQLite Database  (ecommerce.db)
```

### Automated Testing Stack

```
Selenium + Pytest
      │
      │  Controls
      ▼
Chrome Browser
      │
      │  Loads
      ▼
React Application  (http://localhost:5173)
      │
      │  Calls
      ▼
FastAPI API  (http://localhost:8000)
      │
      │  Reads / Writes
      ▼
SQLite Database
```

---

## Features

| Feature | Description |
|---------|-------------|
| Authentication | Email/password login with bcrypt hashing |
| Products | Display 8 sample products with name, description, price, category, and image |
| Search | Real-time product search by name (debounced, server-side filtering) |
| Cart | Add products, view quantity and subtotals, calculate total, remove items |
| Protected Routes | Unauthenticated users are redirected to `/login` |
| Session Persistence | Login state persisted in `sessionStorage` across page refreshes |
| Stable Test Selectors | `data-testid` attributes on all key UI elements |

---

## Technologies Used

| Layer | Technology |
|-------|-----------|
| Frontend | React 19, Vite 8, React Router v7 |
| Backend | Python, FastAPI, Uvicorn |
| ORM | SQLAlchemy 2 |
| Database | SQLite |
| Password Hashing | passlib (bcrypt) |
| Testing Framework | Pytest |
| Browser Automation | Selenium WebDriver 4 |
| ChromeDriver | webdriver-manager (auto-download) |

---

## Project Structure

```
ecommerce-automation-poc/
│
├── frontend/                        # React + Vite application
│   ├── src/
│   │   ├── components/
│   │   │   ├── Navbar.jsx           # Navigation bar with cart link and logout
│   │   │   ├── ProductCard.jsx      # Product display card with Add to Cart button
│   │   │   └── SearchBar.jsx        # Controlled search input
│   │   ├── pages/
│   │   │   ├── Login.jsx            # Login form page
│   │   │   ├── Products.jsx         # Product listing page with search
│   │   │   └── Cart.jsx             # Shopping cart page
│   │   ├── services/
│   │   │   └── api.js               # All fetch() calls to the FastAPI backend
│   │   ├── App.jsx                  # Router, auth state, protected routes
│   │   └── main.jsx                 # React entry point
│   ├── index.html
│   ├── vite.config.js               # Vite config with dev proxy to :8000
│   └── package.json
│
├── backend/                         # FastAPI application
│   ├── app/
│   │   ├── main.py                  # FastAPI app, CORS, router registration, startup
│   │   ├── database.py              # SQLAlchemy engine and session factory
│   │   ├── models.py                # ORM models: User, Product, CartItem
│   │   ├── schemas.py               # Pydantic request/response models
│   │   ├── seed_data.py             # Demo user and sample products
│   │   └── routers/
│   │       ├── auth.py              # POST /auth/login
│   │       ├── products.py          # GET /products?search=
│   │       └── cart.py              # POST /cart, GET /cart/{id}, DELETE /cart/{id}/{pid}
│   ├── requirements.txt
│   └── README.md
│
├── selenium-tests/                  # Pytest + Selenium test suite
│   ├── pages/                       # Page Object Model classes
│   │   ├── login_page.py
│   │   ├── products_page.py
│   │   └── cart_page.py
│   ├── tests/
│   │   ├── test_login.py            # Tests 1, 2, 3
│   │   ├── test_products.py         # Test 4
│   │   └── test_cart.py             # Tests 5, 6, 7
│   ├── conftest.py                  # WebDriver setup and per-test reset fixture
│   ├── pytest.ini
│   ├── requirements.txt
│   └── README.md
│
├── .gitignore
└── README.md                        ← You are here
```

---

## Setup Instructions

### Prerequisites

- Python 3.9 or higher
- Node.js 18 or higher
- Google Chrome (latest)
- Git

---

### 1. Start the FastAPI Backend

```bash
cd backend

# Create and activate a virtual environment
python -m venv venv
venv\Scripts\activate          # Windows
# source venv/bin/activate     # macOS / Linux

# Install dependencies
pip install -r requirements.txt

# Start the server (database is created and seeded automatically)
uvicorn app.main:app --reload --port 8000
```

The API will be available at:
- Base URL: http://localhost:8000
- Interactive API docs: http://localhost:8000/docs

---

### 2. Start the React Frontend

Open a **second terminal**:

```bash
cd frontend

# Dependencies are already installed (or run: npm install)
npm run dev
```

The app will be available at: http://localhost:5173

---

### 3. Install Selenium Test Dependencies

Open a **third terminal**:

```bash
cd selenium-tests

python -m venv venv
venv\Scripts\activate          # Windows
# source venv/bin/activate     # macOS / Linux

pip install -r requirements.txt
```

ChromeDriver is downloaded automatically the first time tests run.

---

### 4. Run the Automated Tests

Make sure both the backend (`:8000`) and frontend (`:5173`) are running, then:

```bash
cd selenium-tests

# Run all 21 tests
pytest

# Run with a detailed HTML report
pytest --html=report.html --self-contained-html

# Run a specific test file
pytest tests/test_login.py
pytest tests/test_products.py
pytest tests/test_cart.py

# Run verbose with full error output
pytest -v --tb=long
```

To run in **headless mode** (no browser window), open `conftest.py` and set:

```python
HEADLESS = True
```

---

## Demo Credentials

| Field | Value |
|-------|-------|
| Email | `test@example.com` |
| Password | `password123` |

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Health check |
| POST | `/auth/login` | Login with email + password |
| GET | `/products` | List all products |
| GET | `/products?search=keyboard` | Filter products by name |
| POST | `/cart` | Add item to cart |
| GET | `/cart/{user_id}` | Get cart for a user |
| DELETE | `/cart/{user_id}/{product_id}` | Remove item from cart |

---

## Automated Test Cases

| # | Test Class | Description |
|---|-----------|-------------|
| 1 | `TestApplicationLoad` | App loads, correct page title shown, form elements present |
| 2 | `TestValidLogin` | Valid credentials redirect to products, products grid visible |
| 3 | `TestInvalidLogin` | Wrong password/email shows error, stays on login page |
| 4 | `TestProductSearch` | Search filters products, clearing search restores full list |
| 5 | `TestAddToCart` | Add to cart shows notification, product appears in cart |
| 6 | `TestVerifyCart` | Correct name, quantity indicator, and total price displayed |
| 7 | `TestRemoveFromCart` | Item removed, count decreases, empty state shown |

**Total: 21 individual test cases** across 3 test files.

---

## data-testid Reference

| Element | `data-testid` |
|---------|--------------|
| Email input | `email-input` |
| Password input | `password-input` |
| Login button | `login-button` |
| Login error message | `login-error` |
| Product search bar | `product-search` |
| Product card | `product-card` |
| Add to cart button | `add-to-cart-button` |
| Cart nav link | `cart-link` |
| Cart item row | `cart-item` |
| Remove from cart button | `remove-from-cart-button` |
| Cart total | `cart-total` |

---

## Future Improvements

- JWT-based authentication with access/refresh tokens
- User registration and multiple accounts
- Product detail pages and image galleries
- Quantity adjustment in the cart
- Checkout flow with order history
- Pagination and category filtering for products
- Pytest-xdist for parallel test execution
- CI/CD pipeline (GitHub Actions) running tests on every push
- Docker Compose setup for one-command startup
- Screenshot capture on test failure

---

## Assumptions & Limitations

- **Single user only.** The demo is seeded with one account for simplicity.
- **No real payment processing.** The checkout button is a placeholder.
- **SQLite is not production-grade.** It is ideal for a local POC; swap in PostgreSQL for production.
- **No JWT.** Auth state is stored in `sessionStorage`; sessions are lost on tab close.
- **Cart is per user_id.** The `user_id` is stored client-side after login — sufficient for a demo, not for a real app.
- **ChromeDriver must match your installed Chrome version.** `webdriver-manager` handles this automatically.
