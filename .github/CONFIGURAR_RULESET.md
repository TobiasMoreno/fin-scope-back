# 🛡️ Guía: Configurar Ruleset para Proteger Main

Esta guía te explica cómo configurar un Ruleset en GitHub para prevenir pushes directos a `main` sin PR.

## 📍 Ubicación

**Settings** → **Rulesets** (en la sección "Code and automation")

## 🚀 Pasos para Configurar el Ruleset

### Paso 1: Habilitar el Ruleset

1. En la página del Ruleset "main" que estás viendo
2. En **"Enforcement status"**, cambia de **"Disabled"** a **"Active"**
3. Esto habilitará el ruleset

### Paso 2: Configurar Target Branches (Ramas Objetivo)

1. En la sección **"Target branches"**, haz clic en **"Add target"**
2. Selecciona **"Branch name pattern"**
3. Escribe: `main`
4. Haz clic en **"Add"** o **"Save"**

Ahora el ruleset apuntará a la rama `main`.

### Paso 3: Configurar las Reglas

Necesitas agregar reglas para prevenir pushes directos. Busca la sección de reglas y agrega:

#### Regla 1: Require Pull Request

1. Busca la sección **"Rules"** o **"Add rule"**
2. Agrega la regla: **"Require pull request before merging"**
3. Configura:
   - ✅ **Require approvals**: `1` (o el número que prefieras)
   - ✅ **Require review from Code Owners**: (opcional)
   - ✅ **Dismiss stale pull request approvals when new commits are pushed**: (recomendado)

#### Regla 2: Bloquear Pushes Directos

1. Agrega la regla: **"Restrict pushes that create files"** o **"Require linear history"**
2. O mejor aún, busca: **"Require pull request"** y asegúrate de que esté activada
3. Esto bloqueará pushes directos a `main`

### Paso 4: Configurar Bypass (Opcional pero Recomendado)

Si quieres que ciertos usuarios/teams puedan hacer bypass (no recomendado para producción):

1. En **"Bypass list"**, haz clic en **"Add bypass"**
2. Agrega solo usuarios/teams de confianza (ej: administradores)
3. ⚠️ **Recomendación**: Deja la bypass list vacía para máxima seguridad

### Paso 5: Guardar

1. Haz clic en **"Save changes"** o **"Update ruleset"**
2. Verás un mensaje de confirmación: "Ruleset updated"

## ✅ Verificar que Funciona

Después de configurar:

1. Intenta hacer push directo a `main` desde tu terminal:
   ```bash
   git checkout main
   git commit --allow-empty -m "test direct push"
   git push origin main
   ```

2. Deberías recibir un error como:
   ```
   ! [remote rejected] main -> main (protected branch hook declined)
   ```

3. Esto significa que la protección está funcionando ✅

## 🔄 Flujo Correcto

Con el ruleset configurado:

1. ❌ **Push directo a main** → GitHub lo rechaza
2. ✅ **Crear branch** → `git checkout -b feature/nueva-funcionalidad`
3. ✅ **Hacer commits** → `git commit -m "cambios"`
4. ✅ **Push a branch** → `git push origin feature/nueva-funcionalidad`
5. ✅ **Crear PR en GitHub** → Pull Request hacia `main`
6. ✅ **Merge PR** → Se ejecuta el workflow completo

## ⚠️ Problemas Comunes

### "This ruleset does not target any resources"
- **Solución**: Agrega la rama `main` en "Target branches"

### "Ruleset is disabled"
- **Solución**: Cambia "Enforcement status" de "Disabled" a "Active"

### "Still can push directly"
- Verifica que el ruleset esté **"Active"**
- Verifica que tenga la rama `main` en "Target branches"
- Verifica que tengas la regla **"Require pull request"** activada
- Verifica que NO estés en la "Bypass list"

### "Can't see Rulesets option"
- Necesitas permisos de **administrador** en el repositorio
- Si es una organización, pide permisos a un administrador

## 📝 Resumen de Configuración

Tu ruleset debería tener:

- ✅ **Enforcement status**: `Active`
- ✅ **Target branches**: `main`
- ✅ **Rules**:
  - ✅ Require pull request before merging
  - ✅ Require approvals: 1
- ✅ **Bypass list**: Vacía (o solo administradores)

## 🔍 Alternativa: Branch Protection Rules (Método Clásico)

Si prefieres usar el método clásico de GitHub:

1. Ve a **Settings** → **Branches** (no Rulesets)
2. Haz clic en **"Add rule"** o **"Add branch protection rule"**
3. En **"Branch name pattern"**, escribe: `main`
4. Activa:
   - ✅ **Require a pull request before merging**
   - ✅ **Do not allow bypassing the above settings**
5. Guarda

Ambos métodos funcionan, pero **Rulesets** es más moderno y flexible.

