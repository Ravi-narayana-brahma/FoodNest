document.addEventListener("DOMContentLoaded", () => {
  const loginLink = document.getElementById("login-link");
  const signupLink = document.getElementById("signup-link");
  const profileLink = document.getElementById("profile-link");
  const logoutBtn = document.getElementById("logout-btn");

  const loginForm = document.getElementById("login-form");
  const signupForm = document.getElementById("signup-form");
  const profileSection = document.getElementById("profile-section");
  const toast = document.getElementById("toast");

  const homeSection = document.getElementById("home-section");
  const ordersSection = document.getElementById("orders-section");
  const dashboardSection = document.getElementById("restaurant-dashboard");

  const menuIcon = document.querySelector(".menu-icon");
  const nav = document.querySelector(".nav");
  
  // ===== NAV TOGGLE =====
  menuIcon.addEventListener("click", () => nav.classList.toggle("active"));

  // ===== NAVIGATION LINKS =====
  const homeLink = document.getElementById("home-link");
  const ordersLink = document.getElementById("orders-link");
  const dashboardLink = document.getElementById("dashboard-link");

  function hideAllSections() {
    homeSection.style.display = "none";
    ordersSection.style.display = "none";
    dashboardSection.style.display = "none";
    profileSection.style.display = "none";
    loginForm.style.display = "none";
    signupForm.style.display = "none";
  }
  
  homeLink.addEventListener("click", () => {
    hideAllSections();
    homeSection.style.display = "block";
  });

  ordersLink.addEventListener("click", () => {
    hideAllSections();
    ordersSection.style.display = "block";
  });

  dashboardLink.addEventListener("click", () => {
    hideAllSections();
    dashboardSection.style.display = "block";
  });

  // ===== LOGIN / SIGNUP HANDLERS =====
  loginLink.addEventListener("click", () => {
    hideAllSections();
    loginForm.style.display = "block";
  });

  signupLink.addEventListener("click", () => {
    hideAllSections();
    signupForm.style.display = "block";
  });
  const isLoggedIn = !!sessionStorage.getItem("vendor_phone");

  if (!isLoggedIn) {
    ordersSection.style.display = "none";
    dashboardSection.style.display = "none";
    profileLink.style.display = "none";
    logoutBtn.style.display = "none";
    loginLink.style.display = "inline-block";
    signupLink.style.display = "inline-block";
  }

  document.querySelector("#login-form form").addEventListener("submit", async (e) => {
    e.preventDefault();
    const fd = new URLSearchParams(new FormData(e.target));

    const res = await fetch("vendorAuth?action=login", {
      method: "POST",
      body: fd
    });

    const result = await res.text();
    if (result.includes("success")) {
    	  const phone = fd.get("vendor_phone");
    	  sessionStorage.setItem("vendor_phone", phone);
    	  showToast("Login Successful");

    	  loginForm.style.display = "none";
    	  loginLink.style.display = "none";
    	  signupLink.style.display = "none";
    	  profileLink.style.display = "inline-block";
    	  logoutBtn.style.display = "inline-block";

    	  // Show main sections
    	  homeSection.style.display = "block";
    	  ordersSection.style.display = "block";
    	  dashboardSection.style.display = "block";

    	  if (sessionStorage.getItem("vendor_phone")) {
    		  loadProfile();
    		  loadMenu();
    		  loadVendorOrders();
    		  updateDashboardStats();
    	 }

    	

    } else {
      showToast("Login Failed", true);
    }
  });

  document.querySelector("#signup-form form").addEventListener("submit", async (e) => {
    e.preventDefault();
    const fd = new URLSearchParams(new FormData(e.target));
    const res = await fetch("vendorAuth?action=signup", {
      method: "POST",
      body: fd
    });
    const result = await res.text();
    if (result.includes("success")) {
      showToast("Signup Successful");
      signupForm.style.display = "none";
      loginForm.style.display = "block";
    } else {
      showToast("Signup Failed", true);
    }
  });

  logoutBtn.addEventListener("click", async () => {
	  await fetch("vendorAuth?action=logout");
	  sessionStorage.removeItem("vendor_phone"); // Remove phone from session

	  loginLink.style.display = "inline-block";
	  signupLink.style.display = "inline-block";
	  profileLink.style.display = "none";
	  logoutBtn.style.display = "none";

	  hideAllSections();
	  homeSection.style.display = "block";

	  // ✅ Clear previous data
	  document.getElementById("vendor-orders-list").innerHTML = "";
	  document.getElementById("menu-list").innerHTML = "";

	  showToast("Logged out successfully");
	});


  profileLink.addEventListener("click", async () => {
	  hideAllSections();                 // Hide all other sections
	  await loadProfile();              // Load profile data
	  profileSection.style.display = "block";  // Now SHOW the profile section
	});

  async function loadProfile() {
	  const res = await fetch("vendorAuth?action=profile");
	  const data = await res.json();
	  if (data && data.vendor_phone) {
	    document.getElementById("profile-details").innerHTML = `
	      <p><strong>Name:</strong> ${data.first_name} ${data.last_name}</p>
	      <p><strong>Phone:</strong> ${data.vendor_phone}</p>
	      <p><strong>Email:</strong> ${data.email}</p>
	      <p><strong>Restaurant:</strong> ${data.restaurant_name}</p>
	      <p><strong>City:</strong> ${data.city}</p>
	      <p><strong>Type:</strong> ${data.type}</p>
	    `;
	  }
	}


  function showToast(message, error = false) {
    toast.textContent = message;
    toast.style.background = error ? "#dc2626" : "#10b981";
    toast.style.zIndex = "9999";
    toast.classList.add("visible");
    setTimeout(() => toast.classList.remove("visible"), 3000);
  }

  // On load, check session
  loadProfile();
});
document.addEventListener("DOMContentLoaded", () => {
	  const vendorPhone = sessionStorage.getItem("vendor_phone");
	  // set this after login
	  const menuList = document.getElementById("menu-list");
	  const addBtn = document.getElementById("add-item-btn");

	  // Load vendor menu
	  async function loadMenu() {
	    if (!vendorPhone) return;

	    const res = await fetch(`vendorMenu?vendor_phone=${vendorPhone}`);
	    const items = await res.json();

	    menuList.innerHTML = "";
	    items.forEach(item => {
	      const div = document.createElement("div");
	      div.className = "menu-item";
	      div.innerHTML = `
	        <h4>${item.name}</h4>
	        <p>₹${item.price}</p>
	        <p>${item.type}</p>
	        <button class="edit-item" data-id="${item.id}">Edit</button>
	        <button class="delete-item" data-id="${item.id}">Delete</button>
	      `;
	      menuList.appendChild(div);
	    });
	  }

	  // Add new item
	  addBtn.addEventListener("click", async () => {
	    const name = document.getElementById("item-name").value;
	    const price = document.getElementById("item-price").value;
	    const type = document.getElementById("item-type").value;

	    if (!name || !price || !type) return alert("All fields required");

	    const data = new URLSearchParams();
	    data.append("action", "add");
	    data.append("vendor_phone", vendorPhone);
	    data.append("item_name", name);
	    data.append("item_price", price);
	    data.append("item_type", type);

	    const res = await fetch("vendorMenu", {
	      method: "POST",
	      headers: { "Content-Type": "application/x-www-form-urlencoded" },
	      body: data,
	    });

	    if ((await res.text()) === "success") {
	      document.getElementById("item-name").value = "";
	      document.getElementById("item-price").value = "";
	      document.getElementById("item-type").value = "";
	      loadMenu();
	    } else {
	      alert("Failed to add item");
	    }
	  });

	  // Handle delete/edit
	  menuList.addEventListener("click", async (e) => {
	    const id = e.target.dataset.id;
	    if (e.target.classList.contains("delete-item")) {
	      if (!confirm("Delete this item?")) return;

	      const data = new URLSearchParams();
	      data.append("action", "delete");
	      data.append("item_id", id);
	      data.append("vendor_phone", vendorPhone);
	      
	      const res = await fetch("vendorMenu", {
	        method: "POST",
	        headers: { "Content-Type": "application/x-www-form-urlencoded" },
	        body: data,
	      });

	      const text = await res.text();
	      if (text === "deleted") loadMenu();
	      else alert("Failed to delete");
	    }

	    if (e.target.classList.contains("edit-item")) {
	      const name = prompt("New item name:");
	      const price = prompt("New price:");
	      const type = prompt("New type (Veg/Non-Veg):");

	      if (!name || !price || !type) return;

	      const data = new URLSearchParams();
	      data.append("action", "edit"); // Add edit support in servlet if needed
	      data.append("item_id", id);
	      data.append("item_name", name);
	      data.append("item_price", price);
	      data.append("item_type", type);
	      data.append("vendor_phone", vendorPhone);

	      const res = await fetch("vendorMenu", {
	        method: "POST",
	        headers: { "Content-Type": "application/x-www-form-urlencoded" },
	        body: data,
	      });

	      const text = await res.text();
	      if (text === "success") loadMenu();
	      else alert("Edit failed");
	    }
	  });

	  loadMenu();
	});
document.addEventListener("DOMContentLoaded", () => {
	  const vendorOrdersLink = document.getElementById("orders-link");
	  const ordersSection = document.getElementById("orders-section");
	  const vendorOrdersList = document.getElementById("vendor-orders-list");

	  vendorOrdersLink.addEventListener("click", (e) => {
	    e.preventDefault();
	    hideAllSections();
	    ordersSection.style.display = "block";
	    loadVendorOrders();
	  });

	  async function loadVendorOrders() {
		 const vendorPhone = sessionStorage.getItem("vendor_phone");
		 if (!vendorPhone) {
		    vendorOrdersList.innerHTML = "<p>Please login to view orders.</p>";
		    return;
		 }
	    console.log("Fetching orders...");
	    try {
	      const res = await fetch("/FoodNest/vendorOrders");
	      const data = await res.json();
	      console.log("Vendor Orders:", data);

	      vendorOrdersList.innerHTML = "";

	      if (!data.length) {
	    	  vendorOrdersList.innerHTML = '<p class="no-orders-message">No orders found.</p>';
	        return;
	      }

	      data.forEach(order => {
	        const orderDiv = document.createElement("div");
	        orderDiv.classList.add("order");

	        const itemsHtml = order.items.map(item =>
	          `<li>${item.name} - ${item.quantity} x ₹${item.price}</li>`
	        ).join("");

	        orderDiv.innerHTML = `
	        <div class="order-card">
	          <h3>Order ID: ${order.id}</h3>
	          <p><strong>Address:</strong> ${order.address}</p>
	          <p><strong>Customer Mobile:</strong> ${order.user_mobile}</p>
	          <p><strong>Payment Method:</strong> ${order.paymentMethod}</p>
	          <p><strong>Time:</strong> ${order.time}</p>
	          <p><strong>Status:</strong> ${order.status}</p>
	          <ul>${itemsHtml}</ul>
	          ${order.status === "Pending" 
	            ? `<button class="confirm-order-btn" data-id="${order.id}">Confirm Order</button>` 
	            : ""}
	        </div>
	        <hr>
	      `;


	        vendorOrdersList.appendChild(orderDiv);
	      });
	   // Attach event listeners for "Confirm Order" buttons
	      document.querySelectorAll(".confirm-order-btn").forEach(btn => {
	        btn.addEventListener("click", async () => {
	          const orderId = btn.dataset.id;
	          const confirm = window.confirm("Are you sure you want to confirm this order?");
	          if (!confirm) return;

	          try {
	            const res = await fetch("/FoodNest/vendorOrders", {
	              method: "POST",
	              headers: { "Content-Type": "application/json" },
	              body: JSON.stringify({ orderId: parseInt(orderId) })
	            });

	            const data = await res.json();
	            if (data.message) {
	              alert("✅ " + data.message);
	              loadVendorOrders(); // Refresh the list
	            } else {
	              alert("❌ " + (data.error || "Failed to confirm"));
	            }
	          } catch (err) {
	            console.error("Failed to confirm order:", err);
	          }
	        });
	      });

	    } catch (err) {
	      console.error("Error loading vendor orders:", err);
	      vendorOrdersList.innerHTML = "<p>Failed to load orders.</p>";
	    }
	  }

	  function hideAllSections() {
	    document.querySelectorAll(".home-section, .orders-section, .restaurant-dashboard, .profile-section").forEach(sec => {
	      sec.style.display = "none";
	    });
	  }
	});
async function updateDashboardStats() {
	  const vendorPhone = sessionStorage.getItem("vendor_phone");
	  if (!vendorPhone) return;

	  try {
	    const res = await fetch(`/FoodNest/vendorStats?vendor_phone=${vendorPhone}`);
	    const data = await res.json();

	    // Update DOM elements
	    document.getElementById("total-orders").textContent = data.totalOrders || 0;
	    document.getElementById("total-revenue").textContent = `₹${data.totalRevenue || 0}`;
	    document.getElementById("active-items").textContent = data.activeItems || 0;

	  } catch (err) {
	    console.error("Failed to load dashboard stats:", err);
	  }
	}
function hideForms() {
	  document.getElementById("login-form").style.display = "none";
	  document.getElementById("signup-form").style.display = "none";
	}

function switchToSignup() {
	  hideForms();
	  document.getElementById("signup-form").style.display = "block";
}

function switchToLogin() {
	  hideForms();
	  document.getElementById("login-form").style.display = "block";
}

