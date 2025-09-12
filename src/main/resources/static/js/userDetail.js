document.addEventListener("pageLoaded", async (e) => {
    if (!e.detail.includes("userDetail")) return;

    const params = new URLSearchParams(window.location.search);
    const accountId = params.get("id");

    const token = localStorage.getItem("token");
    if (!token) {
        showNotify("Bạn chưa đăng nhập!", "Thông báo");
        return;
    }

    try {
        const res = await fetch(`/api/account/${accountId}`, {
            headers: { "Authorization": "Bearer " + token }
        });

        if (!res.ok) {
            console.error("Failed to fetch account", res.status);
            showToast("Không thể tải chi tiết tài khoản!", "error");
            return;
        }

        const account = await res.json();
        renderUserDetail(account);
    } catch (err) {
        console.error("Error loading account detail:", err);
        showToast("Có lỗi khi tải chi tiết tài khoản!", "error");
    }
});

function renderUserDetail(account) {
    // Fill user info
    document.getElementById("userId").innerText = account.accountId;
    document.getElementById("userEmail").innerText = account.email;
    document.getElementById("userName").innerText = account.customerName;
    document.getElementById("userPhone").innerText = account.phoneNumber;

    // Render cards
    const container = document.getElementById("cardsContainer");
    container.innerHTML = account.cards.map(card => `
        <div class="bg-gray-50 border rounded-lg p-4 shadow hover:shadow-md transition">
            <p><span class="font-semibold">Card Number:</span> ${card.cardId}</p>
            <p><span class="font-semibold">Expiry Date:</span> ${card.expiryDate}</p>
            <p><span class="font-semibold">Type:</span> ${card.cardType}</p>
            <p><span class="font-semibold">Status:</span> ${card.status}</p>
            <p><span class="font-semibold">Available Balance:</span> ${account.balance?.availableBalance || 0}</p>
            <button onclick="viewCard(${card.cardId})" 
            class="mt-3 bg-gray-600 text-white px-3 py-1 rounded hover:bg-gray-700">
                View Details
            </button>
        </div>
    `).join("");
}
