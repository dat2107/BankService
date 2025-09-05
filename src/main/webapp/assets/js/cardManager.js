document.addEventListener("pageLoaded", async (e) => {
    if (!e.detail.includes("cardManager")) return;
    await loadCards();
});

async function loadCards(query = "") {
    const token = localStorage.getItem("token");
    if (!token) {
        alert("Bạn chưa đăng nhập!");
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
            return;
        }

        const cards = await res.json();
        renderCardTable(cards);
    } catch (err) {
        console.error("Error loading cards:", err);
    }
}

function renderCardTable(cards) {
    let tbody = document.getElementById("cardTable");
    tbody.innerHTML = "";

    cards.forEach((c, index) => {
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
                    <button onclick="updateStatus(${c.cardId})" class="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600">Update INACTIVE</button>
                </td>
            </tr>
        `;
    });
}

function viewCard(cardId) {
    navigate(event, `/cardDetail?id=${cardId}`);
}

function deleteCard(cardId) {
    if (!confirm("Bạn có chắc muốn xóa card này?")) return;

    const token = localStorage.getItem("token");
    fetch(`/api/card/${cardId}`, {
        method: "DELETE",
        headers: { "Authorization": "Bearer " + token }
    }).then(res => {
        if (res.ok) {
            alert("Xóa thành công!");
            loadCards();
        } else {
            alert("Xóa thất bại!");
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
            alert("Cập nhật trạng thái thành công!");
            loadCards();
        } else {
            alert("Cập nhật trạng thái thất bại!");
        }
    });
}

// Gắn sự kiện search
document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("searchCard").addEventListener("input", e => {
        loadCards(e.target.value);
    });
});
