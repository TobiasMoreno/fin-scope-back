package tobias.moreno.fin.scope.services.interfaces;


import tobias.moreno.fin.scope.entities.RoleEntity;

public interface RoleService {
    RoleEntity findByName (String roleName);
}
