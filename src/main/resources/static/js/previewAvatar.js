document
        .getElementById("avatarFile")
        .addEventListener("change", function (e) {
        const file = e.target.files[0];
        const previewImg = document.getElementById("avatarPreview");

        if (file) {
        // Tạo URL tạm thời từ file được chọn
        const newUrl = URL.createObjectURL(file);

        // Thu hồi URL cũ trước đó (nếu có) để giải phóng bộ nhớ RAM của trình duyệt
        if (previewImg.src && previewImg.src.startsWith("blob:")) {
        URL.revokeObjectURL(previewImg.src);
        }

        // Gán URL mới vào thẻ ảnh
        previewImg.src = newUrl;
        }
    });
