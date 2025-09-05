<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Card Details</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 p-6">

<div class="max-w-6xl mx-auto bg-white shadow rounded-lg p-6">
    <h2 class="text-2xl font-bold mb-6">Card Details</h2>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <!-- Card Info -->
        <div class="border rounded-lg p-4">
            <h3 class="font-semibold mb-3">Card Information</h3>
            <p><b>Card ID:</b> <span id="cardId"></span></p>
            <p><b>Card Number:</b> <span id="cardNumber"></span></p>
            <p><b>Card Type:</b> <span id="cardType"></span></p>
            <p><b>Expiry Date:</b> <span id="expiryDate"></span></p>
            <p><b>Status:</b> <span id="status"></span></p>
            <p><b>User Name:</b> <span id="userName"></span></p>
            <p><b>User Email:</b> <span id="userEmail"></span></p>
            <button onclick="backToUser()" class="mt-4 bg-blue-600 text-white px-4 py-2 rounded">Back to User</button>
        </div>

        <!-- Balance Info -->
        <div class="border rounded-lg p-4">
            <h3 class="font-semibold mb-3">Balance Information</h3>
            <p><b>Balance ID:</b> <span id="balanceId"></span></p>
            <p><b>Available Balance:</b> <span id="availableBalance"></span></p>
            <p><b>Hold Balance:</b> <span id="holdBalance"></span></p>
            <p><b>Last Updated:</b> <span id="lastUpdated"></span></p>

            <!-- Deposit -->
            <div class="mt-4">
                <label>Deposit Amount:</label>
                <input type="number" id="depositAmount" class="border px-2 py-1 w-full rounded">
                <button onclick="deposit()" class="mt-2 bg-green-600 text-white px-4 py-1 rounded">Deposit</button>
            </div>

            <!-- Withdraw -->
            <div class="mt-4">
                <label>Withdraw Amount:</label>
                <input type="number" id="withdrawAmount" class="border px-2 py-1 w-full rounded">
                <button onclick="withdraw()" class="mt-2 bg-red-600 text-white px-4 py-1 rounded">Withdraw</button>
            </div>
        </div>
    </div>
</div>

<script src="/assets/js/cardDetail.js"></script>
</body>
</html>
