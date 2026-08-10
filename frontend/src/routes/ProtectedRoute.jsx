import {
    Navigate,
    Outlet,
    useLocation,
} from "react-router-dom";

import {
    useAuth,
} from "../context/AuthContext";

function ProtectedRoute({
    children,
    message = "Please login to continue.",
}) {

    const location =
        useLocation();

    const {
        isAuthenticated,
    } = useAuth();

    /*
     * Current complete frontend path preserve hoga.
     *
     * Examples:
     *
     * /snippets/abc-123
     * /snippets/abc-123#comments
     * /search?q=react
     */
    const redirectTo =
        `${location.pathname}${location.search}${location.hash}`;

    console.log("ProtectedRoute:", location.pathname);
    console.log("Authenticated:", isAuthenticated);

    if (!isAuthenticated) {

        return (
            <Navigate
                to="/login"
                replace
                state={{
                    message,
                    redirectTo,
                    from: location,
                }}
            />
        );
    }

    return children
        ? children
        : <Outlet />;
}

export default ProtectedRoute;