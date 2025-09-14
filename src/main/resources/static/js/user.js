document.addEventListener("pageLoaded", async (e) => {
    // chỉ chạy khi load user.jsp
    if (!e.detail.includes("user")) return;

    const token = localStorage.getItem("token");
    if (!token) {
        showNotify("Bạn chưa đăng nhập!", "Thông báo");
        return;
    }

    try {
        const res = await fetch("/api/account", {
            headers: { "Authorization": "Bearer " + token }
        });

        if (!res.ok) {
            console.error("Failed to fetch users", res.status);
            return;
        }

        const users = await res.json();
        // lưu global để filter lại sau
        window.allUsers = users;

        // render lần đầu và load select
        renderUsersTable(users);

        initSearch();

    } catch (err) {
        console.error("Error loading users:", err);
    }
});

function renderUsersTable(users) {
    let tbody = document.getElementById("userTable");
    if (!tbody) return;

    tbody.innerHTML = "";

    users.forEach(u => {
        tbody.innerHTML += `
                <tr class="hover:bg-gray-50">
                    <td class="px-4 py-2 border text-center">${u.accountId}</td>
                    <td class="px-4 py-2 border">${u.customerName}</td>
                    <td class="px-4 py-2 border">${u.email}</td>
                    <td class="px-4 py-2 border">${u.phoneNumber}</td>
                    <td class="px-4 py-2 border">${u.userLevel ? u.userLevel.levelName : ''}</td>
                    <td class="px-4 py-2 border text-center">
                        <button onclick="navigate(event, '/userDetail?id=${u.accountId}')"
                                class="bg-green-600 text-white px-3 py-1 rounded hover:bg-green-700">
                            View Card
                        </button>
                        <button onclick="navigate(event, '/updateUser?id=${u.accountId}')"
                                class="bg-yellow-500 text-white px-3 py-1 rounded hover:bg-yellow-600">
                            Update
                        </button>
                        <button onclick="deleteUser(${u.accountId})"
                                class="bg-red-600 text-white px-3 py-1 rounded hover:bg-red-700">
                            Delete
                        </button>
                    </td>
                </tr>
            `;
    });
}

function initSearch() {
    const input = document.querySelector("input[placeholder='Tìm kiếm theo tên người dùng']");
    const btn = input.nextElementSibling; // nút search bên phải

    function doSearch() {
        const keyword = input.value.trim().toLowerCase();
        const filtered = window.allUsers.filter(u =>
            u.customerName.toLowerCase().includes(keyword)
        );
        renderUsersTable(filtered);
    }

    // Gõ phím tự động lọc
    input.addEventListener("input", doSearch);
    // Hoặc bấm nút Search
    btn.addEventListener("click", doSearch);
}

function deleteUser(accountId) {
    showConfirm("Bạn có chắc muốn xoá tài khoản này?", async () => {
        const token = localStorage.getItem("token");
        if (!token) {
            showNotify("Bạn chưa đăng nhập!", "Thông báo");
            return;
        }

        try {
            const res = await fetch(`/api/account/${accountId}`, {
                method: "DELETE",
                headers: {
                    "Authorization": "Bearer " + token
                }
            });

            if (res.ok) {
                showToast("Xoá tài khoản thành công!", "success");
                // reload danh sách
                window.allUsers = window.allUsers.filter(u => u.accountId !== accountId);
                renderUsersTable(window.allUsers);
            } else {
                const msg = await res.text();
                showToast("Xoá thất bại: " + msg, "error");
            }
        } catch (err) {
            console.error("Error deleting user:", err);
            showToast("Có lỗi khi xoá tài khoản!", "error");
        }
    });
}


