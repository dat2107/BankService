document.addEventListener("pageLoaded", async (e) => {
    if (!e.detail.includes("vip-detail")) return;
    await loadUserLevels();

    // Gắn lại event cho nút Save mỗi lần pageLoaded
    const saveBtn = document.getElementById("saveBtn");
    if (saveBtn && !saveBtn.dataset.bound) {
        saveBtn.addEventListener("click", async (ev) => {
            ev.preventDefault();
            await saveUserLevel();
        });
        saveBtn.dataset.bound = "true"; // tránh bind trùng nhiều lần
    }
});


async function loadUserLevels() {
    try {
        const token = localStorage.getItem("token");
        const res = await fetch("/api/userlevel", {
            headers: { "Authorization": "Bearer " + token }
        });

        if (!res.ok) {
            console.error("Failed to fetch user levels", res.status);
            showToast("Không thể tải danh sách cấp độ người dùng", "error");
            return;
        }

        const levels = await res.json();
        const tbody = document.getElementById("levelTableBody");
        tbody.innerHTML = "";

        levels.forEach(l => {
            tbody.innerHTML += `
                <tr class="hover:bg-gray-50">
                    <td class="px-4 py-2 border">${l.id}</td>
                    <td class="px-4 py-2 border">${l.levelName}</td>
                    <td class="px-4 py-2 border">${l.cardLimit}</td>
                    <td class="px-4 py-2 border">${l.dailyTransferLimit}</td>
                    <td class="px-4 py-2 border">
                        <a onclick="openEditModal(${l.id})"
                           class="bg-yellow-400 text-black px-3 py-1 rounded hover:bg-yellow-500">Edit</a>
                        <button onclick="deleteLevel(${l.id})"
                           class="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600 ml-2">Delete</button>
                    </td>
                </tr>
            `;
        });
    } catch (err) {
        console.error("Error loading user levels:", err);
        showToast("Có lỗi khi tải danh sách user level!", "error");
    }
}

async function deleteLevel(id) {
    showConfirm("Bạn có chắc chắn muốn xóa cấp độ này?", async () => {
        try {
            const token = localStorage.getItem("token");
            const res = await fetch("/api/userlevel/" + id, {
                method: "DELETE",
                headers: { "Authorization": "Bearer " + token }
            });

            if (res.ok) {
                showToast("Xóa thành công!", "success");
                await loadUserLevels();
            } else {
                showToast("Xóa thất bại!", "error");
            }
        } catch (err) {
            console.error("Error deleting:", err);
            showToast("Có lỗi khi xóa cấp độ!", "error");
        }
    });
}


function openAddModal() {
    document.getElementById("modalTitle").innerText = "Add User Level";
    document.getElementById("saveBtn").innerText = "Save";

    document.getElementById("levelId").value = "";
    document.getElementById("levelName").value = "";
    document.getElementById("cardLimit").value = "";
    document.getElementById("dailyTransferLimit").value = "";

    document.getElementById("userLevelModal").classList.remove("hidden");
    document.getElementById("userLevelModal").classList.add("flex");
}

async function openEditModal(id) {
    document.getElementById("modalTitle").innerText = "Edit User Level";
    document.getElementById("saveBtn").innerText = "Update";

    const token = localStorage.getItem("token");
    const res = await fetch("/api/userlevel/" + id, {
        headers: { "Authorization": "Bearer " + token }
    });

    if (res.ok) {
        const data = await res.json();
        document.getElementById("levelId").value = data.id;
        document.getElementById("levelName").value = data.levelName;
        document.getElementById("cardLimit").value = data.cardLimit;
        document.getElementById("dailyTransferLimit").value = data.dailyTransferLimit;

        document.getElementById("userLevelModal").classList.remove("hidden");
        document.getElementById("userLevelModal").classList.add("flex");
    } else {
        showToast("Không thể tải thông tin cấp độ!", "error");
    }
}

function closeModal() {
    document.getElementById("userLevelModal").classList.add("hidden");
    document.getElementById("userLevelModal").classList.remove("flex");
}

// ================== SAVE ==================
document.addEventListener("DOMContentLoaded", () => {
    const saveBtn = document.getElementById("saveBtn");
    if (saveBtn) {
        saveBtn.addEventListener("click", async (e) => {
            e.preventDefault();
            await saveUserLevel();
        });
    }
});

async function saveUserLevel() {
    const token = localStorage.getItem("token");
    const id = document.getElementById("levelId").value;
    const data = {
        levelName: document.getElementById("levelName").value,
        cardLimit: document.getElementById("cardLimit").value,
        dailyTransferLimit: document.getElementById("dailyTransferLimit").value
    };

    let url = "/api/userlevel";
    let method = "POST";
    if (id) {
        url += "/" + id;
        method = "PUT";
    }

    try {
        const res = await fetch(url, {
            method,
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        });

        if (res.ok) {
            showToast(id ? "Cập nhật thành công!" : "Thêm mới thành công!", "success");
            closeModal();
            await loadUserLevels();
        } else {
            const errMsg = await res.text();
            showToast("Lưu thất bại: " + errMsg, "error");
        }
    } catch (err) {
        console.error("Error saving user level:", err);
        showToast("Có lỗi khi lưu cấp độ người dùng!", "error");
    }
}
