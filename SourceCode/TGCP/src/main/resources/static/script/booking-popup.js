let pricePerDay = 50; // ví dụ: 50 VND/day



function fetchGuideService(guideId) {
	console.log("fetchGuideService guideId " + guideId);

	fetch(`/api/guide-services/guides/${guideId}`)
		.then(response => response.json())
		.then(data => {
			// Chúng ta sẽ giữ tất cả dữ liệu ban đầu để dễ dàng cập nhật các dropdowns
			const allData = data;

			// Lắng nghe sự thay đổi trên các dropdown
			const serviceTypeSelect = document.getElementById('serviceType');
			const citySelect = document.getElementById('city');
			const groupSizeCategorySelect = document.getElementById('groupSizeCategory');
			const languageSelect = document.getElementById('language');

			// Cập nhật các dropdown khi có sự thay đổi
			serviceTypeSelect.addEventListener('change', function() {
				updateDropdowns(allData);
			});
			citySelect.addEventListener('change', function() {
				updateDropdowns(allData);
			});
			groupSizeCategorySelect.addEventListener('change', function() {
				updateDropdowns(allData);
			});
			languageSelect.addEventListener('change', function() {
				updateDropdowns(allData);
			});

			// Cập nhật các dropdowns ban đầu
			updateDropdowns(allData);
		})
		.catch(error => {
			console.error('Error fetching guide service:', error);
		});
}

function updateDropdowns(data) {
	const serviceTypeSelect = document.getElementById('serviceType');
	const citySelect = document.getElementById('city');
	const groupSizeCategorySelect = document.getElementById('groupSizeCategory');
	const languageSelect = document.getElementById('language');

	const selectedServiceType = serviceTypeSelect.value;
	const selectedCity = citySelect.value;
	const selectedGroupSizeCategory = groupSizeCategorySelect.value;
	const selectedLanguage = languageSelect.value;

	// Populate service type options
	const serviceTypes = [...new Set(data.map(item => item.type))];
	updateDropdown('SERVICE_TYPE', serviceTypeSelect, serviceTypes, selectedServiceType);

	// Populate city options
	const filteredByService = data.filter(item => !serviceTypeSelect.value || item.type === serviceTypeSelect.value);
	const cities = [...new Set(filteredByService.map(item => item.city))];
	updateDropdown('CITY', citySelect, cities, selectedCity);

	// Populate group size category options
	const filteredByServiceAndCity = filteredByService.filter(item => !citySelect.value || item.city === citySelect.value);
	const groupSizeCategories = [...new Set(filteredByServiceAndCity.map(item => item.groupSizeCategory))];
	updateDropdown('GROUP_SIZE_CATEGORY', groupSizeCategorySelect, groupSizeCategories, selectedGroupSizeCategory);

	// Populate language options
	const filteredByServiceCityGroup = filteredByServiceAndCity.filter(item => !groupSizeCategorySelect.value || item.groupSizeCategory === groupSizeCategorySelect.value);
	const languages = [...new Set(filteredByServiceCityGroup.map(item => item.language))];
	updateDropdown('LANGUAGE', languageSelect, languages, selectedLanguage);

	// Calculate price
	calculatePrice(data, serviceTypeSelect.value, citySelect.value, groupSizeCategorySelect.value, languageSelect.value);
}

function updateDropdown(optionType, selectElement, values, previouslySelectedValue) {
	// Ghi nhớ giá trị cũ
	const oldValue = previouslySelectedValue;

	// Xóa tất cả options
	selectElement.innerHTML = '';

	// Thêm placeholder
	const placeholder = document.createElement('option');
	placeholder.value = '';
	placeholder.textContent = 'Select an option';
	selectElement.appendChild(placeholder);

	// Thêm các option mới
	values.forEach(val => {
		const option = document.createElement('option');
		option.value = val;

		if (optionType === 'CITY') {
			option.textContent = cityDisplayNames[val];
		} else if (optionType === 'GROUP_SIZE_CATEGORY') {
			option.textContent = groupSizeCategoryDisplayNames[val];
		} else if (optionType === 'SERVICE_TYPE') {
			option.textContent = serviceTypeDisplayNames[val];
		} else {
			option.textContent = val;
		}
		selectElement.appendChild(option);
	});

	// Nếu giá trị cũ còn tồn tại thì chọn lại
	if (values.includes(oldValue)) {
		selectElement.value = oldValue;
	} else {
		selectElement.value = ''; // Nếu không thì chọn placeholder
	}
}


function clearDropdown(dropdown) {
	while (dropdown.options.length) {
		dropdown.remove(0);
	}
	const placeholder = document.createElement('option');
	placeholder.value = '';
	placeholder.textContent = 'Select an option';
	dropdown.appendChild(placeholder);
}

function calculatePrice(data, serviceType, city, groupSizeCategory, language) {
	const price = data.find(item =>
		(!serviceType || item.type === serviceType) &&
		(!city || item.city === city) &&
		(!groupSizeCategory || item.groupSizeCategory === groupSizeCategory) &&
		(!language || item.language === language)
	)?.price;

	if (price !== undefined) {
		pricePerDay = price;
		document.getElementById('pricePerDay').textContent = price.toLocaleString();
		calculateTotal();
	} else {
		document.getElementById('totalPrice').textContent = 'Price not available';
	}
}






function fetchDayOff(guideId) {
	console.log("fetchDayOff guideId " + guideId)
	fetch(`/api/guides/${guideId}/day-off`)
		.then(response => response.json())
		.then(data => {
			console.log("date  " + data.map(d => d.date))
			flatpickrInstance.set("disable", data.map(d => d.date));
		});

}

function openBookingPopupWithData(element) {
	const guideId = element.dataset.guideId;
	const guideName = element.dataset.guideName;
	const type = element.dataset.serviceType;
	const size = element.dataset.groupSize;
	const language = element.dataset.language;
	const city = element.dataset.city;
	const price = element.dataset.price;

	openBookingPopup(guideId, guideName);


	setTimeout(function() {
		// Gán giá trị cho các trường select
		document.getElementById("serviceType").value = type;
		document.getElementById("city").value = city;
		document.getElementById("groupSizeCategory").value = size;
		document.getElementById("language").value = language;
		document.getElementById("pricePerDay").textContent = parseFloat(price).toLocaleString();
	}, 100);

}


function openBookingPopup(guideId, guideName) {

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
	document.getElementById("totalPrice").textContent = "0";


	fetchDayOff(guideId)
	fetchGuideService(guideId)
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


function submitBooking() {

	createBooking()
	closeBookingPopup();
}





function createBookingPopup() {
	if (document.getElementById("bookingModal")) return; // Đã tồn tại thì không tạo lại

	const modalHtml = `
        <div id="bookingModal" class="modal" style="display:none;">
            <div class="modal-content">
                <span class="close" onclick="closeBookingPopup()">&times;</span>
                <h3>Booking a guide: <span id="guideName"></span></h3>

				<form id="bookingForm">
				    <input type="hidden" id="guideId" name="guideId">

				    <div class="mb-3 row">
				        <label for="dateRange" class="col-sm-4 col-form-label">Period:</label>
				        <div class="col-sm-8">
				            <input id="dateRange" type="text" class="form-control" placeholder="Booking Period" required>
				            <input type="hidden" id="startDate" name="startDate" onchange="calculateTotal()">
				            <input type="hidden" id="endDate" name="endDate" onchange="calculateTotal()">
				        </div>
				    </div>

				    <div class="mb-3 row">
				        <label for="serviceType" class="col-sm-4 col-form-label">Service:</label>
				        <div class="col-sm-8">
				            <select id="serviceType" class="form-control" required>
				                <!-- Options will be dynamically populated -->
				            </select>
				        </div>
				    </div>

				    <div class="mb-3 row">
				        <label for="city" class="col-sm-4 col-form-label">City:</label>
				        <div class="col-sm-8">
				            <select id="city" class="form-control" required>
				                <!-- Options will be dynamically populated -->
				            </select>
				        </div>
				    </div>

				    <div class="mb-3 row">
				        <label for="groupSizeCategory" class="col-sm-4 col-form-label">Group Size:</label>
				        <div class="col-sm-8">
				            <select id="groupSizeCategory" class="form-control" required>
				                <!-- Options will be dynamically populated -->
				            </select>
				        </div>
				    </div>

				    <div class="mb-3 row">
				        <label for="language" class="col-sm-4 col-form-label">Language:</label>
				        <div class="col-sm-8">
				            <select id="language" class="form-control" required>
				                <!-- Options will be dynamically populated -->
				            </select>
				        </div>
				    </div>

				    <div class="mb-3 row">
				        <label for="locationDetail" class="form-label">Areas you want to visit / desired itinerary:</label>
				        <div>
				            <input type="text" class="form-control" id="locationDetail" name="locationDetail" required>
				        </div>
				    </div>
					<p class="fw-bold">Price per day: <span id="pricePerDay" class="text-danger">0</span> VND (depends on specific service)</p>
				    <p class="fw-bold">Total price: <span id="totalPrice" class="text-danger">0</span> VND</p>

				    <div class="d-flex justify-content-between">
				        <button type="button" onclick="submitBooking()" class="btn btn-success">Confirm</button>
				        <button type="button" class="btn btn-secondary" onclick="closeBookingPopup()">Cancel</button>
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
	let destination = document.getElementById("locationDetail").value;
	let totalPrice = document.getElementById("totalPrice").textContent;
	let serviceType = document.getElementById("serviceType").value;
	let city = document.getElementById("city").value;
	let groupSizeCategory = document.getElementById("groupSizeCategory").value;
	let language = document.getElementById("language").value;
	
	if (!startDate || !endDate) {
	    alert("Please select period dates.");
	    return; 
	}
	
	if (!destination) {
		alert("Please enter the destination.");
		return;
	}

	

	const bookingData = {
		guide: { id: guideId },
		guideService: {
			guide: { id: guideId },
			type: serviceType,
			city: city,
			groupSizeCategory: groupSizeCategory,
			language: language
		},
		//user: { id: userId }, 
		startDate: startDate,
		endDate: endDate,
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
			alert("Booking successful! Please pay and send payment receipt");
			window.location.href = `/users/bookings/${data.id}`;
		})
		.catch(error => console.error("Error joining:", error));

}