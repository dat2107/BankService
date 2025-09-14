let card = null;
document.addEventListener("pageLoaded", async (e) => {
    // chỉ chạy khi load cardDetail.jsp
    if (!e.detail.includes("cardDetail")) return;

    const params = new URLSearchParams(window.location.search);
    const cardId = params.get("id");
    const token = localStorage.getItem("token");

    if (!token) {
        showNotify("Bạn chưa đăng nhập!", "Thông báo");
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

        card = await res.json();

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
    } catch (err) {
        console.error("Error loading card details", err);
        showToast("Lỗi khi tải chi tiết thẻ!", "error");
    }
});

// Back
function backToUser() {
    navigate(event, "/user");
}

// Deposit
async function deposit() {
    const amount = document.getElementById("depositAmount").value;
    if (!amount) return showNotify("Vui lòng nhập số tiền", "Thông báo");
    const token = localStorage.getItem("token");

    const res = await fetch(`/api/balance/${card.account.accountId}/deposit?amount=${amount}&toCardId=${card.cardId}`, {
        method: "POST",
        headers: { "Authorization": "Bearer " + token }
    });


    if (res.ok) {
        showToast("Nạp tiền thành công!", "success");
        await loadCardDetail(card.cardId, token);
    } else {
        showToast("Nạp tiền thất bại!", "error");
    }
}

// Withdraw
async function withdraw() {
    const amount = document.getElementById("withdrawAmount").value;
    if (!amount) return showNotify("Vui lòng nhập số tiền", "Thông báo");
    const token = localStorage.getItem("token");

    const res = await fetch(`/api/balance/${card.account.accountId}/withdraw?amount=${amount}&fromCardId=${card.cardId}`, {
        method: "POST",
        headers: { "Authorization": "Bearer " + token }
    });


    if (res.ok) {
        showToast("Rút tiền thành công!", "success");
        await loadCardDetail(card.cardId, token);
    } else {
        showToast("Rút tiền thất bại!", "error");
    }
}
