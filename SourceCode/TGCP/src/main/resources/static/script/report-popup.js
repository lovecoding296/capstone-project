let report = {
	targetId: 1,
	targetName: '',
	type: 'USER'
}

function openReportPopup(targetId, targetName, type) {
	
	report.targetId = targetId;
	report.type = type;
	report.targetName = targetName;
	
	console.log("openReportPopup targetId " + targetId + " type " + type)
	
	createReportPopup();	
	document.getElementById("reportModal").style.display = "flex";
}


function submitReport(){
	
	createReport()
	closeReportPopup();
}

function closeReportPopup() {
	document.getElementById("reportModal").style.display = "none";
}

function createReport() {

	console.log("createReport")

	let reason = document.getElementById("reason").value.trim(); // loại bỏ khoảng trắng đầu/cuối

	if (!reason) {
	    alert("Please enter a reason for your report.");
	    return; // dừng lại, không gửi request
	}


	const reportData = {
		targetId: report.targetId,
		reason: reason,
		reportType: report.type,
		targetName: report.targetName,
	};

	fetch("/api/reports/create", {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify(reportData) // Gửi toàn bộ reportData
	})
	.then(response => {
        if (!response.ok) { // Kiểm tra nếu response không thành công
            throw new Error("Request failed with status " + response.status);
        }
        return response; // response.json() Chuyển đổi phản hồi từ server sang JSON (nếu server trả JSON)
    })
    .then(data => {
        alert(data.message || "Report submitted successfully!"); // Hiển thị thông báo từ server
    })
    .catch(error => {
        console.error("Error creating report:", error); // Xử lý lỗi
        alert("An error occurred. Please try again later.");
    });
}


function createReportPopup(){
	if (document.getElementById("reportModal")) return; // Đã tồn tại thì không tạo lại

	const modalHtml = `
	    <div id="reportModal" class="modal" style="display:none;">
	        <div class="modal-content">
	            <span class="close" onclick="closeReportPopup()">&times;</span>
	            <h3>Send a report: </h3>

	            <form id="userReportForm">	       

	                <div class="mb-3">
	                    <label for="reason" class="form-label">Reason:</label>
	                    <input type="text" class="form-control" id="reason" name="reason" required>
	                </div>

	                <div class="d-flex justify-content-between">
	                    <button type="button" onclick="submitReport()" class="btn btn-success">Submit</button>
	                    <button type="button" class="btn btn-secondary" onclick="closeReportPopup()">Cancel</button>
	                </div>
	            </form>
	        </div>
	    </div>
	`;

	const wrapper = document.createElement('div');
	wrapper.innerHTML = modalHtml.trim();
	document.body.appendChild(wrapper.firstChild);
}