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

document.addEventListener('DOMContentLoaded', function () {

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
