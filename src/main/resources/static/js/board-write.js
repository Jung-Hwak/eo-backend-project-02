window.addEventListener("load", function()  {

    const closeBtn = document.getElementById("muted");
    const deleteForm = document.getElementById("deleteForm");
    const deleteBtn = document.getElementById("danger");
    const titleInput = document.querySelector('input[name="title"]');

    // 1) 닫기 버튼
    if (closeBtn) {
        closeBtn.addEventListener("click", function() {window.close(); });
    }

    // 2) 삭제 버튼
    if (deleteBtn && deleteForm) {
        deleteBtn.addEventListener("click", function (event) {
            event.preventDefault();

            const ok = window.confirm("정말 삭제할까요?");
            if (ok) {
                deleteForm.submit();
            }
        });
    }

    // 포커스
    if (titleInput) {
        titleInput.focus();

        // 기존 값이 있으면 커서를 맨 뒤로 이동
        const length = titleInput.value.length;
        titleInput.setSelectionRange(length, length);
    }
});