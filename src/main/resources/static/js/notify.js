// ---- Modal ----
function showNotify(message, title = "Thông báo") {
    const modal = document.getElementById("notifyModal");
    document.getElementById("notifyTitle").innerText = title;
    document.getElementById("notifyMessage").innerText = message;
    modal.classList.remove("hidden");
    modal.classList.add("flex");
}

function closeNotify() {
    const modal = document.getElementById("notifyModal");
    modal.classList.add("hidden");
    modal.classList.remove("flex");
}

// ---- Toast ----
function showToast(message, type = "success") {
    const container = document.getElementById("toastContainer");

    const toast = document.createElement("div");
    toast.className = `
        px-5 py-3 rounded-lg shadow-lg text-white flex items-center gap-3 animate-slide-in
        ${type === "success" ? "bg-green-500" : "bg-red-500"}
    `;

    const icon = document.createElement("span");
    icon.innerHTML = type === "success" ? "✅" : "⚠️";

    const text = document.createElement("span");
    text.innerText = message;

    toast.appendChild(icon);
    toast.appendChild(text);

    container.appendChild(toast);

    setTimeout(() => {
        toast.classList.add("animate-fade-out");
        setTimeout(() => toast.remove(), 500);
    }, 3000);
}
