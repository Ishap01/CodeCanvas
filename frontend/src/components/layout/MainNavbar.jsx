import React from "react";
import "./MainNavbar.css";

import { NavLink, Link } from "react-router-dom";

import {
  FaCode,
  FaSearch,
  FaBell,
  FaCog,
  FaUser,
} from "react-icons/fa";

export default function MainNavbar() {
  return (
    <>
      {/* TOP NAVBAR */}

      <header className="globalTopNavbar">
        <Link to="/dashboard" className="globalBrand">
          <span className="globalBrandDot"></span>
          CODECANVAS
        </Link>

        <nav className="globalTopLinks">
          <NavLink to="/">LANDING</NavLink>

          <NavLink to="/login">LOGIN</NavLink>

          <NavLink to="/register">REGISTER</NavLink>

          <NavLink to="/user-dashboard">DASHBOARD</NavLink>

          <NavLink to="/search">SEARCH</NavLink>

          <NavLink to="/snippet">SNIPPET</NavLink>

          <NavLink to="/upload">UPLOAD</NavLink>

          <NavLink to="/profile">PROFILE</NavLink>

          <NavLink to="/premium">PREMIUM</NavLink>

          <NavLink to="/admin">ADMIN</NavLink>
        </nav>
      </header>

      {/* SECOND NAVBAR */}

      <header className="globalMainNavbar">
        <Link to="/dashboard" className="globalLogo">
          <span className="globalLogoBox">
            <FaCode />
          </span>

          <span>CodeCanvas</span>
        </Link>

        <div className="globalSearch">
          <FaSearch />

          <input
            type="text"
            placeholder="Search snippets..."
          />
        </div>

        <div className="globalNavbarActions">
          <button type="button" aria-label="Notifications">
            <FaBell />
          </button>

          <button type="button" aria-label="Settings">
            <FaCog />
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