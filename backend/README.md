# Backend – FastAPI E-Commerce API

## Overview

REST API built with **FastAPI**, **SQLAlchemy**, and **SQLite**.  
The database is created and seeded automatically on first startup.

## Setup

```bash
cd backend
python -m venv venv
venv\Scripts\activate        # Windows
pip install -r requirements.txt
```

## Run

```bash
uvicorn app.main:app --reload --port 8000
```

API base URL: http://localhost:8000  
Interactive docs: http://localhost:8000/docs

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | /auth/login | Login with email + password |
| GET | /products | List products (optional ?search=) |
| POST | /cart | Add item to cart |
| GET | /cart/{user_id} | Get cart for user |
| DELETE | /cart/{user_id}/{product_id} | Remove item from cart |

## Demo Credentials

- **Email:** test@example.com  
- **Password:** password123
