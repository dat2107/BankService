document.addEventListener("pageLoaded", async (e) => {
    // chỉ chạy khi load cardDetail.jsp
    if (!e.detail.includes("cardDetail")) return;

    const params = new URLSearchParams(window.location.search);
    const cardId = params.get("id");
    const token = localStorage.getItem("token");

    if (!token) {
        alert("Bạn chưa đăng nhập!");
        return;
    }

    try {
        // Load card detail
        const res = await fetch(`/api/card/${cardId}`, {
            headers: { "Authorization": "Bearer " + token }
        });

        if (!res.ok) {
            alert("Không tìm thấy thẻ");
            return;
        }

        const card = await res.json();

        document.getElementById("cardId").innerText = card.cardId;
        document.getElementById("cardNumber").innerText = card.cardNumber;
        document.getElementById("cardType").innerText = card.cardType;
        document.getElementById("expiryDate").innerText = card.expiryDate;
        document.getElementById("status").innerText = card.status;
        document.getElementById("userName").innerText = card.account.customerName;
        document.getElementById("userEmail").innerText = card.account.email;

        // Load balance
        const balRes = await fetch(`/api/balance/${card.account.accountId}`, {
            headers: { "Authorization": "Bearer " + token }
        });
        const balance = await balRes.json();

        document.getElementById("balanceId").innerText = balance.balanceId;
        document.getElementById("availableBalance").innerText = balance.availableBalance;
        document.getElementById("holdBalance").innerText = balance.holdBalance;
        document.getElementById("lastUpdated").innerText = balance.lastUpdated;
    } catch (err) {
        console.error("Error loading card details", err);
    }
});

// Back
function backToUser() {
    window.location.href = "/user";
}

// Deposit
async function deposit() {
    const amount = document.getElementById("depositAmount").value;
    if (!amount) return alert("Nhập số tiền");
    const token = localStorage.getItem("token");

    const res = await fetch("/api/balance/deposit", {
        method: "POST",
        headers: { "Authorization": "Bearer " + token, "Content-Type": "application/json" },
        body: JSON.stringify({ amount })
    });

    if (res.ok) {
        alert("Nạp tiền thành công");
        location.reload();
    } else {
        alert("Nạp tiền thất bại");
    }
}

// Withdraw
async function withdraw() {
    const amount = document.getElementById("withdrawAmount").value;
    if (!amount) return alert("Nhập số tiền");
    const token = localStorage.getItem("token");

    const res = await fetch("/api/balance/withdraw", {
        method: "POST",
        headers: { "Authorization": "Bearer " + token, "Content-Type": "application/json" },
        body: JSON.stringify({ amount })
    });

    if (res.ok) {
        alert("Rút tiền thành công");
        location.reload();
    } else {
        alert("Rút tiền thất bại");
    }
}
