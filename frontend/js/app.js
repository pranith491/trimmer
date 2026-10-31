const API = (window.API_BASE_URL || "").replace(/\/$/, "");
const $ = (id) => document.getElementById(id);
let activeTab = "login";
let token = localStorage.getItem("bicky_token") || "";
let currentUser = JSON.parse(localStorage.getItem("bicky_user") || "null");
const prices = {"Beard Trim":149,"Hair + Beard":299,"Precision Trim":199};
$("year").textContent = new Date().getFullYear();
$("bookingDate").min = new Date().toISOString().slice(0,10);

$("menuToggle").addEventListener("click", () => $("navLinks").classList.toggle("open"));
document.querySelectorAll("#navLinks a").forEach(a => a.addEventListener("click", () => $("navLinks").classList.remove("open")));
document.querySelectorAll(".tab").forEach(tab => tab.addEventListener("click", () => setTab(tab.dataset.tab)));
$("loginNav").addEventListener("click", () => setTab("login"));
$("logoutNav").addEventListener("click", logout);

function setTab(tab) {
  activeTab = tab;
  document.querySelectorAll(".tab").forEach(t => t.classList.toggle("active", t.dataset.tab === tab));
  $("nameField").classList.toggle("hidden", tab !== "register");
  $("authTitle").textContent = tab === "login" ? "Welcome back." : "Create your account.";
  $("authSub").textContent = tab === "login" ? "Sign in to continue to your booking." : "Register once, then reserve your time.";
  $("authSubmit").innerHTML = (tab === "login" ? "Login" : "Create Account") + " <span>→</span>";
  $("password").autocomplete = tab === "login" ? "current-password" : "new-password";
  $("authMessage").textContent = "";
}
function showMessage(id, msg, error=false) {
  $(id).textContent = msg;
  $(id).classList.toggle("error", error);
}
async function api(path, options={}) {
  const headers = {"Content-Type":"application/json", ...(options.headers || {})};
  if (token) headers.Authorization = "Bearer " + token;
  let response;
  try {
    response = await fetch(API + path, {...options, headers});
  } catch (e) {
    throw new Error("Cannot connect to the backend. Check API_BASE_URL and confirm the backend is running.");
  }
  const body = await response.json().catch(() => ({}));
  if (!response.ok) throw new Error(body.message || body.error || `Request failed (${response.status})`);
  return body;
}
$("authForm").addEventListener("submit", async e => {
  e.preventDefault();
  showMessage("authMessage", "Please wait…");
  const payload = {email:$("email").value.trim(), password:$("password").value};
  if (activeTab === "register") payload.fullName = $("fullName").value.trim();
  try {
    const data = await api(activeTab === "login" ? "/api/auth/login" : "/api/auth/register", {method:"POST", body:JSON.stringify(payload)});
    token = data.token; currentUser = data.user;
    localStorage.setItem("bicky_token", token); localStorage.setItem("bicky_user", JSON.stringify(currentUser));
    updateAuthUI(); showMessage("authMessage", `Welcome${currentUser.fullName ? ", " + currentUser.fullName : ""}! You can book below.`);
    $("bookingForm").classList.remove("hidden"); $("myBookings").classList.remove("hidden");
    await loadBookings();
  } catch(err) { showMessage("authMessage", err.message, true); }
});
$("bookingForm").addEventListener("submit", async e => {
  e.preventDefault();
  if (!token) { showMessage("bookingMessage", "Please log in before booking.", true); return; }
  const payload = {serviceName:$("service").value, bookingDate:$("bookingDate").value, bookingTime:$("bookingTime").value, notes:$("notes").value.trim()};
  showMessage("bookingMessage", "Saving your booking…");
  try {
    const data = await api("/api/bookings", {method:"POST", body:JSON.stringify(payload)});
    showMessage("bookingMessage", data.message || "Booking confirmed!");
    $("notes").value = ""; await loadBookings();
  } catch(err) { showMessage("bookingMessage", err.message, true); }
});
async function loadBookings() {
  if (!token) return;
  try {
    const bookings = await api("/api/bookings");
    $("bookingList").innerHTML = "";
    if (!bookings.length) { $("bookingList").textContent = "No appointments yet."; return; }
    bookings.forEach(b => {
      const div = document.createElement("div"); div.className = "booking-item";
      const title = document.createElement("b"); title.textContent = `${b.serviceName} — ₹${prices[b.serviceName] || ""}`;
      const details = document.createElement("div"); details.textContent = `${b.bookingDate} at ${b.bookingTime} · ${b.status}${b.notes ? " · " + b.notes : ""}`;
      div.append(title, details);
      if (b.status === "BOOKED") {
        const cancel = document.createElement("button"); cancel.textContent = "Cancel";
        cancel.addEventListener("click", async () => { try { await api(`/api/bookings/${b.id}/cancel`, {method:"PATCH"}); await loadBookings(); } catch(err) { alert(err.message); } });
        div.append(cancel);
      }
      $("bookingList").append(div);
    });
  } catch(err) { showMessage("bookingMessage", err.message, true); }
}
function updateAuthUI() {
  const loggedIn = Boolean(token && currentUser);
  $("loginNav").classList.toggle("hidden", loggedIn);
  $("logoutNav").classList.toggle("hidden", !loggedIn);
  $("authForm").classList.toggle("hidden", loggedIn);
  $("bookingForm").classList.toggle("hidden", !loggedIn);
  $("myBookings").classList.toggle("hidden", !loggedIn);
  if (loggedIn) $("authTitle").textContent = `Hi, ${currentUser.fullName || "there"}.`;
}
function logout() {
  token = ""; currentUser = null; localStorage.removeItem("bicky_token"); localStorage.removeItem("bicky_user");
  updateAuthUI(); $("bookingList").innerHTML = ""; showMessage("authMessage", "You have logged out.");
  setTab("login");
}
async function restoreSession() {
  updateAuthUI();
  if (token && currentUser) {
    $("bookingForm").classList.remove("hidden"); $("myBookings").classList.remove("hidden");
    await loadBookings();
  }
}
restoreSession();
