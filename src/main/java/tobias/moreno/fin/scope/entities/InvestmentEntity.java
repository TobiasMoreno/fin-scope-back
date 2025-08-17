package tobias.moreno.fin.scope.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import tobias.moreno.fin.scope.models.InvestmentType;

import java.math.BigDecimal;

@Entity
@Table(name = "investments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InvestmentEntity extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal quantity;

    @Column(name = "last_price", nullable = false, precision = 19, scale = 6)
    private BigDecimal lastPrice;

    @Column(name = "daily_variation_percent", precision = 8, scale = 4)
    private BigDecimal dailyVariationPercent;

    @Column(name = "daily_variation_amount", precision = 19, scale = 6)
    private BigDecimal dailyVariationAmount;

    @Column(name = "cost_per_share", nullable = false, precision = 19, scale = 6)
    private BigDecimal costPerShare;

    @Column(name = "gain_loss_percent", precision = 8, scale = 4)
    private BigDecimal gainLossPercent;

    @Column(name = "gain_loss_amount", precision = 19, scale = 6)
    private BigDecimal gainLossAmount;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvestmentType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;
}
