
setupNotificationClickHandler()
updateUnreadNotificationCount()

function updateUnreadNotificationCount() {
	
	fetch('/api/notifications/unread-count')
		.then(response => response.json())
		.then(count => {
			console.log("updateUnreadMessageCount " + count)
			const badge = document.getElementById('notificationCountBadge');
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


function markNotificationsAsRead(notiId) {

	console.log("markNotificationsAsRead " + notiId)
	
	const payload = {
		id: notiId,
	};

	fetch(`/api/notifications/mark-read`, {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify(payload)
	}).then(response => {
		if (!response.ok) throw new Error("Không thể cập nhật trạng thái đã đọc.");
		console.log("Đã đánh dấu thông báo là đã đọc");
		updateUnreadNotificationCount()
	}).catch(error => {
		console.error(error);
	});

	//updateUnreadMessageCount()
}


function openNotificationLink(notiId, sourceLink) {
	console.log("openNotificationLink " + notiId + " " + sourceLink)
	markNotificationsAsRead(notiId)
	window.location.href = sourceLink;

}


async function loadAllReceivedNotification() {
	const response = await fetch(`/api/notifications`);
	if (!response.ok) {
		throw new Error("không thể lấy thông báo");
	}
	return await response.json();
}

function setupNotificationClickHandler(messageLinkId = 'notificationLink', notificationListId = 'notificationList') {
  document.getElementById(messageLinkId).addEventListener('click', function () {
    loadAllReceivedNotification().then(notifications => {
      const notificationList = document.getElementById(notificationListId);
      notificationList.innerHTML = '';
      
      if (!notifications || notifications.length === 0) {
        const li = document.createElement('li');
        li.innerHTML = `
          <div class="dropdown-item text-muted text-center">
            No notifications
          </div>
        `;
        notificationList.appendChild(li);
        return;
      }

	  notifications.forEach(noti => {
	    const li = document.createElement('li');

		// Tùy chọn class bổ sung nếu chưa đọc
	    const highlightClass = !noti.read ? 'bg-light fw-bold' : '';

	    li.innerHTML = `
	      <a 
	        class="dropdown-item d-flex align-items-start gap-2 ${highlightClass}" 
	        href="#"
	        onclick="openNotificationLink(${noti.id}, '${noti.sourceLink}')"
	        data-userid="${noti.id}" 
	        data-message="${noti.message}">
	        
	        <div>
	          <div class="text-muted small text-truncate" style=" max-width: 220px; width: 200px; white-space: normal; word-wrap: break-word; line-height: 1.4;">
	            ${noti.message}
	          </div>
	          <div class="text-muted small">${formatTimestamp(noti.createdAt)}</div>
	        </div>
	      </a>
	    `;
	    
	    notificationList.appendChild(li);
	  });

	  
	  
	  
    }).catch(err => {
      console.error('Lỗi khi load tin nhắn:', err);
    });
  });
}
