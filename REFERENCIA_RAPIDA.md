# ⚡ Referencia Rápida - Comandos Maven

## 🔨 Comandos Más Comunes

### Compilar
```bash
mvn clean compile
```
✅ Compila sin errores (No requiere MySQL)

### Ejecutar la aplicación
```bash
mvn spring-boot:run
```
⚠️ Requiere MySQL ejecutándose

### Empaquetar (crear JAR)
```bash
mvn clean package
```
Crea: `target/CRUDThymeilif-0.0.1-SNAPSHOT.jar`

### Ejecutar JAR
```bash
java -jar target/CRUDThymeilif-0.0.1-SNAPSHOT.jar
```

### Solo compilar sin pruebas
```bash
mvn clean compile -DskipTests
```

### Limpiar archivos generados
```bash
mvn clean
```

---

## 📂 Estructura de Directorios Clave

```
src/
├── main/
│   ├── java/com/example/crudthymeilif/
│   │   ├── Controller/          ← Controladores REST/Web
│   │   ├── Model/               ← Entidades (@Entity)
│   │   └── repository/          ← Acceso a datos
│   │
│   └── resources/
│       ├── application.properties   ← Config de la app
│       ├── static/                  ← Recursos estáticos
│       │   ├── css/styles.css
│       │   └── js/main.js
│       └── templates/               ← Plantillas Thymeleaf
│           ├── competiciones/
│           ├── concursants/
│           ├── resultats/
│           └── pagaments/
│
└── test/                        ← Pruebas unitarias
    └── java/...
```

---

## 🌐 Rutas Disponibles

```
GET  /                          → home (redirige a /competiciones)
GET  /competiciones             → listado de competiciones
GET  /concursants               → listado de concursantes
GET  /resultats                 → resultados
GET  /pagaments                 → interfaz de pagos
```

---

## 🎨 Archivos de Estilo

Todos los estilos están en un único archivo:

**`src/main/resources/static/css/styles.css`** (1000+ líneas)

Variables CSS principales:
```css
--primary-dark: #1a1f4d      (Sidebar)
--primary-darker: #0f1333    (Fondo)
--accent-blue: #6366f1       (Botones)
--accent-orange: #f97316     (Acentos)
--accent-green: #22c55e      (Éxito)
--accent-cyan: #06b6d4       (Info)
```

---

## 📊 Páginas Implementadas

| Página | Ruta | Estado |
|--------|------|--------|
| Home | `/` | ✅ Redirige a competiciones |
| Competiciones - Lista | `/competiciones` | ✅ Con tarjetas |
| Competiciones - Detalle | `/competiciones/{id}` | ✅ Con datos |
| Competiciones - Crear | `/competiciones/nuevo` | ✅ Formulario |
| Concursantes - Lista | `/concursants` | ✅ Con tarjetas |
| Concursantes - Detalle | `/concursants/{id}` | ✅ Con datos |
| Concursantes - Crear | `/concursants/nuevo` | ✅ Formulario |
| Resultados - Lista | `/resultats` | ✅ Con tabla |
| Resultados - Detalle | `/resultats/{id}` | ✅ Con tabla |
| Pagos - Botiga | `/pagaments` | ✅ Con Stripe |
| Pagos - Éxito | `/pagaments/exit` | ✅ Confirmación |
| Pagos - Cancelado | `/pagaments/cancelat` | ✅ Cancelación |
| Pagos - Historial | `/pagaments/historial` | ✅ Tabla |

**Total: 13 páginas HTML completamente implementadas** ✅

---

## 🔑 Configuración Importante

### Archivo: `src/main/resources/application.properties`

```properties
# Base de Datos
spring.datasource.url=jdbc:mysql://localhost:3306/biblioteca
spring.datasource.username=root
spring.datasource.password=

# Stripe
stripe.api.key=sk_test_51Sw1hkB5...
stripe.public.key=pk_test_51Sw1hkB5...
app.base.url=http://localhost:8080
```

**Cambiar estos valores según tu configuración local**

---

## 🎯 Próximos Pasos de Desarrollo

### 1. Crear Modelos (Entidades)
```java
@Entity
@Table(name = "competicions")
public class Competicio {
    @Id
    @GeneratedValue
    private Long id;
    private String titol;
    private Double preu;
    // ... más propiedades
}
```

### 2. Crear Repositories
```java
public interface CompeticionRepository 
    extends JpaRepository<Competicio, Long> {
}
```

### 3. Inyectar en Controladores
```java
@Autowired
private CompeticionRepository repo;

@GetMapping
public String lista(Model model) {
    model.addAttribute("competicions", repo.findAll());
    return "competiciones/lista";
}
```

### 4. Actualizar Vistas
```html
<div th:each="comp : ${competicions}">
    <h3 th:text="${comp.titol}">Título</h3>
    <p th:text="${comp.preu}">Precio</p>
</div>
```

---

## 🐛 Errores Comunes y Soluciones

| Error | Causa | Solución |
|-------|-------|----------|
| "No plugin found for prefix 'springboot'" | Comando incorrecto | Usar `spring-boot:run` en lugar de `springboot:run` |
| "Communications link failure" | MySQL no está ejecutándose | Iniciar MySQL: `net start MySQL80` |
| "Unable to create requested service" | Base de datos no existe | Crear BD: `CREATE DATABASE biblioteca;` |
| "Cannot resolve symbol" | Dependencia faltante | Ejecutar: `mvn clean install` |

---

## 📞 Soporte Rápido

### Verificar instalación de Java
```bash
java -version
javac -version
```

### Verificar instalación de Maven
```bash
mvn -version
```

### Verificar conexión MySQL
```bash
mysql -u root -p
```

### Limpiar caché Maven
```bash
mvn clean
rmdir /s /q %USERPROFILE%\.m2\repository
```

---

## 📚 Archivos Importantes a Leer

1. **README_CAMBIOS.md** - Resumen de todos los cambios realizados
2. **README_ESTRUCTURA.md** - Estructura completa del proyecto
3. **GUIA_VISUAL.md** - Guía visual del diseño
4. **INSTRUCCIONES_EJECUCION.md** - Cómo ejecutar el proyecto

---

**¡Proyecto listo para desarrollo!** 🚀
