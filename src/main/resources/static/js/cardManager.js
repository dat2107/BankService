document.addEventListener("pageLoaded", async (e) => {
    if (!e.detail.includes("cardManager")) return;
    await loadCards();
});

async function loadCards(query = "") {
    const token = localStorage.getItem("token");
    if (!token) {
        showNotify("Bạn chưa đăng nhập!", "Thông báo");
        return;
    }

    let url = "/api/card";
    if (query) {
        url += "?q=" + encodeURIComponent(query);
    }

    try {
        const res = await fetch(url, {
            headers: { "Authorization": "Bearer " + token }
        });

        if (!res.ok) {
            console.error("Failed to fetch cards", res.status);
            showToast("Không tải được danh sách thẻ", "error");
            return;
        }

        const cards = await res.json();
        renderCardTable(cards);
    } catch (err) {
        console.error("Error loading cards:", err);
        showToast("Lỗi khi tải danh sách thẻ!", "error");
    }
}

function renderCardTable(cards) {
    let tbody = document.getElementById("cardTable");
    tbody.innerHTML = "";

    cards.forEach((c, index) => {
        const isActive = c.status === "ACTIVE";
        const btnLabel = isActive ? "Update INACTIVE" : "Update ACTIVE";
        const btnColor = isActive ? "bg-red-500 hover:bg-red-600" : "bg-green-500 hover:bg-green-600";

        tbody.innerHTML += `
            <tr class="hover:bg-gray-50">
                <td class="px-4 py-2 border">${c.cardId}</td>
               <td class="px-4 py-2 border">${c.account ? c.account.customerName : ''}</td>
                <td class="px-4 py-2 border">${c.cardNumber}</td>
                <td class="px-4 py-2 border">${c.cardType}</td>
                <td class="px-4 py-2 border">${c.status}</td>
                <td class="px-4 py-2 border text-center space-x-2">
                    <button onclick="viewCard(${c.cardId})" class="bg-blue-600 text-white px-3 py-1 rounded hover:bg-blue-700">View</button>
                    <button onclick="deleteCard(${c.cardId})" class="bg-red-600 text-white px-3 py-1 rounded hover:bg-red-700">Delete</button>
                    <button onclick="updateStatus(${c.cardId})" 
                        class="${btnColor} text-white px-3 py-1 rounded">${btnLabel}
                    </button>
                </td>
            </tr>
        `;
    });
}

function viewCard(cardId) {
    navigate(event, `/cardDetail?id=${cardId}`);
}

function deleteCard(cardId) {
    showConfirm("Bạn có chắc muốn xóa card này?", async () => {
        const token = localStorage.getItem("token");
        const res = await fetch(`/api/card/${cardId}`, {
            method: "DELETE",
            headers: { "Authorization": "Bearer " + token }
        });

        if (res.ok) {
            showToast("Xóa thành công!", "success");
            await loadCards();
        } else {
            showToast("Xóa thất bại!", "error");
        }
    });
}

function updateStatus(cardId) {
    const token = localStorage.getItem("token");
    fetch(`/api/card/${cardId}/status`, {
        method: "PUT",
        headers: { "Authorization": "Bearer " + token }
    }).then(res => {
        if (res.ok) {
            showToast("Cập nhật trạng thái thành công!", "success");
            loadCards();
        } else {
            showToast("Cập nhật trạng thái thất bại!", "error");
        }
    });
}

// Gắn sự kiện search
document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("searchCard").addEventListener("input", e => {
        loadCards(e.target.value);
    });
});
