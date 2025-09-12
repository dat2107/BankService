document.addEventListener("pageLoaded", async (e) => {
    if (!e.detail.includes("transaction-history")) return;

    const accountId = localStorage.getItem("accountId");

    const token = localStorage.getItem("token");
    if (!token) {
        showNotify("Bạn chưa đăng nhập!", "Thông báo");
        return;
    }

    try {
        const res = await fetch(`/api/transaction/account/${accountId}`, {
            headers: { "Authorization": "Bearer " + token }
        });

        if (!res.ok) {
            showToast("Không thể tải lịch sử giao dịch!", "error");
            return;
        }

        const transactions = await res.json();
        renderTransactions(transactions);
    } catch (err) {
        console.error("Error loading transactions:", err);
        showToast("Có lỗi khi tải lịch sử giao dịch!", "error");
    }
});

function renderTransactions(transactions) {
    const tbody = document.getElementById("transactionTable");
    tbody.innerHTML = "";

    if (!transactions || transactions.length === 0) {
        tbody.innerHTML = `<tr><td colspan="8" class="text-center p-4 text-gray-500">
            Không có giao dịch nào
        </td></tr>`;
        return;
    }

    const accountId = localStorage.getItem("accountId");
    const typeMap = {
        "TRANSFER": "CHUYỂN KHOẢN",
        "DEPOSIT": "NẠP",
        "WITHDRAW": "RÚT"
    };

    transactions.forEach(t => {
        // Xác định vai trò: CHUYỂN hay NHẬN
        let direction = "";
        if (t.type === "TRANSFER") {
            if (t.fromAccountId && t.fromAccountId.toString() === accountId) {
                direction = "CHUYỂN";
            } else if (t.toAccountId && t.toAccountId.toString() === accountId) {
                // Nếu là nhận mà status FAILED thì bỏ qua luôn
                if (t.status === "FAILED" || t.status === "WAITING_APPROVAL" ) {
                    return; // skip không render
                }
                direction = "NHẬN";
            }
        }

        tbody.innerHTML += `
            <tr class="hover:bg-gray-50">
                <td class="px-4 py-2 border text-center">${t.transactionId}</td>
                <td class="px-4 py-2 border text-center">${formatDate(t.createdAt)}</td>
                <td class="px-4 py-2 border text-center">${Number(t.amount).toLocaleString()}</td>
                <td class="px-4 py-2 border text-center">${typeMap[t.type] || t.type}</td>
                <td class="px-4 py-2 border text-center">
                    <span class="px-2 py-1 rounded text-white 
                        ${t.status === 'SUCCESS' ? 'bg-green-500' :
                            (t.status === 'PENDING' ? 'bg-yellow-500' : 'bg-red-500')}">
                        ${t.status}
                    </span>
                </td>
                <td class="px-4 py-2 border text-center">${t.fromCardNumber || ""}</td>
                <td class="px-4 py-2 border text-center">${t.toCardNumber || ""}</td>
                <td class="px-4 py-2 border text-center font-semibold">${direction}</td>
            </tr>
        `;
    });
}


// format datetime
function formatDate(dateStr) {
    if (!dateStr) return "";
    const d = new Date(dateStr);
    return d.toLocaleString("vi-VN");
}
