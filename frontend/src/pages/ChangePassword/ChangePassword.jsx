import React, { useState } from "react";
import "./ChangePassword.css";

import { Link } from "react-router-dom";

import {
  FaCode,
  FaLock,
  FaEye,
  FaEyeSlash,
  FaKey,
  FaArrowLeft,
} from "react-icons/fa";

import bg from "../../assets/login-bg.jpg";

import { changePassword } from "../../services/authService";

export default function ChangePassword() {
  const [formData, setFormData] = useState({
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
  });

  const [showCurrent, setShowCurrent] = useState(false);
  const [showNew, setShowNew] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);

  const [message, setMessage] = useState("");
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    setMessage("");
    setSuccess(false);

    if (
      !formData.currentPassword ||
      !formData.newPassword ||
      !formData.confirmPassword
    ) {
      setMessage("All fields are required.");
      return;
    }

    if (formData.newPassword.length < 8) {
      setMessage(
        "Password must contain at least 8 characters."
      );
      return;
    }

    if (
      formData.currentPassword ===
      formData.newPassword
    ) {
      setMessage(
        "New password must be different from current password."
      );
      return;
    }

    if (
      formData.newPassword !==
      formData.confirmPassword
    ) {
      setMessage(
        "New password and confirm password do not match."
      );
      return;
    }

    try {
      setLoading(true);

      const response =
        await changePassword(formData);

      setSuccess(true);

      setMessage(response);

      setFormData({
        currentPassword: "",
        newPassword: "",
        confirmPassword: "",
      });
    } catch (error) {
      setSuccess(false);

      setMessage(
        error.response?.data?.message ||
          error.response?.data ||
          "Unable to change password."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="changePage">

      <div
        className="changeLeftPanel"
        style={{ backgroundImage: `url(${bg})` }}
      >
        <div className="changeOverlay"></div>

        <div className="changeLeftContent">
          <h1>
            Secure your
            <br />
            <span>CodeCanvas.</span> account
          </h1>

          <p>
            Update your password regularly
            to keep your account secure.
          </p>
        </div>
      </div>

      <div className="changeRightPanel">

        <form
          className="changeCard"
          onSubmit={handleSubmit}
        >

          <div className="changeLogo">
            <FaCode />
          </div>

          <h2>Change Password</h2>

          <p className="changeSubtitle">
            Enter your current password and choose a new one.
          </p>

          {/* Current Password */}

          <div className="changeInputBox">

            <FaLock />

            <input
              type={showCurrent ? "text" : "password"}
              name="currentPassword"
              placeholder="Current Password"
              value={formData.currentPassword}
              onChange={handleChange}
            />

            <button
              type="button"
              className="changeEyeButton"
              onClick={() =>
                setShowCurrent(!showCurrent)
              }
            >
              {showCurrent ? (
                <FaEyeSlash />
              ) : (
                <FaEye />
              )}
            </button>

          </div>

          {/* New Password */}

          <div className="changeInputBox">

            <FaLock />

            <input
              type={showNew ? "text" : "password"}
              name="newPassword"
              placeholder="New Password"
              value={formData.newPassword}
              onChange={handleChange}
            />

            <button
              type="button"
              className="changeEyeButton"
              onClick={() =>
                setShowNew(!showNew)
              }
            >
              {showNew ? (
                <FaEyeSlash />
              ) : (
                <FaEye />
              )}
            </button>

          </div>

          {/* Confirm Password */}

          <div className="changeInputBox">

            <FaLock />

            <input
              type={
                showConfirm
                  ? "text"
                  : "password"
              }
              name="confirmPassword"
              placeholder="Confirm Password"
              value={formData.confirmPassword}
              onChange={handleChange}
            />

            <button
              type="button"
              className="changeEyeButton"
              onClick={() =>
                setShowConfirm(!showConfirm)
              }
            >
              {showConfirm ? (
                <FaEyeSlash />
              ) : (
                <FaEye />
              )}
            </button>

          </div>

          {message && (
            <p
              className={
                success
                  ? "changeMessage changeSuccess"
                  : "changeMessage changeError"
              }
            >
              {message}
            </p>
          )}

          <button
            type="submit"
            className="changeButton"
            disabled={loading}
          >
            <FaKey />

            {loading
              ? "Changing..."
              : "Change Password"}
          </button>

          <div className="changeBack">
            <Link to="/profile">
              <FaArrowLeft />
              Back to Profile
            </Link>
          </div>

        </form>

      </div>

    </div>
  );
}