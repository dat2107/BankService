async function loadPage(url) {
    try {
        const response = await fetch(url, {
            headers: { "X-Requested-With": "XMLHttpRequest" }
        });
        const html = await response.text();

        const main = document.getElementById("mainContent");
        main.innerHTML = html;

        // bắn sự kiện để JS của từng page xử lý
        document.dispatchEvent(new CustomEvent("pageLoaded", { detail: url }));

    } catch (e) {
        document.getElementById("mainContent").innerHTML =
            "<p class='text-red-600'>Lỗi tải trang!</p>";
    }
}

function navigate(event, url) {
    event.preventDefault();               // chặn reload
    history.pushState({ path: url }, "", url);  // đổi URL
    loadPage(url);                        // render nội dung
}

// Bắt sự kiện back/forward của trình duyệt
window.onpopstate = function(event) {
    if (event.state && event.state.path) {
        loadPage(event.state.path);
    }
};

function logout() {
    // Xóa token JWT trong localStorage
    localStorage.removeItem("token");

    // Thông báo nhỏ
    showToast("Đăng xuất thành công!", "success");

    // Điều hướng về trang login
    setTimeout(() => {
        window.location.href = "/login";
    }, 1000);
}
