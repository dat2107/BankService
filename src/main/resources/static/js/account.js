function parseJwt(token) {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(function (c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
    return JSON.parse(jsonPayload);
}

async function loadAccount() {
    try {
        const token = localStorage.getItem("token");
        if (!token) {
            document.getElementById("accountInfo").innerHTML =
                "<p class='text-red-600'>Bạn chưa đăng nhập!</p>";
            return;
        }

        // Lấy accountId từ localStorage hoặc decode từ token
        let accountId = localStorage.getItem("accountId");
        accountId = accountId ? accountId.toString().trim() : null;
        if (!accountId) {
            const payload = parseJwt(token);
            accountId = payload.accountId;
            localStorage.setItem("accountId", accountId);
        }

        const url = "/api/account/" + accountId;
        // Gọi API /api/account/{accountId}
        const response = await fetch(url, {
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (!response.ok) {
            document.getElementById("accountInfo").innerHTML =
                "<p class='text-red-600'>Không thể tải dữ liệu (401 Unauthorized)!</p>";
            return;
        }

        const data = await response.json();

        if (data && typeof data === 'object') {
            document.getElementById("accountInfo").innerHTML = `
                    <p><strong>Họ và tên:</strong> ${data.customerName ? data.customerName : 'N/A'}</p>
                    <p><strong>Email:</strong> ${data.email ? data.email : 'N/A'}</p>
                    <p><strong>Số điện thoại:</strong> ${data.phoneNumber ? data.phoneNumber : 'N/A'}</p>
                `;

        } else {
            document.getElementById("accountInfo").innerHTML = "<p class='text-red-600'>Dữ liệu không hợp lệ!</p>";
        }

        // Render danh sách thẻ (nếu BE trả về cards)
        let cardHtml = "";
        if (data.cards && data.cards.length > 0) {
            data.cards.forEach(c => {
                cardHtml += `
                        <div class="border rounded p-3">
                            <p>
                                <strong>Số thẻ:</strong> ${c.cardNumber} -
                                <strong>Loại:</strong> ${c.cardType} -
                                <strong>Số dư:</strong> ${data.balance?.availableBalance ?? 0} -
                                <strong>Đang chờ xử lý:</strong> ${data.balance?.holdBalance ?? 0}
                            </p>
                            <button onclick="navigate(event, '/transfer?cardId=${c.cardId}')"
                                    class="mt-2 px-3 py-1 bg-blue-500 text-white rounded hover:bg-blue-600">
                                Chuyển khoản
                            </button>
                        </div>
                    `;
            });
        } else {
            cardHtml = "<p>Bạn chưa có thẻ nào.</p>";
        }
        document.getElementById("cardList").innerHTML = cardHtml;

    } catch (err) {
        console.error(err);
        document.getElementById("accountInfo").innerHTML =
            "<p class='text-red-600'>Lỗi kết nối server!</p>";
    }
}

function getRoleDisplay(role) {
    if (!role) return "N/A";
    switch (role.toUpperCase()) {
        case "USER": return "Customer";
        case "ADMIN": return "Admin";
        default: return role;
    }
}

function createCard() {
    navigate(event, "/createCard");
}

function transfer(cardNumber) {
    alert("Chuyển khoản từ thẻ " + cardNumber);
}

document.addEventListener("pageLoaded", e => {
    if (e.detail.includes("/account")) {
        loadAccount();
    }
});
