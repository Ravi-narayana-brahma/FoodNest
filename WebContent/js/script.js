// Toggle nav
const menuIcon = document.querySelector(".menu-icon");
const nav = document.querySelector(".nav");

menuIcon.addEventListener("click", () => {
    nav.classList.toggle("active");
});

const navLinks = nav.querySelectorAll('a');
const homeContent = document.querySelector('.home-content');


// Function to hide all main sections
function hideAllSections() {
    homeContent.style.display = 'none';
    loginForm.style.display = 'none';
    signupForm.style.display = 'none';
    categoriesWrapper.style.display = 'none';
    cartWrapper.style.display = 'none';
    checkoutPage.style.display = 'none';
    myOrdersPage.style.display = 'none';
    paymentPage.style.display = 'none';
    document.getElementById("restaurant-page").style.display = "none";
    document.getElementById("restaurants-page").style.display = "none"; // ✅ Added line
    profilePage.style.display = 'none';
    if (successMsg) successMsg.style.display = 'none';
}





// Handle navbar link clicks
navLinks.forEach(link => {
	  link.addEventListener('click', e => {
	    e.preventDefault();

	    // Remove active class and add to clicked link
	    navLinks.forEach(a => a.classList.remove('active'));
	    link.classList.add('active');

	    // Collapse nav on click (for mobile)
	    nav.classList.remove('active');

	    // Hide all sections first
	    hideAllSections();

	    const target = link.textContent.trim();

	    if (target === 'Home') {
	      homeContent.style.display = 'block';
	    } else if (target === 'Categories') {
	      categoriesWrapper.style.display = 'block';
	    } else if (target.includes('Cart')) {
	      cartWrapper.style.display = 'block';
	    } else if (target === 'Login') {
	      loginForm.style.display = 'block';
	    } else if (target === 'My Orders') {
	      showMyOrders();
	    } else if (target === 'Restaurants') {
	      showRestaurants(); // ✅ Load all restaurants from backend
	    }
	  }); // <-- this is the correct end of eventListener
	});   // <-- this is the correct end of forEach




// Login / Signup Modals
const loginBtn = document.getElementById("login-btn");
const loginForm = document.getElementById("login-form");
const loginCloseBtn = loginForm.querySelector(".close-btn");
const signupBtn = document.getElementById("signup-btn");
const signupForm = document.getElementById("signup-form");
const signupCloseBtn = signupForm.querySelector(".close-btn");
const loginLink = document.getElementById("login-link");

loginBtn.addEventListener("click", (e) => {
    e.preventDefault();
    loginForm.style.display = "block";
    signupForm.style.display = "none";
});

loginCloseBtn.addEventListener("click", () => {
    loginForm.style.display = "none";
});

signupBtn.addEventListener("click", (e) => {
    e.preventDefault();
    signupForm.style.display = "block";
    loginForm.style.display = "none";
});

loginLink.addEventListener("click", (e) => {
    e.preventDefault();
    loginForm.style.display = "block";
    signupForm.style.display = "none";
});

signupCloseBtn.addEventListener("click", () => {
    signupForm.style.display = "none";
});

window.addEventListener("click", (e) => {
    if (e.target === loginForm) loginForm.style.display = "none";
    if (e.target === signupForm) signupForm.style.display = "none";
});

// Signup Validation
const signupFormEl = document.querySelector("#signup-form form");
const loginModal = document.getElementById("login-form");
const signupModal = document.getElementById("signup-form");
const successMsg = document.getElementById("signup-success");

signupFormEl.addEventListener("submit", async function (e) {
  e.preventDefault();

  const pwd = document.getElementById("new-password").value.trim();
  const cpwd = document.getElementById("confirm-password").value.trim();
  const passwordError = document.getElementById("password-error");
  const strongPwd = /^(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,}$/;

  passwordError.style.display = "none";

  if (!strongPwd.test(pwd)) {
    passwordError.textContent = "❗ Weak password";
    passwordError.style.display = "block";
    return;
  }
  if (pwd !== cpwd) {
    passwordError.textContent = "❗ Passwords do not match";
    passwordError.style.display = "block";
    return;
  }

  // ✅ Build FormData and send request
  const fd = new URLSearchParams(new FormData(signupFormEl));

  try {
    const res = await fetch("auth", {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded"
      },
      body: fd.toString()
    });

    const text = await res.text();
    if (text.includes("✅")) {
      signupModal.style.display = "none";
      loginModal.style.display = "block";
      successMsg.textContent = "Signup successful! Please log in.";
      successMsg.style.display = "block";
      setTimeout(() => successMsg.style.display = "none", 5000);
    } else {
      alert(text);
    }
  } catch (err) {
    console.error(err);
    alert("Server error during signup");
  }
});


document.querySelector("#login-form form").addEventListener("submit", async function (e) {
	  e.preventDefault();

	  const fd = new URLSearchParams(new FormData(this));

	  try {
	    const res = await fetch("auth", {
	      method: "POST",
	      headers: {
	        "Content-Type": "application/x-www-form-urlencoded"
	      },
	      body: fd.toString()
	    });

	    const text = await res.text();
	    console.log("Server Response:", text);

	    if (text.includes("Login success")) {
	    	  console.log("✅ Login success block hit");
	    	  loginForm.style.display = "none";
	    	  showToast("✅ Logged in successfully!");
	    	  loginBtn.style.display = "none";
	    	  profileLink.style.display = "inline-block";
	    	  

	    	  hideAllSections();
	    	  console.log("👉 Trying to show profile page");
	    	  homeContent.style.display = 'block';

	    	  loadProfile();
	    	} else {
	    		  profileLink.style.display = "none";
	    		  profilePage.style.display = "none";
	    		  alert("❌ " + text);
	    		}
	    
	  } catch (err) {
	    console.error(err);
	    alert("Server error during login");
	  }
	});


// Category Loading
const categoryLink = document.querySelector('a[href="#categories"]');
const categoriesWrapper = document.getElementById('categories-wrapper');
const categoryGrid = document.getElementById('categoryGrid');
const closeBtn = document.querySelector('.closeb-btn');
const categoryNavLinks = document.querySelectorAll('.category-navbar a');
let allCategories = [];

categoryLink.addEventListener('click', (e) => {
    e.preventDefault();
    categoriesWrapper.style.display = 'block';
    if (allCategories.length === 0) loadCategories();
    else displayCategories('all');
});

closeBtn.addEventListener('click', () => {
    categoriesWrapper.style.display = 'none';
});

function loadCategories() {
    fetch("allMenuItems")
        .then(res => res.json())
        .then(data => {
            allCategories = data;
            displayCategories('all');
        })
        .catch(err => {
            categoryGrid.innerHTML = `<p>Error loading menu items.</p>`;
            console.error(err);
        });
}

function displayCategories(type) {
    categoryGrid.innerHTML = '';
    const filtered = type === 'all' ? allCategories : allCategories.filter(item => item.type === type);

    if (filtered.length === 0) {
        categoryGrid.innerHTML = `<p>No items available.</p>`;
        return;
    }

    filtered.forEach(cat => {
        const card = document.createElement('div');
        card.className = 'category-card';
        card.innerHTML = `
          <h3 class="category-name">${cat.name}</h3>
          <p class="category-price">₹${cat.price}</p>
        `;

        const button = document.createElement('button');
        button.className = "add-to-cart-btn";
        button.textContent = "Add to Cart";
        button.addEventListener("click", () => addToCart(cat));  // ✅ sends full item including vendor_phone
        card.appendChild(button);

        categoryGrid.appendChild(card);
    });
}


categoryNavLinks.forEach(link => {
    link.addEventListener('click', (e) => {
        e.preventDefault();
        categoryNavLinks.forEach(a => a.classList.remove('active'));
        link.classList.add('active');
        const type = link.getAttribute('data-type');
        displayCategories(type);
    });
});

document.querySelector('.closeb-btn').addEventListener('click', () => {
    categoriesWrapper.style.display = 'none';
});

// Cart Logic
const cartBtn = document.getElementById('cart-btn');
const cartWrapper = document.getElementById('cart-wrapper');
const cartGrid = document.getElementById('cartGrid');
const cartTotal = document.getElementById('cart-total');
const cartCloseBtn = document.querySelector('.close-cart-btn');

cartBtn.addEventListener('click', (e) => {
    e.preventDefault();
    showCart();
});

cartWrapper.style.display = 'block';
async function showCart() {
	  const emptyMsg = document.getElementById("empty-cart-message");
	  cartGrid.innerHTML = "";
	  cartTotal.textContent = "Total: ₹0";

	  try {
		  const cartItems = await fetch("cart", { credentials: "include" })
		                     .then(r => r.json());

	    if (!Array.isArray(cartItems) || cartItems.length === 0) {
	      emptyMsg.style.display = "block";
	      return;
	    }

	    emptyMsg.style.display = "none";
	    let total = 0;

	    cartItems.forEach(item => {
	      total += item.price * item.quantity;

	      const div = document.createElement("div");
	      div.className = "cart-item";
	      div.innerHTML = `
	        <img src="${item.image}" alt="${item.name}" />
	        <div class="cart-details">
	          <h3>${item.name}</h3>
	          <p>₹${item.price}</p>
	          <span class="quantity">Qty: ${item.quantity}</span>
	        </div>
	      `;
	      cartGrid.appendChild(div);
	    });

	    cartTotal.textContent = `Total: ₹${total}`;
	  } catch (err) {
	    console.error("Fetch cart failed:", err);
	    cartGrid.innerHTML = `<p>Error loading cart.</p>`;
	  }
	}


// Empty cart button
document.getElementById('empty-cart-btn').addEventListener('click', () => {
	  fetch("cart", { method: "DELETE" })
	    .then(r => r.text())
	    .then(msg => {
	      showToast(msg || "Cart emptied.");
	      sessionStorage.removeItem("cart_vendor"); // clear vendor info
	      showCart();
	    })
	    .catch(err => {
	      console.error("Cart clear failed", err);
	      alert("Failed to clear cart.");
	    });
	});



/* ─── Toast STYLE injection (run once at startup) ─── */
(function injectToastStyles() {
    const toastStyle = document.createElement('style');
    toastStyle.innerHTML = `
@keyframes toastFadeIn  {from{opacity:0;transform:translate(-50%,8px)}
                          to  {opacity:1;transform:translate(-50%,0)}}
@keyframes toastFadeOut {from{opacity:1;transform:translate(-50%,0)}
                          to  {opacity:0;transform:translate(-50%,8px)}}

.toast-message{
  position:absolute;              /* absolute inside its parent panel  */
  left:50%;
  bottom:20px;
  transform:translateX(-50%);
  background:#10b981;
  color:#fff;
  padding:12px 24px;
  border-radius:8px;
  font-weight:600;
  font-size:14px;
  box-shadow:0 4px 12px rgba(0,0,0,.25);
  opacity:0;
  pointer-events:none;            /* never blocks clicks               */
  z-index:99999;                  /* on top of anything inside panel   */
}

.toast-message.visible{
  animation:toastFadeIn .3s forwards,
            toastFadeOut .3s 2.7s forwards; /* stay ~2.4s, fade .3s      */
}
`;
    document.head.appendChild(toastStyle);
})();
function showToast(message) {
    // 1. Build the toast element
    const toast = document.createElement('div');
    toast.className = 'toast-message';
    toast.textContent = message;

    // 2. Decide which panel is currently visible
    const panel =
        cartWrapper.style.display === 'block' ? cartWrapper :
            categoriesWrapper.style.display === 'block' ? categoriesWrapper :
                homeContent.style.display === 'block' ? homeContent :
                    document.body;  // fallback for login/signup or none open

    // 3. Append toast to that panel and trigger animation
    panel.appendChild(toast);
    requestAnimationFrame(() => toast.classList.add('visible'));

    // 4. Remove after animation completes (~3 s)
    setTimeout(() => toast.remove(), 3000);
}
// ✅ Checkout Logic
const checkoutBtn = document.getElementById('checkout-btn');
const checkoutPage = document.getElementById('checkout-page');
const checkoutItems = document.getElementById('checkout-items');
const checkoutTotal = document.getElementById('checkout-total');

checkoutBtn.addEventListener('click', () => {
    hideAllSections();
    checkoutPage.style.display = 'block';

    (async () => {
    	  const cart = await fetch("cart", { credentials: "include" }).then(r => r.json());
    	  checkoutItems.innerHTML = '';
    	  let total = 0;
    	  cart.forEach(item => {
    	    const div = document.createElement('div');
    	    div.innerHTML = `<p>${item.name} – ₹${item.price} × ${item.quantity}</p>`;
    	    checkoutItems.appendChild(div);
    	    total += item.price * item.quantity;
    	  });
    	  checkoutTotal.textContent = `Total Payable: ₹${total}`;
    	})();

    checkoutItems.innerHTML = '';
    let total = 0;

    cart.forEach(item => {
        const div = document.createElement('div');
        div.innerHTML = `<p>${item.name} - ₹${item.price} × ${item.quantity}</p>`;
        checkoutItems.appendChild(div);
        total += item.price * item.quantity;
    });

    checkoutTotal.textContent = `Total Payable: ₹${total}`;
});
/* ===== Payment‑page handler ===== */
const paymentPage = document.getElementById('payment-page');   // you already have this
const payNowBtn = document.getElementById('pay-now-btn');    // ⬅ button in checkout page

// show payment options when “Pay Now” is clicked
payNowBtn.addEventListener('click', () => {
  const addressInput = document.getElementById('delivery-address');
  const address = addressInput.value.trim();
  const addressError = document.getElementById('address-error');

  if (!address) {
    addressError.style.display = 'block';
    addressInput.style.border = '2px solid red';
    addressInput.focus();
    return;
  } else {
    addressError.style.display = 'none';
    addressInput.style.border = '1px solid #ccc';
  }

  hideAllSections();
  paymentPage.style.display = 'block';
});



async function confirmPayment(method) {
	  const emptyMsg = document.getElementById("cart-empty-msg");
	  const address = document.getElementById("delivery-address").value.trim();

	  if (!address) {
	    emptyMsg.textContent = "❗ Please enter your delivery address.";
	    emptyMsg.style.display = "block";
	    return;
	  }

	  try {
	    const fd = new URLSearchParams();
	    fd.append("address", address);
	    fd.append("paymentMethod", method);
	    

	    const res = await fetch("order", {
	      method: "POST",
	      credentials: "include",
	      headers: {
	        "Content-Type": "application/x-www-form-urlencoded"
	      },
	      body: fd.toString(),
	    });

	    const data = await res.json();

	    if (data.success) {
	      alert(`✅ ${data.message}`);
	      emptyMsg.style.display = "none";
	      showMyOrders(); // redirect to orders after placing
	    } else {
	      emptyMsg.textContent = "❌ " + data.message;
	      emptyMsg.style.display = "block";
	    }

	  } catch (err) {
	    console.error("Order error:", err);
	    emptyMsg.textContent = "❌ Failed to place order.";
	    emptyMsg.style.display = "block";
	  }
	}


const ordersList = document.getElementById('orders-list');
const myOrdersPage = document.getElementById('my-orders-page');

async function showMyOrders() {
	  hideAllSections();
	  myOrdersPage.style.display = 'block';
	  ordersList.innerHTML = '';

	  try {
	    const orders = await fetch("orders", { credentials: "include" }).then(r => r.json());

	    if (!Array.isArray(orders) || orders.length === 0) {
	      ordersList.innerHTML = '<p>You have no orders yet.</p>';
	      return;
	    }

	    orders.forEach((order, index) => {
	      const orderDiv = document.createElement('div');
	      orderDiv.className = 'order-block';

	      const itemsHtml = order.items.map(item => `
	        <li style="margin-bottom: 6px;">
	          ${item.name} - ₹${item.price} × ${item.quantity}
	        </li>
	      `).join('');

	      // Add color-coded status
	      let statusClass = '';
	      if (order.status === 'Confirmed') {
	        statusClass = 'status-confirmed';
	      } else if (order.status === 'Pending') {
	        statusClass = 'status-pending';
	      } else {
	        statusClass = 'status-other';
	      }

	      // Format time (optional)
	      const orderTime = new Date(order.time).toLocaleString();

	      orderDiv.innerHTML = `
	        <h3>Order #${index + 1} - ${orderTime}</h3>
	        <p><strong>Paid via:</strong> ${order.paymentMethod}</p>
	        <p><strong>Status:</strong> <span class="status ${statusClass}">${order.status}</span></p>
	        <p><strong>Delivery Address:</strong><br>${order.address}</p>
	        <ul style="padding-left: 15px; margin-top: 10px;">${itemsHtml}</ul>
	        <hr>
	      `;

	      ordersList.appendChild(orderDiv);
	    });

	  } catch (err) {
	    console.error("Failed to load orders:", err);
	    ordersList.innerHTML = `<p>❌ Error loading orders.</p>`;
	  }
	}


function goBackToHome() {
    hideAllSections();
    homeContent.style.display = 'block';
}
/* ===== Initial view: show Home, hide everything else ===== */
window.addEventListener('DOMContentLoaded', async () => {
    hideAllSections();
    homeContent.style.display = 'block';

    // ✅ Check session login from backend (not localStorage)
    try {
        const res = await fetch("auth", { method: "GET", credentials: "include" });
        const data = await res.json();

        if (!data.error) {
            // User is logged in via session
            document.getElementById("profile-link").style.display = "inline-block";
            document.getElementById("login-btn").style.display = "none";
        } else {
            // Not logged in
            document.getElementById("profile-link").style.display = "none";
            document.getElementById("login-btn").style.display = "inline-block";
        }
    } catch (err) {
        console.error("Session check failed:", err);
    }

    navLinks.forEach(a => a.classList.remove('active'));
    const homeLink = Array.from(navLinks).find(a => a.textContent.trim() === 'Home');
    if (homeLink) homeLink.classList.add('active');
});


const profilePage = document.getElementById('profile-page');
const profileLink = document.getElementById('profile-link');

profileLink.addEventListener('click', (e) => {
    e.preventDefault();
    hideAllSections();
    profilePage.style.display = 'block';
    loadProfile(); // ✅ Load data into profile section
});

function loadProfile() {
	  fetch("auth", { method: "GET" })
	    .then(res => res.json())
	    .then(data => {
	      if (data.error) {
	        document.getElementById("profile-info").innerHTML = `<p>${data.error}</p>`;
	        return;
	      }

	      document.getElementById("profile-info").innerHTML = `
	        <p><strong>Name:</strong> ${data.name}</p>
	        <p><strong>Mobile:</strong> ${data.mobile}</p>
	        <p><strong>Address:</strong> ${data.address}</p>
	      `;
	    })
	    .catch(err => {
	      console.error("Error loading profile:", err);
	      document.getElementById("profile-info").innerHTML = `<p>Error loading profile</p>`;
	    });
	}



document.getElementById("logout-btn").addEventListener("click", async () => {
	  const fd = new URLSearchParams();
	  fd.append("action", "logout");

	  try {
	    const res = await fetch("auth", {
	      method: "POST",
	      headers: {
	        "Content-Type": "application/x-www-form-urlencoded"
	      },
	      body: fd.toString(),            // ✅ Must be string
	      credentials: "include"          // ✅ Send cookies/session
	    });

	    const text = await res.text();
	    console.log("Logout response:", text);

	    if (text.includes("?")) {
	      showToast("✅ Logged out successfully");

	      // ✅ Reset UI
	      document.getElementById("login-btn").style.display = "inline-block";
	      document.getElementById("profile-link").style.display = "none";

	      hideAllSections();
	      homeContent.style.display = "block";

	      // Update nav active state
	      navLinks.forEach(link => link.classList.remove("active"));
	      const homeLink = Array.from(navLinks).find(link => link.textContent.trim() === "Home");
	      if (homeLink) homeLink.classList.add("active");
	    } else {
	      alert("❌ Logout failed: " + text);
	    }
	  } catch (err) {
	    console.error("Logout failed:", err);
	    alert("❌ Logout error");
	  }
	});
function showRestaurants() {
	  hideAllSections();
	  document.getElementById("restaurants-page").style.display = "block";

	  const container = document.getElementById("restaurant-list");
	  container.innerHTML = "<p>Loading...</p>";

	  fetch("restaurants")
	    .then(res => res.json())
	    .then(data => {
	      container.innerHTML = "";
	      data.forEach(r => {
	        const card = document.createElement("div");
	        card.className = "restaurant-card";
	        card.innerHTML = `
	          <img src="images/restaurant1.jpg" alt="${r.name}">
	          <h3>${r.name}</h3>
	          <p>📍 ${r.city} | 🍽️ ${r.type}</p>
	        `;

	        // ✅ Use phone to fetch real menu
	        card.addEventListener("click", () => {
	          showRestaurantPage(r.name, "4.5", "🔥 Special Offer", r.phone); // Pass vendor_phone
	        });

	        container.appendChild(card);
	      });
	    })
	    .catch(err => {
	      container.innerHTML = "<p>Failed to load restaurants.</p>";
	      console.error(err);
	    });
	}

function showRestaurantPage(name, rating, offer, vendorPhone) {
	  hideAllSections();
	  document.getElementById("restaurant-page").style.display = "block";

	  // Set restaurant name, rating, and offer
	  document.getElementById("restaurant-name").textContent = name;
	  document.getElementById("restaurant-rating").textContent = `⭐ ${rating}`;
	  document.getElementById("restaurant-offer").textContent = offer;

	  const grid = document.getElementById("restaurant-menu-grid");
	  grid.innerHTML = "<p>Loading menu...</p>";

	  // Fetch menu items from the backend using vendor phone
	  fetch(`restaurantMenu?vendor_phone=${encodeURIComponent(vendorPhone)}`)
	    .then(res => res.json())
	    .then(menuItems => {
	      grid.innerHTML = "";

	      if (!Array.isArray(menuItems) || menuItems.length === 0) {
	        grid.innerHTML = "<p>No items available.</p>";
	        return;
	      }

	      menuItems.forEach(item => {
	    	item.vendor_phone = vendorPhone;
	        const card = document.createElement("div");
	        card.className = "menu-card";
	        card.innerHTML = `
	          <h4>${item.name}</h4>
	          <p>₹${item.price}</p>
	        `;

	        const button = document.createElement("button");
	        button.className = "add-to-cart-btn";
	        button.textContent = "Add to Cart";
	        button.addEventListener("click", () => addToCart(item));
	        card.appendChild(button);

	        grid.appendChild(card);
	      });
	    })
	    .catch(err => {
	      console.error("Failed to load menu:", err);
	      grid.innerHTML = "<p>Error loading menu.</p>";
	    });
	}
//✅ Reusable Add to Cart function
function addToCart(item) {
	  if (!item || !item.vendor_phone) {
	    alert("Item or vendor information missing.");
	    return;
	  }

	  const currentVendor = sessionStorage.getItem("cart_vendor");
	  const itemVendor = String(item.vendor_phone); // normalize

	  // Check if cart already has a different vendor's item
	  if (currentVendor && currentVendor !== itemVendor) {
	    showCartAlert("🧹 Cart contains items from another vendor. Please clear the cart to add this item.");
	    return;
	  }

	  // Set vendor if this is the first item
	  if (!currentVendor) {
	    sessionStorage.setItem("cart_vendor", itemVendor);
	  }

	  // Add the item to the cart
	  fetch("cart", {
	    method: "POST",
	    credentials: "include",
	    headers: {
	      "Content-Type": "application/x-www-form-urlencoded"
	    },
	    body: new URLSearchParams({
	      name: item.name,
	      price: item.price,
	      image: item.image || "",
	      quantity: 1,
	      vendor_phone: itemVendor
	    })
	  })
	    .then(res => res.text())
	    .then(() => {
	      showToast(`${item.name} added to cart!`);
	    })
	    .catch(err => {
	      console.error("Add to cart failed:", err);
	      alert("Failed to add to cart (server error)");
	    });
	}

function showCartAlert(message) {
	  const alertBox = document.getElementById("cart-alert");
	  alertBox.textContent = message;
	  alertBox.style.display = "block";

	  alertBox.style.animation = "none";
	  void alertBox.offsetWidth; // Reset animation
	  alertBox.style.animation = "fadeOut 3s forwards";

	  setTimeout(() => {
	    alertBox.style.display = "none";
	  }, 3000);
	}
