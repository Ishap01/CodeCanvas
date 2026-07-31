import React from "react";

import {
    Routes,
    Route,
    Navigate,
} from "react-router-dom";

import MainLayout from "./components/layout/MainLayout";

import Login from "./pages/Login/Login";
import Register from "./pages/Register/Register";
import ForgotPassword from "./pages/ForgotPassword/ForgotPassword";
import ResetPassword from "./pages/ResetPassword/ResetPassword";

import Dashboard from "./pages/Dashboard/Dashboard";
import UserDashboard from "./pages/UserDashboard/UserDashboard";
import Profile from "./pages/Profile/Profile";

function App() {
    return (
        <Routes>
            {/* ================================
          AUTH PAGES
          Navbar nahi dikhega
      ================================= */}

            <Route path="/login" element={<Login />} />

            <Route path="/register" element={<Register />} />

            <Route
                path="/forgot-password"
                element={<ForgotPassword />}
            />

            <Route
                path="/reset-password"
                element={<ResetPassword />}
            />

            {/* ================================
          NORMAL PAGES
          Permanent navbar dikhega
      ================================= */}

            <Route element={<MainLayout />}>
                <Route path="/" element={<Dashboard />} />

                <Route
                    path="/dashboard"
                    element={<Dashboard />}
                />

                <Route
                    path="/user-dashboard"
                    element={<UserDashboard />}
                />

                <Route
                    path="/search"
                    element={<div>Search Page</div>}
                />

                <Route
                    path="/snippet"
                    element={<div>Snippet Page</div>}
                />

                <Route
                    path="/upload"
                    element={<div>Upload Page</div>}
                />

                {/* Actual Profile component */}

                <Route
                    path="/profile"
                    element={<Profile />}
                />

                <Route
                    path="/premium"
                    element={<div>Premium Page</div>}
                />

                <Route
                    path="/admin"
                    element={<div>Admin Page</div>}
                />
            </Route>

            {/* Unknown URL redirect */}

            <Route
                path="*"
                element={<Navigate to="/dashboard" replace />}
            />
        </Routes>
    );
}

export default App;