async function loadUsers(query = "") {
    const token = localStorage.getItem("token");
    if (!token) {
        alert("Bạn chưa đăng nhập!");
        return;
    }

    let url = "/api/users";
    if (query) {
        url += "?q=" + encodeURIComponent(query);   // ✅ truyền param search
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
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium space-x-2">
                        <button class="bg-blue-500 hover:bg-blue-600 text-white px-3 py-1 rounded text-xs">Xem</button>
                        <button class="bg-yellow-500 hover:bg-yellow-600 text-white px-3 py-1 rounded text-xs">Sửa</button>
                        <button class="bg-red-500 hover:bg-red-600 text-white px-3 py-1 rounded text-xs">Xóa</button>
                    </td>
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

// function navigate(event, url) {
//     event.preventDefault();               // chặn reload
//     history.pushState({ path: url }, "", url);  // đổi URL
//     loadPage(url +"");                        // render nội dung
// }

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
