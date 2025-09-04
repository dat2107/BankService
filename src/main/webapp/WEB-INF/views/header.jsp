<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Home - MyBank</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script>
        tailwind.config = {
            theme: {
                extend: {
                    colors: {
                        primary: '#1e40af',
                        secondary: '#64748b',
                        accent: '#0ea5e9'
                    }
                }
            }
        }
    </script>
</head>
<body class="bg-gradient-to-br from-blue-50 to-indigo-100 min-h-screen font-sans">
<!-- Header -->
<header class="bg-gradient-to-r from-blue-800 to-blue-900 text-white shadow-lg">
    <div class="container mx-auto px-6 py-4">
        <div class="flex justify-between items-center">
            <div class="flex items-center space-x-2">
                <span class="text-2xl">🏦</span>
                <strong class="text-2xl font-bold">MyBank</strong>
            </div>
            <nav class="hidden md:flex space-x-6">
                <a href="javascript:void(0)" onclick="loadPage('/home')"
                   class="px-3 py-2 rounded-lg bg-blue-700 hover:bg-blue-600">Trang Chủ</a>
                <a href="javascript:void(0)" onclick="loadPage('/account')"
                   class="px-3 py-2 rounded-lg hover:bg-blue-700">Tài Khoản</a>
                <a href="javascript:void(0)" onclick="loadPage('/history')"
                   class="px-3 py-2 rounded-lg hover:bg-blue-700">Lịch Sử</a>
                <a href="javascript:void(0)" onclick="loadPage('/services')"
                   class="px-3 py-2 rounded-lg hover:bg-blue-700">Dịch Vụ</a>
                <a href="javascript:void(0)" onclick="loadPage('/contact')"
                   class="px-3 py-2 rounded-lg hover:bg-blue-700">Liên Hệ</a>
                <a href="#" onclick="logout()"
                   class="px-3 py-2 rounded-lg hover:bg-red-600">Đăng Xuất</a>
            </nav>
            <!-- Mobile menu button -->
            <button class="md:hidden text-white" onclick="toggleMobileMenu()">
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"></path>
                </svg>
            </button>
        </div>
        <!-- Mobile menu -->
        <div id="mobileMenu" class="hidden md:hidden mt-4 space-y-2">
            <a href="/home" class="block hover:text-blue-200 transition-colors duration-200 px-3 py-2 rounded-lg hover:bg-blue-700 bg-blue-700">Trang Chủ</a>
            <a href="/account" class="block hover:text-blue-200 transition-colors duration-200 px-3 py-2 rounded-lg hover:bg-blue-700">Tài Khoản</a>
            <a href="/history" class="block hover:text-blue-200 transition-colors duration-200 px-3 py-2 rounded-lg hover:bg-blue-700">Lịch Sử</a>
            <a href="/services" class="block hover:text-blue-200 transition-colors duration-200 px-3 py-2 rounded-lg hover:bg-blue-700">Dịch Vụ</a>
            <a href="/contact" class="block hover:text-blue-200 transition-colors duration-200 px-3 py-2 rounded-lg hover:bg-blue-700">Liên Hệ</a>
            <a href="#" onclick="logout()" class="block hover:text-red-200 transition-colors duration-200 px-3 py-2 rounded-lg hover:bg-red-600">Đăng Xuất</a>
        </div>
    </div>
</header>

<!-- Welcome Section -->
<main id="mainContent" class="container mx-auto px-6 py-16">
    <h2>Chào mừng bạn đến MyBank!</h2>
</main>
<script>
    async function loadPage(url) {
        try {
            const response = await fetch(url, {
                headers: { "X-Requested-With": "XMLHttpRequest" }
            });
            const html = await response.text();
            document.getElementById("mainContent").innerHTML = html;
        } catch (e) {
            document.getElementById("mainContent").innerHTML = "<p class='text-red-600'>Lỗi tải trang!</p>";
        }
    }
</script>

</body>
</html>
