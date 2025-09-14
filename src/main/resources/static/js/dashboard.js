async function loadUsers(query = "") {
    const token = localStorage.getItem("token");
    if (!token) {
        alert("Bạn chưa đăng nhập!");
        return;
    }

    let url = "/api/users";
    if (query) {
        url += "?keyword=" + encodeURIComponent(query);   // ✅ truyền param search
    }

    const response = await fetch(url, {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + token,
            "Content-Type": "application/json"
        }
    });

    if (!response.ok) {
        console.error("Failed to fetch users", response.status);
        return;
    }

    const users = await response.json();
    let tbody = document.querySelector("#userTableBody");
    tbody.innerHTML = "";

    users.forEach(u => {
        // badge màu cho role
        let roleBadge = u.role === "ADMIN"
            ? `<span class="inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-purple-100 text-purple-800">${getRoleDisplay(u.role)}</span>`
            : `<span class="inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-green-100 text-green-800">${getRoleDisplay(u.role)}</span>`;

        tbody.innerHTML += `
                <tr class="hover:bg-gray-50 transition-colors duration-200">
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">${u.id}</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">${u.username}</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        ${u.account && u.account.email ? u.account.email : 'N/A'}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">${roleBadge}</td>
                </tr>`;
    });
}

function getRoleDisplay(role) {
    if (!role) return "N/A";
    switch (role.toUpperCase()) {
        case "USER": return "Customer";
        case "ADMIN": return "Admin";
        default: return role;
    }
}

document.addEventListener("DOMContentLoaded", () => loadUsers());

async function loadPage(url) {
    try {
        const response = await fetch(url, {
            headers: { "X-Requested-With": "XMLHttpRequest" }
        });
        if (!response.ok) throw new Error(response.status);

        const html = await response.text();

        const main = document.getElementById("mainContent");
        main.innerHTML = html;

        // bắn sự kiện để JS của từng page xử lý
        document.dispatchEvent(new CustomEvent("pageLoaded", { detail: url }));

    } catch (e) {
        document.getElementById("mainContent").innerHTML =
            "<p class='text-red-600'>Lỗi tải trang!</p>";
        console.error("Error loading page:", e);
    }
}

function navigate(event, url) {
    event.preventDefault();

    history.pushState({ path: url }, "", url);
    loadPage(url);   // fetch chính /user
}


function goDashboard(event) {
    event.preventDefault(); // chặn mặc định để xử lý bằng JS
    window.location.href = "/dashboard"; // reload nguyên trang
}

// Bắt sự kiện back/forward của trình duyệt
window.onpopstate = function(event) {
    if (event.state) {
        loadPage(event.state.path);
    }
};

document.addEventListener("DOMContentLoaded", async () => {
    try {
        const token = localStorage.getItem("token");
        const res = await fetch("/api/account", {
            headers: { "Authorization": "Bearer " + token }
        });

        if (!res.ok) {
            console.error("Không thể load danh sách account");
            return;
        }

        const accounts = await res.json();

        // Đếm số user
        const totalUsers = accounts.length;

        // Đếm số thẻ
        const totalCards = accounts.reduce((sum, acc) => sum + (acc.cards ? acc.cards.length : 0), 0);

        // 👉 Nếu bạn có API transaction riêng thì gọi thêm ở đây
        let totalTransactions = 0;
        try {
            const txRes = await fetch("/api/transaction", {
                headers: { "Authorization": "Bearer " + token }
            });
            if (txRes.ok) {
                const txData = await txRes.json();
                totalTransactions = txData.totalElements || txData.length || 0;
            }
        } catch (err) {
            console.warn("Không load được transaction, đặt mặc định = 0");
        }

        // Cập nhật vào dashboard
        document.getElementById("statUsers").textContent = totalUsers.toLocaleString();
        document.getElementById("statCards").textContent = totalCards.toLocaleString();
        document.getElementById("statTransactions").textContent = totalTransactions.toLocaleString();

    } catch (err) {
        console.error("Lỗi khi load stats:", err);
    }
});

function searchUsers() {
    const query = document.getElementById("searchInput").value.trim();
    loadUsers(query);
}

function logout() {
    // Xóa token JWT trong localStorage
    localStorage.removeItem("token");

    // Thông báo
    showToast("Đăng xuất thành công!", "success");

    // Điều hướng về trang login
    setTimeout(() => {
        window.location.href = "/login";
    }, 1000);
}
