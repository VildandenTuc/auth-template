# 🔄 **Guía Completa: Reutilizar tu Auth Template en Nuevos Proyectos**

### 📋 **Pasos detallados para reutilizar el template:**

---

## **1️⃣ Clonar y Preparar el Template**

```bash
# Clonar tu template
git clone https://github.com/TU-USUARIO/auth-template.git mi-nuevo-proyecto
cd mi-nuevo-proyecto

# Eliminar el remote del template original
git remote remove origin

# Inicializar como nuevo proyecto
git remote add origin https://github.com/TU-USUARIO/mi-nuevo-proyecto.git
```

---

## **2️⃣ Personalizar Package y Nombres**

### **A. Cambiar nombre del proyecto**
**En `pom.xml`:**
```xml
<groupId>com.tuempresa</groupId>
<artifactId>mi-nuevo-proyecto</artifactId>
<name>Mi Nuevo Proyecto</name>
<description>Descripción de mi nuevo proyecto</description>
```

### **B. Refactorizar packages**
**Cambiar de:** `com.vildanden.auth_template`
**A:** `com.tuempresa.minuevoproyecto`

**En tu IDE:**
1. **IntelliJ**: `Refactor → Rename Package`
2. **VS Code**: `F2` sobre el package principal
3. **Manual**: Cambiar en todos los archivos `.java`

### **C. Actualizar `application.yml`**
```yaml
spring:
  application:
    name: mi-nuevo-proyecto
  datasource:
    url: jdbc:mysql://localhost:3306/mi_nuevo_proyecto_db

app:
  name: Mi Nuevo Proyecto API
  description: Descripción de mi nuevo sistema
```

---

## **3️⃣ Configurar Base de Datos**

```sql
-- Crear nueva base de datos
CREATE DATABASE mi_nuevo_proyecto_db;
```

**Actualizar credenciales en `application.yml`:**
```yaml
spring:
  datasource:
    username: tu_usuario
    password: tu_password
```

---

## **4️⃣ Personalizar JWT y Seguridad**

```yaml
jwt:
  secret: "nueva-clave-secreta-super-segura-para-mi-proyecto-256-bits-minimo"
  expiration: 86400000    # Ajustar según necesidades
  refresh-expiration: 604800000
```

---

## **5️⃣ Customizar para tu Dominio**

### **A. Agregar campos específicos a User**
```java
@Entity
@Table(name = "users")
public class User extends BaseEntity {
    // Campos existentes del template...

    // Nuevos campos para tu proyecto
    private String telefono;
    private String direccion;
    private LocalDate fechaNacimiento;
    private String empresa;
    // getters/setters...
}
```

### **B. Crear nuevos DTOs**
```java
// Ejemplo: ProductoDTO, ClienteDTO, etc.
public record ProductoResponseDTO(
    Long id,
    String nombre,
    BigDecimal precio,
    String categoria
) {}
```

### **C. Agregar nuevos roles específicos**
```sql
INSERT INTO roles (name, description) VALUES
('CLIENTE', 'Cliente del sistema'),
('VENDEDOR', 'Vendedor con permisos de venta'),
('SUPERVISOR', 'Supervisor de ventas');
```

---

## **6️⃣ Extender Funcionalidades**

### **A. Nuevos Controllers**
```java
@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'VENDEDOR')")
    public ResponseEntity<List<ProductoDTO>> listarProductos() {
        // Tu lógica específica
    }
}
```

### **B. Nuevas Entities y Services**
- Crear entidades específicas de tu dominio
- Implementar repositorios JPA
- Desarrollar servicios de negocio

---

## **7️⃣ Actualizar Documentación**

### **README.md personalizado:**
```markdown
# Mi Nuevo Proyecto

Sistema de [descripción] construido sobre template de autenticación JWT.

## Funcionalidades Específicas
- [Funcionalidad 1]
- [Funcionalidad 2]
- Autenticación JWT (heredada del template)

## Endpoints Específicos
| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/productos` | GET | Listar productos |
| `/ventas` | POST | Crear venta |
```

---

## **8️⃣ Testing y Validación**

```bash
# Compilar y probar
mvn clean compile
mvn test

# Ejecutar aplicación
mvn spring-boot:run

# Probar endpoints básicos
curl http://localhost:8080/api/health
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail": "admin", "password": "admin123"}'
```

---

## **9️⃣ Configuraciones de Producción**

### **A. Variables de entorno**
```bash
# .env o configuración del servidor
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=clave-super-secreta-produccion
DB_HOST=servidor-bd-produccion
DB_USERNAME=usuario-prod
DB_PASSWORD=password-seguro
```

### **B. application-prod.yml**
```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:mysql://localhost:3306/mi_proyecto_prod}
    username: ${DB_USERNAME:admin}
    password: ${DB_PASSWORD:secret}

jwt:
  secret: ${JWT_SECRET:default-secret}
```

---

## **🔟 Commit y Deploy**

```bash
# Primer commit del nuevo proyecto
git add .
git commit -m "🎉 Initial setup: Mi Nuevo Proyecto

✨ Basado en auth-template con customizaciones:
- Package refactorizado a com.tuempresa.minuevoproyecto
- Base de datos configurada para mi_nuevo_proyecto_db
- Nuevos endpoints específicos del dominio
- [Otras customizaciones]

🔒 Autenticación JWT heredada del template"

# Push al nuevo repositorio
git push -u origin master
```

---

## **📚 Plantilla de Checklist**

Copia esto para cada nuevo proyecto:

```markdown
## ✅ Checklist de Migración del Auth Template

### Configuración Básica
- [ ] Clonar template y configurar nuevo remote
- [ ] Cambiar nombre en pom.xml
- [ ] Refactorizar packages
- [ ] Actualizar application.yml
- [ ] Crear nueva base de datos
- [ ] Cambiar JWT secret

### Personalización
- [ ] Agregar campos específicos a User entity
- [ ] Crear DTOs del dominio
- [ ] Agregar roles específicos
- [ ] Implementar controllers específicos
- [ ] Crear entities del negocio

### Documentación y Testing
- [ ] Actualizar README.md
- [ ] Probar endpoints básicos
- [ ] Crear tests específicos
- [ ] Configurar variables de producción

### Deploy
- [ ] Commit inicial
- [ ] Push a repositorio
- [ ] Configurar CI/CD si aplica
```

---

## **🎯 Tiempo estimado de migración:**

- **Proyecto simple**: 30-60 minutos
- **Proyecto medio**: 1-3 horas
- **Proyecto complejo**: Medio día

**¡Con este flujo tendrás autenticación JWT robusta en cualquier proyecto nuevo en minutos!** 🚀

---

## **💡 Consejos Adicionales**

### **🔧 Automatización Opcional**
Puedes crear un script bash para automatizar parte del proceso:

```bash
#!/bin/bash
# script-setup-template.sh

PROJECT_NAME=$1
PACKAGE_NAME=$2

if [ -z "$PROJECT_NAME" ] || [ -z "$PACKAGE_NAME" ]; then
    echo "Uso: ./script-setup-template.sh mi-nuevo-proyecto com.empresa.proyecto"
    exit 1
fi

echo "🚀 Configurando nuevo proyecto: $PROJECT_NAME"

# Clonar y configurar
git clone https://github.com/TU-USUARIO/auth-template.git $PROJECT_NAME
cd $PROJECT_NAME
git remote remove origin

# Cambiar nombres en pom.xml (requiere sed o manual)
echo "✏️  Actualiza manualmente pom.xml y packages"
echo "📝 Actualiza application.yml"
echo "🗄️  Crea base de datos: ${PROJECT_NAME}_db"

echo "✅ Setup inicial completo!"
```

### **🔄 Mantenimiento del Template**
- Actualiza el template original cuando agregues mejoras comunes
- Mantén el template actualizado con las últimas versiones de Spring Boot
- Documenta cambios importantes en el CHANGELOG.md

### **📦 Versionado**
- Usa tags en el template para versiones estables: `v1.0.0`, `v1.1.0`
- Permite elegir versión específica al clonar: `git clone -b v1.0.0 ...`

---

**📅 Creado:** $(date)
**👨‍💻 Autor:** Guido Alfredo Albarracín
**🔄 Versión:** 1.0.0