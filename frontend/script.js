// API base URL - adjust if needed
const API_BASE = 'http://localhost:8080';

// Utility functions
function showError(message) {
    const errorDiv = document.getElementById('errorMessage');
    if (errorDiv) {
        errorDiv.textContent = message;
        errorDiv.style.display = 'block';
        setTimeout(() => {
            errorDiv.style.display = 'none';
        }, 5000);
    }
}

function getToken() {
    return localStorage.getItem('token');
}

function setToken(token) {
    localStorage.setItem('token', token);
}

function clearToken() {
    localStorage.removeItem('token');
}

function getAuthHeaders() {
    const token = getToken();
    return token ? { 'Authorization': `Bearer ${token}` } : {};
}

// API functions
async function login(username, password) {
    try {
        const response = await fetch(`${API_BASE}/api/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, password })
        });

        const data = await response.json();

        if (response.ok && data.token) {
            setToken(data.token);
            window.location.href = 'dashboard.html';
        } else {
            showError(data.message || 'Login failed');
        }
    } catch (error) {
        showError('Network error. Please try again.');
    }
}

async function fetchItems(endpoint = '/api/items') {
    try {
        const response = await fetch(`${API_BASE}${endpoint}`, {
            headers: getAuthHeaders()
        });

        if (response.ok) {
            return await response.json();
        } else if (response.status === 401) {
            clearToken();
            window.location.href = 'index.html';
        } else {
            throw new Error('Failed to fetch items');
        }
    } catch (error) {
        showError('Failed to load items');
        return [];
    }
}

async function reportItem(itemData) {
    try {
        const response = await fetch(`${API_BASE}/api/items/report`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...getAuthHeaders()
            },
            body: JSON.stringify(itemData)
        });

        if (response.ok) {
            return await response.json();
        } else {
            throw new Error('Failed to report item');
        }
    } catch (error) {
        showError('Failed to report item');
        return null;
    }
}

async function markAsFound(id, foundData) {
    try {
        const response = await fetch(`${API_BASE}/api/items/${id}/found`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                ...getAuthHeaders()
            },
            body: JSON.stringify(foundData)
        });

        if (response.ok) {
            return await response.json();
        } else {
            throw new Error('Failed to mark as found');
        }
    } catch (error) {
        showError('Failed to mark as found');
        return null;
    }
}

async function searchItems(query) {
    try {
        const endpoints = [
            `/api/items/search/item?name=${encodeURIComponent(query)}`,
            `/api/items/search/location?location=${encodeURIComponent(query)}`,
            `/api/items/search/student?name=${encodeURIComponent(query)}`
        ];

        const results = await Promise.all(endpoints.map(endpoint => fetchItems(endpoint)));
        const allResults = results.flat();

        // Remove duplicates
        const uniqueResults = allResults.filter((item, index, self) =>
            index === self.findIndex(i => i.id === item.id)
        );

        return uniqueResults;
    } catch (error) {
        showError('Search failed');
        return [];
    }
}

// UI functions
function createItemCard(item) {
    const card = document.createElement('div');
    card.className = `item-card ${item.found ? 'found' : 'lost'}`;

    const statusText = item.found ? 'Found' : 'Lost';
    const statusClass = item.found ? 'found' : 'lost';
    const statusIcon = item.found ? '✅' : '❌';

    const reportDate = new Date(item.reportedDate).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });

    const foundDate = item.foundDate ? new Date(item.foundDate).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    }) : '';

    card.innerHTML = `
        <h3>${item.itemName}</h3>
        <p><strong>📄 Description:</strong> ${item.description}</p>
        <p><strong>📍 Location:</strong> ${item.location}</p>
        <p><strong>👤 Student:</strong> ${item.studentName}</p>
        <p><strong>📞 Contact:</strong> ${item.contactInfo}</p>
        <p><strong>🕐 Reported:</strong> ${reportDate}</p>
        ${item.found ? `<p><strong>🕐 Found Date:</strong> ${foundDate}</p>` : ''}
        ${item.found ? `<p><strong>👤 Found By:</strong> ${item.foundBy}</p>` : ''}
        ${item.remarks ? `<p><strong>💬 Remarks:</strong> ${item.remarks}</p>` : ''}
        <span class="status ${statusClass}">${statusIcon} ${statusText}</span>
        <div class="actions">
            ${!item.found ? `<button class="btn btn-success btn-small" onclick="openFoundModal(${item.id})">✅ Mark as Found</button>` : ''}
        </div>
    `;

    return card;
}

function displayItems(items) {
    const container = document.getElementById('itemsContainer');
    container.innerHTML = '';

    if (items.length === 0) {
        container.innerHTML = `
            <div class="empty-state" style="grid-column: 1/-1;">
                <div style="font-size: 64px; margin-bottom: 20px;">📭</div>
                <h2 style="color: var(--text-primary); margin-bottom: 10px;">No items found</h2>
                <p style="font-size: 1.1em;">Try adjusting your search or add a new item</p>
            </div>
        `;
        return;
    }

    items.forEach(item => {
        const card = createItemCard(item);
        container.appendChild(card);
    });
}

function openFoundModal(id) {
    document.getElementById('foundItemId').value = id;
    document.getElementById('foundModal').style.display = 'block';
}

function closeModals() {
    document.getElementById('reportModal').style.display = 'none';
    document.getElementById('foundModal').style.display = 'none';
}

// Event listeners
document.addEventListener('DOMContentLoaded', function() {
    // Login page
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            await login(username, password);
        });
    }

    // Dashboard page
    if (window.location.pathname.includes('dashboard.html')) {
        // Check if logged in
        if (!getToken()) {
            window.location.href = 'index.html';
            return;
        }

        // Load all items initially
        loadItems('/api/items');

        // Navigation buttons
        document.getElementById('allItemsBtn').addEventListener('click', () => {
            setActiveNav('allItemsBtn');
            loadItems('/api/items');
        });

        document.getElementById('lostItemsBtn').addEventListener('click', () => {
            setActiveNav('lostItemsBtn');
            loadItems('/api/items/lost');
        });

        document.getElementById('foundItemsBtn').addEventListener('click', () => {
            setActiveNav('foundItemsBtn');
            loadItems('/api/items/found');
        });

        document.getElementById('reportBtn').addEventListener('click', () => {
            document.getElementById('reportModal').style.display = 'block';
        });

        // Search
        document.getElementById('searchBtn').addEventListener('click', async () => {
            const query = document.getElementById('searchInput').value.trim();
            if (query) {
                const results = await searchItems(query);
                displayItems(results);
            } else {
                loadItems('/api/items');
            }
        });

        document.getElementById('searchInput').addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                document.getElementById('searchBtn').click();
            }
        });

        // Report form
        document.getElementById('reportForm').addEventListener('submit', async function(e) {
            e.preventDefault();
            const itemData = {
                itemName: document.getElementById('itemName').value,
                description: document.getElementById('description').value,
                location: document.getElementById('location').value,
                studentName: document.getElementById('studentName').value,
                contactInfo: document.getElementById('contactInfo').value
            };

            const result = await reportItem(itemData);
            if (result) {
                closeModals();
                this.reset();
                loadItems('/api/items'); // Refresh items
            }
        });

        // Found form
        document.getElementById('foundForm').addEventListener('submit', async function(e) {
            e.preventDefault();
            const id = document.getElementById('foundItemId').value;
            const foundData = {
                foundBy: document.getElementById('foundBy').value,
                remarks: document.getElementById('remarks').value
            };

            const result = await markAsFound(id, foundData);
            if (result) {
                closeModals();
                this.reset();
                loadItems('/api/items'); // Refresh items
            }
        });

        // Logout
        document.getElementById('logoutBtn').addEventListener('click', () => {
            clearToken();
            window.location.href = 'index.html';
        });

        // Close modals
        document.querySelectorAll('.close').forEach(closeBtn => {
            closeBtn.addEventListener('click', closeModals);
        });

        window.addEventListener('click', function(e) {
            if (e.target.classList.contains('modal')) {
                closeModals();
            }
        });
    }
});

async function loadItems(endpoint) {
    const container = document.getElementById('itemsContainer');
    container.innerHTML = `
        <div class="loading" style="grid-column: 1/-1;">
            <div style="font-size: 48px; margin-bottom: 20px;">⏳</div>
            <h3>Loading items...</h3>
        </div>
    `;
    const items = await fetchItems(endpoint);
    displayItems(items);
}

function setActiveNav(activeId) {
    document.querySelectorAll('.nav-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    document.getElementById(activeId).classList.add('active');
}
