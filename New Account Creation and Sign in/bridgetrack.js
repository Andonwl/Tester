
document.getElementById('phone').addEventListener('input', function () {
  let v = this.value.replace(/\D/g, '');
  if (v.length >= 10)    v = '(' + v.slice(0,3) + ') ' + v.slice(3,6) + '-' + v.slice(6,10);
  else if (v.length > 6) v = '(' + v.slice(0,3) + ') ' + v.slice(3,6) + '-' + v.slice(6);
  else if (v.length > 3) v = '(' + v.slice(0,3) + ') ' + v.slice(3);
  this.value = v;
});

let hsCount = 0;

function addHighSchool() {
  if (hsCount >= 3) return;
  hsCount++;
  const id = 'hs-' + hsCount;
  const entry = document.createElement('div');
  entry.className = 'subform-entry';
  entry.id = id;
  entry.innerHTML = `
    <div class="subform-entry-header">
      High School #${hsCount}
      <button type="button" class="btn-remove" onclick="removeHighSchool('${id}')">✕ Remove</button>
    </div>
    <div class="subform-entry-body">
      <div class="form-row cols-2">
        <div class="field">
          <label>School Name</label>
          <input type="text" name="hs_name_${hsCount}" placeholder="Lincoln High School" />
        </div>
        <div class="field">
          <label>City</label>
          <input type="text" name="hs_city_${hsCount}" placeholder="Charleston" />
        </div>
      </div>
      <div class="form-row cols-2">
        <div class="field">
          <label>State</label>
          <select name="hs_state_${hsCount}">
            <option value="">— Select —</option>
            <option>South Carolina</option>
            <option>North Carolina</option>
            <option>Georgia</option>
            <option>Virginia</option>
            <option>Florida</option>
            <option>Other</option>
          </select>
        </div>
        <div class="field">
          <label>Graduation Year</label>
          <input type="number" name="hs_grad_${hsCount}" min="1990" max="2030" placeholder="${new Date().getFullYear()}" />
        </div>
      </div>
    </div>
  `;
  document.getElementById('hsEntries').appendChild(entry);
  if (hsCount >= 3) document.getElementById('addHsBtn').style.display = 'none';
}

function removeHighSchool(id) {
  document.getElementById(id).remove();
  hsCount--;
  document.getElementById('addHsBtn').style.display = 'block';
}

const fields = [
  { id: 'firstname',       err: 'err-firstname',       msg: 'First name is required.',         check: v => v.trim() !== '' },
  { id: 'lastname',        err: 'err-lastname',        msg: 'Last name is required.',           check: v => v.trim() !== '' },
  { id: 'email',           err: 'err-email',           msg: 'A valid email is required.',       check: v => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v) },
  { id: 'emailConfirm',    err: 'err-emailConfirm',    msg: 'Emails do not match.',             check: v => v !== '' && v === document.getElementById('email').value },
  { id: 'phone',           err: 'err-phone',           msg: 'Phone number is required.',        check: v => v.trim() !== '' },
  { id: 'citizen',         err: 'err-citizen',         msg: 'Please select an option.',         check: v => v !== '' },
  { id: 'term',            err: 'err-term',            msg: 'Please select a start term.',      check: v => v !== '' },
  { id: 'password',        err: 'err-password',        msg: 'Password must be 8+ characters with uppercase, lowercase, and a number.', check: v => /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/.test(v) },
  { id: 'confirmPassword', err: 'err-confirmPassword', msg: 'Passwords do not match.',          check: v => v !== '' && v === document.getElementById('password').value },
];

fields.forEach(f => {
  const el = document.getElementById(f.id);
  if (!el) return;
  el.addEventListener('blur', () => {
    const ok = f.check(el.value);
    el.classList.toggle('error', !ok);
    document.getElementById(f.err).classList.toggle('visible', !ok);
  });
});

document.getElementById('createForm').addEventListener('submit', function (e) {
  e.preventDefault();

  let valid = true;
  const errors = [];

  fields.forEach(f => {
    const el = document.getElementById(f.id);
    const ok = f.check(el.value);
    el.classList.toggle('error', !ok);
    document.getElementById(f.err).classList.toggle('visible', !ok);
    if (!ok) { valid = false; errors.push({ msg: f.msg, id: f.id }); }
  });

  if (!document.getElementById('agreeTerms').checked) {
    valid = false;
    errors.push({ msg: 'You must agree to the Terms of Service.', id: 'agreeTerms' });
  }

  const summary = document.getElementById('valSummary');
  const list    = document.getElementById('valList');
  list.innerHTML = '';
  if (errors.length) {
    errors.forEach(e => {
      const li = document.createElement('li');
      li.textContent = e.msg;
      li.onclick = () => document.getElementById(e.id).focus();
      list.appendChild(li);
    });
    summary.classList.add('visible');
    summary.scrollIntoView({ behavior: 'smooth' });
    return;
  }

  summary.classList.remove('visible');

  const btn = document.getElementById('submitBtn');
  btn.disabled = true;
  btn.textContent = 'Creating account…';
  setTimeout(() => {
    document.getElementById('createForm').style.display = 'none';
    document.querySelector('.signin-banner').style.display = 'none';
    document.getElementById('successScreen').classList.add('visible');
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }, 1000);
});
