import hashlib
import hmac

from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session

from app.database import get_db
from app.models import User
from app.schemas import LoginRequest, LoginResponse

router = APIRouter(prefix="/auth", tags=["auth"])


def _verify_password(plain: str, stored: str) -> bool:
    """
    Verify a plain-text password against a stored PBKDF2-HMAC-SHA256 hash.
    Stored format: salt_hex$key_hex
    """
    try:
        salt_hex, key_hex = stored.split("$")
        salt = bytes.fromhex(salt_hex)
        expected_key = bytes.fromhex(key_hex)
    except (ValueError, AttributeError):
        return False

    candidate_key = hashlib.pbkdf2_hmac("sha256", plain.encode(), salt, 260_000)
    # Constant-time comparison to prevent timing attacks
    return hmac.compare_digest(candidate_key, expected_key)


@router.post("/login", response_model=LoginResponse)
def login(request: LoginRequest, db: Session = Depends(get_db)):
    """Authenticate a user with email and password."""
    user = db.query(User).filter(User.email == request.email).first()

    if not user or not _verify_password(request.password, user.hashed_password):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid email or password.",
        )

    return LoginResponse(
        message="Login successful",
        user_id=user.id,
        email=user.email,
    )
