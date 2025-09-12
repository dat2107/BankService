document.addEventListener("pageLoaded", async (e) => {
    if (!e.detail.includes("transfer")) return;

    const params = new URLSearchParams(window.location.search);
    const cardId = params.get("cardId");

    const token = localStorage.getItem("token");
    if (!token) {
        showNotify("Bạn chưa đăng nhập!", "Thông báo");
        return;
    }

    try {
        // Load thẻ nguồn
        const res = await fetch(`/api/card/${cardId}`, {
            headers: { "Authorization": "Bearer " + token }
        });

        if (!res.ok) {
            showToast("Không tìm thấy thẻ", "error");
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
                showToast("Không tìm thấy người nhận!", "error");
                return;
            }

            const receiverCard = await resReceiver.json();

            if (receiverCard.cardId === card.cardId) {
                showToast("Không thể chuyển sang cùng một thẻ!", "error");
                return;
            }

            if (receiverCard.account.accountId === card.account.accountId) {
                showToast("Không thể chuyển giữa các thẻ trong cùng một tài khoản!", "error");
                return;
            }

            if (receiverCard.status === "INACTIVE") {
                showToast("Thẻ nhận đã bị vô hiệu hóa, không thể chuyển khoản!", "error");
                return;
            }

            document.getElementById("receiverCard").innerText = receiverCard.cardNumber;
            document.getElementById("receiverName").innerText = receiverCard.account.customerName;

            document.getElementById("receiverInfo").classList.remove("hidden");
        });

        // B2: gửi yêu cầu transfer -> sinh OTP
        document.getElementById("transferForm").addEventListener("submit", async function (e) {
            e.preventDefault();

            const amountInput = document.getElementById("amount").value.trim();

            if (isNaN(amountInput) || amountInput === "") {
                showToast("Số tiền không hợp lệ (phải là số)!", "error");
                return;
            }

            const amount = parseFloat(amountInput);

            if (amount <= 0) {
                showToast("Số tiền phải lớn hơn 0!", "error");
                return;
            }

            const availableBalance = parseFloat(card.account.balance.availableBalance);
            if (amount > availableBalance) {
                showToast("Số dư không đủ!", "error");
                return;
            }

            if (card.status === "INACTIVE") {
                showToast("Thẻ nguồn đã bị vô hiệu hóa, không thể chuyển khoản!", "error");
                return;
            }

            const receiverStatus = document.getElementById("receiverStatus")?.innerText;
            if (receiverStatus === "INACTIVE") {
                showToast("Thẻ nhận đã bị vô hiệu hóa, không thể chuyển khoản!", "error");
                return;
            }


            const payload = {
                fromCardId: card.cardId,
                toCardNumber:document.getElementById("receiverCard").innerText,
                amount: amount,
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
                let errMsg;
                try {
                    const errObj = await resReq.json();   // nếu backend trả JSON {error: "..."}
                    errMsg = errObj.error || JSON.stringify(errObj);
                } catch {
                    errMsg = await resReq.text();         // fallback: plain text
                }
                showToast(errMsg || "Không tạo được giao dịch!", "error");
                return;
            }


            const result = await resReq.json();
            showNotify("Mã OTP đã được gửi đến email của bạn. Vui lòng nhập OTP để xác nhận.", "Thông báo");

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
                showToast("Xác nhận OTP thất bại!", "error");
                return;
            }

            const result = await resConfirm.json();
            showToast("OTP hợp lệ! Giao dịch đang chờ Admin duyệt. Mã giao dịch: " + result.transactionId, "success");
            navigate({ preventDefault: () => {} }, "/transaction-history");
        });

    } catch (err) {
        console.error("Lỗi khi load thẻ:", err);
        showToast("Có lỗi khi tải dữ liệu thẻ!", "error");
    }
});
