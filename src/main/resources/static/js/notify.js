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

function showConfirm(message, onConfirm) {
    const modal = document.getElementById("confirmModal");
    const msgEl = document.getElementById("confirmMessage");
    const btnYes = document.getElementById("confirmYes");
    const btnNo = document.getElementById("confirmNo");

    msgEl.innerText = message;
    modal.classList.remove("hidden");
    modal.classList.add("flex");

    // cleanup event listener để tránh gọi nhiều lần
    btnYes.onclick = () => {
        modal.classList.add("hidden");
        modal.classList.remove("flex");
        onConfirm();
    };
    btnNo.onclick = () => {
        modal.classList.add("hidden");
        modal.classList.remove("flex");
    };
}

function showTransactionModal(message, onApprove, onReject) {
    const modal = document.getElementById("transactionModal");
    const msgEl = document.getElementById("transactionModalMessage");

    msgEl.innerText = message;
    modal.classList.remove("hidden");
    modal.classList.add("flex");

    // clear old listeners
    const approveBtn = document.getElementById("transactionApproveBtn");
    const rejectBtn = document.getElementById("transactionRejectBtn");
    const cancelBtn = document.getElementById("transactionCancelBtn");

    approveBtn.onclick = () => {
        modal.classList.add("hidden");
        modal.classList.remove("flex");
        if (onApprove) onApprove();
    };
    rejectBtn.onclick = () => {
        modal.classList.add("hidden");
        modal.classList.remove("flex");
        if (onReject) onReject();
    };
    cancelBtn.onclick = () => {
        modal.classList.add("hidden");
        modal.classList.remove("flex");
    };
}

function showApproveModal(message, onConfirm) {
    const modal = document.getElementById("approveModal");
    const msgEl = modal.querySelector("p");

    msgEl.innerText = message;
    modal.classList.remove("hidden");
    modal.classList.add("flex");

    const btnOk = document.getElementById("approveOkBtn");
    const btnCancel = document.getElementById("approveCancelBtn");

    btnOk.onclick = async () => {
        modal.classList.add("hidden");
        modal.classList.remove("flex");
        if (onConfirm) await onConfirm();
    };
    btnCancel.onclick = () => {
        modal.classList.add("hidden");
        modal.classList.remove("flex");
    };
}

function showRejectModal(message, onConfirm) {
    const modal = document.getElementById("rejectModal");
    const msgEl = modal.querySelector("p");

    msgEl.innerText = message;
    modal.classList.remove("hidden");
    modal.classList.add("flex");

    const btnOk = document.getElementById("rejectOkBtn");
    const btnCancel = document.getElementById("rejectCancelBtn");

    btnOk.onclick = async () => {
        modal.classList.add("hidden");
        modal.classList.remove("flex");
        if (onConfirm) await onConfirm();
    };

    btnCancel.onclick = () => {
        modal.classList.add("hidden");
        modal.classList.remove("flex");
    };
}



