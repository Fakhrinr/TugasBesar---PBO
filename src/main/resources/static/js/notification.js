document.addEventListener("DOMContentLoaded", () => {
    const searchInput = document.getElementById("notificationSearch");
    const typeFilter = document.getElementById("notificationTypeFilter");
    const statusFilter = document.getElementById("notificationStatusFilter");

    const notificationCards = Array.from(
        document.querySelectorAll(".notification-card")
    );

    const filterEmptyState =
        document.getElementById("filterEmptyState");

    /**
     * Mengubah createdAt menjadi:
     * "Just now", "5 minutes ago", "3 hours ago", dan sebagainya.
     */
    function updateRelativeTimes() {
        const timeElements = document.querySelectorAll(".time-ago");

        timeElements.forEach((element) => {
            const createdAtValue =
                element.dataset.createdAt;

            if (!createdAtValue) {
                return;
            }

            const createdAt = new Date(createdAtValue);

            if (Number.isNaN(createdAt.getTime())) {
                return;
            }

            const now = new Date();
            const differenceMilliseconds =
                now.getTime() - createdAt.getTime();

            const differenceSeconds =
                Math.floor(differenceMilliseconds / 1000);

            const differenceMinutes =
                Math.floor(differenceSeconds / 60);

            const differenceHours =
                Math.floor(differenceMinutes / 60);

            const differenceDays =
                Math.floor(differenceHours / 24);

            if (differenceSeconds < 60) {
                element.textContent = "Just now";
                return;
            }

            if (differenceMinutes < 60) {
                element.textContent =
                    `${differenceMinutes} minute${differenceMinutes > 1 ? "s" : ""} ago`;

                return;
            }

            if (differenceHours < 24) {
                element.textContent =
                    `${differenceHours} hour${differenceHours > 1 ? "s" : ""} ago`;

                return;
            }

            if (differenceDays < 7) {
                element.textContent =
                    `${differenceDays} day${differenceDays > 1 ? "s" : ""} ago`;

                return;
            }

            element.textContent =
                createdAt.toLocaleDateString("en-GB", {
                    day: "2-digit",
                    month: "short",
                    year: "numeric",
                    hour: "2-digit",
                    minute: "2-digit"
                });
        });
    }

    /**
     * Search dan filter notifikasi.
     */
    function filterNotifications() {
        const searchKeyword =
            searchInput?.value.trim().toLowerCase() ?? "";

        const selectedType =
            typeFilter?.value ?? "ALL";

        const selectedStatus =
            statusFilter?.value ?? "ALL";

        let visibleCount = 0;

        notificationCards.forEach((card) => {
            const message =
                (card.dataset.message ?? "").toLowerCase();

            const type =
                card.dataset.type ?? "";

            const status =
                card.dataset.status ?? "";

            const matchesSearch =
                message.includes(searchKeyword) ||
                type.toLowerCase().includes(searchKeyword);

            const matchesType =
                selectedType === "ALL" ||
                type === selectedType;

            const matchesStatus =
                selectedStatus === "ALL" ||
                status === selectedStatus;

            const shouldShow =
                matchesSearch &&
                matchesType &&
                matchesStatus;

            card.style.display = shouldShow
                ? "flex"
                : "none";

            if (shouldShow) {
                visibleCount++;
            }
        });

        if (filterEmptyState) {
            filterEmptyState.style.display =
                notificationCards.length > 0 &&
                visibleCount === 0
                    ? "grid"
                    : "none";
        }
    }

    /**
     * Konfirmasi sebelum menghapus notifikasi.
     */
    function initializeDeleteConfirmation() {
        const deleteForms =
            document.querySelectorAll(".delete-form");

        deleteForms.forEach((form) => {
            form.addEventListener("submit", (event) => {
                const isConfirmed = window.confirm(
                    "Are you sure you want to delete this notification?"
                );

                if (!isConfirmed) {
                    event.preventDefault();
                }
            });
        });
    }

    /**
     * Menutup flash message.
     */
    function initializeAlerts() {
        const alerts =
            document.querySelectorAll(".alert");

        alerts.forEach((alert) => {
            const closeButton =
                alert.querySelector(".alert-close");

            const hideAlert = () => {
                alert.classList.add("hide");

                window.setTimeout(() => {
                    alert.remove();
                }, 250);
            };

            closeButton?.addEventListener(
                "click",
                hideAlert
            );

            window.setTimeout(hideAlert, 5000);
        });
    }

    searchInput?.addEventListener(
        "input",
        filterNotifications
    );

    typeFilter?.addEventListener(
        "change",
        filterNotifications
    );

    statusFilter?.addEventListener(
        "change",
        filterNotifications
    );

    updateRelativeTimes();
    initializeDeleteConfirmation();
    initializeAlerts();

    window.setInterval(
        updateRelativeTimes,
        60000
    );
});