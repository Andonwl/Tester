<script>

const createForm = document.getElementById('createForm');

if (createForm) {
  const valSummary = document.getElementById('valSummary');
  const valList = document.getElementById('valList');
  const successScreen = document.getElementById('successScreen');
  const submitBtn = document.getElementById('submitBtn');

  function showFieldError(id, show) {
    const el = document.getElementById(id);
    if (el) el.classList.toggle('visible', show);
  }

  function setInputError(id, show) {
    const el = document.getElementById(id);
    if (el) el.classList.toggle('error', show);
  }

  function validEmail(v) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v);
  }

  function validPassword(v) {
    return /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/.test(v);
  }

  createForm.addEventListener('submit', async function (e) {
    e.preventDefault();

    valSummary.classList.remove('visible');
    successScreen.classList.remove('visible');
    valList.innerHTML = '';

    const firstname = document.getElementById('firstname').value.trim();
    const lastname = document.getElementById('lastname').value.trim();
    const email = document.getElementById('email').value.trim();
    const emailConfirm = document.getElementById('emailConfirm').value.trim();
    const phone = document.getElementById('phone').value.trim();
    const dob = document.getElementById('dob').value;
    const citizen = document.getElementById('citizen').value;
    const term = document.getElementById('term').value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    let errors = [];

    const firstBad = firstname === '';
    const lastBad = lastname === '';
    const emailBad = !validEmail(email);
    const emailConfirmBad = email !== emailConfirm || emailConfirm === '';
    const phoneBad = phone === '';
    const citizenBad = citizen === '';
    const termBad = term === '';
    const passwordBad = !validPassword(password);
    const confirmPasswordBad = password !== confirmPassword || confirmPassword === '';

    setInputError('firstname', firstBad);
    showFieldError('err-firstname', firstBad);
    if (firstBad) errors.push('First name is required.');

    setInputError('lastname', lastBad);
    showFieldError('err-lastname', lastBad);
    if (lastBad) errors.push('Last name is required.');

    setInputError('email', emailBad);
    showFieldError('err-email', emailBad);
    if (emailBad) errors.push('A valid email is required.');

    setInputError('emailConfirm', emailConfirmBad);
    showFieldError('err-emailConfirm', emailConfirmBad);
    if (emailConfirmBad) errors.push('Emails do not match.');

    setInputError('phone', phoneBad);
    showFieldError('err-phone', phoneBad);
    if (phoneBad) errors.push('Phone number is required.');

    setInputError('citizen', citizenBad);
    showFieldError('err-citizen', citizenBad);
    if (citizenBad) errors.push('Please select whether you are a US citizen.');

    setInputError('term', termBad);
    showFieldError('err-term', termBad);
    if (termBad) errors.push('Please select a start term.');

    setInputError('password', passwordBad);
    showFieldError('err-password', passwordBad);
    if (passwordBad) errors.push('Password must be 8+ characters with uppercase, lowercase, and a number.');

    setInputError('confirmPassword', confirmPasswordBad);
    showFieldError('err-confirmPassword', confirmPasswordBad);
    if (confirmPasswordBad) errors.push('Passwords do not match.');

    if (errors.length > 0) {
      valList.innerHTML = errors.map(err => `<li>${err}</li>`).join('');
      valSummary.classList.add('visible');
      return;
    }

    submitBtn.disabled = true;
    submitBtn.textContent = 'Creating Account...';

    try {
      const response = await fetch('/api/students/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          firstname,
          lastname,
          email,
          phone,
          dob,
          password,
          confirmPassword
        })
      });

      const data = await response.json();

      if (!response.ok) {
        valList.innerHTML = `<li>${data.message || 'Unable to create account.'}</li>`;
        valSummary.classList.add('visible');
        submitBtn.disabled = false;
        submitBtn.textContent = 'Create Account';
        return;
      }

      createForm.reset();
      successScreen.classList.add('visible');
      submitBtn.disabled = false;
      submitBtn.textContent = 'Create Account';

      setTimeout(() => {
        window.location.href = "/bridgetrack-signin.html";
      }, 1500);

    } catch (err) {
      valList.innerHTML = `<li>Something went wrong while creating the account.</li>`;
      valSummary.classList.add('visible');
      submitBtn.disabled = false;
      submitBtn.textContent = 'Create Account';
    }
  });
}

</script>