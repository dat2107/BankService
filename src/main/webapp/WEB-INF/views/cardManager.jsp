<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Manager Card</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 p-6">
<div class="max-w-7xl mx-auto">
    <!-- Breadcrumb -->
    <nav class="mb-6 text-sm text-gray-600">
        <a href="/dashboard" class="text-blue-600 hover:underline">Dashboard</a> /
        <span class="text-gray-800 font-semibold">Card</span>
    </nav>

    <!-- Title -->
    <h1 class="text-3xl font-bold mb-6">Manager Card</h1>

    <!-- Search -->
    <div class="mb-4 flex justify-between items-center">
        <input type="text" id="searchCard" placeholder="Search by numberCard"
               class="border rounded px-4 py-2 w-1/3 focus:ring focus:ring-blue-200 focus:outline-none">
        <button onclick="loadCards()" class="ml-2 bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700">Search</button>
    </div>

    <!-- Table -->
    <div class="bg-white shadow rounded-lg overflow-hidden">
        <table class="w-full text-left border-collapse">
            <thead class="bg-gray-50 border-b">
            <tr>
                <th class="px-4 py-2 border">ID</th>
                <th class="px-4 py-2 border">User name</th>
                <th class="px-4 py-2 border">Card number</th>
                <th class="px-4 py-2 border">Card type</th>
                <th class="px-4 py-2 border">Status</th>
                <th class="px-4 py-2 border text-center">Actions</th>
            </tr>
            </thead>
            <tbody id="cardTable" class="divide-y divide-gray-200">
            <!-- Cards render here -->
            </tbody>
        </table>
    </div>

    <!-- Pagination -->
    <div class="flex justify-center mt-4 space-x-2">
        <button id="prevPage" class="px-3 py-1 border rounded hover:bg-gray-100">&laquo;</button>
        <button id="nextPage" class="px-3 py-1 border rounded hover:bg-gray-100">&raquo;</button>
    </div>
</div>

<script src="/assets/js/cardManager.js"></script>
</body>
</html>
