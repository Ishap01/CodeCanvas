import React from "react";
import NotificationBell from
"../notification/NotificationBell";
import "./MainNavbar.css";

import {
    Link,
    NavLink,
    useNavigate,
} from "react-router-dom";

import {
    FaBell,
    FaCode,
    FaCog,
    FaSearch,
    FaUser,
} from "react-icons/fa";

export default function MainNavbar() {

    const navigate = useNavigate();

    const handleSearchSubmit = (event) => {

        event.preventDefault();

        const formData =
            new FormData(
                event.currentTarget
            );

        const searchText =
            formData
                .get("search")
                ?.trim();

        if (!searchText) {
            navigate("/search");
            return;
        }

        navigate(
            `/search?q=${encodeURIComponent(
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

                    <NavLink to="/search">
                        SEARCH
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

                    <NavLink to="/public-profile">
                        PUBLIC PROFILE
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

                    <span>
                        CodeCanvas
                    </span>
                </Link>

                <form
                    className="globalSearch"
                    onSubmit={
                        handleSearchSubmit
                    }
                >
                    <button
                        type="submit"
                        className="globalSearchSubmit"
                        aria-label="Search snippets"
                    >
                        <FaSearch />
                    </button>

                    <input
                        type="text"
                        name="search"
                        placeholder="Search snippets..."
                        autoComplete="off"
                    />
                </form>

                <div className="globalNavbarActions">

                    {/* Separate Search icon */}

                    <button
                        type="button"
                        aria-label="Open search page"
                        title="Search"
                        onClick={() =>
                            navigate("/search")
                        }
                    >
                        <FaSearch />
                    </button>

                    <NotificationBell />

                    <button
                        type="button"
                        aria-label="Settings"
                        title="Settings"
                        onClick={() =>
                            navigate(
                                "/settings"
                            )
                        }
                    >
                        <FaCog />
                    </button>

                    <Link
                        to="/profile"
                        className="globalProfileButton"
                        aria-label="Profile"
                        title="Profile"
                    >
                        <FaUser />
                    </Link>

                </div>

            </header>
        </>
    );
}