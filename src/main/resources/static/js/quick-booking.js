document.addEventListener("DOMContentLoaded", function () {
    const movieSelect = document.getElementById("movieSelect");
    const showtimeSelect = document.getElementById("showtimeSelect");
    const btnQuickBook = document.getElementById("btnQuickBook");

    if (movieSelect) {
        movieSelect.addEventListener("change", function () {
            const movieId = this.value;

            if (!movieId) {
                showtimeSelect.innerHTML = '<option value="">-- Vui lòng chọn phim trước --</option>';
                showtimeSelect.disabled = true;
                if(btnQuickBook) btnQuickBook.disabled = true;
                return;
            }

            fetch(`/api/public/showtimes?movieId=${movieId}`)
                .then(response => {
                    if (!response.ok) throw new Error("Lỗi HTTP! Trạng thái: " + response.status);
                    return response.text();
                })
                .then(text => {
                    if (!text || text.trim() === "") return [];
                    try { return JSON.parse(text); } catch (e) { return []; }
                })
                .then(data => {
                    showtimeSelect.innerHTML = '<option value="">-- Chọn suất chiếu thích hợp --</option>';

                    if (!data || data.length === 0) {
                        showtimeSelect.innerHTML = '<option value="">Hiện tại phim này hết suất chiếu</option>';
                        showtimeSelect.disabled = true;
                        if(btnQuickBook) btnQuickBook.disabled = true;
                        return;
                    }

                    data.forEach(st => {
                        const startTime = new Date(st.startTime);
                        const formattedTime = startTime.toLocaleTimeString('vi-VN', {hour: '2-digit', minute:'2-digit'})
                            + " (" + startTime.toLocaleDateString('vi-VN', {day: '2-digit', month: '2-digit'}) + ")";

                        const option = document.createElement("option");
                        option.value = st.showtimeId;
                        option.text = `${st.roomName} | Giờ: ${formattedTime}`;
                        showtimeSelect.appendChild(option);
                    });

                    showtimeSelect.disabled = false;
                })
                .catch(error => {
                    console.error("Lỗi đồng bộ suất chiếu:", error);
                    showtimeSelect.innerHTML = '<option value="">Lỗi tải dữ liệu suất chiếu</option>';
                });
        });
    }

    if (showtimeSelect && btnQuickBook) {
        showtimeSelect.addEventListener("change", function () {
            if (this.value !== "") {
                btnQuickBook.disabled = false;
            } else {
                btnQuickBook.disabled = true;
            }
        });
    }
});