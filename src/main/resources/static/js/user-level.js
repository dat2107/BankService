document.addEventListener("pageLoaded", async (e) => {
    // chỉ chạy khi load user-level.jsp
    if (!e.detail.includes("user-level")) return;

    const token = localStorage.getItem("token");
    if (!token) {
        showNotify("Bạn chưa đăng nhập!", "Thông báo");
        return;
    }

    try {
        const res = await fetch("/api/users", {
            headers: { "Authorization": "Bearer " + token }
        });

        if (!res.ok) {
            console.error("Failed to fetch users", res.status);
            showToast("Không thể tải danh sách người dùng", "error");
            return;
        }

        const users = await res.json();
        // lưu global để filter lại sau
        window.allUsers = users;

        // render lần đầu và load select
        renderUsers(users);
        populateUserLevels(users);

    } catch (err) {
        console.error("Error loading users:", err);
        showToast("Có lỗi khi tải danh sách người dùng!", "error");
    }
});

function renderUsers(users) {
    let tbody = document.getElementById("userTableBody");
    if (!tbody) return;

    tbody.innerHTML = "";

    // lấy giá trị được chọn từ select
    const selectedLevel = document.getElementById("loaiNguoiDung")?.value || "";

    users
        .filter(u => {
            const level = u.account && u.account.userLevel ? u.account.userLevel.levelName : "";
            return selectedLevel === "" || level === selectedLevel;
        })
        .forEach(u => {
            tbody.innerHTML += `
                <tr class="hover:bg-gray-50">
                    <td class="px-4 py-2 border text-center">${u.id}</td>
                    <td class="px-4 py-2 border">${u.account ? u.account.customerName : ''}</td>
                    <td class="px-4 py-2 border">${u.account ? u.account.email : ''}</td>
                    <td class="px-4 py-2 border">${u.account ? u.account.phoneNumber : ''}</td>
                    <td class="px-4 py-2 border">${u.username}</td>
                    <td class="px-4 py-2 border">${u.role}</td>
                    <td class="px-4 py-2 border">${u.account && u.account.userLevel ? u.account.userLevel.levelName : ''}</td>
                </tr>`;
        });
}

function populateUserLevels(users) {
    const select = document.getElementById("loaiNguoiDung");
    if (!select) return;

    const levels = [...new Set(users.map(u => u.account?.userLevel?.levelName).filter(Boolean))];

    select.innerHTML = '<option value="">-- Tất cả --</option>';
    levels.forEach(level => {
        const opt = document.createElement("option");
        opt.value = level;
        opt.textContent = level;
        select.appendChild(opt);
    });

    select.addEventListener("change", () => renderUsers(window.allUsers));
}

