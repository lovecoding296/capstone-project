function changeLanguage(lang) {
	let url = new URL(window.location.href); // Get current URL
	url.searchParams.set('lang', lang); // Set or update 'lang' parameter
	window.location.href = url.toString(); // Redirect to new URL
}


function autoResize(textarea) {
    textarea.style.height = 'auto';  // Đặt lại chiều cao về mặc định
    textarea.style.height = (textarea.scrollHeight) + 'px';  // Cập nhật chiều cao
}

/* chat message */


let currentReceiverId = null;

updateUnreadMessageCount()

function updateUnreadMessageCount() {
  fetch('/api/chat/unread-count')
    .then(response => response.json())
    .then(count => {
      const badge = document.getElementById('messageCountBadge');
      if (count > 0) {
        badge.textContent = count;
        badge.style.display = 'inline-block';
      } else {
        badge.style.display = 'none';
      }
    })
    .catch(err => {
      console.error("Lỗi khi lấy số tin nhắn chưa đọc:", err);
    });
}


function sendChatMessage() {
	console.log("sendChatMessage currentReceiverId " +  currentReceiverId)
	
	const chatForm = document.getElementById('chatForm');
	const chatInput = document.getElementById('chatInput');
	const chatMessages = document.getElementById('chatMessages');
	
	
	const message = chatInput.value.trim();
	if (!message) return;

	const payload = {
		receiver: { id: currentReceiverId },
		content: message
	};

	fetch('/api/chat/send', {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify(payload)
	})
		.then(res => res.json())
		.then(data => {
			appendMessage(data, true);
			chatInput.value = '';
		});
}


  // Hiển thị tin nhắn
  function appendMessage(msg, isOwn) {
	console.log("appendMessage")
    const el = document.createElement('div');
    el.className = `mb-2 ${isOwn ? 'text-end' : 'text-start'}`;
    el.innerHTML = `<span class="badge ${isOwn ? 'bg-primary' : 'bg-secondary'}">${msg.content}</span>`;
    chatMessages.appendChild(el);
    chatMessages.scrollTop = chatMessages.scrollHeight;
  }

  // Lấy cuộc hội thoại hiện có
  function loadConversation(receiverId) {
	console.log("loadConversation")
	fetch(`/api/chat/conversation?user2=${receiverId}`)
	  .then(res => res.json())
	  .then(data => {
	    const currentUserId = data.currentUserId;
	    const messages = data.messages;

	    chatMessages.innerHTML = '';
	    messages.forEach(msg => {
	      appendMessage(msg, msg.senderId === currentUserId);
	    });
	  });

  }

  function closeChat() {
	console.log("closeChat")
    document.getElementById('chatBox').style.display = 'none';
  }
  
  
function createChatBox() {
  console.log("createChatBox currentReceiverId " +  currentReceiverId)	
	
	
  if (document.getElementById('chatBox')) return; // Tránh tạo lại

  const chatBox = document.createElement('div');
  chatBox.id = 'chatBox';
  chatBox.className = 'shadow border rounded bg-light';
  chatBox.style = `
	  width: 350px;
	  height: 500px;
	  position: fixed;
	  bottom: 20px;
	  right: 20px;
	  z-index: 9999;
	  display: none;
	  flex-direction: column;
	  padding: 16px;
	`;

  chatBox.innerHTML = `
    <div class="d-flex justify-content-between align-items-center mb-2">
      <h5 id="chatBoxPartnerName" class="mb-0">Chat với </h5>
      <button class="btn btn-sm btn-outline-danger" onclick="closeChat()">Đóng</button>
    </div>

    <div id="chatMessages" class="flex-grow-1 overflow-auto mb-3 border p-2 rounded bg-white" style="max-height: 350px;">
    </div>

    <form id="chatForm" class="d-flex gap-2">
      <input type="text" id="chatInput" class="form-control" placeholder="Nhập tin nhắn..." required>
      <button type="button" onclick="sendChatMessage()" class="btn btn-primary">Gửi</button>
    </form>
  `;

  document.body.appendChild(chatBox);

}

function openChatWithUser(partnerId, partnerName) {
  console.log("openChatWithUser")
  currentReceiverId = partnerId;
	
  createChatBox(); // đảm bảo chatBox đã được tạo

  const chatBox = document.getElementById('chatBox');
  const chatBoxPartnerName = document.getElementById('chatBoxPartnerName');
  console.log("partnerName " + partnerName)
  console.log("chatBoxPartnerName " + chatBoxPartnerName.innerText)
  chatBoxPartnerName.innerText = 'Chat với ' + partnerName;
  
  chatBox.dataset.partnerId = partnerId;

  fetch(`/api/chat/conversation/${partnerId}`)
    .then(res => res.json())
    .then(data => {
	  const messages = data.messages;
	  const currentUserId = data.currentUserId;
	  
      console.log("Server trả về:", messages); // 👈 kiểm tra ở đây

      if (!Array.isArray(messages)) {
        console.error("Dữ liệu không phải là mảng!");
        return;
      }

      const chatMessages = document.getElementById('chatMessages');
      chatMessages.innerHTML = '';

      messages.forEach(msg => {
        const isOwnMessage = msg.sender.id === currentUserId;
        appendMessageToChat(msg, isOwnMessage);
      });
	  
      

      chatBox.style.display = 'flex';
      chatMessages.scrollTop = chatMessages.scrollHeight;
    });

	
	markMessagesAsRead(partnerId);
}

function markMessagesAsRead(partnerId) {

	const payload = {
		sender: { id: partnerId },
	};

	fetch(`/api/chat/mark-read`, {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify(payload)
	}).then(response => {
		if (!response.ok) throw new Error("Không thể cập nhật trạng thái đã đọc.");
		console.log("Đã đánh dấu tin nhắn là đã đọc");
	}).catch(error => {
		console.error(error);
	});
	
	updateUnreadMessageCount()
}


function appendMessageToChat(msg, isOwnMessage) {
  const chatMessages = document.getElementById('chatMessages');

  const msgDiv = document.createElement('div');
  msgDiv.className = `mb-2 ${isOwnMessage ? 'text-end' : 'text-start'}`;
  msgDiv.innerHTML = `
    <div class="d-inline-block p-2 rounded ${isOwnMessage ? 'bg-primary text-white' : 'bg-secondary text-white'}">
      ${msg.content}
    </div>
    <div class="small text-muted">${formatTimestamp(msg.timestamp)}</div>
  `;
  chatMessages.appendChild(msgDiv);
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
        item.textContent = `Khách: ${b.customer.fullName} | Số tiền: ${b.totalPrice} | Ngày: ${b.endDate}`;
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
        onDayCreate: function (dObj, dStr, fp, dayElem) {
          const date = fp.formatDate(dayElem.dateObj, "Y-m-d");
          if (selectedDates.includes(date)) {
			console.log("add busy-date " + date)
            dayElem.classList.add("busy-date");
          }

          // Toggle chọn ngày
          dayElem.addEventListener("click", function () {
            if (selectedDates.includes(date)) {
              selectedDates = selectedDates.filter(d => d !== date);
            } else {
              selectedDates.push(date);
            }
            fp.setDate(selectedDates, false); // Cập nhật lại các ngày chọn
            fp.redraw(); // Cập nhật giao diện
          });
        },
        onReady: function () {
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
    const response = await fetch('/api/bookings');
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
		  <td>${booking.guide.fullName}</td>
		  <td>
		    <span class="badge ${getStatusBadgeClass(booking.status)}">
		      ${booking.status}
		    </span>
		    ${booking.status === "CANCELED"
				? `<div class="text-muted small fst-italic mt-1">Lý do: ${booking.canceledReason || "Không có lý do"}</div>`
				: ""
			}
		  </td>
	      <td>
	        <div class="d-flex gap-2">
			<a class="btn btn-sm btn-primary" href="/guides/bookings/${booking.id}" data-id="${booking.id}" >View</a>
			${booking.status === "PENDING" ? `
			  <button class="btn btn-sm btn-success" onclick="handleBookingAction('${booking.id}', 'confirm')">Confirm</button>
			` : `
			  <button class="btn btn-sm btn-success disabled-btn" disabled>Confirm</button>
			`}
			
			${booking.status === "CONFIRMED" ? `
		      <button class="btn btn-sm btn-warning" onclick="handleBookingAction('${booking.id}', 'complete')">Complete</button>
		    ` : `
		      <button class="btn btn-sm btn-warning disabled-btn" disabled>Complete</button>
		    `}
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
    case "cancled": return "bg-danger";
	case "completed": return "bg-info text-white";
    default: return "bg-secondary";
  }
}

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
		    ${booking.status === "CANCELED"
				? `<div class="text-muted small fst-italic mt-1">Lý do: ${booking.canceledReason || "Không có lý do"}</div>`
				: ""
			}
		  </td>
	      <td>
	        <div class="d-flex gap-2">
				<a class="btn btn-sm btn-primary" href="/users/bookings/${booking.id}" data-id="${booking.id}" >View</a>
				${booking.status === "PENDING" ? `
				  <button class="btn btn-sm btn-success" onclick="handleBookingAction('${booking.id}', 'cancel')">Cancel</button>
				` : `
				  <button class="btn btn-sm btn-success disabled-btn" disabled>Cancel</button>
				`}
	
	        	
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
