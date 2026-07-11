document.addEventListener("DOMContentLoaded", function () {
    const dateTabs = document.querySelectorAll(".date-nav-item");
    const showtimeCards = document.querySelectorAll(".showtime-item-card");
    const noMovieAlert = document.getElementById("no-movie-alert");

    function filterShowtimes(selectedDate) {
        let hasVisibleCard = false;

        showtimeCards.forEach(card => {
            if (card.getAttribute("data-date") === selectedDate) {
                card.classList.remove("d-none");
                hasVisibleCard = true;
            } else {
                card.classList.add("d-none");
            }
        });

        // Nếu không có phim nào vào ngày này, hiển thị thông báo trống
        if (hasVisibleCard) {
            noMovieAlert.classList.add("d-none");
        } else {
            noMovieAlert.classList.remove("d-none");
        }
    }

    // Kích hoạt lọc ngày đầu tiên (Hôm nay) ngay khi tải xong trang
    const initialActiveTab = document.querySelector(".date-nav-item.active");
    if (initialActiveTab) {
        filterShowtimes(initialActiveTab.getAttribute("data-target-date"));
    }

    // Lắng nghe sự kiện click thay đổi ngày
    dateTabs.forEach(tab => {
        tab.addEventListener("click", function () {
            dateTabs.forEach(t => t.classList.remove("active"));
            this.classList.add("active");

            const selectedDate = this.getAttribute("data-target-date");
            filterShowtimes(selectedDate);
        });
    });
});