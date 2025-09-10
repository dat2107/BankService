document.addEventListener("pageLoaded", async (e) => {
    if (!e.detail.includes("transfer")) return;

    const params = new URLSearchParams(window.location.search);
    const cardId = params.get("cardId");

    const token = localStorage.getItem("token");
    if (!token) {
        alert("Bạn chưa đăng nhập!");
        return;
    }

    try {
        // Load thẻ nguồn
        const res = await fetch(`/api/card/${cardId}`, {
            headers: { "Authorization": "Bearer " + token }
        });

        if (!res.ok) {
            alert("Không tìm thấy thẻ");
            return;
        }

        const card = await res.json();
        document.getElementById("fromCardNumber").innerText = card.cardNumber;
        document.getElementById("fromCardType").innerText = card.cardType;
        document.getElementById("fromBalance").innerText = card.account.balance.availableBalance;
        document.getElementById("fromHold").innerText = card.account.balance.holdBalance;

        // B1: tìm kiếm người nhận
        document.getElementById("searchReceiverForm").addEventListener("submit", async function (e) {
            e.preventDefault();
            const toCardNumber = document.getElementById("toCardNumber").value;

            const resReceiver = await fetch(`/api/card/number/${toCardNumber}`, {
                headers: { "Authorization": "Bearer " + token }
            });

            if (!resReceiver.ok) {
                alert("Không tìm thấy người nhận!");
                return;
            }

            const receiverCard = await resReceiver.json();
            document.getElementById("receiverCard").innerText = receiverCard.cardNumber;
            document.getElementById("receiverName").innerText = receiverCard.account.customerName;

            document.getElementById("receiverInfo").classList.remove("hidden");
        });

        // B2: gửi yêu cầu transfer -> sinh OTP
        document.getElementById("transferForm").addEventListener("submit", async function (e) {
            e.preventDefault();

            const payload = {
                fromCardId: card.cardId,
                toCardNumber: document.getElementById("receiverCard").innerText,
                amount: document.getElementById("amount").value,
                email: card.account.email
            };

            const resReq = await fetch("/api/transfer/request", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + token
                },
                body: JSON.stringify(payload)
            });

            if (!resReq.ok) {
                alert("Không tạo được giao dịch!");
                return;
            }

            const result = await resReq.json();
            alert("Mã OTP đã được gửi đến email của bạn. Vui lòng nhập OTP để xác nhận.");

            document.getElementById("transactionId").value = result.transactionId;
            document.getElementById("otpSection").classList.remove("hidden");
        });

        // B3: xác nhận OTP
        document.getElementById("otpForm").addEventListener("submit", async function (e) {
            e.preventDefault();

            const payload = {
                transactionId: document.getElementById("transactionId").value,
                otp: document.getElementById("otpCode").value
            };

            const resConfirm = await fetch("/api/transfer/confirm", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + token
                },
                body: JSON.stringify(payload)
            });

            if (!resConfirm.ok) {
                alert("Xác nhận OTP thất bại!");
                return;
            }

            const result = await resConfirm.json();
            alert("OTP hợp lệ! Giao dịch đang chờ Admin duyệt. Mã giao dịch: " + result.transactionId);
            window.location.href = "/home";
        });

    } catch (err) {
        console.error("Lỗi khi load thẻ:", err);
    }
});
