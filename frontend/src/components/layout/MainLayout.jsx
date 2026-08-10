import React from "react";
import { Outlet } from "react-router-dom";

import MainNavbar from "./MainNavbar";
import "./MainLayout.css";

export default function MainLayout() {
  return (
    <div className="main-application">
      <MainNavbar />

      <main className="main-page-content">
        <Outlet />
      </main>
    </div>
  );
}