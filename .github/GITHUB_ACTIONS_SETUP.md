# Configuración de GitHub Actions para Backend

Este documento explica cómo configurar GitHub Actions para el repositorio del backend de FinScope.

> **Nota**: Este workflow está configurado para un repositorio dedicado al backend donde el código está en la raíz del repositorio.

## 📋 Requisitos Previos

1. Cuenta de Docker Hub
2. Repositorio de GitHub con permisos de administrador

## 🔐 Configuración de Secrets

Necesitas configurar los siguientes secrets en tu repositorio de GitHub:

> 📖 **Guía detallada**: Ver [CONFIGURAR_SECRETS.md](./CONFIGURAR_SECRETS.md) para instrucciones paso a paso con capturas de pantalla.

### Resumen rápido:

1. Ve a tu repositorio en GitHub → **Settings** → **Secrets and variables** → **Actions**
2. Haz clic en **New repository secret** y agrega:

#### `DOCKER_USERNAME`
- **Valor**: Tu nombre de usuario de Docker Hub
- **Ejemplo**: `tu-usuario-docker`

#### `DOCKER_PASSWORD`
- **Valor**: Un Access Token de Docker Hub (NO uses tu contraseña real)
- **Cómo obtenerlo**:
  1. Ve a [Docker Hub](https://hub.docker.com/) → Account Settings → Security
  2. Crea un nuevo Access Token con permisos "Read & Write"
  3. Copia el token y úsalo como valor del secret

> ⚠️ **Importante**: Los secrets se configuran en la interfaz web de GitHub, NO en el código.

## 🚀 Funcionamiento del Workflow

### Flujo Automático

1. **Pull Request a main/develop**:
   - Ejecuta tests
   - Valida el código
   - No publica imágenes

2. **Merge de PR a main**:
   - ✅ Valida que el commit provenga de un PR (bloquea commits directos)
   - ✅ Ejecuta tests
   - ✅ Genera nueva versión automáticamente (incrementa patch version)
   - ✅ Crea y publica tag de Git (ej: `v1.0.5`)
   - ✅ Construye y publica imagen Docker con múltiples tags:
     - `tu-usuario/finscope-backend:1.0.5` (versión específica)
     - `tu-usuario/finscope-backend:latest` (última versión)
     - `tu-usuario/finscope-backend:1.0` (major.minor)
     - `tu-usuario/finscope-backend:1` (major)

### Versionado Automático

El workflow genera versiones siguiendo [Semantic Versioning](https://semver.org/):
- **Formato**: `MAJOR.MINOR.PATCH` (ej: `1.0.5`)
- **Incremento**: Se incrementa automáticamente el PATCH al mergear un PR a main
- **Tags Git**: Se crean tags con formato `v1.0.5`

**Primera ejecución**: Si no hay tags previos, comenzará desde `v0.0.1`

## 🛡️ Protección de Rama Main

El workflow incluye validación para prevenir commits directos a `main`:
- ✅ Solo permite commits que provengan de PRs mergeados
- ❌ Bloquea commits directos y falla el workflow

**Recomendación adicional**: Configura Branch Protection Rules en GitHub:
1. Ve a **Settings** → **Branches**
2. Agrega regla para `main`:
   - ✅ Require pull request reviews before merging
   - ✅ Require status checks to pass before merging
   - ✅ Require branches to be up to date before merging

## 🐳 Configuración de Docker Compose

El `docker-compose.yml` está configurado para usar la imagen publicada:

```yaml
app:
  image: ${DOCKER_USERNAME:-tu-usuario-docker}/finscope-backend:latest
```

### Configurar en tu entorno local:

1. Edita el archivo `env` (o créalo desde `env.example`)
2. Agrega:
   ```bash
   DOCKER_USERNAME=tu-usuario-docker
   ```

3. Para usar una versión específica en lugar de `latest`:
   ```yaml
   image: ${DOCKER_USERNAME}/finscope-backend:1.0.5
   ```

## 📝 Ejemplo de Uso

### Desarrollo Normal:
```bash
# 1. Crear feature branch
git checkout -b feature/nueva-funcionalidad

# 2. Hacer cambios y commits
git commit -m "feat: nueva funcionalidad"

# 3. Push y crear PR
git push origin feature/nueva-funcionalidad
# Crear PR en GitHub hacia 'main'

# 4. Después del merge:
# - El workflow se ejecuta automáticamente
# - Genera nueva versión (ej: v1.0.6)
# - Publica imagen Docker
# - Actualiza tag 'latest'
```

### Actualizar Docker Compose en Producción:
```bash
# 1. Pull la nueva imagen
docker pull tu-usuario/finscope-backend:latest

# 2. Reiniciar servicios
docker-compose up -d --pull always
```

## 🔍 Verificar el Workflow

1. Ve a la pestaña **Actions** en tu repositorio
2. Verás el estado de cada ejecución
3. Puedes ver logs detallados de cada job
4. Los artefactos (JAR, test results) están disponibles para descargar

## ⚠️ Troubleshooting

### Error: "DOCKER_USERNAME not found"
- Verifica que hayas configurado el secret en GitHub
- El nombre del secret debe ser exactamente `DOCKER_USERNAME`

### Error: "Direct commit to main detected"
- No puedes hacer commits directos a main
- Crea un PR desde tu branch hacia main

### Error: "Failed to push tag"
- Verifica que el token `GITHUB_TOKEN` tenga permisos de escritura
- Por defecto, GitHub Actions proporciona este token automáticamente

### La imagen no se actualiza en Docker Hub
- Verifica que los secrets `DOCKER_USERNAME` y `DOCKER_PASSWORD` estén correctos
- Revisa los logs del job `docker-build-and-push` en Actions

## 📚 Recursos Adicionales

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Docker Hub Documentation](https://docs.docker.com/docker-hub/)
- [Semantic Versioning](https://semver.org/)

