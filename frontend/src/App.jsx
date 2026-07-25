import { Routes, Route } from "react-router-dom";

import MainLayout from "./components/layout/MainLayout";

import Login from "./pages/Login/Login";
import Register from "./pages/Register/Register";
import LandingPage from "./pages/LandingPage/LandingPage";
import UserDashboard from "./pages/UserDashboard/UserDashboard";
import Profile from "./pages/Profile/Profile";
import EditProfile from "./pages/EditProfile/EditProfile";
import ForgotPassword from "./pages/ForgotPassword/ForgotPassword";
import VerifyOtp from "./pages/VerifyOtp/VerifyOtp";
import ResetPassword from "./pages/ResetPassword/ResetPassword";
import ChangePassword from "./pages/ChangePassword/ChangePassword";
import Settings from "./pages/settings/Settings";

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