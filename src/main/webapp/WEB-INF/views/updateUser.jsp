<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Update User</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 flex items-center justify-center h-screen">

<div class="bg-white shadow-lg rounded-lg p-8 w-full max-w-lg">
    <h2 class="text-3xl font-bold mb-6 text-center">Update User</h2>

    <form id="updateUserForm" class="space-y-5">
        <!-- ID (ẩn) -->
        <input type="hidden" id="id" name="id">

        <!-- Account ID -->
        <div>
            <label class="block text-gray-700 font-medium mb-1">Account ID</label>
            <input type="text" id="accountId" name="accountId"
                   class="w-full px-3 py-2 border rounded bg-gray-100" readonly>
        </div>

        <!-- Customer Name -->
        <div>
            <label class="block text-gray-700 font-medium mb-1">Customer Name</label>
            <input type="text" id="customerName" name="customerName"
                   class="w-full px-3 py-2 border rounded">
        </div>

        <!-- Email -->
        <div>
            <label class="block text-gray-700 font-medium mb-1">Email</label>
            <input type="email" id="email" name="email"
                   class="w-full px-3 py-2 border rounded bg-gray-100" readonly>
        </div>

        <!-- Phone -->
        <div>
            <label class="block text-gray-700 font-medium mb-1">Phone Number</label>
            <input type="text" id="phoneNumber" name="phoneNumber"
                   class="w-full px-3 py-2 border rounded">
        </div>

        <!-- User Level -->
        <div>
            <label class="block text-gray-700 font-medium mb-1">User Level</label>
            <select id="userLevel" name="userLevel"
                    class="w-full px-3 py-2 border rounded">
                <!-- option sẽ load bằng JS -->
            </select>
        </div>

        <!-- Nút submit -->
        <div class="flex justify-end space-x-3">
            <button type="button"
                    onclick="history.back()"
                    class="bg-gray-500 text-white px-4 py-2 rounded hover:bg-gray-600">
                Cancel
            </button>
            <button type="submit"
                    class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700">
                Save
            </button>
        </div>
    </form>
</div>
<script src="/assets/js/updateUser.js"></script>
</body>
</html>
