# CodeInspire Frontend

AI-powered personalized advisor system - Vue 3 Frontend

## Tech Stack

- **Vue 3** (Composition API + TypeScript)
- **Vite** - Build tool
- **Vue Router 4** - Routing
- **Pinia** - State management
- **Element Plus** - UI component library
- **Axios** - HTTP client
- **SCSS** - Styling

## Project Structure

```
src/
├── api/                    # API interface definitions
│   ├── request.ts         # Axios instance with interceptors
│   ├── auth.ts            # Authentication API
│   └── chat.ts            # Chat API
├── assets/
│   └── styles/
│       └── main.scss      # Global styles and CSS variables
├── components/           # Reusable components
├── layouts/
│   └── MainLayout.vue    # Main layout with sidebar navigation
├── router/
│   └── index.ts          # Route configuration
├── stores/
│   └── user.ts           # User state management (Pinia)
├── views/
│   ├── auth/
│   │   ├── LoginView.vue     # Login page
│   │   └── RegisterView.vue  # Registration page
│   ├── chat/
│   │   └── ChatView.vue      # AI chat interface
│   ├── profile/
│   │   └── ProfileView.vue   # User profile page
│   ├── plan/
│   │   └── PlanView.vue      # Learning plan management
│   └── notification/
│       └── NotificationView.vue  # Notification center
├── App.vue               # Root component
└── main.ts              # Application entry point
```

## Features

### Authentication
- User login/registration
- JWT token management
- Auto-redirect on authentication status

### AI Chat Interface
- Real-time multi-turn conversation
- Message history display
- Quick action buttons for common queries
- Streaming response support (SSE)
- Markdown rendering

### User Profile
- Comprehensive user profile form
- Education background, career goals, time planning
- AI-powered personalized advice generation

### Learning Plans
- Create/edit/delete study plans
- Task management with priority levels
- Progress tracking
- Status visualization

### Notifications
- Real-time notification list
- Unread count badge
- Mark as read / mark all as read
- Time-relative display

## Getting Started

```bash
# Install dependencies
npm install

# Run development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview
```

## Design System

### Color Palette
- Primary: `#6366f1` (Indigo)
- Secondary: `#10b981` (Emerald)
- Accent: `#f59e0b` (Amber)
- Danger: `#ef4444` (Red)

### Typography
- Font: System font stack (PingFang SC, Microsoft YaHei, etc.)
- Headings: Bold, larger sizes
- Body: Regular weight, comfortable line height

### Components
- Border radius: 8px-16px
- Shadows: Subtle elevation layers
- Transitions: Smooth hover effects
