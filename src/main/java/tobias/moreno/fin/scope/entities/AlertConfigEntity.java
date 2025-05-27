package tobias.moreno.fin.scope.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "alert_configs")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AlertConfigEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "check_interval")
	private Long checkInterval;

	@Column(name = "notify_email")
	private boolean notifyEmail;

	@OneToOne
	@JoinColumn(name = "user_id")
	private UserEntity userEntity;
}
