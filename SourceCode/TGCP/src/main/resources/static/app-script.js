function changeLanguage(lang) {
	let url = new URL(window.location.href); // Get current URL
	url.searchParams.set('lang', lang); // Set or update 'lang' parameter
	window.location.href = url.toString(); // Redirect to new URL
}


function autoResize(textarea) {
    textarea.style.height = 'auto';  // Đặt lại chiều cao về mặc định
    textarea.style.height = (textarea.scrollHeight) + 'px';  // Cập nhật chiều cao
}

/*manage bookings*/
function fetchGuidesBookings() {		
  fetch("/api/guides/bookings")
    .then((response) => {
      if (!response.ok) {
        throw new Error("Failed to fetch bookings");
      }
      return response.json();
    })
    .then((data) => {
      const tbody = document.getElementById("bookingTableBody");
      tbody.innerHTML = "";

      data.forEach((booking) => {
        const row = document.createElement("tr");

        row.innerHTML = `
          <td>${booking.tour.title}</td>
          <td>${booking.user.fullName}</td>
          <td>${booking.createdAt}</td>
		  <td>
		    <span class="badge ${getStatusBadgeClass(booking.status)}">
		      ${booking.status}
		    </span>
		    ${
		      booking.status === "CANCELED"
		        ? `<div class="text-muted small fst-italic mt-1">Lý do: ${booking.canceledReason || "Không có lý do"}</div>`
		        : ""
		    }
		  </td>
          <td>
            <div class="d-flex gap-2">
				${booking.status === "PENDING" ? `
				  <button class="btn btn-sm btn-success" onclick="handleBookingAction('${booking.id}', 'confirm')">Confirm</button>
				` : `
				  <button class="btn btn-sm btn-success disabled-btn" disabled>Confirm</button>
				`}

            	<a class="btn btn-sm btn-primary" href="/guides/bookings/${booking.id}" data-id="${booking.id}" >View</a>
            </div>
          </td>
        `;

        tbody.appendChild(row);
      });
    })
    .catch((error) => {
      console.error("Error loading bookings:", error);
    });

  function getStatusBadgeClass(status) {
    switch (status.toLowerCase()) {
      case "confirmed": return "bg-success";
      case "pending": return "bg-warning text-dark";
      case "cancled": return "bg-danger";
      default: return "bg-secondary";
    }
  }

};

function handleBookingAction(bookingId, action) {
  console.log("handleBookingAction " + bookingId + " " + action);
  let reason = null;

  if (action === 'cancel') {
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
let currentPage = 1;
const cardsPerPage = 3; // Hiển thị tối đa 3 card mỗi lần

async function fetchBookings(page = 1) {
	currentPage = page;
    const response = await fetch('/api/bookings');
    const bookings = await response.json();
    
    // Tính toán chỉ số bắt đầu và kết thúc cho phân trang
    const startIdx = (page - 1) * cardsPerPage;
    const endIdx = startIdx + cardsPerPage;
    
    // Lấy dữ liệu bookings cho trang hiện tại
    const bookingsToShow = bookings.slice(startIdx, endIdx);
    
    const cardsBody = document.getElementById('bookings-cards-body');
    cardsBody.innerHTML = ''; // Xóa nội dung cũ
    
    bookingsToShow.forEach((booking) => {
        const col = document.createElement('div');
        col.classList.add('col-md-4', 'mb-4'); // col-md-4 tạo 3 card mỗi hàng
        
        const card = document.createElement('div');
        card.classList.add('card');
        
        const tourImage = booking.tour.images[0].url;
        
        card.innerHTML = `
            <img src="${tourImage}" class="card-img-top" alt="Tour Image">
            <div class="card-body">
                <h5 class="card-title"><strong>${booking.tour.title}</strong></h5>
                <p class="card-text">
                    Start Date:<strong> ${booking.tour.startDate}</strong><br>
                    End Date:<strong> ${booking.tour.endDate}</strong><br>
                    Creator:<strong> ${booking.tour.creator.fullName}</strong><br>
                    Booking Status:<strong> <span id="status-${booking.id}">${booking.status}</span></strong>
                </p>
				<div class="card-footer d-flex">
				    <a class="btn btn-primary flex-fill" href="/users/bookings/${booking.id}">View Details</a>
				    ${booking.status !== 'CANCELED' ? 
				        `<button class="btn btn-danger flex-fill" onclick="cancelBooking(${booking.id})">Cancel</button>` : 
				        `<button class="btn btn-danger flex-fill disabled-btn" disabled>Cancel</button>`}
				</div>
            </div>
        `;
        
        col.appendChild(card);
        cardsBody.appendChild(col);
    });
    
    // Cập nhật trạng thái các nút pagination
    updatePaginationButtons(page, bookings.length);
}

function updatePaginationButtons(page, totalItems) {
    const totalPages = Math.ceil(totalItems / cardsPerPage);
    
    // Nếu là trang đầu tiên, disable nút "Previous"
    if (page === 1) {
        document.getElementById('prev-btn').disabled = true;
    } else {
        document.getElementById('prev-btn').disabled = false;
    }

    // Nếu là trang cuối cùng, disable nút "Next"
    if (page === totalPages) {
        document.getElementById('next-btn').disabled = true;
    } else {
        document.getElementById('next-btn').disabled = false;
    }
}

function changePage(direction) {
    if (direction === 'prev') {
        currentPage -= 1;
    } else if (direction === 'next') {
        currentPage += 1;
    }
    
    fetchBookings(currentPage); // Fetch lại bookings cho trang mới
}



async function cancelBooking(bookingId) {
	if (confirm("Bạn có chắc chắn muốn hủy đặt chỗ?")) {
	    let canceledReason = prompt("Nhập lý do hủy:");
		if(canceledReason) {
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
	formData.append("experience", CKEDITOR.instances["experience"].getData());
	
	const statusMessage = document.getElementById('statusMessage');
	let requestMethod = 'POST';
	if (statusMessage.dataset.status === 'REJECTED')  {
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

/*manage tours*/
function fetchTours() {
    fetch("/api/tours") // Đổi đường dẫn API nếu cần
        .then(response => response.json())
        .then(data => {
            const tbody = document.querySelector("tbody");
            tbody.innerHTML = ""; // Xóa nội dung cũ (nếu có)

            data.forEach(tour => {
                const row = document.createElement("tr");
                row.innerHTML = `
                    <td>${tour.title}</td>
                    <td>${tour.city}</td>
                    <td>${formatDate(tour.startDate)}</td>
                    <td>${formatDate(tour.endDate)}</td>
                    <td>${tour.creator.fullName}</td>
					<td>
					    ${(tour.status !== "PENDING" && tour.status !== "REJECTED") 
					        ? getStatusDropdown(tour.id, tour.status) 
					        : `${tour.status}${tour.status === "REJECTED" ? `<br><small>(Lý do: ${tour.rejectedReason || "Không có lý do" })</small>` : ""}`
					    }
					</td>

                    <td>
                        <a href="/tours/${tour.id}" class="btn btn-info btn-sm">Chi tiết</a>
                        <a href="/tours/${tour.id}/edit" class="btn btn-primary btn-sm">Sửa</a>
                        <a href="/tours/${tour.id}/delete" class="btn btn-danger btn-sm" onclick="return confirm('Bạn có chắc chắn muốn xóa?')">Xóa</a>
                    </td>
                `;
                tbody.appendChild(row);
            });
        })
        .catch(error => console.error("Lỗi khi lấy danh sách tour:", error));
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString("vi-VN"); // Định dạng ngày tháng theo Việt Nam
}

// Hàm tạo dropdown cho trạng thái tour
function getStatusDropdown(tourId, currentStatus) {
    return `
        <select onchange="updateTourStatus(${tourId}, this.value)" class="form-select form-select-sm">
            <option value="OPEN" ${currentStatus === "OPEN" ? "selected" : ""}>OPEN</option>
            <option value="REGISTRATION_CLOSED">REGISTRATION_CLOSED</option>
            <option value="ONGOING">ONGOING</option>
            <option value="COMPLETED">COMPLETED</option>
            <option value="CANCELED">CANCELED</option>
        </select>
    `;
}

// Hàm cập nhật trạng thái tour
function updateTourStatus(tourId, newStatus) {
    fetch(`/api/tours/${tourId}/update-status`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ status: newStatus })
    })
    .then(response => {
        if (!response.ok) throw new Error("Lỗi cập nhật trạng thái");
        return response.json();
    })
    .then(() => {
        alert("Cập nhật trạng thái thành công!");
        fetchTours(); // Reload danh sách tour sau khi cập nhật
    })
    .catch(error => console.error("Lỗi khi cập nhật trạng thái tour:", error));
}



/*tour approval*/
async function fetchPendingTours() {
    console.log("fetchPendingTours....")
    const response = await fetch('/api/admin/tours/pending');
    const tours = await response.json();
    const tableBody = document.getElementById("tourTableBody");

    // Không làm lại innerHTML mà thêm các hàng mới vào
    tours.forEach(tour => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${tour.title}</td>
            <td>${tour.description}</td>
            <td>${tour.price}</td>
            <td>${tour.status}</td>
            <td>
                <button onclick="updateTourStatus(${tour.id}, 'approve')">Approve</button>
                <button onclick="updateTourStatus(${tour.id}, 'reject')">Reject</button>
            </td>
        `;
        tableBody.appendChild(row);
    });
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
		const response = await fetch(`/api/admin/guide-requests/${id}/approve`, {method: "PUT"});
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
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify({reason: reason})
		});

		if (!response.ok) throw new Error("Từ chối đơn thất bại");

		alert("Đã từ chối đơn thành công!");
		location.reload(); // Refresh danh sách
	} catch (error) {
		console.error(error);
		alert("Có lỗi xảy ra, vui lòng thử lại!");
	}
}


/*tour-create*/
function previewImages() {
	const input = document.getElementById("images");
	const files = input.files;
	// Kiểm tra số lượng file chọn vào (tối đa 5 ảnh)				

	// Sử dụng th:with để gán giá trị tour.imageUrls.size() vào biến JavaScript
	const totalFileCount = files.length;
	// Kiểm tra số lượng file chọn vào (tối đa 5 ảnh)
	if (files.length > 5) {
		alert("Bạn chỉ có thể chọn tối đa 5 ảnh.");
		// Nếu số file vượt quá 5, reset lại input
		input.value = '';
		return;
	}


	const previewContainer = document.getElementById("previewContainer");

	previewContainer.innerHTML = "";  // Xóa preview cũ

	// Render preview mới
	for (let i = 0; i < input.files.length; i++) {
		const file = input.files[i];
		const reader = new FileReader();

		reader.onload = (e) => {
			const imgWrapper = document.createElement("div");
			imgWrapper.classList.add("position-relative", "m-2");
			imgWrapper.style.transition = "opacity 0.3s";

			const img = document.createElement("img");
			img.src = e.target.result;
			img.classList.add("rounded", "shadow-sm");
			img.style.width = "150px";
			img.style.height = "150px";
			img.style.objectFit = "cover";

			// Nút xóa
			const removeBtn = document.createElement("button");
			removeBtn.textContent = "X";
			removeBtn.classList.add("btn", "btn-danger", "btn-sm", "position-absolute", "top-0", "end-0");
			removeBtn.onclick = () => removeImage(i);

			imgWrapper.appendChild(img);
			imgWrapper.appendChild(removeBtn);
			previewContainer.appendChild(imgWrapper);
		};

		reader.readAsDataURL(file);
	}

}

// Xóa ảnh (giật)
function removeImage(index) {
	const input = document.getElementById("images");
	const imageCount = document.getElementById("imageCount");

	const dataTransfer = new DataTransfer();

	for (let i = 0; i < input.files.length; i++) {
		if (i !== index) {
			dataTransfer.items.add(input.files[i]);
		}
	}

	input.files = dataTransfer.files;

	previewImages();
}

function addItinerary() {
	const itineraryId = `itinerary-${Date.now()}`;
	const itineraryDiv = document.createElement("div");
	itineraryDiv.classList.add("itinerary");
	itineraryDiv.id = itineraryId;
	itineraryDiv.innerHTML = `
			                <label>Ngày: <input type="number" min="1" name="dayNo" id="${itineraryId}-day" ></label>
			                <button onclick="removeElement('${itineraryId}')">Xóa Hành Trình</button>
			                <button type="button" onclick="addActivity('${itineraryId}')">Thêm Hoạt Động</button>
			                <div id="${itineraryId}-activities"></div>
			            `;
	document.getElementById("itineraryContainer").appendChild(itineraryDiv);
}

function addActivity(itineraryId) {
	const activityId = `${itineraryId}-activity-${Date.now()}`;
	const activityDiv = document.createElement("div");
	activityDiv.classList.add("activity");
	activityDiv.id = activityId;
	activityDiv.innerHTML = `
			                <label>Tiêu đề: <input type="text" name="title"></label>
			                <label>Mô tả: <input type="text" name="description"></label>
			                <button onclick="removeElement('${activityId}')">Xóa</button>
			            `;
	document.getElementById(`${itineraryId}-activities`).appendChild(activityDiv);
}

function removeElement(elementId) {
	const element = document.getElementById(elementId);
	if (element) {
		element.remove();
	}
}

// Hàm tạo ra HTML cho input tuổi khi checkbox được chọn
function toggleAgeInputs() {
	const ageRestricted = document.getElementById("ageRestricted").checked;
	const ageInputsContainer = document.getElementById("ageInputs");

	// Nếu checkbox được chọn, tạo HTML cho input tuổi
	if (ageRestricted) {
		ageInputsContainer.style.display = "block";
		// Chỉ tạo ra các input nếu chưa được tạo
		if (!ageInputsContainer.innerHTML.trim()) {
			const ageInputsHTML = `
			                    <div class="row">
			                        <div class="col">
			                            <label for="fromAge" class="form-label">Từ tuổi</label>
			                            <input type="number" id="fromAge" name="fromAge"  class="form-control" min="1" max="100" required>
			                        </div>
			                        <div class="col">
			                            <label for="toAge" class="form-label">Đến tuổi</label>
			                            <input type="number" id="toAge" name="toAge" class="form-control" min="1" max="100" required>
			                        </div>
			                    </div>`;
			ageInputsContainer.innerHTML = ageInputsHTML;
		}
	} else {
		// Ẩn input nếu checkbox không được chọn
		ageInputsContainer.style.display = "none";
		ageInputsContainer.innerHTML = ""; // Xóa HTML khi ẩn
	}
}

// Đảm bảo hiển thị đúng trạng thái khi tải lại trang (khi edit tour)
document.addEventListener("DOMContentLoaded", () => {
	toggleAgeInputs();
});

function submitTour() {
	event.preventDefault(); // Ngăn form gửi theo cách truyền thống

	let formData = new FormData(); // Tạo đối tượng FormData để gửi dữ liệu

	// Thu thập dữ liệu từ form
	let tour = {
		title: document.getElementById("title").value,
		city: document.getElementById("destination").value,
		category: document.getElementById("category").value,
		description: CKEDITOR.instances["description"].getData(),
		price: document.getElementById("price").value,
		maxParticipants: document.getElementById("maxParticipants").value,
		startDate: document.getElementById("startDate").value,
		endDate: document.getElementById("endDate").value,
		ageRestricted: document.getElementById("ageRestricted").checked,
		fromAge: document.getElementById("fromAge") ? document.getElementById("fromAge").value : null,
		toAge: document.getElementById("toAge") ? document.getElementById("toAge").value : null,
		// Lấy danh sách lịch trình (itineraries)
		itineraries: Array.from(document.querySelectorAll(".itinerary")).map(itinerary => {
			let itineraryId = itinerary.id;
			return {
				dayNo: parseInt(itinerary.querySelector(`[id="${itineraryId}-day"]`).value) || 1,
				activities: Array.from(itinerary.querySelectorAll(".activity")).map(activity => ({
					title: activity.querySelector(`[name="title"]`).value,
					description: activity.querySelector(`[name="description"]`).value
				}))
			};
		})
	};

	// Thêm JSON vào FormData
	formData.append("tour", new Blob([JSON.stringify(tour)], {type: "application/json"}));

	// Lấy các file ảnh và thêm vào FormData
	let filesInput = document.getElementById("images");
	for (let i = 0; i < filesInput.files.length; i++) {
		formData.append("files", filesInput.files[i]);
	}

	// Gửi request đến API REST "/api/tours/new"
	fetch("/api/tours", {
		method: "POST",
		body: formData
	})
		.then(response => response.json())
		.then(data => {
			alert("Tour đã được tạo thành công!");
			window.location.href = "/tours/" + data.tourId; // Điều hướng sau khi thành công
		})
		.catch(error => {
			console.error("Lỗi:", error);
			alert("Có lỗi xảy ra khi tạo Tour.");
		});
};
