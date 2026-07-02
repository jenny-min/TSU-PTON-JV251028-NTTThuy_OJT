document.addEventListener("DOMContentLoaded", function () {

    const selectedSeatsElement = document.getElementById("selectedSeats");
    const seatCountElement = document.getElementById("seatCount");
    const totalPriceElement = document.getElementById("totalPrice");

    const form = document.getElementById("confirmForm");
    const seatInput = document.getElementById("selectedSeatsInput");

    const seats = document.querySelectorAll(".seat:not(.booked)");

    let selectedSeatList = [];

    // =========================
    // UPDATE UI SUMMARY
    // =========================
    function updateSummary() {

        let total = 0;

        selectedSeatList.forEach(seat => {
            total += Number(seat.price || 0);
        });

        selectedSeatsElement.textContent =
            selectedSeatList.length > 0
                ? selectedSeatList.map(s => s.name).join(", ")
                : "Chưa chọn";

        seatCountElement.textContent = selectedSeatList.length;

        totalPriceElement.textContent =
            total.toLocaleString("vi-VN") + " VND";
    }

    // =========================
    // SEAT CLICK HANDLER
    // =========================
    seats.forEach(function (seat) {

        seat.addEventListener("click", function () {

            const seatName = this.dataset.name;
            const seatPrice = Number(this.dataset.price);

            const index = selectedSeatList.findIndex(s => s.name === seatName);

            // toggle select
            if (index === -1) {
                selectedSeatList.push({
                    name: seatName,
                    price: seatPrice
                });
                this.classList.add("selected");
            } else {
                selectedSeatList.splice(index, 1);
                this.classList.remove("selected");
            }

            updateSummary();
        });
    });

    // SUBMIT FORM
    form.addEventListener("submit", function (e) {

        // lấy từ state
        const seats = selectedSeatList.map(s => s.name);

        console.log("SELECTED SEATS:", seats);

        if (seats.length === 0) {
            e.preventDefault();
            alert("Vui lòng chọn ghế!");
            return;
        }

        seatInput.value = seats.join(",");

        console.log("Submitting seats:", seatInput.value);
    });

    // init UI
    updateSummary();
});