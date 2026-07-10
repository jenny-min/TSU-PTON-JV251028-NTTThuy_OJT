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
                btnQuickBook.disabled = true;
                return;
            }

            // Gọi API Fetch ngầm tới endpoint lấy suất chiếu sắp diễn ra của phim
            fetch(`/api/public/showtimes?movieId=${movieId}`)
                .then(response => response.json())
                .then(data => {
                    showtimeSelect.innerHTML = '<option value="">-- Chọn suất chiếu thích hợp --</option>';

                    if (data.length === 0) {
                        showtimeSelect.innerHTML = '<option value="">Hiện tại phim này hết suất chiếu</option>';
                        showtimeSelect.disabled = true;
                        btnQuickBook.disabled = true;
                        return;
                    }

                    // Đổ dữ liệu JSON nhận được vào thẻ select suất chiếu
                    data.forEach(st => {
                        const startTime = new Date(st.startTime);
                        const formattedTime = startTime.toLocaleTimeString('vi-VN', {hour: '2-digit', minute:'2-digit'})
                            + " (" + startTime.toLocaleDateString('vi-VN', {day: '2-digit', month: '2-digit'}) + ")";

                        const option = document.createElement("option");
                        option.value = st.showtimeId;
                        option.text = `${st.roomName} | Giờ: ${formattedTime}`;
                        showtimeSelect.appendChild(option);
                    });

                    // Kích hoạt ô chọn và nút bấm cho phép thao tác công đoạn tiếp theo
                    showtimeSelect.disabled = false;
                    btnQuickBook.disabled = false;
                })
                .catch(error => {
                    console.error("Lỗi đồng bộ suất chiếu:", error);
                    showtimeSelect.innerHTML = '<option value="">Lỗi tải dữ liệu suất chiếu</option>';
                });
        });
    }
});