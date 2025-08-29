<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quên mật khẩu - MyBank</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100">
<div class="flex items-center justify-center min-h-screen">
    <div class="bg-white p-8 rounded-lg shadow-md w-96">
        <h2 class="text-2xl font-bold text-center mb-6">Quên mật khẩu</h2>
        <form id="forgotForm">
            <input type="email" id="forgotEmail" placeholder="Nhập email đã đăng ký"
                   class="w-full border px-4 py-2 rounded mb-4" required>
            <button type="submit"
                    class="w-full bg-orange-600 text-white py-2 rounded hover:bg-orange-700">
                Gửi link đặt lại
            </button>
        </form>
        <div class="mt-4 text-center">
            <a href="login" class="text-blue-600 hover:underline">← Quay lại đăng nhập</a>
        </div>
    </div>
</div>
</body>
</html>
