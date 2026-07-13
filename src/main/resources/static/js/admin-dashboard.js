document.addEventListener("DOMContentLoaded", () => {
    // 1. Tìm phần tử HTML chứa dữ liệu ẩn
    const chartDataElement = document.getElementById("chart-data");
    if (!chartDataElement) return;

    try {
        // 2. Parse chuỗi text thành đối tượng JSON
        const chartData = JSON.parse(chartDataElement.textContent);

        // 3. Khởi tạo biểu đồ Chart.js - chưa hiển thị
        new Chart(
            document.getElementById("revenueChart"),
            {
                type: "line",
                data: {
                    labels: chartData.labels, // Mảng các nhãn (Ngày, Tháng...)
                    datasets: [{
                        label: "Doanh thu",
                        data: chartData.values, // Mảng các giá trị số số tiền
                        borderColor: "#dc2626", // Màu đỏ giống theme rạp phim
                        backgroundColor: "rgba(220, 38, 38, 0.15)",
                        fill: true,
                        tension: 0.4
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: {
                            labels: { color: '#94a3b8' } // Màu chữ chú thích
                        }
                    },
                    scales: {
                        y: {
                            grid: { color: '#334155' },
                            ticks: { color: '#94a3b8' }
                        },
                        x: {
                            grid: { color: '#334155' },
                            ticks: { color: '#94a3b8' }
                        }
                    }
                }
            }
        );
    } catch (error) {
        console.error("Lỗi khi parse dữ liệu biểu đồ:", error);
    }
});