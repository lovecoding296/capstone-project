document.addEventListener("DOMContentLoaded", function() {
	
	console.log("DOMContentLoaded")
	
    let dateInput = document.getElementById("date-input");
    let calendar = document.getElementById("calendar-container");

    if (dateInput && calendar) {
		
		console.log("click 1")
		
        dateInput.addEventListener("click", function() {
			console.log("click 1")
            calendar.style.display = calendar.style.display === "flex" ? "none" : "flex";
        });

        document.addEventListener("click", function(event) {
			console.log("click 2")
            if (!dateInput.contains(event.target) && !calendar.contains(event.target)) {
                calendar.style.display = "none";
            }
        });
    }
	
	
	const calendar1 = document.getElementById("calendar1");
	const calendar2 = document.getElementById("calendar2");
	const prevMonth = document.getElementById("prevMonth");
	const nextMonth = document.getElementById("nextMonth");

	let currentDate = new Date();
	let selectedDates = [];

	function renderCalendar(date, container) {
	    container.innerHTML = "";

	    const firstDay = new Date(date.getFullYear(), date.getMonth(), 1).getDay();
	    const lastDate = new Date(date.getFullYear(), date.getMonth() + 1, 0).getDate();

	    const header = document.createElement("div");
	    header.className = "calendar-header";
	    header.innerText = date.toLocaleString("default", { month: "long", year: "numeric" });
	    container.appendChild(header);

	    const daysRow = document.createElement("div");
	    daysRow.className = "calendar-days";
	    ["Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"].forEach(day => {
	        const dayDiv = document.createElement("div");
	        dayDiv.innerText = day;
	        daysRow.appendChild(dayDiv);
	    });
	    container.appendChild(daysRow);

	    const datesGrid = document.createElement("div");
	    datesGrid.className = "calendar-dates";

	    for (let i = 0; i < firstDay; i++) {
	        const emptyDiv = document.createElement("div");
	        datesGrid.appendChild(emptyDiv);
	    }

	    for (let i = 1; i <= lastDate; i++) {
	        const dateDiv = document.createElement("div");
	        dateDiv.innerText = i;

	        let dateString = `${date.getFullYear()}-${date.getMonth() + 1}-${i}`;

	        if (selectedDates.includes(dateString)) {
	            dateDiv.classList.add("selected");
	        }

	        dateDiv.addEventListener("click", () => handleDateSelection(dateString));
	        datesGrid.appendChild(dateDiv);
	    }

	    container.appendChild(datesGrid);
	}

	function handleDateSelection(dateString) {
	    if (selectedDates.includes(dateString)) {
	        selectedDates = selectedDates.filter(date => date !== dateString);
	    } else if (selectedDates.length < 2) {
	        selectedDates.push(dateString);
	    }
	    renderBothCalendars();
	}

	function renderBothCalendars() {
	    renderCalendar(new Date(currentDate.getFullYear(), currentDate.getMonth()), calendar1);
	    renderCalendar(new Date(currentDate.getFullYear(), currentDate.getMonth() + 1), calendar2);
	}

	prevMonth.addEventListener("click", () => {
	    currentDate.setMonth(currentDate.getMonth() - 1);
	    renderBothCalendars();
	});

	nextMonth.addEventListener("click", () => {
	    currentDate.setMonth(currentDate.getMonth() + 1);
	    renderBothCalendars();
	});

	renderBothCalendars();


	
	
});
