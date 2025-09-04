<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="bg-white shadow-md rounded-lg p-8 max-w-xl mx-auto mt-10">
    <h2 class="text-2xl font-bold text-center mb-6">Create New Card</h2>

    <form id="createCardForm" method="post" onsubmit="return false;" class="space-y-4">
    <!-- Expiry Date -->
        <div>
            <label for="expiryDate" class="block font-medium text-gray-700 mb-1">Expiry Date:</label>
            <input type="date" id="expiryDate" name="expiryDate"
                   class="w-full border rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"/>
        </div>

        <!-- Card Type -->
        <div>
            <label for="cardType" class="block font-medium text-gray-700 mb-1">Card Type:</label>
            <select id="cardType" name="cardType"
                    class="w-full border rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500">
                <option value="DEBIT">DEBIT</option>
                <option value="CREDIT">CREDIT</option>
            </select>
        </div>

        <!-- Status -->
        <div>
            <label for="status" class="block font-medium text-gray-700 mb-1">Status:</label>
            <select id="status" name="status"
                    class="w-full border rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500">
                <option value="ACTIVE">ACTIVE</option>
                <option value="INACTIVE">INACTIVE</option>
            </select>
        </div>

        <!-- Submit -->
        <div class="pt-4">
            <button type="submit"
                    class="w-full bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded-lg">
                Create Card
            </button>
        </div>
    </form>
</div>
<script src="/assets/js/createCard.js"></script>

