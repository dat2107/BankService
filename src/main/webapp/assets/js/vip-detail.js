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
    }
}

async function deleteLevel(id) {
    if (!confirm("Are you sure you want to delete this level?")) return;

    try {
        const token = localStorage.getItem("token");
        const res = await fetch("/api/userlevel/" + id, {
            method: "DELETE",
            headers: { "Authorization": "Bearer " + token }
        });

        if (res.ok) {
            alert("Deleted successfully!");
            await loadUserLevels();
        } else {
            alert("Failed to delete level");
        }
    } catch (err) {
        console.error("Error deleting:", err);
    }
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
        alert("Failed to load user level!");
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

    const res = await fetch(url, {
        method,
        headers: {
            "Authorization": "Bearer " + token,
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    });

    if (res.ok) {
        alert(id ? "Updated successfully!" : "Added successfully!");
        closeModal();
        await loadUserLevels();
    } else {
        alert("Error saving user level!");
    }
}
