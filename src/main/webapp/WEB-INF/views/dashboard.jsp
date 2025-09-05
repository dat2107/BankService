<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="true" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard</title>
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
<body class="bg-gray-50 font-sans">
<!-- Header -->
<header class="bg-gradient-to-r from-blue-800 to-blue-900 text-white shadow-lg">
    <div class="flex justify-between items-center px-6 py-4">
        <h2 class="text-2xl font-bold">🏦 Bank Admin</h2>
        <div class="flex items-center space-x-4">
            <span class="text-blue-100">Chào mừng, Admin</span>
            <div class="w-8 h-8 bg-blue-600 rounded-full flex items-center justify-center">
                <span class="text-sm font-semibold">A</span>
            </div>
        </div>
    </div>
</header>

<div class="flex">
    <!-- Sidebar -->
    <nav class="w-64 bg-white shadow-lg min-h-screen">
        <div class="p-6">
            <ul class="space-y-2">
                <li>
                    <a href="/dashboard"
                       onclick="goDashboard(event)"
                       class="flex items-center space-x-3 text-gray-700 p-3 rounded-lg hover:bg-blue-50 hover:text-blue-600 transition-colors duration-200">
                        <span>📊</span>
                        <span class="font-medium">Dashboard</span>
                    </a>
                </li>
                <li>
                    <a href="user" onclick="navigate(event, 'user')"
                       class="flex items-center space-x-3 text-gray-700 p-3 rounded-lg hover:bg-blue-50 hover:text-blue-600 transition-colors duration-200">
                        <span>👥</span>
                        <span class="font-medium">Quản lý người dùng</span>
                    </a>
                </li>
                <li>
                    <a href="cardManager" onclick="navigate(event, '/cardManager')"
                       class="flex items-center space-x-3 text-gray-700 p-3 rounded-lg hover:bg-blue-50 hover:text-blue-600 transition-colors duration-200">
                        <span>💳</span>
                        <span class="font-medium">Thẻ ngân hàng</span>
                    </a>
                </li>
                <li>
                    <a href="#" class="flex items-center space-x-3 text-gray-700 p-3 rounded-lg hover:bg-blue-50 hover:text-blue-600 transition-colors duration-200">
                        <span>💰</span>
                        <span class="font-medium">Giao dịch</span>
                    </a>
                </li>
                <li>
                    <a href="user-level" onclick="navigate(event, 'user-level')"
                       class="flex items-center space-x-3 text-gray-700 p-3 rounded-lg hover:bg-blue-50 hover:text-blue-600 transition-colors duration-200">
                        <span>🔐</span>
                        <span class="font-medium">Phân cấp người dùng</span>
                    </a>
                </li>
            </ul>
        </div>
    </nav>

    <!-- Main Content -->
    <main id="mainContent" class="flex-1 p-8">
        <div class="mb-8">
            <h3 class="text-3xl font-bold text-gray-800 mb-2">Dashboard Quản trị</h3>
            <p class="text-gray-600">Tổng quan hệ thống ngân hàng</p>
        </div>

        <!-- Stats Cards -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
            <div class="bg-gradient-to-br from-blue-500 to-blue-600 p-6 rounded-xl text-white shadow-lg hover:shadow-xl transition-shadow duration-300">
                <div class="flex items-center justify-between">
                    <div>
                        <p class="text-blue-100 text-sm font-medium">Người dùng</p>
                        <p class="text-3xl font-bold">1,234</p>
                    </div>
                    <div class="text-4xl opacity-80">👥</div>
                </div>
            </div>

            <div class="bg-gradient-to-br from-orange-500 to-orange-600 p-6 rounded-xl text-white shadow-lg hover:shadow-xl transition-shadow duration-300">
                <div class="flex items-center justify-between">
                    <div>
                        <p class="text-orange-100 text-sm font-medium">Thẻ ngân hàng</p>
                        <p class="text-3xl font-bold">856</p>
                    </div>
                    <div class="text-4xl opacity-80">💳</div>
                </div>
            </div>

            <div class="bg-gradient-to-br from-green-500 to-green-600 p-6 rounded-xl text-white shadow-lg hover:shadow-xl transition-shadow duration-300">
                <div class="flex items-center justify-between">
                    <div>
                        <p class="text-green-100 text-sm font-medium">Giao dịch</p>
                        <p class="text-3xl font-bold">2,567</p>
                    </div>
                    <div class="text-4xl opacity-80">💰</div>
                </div>
            </div>

            <div class="bg-gradient-to-br from-red-500 to-red-600 p-6 rounded-xl text-white shadow-lg hover:shadow-xl transition-shadow duration-300">
                <div class="flex items-center justify-between">
                    <div>
                        <p class="text-red-100 text-sm font-medium">Cảnh báo</p>
                        <p class="text-3xl font-bold">12</p>
                    </div>
                    <div class="text-4xl opacity-80">⚠️</div>
                </div>
            </div>
        </div>

        <!-- User Table -->
        <div class="bg-white rounded-xl shadow-lg overflow-hidden">
            <div class="p-6 border-b border-gray-200 flex justify-between items-center">
                <h4 class="text-xl font-semibold text-gray-800">Bảng người dùng</h4>
                <div class="flex space-x-2">
                    <!-- Thanh tìm kiếm -->
                    <input id="searchInput" type="text" placeholder="Tìm kiếm người dùng..."
                           class="border rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none">
                    <button onclick="searchUsers()"
                            class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium transition-colors duration-200">
                        🔍 Tìm kiếm
                    </button>
                    <button class="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg font-medium transition-colors duration-200">
                        + Tạo người dùng
                    </button>
                </div>
            </div>


            <div class="overflow-x-auto">
                <table class="w-full">
                    <thead class="bg-gray-50">
                    <tr>
                        <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                        <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Tên đăng nhập</th>
                        <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Email</th>
                        <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Vai trò</th>
                        <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Thao tác</th>
                    </tr>
                    </thead>
                    <tbody  id="userTableBody"  class="bg-white divide-y divide-gray-200">

                    </tbody>
                </table>
            </div>
        </div>
    </main>
</div>

</body>
<script src="/assets/js/dashboard.js"></script>
<script src="/assets/js/user.js"></script>
<script src="/assets/js/updateUser.js"></script>
<script src="/assets/js/userDetail.js"></script>
<script src="/assets/js/cardManager.js"></script>
<script src="/assets/js/cardDetail.js"></script>
<script src="/assets/js/user-level.js"></script>
<script src="/assets/js/vip-detail.js"></script>
<script src="/assets/js/userDetail.js"></script>
</html>

