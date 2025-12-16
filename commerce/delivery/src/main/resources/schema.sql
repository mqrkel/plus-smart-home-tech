DROP TABLE IF EXISTS deliveries;
DROP TABLE IF EXISTS address;

CREATE TABLE IF NOT EXISTS address (
    address_id UUID PRIMARY KEY,
    country VARCHAR(30) NOT NULL,
    city VARCHAR(30) NOT NULL,
    street VARCHAR(50) NOT NULL,
    house VARCHAR(30) NOT NULL,
    flat VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS deliveries (
    delivery_id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    from_address_id UUID NOT NULL REFERENCES address(address_id),
    to_address_id UUID NOT NULL REFERENCES address(address_id),
    state VARCHAR(10) NOT NULL,
    CONSTRAINT deliveries_from_address_fk FOREIGN KEY (from_address_id) REFERENCES address(address_id) ON DELETE CASCADE,
    CONSTRAINT deliveries_to_address_fk FOREIGN KEY (to_address_id) REFERENCES address(address_id) ON DELETE CASCADE
);