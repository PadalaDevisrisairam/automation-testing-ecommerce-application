from fastapi import APIRouter, Depends, Query
from sqlalchemy.orm import Session
from typing import List, Optional

from app.database import get_db
from app.models import Product
from app.schemas import ProductResponse

router = APIRouter(prefix="/products", tags=["products"])


@router.get("", response_model=List[ProductResponse])
def get_products(
    search: Optional[str] = Query(None, description="Filter products by name"),
    db: Session = Depends(get_db),
):
    """Return all products, optionally filtered by name."""
    query = db.query(Product)
    if search:
        query = query.filter(Product.name.ilike(f"%{search}%"))
    return query.all()
