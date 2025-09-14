function initCreateCardForm() {
    const form = document.getElementById("createCardForm");
    if (!form) {
        console.error("❌ Không tìm thấy form createCardForm");
        return;
    }

    const expiryDateInput = document.getElementById("expiryDate");
    if (expiryDateInput) {
        const today = new Date().toISOString().split("T")[0];
        expiryDateInput.setAttribute("min", today);
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

        const params = new URLSearchParams(window.location.search);
        const accountId = params.get("accountId");


        const cardData = {
            cardType: document.getElementById("cardType").value,
            expiryDate: document.getElementById("expiryDate").value
        };

        if (accountId) {
            cardData.accountId = accountId; // ✅ Admin tạo thẻ cho user
        }

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
                setTimeout(() => {
                    if (accountId) {
                        history.back();
                    } else {
                        // User tự tạo thẻ
                        loadPage("/account");
                    }
                }, 1000);
            }
        } catch (err) {
            console.error(err);
            showToast("⚠️ Không thể kết nối server!", "error");
        }
    });
}

function onCreateCardPage(url) {
    console.log("📌 Checking page:", url);
    if (url.includes("/createCard")) {
        console.log("📌 CreateCard page loaded");
        initCreateCardForm();
    }
}

// Khi reload trực tiếp URL
document.addEventListener("DOMContentLoaded", () => {
    onCreateCardPage(window.location.pathname + window.location.search);
});

// Khi load qua SPA
document.addEventListener("pageLoaded", (e) => {
    onCreateCardPage(e.detail); // e.detail chính là url truyền trong loadPage
});

