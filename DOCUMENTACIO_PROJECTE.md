# PateixFit — Documentació del Projecte

> **Aplicació web de gestió de competicions CrossFit.**  
> Nom intern del projecte: `CRUDThymeilif`

---

## 1. Stack Tecnològic

| Capa | Tecnologia |
|---|---|
| Backend | Java 17 · Spring Boot 3.2.5 |
| Plantilles | Thymeleaf |
| Persistència | Spring Data JPA · Hibernate · MySQL 8 |
| Seguretat | Spring Security 6 · BCrypt |
| Pagaments | Stripe Checkout (stripe-java 24.18.0) |
| Correu | Spring Mail (SMTP Gmail) |
| Frontend | Bootstrap 5.3 · Font Awesome 6.4 · CSS personalitzat |
| Build | Maven (mvnw) |
| Port | **8081** |
| Base de dades | `crossfit_db` @ localhost:3306 |

---

## 2. Arquitectura

```
src/main/java/com/example/crudthymeilif/
├── Config/          → Seguretat, MVC, helpers globals
├── Controller/      → Capa web (HTTP request → response)
├── Model/           → Entitats JPA (tablas de base de datos)
├── repository/      → Accés a dades (Spring Data JPA)
└── Service/         → Lògica de negoci (Auth, Email, Stripe, Concursant)
```

---

## 3. Mòduls i Funcionalitats

### 3.1 Autenticació (`AuthController`, `AuthService`)

- **Registre**: formulari amb DNI, nom, correu, contrasenya. La contrasenya es guarda amb **BCrypt**.
- **Verificació per correu**: en registrar-se s'envia un codi de 6 dígits per email (expira als 15 minuts). Si no hi ha servidor SMTP, el codi es mostra per consola (mode desenvolupament).
- **Login**: formulari estàndard Spring Security. Redirigeix a `/competiciones` en èxit.
- **Logout**: invalida la sessió i redirigeix a `/login?logout=true`.
- **Rols**: `USUARI` (per defecte) i `ADMIN`.

---

### 3.2 Gestió d'Usuaris (`UsuariController`)

**Model `Usuari`** — Taula: `usuari`

| Camp | Tipus | Notes |
|---|---|---|
| `dni` | String (PK) | Longitud 9 |
| `nom` | String | |
| `correu` | String (unique) | Login identifier |
| `password` | String | BCrypt hash |
| `rol` | String | `USUARI` / `ADMIN` |
| `nacionalitat` | String (2) | Codi ISO país (mostra bandera) |
| `fotoPerfilPath` | String | Ruta fitxer pujat |
| `verified` | Boolean | Activat per codi email |
| `enabled` | Boolean | Compte actiu/bloquejat |

**Funcionalitats:**
- Llistat d'usuaris *(només ADMIN)*
- Editar perfil propi (nom, telèfon, contrasenya, foto, nacionalitat)
- Pujar foto de perfil (max 5 MB, guardat a `data/uploads/`)
- L'ADMIN pot veure i editar qualsevol usuari
- Bandera de nacionalitat visible al topbar i al perfil

---

### 3.3 Competicions (`CompeticionController`)

**Model `Competicion`** — Taula: `competicion`

| Camp | Tipus | Notes |
|---|---|---|
| `id` | Long (PK, Auto) | |
| `nom` | String | Obligatori |
| `tipusCompeticio` | String | Ex: CrossFit, Halterofília... |
| `dataCompeticio` | LocalDate | Ha de ser present o futura |
| `localitat` | String | |
| `descripcio` | String (1000) | |
| `preuInscripcio` | Double | 0 o null = Gratis |
| `maxParticipants` | Integer | |
| `estat` | String | `OBERTA` / `TANCADA` / `FINALITZADA` / `CANCELˑLADA` |

**Funcionalitats:**
- Llistat de totes les competicions (cards visuals)
- Detall de competició: info general, estat, concursants inscrits, WODs
- Crear / Editar / Eliminar *(només ADMIN)*
- Barra de places ocupades (%)
- Inscripció amb o sense pagament (veure mòdul Inscripcions)

---

### 3.4 Concursants (`ConcursantController`, `ConcursantService`)

**Model `Concursant`** — Taula: `concursant`

| Camp | Tipus | Notes |
|---|---|---|
| `id` | Long (PK, Auto) | |
| `nom` / `cognom` | String | |
| `edat` | Integer | |
| `sexe` | String | `M` / `F` / `ALTRES` |
| `categoria` | String | `RX` / `SCALED` / `MASTERS` / `TEENS` |
| `email` / `telefon` | String | |
| `usuari` | ManyToOne → Usuari | Vincle amb compte registrat |

**Funcionalitats:**
- Cada usuari pot crear el seu perfil de concursant (necessari per inscriure's)
- Llistat global de concursants *(ADMIN veu tots, usuari veu el seu)*
- Editar perfil de concursant
- Cada concursant es mostra amb dades a la pàgina de detall de competició

---

### 3.5 Inscripcions i Pagaments (`InscripcioController`, `PagamentController`, `StripeService`)

**Model `Compra`** — Taula: `compra`

| Camp | Tipus | Notes |
|---|---|---|
| `id` | Long (PK, Auto) | |
| `usuari` | ManyToOne → Usuari | |
| `competicio` | ManyToOne → Competicion | |
| `concursant` | ManyToOne → Concursant | |
| `dataCompra` | LocalDateTime | |
| `preuPagat` | Double | |
| `stripePaymentId` | String | ID de sessió Stripe |
| `estat` | String | `PENDENT` / `COMPLETAT` / `CANCELˑLAT` |

**Flux d'inscripció:**
1. Usuari accedeix al detall d'una competició `OBERTA`.
2. Si no té perfil de concursant → redirigit a crear-ne un.
3. Si la competició és de pagament → redirigit a **Stripe Checkout** (sessió segura).
4. Stripe retorna a `/inscripcio/exit` (èxit) o `/inscripcio/cancelat`.
5. En èxit, es crea la `Compra` amb estat `COMPLETAT`.
6. Si la inscripció queda `PENDENT`, l'usuari pot reintentar el pagament.
7. Si la competició és gratuïta → inscripció directa sense Stripe.

**Historial de pagaments**: l'usuari pot veure totes les seves inscripcions a `/pagaments/historial`.

---

### 3.6 WODs — Workout of the Day (`WodController`)

Sistema jeràrquic de 3 nivells per definir els entrenaments d'una competició.

```
Competicion
  └── Wod  (1..N)
        └── DificultatWod  (1..3)  → DIFÍCIL / INTERMIG / FÀCIL
              └── Exercici  (1..N)
```

#### Model `Wod` — Taula: `wod`
| Camp | Valors | Notes |
|---|---|---|
| `nom` | String | Obligatori |
| `modalitat` | `INDIVIDUAL` / `GRUP` | |
| `subtipusGrup` | `HH` / `DD` / `HD` | Només si modalitat = GRUP |
| `ordre` | Integer | Ordre dins la competició |

#### Model `DificultatWod` — Taula: `dificultat_wod`
| Camp | Valors |
|---|---|
| `dificultat` | `DIFICIL` / `INTERMIG` / `FACIL` |
| `tipusPuntuacio` | `FOR_TIME` / `AMRAP` / `FOR_REPS` |
| `tempsLimit` | Integer (minuts) |
| `rondes` | Integer |
| `descripcio` | String |

#### Model `Exercici` — Taula: `exercici`
| Camp | Tipus |
|---|---|
| `nom` | String |
| `repeticions` | Integer |
| `ordre` | Integer |
| `notes` | String |

**Funcionalitats:**
- Crear / Editar / Eliminar WODs per competició *(ADMIN)*
- Afegir fins a 3 nivells de dificultat per WOD
- Afegir exercicis per cada nivell de dificultat en temps real (formulari inline)
- Vista de detall del WOD amb tots els exercicis per dificultat
- WODs visibles en el detall de la competició (cards amb badges de dificultat)

---

### 3.7 Resultats (`ResultatController`)

**Model `Resultat`** — Taula: `resultat`

| Camp | Tipus | Notes |
|---|---|---|
| `competicio` | ManyToOne | |
| `concursant` | ManyToOne | |
| `posicio` | Integer | Lloc final |
| `temps` | String | Format `HH:MM:SS` o `MM:SS` |
| `puntuacio` | Integer | Per AMRAP / FOR_REPS |
| `repsCompletades` | Integer | |
| `comentaris` | String (500) | |

**Funcionalitats:**
- Registrar resultats per competició i concursant *(ADMIN)*
- Editar / Eliminar resultats *(ADMIN)*
- Llistat de resultats per competició

---

## 4. Seguretat i Control d'Accés

| Accés | Ruta | Rol requerit |
|---|---|---|
| Pàgines públiques | `/login`, `/register`, `/auth/**`, `/css/**`, `/js/**` | Cap |
| Totes les pàgines | `/competiciones`, `/concursants`, etc. | `USUARI` (login) |
| Crear/editar/eliminar competicions | `POST /competiciones/**` | `ADMIN` |
| Gestió WODs | `/wods/nou/**`, `/wods/*/editar`, `POST /wods/**` | `ADMIN` |
| Gestió Resultats | `/resultats/nou`, `POST /resultats/**` | `ADMIN` |
| Gestió Usuaris | `/usuaris/**` | `ADMIN` |

---

## 5. Configuració (`application.properties`)

```properties
server.port=8081
spring.datasource.url=jdbc:mysql://localhost:3306/crossfit_db
spring.datasource.username=root
spring.datasource.password=           # buit per defecte
spring.jpa.hibernate.ddl-auto=update  # actualitza esquema automàticament

app.upload.dir=data/uploads            # fotos de perfil
spring.servlet.multipart.max-file-size=5MB

stripe.api.key=sk_test_...            # clau secreta de Stripe (TEST)
stripe.public.key=pk_test_...         # clau pública de Stripe (TEST)
app.base.url=http://localhost:8081
```

---

## 6. Base de Dades — Taules principals

```
usuari          → Comptes d'usuari amb rol i perfil
concursant      → Perfils esportius vinculats a usuaris
competicion     → Competicions (nom, data, preu, estat, places)
compra          → Inscripcions amb estat i referència Stripe
wod             → Entrenaments d'una competició
dificultat_wod  → Nivells de cada WOD (Difícil/Intermig/Fàcil)
exercici        → Exercicis dins cada nivell de dificultat
resultat        → Resultats finals per concursant i competició
```

---

## 7. Executar el Projecte

**Requisit previ**: MySQL 8 en execució amb la base de dades `crossfit_db` creada.

```bash
# Crear la base de dades (primera vegada)
mysql -u root -e "CREATE DATABASE IF NOT EXISTS crossfit_db;"

# Executar l'aplicació
./mvnw spring-boot:run

# Accedir a l'aplicació
http://localhost:8081
```

Per crear les taules, executar el fitxer `database_setup.sql` a MySQL si `ddl-auto=update` no les genera automàticament.

---

## 8. Estructura de Fitxers Importants

```
D:\GitHub\CrossFit\
├── pom.xml                         → Dependències Maven
├── database_setup.sql              → Script SQL creació taules
├── application.properties          → Configuració connexions i serveis
├── src/main/java/.../
│   ├── CrudThymeilifApplication.java   → Punt d'entrada Spring Boot
│   ├── Config/SecurityConfig.java      → Regles de seguretat
│   ├── Config/GlobalModelAdvice.java   → Dades globals a totes les vistes
│   ├── Service/StripeService.java      → Integració Stripe Checkout
│   └── Service/EmailService.java       → Enviament codis verificació
└── src/main/resources/
    ├── templates/                  → Vistes Thymeleaf
    │   ├── auth/                   → login, register, verify
    │   ├── competiciones/          → lista, detalle, formulari
    │   ├── concursants/            → lista, detalle, formulari, perfil-editar
    │   ├── wods/                   → lista, detalle, formulari
    │   ├── pagaments/              → botiga, exit, cancelat, historial
    │   └── resultats/              → lista, detalle, formulari
    └── static/css/styles.css       → Tema fosc personalitzat
```

---

## 9. Frontend — HTML / CSS / JavaScript

### 9.1 Llibreries externes (CDN)

| Llibreria | Versió | Ús |
|---|---|---|
| Bootstrap | 5.3.0 | Grid, utilitats, modals, formularis |
| Font Awesome | 6.4.0 | Icones a tota l'aplicació |
| CSS propi | — | `static/css/styles.css` — tema fosc complet |
| JS propi | — | `static/js/main.js` — interactivitat |

Cap bundler (Webpack/Vite). Tot es carrega directament des de CDN + fitxers estàtics.

---

### 9.2 Variables CSS (tema fosc)

Definides a `:root` a `styles.css`. S'usen a tota l'aplicació per mantenir consistència:

```css
:root {
    --primary-dark:    #1c2120   /* fons sidebar */
    --primary-darker:  #141918   /* fons body */
    --card-dark:       #1f2a27   /* fons de cards */
    --border-dark:     #2e3c38   /* vores */

    --accent-green:    #22c55e   /* verd primari (botons, actiu) */
    --accent-blue:     #4ade80   /* verd clar (hover, focus, preus) */
    --accent-orange:   #f97316   /* accents taronja */
    --accent-cyan:     #34d399   /* gradient barra places */

    --text-light:      #f0f4f0   /* text principal */
    --text-muted:      #8fa89a   /* text secundari / labels */
}
```

> **Nota**: `--accent-blue` és en realitat un verd llum (#4ade80), no blau, per elecció de disseny.

---

### 9.3 Estructura de layout de cada pàgina

Totes les pàgines autenticades segueixen el mateix esquema:

```html
<div class="d-flex">
    <aside class="sidebar">          <!-- Navegació lateral fixa -->
        .sidebar-title               <!-- Logo + nom PateixFit -->
        .filter-input                <!-- Camp de filtre -->
        nav.sidebar-section          <!-- Enllaços de navegació -->
    </aside>

    <main class="main-content flex-grow-1">
        div.top-bar                  <!-- Buscador + avatar/perfil -->
        div.page-header              <!-- Títol de pàgina + botons d'acció -->
        div.container-fluid          <!-- Contingut principal -->
    </main>
</div>
```

#### Sidebar
- Fons `--primary-dark`, amplada automàtica (`col-auto`)
- Enllaç actiu marcat amb classe `.active` → vora esquerra verda + fons semitransparent
- `.btn-add-competition`: botó circular `+` que apareix al costat d'alguns elements de menú
- En mòbil (pantalla petita): ocult per defecte, es revela amb `.show` via JS

#### Top Bar
- `.search-box`: camp de cerca amb lupa, `border-radius: 50px`, filtra cards i files de taula en temps real
- `.avatar`: cercle 48px amb foto de perfil o icona `fa-user`, vora verda
- `.btn-profile`: botó pill "Perfil" amb vora subtil

---

### 9.4 Sistema de Cards

#### Card estàndard (`.card`)
```
background: var(--card-dark)
border: 1px solid var(--border-dark)
border-radius: 12px
transition: all 0.3s ease
hover → translateY(-5px) + vora blau/verd + box-shadow
```

Estructura interna típica:
```html
<div class="card">
    <div class="card-header">    <!-- gradient verd/taronja subtil -->
    <div class="card-content">   <!-- flex-column, gap 1rem -->
    <div class="card-footer">    <!-- fons rgba(0,0,0,0.2), vora top -->
</div>
```

Components interns reutilitzables:
- `.card-badge` + `.badge-green/red/yellow/orange/blue/cyan` — etiquetes d'estat (pills)
- `.info-item` — icona + label + valor (flex, gap 0.75rem)
- `.info-label` / `.info-value` — text muted/light per descripcions
- `.places-bar` + `.places-fill` — barra de progrés places ocupades (gradient)
- `.price` — preu gran en `--accent-blue`
- `.card-name-bottom` — nom fixat a la part inferior (cards de concursants)

#### Card "Afegir nou" (`.card-add-new`)
```
background: gradient verd semitransparent
border: 2px dashed var(--accent-green)
hover → escala 1.02, border blau, box-shadow verd
```
Estructura:
```html
<a href="..." class="card card-add-new">
    <div class="card-add-content">
        <i class="fas fa-plus card-add-icon">   <!-- 4rem, gira 90° en hover -->
        <h3 class="card-add-title">             <!-- text verd, canvia a blau en hover -->
    </div>
</a>
```
S'usa a: llista de competicions, llista de concursants, llista de WODs, i dins el detall de competicions (Gestionar WODs).

#### Grid de cards (`.cards-container`)
```css
display: grid;
grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
gap: 2rem;
```
S'adapta automàticament a l'amplada disponible.

---

### 9.5 Formularis

Tots els formularis usen les classes personalitzades (no les de Bootstrap per defecte):

```css
.form-group        /* margin-bottom 1.5rem */
.form-label        /* color text-light, font-weight 600 */
.form-control      /* fons rgba blanc 5%, border-dark, color text-light */
.form-control:focus → border accent-blue, glow verd 3px
.form-error        /* text vermell clar #fca5a5 */
textarea           /* resize: vertical, min-height 120px */
select             /* hereten .form-control */
```

---

### 9.6 Botons

| Classe | Aparença | Ús |
|---|---|---|
| `.btn-primary` | Fons `--accent-blue`, text blanc, uppercase | Acció principal |
| `.btn-secondary` | Transparent, vora `--border-dark` | Accions secundàries |
| `.btn-danger` | Fons vermell semitransparent, text vermell | Eliminar |
| Bootstrap `.btn-sm` | Versió reduïda | Botons inline (Afegir exercici, etc.) |
| `.btn-outline-success` | Vora verda, transparent | Botons d'afegir en formularis inline |

---

### 9.7 Badges / Etiquetes d'estat

Usades amb `.card-badge` + modificador de color:

| Classe | Color | Usos típics |
|---|---|---|
| `.badge-green` | Verd (#22c55e) | Estat OBERTA, COMPLETAT |
| `.badge-red` | Vermell (#ef4444) | Estat TANCADA, CANCELˑLAT |
| `.badge-yellow` | Groc (#eab308) | Estat PENDENT |
| `.badge-orange` | Taronja (#f97316) | Accents |
| `.badge-blue` | Verd clar (accent-blue) | Informatiu |

Els badges de dificultat de WODs usen les classes Bootstrap natives: `bg-danger`, `bg-warning`, `bg-success`.

---

### 9.8 JavaScript (`main.js`)

| Funcionalitat | Descripció |
|---|---|
| **Sidebar toggle** | En pantalles petites, el botó `.sidebar-toggle` afegeix/treu la classe `.show`. Clic fora tanca el sidebar. |
| **Filtre sidebar** | `.filter-input` filtra en temps real les `.card` i `tbody tr` per text |
| **Cercador top bar** | `.search-box input` filtra igualment totes les `.card` i files de taula |
| **Ripple effect** | Tots els `.btn` mostren una animació d'ona en fer clic |
| **Hover cards** | `mouseenter/mouseleave` aplica `translateY(-5px)` (complementa la transició CSS) |
| `formatCurrency(val)` | Utilitat — formata número com a moneda EUR (locale `ca-ES`) |
| `formatDate(str)` | Utilitat — formata data a `dd/mm/yyyy` (locale `ca-ES`) |

---

### 9.9 Thymeleaf — Patrons usats

| Patró | Exemple |
|---|---|
| Iteració | `th:each="wod : ${wods}"` |
| Condicions | `th:if="${isAdmin}"` / `th:unless="${...}"` |
| URLs | `th:href="@{'/wods/' + ${wod.id}}"` |
| Text | `th:text="${competicion.nom}"` |
| Classe condicional | `th:classappend="${estat == 'OBERTA' ? 'badge-green' : 'badge-red'}"` |
| Estil condicional | `th:style="'width: ' + ${porcentaje} + '%;'"` |
| Formatjar dates | `${#temporals.format(data, 'dd/MM/yyyy')}` |
| Formularis | `th:action`, `th:object`, `th:field`, `th:errors` |
| CSRF | Inclòs automàticament per Spring Security en tots els `<form method="post">` |

**`GlobalModelAdvice`**: classe `@ControllerAdvice` que injecta automàticament a totes les vistes:
- `isAdmin` — boolean si l'usuari té rol ADMIN
- `currentUserFoto` — ruta de la foto de perfil
- `currentUserNacionalitat` — codi ISO de 2 lletres

---

### 9.10 Pàgina de Perfil

Layout especial (no usa `cards-container`):
```
.profile-wrapper → grid 1fr 2fr
    .profile-left   → avatar gran (180px) + nom + botó editar
    .profile-right  → formulari edició + seccions d'informació
```

La foto de perfil s'envia com a `multipart/form-data` (màx 5 MB), es guarda a `data/uploads/` i es serveix via `/uploads/{filename}` configurat a `WebMvcConfig`.

Selector de nationalitat (`.flag-picker-btn`): botó desplegable que mostra una grid de països amb les seves banderes via `flagcdn.com`.

---

*Documentació generada el 02/03/2026*
