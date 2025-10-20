# Lost & Found - Web Frontend

A beautiful, modern web interface for the Lost & Found management system.

## Features

✨ **Modern Design**
- Beautiful gradient backgrounds
- Smooth animations and transitions
- Responsive design for all devices
- Card-based layout

🔐 **Authentication**
- Secure login with JWT tokens
- Session management

📋 **Item Management**
- View all items, lost items, or found items
- Report new lost items
- Mark items as found
- Search functionality

🎨 **UI/UX**
- Interactive cards with hover effects
- Modal dialogs for forms
- Loading states
- Empty states
- Error handling with visual feedback

## Setup

1. Make sure the backend server is running on `http://localhost:8080`
2. Open `index.html` in a web browser or use a local server

### Using a Local Server

**With Python:**
```bash
cd frontend
python -m http.server 3000
```

**With Node.js:**
```bash
cd frontend
npx serve
```

Then open `http://localhost:3000` in your browser.

## Usage

### Login
- Default credentials (if configured in backend):
  - Username: `admin`
  - Password: `admin123`

### Dashboard Features
- **All Items**: View all reported items
- **Lost Items**: Filter to show only items still lost
- **Found Items**: Filter to show only recovered items
- **Report Lost Item**: Submit a new lost item report
- **Search**: Search by item name, location, or student name
- **Mark as Found**: Update a lost item status when found

## File Structure

```
frontend/
├── index.html          # Login page
├── dashboard.html      # Main dashboard
├── style.css          # All styles and animations
├── script.js          # JavaScript logic and API calls
└── README.md          # This file
```

## Customization

### Colors
Edit the CSS variables in `style.css`:
```css
:root {
    --primary-gradient: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    --success-color: #10b981;
    --danger-color: #ef4444;
    /* ... more variables */
}
```

### API Endpoint
Update the API base URL in `script.js`:
```javascript
const API_BASE = 'http://localhost:8080';
```

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Technologies Used

- HTML5
- CSS3 (Grid, Flexbox, Animations)
- Vanilla JavaScript (ES6+)
- Fetch API for HTTP requests
- Google Fonts (Inter)

## License

Part of the Lost & Found System project.
