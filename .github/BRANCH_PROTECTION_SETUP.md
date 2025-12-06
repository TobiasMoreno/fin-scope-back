# Configuración de Branch Protection para Main

## 🛡️ Configurar Branch Protection Rules en GitHub

Para **prevenir completamente** commits directos a `main` (antes del push), sigue estos pasos:

### Pasos:

1. Ve a tu repositorio en GitHub
2. Navega a **Settings** → **Branches**
3. Haz clic en **Add rule** o **Add branch protection rule**
4. En **Branch name pattern**, escribe: `main`
5. Activa las siguientes opciones:

   ✅ **Require a pull request before merging**
   - ✅ Require approvals: `1` (o el número que prefieras)
   - ✅ Dismiss stale pull request approvals when new commits are pushed
   - ✅ Require review from Code Owners (opcional)

   ✅ **Require status checks to pass before merging**
   - ✅ Require branches to be up to date before merging
   - En "Status checks that are required", selecciona:
     - `validate-branch` (nuestro job de validación)
     - `test` (nuestro job de tests)

   ✅ **Require conversation resolution before merging** (opcional pero recomendado)

   ✅ **Do not allow bypassing the above settings** (importante para admins también)

6. Haz clic en **Create** o **Save changes**

### ⚠️ Importante:

- Con estas reglas, **NO podrás hacer push directo a main** desde la línea de comandos
- Todos los cambios deben pasar por un Pull Request
- El workflow de validación actúa como una capa adicional de seguridad

### 🔄 Flujo con Branch Protection:

1. Intentas hacer push directo → **GitHub lo rechaza** ❌
2. Creas un PR → Se ejecutan tests ✅
3. Mergeas el PR → Se ejecuta el workflow completo ✅
4. Se genera versión y se publica Docker ✅
