// ── Modal Helpers ─────────────────────────────────────────
function openModal(id) {
  document.getElementById(id).classList.add('open');
  document.body.style.overflow = 'hidden';
}
function closeModal(id) {
  document.getElementById(id).classList.remove('open');
  document.body.style.overflow = '';
}

// Close modal on overlay click
document.querySelectorAll('.modal-overlay').forEach(overlay => {
  overlay.addEventListener('click', e => {
    if (e.target === overlay) closeModal(overlay.id);
  });
});

// ── Flash auto-dismiss ────────────────────────────────────
document.querySelectorAll('.alert').forEach(el => {
  setTimeout(() => {
    el.style.transition = 'opacity .4s';
    el.style.opacity = '0';
    setTimeout(() => el.remove(), 400);
  }, 4000);
});

// ── Sidebar mobile toggle ─────────────────────────────────
const menuBtn = document.getElementById('menuToggle');
const sidebar = document.querySelector('.sidebar');
if (menuBtn && sidebar) {
  menuBtn.addEventListener('click', () => sidebar.classList.toggle('open'));
}

// ── Active nav highlight ──────────────────────────────────
const currentPath = window.location.pathname;
document.querySelectorAll('.nav-item').forEach(link => {
  if (link.getAttribute('href') === currentPath) {
    link.classList.add('active');
  }
});

// ── Confirm actions ───────────────────────────────────────
function confirmAction(msg, formId) {
  if (confirm(msg)) {
    document.getElementById(formId).submit();
  }
}

// ── Assign modal pre-fill ─────────────────────────────────
function openAssignModal(ticketId, ticketTitle) {
  document.getElementById('assignTicketId').value = ticketId;
  const titleEl = document.getElementById('assignTicketTitle');
  if (titleEl) titleEl.textContent = ticketTitle;
  openModal('modalAssign');
}

// ── Late fee modal pre-fill ───────────────────────────────
function openLateFeeModal(invoiceId) {
  document.getElementById('lateFeeInvoiceId').value = invoiceId;
  openModal('modalLateFee');
}