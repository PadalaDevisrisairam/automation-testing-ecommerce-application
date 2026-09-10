from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import List

from app.database import get_db
from app.models import CartItem, Product, User
from app.schemas import CartAddRequest, CartItemResponse, CartResponse

router = APIRouter(prefix="/cart", tags=["cart"])


def _build_cart_response(cart_items: List[CartItem]) -> CartResponse:
    """Build a CartResponse from a list of ORM CartItem objects."""
    items = []
    for item in cart_items:
        subtotal = round(item.product.price * item.quantity, 2)
        items.append(
            CartItemResponse(
                product_id=item.product_id,
                name=item.product.name,
                price=item.product.price,
                quantity=item.quantity,
                subtotal=subtotal,
            )
        )
    total = round(sum(i.subtotal for i in items), 2)
    return CartResponse(items=items, total=total)


@router.post("", response_model=CartResponse, status_code=status.HTTP_201_CREATED)
def add_to_cart(request: CartAddRequest, db: Session = Depends(get_db)):
    """Add a product to the user's cart, or increment quantity if it already exists."""
    # Validate user
    user = db.query(User).filter(User.id == request.user_id).first()
    if not user:
        raise HTTPException(status_code=404, detail="User not found.")

    # Validate product
    product = db.query(Product).filter(Product.id == request.product_id).first()
    if not product:
        raise HTTPException(status_code=404, detail="Product not found.")

    # Check if item already in cart
    existing = (
        db.query(CartItem)
        .filter(CartItem.user_id == request.user_id, CartItem.product_id == request.product_id)
        .first()
    )

    if existing:
        existing.quantity += request.quantity
    else:
        new_item = CartItem(
            user_id=request.user_id,
            product_id=request.product_id,
            quantity=request.quantity,
        )
        db.add(new_item)

    db.commit()

    # Return updated cart
    cart_items = (
        db.query(CartItem).filter(CartItem.user_id == request.user_id).all()
    )
    return _build_cart_response(cart_items)


@router.get("/{user_id}", response_model=CartResponse)
def get_cart(user_id: int, db: Session = Depends(get_db)):
    """Retrieve all cart items for a user."""
    user = db.query(User).filter(User.id == user_id).first()
    if not user:
        raise HTTPException(status_code=404, detail="User not found.")

    cart_items = db.query(CartItem).filter(CartItem.user_id == user_id).all()
    return _build_cart_response(cart_items)


@router.delete("/{user_id}/{product_id}", response_model=CartResponse)
def remove_from_cart(user_id: int, product_id: int, db: Session = Depends(get_db)):
    """Remove a specific product from the user's cart."""
    item = (
        db.query(CartItem)
        .filter(CartItem.user_id == user_id, CartItem.product_id == product_id)
        .first()
    )
    if not item:
        raise HTTPException(status_code=404, detail="Cart item not found.")

    db.delete(item)
    db.commit()

    cart_items = db.query(CartItem).filter(CartItem.user_id == user_id).all()
    return _build_cart_response(cart_items)
