import {
    Route,
    Routes,
} from "react-router-dom";

import MainLayout from "./components/layout/MainLayout";

import Login from "./pages/auth/Login/Login";
import Register from "./pages/auth/Register/Register";
import ForgotPassword from "./pages/auth/ForgotPassword/ForgotPassword";
import VerifyOtp from "./pages/auth/VerifyOtp/VerifyOtp";
import ResetPassword from "./pages/auth/ResetPassword/ResetPassword";
import ChangePassword from "./pages/auth/ChangePassword/ChangePassword";

import LandingPage from "./pages/LandingPage/LandingPage";

import UserDashboard from "./pages/user/UserDashboard/UserDashboard";
import Profile from "./pages/user/Profile/Profile";
import EditProfile from "./pages/user/EditProfile/EditProfile";
import Settings from "./pages/user/settings/Settings";
import PublicProfile from "./pages/user/PublicProfile/PublicProfile";

import PricingPage from "./pages/Pricing/PricingPage";

import PublicSnippets from "./pages/snippets/PublicSnippets/PublicSnippets";
import CreateSnippet from "./pages/snippets/CreateSnippet/CreateSnippet";
import MySnippets from "./pages/snippets/MySnippets/MySnippets";
import SnippetDetails from "./pages/snippets/SnippetDetails/SnippetDetails";
import EditSnippet from "./pages/snippets/EditSnippet/EditSnippet";
import BookmarkedSnippets from "./pages/snippets/BookmarkedSnippets/BookmarkedSnippets";

import SearchPage from "./pages/Search/SearchPage";
import NotificationsPage from "./pages/notifications/NotificationsPage";

import ProtectedRoute from "./routes/ProtectedRoute";

function App() {

    return (
        <Routes>

            <Route
                path="/"
                element={<LandingPage />}
            />

            <Route
                path="/login"
                element={<Login />}
            />

            <Route
                path="/register"
                element={<Register />}
            />

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
                    element={
                        <ForgotPassword />
                    }
                />

                <Route
                    path="/verify-otp"
                    element={<VerifyOtp />}
                />

                <Route
                    path="/reset-password"
                    element={
                        <ResetPassword />
                    }
                />

                <Route
                    path="/change-password"
                    element={
                        <ProtectedRoute>
                            <ChangePassword />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/settings"
                    element={
                        <ProtectedRoute>
                            <Settings />
                        </ProtectedRoute>
                    }
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
                    path="/users/:username"
                    element={<PublicProfile />}
                />

                <Route
                    path="/edit-profile"
                    element={
                        <ProtectedRoute>
                            <EditProfile />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/premium"
                    element={
                        <ProtectedRoute>
                            <PricingPage />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/search"
                    element={<SearchPage />}
                />

                <Route
                    path="/snippets"
                    element={
                        <PublicSnippets />
                    }
                />

                <Route
                    path="/snippets/create"
                    element={
                        <ProtectedRoute>
                            <CreateSnippet />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/snippets/my"
                    element={
                        <ProtectedRoute>
                            <MySnippets />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/snippets/:snippetId/edit"
                    element={
                        <ProtectedRoute>
                            <EditSnippet />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/snippets/bookmarks"
                    element={
                        <ProtectedRoute>
                            <BookmarkedSnippets />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/notifications"
                    element={
                        <ProtectedRoute>
                            <NotificationsPage />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/snippets/:snippetId"
                    element={
                        <SnippetDetails />
                    }
                />

                <Route
                    path="/admin"
                    element={
                        <main className="snippetTemporaryPage">
                            <h1>
                                Admin Page
                            </h1>
                        </main>
                    }
                />

            </Route>

            <Route
                path="*"
                element={
                    <main className="snippetNotFoundPage">

                        <h1>404</h1>

                        <p>
                            The requested page was
                            not found.
                        </p>

                    </main>
                }
            />

        </Routes>
    );
}

export default App;