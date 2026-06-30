function updateEndTime() {
    const start = document.getElementById("startTime").value;
    const movie = document.getElementById("movie");

    if (!start || !movie) return;

    const duration = movie.options[movie.selectedIndex]
        .getAttribute("data-duration");

    const startDate = new Date(start);
    startDate.setMinutes(startDate.getMinutes() + parseInt(duration));

    document.getElementById("endTime").value =
        startDate.toISOString().slice(0,16);
}