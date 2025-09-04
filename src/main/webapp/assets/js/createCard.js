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
            alert("Bạn chưa đăng nhập!");
            return;
        }

        // ❌ Không cần gửi accountId nữa
        const cardData = {
            cardType: document.getElementById("cardType").value,
            expiryDate: document.getElementById("expiryDate").value, // input type="date" => "2025-08-07"
            status: document.getElementById("status").value
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
                alert("✅ Tạo thẻ thành công!");
                loadPage("/account");
            } else {
                const errMsg = await response.text();
                alert("❌ Lỗi: " + errMsg);
            }
        } catch (err) {
            console.error(err);
            alert("⚠️ Không thể kết nối server!");
        }
    });
}

document.addEventListener("pageLoaded", (e) => {
    if (e.detail.includes("/createCard")) {
        console.log("📌 CreateCard page loaded");
        initCreateCardForm();
    }
});
