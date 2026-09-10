from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.database import Base, engine
from app.routers import auth, products, cart
from app.seed_data import seed_database

# Create all database tables
Base.metadata.create_all(bind=engine)

# Seed the database with demo data on startup
seed_database()

app = FastAPI(
    title="E-Commerce API",
    description="Simple e-commerce REST API built with FastAPI and SQLite.",
    version="1.0.0",
)

# Allow the React dev server to communicate with this API
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:5173", "http://127.0.0.1:5173"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Register routers
app.include_router(auth.router)
app.include_router(products.router)
app.include_router(cart.router)


@app.get("/", tags=["health"])
def root():
    return {"status": "ok", "message": "E-Commerce API is running."}
