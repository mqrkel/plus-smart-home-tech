DROP TABLE IF EXISTS dimension CASCADE;
DROP TABLE IF EXISTS warehouse_product CASCADE;

CREATE TABLE dimension (
                           id UUID PRIMARY KEY,
                           width NUMERIC(19, 4) NOT NULL,
                           height NUMERIC(19, 4) NOT NULL,
                           depth NUMERIC(19, 4) NOT NULL
);
CREATE TABLE warehouse_product
(
    product_id   UUID PRIMARY KEY,
    weight       NUMERIC(19, 4) NOT NULL,
    fragile      BOOLEAN        NOT NULL,
    quantity     BIGINT         NOT NULL,
    dimension_id UUID           NOT NULL UNIQUE,
    CONSTRAINT fk_dimension FOREIGN KEY (dimension_id) REFERENCES dimension (id)
);
DROP TABLE IF EXISTS address CASCADE;

CREATE TABLE address (
                         id UUID PRIMARY KEY,
                         country VARCHAR(255) NOT NULL,
                         city    VARCHAR(255) NOT NULL,
                         street  VARCHAR(255) NOT NULL,
                         house   VARCHAR(255) NOT NULL,
                         flat    VARCHAR(255)
);


