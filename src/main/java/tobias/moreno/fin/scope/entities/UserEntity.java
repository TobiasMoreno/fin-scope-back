package tobias.moreno.fin.scope.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false, of = "email")
public class UserEntity extends BaseEntity {

	@Column(nullable = false, unique = true)
	private String email;

	private String password;

	@Column(name = "is_enabled")
	private boolean isEnabled;

	@Column(name = "is_account_not_expired")
	private Boolean isAccountNotExpired;

	@Column(name = "is_account_not_locked")
	private Boolean isAccountNotLocked;

	@Column(name = "is_credential_not_expired")
	private Boolean isCredentialNotExpired;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "users_roles",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id")
	)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<RoleEntity> roles;
}
