function initCreateCardForm() {
    const form = document.getElementById("createCardForm");
    if (!form) {
        console.error("❌ Không tìm thấy form createCardForm");
        return;
    }

    form.addEventListener("submit", async function (e) {
        e.preventDefault();
        console.log("👉 Nút Create Card đã được bấm!");

        const token = localStorage.getItem("token");

        console.log("🔑 Token:", token);

        if (!token) {
            showNotify("Bạn chưa đăng nhập!", "Thông báo");
            return;
        }

        // ❌ Không cần gửi accountId nữa
        const cardData = {
            cardType: document.getElementById("cardType").value,
            expiryDate: document.getElementById("expiryDate").value
        };

        console.log("📤 Sending card data:", JSON.stringify(cardData));

        try {
            const response = await fetch("/api/card", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + token // ✅ quan trọng
                },
                body: JSON.stringify(cardData)
            });

            console.log("Response status:", response.status);

            if (response.ok) {
                showToast("✅ Tạo thẻ thành công!", "success");
                setTimeout(() => loadPage("/account"), 1000);
            } else {
                const errMsg = await response.text();
                showToast("❌ Lỗi: " + errMsg, "error");
            }
        } catch (err) {
            console.error(err);
            showToast("⚠️ Không thể kết nối server!", "error");
        }
    });
}

document.addEventListener("pageLoaded", (e) => {
    if (e.detail.includes("/createCard")) {
        console.log("📌 CreateCard page loaded");
        initCreateCardForm();
    }
});
