<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
                    <a href="#" class="flex items-center space-x-3 text-gray-700 p-3 rounded-lg hover:bg-blue-50 hover:text-blue-600 transition-colors duration-200 bg-blue-50 text-blue-600">
                        <span>📊</span>
                        <span class="font-medium">Dashboard</span>
                    </a>
                </li>
                <li>
                    <a href="#" class="flex items-center space-x-3 text-gray-700 p-3 rounded-lg hover:bg-blue-50 hover:text-blue-600 transition-colors duration-200">
                        <span>👥</span>
                        <span class="font-medium">Quản lý người dùng</span>
                    </a>
                </li>
                <li>
                    <a href="#" class="flex items-center space-x-3 text-gray-700 p-3 rounded-lg hover:bg-blue-50 hover:text-blue-600 transition-colors duration-200">
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
                    <a href="#" class="flex items-center space-x-3 text-gray-700 p-3 rounded-lg hover:bg-blue-50 hover:text-blue-600 transition-colors duration-200">
                        <span>🔐</span>
                        <span class="font-medium">Phân quyền</span>
                    </a>
                </li>
            </ul>
        </div>
    </nav>

    <!-- Main Content -->
    <main class="flex-1 p-8">
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
            <div class="p-6 border-b border-gray-200">
                <div class="flex justify-between items-center">
                    <h4 class="text-xl font-semibold text-gray-800">Bảng người dùng</h4>
                    <button class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium transition-colors duration-200 flex items-center space-x-2">
                        <span>+</span>
                        <span>Tạo người dùng</span>
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
                    <tbody class="bg-white divide-y divide-gray-200">
                    <tr class="hover:bg-gray-50 transition-colors duration-200">
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">1</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">admin</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">admin@gmail.com</td>
                        <td class="px-6 py-4 whitespace-nowrap">
                                    <span class="inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-purple-100 text-purple-800">
                                        ADMIN
                                    </span>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium space-x-2">
                            <button class="bg-blue-500 hover:bg-blue-600 text-white px-3 py-1 rounded text-xs transition-colors duration-200">Xem</button>
                            <button class="bg-yellow-500 hover:bg-yellow-600 text-white px-3 py-1 rounded text-xs transition-colors duration-200">Sửa</button>
                            <button class="bg-red-500 hover:bg-red-600 text-white px-3 py-1 rounded text-xs transition-colors duration-200">Xóa</button>
                        </td>
                    </tr>
                    <tr class="hover:bg-gray-50 transition-colors duration-200">
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">2</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">user1</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">user1@mail.com</td>
                        <td class="px-6 py-4 whitespace-nowrap">
                                    <span class="inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-green-100 text-green-800">
                                        CUSTOMER
                                    </span>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium space-x-2">
                            <button class="bg-blue-500 hover:bg-blue-600 text-white px-3 py-1 rounded text-xs transition-colors duration-200">Xem</button>
                            <button class="bg-yellow-500 hover:bg-yellow-600 text-white px-3 py-1 rounded text-xs transition-colors duration-200">Sửa</button>
                            <button class="bg-red-500 hover:bg-red-600 text-white px-3 py-1 rounded text-xs transition-colors duration-200">Xóa</button>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</div>

</body>
</html>

