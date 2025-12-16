DROP TABLE IF EXISTS order_products;
DROP TABLE IF EXISTS orders;

CREATE TABLE IF NOT EXISTS orders (
    order_id UUID PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    cart_id UUID NOT NULL,
    payment_id UUID NOT NULL,
    delivery_id UUID NOT NULL,
    state VARCHAR(20) NOT NULL,
    delivery_weight NUMERIC(19, 4) NOT NULL,
    delivery_volume NUMERIC(19, 4) NOT NULL,
    fragile BOOLEAN NOT NULL,
    total_price NUMERIC(19, 4) NOT NULL,
    delivery_price NUMERIC(19, 4) NOT NULL,
    product_price NUMERIC(19, 4) NOT NULL
);

CREATE TABLE IF NOT EXISTS order_products (
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity BIGINT,
    CONSTRAINT order_products_pk PRIMARY KEY (order_id, product_id),
    CONSTRAINT order_products_order_fk FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
);