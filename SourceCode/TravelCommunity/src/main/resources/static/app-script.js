function changeLanguage(lang) {
	let url = new URL(window.location.href); // Get current URL
	url.searchParams.set('lang', lang); // Set or update 'lang' parameter
	window.location.href = url.toString(); // Redirect to new URL
}


function autoResize(textarea) {
    textarea.style.height = 'auto';  // Đặt lại chiều cao về mặc định
    textarea.style.height = (textarea.scrollHeight) + 'px';  // Cập nhật chiều cao
}
