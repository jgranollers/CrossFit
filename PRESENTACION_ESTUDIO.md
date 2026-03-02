# PateixFit — Texto para la Presentación

> Guía de estudio en español. Léelo en voz alta varias veces antes de la presentación.

---

## INTRODUCCIÓN — ¿Qué es PateixFit?

*"El proyecto que he desarrollado se llama **PateixFit**, y es una aplicación web completa para la gestión de competiciones de CrossFit.*

*Permite a los usuarios registrarse, inscribirse en competiciones, pagar la inscripción de forma segura, y ver los resultados. Los administradores pueden gestionar todo el contenido: competiciones, participantes, entrenamientos y resultados."*

---

## BLOQUE 1 — Tecnologías utilizadas

*"Para desarrollar la aplicación he utilizado las siguientes tecnologías:"*

**Backend:**
- **Java 17** como lenguaje de programación principal.
- **Spring Boot 3.2.5**, que es el framework que me permite crear el servidor web, gestionar las rutas, la seguridad y la conexión a la base de datos de forma integrada.

**Capa de datos:**
- **Spring Data JPA con Hibernate** para mapear las clases Java a tablas de base de datos sin escribir SQL manualmente.
- **MySQL 8** como sistema gestor de base de datos relacional.

**Frontend:**
- **Thymeleaf** como motor de plantillas HTML, que se integra directamente con Spring Boot y permite generar páginas HTML dinámicas en el servidor.
- **Bootstrap 5** para el diseño responsive y los componentes visuales.
- **CSS personalizado** con un tema oscuro propio, usando variables CSS para todos los colores.

**Servicios externos:**
- **Stripe** para procesar pagos de inscripción de forma segura mediante Stripe Checkout.
- **Spring Mail** para enviar correos de verificación al registrarse.

**Seguridad:**
- **Spring Security 6** para controlar quién puede acceder a qué, con **BCrypt** para cifrar las contraseñas.

---

## BLOQUE 2 — Arquitectura del proyecto

*"El proyecto sigue la arquitectura estándar de Spring Boot, dividida en capas:"*

- **Model**: contiene las clases Java que representan las tablas de la base de datos. Por ejemplo `Competicion`, `Usuari`, `Concursant`, `Wod`...
- **Repository**: interfaces que extienden de Spring Data JPA y nos permiten hacer consultas a la base de datos sin escribir SQL.
- **Service**: contiene la lógica de negocio. Por ejemplo `StripeService` gestiona los pagos, `EmailService` los correos, `AuthService` el registro y la verificación.
- **Controller**: recibe las peticiones HTTP del navegador, llama a los servicios o repositorios, y devuelve la plantilla HTML con los datos.
- **Config**: configuración de seguridad, rutas permitidas y helpers globales de Thymeleaf.

---

## BLOQUE 3 — Módulos de la aplicación

### Autenticación
*"El sistema de autenticación funciona así:"*

*"Cuando un usuario se registra, introduce su DNI, nombre, correo y contraseña. La contraseña se cifra automáticamente con BCrypt y se guarda así en la base de datos, nunca en texto plano."*

*"Después del registro, el sistema envía un correo con un código de verificación de 6 dígitos que caduca a los 15 minutos. El usuario debe introducirlo para activar su cuenta."*

*"Una vez verificado, puede hacer login. Spring Security gestiona la sesión automáticamente. Hay dos roles: USUARI y ADMIN."*

---

### Competiciones
*"El módulo central de la aplicación son las competiciones."*

*"Una competición tiene: nombre, tipo, fecha, localitat, descripción, precio de inscripción, número máximo de participantes y estado (puede ser OBERTA, TANCADA, FINALITZADA o CANCELˑLADA)."*

*"Cualquier usuario puede ver la lista y el detalle de las competiciones. Solo los administradores pueden crearlas, editarlas o eliminarlas."*

*"En la página de detalle se puede ver: la información general, el estado con una barra de plazas ocupadas, los concursantes inscritos y los WODs de ese evento."*

---

### Concursants (Participantes)
*"Para inscribirse en una competición, el usuario necesita primero crear un perfil de concursante."*

*"Un concursante tiene: nombre, apellido, edad, sexo, categoría (RX, SCALED, MASTERS o TEENS) y está vinculado al usuario que lo creó."*

*"Esto separa la cuenta de usuario (datos de login) del perfil deportivo (datos de competición)."*

---

### Inscripciones y pagos con Stripe
*"El flujo de inscripción es el siguiente:"*

1. *"El usuario entra al detalle de una competición abierta."*
2. *"Si no tiene perfil de concursante, se le redirige a crearlo."*
3. *"Si la competición es gratuita, se inscribe directamente."*
4. *"Si tiene precio, se genera una sesión de Stripe Checkout y el usuario es redirigido a la pasarela de pago segura de Stripe."*
5. *"Si el pago tiene éxito, Stripe redirige al usuario a `/inscripcio/exit` y se guarda la inscripción como COMPLETADA."*
6. *"Si cancela, vuelve a `/inscripcio/cancelat`."*
7. *"Si la inscripción quedó pendiente, puede reintentar el pago."*

*"Toda la información del pago (ID de sesión de Stripe, precio pagado, estado) se guarda en la entidad `Compra`."*

---

### WODs — Workout of the Day
*"Los WODs son los entrenamientos de cada competición. He implementado un sistema jerárquico de tres niveles:"*

```
Competición
  └── WOD (puede haber varios por competición)
        └── Nivel de dificultad: DIFÍCIL / INTERMIG / FÀCIL
              └── Ejercicios (nombre, repeticiones, orden)
```

*"Cada WOD tiene una modalidad: INDIVIDUAL o GRUP. Si es grupal, se especifica el subtipo: HH (hombre-hombre), DD (dona-dona) o HD (hombre-dona)."*

*"Los administradores pueden crear WODs, añadirles niveles de dificultad y agregar ejercicios a cada nivel directamente desde la interfaz, sin salir de la página."*

---

### Resultados
*"El módulo de resultados permite registrar el resultado final de cada concursante en cada competición."*

*"Se puede guardar: posición final, tiempo (formato MM:SS o HH:MM:SS), puntuación numérica, repeticiones completadas y comentarios."*

*"Solo los administradores pueden crear y editar resultados."*

---

## BLOQUE 4 — Seguridad y control de acceso

*"El control de acceso está centralizado en `SecurityConfig`. Las reglas son:"*

- *"Las páginas públicas son solo el login, el registro y los ficheros CSS/JS."*
- *"Todo lo demás requiere estar autenticado (haber hecho login)."*
- *"Crear, editar o eliminar competiciones, resultados y WODs solo lo puede hacer el rol ADMIN."*
- *"La gestión de usuarios también es exclusiva del ADMIN."*
- *"Cada usuario solo puede editar su propio perfil."*

*"Las contraseñas se almacenan cifradas con BCrypt. Spring Security gestiona automáticamente la protección CSRF en todos los formularios."*

---

## BLOQUE 5 — Base de datos

*"La base de datos se llama `crossfit_db` y tiene las siguientes tablas principales:"*

| Tabla | Para qué sirve |
|---|---|
| `usuari` | Cuentas de usuario (DNI como clave primaria) |
| `concursant` | Perfiles deportivos vinculados a usuarios |
| `competicion` | Competiciones con sus datos y estado |
| `compra` | Inscripciones con estado y referencia de Stripe |
| `wod` | Entrenamientos de cada competición |
| `dificultat_wod` | Niveles de dificultad de cada WOD |
| `exercici` | Ejercicios dentro de cada nivel |
| `resultat` | Resultados finales por concursante y competición |

*"Hibernate con `ddl-auto=update` actualiza el esquema automáticamente al arrancar la aplicación."*

---

## BLOQUE 6 — Frontend y diseño

*"El diseño es un tema oscuro personalizado, sin usar ningún framework de JavaScript moderno como React o Vue. Todo el HTML se genera en el servidor con Thymeleaf."*

*"He definido todas las variables de color en CSS (`:root`) para tener consistencia en toda la aplicación: fondo oscuro, texto claro y acentos en verde."*

*"La interfaz es responsive gracias a Bootstrap y al grid CSS personalizado con `auto-fill` y `minmax`, que se adapta automáticamente al ancho de pantalla."*

*"El JavaScript propio gestiona: la barra de búsqueda en tiempo real, el toggle del sidebar en móvil, un efecto ripple en los botones, y un selector visual de banderas de países."*

---

## BLOQUE 7 — Cómo ejecutar el proyecto

*"Para ejecutar el proyecto necesitas:"*

1. **MySQL activo** con la base de datos `crossfit_db` creada.
2. Ejecutar desde la raíz del proyecto:
   ```
   mvnw spring-boot:run
   ```
3. Acceder en el navegador a `http://localhost:8081`.

*"El puerto es el 8081, no el 8080 por defecto, porque lo he configurado así en `application.properties`."*

---

## POSIBLES PREGUNTAS DEL TRIBUNAL

**¿Por qué Spring Boot?**
*"Spring Boot simplifica enormemente la configuración. Con pocas líneas ya tienes un servidor, seguridad, acceso a base de datos y motor de plantillas funcionando juntos."*

**¿Por qué Thymeleaf y no React?**
*"Para este proyecto el renderizado en servidor es suficiente y más simple. No hay necesidad de una API REST separada ni de gestionar estado en el cliente."*

**¿Cómo funciona la seguridad de los pagos?**
*"El dinero nunca pasa por mi servidor. El usuario es redirigido a la plataforma de Stripe, que gestiona el pago. Solo recibo una confirmación con el ID de sesión para marcar la inscripción como completada."*

**¿Qué pasa si el usuario cierra el navegador durante el pago?**
*"La inscripción queda en estado PENDENT. La próxima vez que entre al detalle de la competición, verá un aviso y un botón para completar el pago pendiente."*

**¿Cómo se protegen las contraseñas?**
*"Con BCrypt, que es un algoritmo de hash diseñado para ser lento y resistente a ataques de fuerza bruta. La contraseña nunca se guarda en texto plano, ni en la base de datos ni en los logs."*

**¿Qué es un WOD?**
*"Workout of the Day. En CrossFit, cada día o cada prueba de competición tiene un entrenamiento definido con ejercicios, repeticiones y tiempo o puntuación objetivo. Mi sistema permite definirlos de forma estructurada con diferentes niveles de dificultad."*

---

*Guia preparada el 02/03/2026*
