import React, { useState } from "react";
import "./ResetPassword.css";

import {
  Link,
  useNavigate,
  useLocation,
} from "react-router-dom";

import {
  FaCode,
  FaLock,
  FaEye,
  FaEyeSlash,
  FaKey,
  FaArrowLeft,
} from "react-icons/fa";

import bg from "../../../assets/images/login-bg.jpg";

import { resetPassword } from "../../../services/authService";

export default function ResetPassword() {
  const navigate = useNavigate();

  const location = useLocation();

const email = location.state?.email;

  const [formData, setFormData] = useState({
    newPassword: "",
    confirmPassword: "",
  });

  const [showNewPassword, setShowNewPassword] =
    useState(false);

  const [showConfirmPassword, setShowConfirmPassword] =
    useState(false);

  const [message, setMessage] = useState("");
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleChange = (event) => {
    const { name, value } = event.target;

    setFormData((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  const handleResetPassword = async (event) => {
    event.preventDefault();

    setMessage("");
    setSuccess(false);

    if (!formData.newPassword.trim()) {
      setMessage("New password is required.");
      return;
    }

    if (!formData.confirmPassword.trim()) {
      setMessage("Please confirm your new password.");
      return;
    }

    if (formData.newPassword.length < 8) {
      setMessage(
        "Password must contain at least 8 characters."
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

    if (!email) {
  setMessage("Session expired. Please verify OTP again.");
  return;
}

    const resetPasswordRequest = {
  email,
  newPassword: formData.newPassword,
  confirmPassword: formData.confirmPassword,
};
    try {
      setLoading(true);

      const data = await resetPassword(
        resetPasswordRequest
      );

      setSuccess(true);

      setMessage(
        data.message ||
          "Your password has been reset successfully."
      );

      setFormData({
        newPassword: "",
        confirmPassword: "",
      });

      setTimeout(() => {
        navigate("/login");
      }, 2000);
    } catch (error) {
      console.error(
        "Reset password error:",
        error
      );

      setSuccess(false);

      setMessage(
        error.response?.data?.message ||
          "Unable to connect to the server. Please try again."
      );
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
            Secure your
            <br />
            <span>CodeCanvas.</span> account
          </h1>

          <p>
            Create a strong new password to restore
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
            Enter and confirm your new password.
          </p>

          {/* NEW PASSWORD */}

          <div className="resetInputBox">
            <FaLock />

            <input
              type={
                showNewPassword
                  ? "text"
                  : "password"
              }
              name="newPassword"
              placeholder="New Password"
              value={formData.newPassword}
              onChange={handleChange}
              autoComplete="new-password"
              autoFocus
              required
            />

            <button
              type="button"
              className="resetEyeButton"
              onClick={() =>
                setShowNewPassword(
                  !showNewPassword
                )
              }
              aria-label={
                showNewPassword
                  ? "Hide new password"
                  : "Show new password"
              }
            >
              {showNewPassword ? (
                <FaEyeSlash />
              ) : (
                <FaEye />
              )}
            </button>
          </div>

          {/* CONFIRM PASSWORD */}

          <div className="resetInputBox">
            <FaLock />

            <input
              type={
                showConfirmPassword
                  ? "text"
                  : "password"
              }
              name="confirmPassword"
              placeholder="Confirm New Password"
              value={formData.confirmPassword}
              onChange={handleChange}
              autoComplete="new-password"
              required
            />

            <button
              type="button"
              className="resetEyeButton"
              onClick={() =>
                setShowConfirmPassword(
                  !showConfirmPassword
                )
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

            {loading
              ? "Resetting..."
              : "Reset Password"}
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