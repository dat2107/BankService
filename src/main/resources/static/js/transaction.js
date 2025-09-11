document.addEventListener("pageLoaded", async (e) => {
    if (!e.detail.includes("transaction")) return;

    const token = localStorage.getItem("token");
    if (!token) {
        alert("Bạn chưa đăng nhập!");
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
                return;
            }

            const data = await res.json();
            totalPages = data.totalPages;   // lấy tổng số trang
            renderTransactions(data.content);

            // update hiển thị trang hiện tại
            document.getElementById("currentPage").innerText = `${currentPage} / ${totalPages}`;
        } catch (err) {
            console.error("Error loading transactions:", err);
        }
    }

    function renderTransactions(transactions) {
        const tbody = document.getElementById("transactionTable");
        tbody.innerHTML = "";
        transactions.forEach(t => {
            tbody.innerHTML += `
                <tr class="hover:bg-gray-50">
                    <td class="px-4 py-2 border text-center">${t.transactionId}</td>
                    <td class="px-4 py-2 border text-center">${t.fromCardNumber || "0"}</td>
                    <td class="px-4 py-2 border text-center">${t.toCardNumber || "0"}</td>
                    <td class="px-4 py-2 border text-center">${Number(t.amount).toLocaleString()}</td>
                    <td class="px-4 py-2 border text-center">${t.type}</td>
                    <td class="px-4 py-2 border text-center">
                        <span class="px-2 py-1 rounded text-white ${t.status === 'SUCCESS' ? 'bg-green-500' : (t.status === 'PENDING' ? 'bg-yellow-500' : 'bg-red-500')}">
                            ${t.status}
                        </span>
                    </td>
                    <td class="px-4 py-2 border text-center">
                        <button class="bg-yellow-500 text-white px-3 py-1 rounded hover:bg-yellow-600"
                                onclick="updateStatus(${t.transactionId})">Update success</button>
                    </td>
                </tr>
            `;
        });
    }

    window.updateStatus = async function(id) {
        if (!confirm("Xác nhận duyệt giao dịch này?")) return;
        try {
            const res = await fetch(`/api/admin/transactions/${id}/approve`, {
                method: "POST",
                headers: {
                    "Authorization": "Bearer " + token,
                    "Content-Type": "application/json"
                }

            });
            if (res.ok) {
                alert("Duyệt thành công!");
                loadTransactions(currentPage);
            } else {
                alert("Duyệt thất bại!");
            }
        } catch (err) {
            console.error("Error approving transaction:", err);
        }
    }


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
