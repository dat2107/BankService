document.addEventListener("pageLoaded", async (e) => {
    if (!e.detail.includes("transaction")) return;

    const token = localStorage.getItem("token");
    if (!token) {
        showNotify("Bạn chưa đăng nhập!", "Thông báo");
        return;
    }

    let currentPage = 1;   // hiển thị cho người dùng (1-based)
    const pageSize = 7;
    let totalPages = 1;

    async function loadTransactions(page) {
        try {
            // BE page = 0-based → FE page - 1
            const res = await fetch(`/api/transaction?page=${page - 1}&size=${pageSize}`, {
                headers: { "Authorization": "Bearer " + token }
            });

            if (!res.ok) {
                console.error("Lỗi tải transaction", res.status);
                showToast("Không thể tải danh sách giao dịch", "error");
                return;
            }

            const data = await res.json();
            totalPages = data.totalPages;   // lấy tổng số trang
            renderTransactions(data.content);

            // update hiển thị trang hiện tại
            document.getElementById("currentPage").innerText = `${currentPage} / ${totalPages}`;
        } catch (err) {
            console.error("Error loading transactions:", err);
            showToast("Có lỗi khi tải giao dịch!", "error");
        }
    }

    function renderTransactions(transactions) {
        const tbody = document.getElementById("transactionTable");
        tbody.innerHTML = "";
        transactions.forEach(t => {
            let actionHtml = "";

            if (t.status === 'WAITING_APPROVAL') {
                actionHtml = `
                <button class="bg-green-600 text-white px-3 py-1 rounded hover:bg-green-700 mr-2"
                        onclick="approveTransaction(${t.transactionId})">Approve</button>
                <button class="bg-red-600 text-white px-3 py-1 rounded hover:bg-red-700"
                        onclick="rejectTransaction(${t.transactionId})">Reject</button>
            `;
            } else {
                actionHtml = `<span class="text-gray-500 italic">No action</span>`;
            }

            tbody.innerHTML += `
                <tr class="hover:bg-gray-50">
                    <td class="px-4 py-2 border text-center">${t.transactionId}</td>
                    <td class="px-4 py-2 border text-center">${t.fromCardNumber || "0"}</td>
                    <td class="px-4 py-2 border text-center">${t.toCardNumber || "0"}</td>
                    <td class="px-4 py-2 border text-center">${Number(t.amount).toLocaleString()}</td>
                    <td class="px-4 py-2 border text-center">${t.type}</td>
                    <td class="px-4 py-2 border text-center">
                        <span class="px-2 py-1 rounded text-white ${
                            t.status === 'SUCCESS' ? 'bg-green-500' : 
                                (t.status === 'PENDING' ? 'bg-yellow-500' : 'bg-red-500')
                        }">
                            ${t.status}
                        </span>
                    </td>
                   <td class="px-4 py-2 border text-center">${actionHtml}</td>
                </tr>
            `;
        });
    }

    window.approveTransaction = async function(id) {
        showApproveModal("Xác nhận duyệt giao dịch này?", async () => {
            try {
                const res = await fetch(`/api/admin/transactions/${id}/approve`, {
                    method: "POST",
                    headers: {
                        "Authorization": "Bearer " + token,
                        "Content-Type": "application/json"
                    }
                });

                if (res.ok) {
                    showToast("Duyệt thành công!", "success");
                    loadTransactions(currentPage);
                } else {
                    showToast("Duyệt thất bại!", "error");
                }
            } catch (err) {
                console.error("Error approving transaction:", err);
                showToast("Có lỗi khi duyệt giao dịch!", "error");
            }
        });
    };

    window.rejectTransaction = async function(id) {
        showRejectModal("Bạn chắc chắn muốn từ chối giao dịch này?", async () => {
            try {
                const res = await fetch(`/api/admin/transactions/${id}/reject`, {
                    method: "POST",
                    headers: {
                        "Authorization": "Bearer " + token,
                        "Content-Type": "application/json"
                    }
                });

                if (res.ok) {
                    showToast("🚫 Từ chối thành công!", "success");
                    loadTransactions(currentPage);
                } else {
                    showToast("❌ Từ chối thất bại!", "error");
                }
            } catch (err) {
                console.error("Error rejecting transaction:", err);
                showToast("⚠️ Có lỗi khi từ chối giao dịch!", "error");
            }
        });
    };

    // Pagination control
    document.getElementById("prevPage").addEventListener("click", () => {
        if (currentPage > 1) {
            currentPage--;
            loadTransactions(currentPage);
        }
    });
    document.getElementById("nextPage").addEventListener("click", () => {
        if (currentPage < totalPages) {
            currentPage++;
            loadTransactions(currentPage);
        }
    });

    loadTransactions(currentPage);
});
