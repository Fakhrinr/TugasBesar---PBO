document.addEventListener("DOMContentLoaded", () => {

    initializeSearch();
    initializeStatusFilter();
    initializeForms();
    initializeStatusBadges();
    calculateDashboardStats();

});

/* =========================================
   SEARCH
========================================= */
function initializeSearch() {

    const searchInput =
        document.getElementById(
            "searchInput"
        );

    if (!searchInput) return;

    searchInput.addEventListener(
        "keyup",
        filterTable
    );
}

/* =========================================
   STATUS FILTER
========================================= */
function initializeStatusFilter() {

    const statusFilter =
        document.getElementById(
            "statusFilter"
        );

    if (!statusFilter) return;

    statusFilter.addEventListener(
        "change",
        filterTable
    );
}

/* =========================================
   SEARCH + FILTER
========================================= */
function filterTable() {

    const searchInput =
        document.getElementById(
            "searchInput"
        );

    const statusFilter =
        document.getElementById(
            "statusFilter"
        );

    const keyword =
        searchInput
            ? searchInput.value.toLowerCase()
            : "";

    const selectedStatus =
        statusFilter
            ? statusFilter.value.toUpperCase()
            : "ALL";

    const rows =
        document.querySelectorAll(
            "tbody tr"
        );

    rows.forEach(row => {

        const rowText =
            row.innerText.toLowerCase();

        const rowStatus =
            row.dataset.status
                ? row.dataset.status.toUpperCase()
                : "";

        const matchesKeyword =
            rowText.includes(keyword);

        const matchesStatus =
            selectedStatus === "ALL"
            || rowStatus === selectedStatus;

        row.style.display =
            matchesKeyword && matchesStatus
                ? ""
                : "none";
    });
}

/* =========================================
   DASHBOARD STATISTICS
========================================= */
function calculateDashboardStats() {

    const rows =
        document.querySelectorAll(
            "tbody tr[data-status]"
        );

    if (!rows.length) return;

    let openCount = 0;
    let overdueCount = 0;
    let paidCount = 0;
    let revenue = 0;

    rows.forEach(row => {

        const status =
            row.dataset.status;

        const amount =
            parseFloat(
                row.dataset.amount || 0
            );

        revenue += amount;

        switch (status) {

            case "OPEN":
                openCount++;
                break;

            case "OVERDUE":
                overdueCount++;
                break;

            case "PAID":
                paidCount++;
                break;
        }
    });

    const openElement =
        document.getElementById(
            "openCount"
        );

    const overdueElement =
        document.getElementById(
            "overdueCount"
        );

    const paidElement =
        document.getElementById(
            "paidCount"
        );

    const revenueElement =
        document.getElementById(
            "revenueTotal"
        );

    if (openElement)
        openElement.textContent =
            openCount;

    if (overdueElement)
        overdueElement.textContent =
            overdueCount;

    if (paidElement)
        paidElement.textContent =
            paidCount;

    if (revenueElement)
        revenueElement.textContent =
            "Rp " +
            revenue.toLocaleString(
                "id-ID"
            );
}

/* =========================================
   FORM CONFIRMATION
========================================= */
function initializeForms() {

    const forms =
        document.querySelectorAll(
            "form"
        );

    if (!forms.length) return;

    forms.forEach(form => {

        form.addEventListener(
            "submit",
            function (event) {

                const action =
                    this.action.toLowerCase();

                let message =
                    "Are you sure?";

                if (
                    action.includes(
                        "late-fee"
                    )
                ) {

                    message =
                        "Apply late fee to this invoice?";

                }
                else if (
                    action.includes(
                        "pay"
                    )
                ) {

                    message =
                        "Mark this invoice as PAID?";

                }
                else if (
                    action.includes(
                        "check-status"
                    )
                ) {

                    message =
                        "Refresh invoice status?";
                }

                const confirmed =
                    confirm(message);

                if (!confirmed) {

                    event.preventDefault();
                    return;
                }

                const button =
                    this.querySelector(
                        "button"
                    );

                if (button) {

                    button.disabled =
                        true;

                    button.innerHTML =
                        "<i class='fa-solid fa-spinner fa-spin'></i> Processing...";
                }
            }
        );
    });
}

/* =========================================
   STATUS BADGE COLOR
========================================= */
function initializeStatusBadges() {

    const badges =
        document.querySelectorAll(
            ".status-badge"
        );

    badges.forEach(badge => {

        const status =
            badge.innerText
                .trim()
                .toUpperCase();

        badge.classList.remove(
            "status-open",
            "status-paid",
            "status-overdue",
            "status-draft"
        );

        switch (status) {

            case "PAID":

                badge.classList.add(
                    "status-paid"
                );
                break;

            case "OVERDUE":

                badge.classList.add(
                    "status-overdue"
                );
                break;

            case "DRAFT":

                badge.classList.add(
                    "status-draft"
                );
                break;

            default:

                badge.classList.add(
                    "status-open"
                );
        }
    });
}