let currentReceiverId = null;
let currentReceiver = {
	id: null,
	email: null
};

// Đánh dấu tin nhắn là đã đọc
function markMessAsRead() {
	console.log("markMessAsRead")
    stompClient.send("/app/chat.read", {}, JSON.stringify(currentReceiver.id));
	console.log("finish")
}

const debouncedMarkMessAsRead = debounce(markMessAsRead, 500);
const debounceuUpdateUnreadMessageCount = debounce(updateUnreadMessageCount, 600);


setupMessageClickHandler()
updateUnreadMessageCount()

function updateUnreadMessageCount() {
	
	fetch('/api/chat/unread-count')
		.then(response => response.json())
		.then(count => {
			console.log("updateUnreadMessageCount " + count)
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


function sendChatMessage(event) {
	event.preventDefault(); // Ngăn reload trang khi submit
	console.log("sendChatMessage currentReceiverId " + currentReceiver.id)

	const chatForm = document.getElementById('chatForm');
	const chatInput = document.getElementById('chatInput');
	const chatMessages = document.getElementById('chatMessages');


	const message = chatInput.value.trim();
	if (!message) return;

	const payload = {
		receiver: currentReceiver,
		content: message
	};
	
	
	stompClient.send("/app/chat.send", {}, JSON.stringify(payload));
	
	chatInput.value = '';

/*	fetch('/api/chat/send', {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify(payload)
	})
		.then(res => res.json())
		.then(data => {
			appendMessage(data, true);
			chatInput.value = '';
		});*/
}


// Hiển thị tin nhắn
/*function appendMessage(msg, isOwn) {
	console.log("appendMessage")
	const el = document.createElement('div');
	el.className = `mb-2 ${isOwn ? 'text-end' : 'text-start'}`;
	el.innerHTML = `<span class="badge ${isOwn ? 'bg-primary' : 'bg-secondary'}">${msg.content}</span>`;
	chatMessages.appendChild(el);
	chatMessages.scrollTop = chatMessages.scrollHeight;
}*/

// Lấy cuộc hội thoại hiện có
/*function loadConversation(receiverId) {
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

}*/

function closeChat() {
	console.log("closeChat")
	document.getElementById('chatBox').style.display = 'none';
}


function createChatBox() {
	console.log("createChatBox currentReceiverId " + currentReceiver.id)


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
      <a href="/users/${currentReceiver.id}"><h5 id="chatBoxPartnerName" class="mb-0"></h5></a>
      <button class="btn btn-sm btn-outline-danger" onclick="closeChat()">Close</button>
    </div>

    <div id="chatMessages" class="flex-grow-1 overflow-auto mb-3 border p-2 rounded bg-white" style="max-height: 350px;">
    </div>

    <form id="chatForm" class="d-flex gap-2">
      <input type="text" id="chatInput" class="form-control" placeholder="Send a message..." required>
      <button type="submit" onclick="sendChatMessage(event)" class="btn btn-primary">Send</button>
    </form>
  `;

	document.body.appendChild(chatBox);

}

function openChatWithUser(partnerId, partnerEmail, partnerName) {
	console.log("openChatWithUser")
	currentReceiver.id = partnerId;
	currentReceiver.email = partnerEmail;

	createChatBox(); // đảm bảo chatBox đã được tạo

	const chatBox = document.getElementById('chatBox');
	const chatBoxPartnerName = document.getElementById('chatBoxPartnerName');
	console.log("partnerName " + partnerName)
	console.log("chatBoxPartnerName " + chatBoxPartnerName.innerText)
	
	chatBoxPartnerName.innerText = partnerName;
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
		updateUnreadMessageCount()
	}).catch(error => {
		console.error(error);
	});

	//updateUnreadMessageCount()
}


function appendMessageToChat(msg, isOwnMessage) {
	const chatMessages = document.getElementById('chatMessages');

	if(chatMessages != null) {
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
}

function formatTimestamp(isoString) {
	const date = new Date(isoString);
	return date.toLocaleString('vi-VN', {
		hour: '2-digit',
		minute: '2-digit',
		day: '2-digit',
		month: '2-digit',
		year: 'numeric'
	});
}


async function loadAllReceivedMessage() {
	const response = await fetch(`/api/chat/last-messages`);
	if (!response.ok) {
		throw new Error("không thể lấy tin nhắn");
	}
	return await response.json();
}

function setupMessageClickHandler(messageLinkId = 'messageLink', userListId = 'userList') {
  document.getElementById(messageLinkId).addEventListener('click', function () {
    loadAllReceivedMessage().then(users => {
      const userList = document.getElementById(userListId);
      userList.innerHTML = '';
      
      if (!users || users.length === 0) {
				
        const li = document.createElement('li');
        li.innerHTML = `
          <div class="dropdown-item text-muted text-center">
            No messages
          </div>
        `;
        userList.appendChild(li);
        return;
      }

      users.forEach(user => {
        const li = document.createElement('li');
		
		const highlightClass = !user.read ? 'bg-light fw-bold' : '';
		
		
        li.innerHTML = `
          <a class="dropdown-item d-flex align-items-start ${highlightClass} gap-2" href="#" 
		  data-userid="${user.partnerId}"  data-useremail="${user.email}" data-username="${user.fullName}">
            <img src="${user.avatarUrl || 'https://i.pravatar.cc/40?u=' + user.partnerId}" 
                 alt="${user.fullName || 'Người dùng'}" 
                 class="rounded-circle" width="40" height="40">
            <div>
              <div><strong>${user.fullName || 'Không rõ tên'}</strong></div>
              <div class="small text-truncate" style="max-width: 220px; width:200px">
                ${user.lastMessage}
              </div>
              <div class="small">${formatTimestamp(user.timestamp)}</div>
            </div>
          </a>
        `;
        userList.appendChild(li);

        li.querySelector('a').addEventListener('click', (e) => {
          e.preventDefault();
          const partnerId = e.currentTarget.dataset.userid;
		  const partnerEmail = e.currentTarget.dataset.useremail;
          const partnerName = e.currentTarget.dataset.username;

          console.log("data set " + partnerId);
          openChatWithUser(partnerId,partnerEmail, partnerName);
        });
      });
    }).catch(err => {
      console.error('Lỗi khi load tin nhắn:', err);
    });
  });
}
