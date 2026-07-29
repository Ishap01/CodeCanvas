import { Routes, Route } from "react-router-dom";

import MainLayout from "./components/layout/MainLayout";

import Login from "./pages/auth/Login/Login";
import Register from "./pages/auth/Register/Register";
import LandingPage from "./pages/LandingPage/LandingPage";
import UserDashboard from "./pages/user/UserDashboard/UserDashboard";
import Profile from "./pages/user/Profile/Profile";
import EditProfile from "./pages/user/EditProfile/EditProfile";
import ForgotPassword from "./pages/auth/ForgotPassword/ForgotPassword";
import VerifyOtp from "./pages/auth/VerifyOtp/VerifyOtp";
import ResetPassword from "./pages/auth/ResetPassword/ResetPassword";
import ChangePassword from "./pages/auth/ChangePassword/ChangePassword";
import Settings from "./pages/user/settings/Settings";

import ProtectedRoute from "./routes/ProtectedRoute";

function App() {
    return (
        <Routes>

            {/* Public pages without Navbar */}
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />


            <Route path="/" element={<LandingPage />} />

            {/* Pages with Main Layout */}
            <Route element={<MainLayout />}>

                <Route
                    path="/dashboard"
                    element={
                        <ProtectedRoute>
                            <UserDashboard />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/forgot-password"
                    element={<ForgotPassword />}
                />

                <Route
                    path="/verify-otp"
                    element={<VerifyOtp />}
                />

                <Route
                    path="/reset-password"
                    element={<ResetPassword />}
                />

                <Route
                    path="/change-password"
                    element={<ChangePassword />}
                />

                <Route
                    path="/settings"
                    element={<Settings />}
                />

                <Route
                    path="/profile"
                    element={
                        <ProtectedRoute>
                            <Profile />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/edit-profile"
                    element={
                        <ProtectedRoute>
                            <EditProfile />
                        </ProtectedRoute>
                    }
                />

                {/* Other routes */}


                {/* Placeholder Routes (Modules to be developed) */}
                <Route path="/search" element={<h1>Search Page</h1>} />
                <Route path="/snippet" element={<h1>Snippet Page</h1>} />
                <Route path="/upload" element={<h1>Upload Page</h1>} />

                <Route path="/premium" element={<h1>Premium Page</h1>} />
                <Route path="/admin" element={<h1>Admin Page</h1>} />

            </Route>

        </Routes>
    );
}

export default App;