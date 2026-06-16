/* ============================================================
   subscription.js
   Taruh di: src/main/resources/static/js/subscription.js
   ============================================================ */

/**
 * Untuk sekarang JS nya minimal karena fitur subscription
 * hanya READ ONLY (tidak ada form input atau interaksi kompleks).
 *
 * Fungsi yang ada:
 * - Auto highlight baris tabel kalau status GRACE atau SUSPENDED
 * - Tooltip untuk status badge
 */

/** document.addEventListener('DOMContentLoaded', function () {

    // ── Highlight baris tabel berdasarkan status ──────────────
    // Kalau status GRACE → kasih background kuning tipis
    // Kalau status SUSPENDED → kasih background merah tipis
    const rows = document.querySelectorAll('tbody tr');

    rows.forEach(function (row) {
        const pill = row.querySelector('.pill');
        if (!pill) return;

        const status = pill.textContent.trim();

        if (status === 'GRACE') {
            // Warning: subscription mau expired
            row.style.background = '#fefce8';
        } else if (status === 'SUSPENDED') {
            // Danger: subscription expired / belum bayar
            row.style.background = '#fff5f5';
        }
    });

    // ── Auto refresh status setiap 5 menit ───────────────────
    // Biar status selalu up to date tanpa perlu reload manual
    // 5 menit = 300000 ms
    setTimeout(function () {
        window.location.reload();
    }, 300000);

});
*/

document.addEventListener("DOMContentLoaded", function () {
    const rows = document.querySelectorAll(".sub-row");
    const detailCard = document.getElementById("detail-card");

    rows.forEach(row => {
        row.addEventListener("click", function () {
            // Ambil data dari atribut data-*
            const data = {
                id: this.dataset.id,
                name: this.dataset.name,
                plan: this.dataset.plan,
                status: this.dataset.status,
                start: this.dataset.start,
                end: this.dataset.end,
                fee: this.dataset.fee
            };

            // Render ke dalam card
            detailCard.innerHTML = `
                <div class="plan-title-row">
                    <span class="plan-name">${data.plan}</span>
                    <span class="status-badge badge-${data.status.toLowerCase()}">${data.status}</span>
                </div>
                <p class="plan-sub-id">User: <strong>${data.name}</strong> | ID: <strong>${data.id}</strong></p>
                <div class="plan-meta">
                    <div class="meta-block">
                        <span class="meta-label">Start Date</span>
                        <span class="meta-value">${data.start}</span>
                    </div>
                    <div class="meta-block">
                        <span class="meta-label">End Date</span>
                        <span class="meta-value">${data.end}</span>
                    </div>
                    <div class="meta-block">
                        <span class="meta-label">Monthly Fee</span>
                        <span class="meta-value">$${data.fee}</span>
                    </div>
                </div>
            `;
        });
    });
});
