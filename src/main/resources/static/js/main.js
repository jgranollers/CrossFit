// Funcionalidades básicas del sitio

document.addEventListener('DOMContentLoaded', function() {
    // Filtrar en el sidebar
    const filterInput = document.getElementById('filterInput');
    if (filterInput) {
        filterInput.addEventListener('input', function() {
            const filterText = this.value.toLowerCase();
            const sidebarLinks = document.querySelectorAll('.sidebar-link');
            sidebarLinks.forEach(link => {
                const text = link.textContent.toLowerCase();
                if (text.includes(filterText)) {
                    link.style.display = 'block';
                } else {
                    link.style.display = 'none';
                }
            });
        });
    }

    // Animación de hover en las tarjetas
    const cards = document.querySelectorAll('.card');
    cards.forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-5px)';
        });
        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
        });
    });

    // Botones de acción
    const buttons = document.querySelectorAll('.btn');
    buttons.forEach(button => {
        button.addEventListener('click', function(e) {
            // Ripple effect
            const rect = this.getBoundingClientRect();
            const x = e.clientX - rect.left;
            const y = e.clientY - rect.top;

            const ripple = document.createElement('span');
            ripple.style.position = 'absolute';
            ripple.style.left = x + 'px';
            ripple.style.top = y + 'px';
            ripple.style.width = '10px';
            ripple.style.height = '10px';
            ripple.style.backgroundColor = 'rgba(255, 255, 255, 0.5)';
            ripple.style.borderRadius = '50%';
            ripple.style.animation = 'ripple 0.6s ease-out';
            this.style.position = 'relative';
            this.style.overflow = 'hidden';
            this.appendChild(ripple);

            setTimeout(() => ripple.remove(), 600);
        });
    });

    // Cerrar sesión o perfil
    const profileBtn = document.querySelector('.btn-profile');
    if (profileBtn) {
        profileBtn.addEventListener('click', function() {
            alert('Perfil del usuario - Próximamente');
        });
    }
});

// Animación Ripple CSS
const style = document.createElement('style');
style.textContent = `
    @keyframes ripple {
        to {
            transform: scale(4);
            opacity: 0;
        }
    }
`;
document.head.appendChild(style);

// Formatear números como moneda
function formatCurrency(value) {
    return new Intl.NumberFormat('ca-ES', {
        style: 'currency',
        currency: 'EUR'
    }).format(value);
}

// Formatear fecha
function formatDate(dateString) {
    const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
    return new Date(dateString).toLocaleDateString('ca-ES', options);
}

// Log para debugging
console.log('Script principal cargado');
