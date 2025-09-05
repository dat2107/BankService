document.addEventListener("pageLoaded", async (e) => {
    if (!e.detail.includes("updateUser")) return;
    const params = new URLSearchParams(window.location.search);
    const accountId = params.get("id");

    const token = localStorage.getItem("token");
    if (!token) {
        alert("Bạn chưa đăng nhập!");
        return;
    }

    try {
        // Load chi tiết user
        const res = await fetch(`/api/account/${accountId}`, {
            headers: { "Authorization": "Bearer " + token }
        });
        console.log("Status:", res.status);

        const text = await res.text();
        console.log("Raw body:", text);

        let user;
        try {
            user = JSON.parse(text);
            console.log("Parsed JSON:", user);
        } catch (err) {
            console.error("Body không phải JSON:", err);
        }


        document.getElementById("accountId").value = user.accountId;
        console.log(user.accountId)
        document.getElementById("customerName").value = user.customerName;
        document.getElementById("email").value = user.email;
        document.getElementById("phoneNumber").value = user.phoneNumber;

        // Load danh sách UserLevel
        const resLevel = await fetch("/api/userlevel", {
            headers: { "Authorization": "Bearer " + token }
        });
        const levels = await resLevel.json();

        let select = document.getElementById("userLevel");
        levels.forEach(l => {
            let opt = document.createElement("option");
            opt.value = l.id;
            opt.textContent = l.levelName;
            if (user.userLevel && user.userLevel.id === l.id) {
                opt.selected = true;
            }
            select.appendChild(opt);
        });

        // Submit form
        document.getElementById("updateUserForm").addEventListener("submit", async (e) => {
            e.preventDefault();
            const payload = {
                customerName: document.getElementById("customerName").value,
                phoneNumber: document.getElementById("phoneNumber").value,
                userLevelId: document.getElementById("userLevel").value
            };

            const resUpdate = await fetch(`/api/account/${accountId}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + token
                },
                body: JSON.stringify(payload)
            });

            if (resUpdate.ok) {
                alert("Cập nhật thành công!");
                history.back();
            } else {
                alert("Cập nhật thất bại!");
            }
        });

    } catch (err) {
        console.error("Error loading user:", err);
    }
});