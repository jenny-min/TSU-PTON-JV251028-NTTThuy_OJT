//hiển thị endTime cho người dùng
const movie = document.getElementById("movie");
const start = document.getElementById("startTime");
const end = document.getElementById("endTime");

function calculateEndTime() {
    const option = movie.options[movie.selectedIndex];

    if (!option) return;

    const duration = parseInt(option.dataset.duration);

    if (isNaN(duration) || !start.value) {
        end.value = "";
        return;
    }

    const date = new Date(start.value);
    date.setMinutes(date.getMinutes() + duration + 15);

    const yyyy = date.getFullYear();
    const MM = String(date.getMonth() + 1).padStart(2, "0");
    const dd = String(date.getDate()).padStart(2, "0");
    const hh = String(date.getHours()).padStart(2, "0");
    const mm = String(date.getMinutes()).padStart(2, "0");

    end.value = `${dd}/${MM}/${yyyy} ${hh}:${mm}`;
}

movie?.addEventListener("change", calculateEndTime);
start?.addEventListener("change", calculateEndTime);

window.addEventListener("load", calculateEndTime);