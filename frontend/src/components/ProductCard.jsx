export default function ProductCard({ product, onAddToCart }) {
  return (
    <div className="product-card" data-testid="product-card">
      <img
        src={product.image_url}
        alt={product.name}
        className="product-image"
        onError={(e) => {
          e.target.src = "https://placehold.co/400x300?text=No+Image";
        }}
      />
      <div className="product-info">
        <span className="product-category">{product.category}</span>
        <h3 className="product-name">{product.name}</h3>
        <p className="product-description">{product.description}</p>
        <div className="product-footer">
          <span className="product-price">${product.price.toFixed(2)}</span>
          <button
            className="btn btn-primary"
            data-testid="add-to-cart-button"
            onClick={() => onAddToCart(product)}
            aria-label={`Add ${product.name} to cart`}
          >
            Add to Cart
          </button>
        </div>
      </div>
    </div>
  );
}
