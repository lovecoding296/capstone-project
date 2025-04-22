let pricePerDay = 200000; // ví dụ: 200.000 VND/ngày



function fetchBusyDate(guideId) {
	console.log("fetchBusyDate guideId " + guideId)
	fetch(`/api/guides/${guideId}/busy-date`)
		.then(response => response.json())
		.then(data => {
			console.log("date  " + data.map(d => d.date))
			flatpickrInstance.set("disable", data.map(d => d.date));
		});

}

function openBookingPopup(guideId, guideName, price) {
	
	createBookingPopup()
	
	flatpickrInstance = flatpickr("#dateRange", {
		mode: "range",
		dateFormat: "Y-m-d",
		minDate: "today",

		onChange: function(selectedDates) {
			if (selectedDates.length === 2) {
				document.getElementById("startDate").value = selectedDates[0].toISOString().slice(0, 10);
				document.getElementById("endDate").value = selectedDates[1].toISOString().slice(0, 10);
				calculateTotal(); // Tính tiền sau khi chọn ngày
			}
		}
	});
	
	flatpickrInstance.clear();

	document.getElementById("bookingModal").style.display = "flex";
	document.getElementById("guideId").value = guideId;
	document.getElementById("guideName").textContent = guideName;

	document.getElementById("startDate").value = "";
	document.getElementById("endDate").value = "";
	document.getElementById("numPeople").value = 1;
	document.getElementById("totalPrice").textContent = "0";

	pricePerDay = price;

	fetchBusyDate(guideId)
}


function closeBookingPopup() {
	document.getElementById("bookingModal").style.display = "none";
}

function calculateTotal() {
	const start = new Date(document.getElementById("startDate").value);
	const end = new Date(document.getElementById("endDate").value);

	if (!isNaN(start) && !isNaN(end) && end >= start) {
		const days = (end - start) / (1000 * 60 * 60 * 24) + 1;
		const total = days * pricePerDay;
		document.getElementById("totalPrice").textContent = total.toLocaleString();
	}
}


function submitBooking(){
	
	createBooking()
	closeBookingPopup();
}

function createBookingPopup() {
    if (document.getElementById("bookingModal")) return; // Đã tồn tại thì không tạo lại

    const modalHtml = `
        <div id="bookingModal" class="modal" style="display:none;">
            <div class="modal-content">
                <span class="close" onclick="closeBookingPopup()">&times;</span>
                <h3>Thuê hướng dẫn viên: <span id="guideName"></span></h3>

                <form id="bookingForm">
                    <input type="hidden" id="guideId" name="guideId">

                    <div class="mb-3">
                        <label for="dateRange" class="form-label">Chọn ngày:</label>
                        <input id="dateRange" type="text" class="form-control" placeholder="Chọn ngày" required>
                        <input type="hidden" id="startDate" name="startDate" onchange="calculateTotal()">
                        <input type="hidden" id="endDate" name="endDate" onchange="calculateTotal()">
                    </div>

                    <div class="mb-3">
                        <label for="numPeople" class="form-label">Số người tham gia:</label>
                        <input type="number" class="form-control" id="numPeople" name="numPeople" oninput="calculateTotal()" min="1" value="1" required>
                    </div>

                    <div class="mb-3">
                        <label for="locationDetail" class="form-label">Địa điểm cụ thể:</label>
                        <input type="text" class="form-control" id="locationDetail" name="locationDetail" required>
                    </div>

                    <p class="fw-bold">Tổng tiền: <span id="totalPrice" class="text-danger">0</span> VND</p>

                    <div class="d-flex justify-content-between">
                        <button type="button" onclick="submitBooking()" class="btn btn-success">Xác nhận đặt lịch</button>
                        <button type="button" class="btn btn-secondary" onclick="closeBookingPopup()">Hủy</button>
                    </div>
                </form>
            </div>
        </div>
    `;

    const wrapper = document.createElement('div');
    wrapper.innerHTML = modalHtml.trim();
    document.body.appendChild(wrapper.firstChild);
}


function createBooking() {

	console.log("createBooking")

	let guideId = document.getElementById("guideId").value;
	let startDate = document.getElementById("startDate").value;
	let endDate = document.getElementById("endDate").value;
	let numberOfPeople = document.getElementById("numPeople").value;
	let destination = document.getElementById("locationDetail").value;
	let totalPrice = document.getElementById("totalPrice").textContent;

	const bookingData = {
		guide: { id: guideId },
		//user: { id: userId }, 
		startDate: startDate,
		endDate: endDate,
		numberOfPeople: parseInt(numberOfPeople),
		destination: destination,
		totalPrice: parseFloat(totalPrice.replace(/\./g, "").replace(",", ".")),
		status: "PENDING" // Status mặc định
	};

	fetch("/api/bookings/create", {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify(bookingData) // Gửi toàn bộ bookingData
	})
		.then(response => response.json())
		.then(data => {
			alert("Đã tham gia tour thành công! Vui lòng thanh toán và gửi phiếu thanh toán");
			window.location.href = `/users/bookings/${data.id}`;
		})
		.catch(error => console.error("Error joining:", error));

}