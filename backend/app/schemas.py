from pydantic import BaseModel, EmailStr
from typing import List


# ── Auth ──────────────────────────────────────────────────────────────────────

class LoginRequest(BaseModel):
    email: str
    password: str


class LoginResponse(BaseModel):
    message: str
    user_id: int
    email: str


# ── Products ──────────────────────────────────────────────────────────────────

class ProductResponse(BaseModel):
    id: int
    name: str
    description: str
    price: float
    image_url: str
    category: str

    model_config = {"from_attributes": True}


# ── Cart ──────────────────────────────────────────────────────────────────────

class CartAddRequest(BaseModel):
    user_id: int
    product_id: int
    quantity: int = 1


class CartItemResponse(BaseModel):
    product_id: int
    name: str
    price: float
    quantity: int
    subtotal: float

    model_config = {"from_attributes": True}


class CartResponse(BaseModel):
    items: List[CartItemResponse]
    total: float
