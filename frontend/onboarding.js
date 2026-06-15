const slider = document.getElementById('slider');
const dots = document.querySelectorAll('.dot');
const nextBtn = document.getElementById('nextBtn');
const skipBtn = document.querySelector('.skip-btn');

let currentSlide = 0;
const totalSlides = 3;

nextBtn.addEventListener('click', () => {
    if (currentSlide < totalSlides - 1) {
        currentSlide++;
        updateSlider();
    } else {
        window.location.href = 'role-selection.html';
    }
});

skipBtn.addEventListener('click', () => {
    window.location.href = 'role-selection.html';
});

function updateSlider() {
    slider.style.transform = `translateX(-${currentSlide * 100}%)`;
    
    // Update dots
    dots.forEach((dot, index) => {
        dot.classList.toggle('active', index === currentSlide);
    });

    // Update button text
    if (currentSlide === totalSlides - 1) {
        nextBtn.innerHTML = 'Get Started';
    } else {
        nextBtn.innerHTML = 'Continue <span class="arrow">›</span>';
    }
}
