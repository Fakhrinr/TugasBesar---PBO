document.addEventListener("DOMContentLoaded", function () {
    const searchInput = document.querySelector("#notificationSearch");
    const filterButtons = document.querySelectorAll(".filter-tab");
    const cards = Array.from(document.querySelectorAll(".notification-card"));
    const clientEmpty = document.querySelector(".client-empty");
    const serverEmpty = document.querySelector(".server-empty");
    const flashes = document.querySelectorAll(".flash");
    let activeFilter = "all";

    function normalize(value) {
        return (value || "").toString().toLowerCase();
    }

    function applyFilters() {
        const query = normalize(searchInput ? searchInput.value : "");
        let visibleCount = 0;

        cards.forEach(function (card) {
            const state = card.dataset.state;
            const haystack = normalize(card.dataset.type + " " + card.dataset.message);
            const matchesState = activeFilter === "all" || state === activeFilter;
            const matchesSearch = query === "" || haystack.includes(query);
            const shouldShow = matchesState && matchesSearch;

            card.classList.toggle("is-hidden", !shouldShow);

            if (shouldShow) {
                visibleCount += 1;
            }
        });

        if (clientEmpty && !serverEmpty) {
            clientEmpty.hidden = visibleCount > 0;
        }
    }

    if (searchInput) {
        searchInput.addEventListener("input", applyFilters);
    }

    filterButtons.forEach(function (button) {
        button.addEventListener("click", function () {
            activeFilter = button.dataset.filter;

            filterButtons.forEach(function (item) {
                item.classList.remove("active");
            });

            button.classList.add("active");
            applyFilters();
        });
    });

    document.querySelectorAll(".delete-form").forEach(function (form) {
        form.addEventListener("submit", function (event) {
            const confirmed = window.confirm("Hapus notifikasi ini?");
            if (!confirmed) {
                event.preventDefault();
            }
        });
    });

    flashes.forEach(function (flash) {
        window.setTimeout(function () {
            flash.style.display = "none";
        }, 3500);
    });
});