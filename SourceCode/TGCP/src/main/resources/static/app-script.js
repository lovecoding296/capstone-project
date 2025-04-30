const cityDisplayNames = {
  AN_GIANG: "An Giang",
  BAC_GIANG: "Bac Giang",
  BAC_KAN: "Bac Kan",
  BAC_LIEU: "Bac Lieu",
  BAC_NINH: "Bac Ninh",
  BEN_TRE: "Ben Tre",
  BINH_DINH: "Binh Dinh",
  BINH_DUONG: "Binh Duong",
  BINH_PHUOC: "Binh Phuoc",
  BINH_THUAN: "Binh Thuan",
  CA_MAU: "Ca Mau",
  CAN_THO: "Can Tho",
  CAO_BANG: "Cao Bang",
  DA_NANG: "Da Nang",
  DAK_LAK: "Dak Lak",
  DAK_NONG: "Dak Nong",
  DIEN_BIEN: "Dien Bien",
  DONG_NAI: "Dong Nai",
  DONG_THAP: "Dong Thap",
  GIA_LAI: "Gia Lai",
  HA_GIANG: "Ha Giang",
  HA_NAM: "Ha Nam",
  HA_NOI: "Hanoi",
  HA_TINH: "Ha Tinh",
  HAI_DUONG: "Hai Duong",
  HAI_PHONG: "Hai Phong",
  HAU_GIANG: "Hau Giang",
  HOA_BINH: "Hoa Binh",
  HUNG_YEN: "Hung Yen",
  KHANH_HOA: "Khanh Hoa",
  KIEN_GIANG: "Kien Giang",
  KON_TUM: "Kon Tum",
  LAI_CHAU: "Lai Chau",
  LANG_SON: "Lang Son",
  LAO_CAI: "Lao Cai",
  LAM_DONG: "Lam Dong",
  LONG_AN: "Long An",
  NAM_DINH: "Nam Dinh",
  NGHE_AN: "Nghe An",
  NINH_BINH: "Ninh Binh",
  NINH_THUAN: "Ninh Thuan",
  PHU_THO: "Phu Tho",
  PHU_YEN: "Phu Yen",
  QUANG_BINH: "Quang Binh",
  QUANG_NAM: "Quang Nam",
  QUANG_NGAI: "Quang Ngai",
  QUANG_NINH: "Quang Ninh",
  QUANG_TRI: "Quang Tri",
  SOC_TRANG: "Soc Trang",
  SON_LA: "Son La",
  TAY_NINH: "Tay Ninh",
  THAI_BINH: "Thai Binh",
  THAI_NGUYEN: "Thai Nguyen",
  THANH_HOA: "Thanh Hoa",
  THUA_THIEN_HUE: "Thua Thien Hue",
  TIEN_GIANG: "Tien Giang",
  HO_CHI_MINH_CITY: "Ho Chi Minh City",
  TRA_VINH: "Tra Vinh",
  TUYEN_QUANG: "Tuyen Quang",
  VINH_LONG: "Vinh Long",
  VINH_PHUC: "Vinh Phuc",
  YEN_BAI: "Yen Bai"
};

const groupSizeCategoryDisplayNames = {
    UNDER_5: "Under 5",
    FROM_5_TO_10: "From 5 to 10",
    OVER_10: "Over 10"
};

const serviceTypeDisplayNames = {
    CITY_TOUR: "City Tour",
    NATURE_TOUR: "Nature Tour",
    TRANSLATOR: "Translator",
    PHOTOGRAPHER: "Photographer"
};


const languages = [
	"Afrikaans", "Albanian", "Amharic", "Arabic", "Armenian", "Azerbaijani",
	"Basque", "Belarusian", "Bengali", "Bosnian", "Bulgarian", "Burmese",
	"Catalan", "Chinese", "Corsican", "Croatian", "Czech", "Danish", "Dutch",
	"English", "Esperanto", "Estonian", "Finnish", "French", "Frisian",
	"Galician", "Georgian", "German", "Greek", "Gujarati", "Haitian_Creole",
	"Hebrew", "Hindi", "Hungarian", "Icelandic", "Indonesian", "Irish",
	"Italian", "Japanese", "Javanese", "Kannada", "Kazakh", "Khmer", "Korean",
	"Kurdish", "Kyrgyz", "Lao", "Latin", "Latvian", "Lithuanian", "Luxembourgish",
	"Macedonian", "Malagasy", "Malay", "Malayalam", "Maltese", "Maori",
	"Marathi", "Mongolian", "Nepali", "Norwegian", "Pashto", "Persian",
	"Polish", "Portuguese", "Punjabi", "Romanian", "Russian", "Samoan",
	"Sanskrit", "Serbian", "Slovak", "Slovenian", "Somali", "Spanish",
	"Sundanese", "Swahili", "Swedish", "Tagalog", "Tamil", "Telugu",
	"Thai", "Turkish", "Ukrainian", "Urdu", "Uzbek", "Vietnamese",
	"Welsh", "Xhosa", "Yiddish", "Yoruba", "Zulu"
];

function changeLanguage(lang) {
	let url = new URL(window.location.href); 
	url.searchParams.set('lang', lang); 
	window.location.href = url.toString(); 
}


function autoResize(textarea) {
	textarea.style.height = 'auto';  
	textarea.style.height = (textarea.scrollHeight) + 'px';  
}

/* connect socket */

function debounce(func, delay) {
    let timeoutId;
    return function(...args) {
        if (timeoutId) clearTimeout(timeoutId);
        timeoutId = setTimeout(() => {
            func.apply(this, args);
        }, delay);
    };
}

let stompClient = null;

function connect() {
	
	console.log("userId " + userId)
		
    const socket = new SockJS('/ws-chat');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);

        // Subscribe vào kênh thông báo
        stompClient.subscribe(`/user/queue/notification`, function(messageOutput) {            
			const payload = JSON.parse(messageOutput.body);
			console.log("payload.type " + payload.type)
			if (payload.type === "NEW_MESSAGE") {
				debounceuUpdateUnreadMessageCount();
			} else if (payload.type === "NEW_NOTIFICATION") {
				updateUnreadNotificationCount();
			} else if (payload.type === "REFRESH_MESSAGE_BADGE") {
				debounceuUpdateUnreadMessageCount();
			}
			
			
        });

        // Subscribe vào kênh chat của người dùng
		stompClient.subscribe(`/user/queue/chat`, function(messageOutput) {
			const chatMessage = JSON.parse(messageOutput.body);
			console.log(" chatMessage.sender.id " + chatMessage.sender.id + " userId " + userId + " " + (chatMessage.sender.id == userId))
			appendMessageToChat(chatMessage, chatMessage.sender.id == userId);
			
			const chatMessages = document.getElementById('chatMessages');
			
			if(chatMessages != null && chatMessage.receiver.id == userId) {
				debouncedMarkMessAsRead();
				console.log("đánh dấu tin nhắn đã đọc.")
			}
		});
    });
}


window.onload = connect;



/* manage services */

function initOptionData() {

	console.log("initOptionData")



	const languageSelect = document.getElementById('language');
	const editLanguageSelect = document.getElementById('editLanguage');

	languages.forEach(language => {
		const option = document.createElement('option');
		option.value = language;  
		option.textContent = language; 


		const editOption = document.createElement('option');
		editOption.value = language;
		editOption.textContent = language;

		languageSelect.appendChild(option);
		editLanguageSelect.appendChild(editOption)

	});

	const citySelect = document.getElementById('city');
	const editCitySelect = document.getElementById('editCity');


	for (const code in cityDisplayNames) {
		const option = document.createElement('option');	
		option.value = code;
		option.textContent = cityDisplayNames[code];
		citySelect.appendChild(option);
		
		const editOption = document.createElement('option');
		editOption.value = code;
		editOption.textContent = cityDisplayNames[code];
		editCitySelect.appendChild(editOption);
	}
}

let servicesPage = {
	currentPage: 1,
	itemsPerPage: 10
}

function changeServicesPage(direction) {
	if (direction === 'prev') {
		servicesPage.currentPage -= 1;
	} else if (direction === 'next') {
		servicesPage.currentPage += 1;
	}

	fetchServices(servicesPage.currentPage);
}

function openEditServicePopup(serviceId) {
	const serviceModal = document.getElementById("editServiceModal");
	serviceModal.style.display = "flex";

	const row = document.getElementById(`service-row-${serviceId}`);


	const serviceType = row.querySelector(".type").dataset.type;
	const groupSizeCategory = row.querySelector(".groupSizeCategory").dataset.groupSizeCategory;
	const city = row.querySelector(".city").dataset.city;
	const language = row.querySelector(".language").innerText;
	const price = row.querySelector(".price").innerText;

	document.getElementById("editServiceId").value = serviceId;
	document.getElementById("editServiceType").value = serviceType;
	document.getElementById("editGroupSizeCategory").value = groupSizeCategory;
	document.getElementById("editCity").value = city;
	document.getElementById("editPrice").value = price;
	document.getElementById("editLanguage").value = language;
}

function openAddServicePopup() {
	const serviceModal = document.getElementById("serviceModal");
	serviceModal.style.display = "flex";
}

function closeAddServicePopup() {
	const serviceModal = document.getElementById("serviceModal");
	serviceModal.style.display = "none";
}

function closeEditServicePopup() {
	const serviceModal = document.getElementById("editServiceModal");
	serviceModal.style.display = "none";
}


async function submitEditService() {
	const id = document.getElementById("editServiceId").value;
	const serviceForm = document.getElementById("editServiceForm");
	const type = document.getElementById("editServiceType").value;
	const groupSizeCategory = document.getElementById("editGroupSizeCategory").value;
	const city = document.getElementById("editCity").value;
	const price = document.getElementById("editPrice").value;
	const language = document.getElementById("editLanguage").value;

	if (!type || !groupSizeCategory || !city || !price || !language) {
		alert("Please fill out all fields.");
		return;
	}

	const serviceData = { id, type, groupSizeCategory, city, language, price };
	console.log("Service Data:", serviceData);

	try {
		const response = await fetch(`/api/guide-services/${id}`, {
			method: 'PUT',
			headers: {
				'Content-Type': 'application/json',
			},
			body: JSON.stringify(serviceData),
		});

		const message = await response.text();

		if (response.ok) {
			fetchServices(servicesPage.currentPage);
			closeEditServicePopup();
			alert("Service updated successfully!");
		} else {
			closeEditServicePopup();
			alert(`Error: ${message}`);
		}
	} catch (error) {
		console.error("Error while updating service:", error);
		alert("An unexpected error occurred!");
	}

	serviceForm.reset();
}


async function submitService() {

	const serviceForm = document.getElementById("serviceForm");
	const type = document.getElementById("serviceType").value;
	const groupSizeCategory = document.getElementById("groupSizeCategory").value;
	const city = document.getElementById("city").value;
	const price = document.getElementById("price").value;
	const language = document.getElementById("language").value;

	if (!type || !groupSizeCategory || !city || !price || !language) {
		alert("Please fill out all fields.");
		return;
	}

	const serviceData = {
		type,
		groupSizeCategory,
		city,
		language,
		price
	};

	console.log("Service Data:", serviceData); 

	closeAddServicePopup()

	try {
		const response = await fetch('/api/guide-services', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
			},
			body: JSON.stringify(serviceData),
		});

		if (response.status === 201) {
			fetchServices(servicesPage.currentPage);
			closeAddServicePopup();
			alert("Service added successfully!");
		} else {
			const errorMessage = await response.text();
			closeAddServicePopup();
			alert(`Error: ${errorMessage}`);
		}
	} catch (error) {
		console.error("An unexpected error occurred!", error);
		alert("An unexpected error occurred!");
	}

	serviceForm.reset();
}

async function deleteService(serviceId) {
	const confirmed = confirm('Are you sure you want to delete this service?');
	if (!confirmed) return;

	try {
		const response = await fetch(`/api/guide-services/${serviceId}`, {
			method: 'DELETE',
		});

		if (response.ok) {
			alert('Service deleted successfully!');
			fetchServices(servicesPage.currentPage);
		} else {
			const errorMessage = await response.text();
			alert(`Failed to delete service: ${errorMessage}`);
		}
	} catch (error) {
		console.error('Error deleting service:', error);
		alert('An unexpected error occurred while deleting service!');
	}
}


async function fetchServices(page = 1) {
	servicesPage.currentPage = page;

	let url = '/api/guide-services?';
	url += `page=${servicesPage.currentPage - 1}&`;
	url += `size=${servicesPage.itemsPerPage}`;

	try {
		const response = await fetch(url);
		if (!response.ok) {
			throw new Error(`HTTP error! status: ${response.status}`);
		}

		const data = await response.json();
		const services = data.content;
		const tableBody = document.getElementById('servicesTableBody');
		tableBody.innerHTML = '';

		services.forEach(service => {
			const row = document.createElement('tr');
			row.id = `service-row-${service.id}`;
			row.innerHTML = `
				<td class="type" data-type=${service.type}>${serviceTypeDisplayNames[service.type]}</td>
				<td class="groupSizeCategory" data-group-size-category=${service.groupSizeCategory}>${groupSizeCategoryDisplayNames[service.groupSizeCategory]}</td>
				<td class="language">${service.language}</td>
				<td class="city" data-city=${service.city}>${cityDisplayNames[service.city]}</td>
				<td class="price" data-price="150">${service.price}</td>
                <td>
                    <button class="btn btn-sm btn-warning" onclick="openEditServicePopup(${service.id})">Edit</button>
                    <button class="btn btn-sm btn-danger" onclick="deleteService(${service.id})">Delete</button>
                </td>
            `;
			tableBody.appendChild(row);
		});

		updatePaginationButtons(data.number + 1, data.totalElements, data.size);

	} catch (error) {
		console.error('Lỗi khi tải danh sách dịch vụ:', error);
		const tableBody = document.getElementById('servicesTableBody');
		tableBody.innerHTML = `<tr><td colspan="6">Không thể tải danh sách dịch vụ.</td></tr>`;
	}
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

async function fetchPosts(page = 1) {
	postsPage.currentPage = page;

	const title = document.getElementById('title').value;
	const category = document.getElementById('category').value;


	let url = '/api/users/posts?';
	if (title) url += `title=${encodeURIComponent(title)}&`;
	if (category) url += `category=${encodeURIComponent(category)}&`;
	url += `page=${postsPage.currentPage - 1}&`;
	url += `size=${postsPage.itemsPerPage}`;

	try {
		const response = await fetch(url);
		if (!response.ok) {
			throw new Error(`HTTP error! status: ${response.status}`);
		}

		const data = await response.json();
		const posts = data.content;
		const tableBody = document.getElementById('postsTableBody');
		tableBody.innerHTML = '';

		posts.forEach(post => {
			const categoryName = categoryDisplayNames[post.category] || post.category;
			const row = document.createElement('tr');
			row.innerHTML = `
                <td>${post.title}</td>
                <td>${categoryName}</td>
                <td>${post.author.fullName}</td>
                <td>
                    <a class="btn btn-sm btn-primary" href="/posts/${post.id}">View</a>
                    <a class="btn btn-sm btn-success" href="/posts/${post.id}/edit">Edit</a>
                    <button class="btn btn-sm btn-danger" onclick="deletePost(${post.id})">Delete</button>
                </td>
            `;
			tableBody.appendChild(row);
		});

		updatePaginationButtons(data.number + 1, data.totalElements, data.size);

	} catch (error) {
		console.error('Lỗi khi tải danh sách bài viết:', error);
		const tableBody = document.getElementById('postsTableBody');
		tableBody.innerHTML = `<tr><td colspan="4">Không thể tải danh sách bài viết.</td></tr>`;
	}
}


async function deletePost(id) {
	if (confirm("Are you sure you want to delete this post?")) {
		try {
			const response = await fetch(`/api/posts/${id}/delete`, {
				method: 'DELETE',
				headers: {
					'Content-Type': 'application/json',
				}
			});

			if (!response.ok) {
				const errorText = await response.text();
				throw new Error(errorText);
			}

			alert("Post deleted successfully.");
			fetchPosts(postsPage.currentPage);

		} catch (error) {
			console.error("Error deleting post:", error);
			alert("Failed to delete post.");
		}
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

async function fetchUsers(page = 1) {
	usersPage.currentPage = page;

	const email = document.getElementById('email').value;
	const fullName = document.getElementById('fullName').value;
	const role = document.getElementById('role').value;
	const kycApproved = document.getElementById('kycApproved').value;
	const enabled = document.getElementById('enabled').value;


	let url = '/api/users?';
	if (email) url += `email=${encodeURIComponent(email)}&`;
	if (fullName) url += `fullName=${encodeURIComponent(fullName)}&`;
	if (role) url += `role=${encodeURIComponent(role)}&`;
	if (kycApproved !== "") url += `kycApproved=${kycApproved}&`;
	if (enabled !== "") url += `enabled=${enabled}&`;
	url += `page=${usersPage.currentPage - 1}&`;
	url += `size=${usersPage.itemsPerPage}`;

	try {
		const response = await fetch(url);
		if (!response.ok) {
			throw new Error(`HTTP error! Status: ${response.status}`);
		}

		const data = await response.json();

		const tableBody = document.getElementById('usersTable');
		tableBody.innerHTML = '';

		const users = data.content;

		users.forEach(user => {
			const actions = [];
 
			actions.push(`<a class="button btn btn-sm btn-info" href="/users/${user.id}">View profile</a>`);
			if (user.enabled) {
				actions.push(`<button class="btn btn-sm btn-danger" onclick="performUserAction(${user.id}, 'disable')">Disable</button>`);
			} else {
				actions.push(`<button class="btn btn-sm btn-success" onclick="performUserAction(${user.id}, 'enable')">Enable</button>`);
			}

			if (!user.kycApproved) {
				actions.push(`<button class="btn btn-sm btn-primary" onclick="performUserAction(${user.id}, 'approve-kyc')">Approve KYC</button>`);
				actions.push(`<button class="btn btn-sm btn-warning" onclick="performUserAction(${user.id}, 'reject-kyc')">Reject KYC</button>`);
			}

			const row = document.createElement('tr');
			row.innerHTML = `
                <td>${user.fullName}</td>
                <td>${user.email}</td>
                <td>${user.role}</td>
                <td><b>KYC: </b>${user.kycApproved}</td>
                <td><b>Enabled: </b>${user.enabled}</td>
                <td>${actions.join(' ')}</td>
            `;
			tableBody.appendChild(row);
		});

		updatePaginationButtons(data.number + 1, data.totalElements, data.size);

	} catch (error) {
		console.error('Lỗi khi tải danh sách người dùng:', error);
		const tableBody = document.querySelector('#usersTable tbody');
		if (tableBody) {
			tableBody.innerHTML = `<tr><td colspan="7">Không thể tải danh sách người dùng.</td></tr>`;
		}
	}
}



async function performUserAction(userId, action) {
	let url = `/api/users/${userId}/${action}`;
	let confirmMessage = '';
	let successMessage = '';
	let rejectReason;
	

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
		/*case 'reject-kyc':
			const data = {
					reason: rejectReason
				};*/
		default:
			console.error('Hành động không hợp lệ:', action);
			return;
	}

	if (!confirm(confirmMessage)) return;

	try {
		const response = await fetch(url, { method: 'PUT' });

		if (!response.ok) {
			const errorText = await response.text();
			throw new Error(errorText || `${action} failed`);
		}

		alert(successMessage);
		await fetchUsers(usersPage.currentPage);

	} catch (error) {
		console.error(`Lỗi khi thực hiện hành động ${action}:`, error);
		alert(`Có lỗi khi thực hiện hành động: ${action}`);
	}
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



	let url = '/api/reports?';
	if (reporter) url += `reporter=${reporter}&`;
	if (reason) url += `reason=${reason}&`;
	if (resolved) url += `resolved=${resolved}&`;
	if (reportType) url += `reportType=${reportType}&`;

	url += `page=${reportsPage.currentPage - 1}&`;
	url += `size=${reportsPage.itemsPerPage}`

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
		  	<div>${report.resolved ? `<span class="badge bg-success text-white"> Resolved` : `<span class="badge bg-warning text-dark"> Unresolved`}</div>
		  	${report.resolved ? `<div style="font-size: 0.9em; color: #555;">Admin Feedback: ${report.adminFeedBack || "(No feedback)"}</div>` : ""}
		  </td>
		  <td>${!report.resolved ? `
	        <button class="btn btn-sm btn-primary" onclick="resolveReport(${report.id})">Resolve</button>
	      ` : `<button class="btn btn-sm btn-warning disabled-btn" disabled>Resolve</button>`}
		  </td>
	    `;

		tbody.appendChild(row);
	});


	updatePaginationButtons(data.number + 1, data.totalElements, data.size);
}

async function resolveReport(reportId) {
	const adminFeedBack = prompt("Enter feedback:");

	if (!adminFeedBack) {
		alert("You need to enter a feedback!");
		return;
	}

	const reportData = {
		id: reportId,
		adminFeedBack: adminFeedBack,
	};

	try {
		const res = await fetch(`/api/reports/${reportData.id}`, {
			method: "PUT",
			headers: {
				"Content-Type": "application/json",
			},
			body: JSON.stringify(reportData),
		});

		if (!res.ok) {
			const errorText = await res.text();
			throw new Error(errorText || "Đã xảy ra lỗi");
		}

		const result = await res.text();
		alert(result || "Feedback resolved successfully!");

		fetchReports(reportsPage.currentPage); 

	} catch (err) {
		console.error(err);
		alert(err.message || "Đã xảy ra lỗi");
	}
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

async function checkReviewStatus(bookingId) {
	try {
		const response = await fetch(`/api/reviews/exists?bookingId=${bookingId}`, {
			method: 'GET',
			headers: {
				'Accept': 'application/json'
			}
		});

		if (!response.ok) {
			throw new Error(`HTTP error! status: ${response.status}`);
		}

		const hasReviewed = await response.json();
		console.log("bookingId hasReviewed: " + hasReviewed);
		return hasReviewed;

	} catch (error) {
		console.error("Lỗi khi kiểm tra đánh giá:", error);
		return false;
	}
}

async function submitReview(bookingId, reviewedUserId) {

	const rating = document.getElementById("rating").value;

	const feedback = document.getElementById("feedback").value.trim();

	console.log("rating " + rating);

	if (!rating) {
		alert("Please select a rating.");
		return;
	}
	if (feedback === "") {
		alert("Please enter your feedback.");
		return;
	}


	const reviewData = {
		reviewedUser: { id: reviewedUserId },
		booking: { id: bookingId },
		rating: parseInt(rating),
		feedback: feedback
	};

	try {
		const response = await fetch('/api/reviews', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify(reviewData)
		});

		if (!response.ok) {
			throw new Error("Failed to submit review");
		}

		const msg = await response.text();
		alert(msg);
		closeReviewPopup();
		document.getElementById("reviewForm").reset();
	} catch (err) {
		console.error("Submit failed", err);
		alert("Failed to submit review.");
	}

	closeReviewPopup();
	document.getElementById("reviewForm").reset();
}


function closeReviewPopup() {
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
async function loadIncomeSummary() {
	try {
		const response = await fetch(`/api/guides/income-summary`);
		const data = await response.json();

		// Hiển thị tổng thu nhập
		document.getElementById("totalIncome").textContent = data.totalIncome.toLocaleString("vi-VN");

		// Vẽ biểu đồ thu nhập hàng tháng
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

		// Biểu đồ tổng giá trị đơn hàng
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
		
		
		const ctx3 = document.getElementById('incomeByTypeChart').getContext('2d');
		
		new Chart(ctx3, {
			type: 'pie',
			data: {
				labels: Object.keys(data.incomeByServiceType),
				datasets: [{
					labels: Object.keys(data.incomeByServiceType),
					data: Object.values(data.incomeByServiceType),
					backgroundColor: [
						'rgba(255, 99, 132, 0.7)',
						'rgba(54, 162, 235, 0.7)',
						'rgba(255, 206, 86, 0.7)',
						'rgba(75, 192, 192, 0.7)'
					]
				}]
			},
			options: {
				responsive: false,
				maintainAspectRatio: false
			}
		});
		
	} catch (error) {
		console.error("Error loading income summary:", error);
		alert("Failed to load income summary.");
	}
}




/* manage DayOff */
let flatpickrInstance;
let originalDates = []; 
let originalAutoGeneratedDates = [];
let selectedDates = [];

function fetchDayOff() {
	fetch('/api/guides/day-off')
		.then(response => {
			if (!response.ok) {
				throw new Error("Lỗi khi fetch day-off");
			}
			return response.json();
		})
		.then(data => {
			originalDates = data
				.filter(d => !d.autoGenerated)
				.map(d => d.date);

			originalAutoGeneratedDates = data
				.filter(d => d.autoGenerated)
				.map(d => d.date);

			selectedDates = [...originalDates];

			console.log("data", data);
			console.log("originalDates", originalDates);

			if (flatpickrInstance) {
				// Nếu đã có flatpickr -> update lại
				flatpickrInstance.set("disable", originalAutoGeneratedDates);
				flatpickrInstance.setDate(selectedDates, false);
				flatpickrInstance.redraw();
			} else {
				// Nếu chưa có flatpickr -> khởi tạo mới
				flatpickrInstance = flatpickr("#manualDayOffs", {
					mode: "multi",
					dateFormat: "Y-m-d",
					minDate: "today",
					disable: originalAutoGeneratedDates,
					inline: true,
					defaultDate: selectedDates,
					onDayCreate: function(dObj, dStr, fp, dayElem) {
						const date = fp.formatDate(dayElem.dateObj, "Y-m-d");
						if (selectedDates.includes(date)) {
							console.log("add day-off", date);
							dayElem.classList.add("day-off");
						}

						dayElem.addEventListener("click", function() {
							if (selectedDates.includes(date)) {
								selectedDates = selectedDates.filter(d => d !== date);
							} else {
								selectedDates.push(date);
							}
							fp.setDate(selectedDates, false);
							fp.redraw();
						});
					}
				});
			}
		})
		.catch(err => {
			console.error("Lỗi fetch day-off:", err);
			alert("Không thể tải lịch bận.");
		});
}



function deleteDayOffs(datesToDelete) {
	if (datesToDelete.length === 0) return Promise.resolve();
	return fetch('/api/guides/day-off/delete', {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify(datesToDelete)
	});
}

function addDayOffs(datesToAdd) {
	if (datesToAdd.length === 0) return Promise.resolve();
	return fetch('/api/guides/day-off', {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify(datesToAdd)
	});
}

async function submitDayOff() {
	const toAdd = selectedDates.filter(date => !originalDates.includes(date));
	const toDelete = originalDates.filter(date => !selectedDates.includes(date));

	try {
		// Xóa các ngày bận cũ trước
		await deleteDayOffs(toDelete);
		
		// Thêm các ngày bận mới sau
		await addDayOffs(toAdd);

		// Hiển thị thông báo và reload lịch bận
		alert("Đã cập nhật lịch bận!");
		fetchDayOff(); // Reload lại từ server
	} catch (err) {
		console.error("Lỗi cập nhật:", err);
		alert("Đã xảy ra lỗi, vui lòng thử lại.");
	}
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
		  <td>${serviceTypeDisplayNames[booking.guideService.type]}</td>
		  <td>${cityDisplayNames[booking.guideService.city]}</td>
		  <td>${booking.guideService.language}</td>
		  <td>${groupSizeCategoryDisplayNames[booking.guideService.groupSizeCategory]}</td>		
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
				? `<div class="text-muted small fst-italic mt-1">Reason: ${booking.reason || "No reason"}. (Canceled at: ${formatDate(booking.canceledAt)})</div>`
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

async function handleBookingAction(bookingId, action) {
	console.log("handleBookingAction", bookingId, action);
	let reason = null;

	// Kiểm tra nếu cần nhập lý do (đối với các hành động hủy hoặc từ chối)
	if (action === 'cancel-by-user' || action === 'cancel-by-guide' || action === 'reject') {
		reason = prompt("Enter reason:");
		if (!reason) {
			alert("You need to enter a reason for cancellation!");
			return;
		}
	}

	try {
		// Thực hiện yêu cầu PUT với lý do nếu có
		const response = await fetch(`/api/bookings/${bookingId}/${action}`, {
			method: "PUT",
			headers: {
				"Content-Type": "application/json",
			},
			body: JSON.stringify({ reason })
		});

		const data = await response.json();
		if (!response.ok) {
			throw new Error(data.message || "An error occurred.");
		}

		// Thông báo và cập nhật danh sách đặt chỗ
		alert(data.message || `${action} success`);
		if(action === 'cancel-by-user') {
			await fetchBookings(historyBookingsPage.currentPage); 
		} else {
			await fetchGuideBookings(guideBookingsPage.currentPage); 
		}
		

	} catch (err) {
		console.error("Error handling booking action:", err);
		alert(err.message || "Đã xảy ra lỗi");
	}
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
		  <td>${serviceTypeDisplayNames[booking.guideService.type]}</td>
		  <td>${cityDisplayNames[booking.guideService.city]}</td>
		  <td>${booking.guideService.language}</td>
		  <td>${groupSizeCategoryDisplayNames[booking.guideService.groupSizeCategory]}</td>		
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
				? `<div class="text-muted small fst-italic mt-1">Reason: ${booking.reason || "No reason"}. (Cancled at: ${formatDate(booking.canceledAt)})</div>`
				: ""
			}
		  </td>
		  <td>
		    <div class="d-flex gap-2">
		      <a class="btn btn-sm btn-primary" href="/users/bookings/${booking.id}" data-id="${booking.id}">View</a>
		      
			  ${(booking.status === "PENDING" || booking.status === "CONFIRMED") ? `
			    <button class="btn btn-sm btn-danger" onclick="handleBookingAction('${booking.id}', 'cancel-by-user')">Cancel</button>
			  ` : ''}

			  ${booking.status === "COMPLETED" ? `
			    <button class="btn btn-sm btn-success" onclick="openReviewModal(${booking.id}, ${booking.guide.id}, '${booking.guide.fullName}')">Submit Review</button>
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
	if (confirm("Are you sure you want to cancel your booking?")) {
		let canceledReason = prompt("Enter reason for cancellation:");
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
function previewImage(event, previewId) {
	console.log("previewImage...")
	const file = event.target.files[0];  // Lấy file người dùng chọn
	const previewDiv = document.getElementById(previewId);  // Nơi hiển thị ảnh preview
	previewDiv.innerHTML = '';  // Xóa mọi ảnh preview trước đó (nếu có)

	if (file) {
		const reader = new FileReader();  // Đọc file ảnh

		reader.onload = function(e) {
			previewDiv.innerHTML = `<img src="${e.target.result}" alt="Ảnh giấy phép" class="img-thumbnail mt-2" width="200">`;
		};

		reader.readAsDataURL(file);  // Đọc file ảnh dưới dạng base64
	}
}


function updateGuideStatusUI(data) {
	
	// Các phần tử DOM cần cập nhật
	const statusMessage = document.getElementById('statusMessage');
	const guideForm = document.getElementById('guideForm');
	const guideLicenseInput = document.getElementById('guideLicense');
	const experienceInput = document.getElementById('experience');
	const guideLicensePreview = document.getElementById('guideLicensePreview');
	const cccdPreview = document.getElementById('cccdPreview');
	const internationalGuide = document.getElementById('isInternationalGuide');
	const localGuide = document.getElementById('isLocalGuide');
	
	const status = data.status;
	statusMessage.dataset.status = status || 'UNKNOWN';

	switch (status) {
		case 'REJECTED':
			statusMessage.innerHTML = `<div class="alert alert-danger">Rejected: ${data.reason || 'No reason provided.'}</div>`;
			guideForm.style.display = 'block';
			guideLicenseInput.value = data.guideLicense || '';
			experienceInput.value = data.experience || '';
			internationalGuide.checked = data.internationalGuide;
			localGuide.checked = data.localGuide;
			cccdPreview.innerHTML = data.cccdUrl
				? `<img src="${data.cccdUrl}" alt="cccdUrl" class="img-thumbnail mt-2" width="200">`
							: '';
			guideLicensePreview.innerHTML = data.guideLicenseUrl
				? `<img src="${data.guideLicenseUrl}" alt="Ảnh giấy phép" class="img-thumbnail mt-2" width="200">`
				: '';
			break;

		case 'PENDING':
			statusMessage.innerHTML = `<div class="alert alert-info">Your request is pending approval.</div>`;
			guideForm.style.display = 'none';
			break;

		case 'APPROVED':
			statusMessage.innerHTML = `<div class="alert alert-info">Your request has been approved.</div>`;
			guideForm.style.display = 'block';
			guideLicenseInput.value = data.guideLicense || '';
			experienceInput.value = data.experience || '';
			internationalGuide.checked = data.internationalGuide;
		    localGuide.checked = data.localGuide;
			cccdPreview.innerHTML = data.cccdUrl
				? `<img src="${data.cccdUrl}" alt="cccdUrl" class="img-thumbnail mt-2" width="200">`
				: '';
			guideLicensePreview.innerHTML = data.guideLicenseUrl
				? `<img src="${data.guideLicenseUrl}" alt="Ảnh giấy phép" class="img-thumbnail mt-2" width="200">`
				: '';
			break;

		case 'NONE':
			statusMessage.innerHTML = '';
			guideForm.style.display = 'block';
			break;

		default:
			statusMessage.textContent = 'You are not registered as a guide.';
			guideForm.style.display = 'block';
			break;
	}
}



async function checkGuideRequestStatus() {
	try {
		const response = await fetch('/api/guide-requests/status');

		// Kiểm tra lỗi phản hồi từ server
		let data;
		if (!response.ok) {
			if (response.status === 404) {
				data = { status: 'NONE' };
			} else {
				throw new Error(`HTTP error! status: ${response.status}`);
			}
		}
		else {
			data = await response.json();
		}	
		console.log("checkGuideRequestStatus data.status " + data.status);
		updateGuideStatusUI(data)
	} catch (error) {
		console.error('Error:', error);
		const statusMessage = document.getElementById('statusMessage');
		if (statusMessage) {
			statusMessage.innerHTML = '<div class="alert alert-danger">Có lỗi xảy ra. Vui lòng thử lại sau.</div>';
		}
	}
}



async function submitGuideRegister() {

	console.log("submitGuideRegister...")

	const formData = new FormData();
	formData.append("cccdFile", document.getElementById("cccdFile").files[0]);
	formData.append("guideLicenseFile", document.getElementById("guideLicenseFile").files[0]);
	formData.append("guideLicense", document.getElementById("guideLicense").value);
	formData.append("experience", CKEDITOR.instances["experience"].getData());
	formData.append("isLocalGuide", document.getElementById("isLocalGuide").checked);
	formData.append("isInternationalGuide", document.getElementById("isInternationalGuide").checked);

	const statusMessage = document.getElementById('statusMessage');
	let requestMethod = 'POST';
	if (statusMessage.dataset.status === 'REJECTED' || statusMessage.dataset.status === 'APPROVED') {
		requestMethod = 'PUT'
	}

	try {
		// Đợi kết quả của fetch
		const response = await fetch("/api/guide-requests/register", {
			method: requestMethod,
			body: formData
		});

		console.log("response " + response)
		console.log("response.ok " + response.ok)
		
		if (!response.ok) {
			const errorData = await response.json(); 
			throw new Error(errorData.message || "An error occurred, please try again later!");
		}

		alert("Application submitted successfully!");
		data = await response.json(); 
		updateGuideStatusUI(data)
		
	} catch (error) {
		console.error(error);
		alert(`An error occurred, please try again later!`);
	}
}


function formatDate(dateString) {
	const date = new Date(dateString);
	return date.toLocaleDateString("vi-VN");
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
		if (!response.ok) throw new Error("Unable to load application list.");
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
								<td><img src="${request.cccdUrl}" alt="cccd" width="100" height="60" style="object-fit: cover; border-radius: 5px;"></td>
				                <td><img src="${request.guideLicenseUrl}" alt="Guide License" width="100" height="60" style="object-fit: cover; border-radius: 5px;"></td>
				                <td>${request.guideLicense}</td>
								<td>${request.localGuide}</td>
								<td>${request.internationalGuide}</td>
				                <td>${request.experience}</td>
				                <td>
				                    <a href="/users/${request.user.id}" class="btn btn-info btn-sm">View user profile</a>
				                    <button class="btn btn-success btn-sm" onclick="approveGuide(${request.id})">Approve</button>
				                    <button class="btn btn-danger btn-sm" onclick="rejectGuide(${request.id})">Reject</button>
				                </td>
				            `;

			tableBody.appendChild(row);
		});

		updatePaginationButtons(page, guideRequests.length, guideRequestsPage.itemsPerPage);

	} catch (error) {
		console.error("error:", error);
	}
}

function changeGuideRequestsPage(direction) {
	if (direction === 'prev') {
		guideRequestsPage.currentPage -= 1;
	} else if (direction === 'next') {
		guideRequestsPage.currentPage += 1;
	}

	fetchGuideRequests(guideRequestsPage.currentPage);
}


async function approveGuide(id) {
	if (!confirm("Are you sure you want to approve this application?")) return;

	try {
		const response = await fetch(`/api/admin/guide-requests/${id}/approve`, { method: "PUT" });
		if (!response.ok) throw new Error("An error occurred, please try again later!");
		alert("Application approved successfully!");
		fetchGuideRequests(guideRequestsPage.currentPage);
	} catch (error) {
		console.error(error);
		alert("An error occurred, please try again later!");
	}
}

async function rejectGuide(id) {
	const reason = prompt("Enter the reason:");
	if (!reason) return;

	if (!confirm("Are you sure you want to reject this application?")) return;

	try {
		const response = await fetch(`/api/admin/guide-requests/${id}/reject`, {
			method: "PUT",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify({ reason: reason })
		});

		if (!response.ok) throw new Error("An error occurred, please try again later!");

		alert("GuideRequest rejected successfully!");
		fetchGuideRequests(guideRequestsPage.currentPage);
	} catch (error) {
		console.error(error);
		alert("An error occurred, please try again later!");
	}
}
