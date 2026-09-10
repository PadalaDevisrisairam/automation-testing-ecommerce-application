import hashlib
import hmac
import os

from app.models import User, Product
from app.database import SessionLocal


def _hash_password(password: str) -> str:
    """Hash a password using PBKDF2-HMAC-SHA256 with a random salt."""
    salt = os.urandom(16)
    key = hashlib.pbkdf2_hmac("sha256", password.encode(), salt, 260_000)
    # Store as hex: salt_hex$key_hex
    return f"{salt.hex()}${key.hex()}"

DEMO_USER = {
    "email": "test@example.com",
    "password": "password123",
}

SAMPLE_PRODUCTS = [
    {
        "name": "Wireless Noise-Cancelling Headphones",
        "description": "Premium over-ear headphones with 30-hour battery life and active noise cancellation.",
        "price": 79.99,
        "image_url": "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400",
        "category": "Electronics",
    },
    {
        "name": "Mechanical Keyboard",
        "description": "Compact TKL mechanical keyboard with RGB backlight and tactile brown switches.",
        "price": 54.99,
        "image_url": "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=400",
        "category": "Electronics",
    },
    {
        "name": "Running Shoes",
        "description": "Lightweight breathable running shoes with cushioned sole, perfect for daily training.",
        "price": 89.99,
        "image_url": "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400",
        "category": "Footwear",
    },
    {
        "name": "Stainless Steel Water Bottle",
        "description": "Insulated 32 oz water bottle that keeps drinks cold for 24 hours or hot for 12 hours.",
        "price": 24.99,
        "image_url": "https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=400",
        "category": "Kitchen",
    },
    {
        "name": "Yoga Mat",
        "description": "Non-slip eco-friendly yoga mat, 6mm thick with alignment lines and carry strap.",
        "price": 34.99,
        "image_url": "https://images.unsplash.com/photo-1601925228518-8d6c5b9c7d8c?w=400",
        "category": "Sports",
    },
    {
        "name": "Backpack",
        "description": "Durable 30L laptop backpack with USB charging port and waterproof exterior.",
        "price": 49.99,
        "image_url": "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400",
        "category": "Bags",
    },
    {
        "name": "Desk Lamp",
        "description": "LED desk lamp with adjustable brightness, color temperature, and USB-C charging port.",
        "price": 39.99,
        "image_url": "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=400",
        "category": "Home",
    },
    {
        "name": "Wireless Mouse",
        "description": "Ergonomic wireless mouse with silent clicks, 18-month battery life, and 3 DPI settings.",
        "price": 29.99,
        "image_url": "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=400",
        "category": "Electronics",
    },
]


def seed_database():
    """Populate the database with a demo user and sample products if not already present."""
    db = SessionLocal()
    try:
        # Seed user
        existing_user = db.query(User).filter(User.email == DEMO_USER["email"]).first()
        if not existing_user:
            hashed = _hash_password(DEMO_USER["password"])
            user = User(email=DEMO_USER["email"], hashed_password=hashed)
            db.add(user)
            print(f"[seed] Created demo user: {DEMO_USER['email']}")

        # Seed products
        existing_products = db.query(Product).count()
        if existing_products == 0:
            for p in SAMPLE_PRODUCTS:
                product = Product(**p)
                db.add(product)
            print(f"[seed] Created {len(SAMPLE_PRODUCTS)} sample products.")

        db.commit()
    finally:
        db.close()
