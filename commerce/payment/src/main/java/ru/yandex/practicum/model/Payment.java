package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.interaction.api.payment.PaymentState;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID paymentId;

    private UUID orderId;

    @Column(precision = 19, scale = 4)
    private BigDecimal totalPayment;

    @Column(precision = 19, scale = 4)
    private BigDecimal deliveryTotal;

    @Column(precision = 19, scale = 4)
    private BigDecimal feeTotal;

    @Enumerated(EnumType.STRING)
    private PaymentState state;
}