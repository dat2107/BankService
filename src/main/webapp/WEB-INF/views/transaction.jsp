<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Transactions List</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100">

<div class="max-w-6xl mx-auto mt-10 bg-white shadow-md rounded-lg p-6">
    <div class="flex justify-between items-center mb-6">
        <h2 class="text-2xl font-bold">Table Transactions</h2>
    </div>

    <!-- Bảng danh sách transaction -->
    <div class="overflow-x-auto">
        <table class="min-w-full border border-gray-200 text-gray-700">
            <thead>
            <tr class="bg-gray-100">
                <th class="px-4 py-2 border">ID</th>
                <th class="px-4 py-2 border">FromCardNumber</th>
                <th class="px-4 py-2 border">ToCardNumber</th>
                <th class="px-4 py-2 border">Amount</th>
                <th class="px-4 py-2 border">Transaction Type</th>
                <th class="px-4 py-2 border">Status</th>
                <th class="px-4 py-2 border">Actions</th>
            </tr>
            </thead>
            <tbody id="transactionTable">
            <!-- Render từ JS -->
            </tbody>
        </table>
    </div>

    <!-- Pagination -->
    <div class="flex justify-center mt-4 space-x-2">
        <button id="prevPage" class="px-3 py-1 border rounded bg-gray-200">«</button>
        <span id="currentPage" class="px-3 py-1 border rounded bg-blue-500 text-white">1</span>
        <button id="nextPage" class="px-3 py-1 border rounded bg-gray-200">»</button>
    </div>
</div>

<script src="/assets/js/transaction.js"></script>

</body>
</html>
