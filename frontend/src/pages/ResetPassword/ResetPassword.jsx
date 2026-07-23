import React, { useState } from "react";
import "./ResetPassword.css";

import { Link, useNavigate, useSearchParams } from "react-router-dom";

import {
  FaCode,
  FaLock,
  FaEye,
  FaEyeSlash,
  FaKey,
  FaArrowLeft,
} from "react-icons/fa";

import bg from "../../assets/login-bg.jpg";

export default function ResetPassword() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  // Example URL:
  // http://localhost:5173/reset-password?token=abc123
  const token = searchParams.get("token");

  const [formData, setFormData] = useState({
    newPassword: "",
    confirmPassword: "",
  });

  const [showNewPassword, setShowNewPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] =
    useState(false);

  const [message, setMessage] = useState("");
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleChange = (event) => {
    const { name, value } = event.target;

    setFormData((previousData) => ({
      ...previousData,
      [name]: value,
    }));
  };

  const handleResetPassword = async (event) => {
    event.preventDefault();

    setMessage("");
    setSuccess(false);

    if (
      !formData.newPassword.trim() ||
      !formData.confirmPassword.trim()
    ) {
      setMessage("Both password fields are required");
      return;
    }

    if (formData.newPassword.length < 6) {
      setMessage("Password must contain at least 6 characters");
      return;
    }

    if (formData.newPassword !== formData.confirmPassword) {
      setMessage("New password and confirm password do not match");
      return;
    }

    if (!token) {
      setMessage("Reset token is missing or invalid");
      return;
    }

    try {
      setLoading(true);

      const response = await fetch(
        "http://localhost:8082/api/auth/reset-password",
        {
          method: "POST",

          headers: {
            "Content-Type": "application/json",
          },

          body: JSON.stringify({
            token,
            newPassword: formData.newPassword,
            confirmPassword: formData.confirmPassword,
          }),
        }
      );

      const data = await response.json();

      if (response.ok && data.success) {
        setSuccess(true);
        setMessage(data.message || "Password reset successfully");

        setFormData({
          newPassword: "",
          confirmPassword: "",
        });

        setTimeout(() => {
          navigate("/login");
        }, 2000);
      } else {
        setSuccess(false);
        setMessage(
          data.message || "Unable to reset your password"
        );
      }
    } catch (error) {
      console.error("Reset password error:", error);

      setSuccess(false);
      setMessage("Backend server se connection nahi ho raha");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="resetPage">
      {/* LEFT PANEL */}

      <div
        className="resetLeftPanel"
        style={{ backgroundImage: `url(${bg})` }}
      >
        <div className="resetOverlay"></div>

        <div className="resetLeftContent">
          <h1>
            Secure your <br />
            <span>CodeCanvas.</span> account
          </h1>

          <p>
            Create a strong new password to restore
            <br />
            access to your account.
          </p>
        </div>
      </div>

      {/* RIGHT PANEL */}

      <div className="resetRightPanel">
        <form
          className="resetCard"
          onSubmit={handleResetPassword}
        >
          <div className="resetLogo">
            <FaCode />
          </div>

          <h2>Reset Password</h2>

          <p className="resetSubtitle">
            Enter and confirm your new password
          </p>

          {/* NEW PASSWORD */}

          <div className="resetInputBox">
            <FaLock />

            <input
              type={showNewPassword ? "text" : "password"}
              name="newPassword"
              placeholder="New Password"
              value={formData.newPassword}
              onChange={handleChange}
              autoComplete="new-password"
            />

            <button
              type="button"
              className="resetEyeButton"
              onClick={() =>
                setShowNewPassword(!showNewPassword)
              }
              aria-label={
                showNewPassword
                  ? "Hide new password"
                  : "Show new password"
              }
            >
              {showNewPassword ? <FaEyeSlash /> : <FaEye />}
            </button>
          </div>

          {/* CONFIRM PASSWORD */}

          <div className="resetInputBox">
            <FaLock />

            <input
              type={showConfirmPassword ? "text" : "password"}
              name="confirmPassword"
              placeholder="Confirm New Password"
              value={formData.confirmPassword}
              onChange={handleChange}
              autoComplete="new-password"
            />

            <button
              type="button"
              className="resetEyeButton"
              onClick={() =>
                setShowConfirmPassword(!showConfirmPassword)
              }
              aria-label={
                showConfirmPassword
                  ? "Hide confirm password"
                  : "Show confirm password"
              }
            >
              {showConfirmPassword ? (
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
                  ? "resetMessage resetSuccess"
                  : "resetMessage resetError"
              }
            >
              {message}
            </p>
          )}

          <button
            type="submit"
            className="resetButton"
            disabled={loading}
          >
            <FaKey />

            {loading ? "Resetting..." : "Reset Password"}
          </button>

          <div className="resetBack">
            <Link to="/login">
              <FaArrowLeft />
              Back to Login
            </Link>
          </div>
        </form>
      </div>
    </div>
  );
}