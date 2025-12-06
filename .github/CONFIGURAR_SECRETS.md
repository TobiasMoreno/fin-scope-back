# 🔐 Guía: Configurar Secrets de GitHub Actions

Esta guía te explica paso a paso cómo configurar los secrets necesarios para que GitHub Actions pueda publicar imágenes en Docker Hub.

## 📍 Ubicación de los Secrets

Los secrets se configuran en la configuración de tu repositorio de GitHub, no en el código.

## 🚀 Pasos para Configurar los Secrets

### Paso 1: Acceder a la Configuración del Repositorio

1. Ve a tu repositorio en GitHub (ej: `https://github.com/tu-usuario/fin-scope-back`)
2. Haz clic en la pestaña **Settings** (Configuración) en la parte superior del repositorio
   - Si no ves "Settings", necesitas permisos de administrador en el repositorio

### Paso 2: Navegar a Secrets

1. En el menú lateral izquierdo, busca la sección **"Secrets and variables"**
2. Haz clic en **"Actions"**
3. Verás una página con dos opciones:
   - **Repository secrets**: Secrets específicos de este repositorio (usa estos)
   - **Environment secrets**: Secrets para entornos específicos (no necesario ahora)

### Paso 3: Crear el Secret `DOCKER_USERNAME`

1. Haz clic en el botón **"New repository secret"** (Nuevo secret del repositorio)
2. En el campo **Name** (Nombre), escribe exactamente: `DOCKER_USERNAME`
3. En el campo **Secret** (Valor), escribe tu nombre de usuario de Docker Hub
   - Ejemplo: Si tu usuario de Docker Hub es `johndoe`, escribe `johndoe`
4. Haz clic en **"Add secret"** (Agregar secret)

### Paso 4: Crear el Secret `DOCKER_PASSWORD`

**⚠️ IMPORTANTE: Usa un Access Token, NO tu contraseña real**

#### 4.1. Crear Access Token en Docker Hub

1. Ve a [Docker Hub](https://hub.docker.com/) e inicia sesión
2. Haz clic en tu nombre de usuario (arriba a la derecha)
3. Selecciona **"Account Settings"** (Configuración de cuenta)
4. En el menú lateral, haz clic en **"Security"** (Seguridad)
5. Busca la sección **"Access Tokens"** (Tokens de acceso)
6. Haz clic en **"New Access Token"** (Nuevo token de acceso)
7. Dale un nombre descriptivo, por ejemplo: `github-actions-finscope`
8. Selecciona los permisos:
   - **Read & Write** (Lectura y escritura) - necesario para publicar imágenes
9. Haz clic en **"Generate"** (Generar)
10. **⚠️ COPIA EL TOKEN INMEDIATAMENTE** - Solo se muestra una vez
    - Ejemplo: `dckr_pat_xxxxxxxxxxxxxxxxxxxxxxxxxx`

#### 4.2. Agregar el Token como Secret en GitHub

1. Vuelve a GitHub → Settings → Secrets and variables → Actions
2. Haz clic en **"New repository secret"**
3. En **Name**, escribe: `DOCKER_PASSWORD`
4. En **Secret**, pega el Access Token que copiaste de Docker Hub
5. Haz clic en **"Add secret"**

## ✅ Verificar que los Secrets Están Configurados

Deberías ver en la lista de secrets:
- ✅ `DOCKER_USERNAME` (con valor oculto: `••••••••`)
- ✅ `DOCKER_PASSWORD` (con valor oculto: `••••••••`)

## 🔍 Cómo Funciona en el Workflow

En el archivo `.github/workflows/backend-ci.yml`, estos secrets se usan así:

```yaml
- name: Log in to Docker Hub
  uses: docker/login-action@v3
  with:
    username: ${{ secrets.DOCKER_USERNAME }}      # ← Lee el secret
    password: ${{ secrets.DOCKER_PASSWORD }}      # ← Lee el secret
```

GitHub Actions automáticamente:
1. Lee los valores de los secrets
2. Los inyecta en el workflow
3. **Nunca los muestra en los logs** (por seguridad)

## 🛡️ Seguridad

- ✅ Los secrets están encriptados
- ✅ Solo usuarios con permisos de administrador pueden ver/editar secrets
- ✅ Los valores nunca aparecen en los logs de GitHub Actions
- ✅ Si alguien hace fork de tu repositorio, NO tendrá acceso a tus secrets

## 🔄 Actualizar o Eliminar Secrets

1. Ve a Settings → Secrets and variables → Actions
2. Encuentra el secret que quieres modificar
3. Haz clic en el ícono de lápiz (editar) o de papelera (eliminar)
4. Para editar: Cambia el valor y guarda
5. Para eliminar: Confirma la eliminación

## ❓ Troubleshooting

### Error: "DOCKER_USERNAME not found"
- Verifica que el nombre del secret sea exactamente `DOCKER_USERNAME` (case-sensitive)
- Verifica que esté en "Repository secrets", no en "Environment secrets"

### Error: "authentication required" o "unauthorized"
- Verifica que el `DOCKER_USERNAME` sea correcto
- Verifica que el `DOCKER_PASSWORD` sea un Access Token válido (no tu contraseña)
- Verifica que el Access Token tenga permisos de "Read & Write"

### No puedo ver "Settings" en mi repositorio
- Necesitas ser **administrador** del repositorio
- Si es un repositorio de una organización, pide permisos a un administrador

## 📝 Resumen Rápido

1. GitHub → Tu Repositorio → **Settings**
2. **Secrets and variables** → **Actions**
3. **New repository secret** → `DOCKER_USERNAME` → Tu usuario de Docker Hub
4. **New repository secret** → `DOCKER_PASSWORD` → Access Token de Docker Hub
5. ✅ Listo - El workflow los usará automáticamente

