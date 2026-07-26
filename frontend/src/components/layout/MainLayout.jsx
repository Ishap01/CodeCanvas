import React from "react";
import { Outlet } from "react-router-dom";

import MainNavbar from "./MainNavbar";

export default function MainLayout() {
  return (
    <div className="mainApplicationLayout">
      <MainNavbar />

      <Outlet />
    </div>
  );
}