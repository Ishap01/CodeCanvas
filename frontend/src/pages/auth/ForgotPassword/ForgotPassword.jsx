import React, { useState } from "react";
import "./ForgotPassword.css";


import { Link, useNavigate } from "react-router-dom";

import {
  FaUser,
  FaCode,
  FaPaperPlane,
  FaArrowLeft,
} from "react-icons/fa";

import bg from "../../assets/login-bg.jpg";

import { forgotPassword } from "../../../services/authService";

export default function ForgotPassword() {
  const [formData, setFormData] = useState({
    email: "",
  });

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

  const navigate = useNavigate();

  const handleForgotPassword = async (event) => {
    event.preventDefault();

    setMessage("");
    setSuccess(false);

    if (!formData.email.trim()) {
      setMessage("Email is required.");
      return;
    }

    const forgotPasswordRequest = {
      email: formData.email.trim(),
    };

    try {
      setLoading(true);

      const data = await forgotPassword(formData.email.trim());

      setSuccess(true);

      setMessage(
        data.message ||
          "If the email exists, password reset instructions have been sent."
      );
      navigate("/verify-otp", {
    state: {
        email: formData.email.trim(),
    },
});
    } catch (error) {
      console.error("Forgot password error:", error);

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
    <div className="forgotPage">
      {/* LEFT PANEL */}

      <div
        className="forgotLeftPanel"
        style={{ backgroundImage: `url(${bg})` }}
      >
        <div className="forgotOverlay"></div>

        <div className="forgotLeftContent">
          <h1>
            Recover your
            <br />
            <span>CodeCanvas</span> account
          </h1>

          <p>
            Enter your email and we'll send password reset instructions to
            your registered email address.
          </p>
        </div>
      </div>

      {/* RIGHT PANEL */}

      <div className="forgotRightPanel">
        <form
          className="forgotCard"
          onSubmit={handleForgotPassword}
        >
          <div className="forgotLogo">
            <FaCode />
          </div>

          <h2>Forgot Password?</h2>

          <p className="forgotSubtitle">
            Enter your email to receive a password reset link.
          </p>

          <div className="forgotInputBox">
            <FaUser />

            <input
              type="email"
              name="email"
              placeholder="email"
              value={formData.email}
              onChange={handleChange}
              autoComplete="email"
              autoFocus
              required
            />
          </div>

          {message && (
            <p
              className={
                success
                  ? "forgotMessage forgotSuccess"
                  : "forgotMessage forgotError"
              }
            >
              {message}
            </p>
          )}

          <button
            type="submit"
            className="forgotButton"
            disabled={loading}
          >
            <FaPaperPlane />

            {loading
              ? "Sending..."
              : "Send OTP"}
          </button>

          <div className="forgotBack">
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