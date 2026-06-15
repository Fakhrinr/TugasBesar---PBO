// Toggle show/hide password
function togglePwd(inputId, btn) {
  const inp = document.getElementById(inputId);
  const isText = inp.type === 'password';
  inp.type = isText ? 'text' : 'password';
  btn.classList.toggle('active', isText);
}

// Validate confirm password before submit
function checkPwd(pwdId, confirmId) {
  const p = document.getElementById(pwdId).value;
  const c = document.getElementById(confirmId).value;
  if (p !== c) {
    alert('Password dan Confirmation Password tidak cocok!');
    return false;
  }
  return true;
}