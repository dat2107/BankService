document.addEventListener("pageLoaded", async (e) => {
    // chỉ chạy khi load user.jsp
    if (!e.detail.includes("user")) return;

    const token = localStorage.getItem("token");
    if (!token) {
        alert("Bạn chưa đăng nhập!");
        return;
    }

    try {
        const res = await fetch("/api/users", {
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
                    <td class="px-4 py-2 border text-center">${u.id}</td>
                    <td class="px-4 py-2 border">${u.username}</td>
                    <td class="px-4 py-2 border">${u.account ? u.account.email : ''}</td>
                    <td class="px-4 py-2 border">${u.role}</td>
                    <td class="px-4 py-2 border">${u.account && u.account.userLevel ? u.account.userLevel.levelName : ''}</td>
                    <td class="px-4 py-2 border text-center">
                        <button class="bg-green-600 text-white px-3 py-1 rounded hover:bg-green-700">View Card</button>
                        <button class="bg-yellow-500 text-white px-3 py-1 rounded hover:bg-yellow-600">Update</button>
                        <button class="bg-red-600 text-white px-3 py-1 rounded hover:bg-red-700">Delete</button>
                    </td>
                </tr>
            `;
    });
}



