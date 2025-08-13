package tobias.moreno.fin.scope.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
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
@lombok.experimental.SuperBuilder
public class UserEntity extends BaseEntity {

	@Column(nullable = false, unique = true)
	private String email;

	@Column(name = "name")
	private String name;

	@Column(name = "picture")
	private String picture;

	@Column(name = "provider")
	private String provider = "google";

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "users_roles",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id")
	)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<RoleEntity> roles;
}
