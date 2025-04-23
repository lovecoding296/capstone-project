function changeLanguage(lang) {
	let url = new URL(window.location.href); // Get current URL
	url.searchParams.set('lang', lang); // Set or update 'lang' parameter
	window.location.href = url.toString(); // Redirect to new URL
}


function autoResize(textarea) {
	textarea.style.height = 'auto';  // Đặt lại chiều cao về mặc định
	textarea.style.height = (textarea.scrollHeight) + 'px';  // Cập nhật chiều cao
}

/* Submit review */

function openReviewModal(bookingId, reviewedUserId, reviewedUserName) {
    checkReviewStatus(bookingId).then(function(hasReviewed) {
        if (hasReviewed) {
            alert("You have already submitted a review.");
            return;
        }

        createReviewModal(bookingId, reviewedUserId);
        document.getElementById("reviewModal").style.display = "flex";
        document.getElementById("guideName").innerText = reviewedUserName;
    });
}

function checkReviewStatus(bookingId) {
    return fetch(`/api/reviews/exists?bookingId=${bookingId}`, {
        method: 'GET',
        headers: {
            'Accept': 'application/json'
        }
    })
    .then(function(response) {
        if (!response.ok) {
            throw new Error("HTTP error! status: " + response.status);
        }
        return response.json();
    })
    .then(function(hasReviewed) {
        console.log("bookingId hasReviewed: " + hasReviewed);
        return hasReviewed;
    })
    .catch(function(error) {
        console.error("Lỗi khi kiểm tra đánh giá:", error);
        return false;
    });
}



function submitReview(bookingId, reviewedUserId) {
    // Lấy số sao đã chọn
	const rating = document.getElementById("rating").value;
    
    // Lấy nội dung feedback
    const feedback = document.getElementById("feedback").value.trim();
		
	console.log("rating " + rating)

    // Kiểm tra hợp lệ
    if (!rating) {
        alert("Please select a rating.");
        return;
    }
    if (feedback === "") {
        alert("Please enter your feedback.");
        return;
    }

    // Tạo object dữ liệu
    const reviewData = {
		reviewedUser: {id: reviewedUserId},
		booking: {id: bookingId},
        rating: parseInt(rating),
        feedback: feedback
    };

	fetch('/api/reviews', {
	    method: 'POST',
	    headers: { 'Content-Type': 'application/json' },
	    body: JSON.stringify(reviewData)
	})
	.then(res => {
	    if (!res.ok) throw new Error("Failed to submit review");
	    return res.text();
	})
	.then(msg => {
	    alert(msg); // hoặc hiển thị ra giao diện
	    closeReviewPopup();
	    document.getElementById("reviewForm").reset();
	})
	.catch(err => {
	    console.error("Submit failed", err);
	    alert("Failed to submit review.");
	});

    // Đóng popup và reset form
    closeReviewPopup();
    document.getElementById("reviewForm").reset();
}

function closeReviewPopup () {
	document.getElementById("reviewModal").style.display = "none";
}

function createReviewModal(bookingId, reviewedUserId) {
    if (document.getElementById("reviewModal")) return;

    const modalHtml = `
        <div id="reviewModal" class="modal" style="display:none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0,0,0,0.5); z-index: 9999;">
            <div class="modal-content" style="background: white; padding: 20px; border-radius: 10px; max-width: 500px; margin: 100px auto; position: relative;">
                <span class="close" onclick="closeReviewPopup()" style="position: absolute; top: 10px; right: 15px; font-size: 24px; cursor: pointer;">&times;</span>
                <h3 style="margin-bottom: 20px;">Feedback: <span id="guideName"></span></h3>

                <form id="reviewForm">
                    <!-- Rating -->
                    <div style="margin-bottom: 15px;">
                        <label style="display: block; margin-bottom: 5px;">Rating:</label>
                        <div id="starContainer" style="display: flex; justify-content: start; gap: 5px; font-size: 28px;">
                            ${[1, 2, 3, 4, 5].map(i => `
                                <label style="color: #ccc; cursor: pointer; transition: color 0.2s;" onmouseover="highlightStars(${i})" onmouseout="resetStars()" onclick="selectStar(${i})" id="star-${i}">★</label>
                            `).join('')}
                        </div>
                        <input type="hidden" id="rating" name="rating">
                    </div>

                    <!-- Feedback -->
                    <div style="margin-bottom: 15px;">
                        <label for="feedback" style="display: block; margin-bottom: 5px;">Your Feedback:</label>
                        <input type=text id="feedback" name="feedback" style="width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px;" rows="4" placeholder="Write your review here..."></textarea>
                    </div>

                    <!-- Buttons -->
                    <div style="display: flex; justify-content: space-between;">
                        <button type="button" onclick="submitReview(${bookingId}, ${reviewedUserId})" style="background-color: #28a745; color: white; padding: 8px 16px; border: none; border-radius: 4px;">Submit</button>
                        <button type="button" onclick="closeReviewPopup()" style="background-color: #6c757d; color: white; padding: 8px 16px; border: none; border-radius: 4px;">Cancel</button>
                    </div>
                </form>
            </div>
        </div>
    `;

    const wrapper = document.createElement('div');
    wrapper.innerHTML = modalHtml.trim();
    document.body.appendChild(wrapper.firstChild);
}

function highlightStars(starCount) {
    for (let i = 1; i <= 5; i++) {
        document.getElementById(`star-${i}`).style.color = i <= starCount ? 'gold' : '#ccc';
    }
}

function resetStars() {
    const selected = parseInt(document.getElementById("rating").value || 0);
    for (let i = 1; i <= 5; i++) {
        document.getElementById(`star-${i}`).style.color = i <= selected ? 'gold' : '#ccc';
    }
}

function selectStar(starCount) {
    document.getElementById("rating").value = starCount;
    highlightStars(starCount);
}


/* income summary */
function loadIncomeSummary() {
	fetch(`/api/guides/income-summary`)
		.then(res => res.json())
		.then(data => {
			document.getElementById("totalIncome").textContent = data.totalIncome.toLocaleString("vi-VN");

			// Vẽ biểu đồ
			const ctx = document.getElementById("incomeChart").getContext("2d");
			new Chart(ctx, {
				type: 'bar',
				data: {
					labels: Object.keys(data.monthlyIncome),
					datasets: [{
						label: 'Thu nhập theo tháng',
						data: Object.values(data.monthlyIncome),
						backgroundColor: 'rgba(75, 192, 192, 0.5)'
					}]
				}
			});

			// Hiển thị danh sách đơn
			const list = document.getElementById("completedBookingsList");
			data.bookings.forEach(b => {
				const item = document.createElement("li");
				item.textContent = `Customer: ${b.customer.fullName} | amount: ${b.totalPrice} | date: ${b.endDate}`;
				list.appendChild(item);
			});
		});
}


/* manage BusyDate */
let flatpickrInstance;
let originalDates = [];   // các ngày đã lưu từ server
let originalAutoGeneratedDates = [];
let selectedDates = [];   // ngày người dùng chọn hiện tại

function fetchBusyDate() {
	fetch('/api/guides/busy-date')
		.then(response => response.json())
		.then(data => {
			originalDates = data
				.filter(d => !d.autoGenerated)
				.map(d => d.date);

			originalAutoGeneratedDates = data
				.filter(d => d.autoGenerated)
				.map(d => d.date);

			selectedDates = [...originalDates];

			console.log("data " + data)
			console.log("originalDates " + originalDates)

			//if (flatpickrInstance) {
			//flatpickrInstance.destroy();
			//}

			flatpickrInstance = flatpickr("#manualBusyDates", {
				mode: "multi", // Cho phép chọn nhiều ngày
				dateFormat: "Y-m-d",
				minDate: "today",
				disable: originalAutoGeneratedDates,
				inline: true,
				defaultDate: selectedDates, // Khởi tạo các ngày đã chọn
				onDayCreate: function(dObj, dStr, fp, dayElem) {
					const date = fp.formatDate(dayElem.dateObj, "Y-m-d");
					if (selectedDates.includes(date)) {
						console.log("add busy-date " + date)
						dayElem.classList.add("busy-date");
					}

					// Toggle chọn ngày
					dayElem.addEventListener("click", function() {
						if (selectedDates.includes(date)) {
							selectedDates = selectedDates.filter(d => d !== date);
						} else {
							selectedDates.push(date);
						}
						fp.setDate(selectedDates, false); // Cập nhật lại các ngày chọn
						fp.redraw(); // Cập nhật giao diện
					});
				},
				onReady: function() {
					if (flatpickrInstance) {
						flatpickrInstance.setDate(selectedDates, false); // Cập nhật lại các ngày đã chọn
						flatpickrInstance.redraw(); // Vẽ lại giao diện với màu đã chọn
					}
				}
			});
		});
}

function deleteBusyDates(datesToDelete) {
	if (datesToDelete.length === 0) return Promise.resolve();
	return fetch('/api/guides/busy-date/delete', {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify(datesToDelete)
	});
}

function addBusyDates(datesToAdd) {
	if (datesToAdd.length === 0) return Promise.resolve();
	return fetch('/api/guides/busy-date', {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify(datesToAdd)
	});
}

function submitBusyDate() {
	const toAdd = selectedDates.filter(date => !originalDates.includes(date));
	const toDelete = originalDates.filter(date => !selectedDates.includes(date));

	deleteBusyDates(toDelete)
		.then(() => addBusyDates(toAdd))
		.then(() => {
			alert("Đã cập nhật lịch bận!");
			fetchBusyDate(); // reload lại từ server
		})
		.catch(err => {
			console.error("Lỗi cập nhật:", err);
			alert("Đã xảy ra lỗi, vui lòng thử lại.");
		});
}


/*manage bookings*/

let guideBooking = {
	currentPage: 1,
	itemsPerPage: 10
};


async function fetchGuidesBookings(page = 1) {
	guideBooking.currentPage = page;
	const response = await fetch('/api/guides/bookings');
	const bookings = await response.json();

	// Tính toán chỉ số bắt đầu và kết thúc cho phân trang
	const startIdx = (page - 1) * guideBooking.itemsPerPage;
	const endIdx = startIdx + guideBooking.itemsPerPage;

	// Lấy dữ liệu bookings cho trang hiện tại
	const bookingsToShow = bookings.slice(startIdx, endIdx);


	const tbody = document.getElementById("bookingTableBody");
	tbody.innerHTML = "";

	bookingsToShow.forEach((booking) => {
		const row = document.createElement("tr");

		row.innerHTML = `
	      <td>${booking.destination}</td>
	      <td>${formatDate(booking.startDate)}</td>
		  <td>${formatDate(booking.endDate)}</td>
		  <td>${formatDate(booking.createdAt)}</td>
		  <td>${booking.customer.fullName}</td>
		  <td>
		    <span class="badge ${getStatusBadgeClass(booking.status)}">
		      ${booking.status}
		    </span>
			${booking.status === "CANCELED_BY_USER" || booking.status === "CANCELED_BY_GUIDE" || booking.status === "REJECTED"
				? `<div class="text-muted small fst-italic mt-1">Lý do: ${booking.reason || "Không có lý do"}</div>`
				: ""
			}
		  </td>
		  <td>
		    <div class="d-flex gap-2">
		      <a class="btn btn-sm btn-primary" href="/guides/bookings/${booking.id}" data-id="${booking.id}">View</a>

		      ${booking.status === "PENDING" ? `
		        <button class="btn btn-sm btn-success" onclick="handleBookingAction('${booking.id}', 'confirm')">Confirm</button>
		        <button class="btn btn-sm btn-danger" onclick="handleBookingAction('${booking.id}', 'reject')">Reject</button>
		      ` : ""}

		      ${booking.status === "CONFIRMED" ? `
		        <button class="btn btn-sm btn-warning" onclick="handleBookingAction('${booking.id}', 'complete')">Complete</button>
		        <button class="btn btn-sm btn-danger" onclick="handleBookingAction('${booking.id}', 'cancel-by-guide')">Cancel</button>
		      ` : ""}

		      ${booking.status === "COMPLETED" ? `
		        <button class="btn btn-sm btn-success" onclick="openReviewModal(${booking.id}, ${booking.customer.id},'${booking.customer.fullName}')">Submit Review</button>
		      ` : ""}
		    </div>
		  </td>

	    `;

		tbody.appendChild(row);
	});

	// Cập nhật trạng thái các nút pagination
	updatePaginationButtons(page, bookings.length, guideBooking.itemsPerPage);
}

function updatePaginationButtons(page, totalItems, itemsPerPage) {
	const totalPages = Math.ceil(totalItems / itemsPerPage);

	const prevBtn = document.getElementById('prev-btn');
	const nextBtn = document.getElementById('next-btn');

	// Nếu là trang đầu tiên, disable nút "Previous"
	if (page === 1) {
		prevBtn.disabled = true;
		prevBtn.classList.add('disabled-btn');
	} else {
		prevBtn.disabled = false;
		prevBtn.classList.remove('disabled-btn');
	}

	// Nếu là trang cuối cùng, disable nút "Next"
	if (page === totalPages) {
		nextBtn.disabled = true;
		nextBtn.classList.add('disabled-btn');
	} else {
		nextBtn.disabled = false;
		nextBtn.classList.remove('disabled-btn');
	}
}

function changePage(direction) {
	if (direction === 'prev') {
		userBooking.currentPage -= 1;
	} else if (direction === 'next') {
		userBooking.currentPage += 1;
	}

	fetchBookings(userBooking.currentPage); // Fetch lại bookings cho trang mới
}

function getStatusBadgeClass(status) {
	switch (status.toLowerCase()) {
		case "confirmed": return "bg-success";
		case "pending": return "bg-warning text-dark";
		case "cancled_by_user": return "bg-danger";
		case "cancled_by_guide": return "bg-danger";
		case "rejected": return "bg-danger";
		case "completed": return "bg-info text-white";
		default: return "bg-secondary";
	}
}

function handleBookingAction(bookingId, action) {
	console.log("handleBookingAction " + bookingId + " " + action);
	let reason = null;

	if (action === 'cancel-by-user' || action === 'cancel-by-guide' || action === 'reject') {
		reason = prompt("Nhập lý do:");
		if (!reason) {
			alert("Bạn cần nhập lý do hủy!");
			return;
		}
	}

	fetch(`/api/bookings/${bookingId}/${action}`, {
		method: "PUT",
		headers: {
			"Content-Type": "application/json",
		},
		body: JSON.stringify({
			reason
		})
	})
		.then(async (res) => {
			const data = await res.json();
			if (!res.ok) {
				throw new Error(data.message || "Đã xảy ra lỗi");
			}
			return data;
		})
		.then((result) => {
			alert(result.message || `${action} thành công`);
			fetchGuidesBookings(); // hoặc update riêng dòng nếu bạn muốn tối ưu
		})
		.catch((err) => {
			console.error(err);
			alert(err.message || "Đã xảy ra lỗi");
		});
}



/*booking history*/
let userBooking = {
	currentPage: 1,
	itemsPerPage: 10
};


async function fetchBookings(page = 1) {
	userBooking.currentPage = page;
	const response = await fetch('/api/bookings');
	const bookings = await response.json();

	// Tính toán chỉ số bắt đầu và kết thúc cho phân trang
	const startIdx = (page - 1) * userBooking.itemsPerPage;
	const endIdx = startIdx + userBooking.itemsPerPage;

	// Lấy dữ liệu bookings cho trang hiện tại
	const bookingsToShow = bookings.slice(startIdx, endIdx);


	const tbody = document.getElementById("bookingTableBody");
	tbody.innerHTML = "";

	bookingsToShow.forEach((booking) => {
		const row = document.createElement("tr");

		row.innerHTML = `
	      <td>${booking.destination}</td>
	      <td>${formatDate(booking.startDate)}</td>
		  <td>${formatDate(booking.endDate)}</td>
		  <td>${formatDate(booking.createdAt)}</td>
		  <td>${booking.guide.fullName}</td>
		  <td>
		    <span class="badge ${getStatusBadgeClass(booking.status)}">
		      ${booking.status}
		    </span>
		    ${booking.status === "CANCELED_BY_USER" || booking.status === "CANCELED_BY_GUIDE" || booking.status === "REJECTED"
				? `<div class="text-muted small fst-italic mt-1">Lý do: ${booking.reason || "Không có lý do"}</div>`
				: ""
			}
		  </td>
		  <td>
		    <div class="d-flex gap-2">
		      <a class="btn btn-sm btn-primary" href="/users/bookings/${booking.id}" data-id="${booking.id}">View</a>
		      
		      ${booking.status === "PENDING" ? `
		        <button class="btn btn-sm btn-danger" onclick="handleBookingAction('${booking.id}', 'cancel-by-user')">Cancel</button>
		      ` : booking.status === "COMPLETED" ? `
		        <button class="btn btn-sm btn-success" onclick="openReviewModal(${booking.id}, ${booking.guide.id},'${booking.guide.fullName}')">Submit Review</button>
		      ` : ''}
		      
		    </div>
		  </td>

	    `;

		tbody.appendChild(row);
	});

	// Cập nhật trạng thái các nút pagination
	updatePaginationButtons(page, bookings.length, userBooking.itemsPerPage);
}


function changePage(direction) {
	if (direction === 'prev') {
		userBooking.currentPage -= 1;
	} else if (direction === 'next') {
		userBooking.currentPage += 1;
	}

	fetchBookings(userBooking.currentPage); // Fetch lại bookings cho trang mới
}



async function cancelBooking(bookingId) {
	if (confirm("Bạn có chắc chắn muốn hủy đặt chỗ?")) {
		let canceledReason = prompt("Nhập lý do hủy:");
		if (canceledReason) {
			await fetch(`/api/bookings/${bookingId}/cancel`, {
				method: 'PUT',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify({ reason: canceledReason })
			});
			fetchBookings(currentPage);
		}
	}
}

/*guide register */
function previewImage(event) {
	console.log("previewImage...")
	const file = event.target.files[0];  // Lấy file người dùng chọn
	const previewDiv = document.getElementById('guideLicensePreview');  // Nơi hiển thị ảnh preview
	previewDiv.innerHTML = '';  // Xóa mọi ảnh preview trước đó (nếu có)

	if (file) {
		const reader = new FileReader();  // Đọc file ảnh

		reader.onload = function(e) {
			previewDiv.innerHTML = `<img src="${e.target.result}" alt="Ảnh giấy phép" class="img-thumbnail mt-2" width="200">`;
		};

		reader.readAsDataURL(file);  // Đọc file ảnh dưới dạng base64
	}
}

function checkGuideRequestStatus() {
	fetch('/api/guide-requests/status')  // Giả sử có API để lấy trạng thái đăng ký
		.then(response => response.json())
		.then(data => {
			const statusMessage = document.getElementById('statusMessage');
			const guideForm = document.getElementById('guideForm');
			const guideLicenseInput = document.getElementById('guideLicense');
			const experienceInput = document.getElementById('experience');
			const guideLicensePreview = document.getElementById('guideLicensePreview'); // Thẻ hiển thị ảnh

			// Xử lý các trạng thái khác nhau
			if (data.status === 'REJECTED') {
				statusMessage.innerHTML = `<div class="alert alert-danger">Bị từ chối: ${data.reason}</div>`;
				guideForm.style.display = 'block';  // Hiển thị form đăng ký
				statusMessage.dataset.status = 'REJECTED';

				console.log("data " + data + " guideLicense " + data.guideLicense + " experience " + data.experience)

				// Điền sẵn thông tin đã nhập trước đó
				guideLicenseInput.value = data.guideLicense || '';
				experienceInput.value = data.experience || '';

				// Hiển thị ảnh nếu có
				if (data.guideLicenseUrl) {
					guideLicensePreview.innerHTML = `<img src="${data.guideLicenseUrl}" alt="Ảnh giấy phép" class="img-thumbnail mt-2" width="200">`;
				} else {
					guideLicensePreview.innerHTML = ''; // Xóa nếu không có ảnh
				}
			} else if (data.status === 'PENDING') {
				statusMessage.innerHTML = `<div class="alert alert-info">Yêu cầu của bạn đang chờ duyệt.</div>`;
				guideForm.style.display = 'none';  // Ẩn form đăng ký
				statusMessage.dataset.status = 'PENDING';
			} else if (data.status === 'APPROVED') {
				statusMessage.innerHTML = `<div class="alert alert-info">Yêu cầu của bạn đã được duyệt.</div>`;
				guideForm.style.display = 'none';  // Ẩn form đăng ký
				statusMessage.dataset.status = 'APPROVED';
			} else {
				// Nếu không có trạng thái hoặc không có yêu cầu nào, hiển thị form
				statusMessage.innerHTML = 'Bạn chưa đăng ký làm hướng dẫn viên.';
				guideForm.style.display = 'block';  // Hiển thị form đăng ký
			}
		})
		.catch(error => {
			console.error('Error:', error);
			const statusMessage = document.getElementById('statusMessage');
			statusMessage.innerHTML = 'Có lỗi xảy ra. Vui lòng thử lại sau.';
		});
}

function submitGuideRegister() {

	console.log("submitGuideRegister...")

	const formData = new FormData();
	formData.append("guideLicenseFile", document.getElementById("guideLicenseFile").files[0]);
	formData.append("guideLicense", document.getElementById("guideLicense").value);
	formData.append("isLocalGuide", document.getElementById("isLocalGuide").value);
	formData.append("isInternationalGuide", document.getElementById("isInternationalGuide").value);
	formData.append("experience", CKEDITOR.instances["experience"].getData());

	const statusMessage = document.getElementById('statusMessage');
	let requestMethod = 'POST';
	if (statusMessage.dataset.status === 'REJECTED') {
		requestMethod = 'PUT'
	}

	try {
		const response = fetch("/api/guide-requests/register", {
			method: requestMethod,
			body: formData
		});
		if (!response.ok) throw new Error("Gửi yêu cầu thất bại");
		alert("Đăng ký thành công!");
		document.getElementById("guideForm").reset();
	} catch (error) {
		console.error(error);
		alert("Có lỗi xảy ra, vui lòng thử lại!");
	}
}


function formatDate(dateString) {
	const date = new Date(dateString);
	return date.toLocaleDateString("vi-VN"); // Định dạng ngày tháng theo Việt Nam
}



async function updateTourStatus(id, action) {
	let url = `/api/admin/tours/${id}/${action}`;
	let options = { method: 'PUT' };

	// Nếu action là "reject", yêu cầu nhập lý do từ chối
	if (action === "reject") {
		let reason = prompt("Nhập lý do từ chối:");
		if (!reason) {
			alert("Bạn cần nhập lý do từ chối!");
			return;
		}

		// Gửi request với lý do từ chối
		options = {
			method: 'PUT',
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify({ reason })
		};
	}

	const response = await fetch(url, options);

	if (response.ok) {
		alert(`Tour ${action}d successfully!`);
		fetchPendingTours(); // Refresh danh sách mà không làm lại toàn bộ bảng
	} else {
		alert(`Failed to ${action} tour.`);
	}
}


/* guide approval */
async function fetchGuideRequests() {
	console.log("fetchGuideRequests....")
	try {
		const response = await fetch("/api/admin/guide-requests");
		if (!response.ok) throw new Error("Không thể tải danh sách đơn đăng ký");
		const guideRequests = await response.json();

		const tableBody = document.getElementById("guideRequestsTable");
		tableBody.innerHTML = ""; // Xóa nội dung cũ trước khi cập nhật

		guideRequests.forEach(request => {
			const row = document.createElement("tr");

			row.innerHTML = `
				                <td>${request.user.fullName}</td>
				                <td><img src="${request.guideLicenseUrl}" alt="Guide License" width="100" height="60" style="object-fit: cover; border-radius: 5px;"></td>
				                <td>${request.guideLicense}</td>
				                <td>${request.experience}</td>
				                <td>
				                    <a href="/users/${request.user.id}" class="btn btn-info btn-sm">Xem</a>
				                    <button class="btn btn-success btn-sm" onclick="approveGuide(${request.id})">Duyệt</button>
				                    <button class="btn btn-danger btn-sm" onclick="rejectGuide(${request.id})">Từ chối</button>
				                </td>
				            `;

			tableBody.appendChild(row);
		});
	} catch (error) {
		console.error("Lỗi:", error);
	}
}


async function approveGuide(id) {
	if (!confirm("Bạn có chắc chắn muốn duyệt đơn này?")) return;

	try {
		const response = await fetch(`/api/admin/guide-requests/${id}/approve`, { method: "PUT" });
		if (!response.ok) throw new Error("Duyệt đơn thất bại");
		alert("Đã duyệt đơn thành công!");
		fetchGuideRequests(); // Cập nhật danh sách mà không tải lại trang
	} catch (error) {
		console.error(error);
		alert("Có lỗi xảy ra, vui lòng thử lại!");
	}
}

async function rejectGuide(id) {
	const reason = prompt("Nhập lý do từ chối:");
	if (!reason) return; // Nếu không nhập, thoát

	if (!confirm("Bạn có chắc chắn muốn từ chối đơn này?")) return;

	try {
		const response = await fetch(`/api/admin/guide-requests/${id}/reject`, {
			method: "PUT",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify({ reason: reason })
		});

		if (!response.ok) throw new Error("Từ chối đơn thất bại");

		alert("Đã từ chối đơn thành công!");
		location.reload(); // Refresh danh sách
	} catch (error) {
		console.error(error);
		alert("Có lỗi xảy ra, vui lòng thử lại!");
	}
}
