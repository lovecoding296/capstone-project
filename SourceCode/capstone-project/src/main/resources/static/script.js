document.addEventListener("DOMContentLoaded", function() {
	
	console.log("DOMContentLoaded")
	
    let dateInput = document.getElementById("date-input");
    let calendar = document.getElementById("calendar");

    if (dateInput && calendar) {
		
		console.log("click 1")
		
        dateInput.addEventListener("click", function() {
			console.log("click 1")
            calendar.style.display = calendar.style.display === "block" ? "none" : "block";
        });

        document.addEventListener("click", function(event) {
			console.log("click 2")
            if (!dateInput.contains(event.target) && !calendar.contains(event.target)) {
                calendar.style.display = "none";
            }
        });
    }
	
	
	const monthYear = document.getElementById("monthYear");
	const calendarDates = document.getElementById("calendarDates");
	const prevMonth = document.getElementById("prevMonth");
	const nextMonth = document.getElementById("nextMonth");

	let currentDate = new Date();

	function renderCalendar(date) {
	    calendarDates.innerHTML = "";
	    const firstDay = new Date(date.getFullYear(), date.getMonth(), 1).getDay();
	    const lastDate = new Date(date.getFullYear(), date.getMonth() + 1, 0).getDate();

	    monthYear.innerText = date.toLocaleString("default", { month: "long", year: "numeric" });

	    for (let i = 0; i < firstDay; i++) {
	        const emptyDiv = document.createElement("div");
	        calendarDates.appendChild(emptyDiv);
	    }

	    for (let i = 1; i <= lastDate; i++) {
	        const dateDiv = document.createElement("div");
	        dateDiv.innerText = i;
	        dateDiv.addEventListener("click", () => alert(`Selected date: ${i}/${date.getMonth() + 1}/${date.getFullYear()}`));
	        calendarDates.appendChild(dateDiv);
	    }
	}

	prevMonth.addEventListener("click", () => {
	    currentDate.setMonth(currentDate.getMonth() - 1);
	    renderCalendar(currentDate);
	});

	nextMonth.addEventListener("click", () => {
	    currentDate.setMonth(currentDate.getMonth() + 1);
	    renderCalendar(currentDate);
	});

	renderCalendar(currentDate);

	
	
});
