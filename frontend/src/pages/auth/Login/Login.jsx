import {
    useEffect,
    useMemo,
    useState,
} from "react";

import {
    Link,
    useLocation,
    useNavigate,
} from "react-router-dom";

import {
    FaCode,
    FaEye,
    FaEyeSlash,
    FaGithub,
    FaGoogle,
    FaLock,
    FaSignInAlt,
    FaUser,
} from "react-icons/fa";

import {
    loginUser,
} from "../../../services/authService";

import {
    useAuth,
} from "../../../context/AuthContext";

import bg from "../../../assets/images/login-bg.jpg";

import "./Login.css";

function getSafeRedirectPath(
    locationState
) {

    const redirectTo =
        locationState?.redirectTo;

    /*
     * ProtectedRoute ke purane ya alternate
     * state format ko bhi support karega.
     */
    const locationFromState =
        locationState?.from;

    let destination =
        "/dashboard";

    if (
        typeof redirectTo === "string" &&
        redirectTo.startsWith("/") &&
        !redirectTo.startsWith("//") &&
        redirectTo !== "/login"
    ) {
        destination = redirectTo;
    } else if (
        locationFromState &&
        typeof locationFromState === "object"
    ) {

        const pathname =
            locationFromState.pathname || "";

        const search =
            locationFromState.search || "";

        const hash =
            locationFromState.hash || "";

        const combinedPath =
            `${pathname}${search}${hash}`;

        if (
            combinedPath.startsWith("/") &&
            !combinedPath.startsWith("//") &&
            pathname !== "/login"
        ) {
            destination =
                combinedPath;
        }
    }

    return destination;
}

export default function Login() {

    const navigate =
        useNavigate();

    const location =
        useLocation();

    const {
        login,
        isAuthenticated,
    } = useAuth();

    const redirectPath =
        useMemo(
            () =>
                getSafeRedirectPath(
                    location.state
                ),
            [location.state]
        );

    const redirectMessage =
        location.state?.message ||
        "";

    const [formData, setFormData] =
        useState({
            email: "",
            password: "",
        });

    const [
        showPassword,
        setShowPassword,
    ] = useState(false);

    const [
        message,
        setMessage,
    ] = useState(
        redirectMessage
    );

    const [
        messageType,
        setMessageType,
    ] = useState(
        redirectMessage
            ? "info"
            : ""
    );

    const [
        loading,
        setLoading,
    ] = useState(false);

    /*
     * Already logged-in user manually /login open kare
     * to destination page par bhej denge.
     */
    useEffect(() => {

        if (isAuthenticated) {

            navigate(
                redirectPath,
                {
                    replace: true,
                }
            );
        }

    }, [
        isAuthenticated,
        navigate,
        redirectPath,
    ]);

    /*
     * Route state message change hone par
     * login page par updated information show hogi.
     */
    useEffect(() => {

        if (redirectMessage) {

            setMessage(
                redirectMessage
            );

            setMessageType(
                "info"
            );
        }

    }, [redirectMessage]);

    const handleChange = (
        event
    ) => {

        const {
            name,
            value,
        } = event.target;

        setFormData(
            (previousData) => ({
                ...previousData,
                [name]: value,
            })
        );

        /*
         * User form edit kar raha hai to purana
         * validation error clear kar denge.
         *
         * Login suggestion/info message preserve rahega.
         */
        if (
            messageType === "error"
        ) {
            setMessage("");
            setMessageType("");
        }
    };

    const handleLogin = async (
        event
    ) => {

        event.preventDefault();

        const normalizedEmail =
            formData.email.trim();

        if (
            !normalizedEmail ||
            !formData.password.trim()
        ) {

            setMessage(
                "Email and password are required."
            );

            setMessageType(
                "error"
            );

            return;
        }

        try {

            setLoading(true);
            setMessage("");
            setMessageType("");

            const loginRequest = {
                email:
                    normalizedEmail,

                password:
                    formData.password,
            };

            const response =
                await loginUser(
                    loginRequest
                );

            /*
             * Response direct ho sakta hai:
             *
             * {
             *     token: "..."
             * }
             *
             * Ya wrapper:
             *
             * {
             *     data: {
             *         token: "..."
             *     }
             * }
             */
            const payload =
                response?.data &&
                typeof response.data ===
                    "object"
                    ? response.data
                    : response;

            const token =
                payload?.token;

            if (!token) {

                setMessage(
                    payload?.message ||
                    "Login failed. Token was not received."
                );

                setMessageType(
                    "error"
                );

                return;
            }

            /*
             * AuthContext token ko localStorage aur
             * application state mein save karega.
             */
            login(token);

            /*
             * Successful login ke baad:
             *
             * Protected page se aaye the
             * → wahi page open hoga.
             *
             * Direct login kiya tha
             * → dashboard open hoga.
             */
            navigate(
                redirectPath,
                {
                    replace: true,
                }
            );

        } catch (error) {

            console.error(
                "Login error:",
                error
            );

            const backendMessage =
                error?.response
                    ?.data
                    ?.message;

            if (
                error?.response
            ) {

                setMessage(
                    backendMessage ||
                    "Invalid email or password."
                );

            } else {

                setMessage(
                    error?.message ||
                    "Unable to connect to the server."
                );
            }

            setMessageType(
                "error"
            );

        } finally {

            setLoading(false);
        }
    };

    return (

        <main className="loginPage">

            {/* ================= LEFT PANEL ================= */}

            <section
                className="leftPanel"
                style={{
                    backgroundImage:
                        `url(${bg})`,
                }}
            >

                <div className="overlay" />

                <div className="leftContent">

                    <h1>
                        Welcome back to
                        <br />

                        <span>
                            CodeCanvas.
                        </span>
                    </h1>

                    <p>
                        The visual code collaboration
                        platform
                        <br />

                        for modern developers.
                    </p>

                </div>

            </section>

            {/* ================= RIGHT PANEL ================= */}

            <section className="rightPanel">

                <form
                    className="loginCard"
                    onSubmit={
                        handleLogin
                    }
                >

                    <div className="logo">
                        <FaCode />
                    </div>

                    <h2>
                        Welcome Back
                    </h2>

                    <p>
                        Sign in to your CodeCanvas
                        account
                    </p>

                    {message && (

                        <div
                            className={`formMessage ${
                                messageType === "info"
                                    ? "formMessageInfo"
                                    : "formMessageError"
                            }`}
                            role={
                                messageType === "error"
                                    ? "alert"
                                    : "status"
                            }
                        >
                            {message}
                        </div>

                    )}

                    <div className="inputBox">

                        <FaUser />

                        <input
                            autoFocus
                            type="email"
                            name="email"
                            placeholder="Enter Email"
                            value={
                                formData.email
                            }
                            onChange={
                                handleChange
                            }
                            autoComplete="email"
                        />

                    </div>

                    <div className="inputBox">

                        <FaLock />

                        <input
                            type={
                                showPassword
                                    ? "text"
                                    : "password"
                            }
                            name="password"
                            placeholder="Password"
                            value={
                                formData.password
                            }
                            onChange={
                                handleChange
                            }
                            autoComplete="current-password"
                        />

                        <button
                            type="button"
                            className="eye"
                            onClick={() =>
                                setShowPassword(
                                    (
                                        previousValue
                                    ) =>
                                        !previousValue
                                )
                            }
                            aria-label={
                                showPassword
                                    ? "Hide password"
                                    : "Show password"
                            }
                        >
                            {showPassword
                                ? <FaEyeSlash />
                                : <FaEye />}
                        </button>

                    </div>

                    <div className="options">

                        <label>

                            <input
                                type="checkbox"
                            />

                            Remember me

                        </label>

                        <Link to="/forgot-password">
                            Forgot Password?
                        </Link>

                    </div>

                    <button
                        type="submit"
                        className="loginBtn"
                        disabled={
                            loading
                        }
                    >

                        <FaSignInAlt />

                        {loading
                            ? "Signing In..."
                            : "Sign In"}

                    </button>

                    <button
                        type="button"
                        className="socialBtn"
                    >
                        <FaGithub />

                        Continue with GitHub
                    </button>

                    <button
                        type="button"
                        className="socialBtn"
                    >
                        <FaGoogle />

                        Continue with Google
                    </button>

                    <div className="signup">

                        Don't have an account?

                        <Link
                            to="/register"
                            state={{
                                redirectTo:
                                    redirectPath,
                            }}
                        >
                            <span>
                                Create one
                            </span>
                        </Link>

                    </div>

                </form>

            </section>

        </main>
    );
}