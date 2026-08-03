import React from "react";
import "./MainNavbar.css";

import {
    NavLink,
    Link,
    useNavigate,
} from "react-router-dom";

import {
    FaCode,
    FaSearch,
    FaBell,
    FaCog,
    FaUser,
} from "react-icons/fa";

export default function MainNavbar() {

    const navigate = useNavigate();

    const handleSearchSubmit = (event) => {
        event.preventDefault();

        const formData = new FormData(
            event.currentTarget
        );

        const searchText = formData
            .get("search")
            ?.trim();

        if (!searchText) {
            navigate("/snippets");
            return;
        }

        navigate(
            `/snippets?search=${encodeURIComponent(
                searchText
            )}`
        );
    };

    return (
        <>
            <header className="globalTopNavbar">

                <Link
                    to="/"
                    className="globalBrand"
                >
                    <span className="globalBrandDot" />

                    CODECANVAS
                </Link>

                <nav className="globalTopLinks">

                    <NavLink to="/">
                        LANDING
                    </NavLink>

                    <NavLink to="/login">
                        LOGIN
                    </NavLink>

                    <NavLink to="/register">
                        REGISTER
                    </NavLink>

                    <NavLink to="/dashboard">
                        DASHBOARD
                    </NavLink>

                    <NavLink to="/snippets">
                        SNIPPETS
                    </NavLink>

                    <NavLink to="/snippets/my">
                        MY SNIPPETS
                    </NavLink>

                    <NavLink to="/snippets/create">
                        CREATE
                    </NavLink>

                    <NavLink to="/snippets/bookmarks">
                        BOOKMARKS
                    </NavLink>

                    <NavLink to="/profile">
                        PROFILE
                    </NavLink>

                    <NavLink to="/premium">
                        PREMIUM
                    </NavLink>

                    <NavLink to="/admin">
                        ADMIN
                    </NavLink>

                </nav>

            </header>

            <header className="globalMainNavbar">

                <Link
                    to="/"
                    className="globalLogo"
                >
                    <span className="globalLogoBox">
                        <FaCode />
                    </span>

                    <span>CodeCanvas</span>
                </Link>

                <form
                    className="globalSearch"
                    onSubmit={handleSearchSubmit}
                >
                    <FaSearch />

                    <input
                        type="text"
                        name="search"
                        placeholder="Search snippets..."
                        autoComplete="off"
                    />
                </form>

                <div className="globalNavbarActions">

                    <button
                        type="button"
                        aria-label="Notifications"
                    >
                        <FaBell />
                    </button>

                    <button
                        type="button"
                        aria-label="Settings"
                        onClick={() =>
                            navigate("/settings")
                        }
                    >
                        <FaCog className="navIcon" />
                    </button>

                    <Link
                        to="/profile"
                        className="globalProfileButton"
                        aria-label="Profile"
                    >
                        <FaUser />
                    </Link>

                </div>

            </header>
        </>
    );
}