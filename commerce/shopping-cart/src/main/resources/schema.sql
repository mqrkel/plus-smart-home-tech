DROP TABLE IF EXISTS cart_products;
DROP TABLE IF EXISTS carts;

CREATE TABLE IF NOT EXISTS carts
(
    cart_id  UUID PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    status    VARCHAR(10)  NOT NULL
);

CREATE TABLE IF NOT EXISTS cart_products
(
    cart_id    UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity   BIGINT,
    CONSTRAINT cart_products_pk PRIMARY KEY (cart_id, product_id),
    CONSTRAINT cart_products_cart_fk FOREIGN KEY (cart_id) REFERENCES carts (cart_id) ON DELETE CASCADE
);