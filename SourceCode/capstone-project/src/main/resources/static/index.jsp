<!DOCTYPE html>
<html lang="en">

<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Nền tảng kết nối du lịch</title>
	<link rel="stylesheet" href="styles.css">
	<script src="script.js"></script>
</head>

<body>

	<!-- Navigation Bar -->
	<header class="navbar">
		<div class="logo">Nền tảng kết nối du lịch</div>
		<nav>
			<ul>
				<li><a href="#">Tìm Tour</a></li>
				<li><a href="#">Điểm đến</a></li>
				<li><a href="#">Trở thành hướng dẫn viên</a></li>
				<li><a href="#">Danh sách mong muốn</a></li>
				<li><a href="#" class="login-btn">Đăng nhập</a></li>
			</ul>
		</nav>
	</header>

	<!-- Hero Section -->
	<section class="hero">
		<h1>Đi theo con đường thân thiện hơn</h1>
		<p>Thực sự tìm hiểu một địa điểm từ những người hiểu rõ nhất về nó</p>

		<div class="search-container">
			<div class="tabs">
				<div class="tab active">Tour</div>
				<div class="tab">Hướng dẫn viên</div>
				<!--<div class="tab">Shore Excursions</div>
				<div class="tab">Safari</div>
				<div class="tab">Live Virtual Tour</div>-->
			</div>
			<div class="input-container">
				<svg data-icon="location-dot" aria-hidden="true" focusable="false" data-prefix="fas"
					class="location-dot" role="img" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 384 512">
					<path fill="currentColor"
						d="M215.7 499.2C267 435 384 279.4 384 192C384 86 298 0 192 0S0 86 0 192c0 87.4 117 243 168.3 307.2c12.3 15.3 35.1 15.3 47.4 0zM192 128a64 64 0 1 1 0 128 64 64 0 1 1 0-128z">
					</path>
				</svg>
				<input type="text" placeholder="Thành phố nào?">
				<input id="date-input" type="text" placeholder="Thêm ngày" readonly>
				<button class="search-button">Tìm kiếm</button>
			</div>
			<div class="calendar-container" id="calendar-container" style="display: none;">
				<button id="prevMonth">&lt;</button>
				<div class="calendar" id="calendar1"></div>
				<div class="calendar" id="calendar2"></div>
				<button id="nextMonth">&gt;</button>
			</div>
		</div>
		<div class="features">
			<div class="feature">
				<img loading="lazy" width="48" height="48" decoding="async" data-nimg="1" style="color:transparent"
					src="images/person.jpg">
				<p>Tour riêng tư 100%</p>
			</div>
			<div class="feature">
				<img loading="lazy" width="48" height="48" decoding="async" data-nimg="1" style="color:transparent"
					src="images/location.jpg">
				<p>Hành trình có thể tùy chỉnh</p>
			</div>
			<div class="feature">
				<img loading="lazy" width="48" height="48" decoding="async" data-nimg="1" style="color:transparent"
					src="images/phone.jpg">
				<p>Hủy bỏ linh hoạt</p>
			</div>
			<div class="feature">
				<img loading="lazy" width="48" height="48" decoding="async" data-nimg="1" style="color:transparent"
					src="images/headset.jpg">
				<p>Hỗ trợ khách hàng 24/7</p>
			</div>
		</div>
	</section>

	<!-- Spotlight Destinations -->
	<section class="spotlight">
		<svg width="48" height="39" viewBox="0 0 48 39" fill="none">
			<path
				d="M19.5029 39.0002C8.75348 39.0002 0.00634766 30.2531 0.00634766 19.5037C0.00634766 8.75423 8.75348 0.00708008 19.5029 0.00708008C29.0496 0.00708008 37.3389 7.11414 38.7808 16.5378C38.8355 16.8795 38.6031 17.1939 38.2615 17.2417C37.9198 17.2964 37.6054 17.064 37.5508 16.7223C36.2045 7.90002 28.4414 1.24398 19.5029 1.24398C9.43685 1.24398 1.24325 9.4376 1.24325 19.5037C1.24325 29.5697 9.43685 37.7633 19.5029 37.7633C24.3207 37.7633 28.8651 35.9046 32.2956 32.5287C32.5417 32.2895 32.9312 32.2895 33.1703 32.5356C33.4095 32.7816 33.4096 33.1711 33.1635 33.4103C29.5007 37.0116 24.6487 39.0002 19.5029 39.0002Z"
				fill="#35BD98"></path>
			<path
				d="M19.503 11.6652C14.5144 11.6652 9.77178 9.78597 6.14308 6.36911C5.89707 6.13676 5.88341 5.74041 6.11576 5.49439C6.34811 5.24155 6.74444 5.23471 6.99045 5.46706C10.3868 8.66524 14.8356 10.4283 19.503 10.4283C24.2182 10.4283 28.6875 8.63791 32.0976 5.38506C32.3436 5.14588 32.7399 5.15954 32.9722 5.40556C33.2114 5.65157 33.1978 6.04793 32.9518 6.28027C29.3162 9.75863 24.5394 11.6652 19.503 11.6652Z"
				fill="#35BD98"></path>
			<path
				d="M6.6354 33.6356C6.47139 33.6356 6.30053 33.5673 6.18435 33.4375C5.95201 33.1846 5.96567 32.7951 6.21851 32.5628C9.84038 29.1869 14.5557 27.3281 19.5101 27.3281C21.1023 27.3281 22.6809 27.5195 24.2117 27.9022C24.5465 27.9842 24.7447 28.319 24.6627 28.6539C24.5807 28.9887 24.2459 29.1869 23.911 29.1049C22.4759 28.7495 20.993 28.5719 19.5033 28.5719C14.87 28.5719 10.4486 30.3145 7.05909 33.4716C6.94291 33.581 6.78574 33.6356 6.6354 33.6356Z"
				fill="#35BD98"></path>
			<path
				d="M19.5034 39.0002C13.6059 39.0002 8.97949 30.4376 8.97949 19.5037C8.97949 8.56972 13.5991 0.00708008 19.5034 0.00708008C24.369 0.00708008 28.5717 6.08908 29.7198 14.7952C29.7676 15.1369 29.5285 15.4444 29.1868 15.4923C28.8519 15.5401 28.5376 15.3009 28.4897 14.9592C27.4373 7.01164 23.6583 1.24398 19.5034 1.24398C14.3849 1.24398 10.2232 9.4376 10.2232 19.5037C10.2232 29.5697 14.3849 37.7633 19.5034 37.7633C22.189 37.7633 24.7517 35.4262 26.5353 31.3465C26.672 31.0321 27.041 30.8886 27.3554 31.0253C27.6697 31.162 27.8132 31.531 27.6765 31.8453C25.6879 36.3898 22.7084 39.0002 19.5034 39.0002Z"
				fill="#35BD98"></path>
			<path
				d="M23.7062 20.1185H0.621891C0.280206 20.1185 0 19.8383 0 19.4966C0 19.1549 0.280206 18.8748 0.621891 18.8748H23.7062C24.0478 18.8748 24.328 19.1549 24.328 19.4966C24.328 19.8383 24.0547 20.1185 23.7062 20.1185Z"
				fill="#35BD98"></path>
			<path
				d="M19.5032 39C19.1615 39 18.8813 38.7198 18.8813 38.3781V0.62187C18.8813 0.280185 19.1615 0 19.5032 0C19.8449 0 20.1251 0.280185 20.1251 0.62187V38.3781C20.1251 38.7198 19.8449 39 19.5032 39Z"
				fill="#35BD98"></path>
			<path
				d="M32.03 33.5878C26.5698 33.5878 22.1279 29.1459 22.1279 23.6857C22.1279 18.2256 26.5698 13.7837 32.03 13.7837C37.4901 13.7837 41.932 18.2256 41.932 23.6857C41.932 29.1459 37.4901 33.5878 32.03 33.5878ZM32.03 15.0206C27.2532 15.0206 23.3648 18.909 23.3648 23.6857C23.3648 28.4625 27.2532 32.3509 32.03 32.3509C36.8067 32.3509 40.6951 28.4625 40.6951 23.6857C40.6951 18.909 36.8067 15.0206 32.03 15.0206Z"
				fill="#35BD98"></path>
			<path
				d="M45.3276 38.9998C44.7878 38.9998 44.2821 38.788 43.8994 38.4053L37.2639 31.7698C37.0247 31.5306 37.0247 31.1342 37.2639 30.8951L39.2388 28.9201C39.4711 28.6878 39.8812 28.6878 40.1135 28.9201L46.7491 35.5556C47.1317 35.9383 47.3436 36.444 47.3436 36.9839C47.3436 37.5238 47.1317 38.0294 46.7491 38.4121C46.3732 38.788 45.8675 38.9998 45.3276 38.9998ZM38.5828 31.3324L44.7809 37.5306C45.0816 37.8313 45.5736 37.8313 45.8812 37.5306C46.0247 37.3871 46.1067 37.1889 46.1067 36.9839C46.1067 36.7789 46.0247 36.5807 45.8812 36.4372L39.683 30.239L38.5828 31.3324Z"
				fill="#35BD98"></path>
		</svg>
		<h1>Điểm đến</h1>
		<div class="destinations">
			<div class="card">
				<img src="images/ho-chi-minh.jpg" alt="Ho Chi Minh City">
				<div class="overlay">
					<span>📍 Ho Chi Minh City, Vietnam</span>
				</div>
			</div>
			<div class="card">
				<img src="images/cape-town.jpg" alt="Cape Town">
				<div class="overlay">
					<span>📍 Cape Town, South Africa</span>
				</div>
			</div>
			<div class="card">
				<img src="images/porto.jpg" alt="Porto">
				<div class="overlay">
					<span>📍 Porto, Portugal</span>
				</div>
			</div>
			<div class="card">
				<img src="images/kusadasi.jpg" alt="Kusadasi">
				<div class="overlay">
					<span>📍 Kusadasi, Türkiye</span>
				</div>
			</div>
		</div>
	</section>


	<!--Guides -->
	<section class="local-guides">
		<svg width="22" height="48" viewBox="0 0 22 48" fill="none">
			<path
				d="M10.6436 45.2626C10.2105 45.2626 9.85547 44.9076 9.85547 44.4745V31.2056C9.85547 30.7726 10.2105 30.4175 10.6436 30.4175C11.0766 30.4175 11.4319 30.7726 11.4319 31.2056V44.4745C11.4232 44.9076 11.0766 45.2626 10.6436 45.2626Z"
				fill="#35BD98"></path>
			<path
				d="M13.3814 48C12.2728 48 11.2855 47.4889 10.6359 46.6921C9.98631 47.4889 8.9988 48 7.89017 48C5.94142 48 4.36503 46.415 4.36503 44.4662V29.5864C4.02725 29.7769 3.65501 29.8895 3.25659 29.9328C2.42513 30.0108 1.58488 29.7336 0.969937 29.1706C0.354996 28.6077 0 27.8108 0 26.9794V15.4688C0 12.6279 2.31247 10.324 5.14466 10.324H16.1182C18.9591 10.324 21.2629 12.6366 21.2629 15.4688V26.9794C21.2629 27.8108 20.9079 28.6163 20.293 29.1793C19.678 29.7423 18.8378 30.0194 18.0063 29.9328C17.6079 29.8982 17.2357 29.7769 16.8979 29.5864V44.4662C16.9152 46.4236 15.3302 48 13.3814 48ZM10.6444 43.6867C11.0774 43.6867 11.4319 44.0415 11.4319 44.4745C11.4319 45.5571 12.3074 46.4323 13.3901 46.4323C14.4727 46.4323 15.3475 45.5575 15.3475 44.4749V26.9014C15.3475 26.4684 15.7025 26.1133 16.1356 26.1133C16.5686 26.1133 16.9237 26.4597 16.9237 26.9014C16.9237 27.6636 17.4782 28.3132 18.1797 28.3738C18.5781 28.4171 18.9592 28.2872 19.2537 28.0187C19.5482 27.7502 19.7127 27.3778 19.7127 26.988V15.4774C19.7127 13.5027 18.1103 11.9003 16.1356 11.9003H5.162C3.18726 11.9003 1.58484 13.5027 1.58484 15.4774V26.988C1.58484 27.3778 1.74943 27.7589 2.04391 28.0187C2.33839 28.2872 2.71947 28.4171 3.11788 28.3738C3.81943 28.3045 4.3737 27.655 4.3737 26.8928C4.3737 26.4597 4.72894 26.1046 5.162 26.1046C5.59506 26.1046 5.95009 26.4597 5.95009 26.8928V44.4749C5.95009 45.5575 6.82487 46.4323 7.90751 46.4323C8.99015 46.4323 9.86493 45.5575 9.86493 44.4749C9.85627 44.0418 10.2026 43.6867 10.6444 43.6867Z"
				fill="#35BD98"></path>
			<path
				d="M16.1271 11.9092H5.15353C4.72048 11.9092 4.36523 11.554 4.36523 11.121C4.36523 10.6879 4.72048 10.3328 5.15353 10.3328H16.1271C16.5602 10.3328 16.9154 10.6879 16.9154 11.121C16.9154 11.554 16.5602 11.9092 16.1271 11.9092Z"
				fill="#35BD98"></path>
			<path
				d="M5.15353 27.6809C4.72048 27.6809 4.36523 27.3258 4.36523 26.8928V18.9938C4.36523 18.5607 4.72048 18.2056 5.15353 18.2056C5.58659 18.2056 5.94162 18.5607 5.94162 18.9938V26.8928C5.94162 27.3258 5.58659 27.6809 5.15353 27.6809Z"
				fill="#35BD98"></path>
			<path
				d="M16.1279 27.6807C15.6949 27.6807 15.3398 27.3256 15.3398 26.8926V18.9936C15.3398 18.5606 15.6949 18.2054 16.1279 18.2054C16.561 18.2054 16.9162 18.5606 16.9162 18.9936V26.8926C16.9076 27.3256 16.561 27.6807 16.1279 27.6807Z"
				fill="#35BD98"></path>
			<path
				d="M10.6444 8.93829C8.17594 8.93829 6.17529 6.92891 6.17529 4.46915C6.17529 2.00072 8.1846 0 10.6444 0C13.1041 0 15.1136 2.00939 15.1136 4.46915C15.1136 6.93757 13.1041 8.93829 10.6444 8.93829ZM10.6444 1.57633C9.05071 1.57633 7.75168 2.8755 7.75168 4.46915C7.75168 6.06279 9.05071 7.36196 10.6444 7.36196C12.238 7.36196 13.5373 6.06279 13.5373 4.46915C13.5373 2.8755 12.238 1.57633 10.6444 1.57633Z"
				fill="#35BD98"></path>
		</svg>
		<h1>Khám phá cùng hướng dẫn viên địa phương của bạn</h1>
		<div class="guides">
			<div class="profile-card">
				<!-- Profile Image -->
				<img src="images/avatar/guide4.jpg" alt="Diana B." class="profile-img">

				<!-- Card Content -->
				<div class="profile-card-content">
					<h2 class="name">Mr. Tuấn</h2>

					<!-- Rating -->
					<div class="rating">
						⭐⭐⭐⭐⭐ <span class="review-count">(22)</span>
					</div>

					<!-- Location -->
					<div class="location">
						<img src="images/icons/location-icon.webp" style="width: 16px; height: 16px;">
						Hạ Long + Cát Bà + Cô Tô
					</div>

					<!-- Languages -->
					<div class="languages">
						<img src="images/icons/language-icon.PNG" style="width: 16px; height: 16px;">
						Việt Nam, English
					</div>
				</div>

				<!-- Action Buttons -->
				<div class="profile-card-footer">
					<button class="message-btn">💬 Gửi tin nhắn</button>
				</div>
			</div>

			<div class="profile-card">
				<!-- Profile Image -->
				<img src="images/avatar/guide3.jpg" alt="Diana B." class="profile-img">

				<!-- Card Content -->
				<div class="profile-card-content">
					<h2 class="name">Nguyễn Khánh Nam</h2>

					<!-- Rating -->
					<div class="rating">
						⭐⭐⭐⭐⭐ <span class="review-count">(22)</span>
					</div>

					<!-- Location -->

					<div class="location">
						<img src="images/icons/location-icon.webp" style="width: 16px; height: 16px;">
						Hà Giang + Cao Bằng
					</div>

					<!-- Languages -->
					<div class="languages">
						<img src="images/icons/language-icon.PNG" style="width: 16px; height: 16px;">
						Việt Nam, English
					</div>
				</div>

				<!-- Action Buttons -->
				<div class="profile-card-footer">
					<button class="message-btn">💬 Gửi tin nhắn</button>
				</div>
			</div>

			<div class="profile-card">
				<!-- Profile Image -->
				<img src="images/avatar/guide1.jpg" alt="Diana B." class="profile-img">

				<!-- Card Content -->
				<div class="profile-card-content">
					<h2 class="name">Nguyen Linh</h2>

					<!-- Rating -->
					<div class="rating">
						⭐⭐⭐⭐⭐ <span class="review-count">(22)</span>
					</div>

					<!-- Location -->
					<div class="location">
						<img src="images/icons/location-icon.webp" style="width: 16px; height: 16px;">
						Quảng Bình
					</div>

					<!-- Languages -->
					<div class="languages">
						<img src="images/icons/language-icon.PNG" style="width: 16px; height: 16px;">
						Việt Name, English
					</div>
				</div>

				<!-- Action Buttons -->
				<div class="profile-card-footer">
					<button class="message-btn">💬 Gửi tin nhắn</button>
				</div>
			</div>


			<div class="profile-card">
				<!-- Profile Image -->
				<img src="images/avatar/guide2.jpg" alt="Diana B." class="profile-img">

				<!-- Card Content -->
				<div class="profile-card-content">
					<h2 class="name">Hà Nguyễn</h2>

					<!-- Rating -->
					<div class="rating">
						⭐⭐⭐⭐⭐ <span class="review-count">(22)</span>
					</div>

					<!-- Location -->
					<div class="location">
						<img src="images/icons/location-icon.webp" style="width: 16px; height: 16px;">
						Nha Trang
					</div>

					<!-- Languages -->
					<div class="languages">
						<img src="images/icons/language-icon.PNG" style="width: 16px; height: 16px;">
						Việt Nam, English
					</div>
				</div>

				<!-- Action Buttons -->
				<div class="profile-card-footer">
					<button class="message-btn">💬 Gửi tin nhắn</button>
				</div>
			</div>
		</div>

	</section>

	<!-- Explore with us -->
	<section class="explore-with-us">
		<svg width="40" height="40" viewBox="0 0 40 40" fill="none">
			<path fill-rule="evenodd" clip-rule="evenodd"
				d="M19.75 1.5C29.8292 1.5 38 9.6708 38 19.75C38 29.8292 29.8292 38 19.75 38C9.6708 38 1.5 29.8292 1.5 19.75C1.5 9.6708 9.6708 1.5 19.75 1.5ZM39.5 19.75C39.5 8.84238 30.6576 0 19.75 0C8.84238 0 0 8.84238 0 19.75C0 30.6576 8.84238 39.5 19.75 39.5C30.6576 39.5 39.5 30.6576 39.5 19.75Z"
				fill="#35BD98"></path>
			<path fill-rule="evenodd" clip-rule="evenodd"
				d="M8.48138 8.47979C8.24933 8.71121 8.1944 9.0665 8.34575 9.35719L13.7649 19.7659C13.769 19.7737 13.7732 19.7815 13.7775 19.7892C15.1839 22.272 17.2498 24.3204 19.7494 25.7091L19.7656 25.7178L30.1412 31.1535C30.4316 31.3056 30.7871 31.2515 31.0191 31.0199C31.2511 30.7883 31.3058 30.4329 31.1542 30.1422L25.7846 19.8492C25.7811 19.8424 25.7775 19.8358 25.7738 19.8292C24.3341 17.2619 22.2175 15.1629 19.651 13.7409L19.6357 13.7327L9.35915 8.34656C9.06888 8.19442 8.71344 8.24837 8.48138 8.47979ZM18.9316 15.0572C21.2488 16.3428 23.1587 18.2369 24.46 20.5531L28.7174 28.7142L20.4699 24.3934C18.2186 23.1408 16.3577 21.2959 15.0892 19.0613L10.7802 10.7849L18.9316 15.0572Z"
				fill="#35BD98"></path>
			<path
				d="M19.7503 5.70657C19.294 5.70657 18.9242 5.33672 18.9242 4.88048C18.9242 4.42424 19.294 4.05439 19.7503 4.05439C20.2065 4.05439 20.5763 4.42424 20.5763 4.88048C20.5763 5.33672 20.2065 5.70657 19.7503 5.70657Z"
				fill="#35BD98"></path>
			<path
				d="M34.6196 20.5762C34.1634 20.5762 33.7935 20.2063 33.7935 19.7501C33.7935 19.2939 34.1634 18.924 34.6196 18.924C35.0758 18.924 35.4457 19.2939 35.4457 19.7501C35.4457 20.2063 35.0758 20.5762 34.6196 20.5762Z"
				fill="#35BD98"></path>
			<path
				d="M19.7503 35.4456C19.294 35.4456 18.9242 35.0757 18.9242 34.6195C18.9242 34.1633 19.294 33.7934 19.7503 33.7934C20.2065 33.7934 20.5763 34.1633 20.5763 34.6195C20.5763 35.0757 20.2065 35.4456 19.7503 35.4456Z"
				fill="#35BD98"></path>
			<path
				d="M4.88053 20.5762C4.42429 20.5762 4.05444 20.2063 4.05444 19.7501C4.05444 19.2939 4.42429 18.924 4.88053 18.924C5.33676 18.924 5.70662 19.2939 5.70662 19.7501C5.70662 20.2063 5.33676 20.5762 4.88053 20.5762Z"
				fill="#35BD98"></path>
		</svg>
		<h1>Khám phá cùng chúng tôi</h1>
		<img src="images/status/tour-guide.webp">
		<div class="guide-status">
			<div class="sologan">
				<h1>Chúng tôi ở đây</h1>
				<p><em>để đưa bạn đến những nơi bạn sẽ yêu thích</em></p>
			</div>
			<div class="stats">
				<div class="stat-item">Khách du lịch được phục vụ <span>100</span></div>
				<div class="stat-item">Hướng dẫn viên <span>20</span></div>
			</div>
		</div>

	</section>

	<!--.............................-->

	<!--What travellers say -->
	<section class="what-travellers-say">
		<svg width="41" height="41" viewBox="0 0 41 41" fill="none">
			<path fill-rule="evenodd" clip-rule="evenodd"
				d="M40.5 20.25C40.5 9.06839 31.4316 0 20.25 0C9.0684 0 0 9.06839 0 20.25C0 31.4316 9.0684 40.5 20.25 40.5C23.9227 40.5 27.3637 39.5057 30.3299 37.7918L39.2677 40.1862C39.5265 40.2555 39.8026 40.1815 39.9921 39.992C40.1816 39.8026 40.2556 39.5264 40.1862 39.2676L37.7918 30.3299C39.5057 27.3637 40.5 23.9227 40.5 20.25ZM20.25 1.5C30.6032 1.5 39 9.89682 39 20.25C39 23.7541 38.0225 27.0299 36.3426 29.8351C36.2383 30.0093 36.2091 30.2184 36.2616 30.4145L38.4012 38.4012L30.4145 36.2616C30.2184 36.2091 30.0093 36.2383 29.8351 36.3426C27.0299 38.0225 23.7541 39 20.25 39C9.89682 39 1.5 30.6032 1.5 20.25C1.5 9.89682 9.89682 1.5 20.25 1.5Z"
				fill="#35BD98"></path>
			<path fill-rule="evenodd" clip-rule="evenodd"
				d="M30.3262 16.0109C30.3262 15.5967 29.9904 15.2609 29.5762 15.2609H10.9241C10.5098 15.2609 10.1741 15.5967 10.1741 16.0109C10.1741 16.4251 10.5098 16.7609 10.9241 16.7609H29.5762C29.9904 16.7609 30.3262 16.4251 30.3262 16.0109Z"
				fill="#35BD98"></path>
			<path fill-rule="evenodd" clip-rule="evenodd"
				d="M30.3262 21.0978C30.3262 20.6836 29.9904 20.3478 29.5762 20.3478H10.9241C10.5098 20.3478 10.1741 20.6836 10.1741 21.0978C10.1741 21.512 10.5098 21.8478 10.9241 21.8478H29.5762C29.9904 21.8478 30.3262 21.512 30.3262 21.0978Z"
				fill="#35BD98"></path>
			<path fill-rule="evenodd" clip-rule="evenodd"
				d="M30.3262 26.1847C30.3262 25.7705 29.9904 25.4347 29.5762 25.4347H10.9241C10.5098 25.4347 10.1741 25.7705 10.1741 26.1847C10.1741 26.5989 10.5098 26.9347 10.9241 26.9347H29.5762C29.9904 26.9347 30.3262 26.5989 30.3262 26.1847Z"
				fill="#35BD98"></path>
		</svg>
		<h1>Du khách nói gì</h1>
		<div class="comments">
			<div class="comment-card">
				<img src="images/icons/qoutes-icon.png" style="width: 45px; height: 45px;">
				<!-- Card Content -->
				<div class="content">
					<p class="message">"Chuyến du lịch của tôi là một trong những trải nghiệm tuyệt vời nhất mà tôi từng
						có!"</p>

					<!-- Rating -->
					<div class="rating">
						⭐⭐⭐⭐⭐
					</div>

					<br>
					<b>YenNguyen</b>
				</div>
			</div>

			<div class="comment-card">
				<img src="images/icons/qoutes-icon.png" style="width: 45px; height: 45px;">
				<!-- Card Content -->
				<div class="content">
					<p class="message">"Chuyến du lịch của tôi là một trong những trải nghiệm tuyệt vời nhất mà tôi từng
						có!"</p>

					<!-- Rating -->
					<div class="rating">
						⭐⭐⭐⭐⭐
					</div>

					<br>
					<b>YenNguyen</b>
				</div>
			</div>

			<div class="comment-card">
				<img src="images/icons/qoutes-icon.png" style="width: 45px; height: 45px;">
				<!-- Card Content -->
				<div class="content">
					<p class="message">"Chuyến du lịch của tôi là một trong những trải nghiệm tuyệt vời nhất mà tôi từng
						có!"</p>

					<!-- Rating -->
					<div class="rating">
						⭐⭐⭐⭐⭐
					</div>

					<br>
					<b>YenNguyen</b>
				</div>
			</div>
		</div>

	</section>



	<footer class="footer">
		<div class="footer-info">
			<div class="footer-support">
				<h2>Support</h2>
				<br><a href="#">Câu hỏi thường gặp</a>
				<br><a href="#">Liên hệ với chúng tôi</a>
				<br><a href="#">Hỗ trợ chúng tôi</a>
			</div>
			<div class="footer-support">
				<h2>Về chúng tôi</h2>
				<br><a href="#">Blog</a>
			</div>

		</div>
		<br><br><br>
		<hr style="height: 1px; background-color: white; width: 50%; border: none;">
		<br>


		<p>&copy; 2025 Tourism Guide Connection Platform Company. All rights reserved.</p>
		<div>
			<a href="#">Privacy Policy</a> |
			<a href="#">Terms of Service</a> |
			<a href="#">Contact</a>
		</div>
		<div class="social-icons">
			<a href="#">🔵 Facebook</a>
			<a href="#">🔷 Twitter</a>
			<a href="#">📸 Instagram</a>
		</div>
	</footer>







</body>


</html>