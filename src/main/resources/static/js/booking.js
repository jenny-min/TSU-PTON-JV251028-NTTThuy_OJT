    document.addEventListener("DOMContentLoaded", function () {

        const seats = document.querySelectorAll(".seat");

        const selectedSeatsEl = document.getElementById("selectedSeats");
        const seatCountEl = document.getElementById("seatCount");
        const totalPriceEl = document.getElementById("totalPrice");

        let selectedSeats = [];
        let totalPrice = 0;

        seats.forEach(seat => {

            // bỏ qua ghế đã đặt
            if (seat.classList.contains("booked")) return;

        seat.addEventListener("click", function () {

            const seatName = this.dataset.name;
            const price = parseInt(this.dataset.price || 0);

            // check đã chọn chưa
            if (this.classList.contains("selected")) {

            // bỏ chọn
            this.classList.remove("selected");

            selectedSeats = selectedSeats.filter(s => s.name !== seatName);
            totalPrice -= price;

            } else {
                // chọn ghế
                this.classList.add("selected");

                selectedSeats.push({
                name: seatName,
                price: price
                });

                totalPrice += price;
            }

            updateUI();
        });
    });

    function updateUI() {
    // danh sách ghế
        if (selectedSeats.length === 0) {
        selectedSeatsEl.innerText = "Chưa chọn";
        } else {
        selectedSeatsEl.innerText =
        selectedSeats.map(s => s.name).join(", ");
        }

        // số lượng ghế
        seatCountEl.innerText = selectedSeats.length;

        // tổng tiền
        totalPriceEl.innerText =
        new Intl.NumberFormat('vi-VN').format(totalPrice) + " ₫";
        }
    });

    function syncSeatsToForm(selectedSeats) {
        document.getElementById("selectedSeatsInput").value =
            selectedSeats.map(s => s.name).join(",");
    }