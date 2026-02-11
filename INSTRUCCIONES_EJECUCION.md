# 🚀 Cómo Ejecutar el Proyecto CRUDThymeilif

## ✅ Estado Actual del Proyecto

El proyecto ha sido **compilado exitosamente** ✅

```
mvn clean compile → ✅ ÉXITO
```

## 📋 Requisitos Previos

Antes de ejecutar el proyecto, necesitas tener instalado:

1. **Java 21+** (Verificar con: `java -version`)
2. **Maven 3.8+** (Verificar con: `mvn -version`)
3. **MySQL 8.0+** (Necesario para la base de datos)

## 🔧 Opciones de Ejecución

### Opción 1: Compilar solamente (SIN ejecutar)

```bash
cd C:\Users\fredb\IdeaProjects\CRUDThymeilif
mvn clean compile
```

**Resultado**: Compila el código sin intentar conectar a la BD
✅ Esto funciona ahora mismo sin MySQL

### Opción 2: Ejecutar la aplicación (Requiere MySQL)

```bash
cd C:\Users\fredb\IdeaProjects\CRUDThymeilif
mvn spring-boot:run
```

**Requisitos**:
- MySQL debe estar ejecutándose
- Base de datos `biblioteca` debe existir
- Usuario `root` con la contraseña configurada en `application.properties`

## 🗄️ Configuración de Base de Datos

Si deseas ejecutar el proyecto con la aplicación completa:

### 1. Iniciar MySQL

En PowerShell como Administrador:

```powershell
# Si usas MySQL Community Server
net start MySQL80

# O si tienes MySQL en un contenedor Docker
docker run --name mysql -e MYSQL_ROOT_PASSWORD=root -p 3306:3306 -d mysql:8.0
```

### 2. Crear la base de datos

```sql
CREATE DATABASE biblioteca;
```

### 3. Ejecutar la aplicación

```bash
mvn spring-boot:run
```

La aplicación estará disponible en: **http://localhost:8080**

## 📝 Alternativa: Ejecutar con Maven sin Spring Boot Plugin

Si tienes problemas con el plugin de Spring Boot:

```bash
# Opción A: Compilar y ejecutar manualmente
mvn clean package -DskipTests
java -jar target/CRUDThymeilif-0.0.1-SNAPSHOT.jar

# Opción B: Usar Java para ejecutar la clase principal
mvn exec:java -Dexec.mainClass="com.example.crudthymeilif.CrudThymeilifApplication"
```

## 🌐 URLs del Proyecto

Una vez que el proyecto esté ejecutándose:

```
Homepage:           http://localhost:8080/
Competiciones:      http://localhost:8080/competiciones
Concursantes:       http://localhost:8080/concursants
Resultados:         http://localhost:8080/resultats
Pagos (Stripe):     http://localhost:8080/pagaments
```

## 🐛 Solución de Problemas

### Problema: "No plugin found for prefix 'springboot'"

**Solución**: Usa el comando correcto:
```bash
mvn spring-boot:run
# ✅ Correcto: spring-boot:run
# ❌ Incorrecto: springboot:run
```

### Problema: "Communications link failure" (MySQL)

**Solución**: 
1. Verifica que MySQL esté ejecutándose
2. Verifica la configuración en `src/main/resources/application.properties`
3. Comprueba la contraseña del usuario root

### Problema: "Unable to determine Dialect"

**Solución**: 
Es normal durante las pruebas sin BD. Si quieres usar la BD:
1. Inicia MySQL
2. Crea la base de datos `biblioteca`
3. Ejecuta nuevamente

## 📊 Estructura de Archivos Importante

```
CRUDThymeilif/
├── src/main/
│   ├── java/com/example/crudthymeilif/
│   │   ├── Controller/          ← Controladores (ya creados)
│   │   ├── Model/               ← Entidades (pendientes)
│   │   └── repository/          ← Repositories (pendientes)
│   └── resources/
│       ├── application.properties    ← Configuración DB
│       ├── static/css/styles.css     ← Estilos personalizados
│       ├── static/js/main.js         ← JavaScript
│       └── templates/                ← Plantillas HTML
├── pom.xml                      ← Dependencias Maven
└── run-compile.bat              ← Script para compilar
```

## 🎯 Próximos Pasos

### Para hacer funcional la aplicación:

1. **Crear Entidades JPA**
   ```java
   // Archivos a crear:
   src/main/java/com/example/crudthymeilif/Model/
   - Competicio.java
   - Concursant.java
   - Resultat.java
   - Pagament.java
   ```

2. **Implementar Repositories**
   ```java
   // Archivos a crear:
   src/main/java/com/example/crudthymeilif/repository/
   - CompeticionRepository.java
   - ConcursantRepository.java
   - ResultatRepository.java
   - PagamentRepository.java
   ```

3. **Crear Servicios**
   ```java
   // Crear carpeta: src/main/java/com/example/crudthymeilif/Service/
   - CompeticionService.java
   - ConcursantService.java
   - PagamentService.java (con integración Stripe)
   ```

4. **Actualizar Controladores**
   - Agregar anotaciones `@Autowired`
   - Implementar métodos POST/PUT/DELETE
   - Pasar datos dinámicos a las vistas

## 🔐 Configuración Stripe

Las claves de Stripe ya están configuradas en `application.properties`:

```properties
stripe.api.key=sk_test_...
stripe.public.key=pk_test_...
```

Estas son claves de **prueba (test)**. Para producción, cambiarlas por las claves reales.

## 📚 Recursos Útiles

- **Spring Boot Docs**: https://spring.io/projects/spring-boot
- **Thymeleaf Docs**: https://www.thymeleaf.org/
- **Bootstrap 5 Docs**: https://getbootstrap.com/docs/5.0/
- **Stripe Docs**: https://stripe.com/docs

## ✅ Checklist de Verificación

- [x] Proyecto compilado sin errores
- [x] Plantillas HTML creadas
- [x] Estilos CSS implementados
- [x] Controladores configurados
- [x] Rutas mapeadas
- [ ] Base de datos MySQL creada
- [ ] Entidades JPA implementadas
- [ ] Repositories configurados
- [ ] Lógica de negocio completada
- [ ] Integración Stripe finalizada

---

**¡El proyecto está listo para desarrollar!** 🚀

Cuando tengas lista tu base de datos, notificaré para ayudarte a implementar la lógica backend.
