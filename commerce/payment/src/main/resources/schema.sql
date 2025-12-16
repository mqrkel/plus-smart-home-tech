DROP TABLE IF EXISTS payments;

CREATE TABLE IF NOT EXISTS payments (
    payment_id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    total_payment NUMERIC(19, 4) NOT NULL,
    delivery_total NUMERIC(19, 4) NOT NULL,
    fee_total NUMERIC(19, 4) NOT NULL,
    state VARCHAR(10) NOT NULL
);