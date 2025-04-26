function changeLanguage(lang) {
	let url = new URL(window.location.href); // Get current URL
	url.searchParams.set('lang', lang); // Set or update 'lang' parameter
	window.location.href = url.toString(); // Redirect to new URL
}


function autoResize(textarea) {
	textarea.style.height = 'auto';  // Đặt lại chiều cao về mặc định
	textarea.style.height = (textarea.scrollHeight) + 'px';  // Cập nhật chiều cao
}

/* manage posts */

const categoryDisplayNames = {
  EXPERIENCE_SHARING: "Experience Sharing",
  TRAVEL_QNA: "Travel Q&A",
  HOT_DESTINATIONS: "Hot Destinations",
  FOOD_SPECIALTIES: "Food Specialties",
  CULTURE_AND_PEOPLE: "Culture and People",
  SERVICE_REVIEWS: "Service Reviews",
  TICKET_BOOKING_TIPS: "Ticket Booking Tips",
  TRAVEL_DIARY: "Travel Diary"
};


let postsPage = {
	currentPage: 1,
	itemsPerPage: 10
}

function changePostsPage(direction) {
	if (direction === 'prev') {
		postsPage.currentPage -= 1;
	} else if (direction === 'next') {
		postsPage.currentPage += 1;
	}

	fetchPosts(postsPage.currentPage);
}

function fetchPosts(page = 1) {
	postsPage.currentPage = page;
	
	const title = document.getElementById('title').value;
    const category = document.getElementById('category').value;
    const author = document.getElementById('author').value;


    // Tạo URL với các tham số tìm kiếm
    let url = '/api/posts?';
    if (title) url += `title=${title}&`;
    if (category) url += `category=${category}&`;
    if (author) url += `role=${author}&`;
	url += `page=${postsPage.currentPage - 1}&`;
	url += `size=${postsPage.itemsPerPage}`
	
	fetch(url)
		.then(response => {
			if (!response.ok) {
				throw new Error(`HTTP error! status: ${response.status}`);
			}
			return response.json(); // dữ liệu từ Page<Post>
		})
		.then(data => {
			const posts = data.content; // danh sách bài viết thực tế
			const tableBody = document.getElementById('postsTableBody');
			tableBody.innerHTML = ''; // Xóa dữ liệu cũ

			posts.forEach(post => {
				let categoryName = categoryDisplayNames[post.category];
				const row = document.createElement('tr');
				row.innerHTML = `
					<td>${post.title}</td>
					<td>${categoryName}</td>
					<td>${post.author.fullName}</td>
					<td>
						<a class="button btn btn-sm btn-primary" href="/posts/${post.id}">View</a>
						<a class="button btn btn-sm btn-success" href="/posts/${post.id}/edit">Edit</a>
						<button class="btn btn-sm btn-danger" onclick="deletePost(${post.id})">Delete</button>
					</td>
				`;
				tableBody.appendChild(row);
			});

			// Cập nhật phân trang
			updatePaginationButtons(data.number + 1, data.totalElements, data.size);
		})
		.catch(error => {
			console.error('Lỗi khi tải danh sách bài viết:', error);
			const tableBody = document.getElementById('postsTableBody');
			tableBody.innerHTML = `<tr><td colspan="4">Không thể tải danh sách bài viết.</td></tr>`;
		});

	
}

function deletePost(id) {
    if (confirm("Are you sure you want to delete this post?")) {
      fetch(`/api/posts/${id}/delete`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
        }
      })
      .then(response => {
        if (response.ok) {
          alert("Post deleted successfully.");
          fetchPosts(postsPage.currentPage)
        } else {
          return response.text().then(text => { throw new Error(text); });
        }
      })
      .catch(error => {
        console.error("Error deleting post:", error);
        alert("Failed to delete post.");
      });
    }
  }

/* manage users */

let usersPage = {
	currentPage: 1,
	itemsPerPage: 10
}

function changeUsersPage(direction) {
	if (direction === 'prev') {
		usersPage.currentPage -= 1;
	} else if (direction === 'next') {
		usersPage.currentPage += 1;
	}

	fetchUsers(usersPage.currentPage);
}

function fetchUsers(page = 1) {
	
	usersPage.currentPage = page;
	
	const email = document.getElementById('email').value;
    const fullName = document.getElementById('fullName').value;
    const role = document.getElementById('role').value;
    const kycApproved = document.getElementById('kycApproved').value;
    const verified = document.getElementById('verified').value;
	const enabled = document.getElementById('enabled').value;

    // Tạo URL với các tham số tìm kiếm
    let url = '/api/users?';
    if (email) url += `email=${email}&`;
    if (fullName) url += `fullName=${fullName}&`;
    if (role) url += `role=${role}&`;
    if (kycApproved !== "") url += `kycApproved=${kycApproved}&`;
    if (verified !== "") url += `verified=${verified}&`;
	if (enabled !== "") url += `enabled=${enabled}&`;
	url += `page=${usersPage.currentPage - 1}&`;
	url += `size=${usersPage.itemsPerPage}`

	
	fetch(url)
		.then(response => {
			if (!response.ok) {
				throw new Error(`HTTP error! status: ${response.status}`);
			}
			return response.json();
		})
		.then(data => {
			const tableBody = document.getElementById('usersTable');
			tableBody.innerHTML = ''; // Xóa dữ liệu cũ

			const users = data.content

			users.forEach(user => {
				
				// Tạo nút hành động
				const actions = [];
				
				if (user.enabled) {
					actions.push(`<button class="btn btn-sm btn-danger" onclick="performUserAction(${user.id}, 'disable')">Disable</button>`);
				} else {
					actions.push(`<button class="btn btn-sm btn-success" onclick="performUserAction(${user.id}, 'enable')">Enable</button>`);
				}

				if (!user.kycApproved) {
					actions.push(`<button class="btn btn-sm btn-primary" onclick="performUserAction(${user.id}, 'approve-kyc')">Approve KYC</button>`);
				}

				
				const row = document.createElement('tr');
				row.innerHTML = `
					<td>${user.fullName}</td>
					<td>${user.email}</td>
					<td>${user.role}</td>
					<td><b>KYC: </b>${user.kycApproved}</td>
					<td><b>Email Verified: </b>${user.verified}</td>
					<td><b>Enabled: </b>${user.enabled}</td>
					<td>${actions.join(' ')}</td>
				`;
				tableBody.appendChild(row);
			});
			
			updatePaginationButtons(data.number + 1, data.totalElements, data.size);
		})
		.catch(error => {
			console.error('Lỗi khi tải danh sách người dùng:', error);
			const tableBody = document.querySelector('#usersTable tbody');
			tableBody.innerHTML = `<tr><td colspan="3">Không thể tải danh sách người dùng.</td></tr>`;
		});
}


function performUserAction(userId, action) {
	let url = '';
	let confirmMessage = '';
	let successMessage = '';
	
	url = `/api/users/${userId}/${action}`;

	switch (action) {
		case 'enable':
			confirmMessage = 'Bạn có chắc chắn muốn ENABLE người dùng này?';
			successMessage = 'Đã enable người dùng.';
			break;
		case 'disable':
			confirmMessage = 'Bạn có chắc chắn muốn DISABLE người dùng này?';
			successMessage = 'Đã disable người dùng.';
			break;
		case 'approve-kyc':
			confirmMessage = 'Bạn có chắc chắn muốn duyệt KYC cho người dùng này?';
			successMessage = 'Đã duyệt KYC.';
			break;
		default:
			console.error('Hành động không hợp lệ:', action);
			return;
	}

	if (!confirm(confirmMessage)) return;

	fetch(url, { method: 'PUT' })
		.then(response => {
			if (!response.ok) throw new Error(`${action} failed`);
			return response.text();
		})
		.then(() => {
			alert(successMessage);
			fetchUsers(usersPage.currentPage);
		})
		.catch(error => {
			console.error(error);
			alert(`Có lỗi khi thực hiện hành động: ${action}`);
		});
}



/* manage reports */

let reportsPage = {
	currentPage: 1,
	itemsPerPage: 10
}

function changeReportPage(direction) {
	if (direction === 'prev') {
		reportsPage.currentPage -= 1;
	} else if (direction === 'next') {
		reportsPage.currentPage += 1;
	}

	fetchReports(reportsPage.currentPage);
}

async function fetchReports(page = 1) {
	
	reportsPage.currentPage = page;
	
	const reporter = document.getElementById('reporter').value;
	const reason = document.getElementById('reason').value;
	const resolved = document.getElementById('resolved').value;
	const reportType = document.getElementById('reportType').value;


	// Tạo URL với các tham số tìm kiếm
	let url = '/api/reports?';
	if (reporter) url += `reporter=${reporter}&`;
	if (reason) url += `reason=${reason}&`;
	if (resolved) url += `resolved=${resolved}&`;
	if (reportType) url += `reportType=${reportType}&`;
	
	url += `page=${reportsPage.currentPage - 1}&`;
	url += `size=${reportsPage.itemsPerPage}`

	// Xóa dấu "&" thừa ở cuối URL nếu có
	
	const response = await fetch(url);
	const data = await response.json();


	const tbody = document.getElementById("reportTableBody");
	tbody.innerHTML = "";
	
	const reports = data.content;

	reports.forEach((report) => {
		const row = document.createElement("tr");

		row.innerHTML = `
		  <td>${formatDate(report.reportTime)}</td>
		  <td><a href="/users/${report.reporter.id}">${report.reporter.fullName}</td>
		  <td>
		      <a href="${report.reportType === 'USER' ? `/users/${report.targetId}` : `/posts/${report.targetId}`}">
		        ${report.reportType} ${report.targetName}
		      </a>
		  </td>

		  <td>${report.reason}</td>
		  <td>
		  	<div>${report.resolved ? `<span class="badge bg-success text-white"> Resolved` :  `<span class="badge bg-warning text-dark"> Unresolved`}</div>
		  	${report.resolved ? `<div style="font-size: 0.9em; color: #555;">Admin Feedback: ${report.adminFeedBack || "(No feedback)"}</div>` : ""}
		  </td>
		  <td>${!report.resolved ? `
	        <button class="btn btn-sm btn-primary" onclick="resolveReport(${report.id})">Resolve</button>
	      ` : `<button class="btn btn-sm btn-warning disabled-btn" disabled>Resolve</button>`}
		  </td>
	    `;

		tbody.appendChild(row);
	});

	// Cập nhật trạng thái các nút pagination
	updatePaginationButtons(data.number + 1, data.totalElements, data.size);
}

function resolveReport(reportId) {
	
	let adminFeedBack = prompt("Enter feedback:");
	
	if (!adminFeedBack) {
		alert("You need to enter a feedback!");
		return;
	}
	
	let reportData = {
			id: reportId,
			adminFeedBack: adminFeedBack,
		}

	fetch(`/api/reports/${reportData.id}`, {
		method: "PUT",
		headers: {
			"Content-Type": "application/json",
		},
		body: JSON.stringify(reportData)
	})
		.then(async (res) => {
			const text = await res.text(); // nhận dữ liệu dưới dạng chuỗi

			if (!res.ok) {
				throw new Error(text || "Đã xảy ra lỗi");
			}

			return text; // trả về chuỗi phản hồi
		})
		.then((result) => {
			alert(result || `${action} thành công`);
			fetchReports(reportsPage.currentPage);
		})
		.catch((err) => {
			console.error(err);
			alert(err.message || "Đã xảy ra lỗi");
		});

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
						label: 'Monthly income',
						data: Object.values(data.monthlyIncome),
						backgroundColor: 'rgba(75, 192, 192, 0.5)'
					}]
				}
			});

			// Hiển thị danh sách đơn
/*			const list = document.getElementById("completedBookingsList");
			data.bookings.forEach(b => {
				const item = document.createElement("li");
				item.className = "booking-item";
				item.textContent = `Customer: ${b.customer.fullName} | amount: ${b.totalPrice} | date: ${b.endDate}`;
				list.appendChild(item);
			});*/
			
			
			const ctx2 = document.getElementById('bookingsChart').getContext('2d');

				const labels = data.bookings.map(b => b.endDate);
				const revenues = data.bookings.map(b => b.totalPrice);

				new Chart(ctx2, {
					type: 'bar',
					data: {
						labels: labels,
						datasets: [{
							label: 'Total Price',
							data: revenues,
							backgroundColor: 'rgba(54, 162, 235, 0.6)',
							borderColor: 'rgba(54, 162, 235, 1)',
							borderWidth: 1
						}]
					},
					options: {
						scales: {
							y: {
								beginAtZero: true,
								title: {
									display: true,
									text: 'Amount (VND)'
								}
							},
							x: {
								title: {
									display: true,
									text: 'Date'
								}
							}
						}
					}
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

let guideBookingsPage = {
	currentPage: 1,
	itemsPerPage: 10
};


async function fetchGuideBookings(page = 1) {
	guideBookingsPage.currentPage = page;
	
	const destination = document.getElementById('destination').value;
	const startDate = document.getElementById('startDate').value;
	const endDate = document.getElementById('endDate').value;
	const user = document.getElementById('user').value;
	const bookingStatus = document.getElementById('bookingStatus').value;
	
	
		

	// Tạo URL với các tham số tìm kiếm
	let url = '/api/guides/bookings?';
	if (destination) url += `destination=${destination}&`;
	if (startDate) url += `startDate=${startDate}&`;
	if (endDate) url += `endDate=${endDate}&`;
	if (user) url += `user=${user}&`;
	if (bookingStatus) url += `status=${bookingStatus}&`;
	
	url += `page=${postsPage.currentPage - 1}&`;
	url += `size=${postsPage.itemsPerPage}`

	
	const response = await fetch(url);
	const data = await response.json();

	const bookings = data.content;

	const tbody = document.getElementById("bookingTableBody");
	tbody.innerHTML = "";

	bookings.forEach((booking) => {
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
	updatePaginationButtons(data.number + 1, data.totalElements, data.size);
}

function updatePaginationButtons(page, totalItems, itemsPerPage) {
	
	
	
	const totalPages = Math.ceil(totalItems / itemsPerPage);

	const prevBtn = document.getElementById('prev-btn');
	const nextBtn = document.getElementById('next-btn');
	
	const paginationDiv = document.getElementById('pagination');
	
	
	
	
	if (totalPages <= 0) {
		prevBtn.disabled = true;
		prevBtn.disabled = false;
		prevBtn.classList.add('disabled-btn');
		nextBtn.classList.add('disabled-btn');
		paginationDiv.style.display = 'flex';
		return;
	}
	
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
	
	paginationDiv.style.display = 'flex';
	
}

function changeGuideBookingsPage(direction) {
	if (direction === 'prev') {
		guideBookingsPage.currentPage -= 1;
	} else if (direction === 'next') {
		guideBookingsPage.currentPage += 1;
	}

	fetchGuideBookings(guideBookingsPage.currentPage); // Fetch lại bookings cho trang mới
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
			fetchGuideBookings(); // hoặc update riêng dòng nếu bạn muốn tối ưu
		})
		.catch((err) => {
			console.error(err);
			alert(err.message || "Đã xảy ra lỗi");
		});
}



/*booking history*/
let historyBookingsPage = {
	currentPage: 1,
	itemsPerPage: 10
};


async function fetchBookings(page = 1) {
	
	historyBookingsPage.currentPage = page;
	console.log("historyBookingsPage")
	
	const destination = document.getElementById('destination').value;
	const startDate = document.getElementById('startDate').value;
	const endDate = document.getElementById('endDate').value;
	const guide = document.getElementById('guide').value;
	const bookingStatus = document.getElementById('bookingStatus').value;

	// Tạo URL với các tham số tìm kiếm
	let url = '/api/bookings?';
	if (destination) url += `destination=${destination}&`;
	if (startDate) url += `startDate=${startDate}&`;
	if (endDate) url += `endDate=${endDate}&`;
	if (guide) url += `guide=${guide}&`;
	if (bookingStatus) url += `status=${bookingStatus}&`;
	
	url += `page=${postsPage.currentPage - 1}&`;
	url += `size=${postsPage.itemsPerPage}`

	
	const response = await fetch(url);
	const data = await response.json();
	const bookings = data.content;

	const tbody = document.getElementById("bookingTableBody");
	tbody.innerHTML = "";

	bookings.forEach((booking) => {
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
	updatePaginationButtons(data.number + 1, data.totalElements, data.size);
}


function changeHistoryBookingPage(direction) {
	if (direction === 'prev') {
		historyBookingsPage.currentPage -= 1;
	} else if (direction === 'next') {
		historyBookingsPage.currentPage += 1;
	}

	fetchBookings(historyBookingsPage.currentPage); // Fetch lại bookings cho trang mới
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
			fetchBookings(historyBookingsPage.currentPage);
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
	fetch('/api/guide-requests/status')
		.then(response => {
			if (!response.ok) {
				if (response.status === 404) {
					// Trường hợp không có yêu cầu nào (coi như là chưa đăng ký)
					return { status: 'NONE' };
				}
				throw new Error(`HTTP error! status: ${response.status}`);
			}
			return response.json();
		})
		.then(data => {
			const statusMessage = document.getElementById('statusMessage');
			const guideForm = document.getElementById('guideForm');
			const guideLicenseInput = document.getElementById('guideLicense');
			const experienceInput = document.getElementById('experience');
			const guideLicensePreview = document.getElementById('guideLicensePreview');

			if (data.status === 'REJECTED') {
				statusMessage.innerHTML = `<div class="alert alert-danger">Bị từ chối: ${data.reason}</div>`;
				guideForm.style.display = 'block';
				statusMessage.dataset.status = 'REJECTED';

				guideLicenseInput.value = data.guideLicense || '';
				experienceInput.value = data.experience || '';

				if (data.guideLicenseUrl) {
					guideLicensePreview.innerHTML = `<img src="${data.guideLicenseUrl}" alt="Ảnh giấy phép" class="img-thumbnail mt-2" width="200">`;
				} else {
					guideLicensePreview.innerHTML = '';
				}
			} else if (data.status === 'PENDING') {
				statusMessage.innerHTML = `<div class="alert alert-info">Yêu cầu của bạn đang chờ duyệt.</div>`;
				guideForm.style.display = 'none';
				statusMessage.dataset.status = 'PENDING';
			} else if (data.status === 'APPROVED') {
				statusMessage.innerHTML = `<div class="alert alert-info">Yêu cầu của bạn đã được duyệt.</div>`;
				guideForm.style.display = 'none';
				statusMessage.dataset.status = 'APPROVED';
			} else {
				statusMessage.innerHTML = 'You are not registered as a guide.';
				guideForm.style.display = 'block';
			}
		})
		.catch(error => {
			console.error('Error:', error);
			const statusMessage = document.getElementById('statusMessage');
			statusMessage.innerHTML = 'Có lỗi xảy ra. Vui lòng thử lại sau.';
		});
}

async function submitGuideRegister() {

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
		// Đợi kết quả của fetch
		const response = await fetch("/api/guide-requests/register", {
			method: requestMethod,
			body: formData
		});

		// Kiểm tra response status
		if (!response.ok) {
			// Nếu có lỗi từ server, lấy message chi tiết từ response
			const errorData = await response.json();  // giả sử API trả về lỗi dưới dạng JSON
			throw new Error(errorData.message || "Gửi yêu cầu thất bại");
		}

		alert("Đăng ký thành công!");
		document.getElementById("guideForm").reset();
	} catch (error) {
		console.error(error);
		alert(`Có lỗi xảy ra: ${error.message}`);
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

let guideRequestsPage = {
	currentPage: 1,
	itemsPerPage: 10
}


/* manage guide requests */
async function fetchGuideRequests(page = 1) {
	console.log("fetchGuideRequests....")
	try {
		const response = await fetch("/api/admin/guide-requests");
		if (!response.ok) throw new Error("Không thể tải danh sách đơn đăng ký");
		const guideRequests = await response.json();
		
		// Tính toán chỉ số bắt đầu và kết thúc cho phân trang
		const startIdx = (page - 1) * guideRequestsPage.itemsPerPage;
		const endIdx = startIdx + guideRequestsPage.itemsPerPage;

		// Lấy dữ liệu bookings cho trang hiện tại
		const guideRequestsToShow = guideRequests.slice(startIdx, endIdx);

		const tableBody = document.getElementById("guideRequestsTable");
		tableBody.innerHTML = ""; // Xóa nội dung cũ trước khi cập nhật

		guideRequestsToShow.forEach(request => {
			const row = document.createElement("tr");
			row.innerHTML = `
				                <td>${request.user.fullName}</td>
				                <td><img src="${request.guideLicenseUrl}" alt="Guide License" width="100" height="60" style="object-fit: cover; border-radius: 5px;"></td>
				                <td>${request.guideLicense}</td>
				                <td>${request.experience}</td>
				                <td>
				                    <a href="/users/${request.user.id}" class="btn btn-info btn-sm">View</a>
				                    <button class="btn btn-success btn-sm" onclick="approveGuide(${request.id})">Approve</button>
				                    <button class="btn btn-danger btn-sm" onclick="rejectGuide(${request.id})">Reject</button>
				                </td>
				            `;

			tableBody.appendChild(row);
		});
		
		updatePaginationButtons(page, guideRequests.length, guideRequestsPage.itemsPerPage);

	} catch (error) {
		console.error("Lỗi:", error);
	}
}

function changeGuideRequestsPage(direction) {
	if (direction === 'prev') {
		guideRequestsPage.currentPage -= 1;
	} else if (direction === 'next') {
		guideRequestsPage.currentPage += 1;
	}

	fetchGuideRequests(guideRequestsPage.currentPage); // Fetch lại bookings cho trang mới
}


async function approveGuide(id) {
	if (!confirm("Bạn có chắc chắn muốn duyệt đơn này?")) return;

	try {
		const response = await fetch(`/api/admin/guide-requests/${id}/approve`, { method: "PUT" });
		if (!response.ok) throw new Error("Duyệt đơn thất bại");
		alert("Đã duyệt đơn thành công!");
		fetchGuideRequests(guideRequestsPage.currentPage); // Cập nhật danh sách mà không tải lại trang
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
		fetchGuideRequests(guideRequestsPage.currentPage);
	} catch (error) {
		console.error(error);
		alert("Có lỗi xảy ra, vui lòng thử lại!");
	}
}
