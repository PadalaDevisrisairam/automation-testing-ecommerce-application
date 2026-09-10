export default function SearchBar({ value, onChange }) {
  return (
    <div className="search-bar">
      <input
        type="text"
        placeholder="Search products..."
        value={value}
        onChange={(e) => onChange(e.target.value)}
        data-testid="product-search"
        className="search-input"
        aria-label="Search products"
      />
    </div>
  );
}
