document.addEventListener("DOMContentLoaded", function() {
    setInterval(() => {
        const now = new Date();
        const clockEl = document.getElementById('liveClock');
        if(clockEl) {
            clockEl.innerText = now.toLocaleTimeString('vi-VN');
        }
    }, 1000);
});