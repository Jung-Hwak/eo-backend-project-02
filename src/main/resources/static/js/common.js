function openMypage() {
    const width = 900;
    const height = 800;

    const left = (window.screen.width / 2) - (width / 2);
    const top = (window.screen.height / 2) - (height / 2);

    window.open(
        "/mypage",
        "mypagePopup",
        `width=${width},height=${height},left=${left},top=${top},scrollbars=yes,resizable=yes`
    );
}

function openMessagePopup(url) {
    const width = 900;
    const height = 800;

    const left = (window.screen.width / 2) - (width / 2);
    const top = (window.screen.height / 2) - (height / 2);

    window.open(
        url,
        "messagePopup",
        `width=${width},height=${height},left=${left},top=${top},scrollbars=yes,resizable=yes`
    );
}