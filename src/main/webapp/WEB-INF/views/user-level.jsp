<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="true" %>
<html>
<head>
    <title>Danh Sách Người Dùng</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100">

<div class="max-w-6xl mx-auto mt-10 bg-white shadow-md rounded-lg p-6">
    <h2 class="text-2xl font-bold mb-6">Danh Sách Người Dùng</h2>

    <!-- Bộ lọc -->
    <form method="get" action="user-list" class="mb-6">
        <div class="flex flex-col md:flex-row md:items-end gap-4">
            <div class="flex-1">
                <label class="block text-gray-700 mb-2">Chọn Loại Người Dùng:</label>
                <select id="loaiNguoiDung"
                        name="loaiNguoiDung"
                        class="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500">
                    <option value="">-- Tất cả --</option>
                </select>
            </div>
            <div class="flex gap-3">
                <button type="submit" class="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700">Lọc</button>
                <a href="/vip-detail"
                   onclick="navigate(event, '/vip-detail')"
                   class="bg-cyan-500 text-white px-4 py-2 rounded-lg hover:bg-cyan-600">
                    Detail VIP
                </a>
            </div>
        </div>
    </form>

    <!-- Bảng danh sách -->
    <div class="overflow-x-auto">
        <table class="min-w-full border border-gray-200 text-gray-700">
            <thead class="bg-gray-100">
            <tr>
                <th class="px-4 py-2 border">ID</th>
                <th class="px-4 py-2 border">Họ Tên</th>
                <th class="px-4 py-2 border">Email</th>
                <th class="px-4 py-2 border">Số Điện Thoại</th>
                <th class="px-4 py-2 border">Tên Người Dùng</th>
                <th class="px-4 py-2 border">Vai Trò</th>
                <th class="px-4 py-2 border">Loại Người Dùng</th>
            </tr>
            </thead>
            <tbody id="userTableBody"></tbody>

            </tbody>
        </table>
    </div>
</div>
<script src="/assets/js/user-level.js"></script>
</body>
</html>
